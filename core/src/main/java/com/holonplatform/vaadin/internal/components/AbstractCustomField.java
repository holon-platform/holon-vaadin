/*
 * Copyright 2016-2017 Axioma srl.
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
package com.holonplatform.vaadin.internal.components;

import java.util.Locale;
import java.util.Optional;

import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.components.Field;
import com.holonplatform.vaadin.components.Input;
import com.vaadin.data.HasValue;
import com.vaadin.server.ErrorMessage;
import com.vaadin.shared.Registration;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.UI;

/**
 * Base class to build a custom field wrapping a {@link HasValue} component, maybe with a different value type, that
 * acts as presenter and input handler in UI.
 * 
 * <p>
 * Any style name added to this Field using {@link #addStyleName(String)} is reflected to internal field, to ensure
 * visual consistency using common field styles.
 * </p>
 * 
 * @param <T> Field type
 * @param <I> Internal value type
 * @param <F> Internal field type
 * 
 * @since 5.0.0
 */
public abstract class AbstractCustomField<T, I, F extends HasValue<I> & Component> extends CustomField<T>
		implements Input<T>, Field<T> {

	private static final long serialVersionUID = -5952052029882768908L;

	/**
	 * Default style class name for invalid fields
	 */
	static final String DEFAULT_INVALID_FIELD_STYLE_CLASS = "error";

	/**
	 * Field type
	 */
	private final Class<? extends T> type;

	/**
	 * Internal Field
	 */
	private F internalField;

	/**
	 * Constructor
	 * @param type Field concrete type
	 */
	public AbstractCustomField(Class<? extends T> type) {
		this(type, true);
	}

	/**
	 * Constructor
	 * @param type Field concrete type
	 * @param init <code>true</code> to init the internal field. If <code>false</code>, subclasses must ensure the
	 *        {@link #init()} method to called after construction.
	 */
	public AbstractCustomField(Class<? extends T> type, boolean init) {
		super();

		ObjectUtils.argumentNotNull(type, "Field type must be not null");

		this.type = type;

		setWidthUndefined();

		addStyleName("h-field", false);

		if (init) {
			init();
		}

	}

	/**
	 * Initialize and configure the internal wrapped field
	 */
	protected void init() {
		// build internal field
		this.internalField = buildInternalField(getType());
	}

	/**
	 * Build concrete internal Field.
	 * <p>
	 * If internal Field type is not consistent with the custom field type, it is the responsibility of subclasses to
	 * provide a suitable Converter for the internal Field.
	 * </p>
	 * @param type Concrete field type
	 * @return Internal field (must be not null)
	 */
	protected abstract F buildInternalField(Class<? extends T> type);

	/**
	 * Get the actual value from internal field value
	 * @param value internal field value
	 * @return the actual value
	 */
	protected abstract T fromInternalValue(I value);

	/**
	 * Convert the value to internal value type
	 * @param value the value to convert
	 * @return the internal value
	 */
	protected abstract I toInternalValue(T value);

	/**
	 * Get the most suitable {@link Locale} to use.
	 * @return the field, UI or {@link LocalizationContext} locale
	 */
	protected Locale findLocale() {
		Locale locale = getLocale();
		if (locale == null && UI.getCurrent() != null) {
			locale = UI.getCurrent().getLocale();
		}
		if (locale == null) {
			locale = LocalizationContext.getCurrent().filter(l -> l.isLocalized()).flatMap(l -> l.getLocale())
					.orElse(Locale.getDefault());
		}
		return locale;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValueHolder#getValue()
	 */
	@Override
	public T getValue() {
		return fromInternalValue(getInternalField().getValue());
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.ui.AbstractField#doSetValue(java.lang.Object)
	 */
	@Override
	protected void doSetValue(T value) {
		getInternalField().setValue(toInternalValue(value));
	}

	/**
	 * Gets the wrapped field
	 * @return the internal field
	 */
	protected F getInternalField() {
		return internalField;
	}

	/**
	 * Get the field type.
	 * @return the field type
	 */
	public Class<? extends T> getType() {
		return type;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.ui.CustomField#initContent()
	 */
	@Override
	protected Component initContent() {
		final Component content = getInternalField();
		if (getWidth() > -1) {
			content.setWidth(100, Unit.PERCENTAGE);
		}
		if (getHeight() > -1) {
			content.setHeight(100, Unit.PERCENTAGE);
		}
		return content;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.ui.AbstractComponent#addStyleName(java.lang.String)
	 */
	@Override
	public void addStyleName(String style) {
		super.addStyleName(style);
		// add to internal field too
		getInternalField().addStyleName(style);
	}

	/**
	 * Adds one or more style names to this component.
	 * @param styleName Style name to add
	 * @param reflectToInternalField <code>true</code> to add given <code>styleName</code> to internal field too
	 */
	protected void addStyleName(String styleName, boolean reflectToInternalField) {
		super.addStyleName(styleName);
		if (reflectToInternalField) {
			getInternalField().addStyleName(styleName);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.ui.AbstractComponent#removeStyleName(java.lang.String)
	 */
	@Override
	public void removeStyleName(String style) {
		super.removeStyleName(style);
		getContent().removeStyleName(style);
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.ui.AbstractField#setReadOnly(boolean)
	 */
	@Override
	public void setReadOnly(boolean readOnly) {
		super.setReadOnly(readOnly);
		// reflect to internal field
		getInternalField().setReadOnly(readOnly);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#getComponent()
	 */
	@Override
	public Component getComponent() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.ui.AbstractComponent#setComponentError(com.vaadin.server.ErrorMessage)
	 */
	@Override
	public void setComponentError(ErrorMessage componentError) {
		super.setComponentError(componentError);
		if (componentError != null) {
			addStyleName(DEFAULT_INVALID_FIELD_STYLE_CLASS);
		} else {
			removeStyleName(DEFAULT_INVALID_FIELD_STYLE_CLASS);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.ui.AbstractField#setRequired(boolean)
	 */
	@Override
	public void setRequired(boolean required) {
		setRequiredIndicatorVisible(required);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#isRequired()
	 */
	@Override
	public boolean isRequired() {
		return isRequiredIndicatorVisible();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#addValueChangeListener(com.holonplatform.vaadin.components.Input.
	 * ValueChangeListener)
	 */
	@Override
	public Registration addValueChangeListener(
			final com.holonplatform.vaadin.components.Input.ValueChangeListener<T> listener) {
		ObjectUtils.argumentNotNull(listener, "ValueChangeListener must be not null");
		return getInternalField().addValueChangeListener(e -> listener.valueChange(new DefaultValueChangeEvent<>(this,
				fromInternalValue(e.getOldValue()), fromInternalValue(e.getValue()), e.isUserOriginated())));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValueHolder#getOptionalValue()
	 */
	@Override
	public Optional<T> getOptionalValue() {
		return Input.super.getOptionalValue();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValueHolder#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return Input.super.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValueHolder#clear()
	 */
	@Override
	public void clear() {
		Input.super.clear();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValueHolder#getEmptyValue()
	 */
	@Override
	public T getEmptyValue() {
		return Input.super.getEmptyValue();
	}

}
