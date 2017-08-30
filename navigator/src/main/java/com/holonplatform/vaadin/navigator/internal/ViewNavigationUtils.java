/*
 * Copyright 2000-2016 Holon TDCN.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.holonplatform.vaadin.navigator.internal;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.regex.Pattern;

import org.apache.commons.lang3.reflect.FieldUtils;

import com.holonplatform.auth.annotations.Authenticate;
import com.holonplatform.core.Context;
import com.holonplatform.core.i18n.Caption;
import com.holonplatform.core.internal.Logger;
import com.holonplatform.core.internal.utils.AnnotationUtils;
import com.holonplatform.core.internal.utils.ConversionUtils;
import com.holonplatform.core.internal.utils.TypeUtils;
import com.holonplatform.core.temporal.TemporalType;
import com.holonplatform.vaadin.internal.VaadinLogger;
import com.holonplatform.vaadin.navigator.SubViewContainer;
import com.holonplatform.vaadin.navigator.ViewContentProvider;
import com.holonplatform.vaadin.navigator.ViewNavigator;
import com.holonplatform.vaadin.navigator.ViewNavigator.ViewNavigatorChangeEvent;
import com.holonplatform.vaadin.navigator.annotations.OnLeave;
import com.holonplatform.vaadin.navigator.annotations.OnShow;
import com.holonplatform.vaadin.navigator.annotations.SubViewOf;
import com.holonplatform.vaadin.navigator.annotations.ViewContext;
import com.holonplatform.vaadin.navigator.annotations.ViewParameter;
import com.holonplatform.vaadin.navigator.annotations.VolatileView;
import com.holonplatform.vaadin.navigator.annotations.WindowView;
import com.holonplatform.vaadin.navigator.internal.ViewConfiguration.ViewConfigurationException;
import com.holonplatform.vaadin.navigator.internal.ViewConfiguration.ViewConfigurationProvider;
import com.holonplatform.vaadin.navigator.internal.ViewConfiguration.ViewParameterDefinition;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;

/**
 * Utility class for {@link View} configuration and navigation management.
 * 
 * @since 5.0.0
 */
public final class ViewNavigationUtils implements Serializable {

	private static final long serialVersionUID = -4332074385544634029L;

	/**
	 * Logger
	 */
	private static final Logger LOGGER = VaadinLogger.create();

	private static final ThreadLocal<DateFormat> DEFAULT_PARAM_DATE_FORMAT = new ThreadLocal<DateFormat>() {
		@Override
		protected DateFormat initialValue() {
			return new SimpleDateFormat(ViewParameter.DEFAULT_DATE_PATTERN);
		}
	};

	private static final DecimalFormat PARAMETER_VALUE_INTEGER_FORMAT = new DecimalFormat("#0");
	private static final DecimalFormat PARAMETER_VALUE_DECIMAL_FORMAT = new DecimalFormat("#0.#");

	private static final Pattern PARAMETERS_SEPARATOR_PATTERN = Pattern.compile("/", Pattern.LITERAL);
	private static final Pattern PARAMETER_SEPARATOR_PATTERN = Pattern.compile("=", Pattern.LITERAL);

	static {
		DecimalFormatSymbols dfs = new DecimalFormatSymbols();
		dfs.setDecimalSeparator('.');
		PARAMETER_VALUE_DECIMAL_FORMAT.setDecimalFormatSymbols(dfs);
		PARAMETER_VALUE_DECIMAL_FORMAT.setGroupingUsed(false);
		PARAMETER_VALUE_INTEGER_FORMAT.setGroupingUsed(false);
	}

	/*
	 * Empty private constructor: this class is intended only to provide constants ad utility methods.
	 */
	private ViewNavigationUtils() {
	}

	/**
	 * Try to obtain ViewNavigator from current thread-bound {@link UI}
	 * @return If current {@link UI} is available and has a ViewNavigator setted, this one is retuned. Null otherwise.
	 */
	public static ViewNavigator getCurrentUIViewNavigator() {
		UI ui = UI.getCurrent();
		if (ui != null) {
			Navigator navigator = ui.getNavigator();
			if (navigator instanceof ViewNavigator) {
				return (ViewNavigator) navigator;
			}
		}
		return null;
	}

	/**
	 * Check if given view name is valid
	 * @param viewName View name to check
	 * @throws ViewConfigurationException Invalid view name
	 */
	public static void checkValidViewName(String viewName) throws ViewConfigurationException {
		if (viewName != null && viewName.contains("=")) {
			throw new ViewConfigurationException(
					"Invalid view name " + viewName + ": equal (=) character is not allowed in view names");
		}
	}

	/**
	 * Check if given view class is valid
	 * @param viewClass View class to check
	 * @throws ViewConfigurationException Invalid view class
	 */
	public static void checkValidViewClass(Class<? extends View> viewClass) throws ViewConfigurationException {
		if (viewClass.isInterface()) {
			throw new ViewConfigurationException("Interfaces are not allowed as view class");
		}
		if (Modifier.isAbstract(viewClass.getModifiers())) {
			throw new ViewConfigurationException("Abstract classes are not allowed as view class");
		}
	}

	/**
	 * Fire {@link OnShow} view methods
	 * @param view View instance (not null)
	 * @param configuration View configuration (not null)
	 * @param event View change event
	 * @param refresh <code>true</code> if is a page refresh
	 * @throws ViewConfigurationException Error invoking view methods
	 */
	public static <E extends ViewChangeEvent & ViewNavigatorChangeEvent> void fireViewOnShow(View view,
			ViewConfiguration configuration, E event, boolean refresh) throws ViewConfigurationException {
		if (view == null) {
			throw new ViewConfigurationException("Null view instance");
		}
		if (configuration == null) {
			throw new ViewConfigurationException("Missing view configuration");
		}

		for (Method method : configuration.getOnShowMethods()) {
			if (!refresh || configuration.isFireOnRefresh(method)) {
				try {
					if (method.getParameterCount() == 0) {
						method.invoke(view, new Object[0]);
					} else {
						method.invoke(view, new Object[] { event });
					}
				} catch (Exception e) {
					throw new ViewConfigurationException("Failed to fire OnShow method " + method.getName()
							+ " on view class " + view.getClass().getName(), e);
				}
			}
		}
	}

	/**
	 * Fire {@link OnLeave} view methods
	 * @param view View instance (not null)
	 * @param configuration View configuration (not null)
	 * @param event View change event
	 * @throws ViewConfigurationException Error invoking view methods
	 */
	public static <E extends ViewChangeEvent & ViewNavigatorChangeEvent> void fireViewOnLeave(View view,
			ViewConfiguration configuration, E event) throws ViewConfigurationException {
		if (view == null) {
			throw new ViewConfigurationException("Null view instance");
		}
		if (configuration == null) {
			throw new ViewConfigurationException("Missing view configuration");
		}

		for (Method method : configuration.getOnLeaveMethods()) {
			try {
				if (method.getParameterCount() == 0) {
					method.invoke(view, new Object[0]);
				} else {
					method.invoke(view, new Object[] { event });
				}
			} catch (Exception e) {
				throw new ViewConfigurationException("Failed to fire OnLeave method " + method.getName()
						+ " on view class " + view.getClass().getName(), e);
			}
		}
	}

	/**
	 * Extract parameters string from view request
	 * @param viewName View name
	 * @param nameAndParameters Full request string
	 * @return Parameters string
	 */
	public static String getViewParameters(String viewName, String nameAndParameters) {
		if (nameAndParameters != null && !nameAndParameters.trim().equals("")
				&& nameAndParameters.length() > viewName.length()) {
			String parameters = nameAndParameters.substring(viewName.length() + 1);
			if (parameters != null && !parameters.trim().equals("")) {
				return parameters;
			}
		}
		return null;
	}

	/**
	 * Generate parameters URI string from name-value map
	 * @param paramsMap name-value map
	 * @param encoding URI encoding (charset)
	 * @return Parameters string
	 * @throws ViewConfigurationException Error in URI building
	 */
	public static String generateParametersString(Map<String, Object> paramsMap, String encoding)
			throws ViewConfigurationException {
		if (paramsMap != null && !paramsMap.isEmpty()) {

			String charset = (encoding == null) ? ViewParameter.DEFAULT_PARAMETER_ENCODING : encoding;

			StringBuilder paramsString = new StringBuilder();

			for (Entry<String, Object> entry : paramsMap.entrySet()) {
				if (entry.getKey() != null && !entry.getKey().trim().equals("")) {
					try {
						String serializedValue = serializeParameterValue(entry.getValue(),
								DEFAULT_PARAM_DATE_FORMAT.get());
						if (serializedValue != null) {
							if (paramsString.length() > 0) {
								paramsString.append("/");
							}
							paramsString.append(URLEncoder.encode(entry.getKey().trim(), charset));
							paramsString.append("=");
							paramsString.append(URLEncoder.encode(serializedValue, charset));
						}
					} catch (Exception e) {
						throw new ViewConfigurationException("Failed to serialize view parameters", e);
					}
				}
			}

			return paramsString.toString();
		}
		return null;
	}

	/**
	 * Serialize given parameter value as String
	 * @param value Value to serialize
	 * @param dateFormat Date format to use with {@link Date} value types
	 * @return Serialized value
	 */
	private static String serializeParameterValue(Object value, DateFormat dateFormat) {
		if (!isNullOrEmpty(value)) {
			if (TypeUtils.isString(value.getClass())) {
				return (String) value;
			}
			if (TypeUtils.isBoolean(value.getClass())) {
				return ((Boolean) value) ? "true" : "false";
			}
			if (TypeUtils.isEnum(value.getClass())) {
				int ordinal = ((Enum<?>) value).ordinal();
				return String.valueOf(ordinal);
			}
			if (TypeUtils.isNumber(value.getClass())) {
				if (TypeUtils.isDecimalNumber(value.getClass())) {
					return PARAMETER_VALUE_DECIMAL_FORMAT.format(value);
				} else {
					return PARAMETER_VALUE_INTEGER_FORMAT.format(value);
				}
			}
			if (TypeUtils.isDate(value.getClass())) {
				return dateFormat.format((Date) value);
			}
			if (TypeUtils.isTemporal(value.getClass())) {
				TemporalType type = TemporalType.getTemporalType((Temporal) value);
				if (type == null) {
					type = TemporalType.DATE;
				}
				switch (type) {
				case DATE_TIME:
					return DateTimeFormatter.ISO_LOCAL_DATE_TIME.format((Temporal) value);
				case TIME:
					return DateTimeFormatter.ISO_LOCAL_TIME.format((Temporal) value);
				case DATE:
				default:
					return DateTimeFormatter.ISO_LOCAL_DATE.format((Temporal) value);
				}
			}
			throw new UnsupportedOperationException(
					"Parameter value serialization " + "not supported for type: " + value.getClass().getName());
		}
		return null;
	}

	/**
	 * Convert given parameters String into a name-value parameters Map
	 * @param parametersString Parameters String
	 * @param encoding Parameters String encoding (charset), or <code>null</code> for default
	 * @return Converted parameters Map
	 * @throws UnsupportedEncodingException Given encoding is not supported
	 */
	public static Map<String, String> parseParametersString(String parametersString, String encoding)
			throws UnsupportedEncodingException {
		String parameters = sanitizeParametersString(parametersString);
		if (parameters != null && !parameters.trim().equals("")) {
			Map<String, String> parsed = new HashMap<>();
			if (parameters.contains("/")) {
				String[] pars = PARAMETERS_SEPARATOR_PATTERN.split(parameters);
				if (pars != null) {
					for (String par : pars) {
						parseParameter(par, parsed, encoding);
					}
				}
			} else {
				parseParameter(parameters, parsed, encoding);
			}
			return parsed;
		}
		return Collections.emptyMap();
	}

	/*
	 * Parse name=value parameter and add to map
	 */
	private static void parseParameter(String p, Map<String, String> parametersMap, String encoding)
			throws UnsupportedEncodingException {
		if (p != null && p.contains("=")) {
			String[] splitted = PARAMETER_SEPARATOR_PATTERN.split(p);
			if (splitted != null && splitted.length == 2) {
				String key = splitted[0];
				String value = splitted[1];
				if (key != null && !key.trim().equals("") && value != null && !value.trim().equals("")) {
					String charset = (encoding == null) ? ViewParameter.DEFAULT_PARAMETER_ENCODING : encoding;
					parametersMap.put(URLDecoder.decode(key, charset), URLDecoder.decode(value, charset));
				}
			}
		}
	}

	/**
	 * Set parameters values in View instance using any matching parameter definition
	 * @param view View instance (not null)
	 * @param configuration View configuration (not null)
	 * @param parametersString Parameters String
	 * @param encoding Parameters String encoding (charset), or <code>null</code> for default
	 * @throws ViewConfigurationException Error setting parameter values
	 */
	public static void setViewParameters(View view, ViewConfiguration configuration, String parametersString,
			String encoding) throws ViewConfigurationException {
		try {
			setViewParameters(view, configuration, parseParametersString(parametersString, encoding));
		} catch (UnsupportedEncodingException e) {
			throw new ViewConfigurationException(e);
		}
	}

	/**
	 * Set parameters values in View instance using any matching parameter definition
	 * @param view View instance (not null)
	 * @param configuration View configuration (not null)
	 * @param parameters Parameters name-value map
	 * @throws ViewConfigurationException Error setting parameter values
	 */
	public static void setViewParameters(View view, ViewConfiguration configuration, Map<String, String> parameters)
			throws ViewConfigurationException {
		if (view == null) {
			throw new ViewConfigurationException("Null view instance");
		}
		if (configuration == null) {
			throw new ViewConfigurationException("Missing view configuration");
		}

		// get definitions
		Collection<ViewParameterDefinition> definitions = configuration.getParameters();

		// check view has some parameter definition
		if (!definitions.isEmpty()) {

			// check required parameters
			checkRequiredParameters(view, definitions, parameters);

			// set parameter values
			for (ViewParameterDefinition definition : definitions) {
				String value = (parameters != null) ? parameters.get(definition.getName()) : null;
				if (!isNullOrEmpty(value)) {
					Object deserialized = deserializeParameterValue(value, definition.getType());
					setViewParameterValue(view, definition, deserialized);
				} else {
					if (definition.getDefaultValue() != null) {
						setViewParameterValue(view, definition, definition.getDefaultValue());
					} else {
						clearViewParameter(view, definition);
					}
				}
			}

		}
	}

	/**
	 * Clear view parameter on given view instance
	 * @param view View instance
	 * @param definition Parameter definition
	 */
	private static void clearViewParameter(View view, ViewParameterDefinition definition)
			throws ViewConfigurationException {
		Object value = null;
		if (TypeUtils.isPrimitiveBoolean(definition.getType())) {
			value = Boolean.FALSE;
		} else if (TypeUtils.isPrimitiveInt(definition.getType()) || short.class == definition.getType()) {
			value = 0;
		} else if (TypeUtils.isPrimitiveInt(definition.getType())) {
			value = 0;
		} else if (TypeUtils.isPrimitiveFloat(definition.getType())) {
			value = 0f;
		} else if (TypeUtils.isPrimitiveDouble(definition.getType())) {
			value = 0d;
		}

		setViewParameterValue(view, definition, value);
	}

	/**
	 * Check the given view parameter value, performing type conversions when applicable.
	 * @param view View instance
	 * @param definition Parameter definition
	 * @param value Parameter value
	 * @return Processed parameter value
	 * @throws ViewConfigurationException Error processing value
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Object checkParameterValue(View view, ViewParameterDefinition definition, Object value)
			throws ViewConfigurationException {
		if (value != null) {
			// String
			if (TypeUtils.isString(definition.getType()) && !TypeUtils.isString(value.getClass())) {
				return value.toString();
			}
			if (!TypeUtils.isString(definition.getType()) && TypeUtils.isString(value.getClass())) {
				return ConversionUtils.convertStringValue((String) value, definition.getType());
			}
			// Numbers
			if (TypeUtils.isNumber(definition.getType()) && TypeUtils.isNumber(value.getClass())) {
				return ConversionUtils.convertNumberToTargetClass((Number) value, (Class<Number>) definition.getType());
			}
			// Enums
			if (TypeUtils.isEnum(definition.getType()) && !TypeUtils.isEnum(value.getClass())) {
				return ConversionUtils.convertEnumValue((Class<Enum>) definition.getType(), value);
			}
			// check type consistency
			if (!TypeUtils.isAssignable(value.getClass(), definition.getType())) {
				throw new ViewConfigurationException("Value type " + value.getClass().getName()
						+ " doesn't match view parameter type " + definition.getType().getName());
			}
		}
		return value;
	}

	/**
	 * Set view parameter value in given View instance
	 * @param view View instance
	 * @param definition Parameter definition
	 * @param value Parameter value to set
	 * @throws ViewConfigurationException Error setting parameter value
	 */
	private static void setViewParameterValue(View view, ViewParameterDefinition definition, Object value)
			throws ViewConfigurationException {
		final Object v = checkParameterValue(view, definition, value);

		final Method m = definition.getWriteMethod();
		if (m != null) {
			// use write method
			try {
				m.invoke(view, new Object[] { v });
			} catch (Exception e) {
				throw new ViewConfigurationException("Failed to set value of parameter " + definition.getName()
						+ " on view class " + view.getClass().getName(), e);
			}
		} else {
			// use field
			Field fld = definition.getField();
			if (fld == null) {
				throw new ViewConfigurationException("Failed to set value of parameter " + definition.getName()
						+ " on view class " + view.getClass().getName() + ": missing parameter Field or setter Method");
			}
			try {
				FieldUtils.writeField(fld, view, v, true);
			} catch (Exception e) {
				throw new ViewConfigurationException("Failed to set value of parameter " + definition.getName()
						+ " on view class " + view.getClass().getName(), e);
			}
		}
	}

	/*
	 * check required parameters are provided
	 */
	private static void checkRequiredParameters(View view, Collection<ViewParameterDefinition> definitions,
			Map<String, ?> parameters) throws ViewConfigurationException {
		for (ViewParameterDefinition definition : definitions) {
			if (definition.isRequired()) {
				if (parameters == null || !parameters.containsKey(definition.getName())) {
					throw new ViewConfigurationException("Parameter " + definition.getName() + " in view class "
							+ view.getClass().getName() + " is required");
				}
				if (isNullOrEmpty(parameters.get(definition.getName()))) {
					throw new ViewConfigurationException("Parameter " + definition.getName() + " in view class "
							+ view.getClass().getName() + " is required and must be not null or empty");
				}
			}
		}
	}

	/**
	 * Deserialize given parameters String into a map of View parameters consistent with {@link ViewConfiguration}
	 * parameters declarations.
	 * @param configuration ViewConfiguration (required)
	 * @param parameters Parameters string
	 * @param encoding Optional encoding
	 * @return Deserialized View parameters
	 * @throws ViewConfigurationException Error processing parameters
	 */
	public static Map<String, Object> deserializeParameters(ViewConfiguration configuration, String parameters,
			String encoding) throws ViewConfigurationException {
		if (configuration == null) {
			throw new ViewConfigurationException("Missing view configuration");
		}
		try {
			Map<String, String> parsed = parseParametersString(parameters, encoding);
			if (parsed != null) {
				// get definitions
				Collection<ViewParameterDefinition> definitions = configuration.getParameters();
				if (!definitions.isEmpty()) {

					Map<String, Object> deserialized = new HashMap<>(parsed.size());

					for (ViewParameterDefinition definition : definitions) {
						String value = parsed.get(definition.getName());
						if (!isNullOrEmpty(value)) {
							deserialized.put(definition.getName(),
									deserializeParameterValue(value, definition.getType()));
						} else {
							if (definition.getDefaultValue() != null) {
								deserialized.put(definition.getName(), definition.getDefaultValue());
							}
						}
					}

					return deserialized;
				}
			}

			return Collections.emptyMap();
		} catch (UnsupportedEncodingException e) {
			throw new ViewConfigurationException(e);
		}
	}

	/**
	 * Build a {@link ViewConfiguration} using given view class
	 * @param viewClass View class (not null)
	 * @return ViewConfiguration
	 * @throws ViewConfigurationException Error building view configuration
	 */
	public static ViewConfiguration buildViewConfiguration(Class<? extends View> viewClass)
			throws ViewConfigurationException {
		if (viewClass == null) {
			throw new ViewConfigurationException("Null view class");
		}

		// check valid navigation view
		boolean viewContentProvider = false;
		if (ViewContentProvider.class.isAssignableFrom(viewClass)) {
			viewContentProvider = true;
		} else {
			if (!Component.class.isAssignableFrom(viewClass)) {
				throw new ViewConfigurationException(
						"Invalid navigation view class " + viewClass.getName() + ": View class must be a "
								+ Component.class.getName() + " or a " + ViewContentProvider.class.getName());
			}
		}

		DefaultViewConfiguration cfg = new DefaultViewConfiguration();
		cfg.setViewContentProvider(viewContentProvider);

		cfg.setSubViewContainer(SubViewContainer.class.isAssignableFrom(viewClass));

		SubViewOf sv = viewClass.getAnnotation(SubViewOf.class);
		if (sv != null) {
			String parentViewName = sv.value();
			if (AnnotationUtils.isEmpty(parentViewName)) {
				throw new ViewConfigurationException("Invalid sub view declaration for view class" + viewClass.getName()
						+ ": parent view name must be not null or empty");
			}
			cfg.setParentViewName(parentViewName);
		}

		cfg.setParameters(getViewParameterDefinitions(viewClass));

		List<Method> onShows = getViewOnShowMethods(viewClass);
		cfg.setOnShowMethods(onShows);
		if (onShows != null) {
			for (Method method : onShows) {
				if (method.getAnnotation(OnShow.class).onRefresh()) {
					cfg.setFireOnRefreshMethod(method);
				}
			}
		}

		cfg.setOnLeaveMethods(getViewOnLeaveMethods(viewClass));

		cfg.setContextInjectionFields(getContextInjectionFields(viewClass));

		cfg.setVolatile(viewClass.isAnnotationPresent(VolatileView.class));

		cfg.setAuthentication(viewClass.getAnnotation(Authenticate.class));

		WindowView wv = viewClass.getAnnotation(WindowView.class);
		if (wv != null) {
			cfg.setForceInWindow(true);

			DefaultViewWindowConfiguration wCfg = new DefaultViewWindowConfiguration();
			wCfg.setClosable(wv.closable());
			wCfg.setResizable(wv.resizable());
			if (!AnnotationUtils.isEmpty(wv.windowWidth())) {
				wCfg.setWindowWidth(wv.windowWidth());
			}
			if (!AnnotationUtils.isEmpty(wv.windowHeigth())) {
				wCfg.setWindowHeight(wv.windowHeigth());
			}
			cfg.setWindowConfiguration(wCfg);
		}

		Caption cpt = viewClass.getAnnotation(Caption.class);
		if (cpt != null) {
			cfg.setCaption(cpt.value());
			cfg.setCaptionMessageCode(cpt.messageCode());
		}

		return cfg;
	}

	/**
	 * Get valid {@link OnShow} methods in given <code>viewClass</code>
	 * @param viewClass View class
	 * @return List of methods ordered according to class hierarchy
	 * @throws ViewConfigurationException Error parsing methods or invalid method signature
	 */
	public static List<Method> getViewOnShowMethods(Class<?> viewClass) throws ViewConfigurationException {
		List<Method> methods = getPublicAnnotatedMethods(viewClass, OnShow.class);
		if (methods != null) {
			// check signature
			for (Method method : methods) {
				// exclude View#enter default method to avoid duplicate calls
				if (!isViewEnterDefaultMethod(method)) {
					checkViewOnShowOrLeaveMethod(viewClass, method, "OnShow");
				} else {
					methods.remove(method);
				}
			}
			// reverse and return
			Collections.reverse(methods);
			return methods;
		}
		return Collections.emptyList();
	}

	/**
	 * Get valid {@link OnLeave} methods in given <code>viewClass</code>
	 * @param viewClass View class
	 * @return List of methods ordered according to class hierarchy
	 * @throws ViewConfigurationException Error parsing methods or invalid method signature
	 */
	public static List<Method> getViewOnLeaveMethods(Class<?> viewClass) throws ViewConfigurationException {
		List<Method> methods = getPublicAnnotatedMethods(viewClass, OnLeave.class);
		if (methods != null) {
			// check signature
			for (Method method : methods) {
				checkViewOnShowOrLeaveMethod(viewClass, method, "OnLeave");
			}
			// reverse and return
			Collections.reverse(methods);
			return methods;
		}
		return Collections.emptyList();
	}

	/**
	 * Check given method has a valid signature for {@link OnShow} or {@link OnLeave} view method
	 * @param viewClass View class
	 * @param method Method to check
	 * @param message Error message annotation description
	 * @throws ViewConfigurationException Method is not valid
	 */
	private static void checkViewOnShowOrLeaveMethod(Class<?> viewClass, Method method, String message)
			throws ViewConfigurationException {
		if (method.getReturnType() != Void.class && method.getReturnType() != Void.TYPE) {
			throw new ViewConfigurationException("Invalid " + message + " method in view class " + viewClass.getName()
					+ ": method must be a void return method");
		}
		int params = method.getParameterCount();
		if (params > 1) {
			throw new ViewConfigurationException("Invalid " + message + " method in view class " + viewClass.getName()
					+ ": method must have no parameters or only one parameter of type ViewChangeEvent");
		}
		if (params == 1) {
			Parameter param = method.getParameters()[0];
			if (param.isVarArgs() || !(ViewChangeEvent.class.isAssignableFrom(param.getType())
					|| ViewNavigatorChangeEvent.class.isAssignableFrom(param.getType()))) {
				throw new ViewConfigurationException(
						"Invalid " + message + " method in view class " + viewClass.getName()
								+ ": method must have no parameters or only one parameter of type ViewChangeEvent");
			}
		}
	}

	/**
	 * Get {@link ViewContext} annotated fields of class and its superclasses
	 * @param cls Class to inspect
	 * @return ViewContext annotated fields
	 * @throws ViewConfigurationException Invalid Context injection type
	 */
	public static Collection<ViewContextField> getContextInjectionFields(Class<?> cls)
			throws ViewConfigurationException {
		ArrayList<ViewContextField> fields = new ArrayList<>();

		Class<?> currentClass = cls;
		while (currentClass != null) {
			final Field[] declaredFields = currentClass.getDeclaredFields();
			for (final Field field : declaredFields) {
				if (field.isAnnotationPresent(ViewContext.class)) {
					// check not final
					if (Modifier.isFinal(field.getModifiers())) {
						throw new ViewConfigurationException("Context injection field " + field.getName()
								+ " must not be declared as final in class " + cls.getName());
					}
					ViewContext vc = field.getAnnotation(ViewContext.class);

					fields.add(
							ViewContextField.build(AnnotationUtils.getStringValue(vc.value()), vc.required(), field));
				}
			}
			currentClass = currentClass.getSuperclass();
		}

		fields.trimToSize();
		return fields;
	}

	/**
	 * Search for any {@link ViewParameter} annotated field in given <code>viewClass</code> and returns a set of
	 * {@link ViewParameterDefinition} corresponding to detected parameter fields.
	 * @param viewClass View class
	 * @return ViewParameterDefinitions
	 * @throws ViewConfigurationException Error parsing view parameters
	 */
	public static Collection<ViewParameterDefinition> getViewParameterDefinitions(Class<?> viewClass)
			throws ViewConfigurationException {
		try {
			List<Field> fields = getViewParameterFields(viewClass);
			if (!fields.isEmpty()) {
				BeanInfo bi = Introspector.getBeanInfo(viewClass);
				PropertyDescriptor[] propertyDescriptors = bi.getPropertyDescriptors();

				ArrayList<ViewParameterDefinition> definitions = new ArrayList<>(fields.size());
				for (Field field : fields) {
					ViewParameter vp = field.getAnnotation(ViewParameter.class);

					// check not final
					if (Modifier.isFinal(field.getModifiers())) {
						throw new ViewConfigurationException("View parameter field " + field.getName()
								+ " must not be declared as final in view class " + viewClass.getName());
					}

					// parameter name and type
					String parameterName = vp.value();
					if (parameterName == null || parameterName.trim().equals("")) {
						parameterName = field.getName();
					}

					checkParameterName(parameterName);

					Class<?> type = field.getType();
					if (!isAdmittedParameterFieldType(type)) {
						throw new ViewConfigurationException("Not admitted view parameter field type " + type.getName()
								+ " in view class " + viewClass.getName());
					}
					DefaultViewParameterDefinition definition = new DefaultViewParameterDefinition(parameterName, type);
					definition.setField(field);

					// check getter and setter
					PropertyDescriptor propertyDescriptor = getPropertyDescriptor(propertyDescriptors, field.getName());
					if (propertyDescriptor != null) {
						// check methods parameter consistency
						if (propertyDescriptor.getReadMethod() != null
								&& TypeUtils.isAssignable(propertyDescriptor.getReadMethod().getReturnType(), type)) {
							definition.setReadMethod(propertyDescriptor.getReadMethod());
						}
						if (propertyDescriptor.getWriteMethod() != null
								&& propertyDescriptor.getWriteMethod().getParameterTypes().length == 1
								&& TypeUtils.isAssignable(propertyDescriptor.getWriteMethod().getParameterTypes()[0],
										type)) {
							definition.setWriteMethod(propertyDescriptor.getWriteMethod());
						}
					}

					// settings
					definition.setRequired(vp.required());

					String dft = vp.defaultValue();
					if (dft != null && !dft.isEmpty()) {
						definition.setDefaultValue(deserializeParameterValue(dft, type));
					}

					// avoid duplicates
					if (definitions.contains(definition)) {
						throw new ViewConfigurationException("Duplicate view parameter name: " + parameterName
								+ " in view class " + viewClass.getName());
					}

					// add definition
					definitions.add(definition);

				}

				definitions.trimToSize();
				return definitions;
			}
		} catch (Exception e) {
			throw new ViewConfigurationException(e);
		}
		return Collections.emptyList();
	}

	/**
	 * Check parameter name does not contains illegal characters
	 * @param parameterName Parameter name to check
	 * @throws ViewConfigurationException If parameter contains illegal characters
	 */
	private static void checkParameterName(String parameterName) throws ViewConfigurationException {
		if (parameterName != null) {
			for (char illegalChar : ViewParameter.ILLEGAL_PARAMETER_NAME_CHARACTERS) {
				if (parameterName.indexOf(illegalChar) > -1) {
					throw new ViewConfigurationException(
							"Illegal character " + illegalChar + " in View parameter name: " + parameterName);
				}
			}
		}
	}

	/**
	 * Deserialize given String parameter value
	 * @param value Parameter value
	 * @param requiredType Required value type
	 * @return The deserialized value
	 */
	public static Object deserializeParameterValue(String value, Class<?> requiredType)
			throws ViewConfigurationException {
		if (value != null && !value.trim().equals("")) {
			if (TypeUtils.isDate(requiredType)) {
				try {
					return new SimpleDateFormat(ViewParameter.DEFAULT_DATE_PATTERN).parse(value);
				} catch (Exception e) {
					throw new ViewConfigurationException(
							"Failed to deserialize parameter value " + value + " into type " + requiredType.getName(),
							e);
				}
			}
			if (TypeUtils.isLocalTemporal(requiredType)) {
				TemporalType type = TemporalType.DATE;
				if (LocalTime.class.isAssignableFrom(requiredType)) {
					type = TemporalType.TIME;
				}
				if (LocalDateTime.class.isAssignableFrom(requiredType)) {
					type = TemporalType.DATE_TIME;
				}

				switch (type) {
				case DATE_TIME: {
					TemporalAccessor parsed = DateTimeFormatter.ISO_LOCAL_DATE_TIME.parse(value);
					if (parsed != null) {
						return parsed.query(LocalDateTime::from);
					}
				}
					break;
				case TIME: {
					TemporalAccessor parsed = DateTimeFormatter.ISO_LOCAL_TIME.parse(value);
					if (parsed != null) {
						return parsed.query(LocalTime::from);
					}
				}
					break;
				case DATE:
				default: {
					TemporalAccessor parsed = DateTimeFormatter.ISO_LOCAL_DATE.parse(value);
					if (parsed != null) {
						return parsed.query(LocalDate::from);
					}
				}
					break;
				}

			}
			try {
				return ConversionUtils.convertStringValue(value, requiredType);
			} catch (IllegalArgumentException e) {
				throw new ViewConfigurationException(
						"Failed to deserialize parameter value " + value + " into type " + requiredType.getName(), e);
			}
		}
		return null;
	}

	/**
	 * Check parameter field type
	 * @param type Field type
	 * @return <code>true</code> if type is admitted as view parameter value
	 */
	private static boolean isAdmittedParameterFieldType(Class<?> type) {
		return TypeUtils.isString(type) || TypeUtils.isNumber(type) || TypeUtils.isBoolean(type)
				|| TypeUtils.isDate(type) || TypeUtils.isLocalTemporal(type) || TypeUtils.isEnum(type);
	}

	/**
	 * Get class (and superclasses) {@link ViewParameter} annotated fields
	 * @param cls Class to inspect
	 * @return {@link ViewParameter} annotated fields, or an empty list if none
	 */
	private static List<Field> getViewParameterFields(Class<?> cls) {
		List<Field> fields = new ArrayList<>();
		Class<?> currentClass = cls;
		while (currentClass != null) {
			final Field[] declaredFields = currentClass.getDeclaredFields();
			for (final Field field : declaredFields) {
				if (field.isAnnotationPresent(ViewParameter.class)) {
					fields.add(field);
				}
			}
			currentClass = currentClass.getSuperclass();
		}
		return fields;
	}

	/**
	 * Get <code>public</code> annotated methods with given <code>annotationType</code>
	 * @param cls Class to inspect
	 * @param annotationType Annotation class
	 * @return List of methods
	 */
	private static List<Method> getPublicAnnotatedMethods(Class<?> cls, Class<? extends Annotation> annotationType) {
		List<Method> methods = new LinkedList<>();
		Class<?> currentClass = cls;
		while (currentClass != null) {
			final Method[] declaredMethods = currentClass.getDeclaredMethods();
			for (final Method method : declaredMethods) {
				if (method.isAnnotationPresent(annotationType) && Modifier.isPublic(method.getModifiers())) {
					methods.add(method);
				}
			}
			currentClass = currentClass.getSuperclass();
		}
		return methods;
	}

	/**
	 * Get the PropertyDescriptor which corresponds to given property name, if any
	 * @param descriptors Available PropertyDescriptors
	 * @param propertyName Property name
	 * @return PropertyDescriptor which corresponds to given property name, or <code>null</code> if not present
	 */
	private static PropertyDescriptor getPropertyDescriptor(PropertyDescriptor[] descriptors, String propertyName) {
		if (descriptors != null && propertyName != null) {
			for (PropertyDescriptor descriptor : descriptors) {
				if (propertyName.equals(descriptor.getName())) {
					return descriptor;
				}
			}
		}
		return null;
	}

	/**
	 * Check if given method is {@link View#enter(ViewChangeEvent)} method
	 * @param method Method to check
	 * @return <code>true</code> if given method is {@link View#enter(ViewChangeEvent)} method
	 */
	private static boolean isViewEnterDefaultMethod(Method method) {
		if ("enter".equals(method.getName()) && method.getParameterCount() == 1
				&& ViewChangeEvent.class == method.getParameterTypes()[0]) {
			return true;
		}
		return false;
	}

	/**
	 * Check if given value is <code>null</code> or is an empty String
	 * @param value Value to check
	 * @return <code>true</code> if given value is <code>null</code> or is an empty String
	 */
	private static boolean isNullOrEmpty(Object value) {
		return (value == null || value.toString().trim().equals(""));
	}

	/**
	 * Sanitize parameters string
	 * @param parametersString Parameters string
	 * @return Trimmed parameters string
	 */
	private static String sanitizeParametersString(String parametersString) {
		if (parametersString != null && parametersString.trim().startsWith("/")) {
			return parametersString.trim().substring(1);
		}
		return (parametersString != null) ? parametersString.trim() : null;
	}

	/**
	 * Inject {@link Context} field, if any, in a given View instance
	 * @param viewConfigurationProvider ViewConfigurationProvider
	 * @param view View instance
	 * @return View
	 * @throws ViewConfigurationException Error in context data injection
	 */
	public static View injectContext(ViewConfigurationProvider viewConfigurationProvider, View view)
			throws ViewConfigurationException {
		if (view != null) {
			ViewConfiguration configuration = viewConfigurationProvider.getViewConfiguration(view.getClass());
			if (configuration != null) {
				Collection<ViewContextField> fields = configuration.getContextInjectionFields();
				if (fields != null) {
					for (final ViewContextField vcf : fields) {

						final Class<?> type = vcf.getField().getType();
						final String key = (vcf.getContextResourceKey() != null) ? vcf.getContextResourceKey()
								: type.getName();

						Optional<?> resource = Context.get().resource(key, type);
						if (resource.isPresent()) {
							try {
								FieldUtils.writeField(vcf.getField(), view, resource.get(), true);
							} catch (Exception e) {
								throw new ViewConfigurationException("Failed to inject context resource type " + type
										+ " in field " + vcf.getField().getName() + " of view class "
										+ view.getClass().getName(), e);
							}
						} else {
							if (vcf.isRequired()) {
								throw new ViewConfigurationException("Failed to inject context resource type " + type
										+ " in field " + vcf.getField().getName() + " of view class "
										+ view.getClass().getName() + ": context resource not available");
							}
						}

					}
				}
			} else {
				LOGGER.warn("No ViewConfiguration available for view class " + view.getClass().getName()
						+ ": Context informations injection skipped");
			}
		}
		return view;
	}

}
