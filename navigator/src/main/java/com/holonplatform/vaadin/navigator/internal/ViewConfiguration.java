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

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import com.holonplatform.auth.annotations.Authenticate;
import com.holonplatform.core.i18n.Caption;
import com.holonplatform.vaadin.navigator.SubViewContainer;
import com.holonplatform.vaadin.navigator.ViewContentProvider;
import com.holonplatform.vaadin.navigator.ViewWindowConfigurator;
import com.holonplatform.vaadin.navigator.annotations.OnLeave;
import com.holonplatform.vaadin.navigator.annotations.OnShow;
import com.holonplatform.vaadin.navigator.annotations.ViewContext;
import com.holonplatform.vaadin.navigator.annotations.VolatileView;
import com.holonplatform.vaadin.navigator.annotations.WindowView;
import com.vaadin.navigator.View;

/**
 * {@link View} configuration representation.
 * 
 * <p>
 * This class is intended for internal framework use only.
 * </p>
 * 
 * @since 5.0.0
 */
public interface ViewConfiguration extends Consumer<ViewWindowConfigurator>, Serializable {

	/**
	 * Gets whether this view is volatile (not to be tracked in navigation history)
	 * @return <code>true</code> if this view is volatile
	 * @see VolatileView
	 */
	boolean isVolatile();

	/**
	 * Gets whether this view must be displayed in a Window
	 * @return <code>true</code> if this view must be displayed in a Window
	 * @see WindowView
	 */
	boolean isForceInWindow();

	/**
	 * Gets whether the view class extends {@link ViewContentProvider} to delegate View contents supplying
	 * @return <code>true</code> if the view class is a ViewContentProvider
	 */
	boolean isViewContentProvider();

	/**
	 * Gets whether the view class is a sub view
	 * @return <code>true</code> if the view class is a sub view
	 */
	boolean isSubView();

	/**
	 * Gets whether the view class is a {@link SubViewContainer}
	 * @return <code>true</code> if the view class is a {@link SubViewContainer}
	 */
	boolean isSubViewContainer();

	/**
	 * If {@link #isSubView()}, get parent View name
	 * @return Parent View name, or <code>null</code> if it is not a sub view
	 */
	String getParentViewName();

	/**
	 * Get optional view caption
	 * @return View caption, or <code>null</code> if not defined
	 * @see Caption
	 */
	String getCaption();

	/**
	 * Get optional view caption translation message code
	 * @return View caption translation message code, or <code>null</code> if not defined
	 * @see Caption
	 */
	String getCaptionMessageCode();

	/**
	 * Get View declared parameters
	 * @return View declared parameters, or an empty set if no parameter declared
	 */
	Collection<ViewParameterDefinition> getParameters();

	/**
	 * Get view {@link OnShow} methods
	 * @return OnShow methods in call order, or an empty list if none
	 */
	List<Method> getOnShowMethods();

	/**
	 * Check whether given {@link OnShow} method must be called also at browser page refresh
	 * @param onShowMethod Method to check
	 * @return <code>true</code> if method must be called also at browser page refresh
	 */
	boolean isFireOnRefresh(Method onShowMethod);

	/**
	 * Get view {@link OnLeave} methods
	 * @return OnLeave methods in call order, or an empty list if none
	 */
	List<Method> getOnLeaveMethods();

	/**
	 * View class fields which declare {@link ViewContext} data injection
	 * @return Context inection fields, or an empty set if none
	 */
	Collection<ViewContextField> getContextInjectionFields();

	/**
	 * Get the optional view {@link Authenticate} annotation.
	 * @return View {@link Authenticate} annotation
	 */
	Optional<Authenticate> getAuthentication();

	/**
	 * View parameter definition.
	 */
	public interface ViewParameterDefinition extends Serializable {

		/**
		 * Get the parameter name
		 * @return Parameter name
		 */
		String getName();

		/**
		 * Get the parameter value type
		 * @return Parameter value type
		 */
		Class<?> getType();

		/**
		 * Get whether the parameter is required
		 * @return <code>true</code> if parameter is required, <code>false</code> otherwise
		 */
		boolean isRequired();

		/**
		 * Get optional parameter default value
		 * @return Parameter default value, or <code>null</code> if not defined
		 */
		Object getDefaultValue();

		/**
		 * Get the view class field bound to this parameter definition
		 * @return View class field bound to this parameter definition
		 */
		Field getField();

		/**
		 * View parameter field read method, if any
		 * @return Field read method, or <code>null</code> if not available
		 */
		Method getReadMethod();

		/**
		 * View parameter field write method, if any
		 * @return Field write method, or <code>null</code> if not available
		 */
		Method getWriteMethod();

	}

	/**
	 * Provider of {@link ViewConfiguration}s.
	 */
	public interface ViewConfigurationProvider {

		/**
		 * Get {@link ViewConfiguration} associated to given view class
		 * <p>
		 * This method is intended for internal use only.
		 * </p>
		 * @param viewClass View class
		 * @return Associated ViewConfiguration, or <code>null</code> if not available
		 */
		ViewConfiguration getViewConfiguration(Class<? extends View> viewClass);

	}

	/**
	 * Exception related to {@link View} configuration errors.
	 */
	@SuppressWarnings("serial")
	public static class ViewConfigurationException extends RuntimeException {

		/**
		 * Constructor with error message
		 * @param message Error message
		 */
		public ViewConfigurationException(String message) {
			super(message);
		}

		/**
		 * Constructor with nested exception
		 * @param cause Nested exception
		 */
		public ViewConfigurationException(Throwable cause) {
			super(cause);
		}

		/**
		 * Constructor with error message and nested exception
		 * @param message Error message
		 * @param cause Nested exception
		 */
		public ViewConfigurationException(String message, Throwable cause) {
			super(message, cause);
		}

	}

}
