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

import java.util.Optional;

import com.holonplatform.vaadin.components.Field;
import com.holonplatform.vaadin.components.Input;
import com.holonplatform.vaadin.components.builders.StringInputBuilder;
import com.holonplatform.vaadin.internal.components.builders.AbstractStringFieldBuilder;
import com.vaadin.shared.Registration;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.TextField;

/**
 * A {@link String} type {@link Input} field.
 * 
 * @since 5.0.0
 */
public class StringField extends TextField implements Input<String>, Field<String> {

	private static final long serialVersionUID = -4023624843325799165L;

	/**
	 * Treat empty values as <code>null</code> values
	 */
	private boolean emptyValuesAsNull = true;

	/**
	 * Treat blank values as <code>null</code> values
	 */
	private boolean blankValuesAsNull = false;

	/**
	 * Constructs an empty <code>StringField</code> with no caption.
	 */
	public StringField() {
		super();
		init();
	}

	/**
	 * Constructs an empty <code>StringField</code> with given caption and given initial value.
	 * @param caption the field caption.
	 * @param value Initial value.
	 */
	public StringField(String caption, String value) {
		super(caption, value);
		init();
	}

	/**
	 * Constructs an empty <code>StringField</code> with given caption.
	 * @param caption the field caption.
	 */
	public StringField(String caption) {
		super(caption);
		init();
	}

	/**
	 * Init field
	 */
	protected void init() {
		addStyleName("h-field");
		addStyleName("h-stringfield");
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#getComponent()
	 */
	@Override
	public Component getComponent() {
		return this;
	}

	/**
	 * Gets whether to treat empty String values as <code>null</code> values
	 * @return <code>true</code> to treat empty String values as <code>null</code> values, false otherwise
	 */
	public boolean isEmptyValuesAsNull() {
		return emptyValuesAsNull;
	}

	/**
	 * Sets whether to treat empty String values as <code>null</code> values
	 * @param emptyValuesAsNull <code>true</code> to treat empty String values as <code>null</code> values, false
	 *        otherwise
	 */
	public void setEmptyValuesAsNull(boolean emptyValuesAsNull) {
		this.emptyValuesAsNull = emptyValuesAsNull;
	}

	/**
	 * Gets whether to treat blank String values (empty or whitespaces only) as <code>null</code> values
	 * @return <code>true</code> to treat blank String values (empty or whitespaces only) as <code>null</code> values,
	 *         false otherwise
	 */
	public boolean isBlankValuesAsNull() {
		return blankValuesAsNull;
	}

	/**
	 * Sets whether to treat blank String values (empty or whitespaces only) as <code>null</code> values
	 * @param blankValuesAsNull <code>true</code> to treat blank String values (empty or whitespaces only) as
	 *        <code>null</code> values, false otherwise
	 */
	public void setBlankValuesAsNull(boolean blankValuesAsNull) {
		this.blankValuesAsNull = blankValuesAsNull;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.ui.AbstractTextField#setValue(java.lang.String)
	 */
	@Override
	public void setValue(String value) {
		super.setValue((value == null) ? "" : value);
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.ui.AbstractTextField#getValue()
	 */
	@Override
	public String getValue() {
		String value = super.getValue();
		if (value != null && value.length() == 0 && isEmptyValuesAsNull()) {
			return null;
		}
		return value;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.ui.AbstractTextField#doSetValue(java.lang.String)
	 */
	@Override
	protected void doSetValue(String value) {
		super.doSetValue(sanitizeValue(value));
	}

	/**
	 * Checkup String value to apply empty-as-null or blank-as-null behaviours, if enabled.
	 * @param value Value to check
	 * @return Sanitized value
	 */
	protected String sanitizeValue(String value) {
		if (value != null && isBlankValuesAsNull() && value.trim().equals("")) {
			return "";
		}
		return value;
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
			com.holonplatform.vaadin.components.Input.ValueChangeListener<String> listener) {
		return ValueChangeListenerUtils.adapt(this, this, listener);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValueHolder#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		if (Input.super.isEmpty()) {
			return true;
		}
		if ((isEmptyValuesAsNull() || isBlankValuesAsNull()) && getValue() == null) {
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValueHolder#getOptionalValue()
	 */
	@Override
	public Optional<String> getOptionalValue() {
		return Input.super.getOptionalValue();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValueHolder#clear()
	 */
	@Override
	public void clear() {
		Input.super.clear();
	}

	// Value change mode support

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#isValueChangeModeSupported()
	 */
	@Override
	public boolean isValueChangeModeSupported() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#setValueChangeMode(com.vaadin.shared.ui.ValueChangeMode)
	 */
	@Override
	public void setValueChangeMode(ValueChangeMode valueChangeMode) {
		super.setValueChangeMode(valueChangeMode);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#getValueChangeMode()
	 */
	@Override
	public ValueChangeMode getValueChangeMode() {
		return super.getValueChangeMode();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#setValueChangeTimeout(int)
	 */
	@Override
	public void setValueChangeTimeout(int valueChangeTimeout) {
		super.setValueChangeTimeout(valueChangeTimeout);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#getValueChangeTimeout()
	 */
	@Override
	public int getValueChangeTimeout() {
		return super.getValueChangeTimeout();
	}

	// Builder

	/**
	 * Builder to create {@link StringField} instances.
	 * <p>
	 * By default, this builder sets the empty String as <code>null</code> representation and null settings allowed to
	 * true. The internal field value is inited with <code>null</code>.
	 * </p>
	 */
	public static class Builder extends AbstractStringFieldBuilder<StringField> {

		public Builder() {
			super(new StringField());
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentBuilder#builder()
		 */
		@Override
		protected StringInputBuilder builder() {
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.StringFieldBuilder#emptyValuesAsNull(boolean)
		 */
		@Override
		public StringInputBuilder emptyValuesAsNull(boolean enable) {
			getInstance().setEmptyValuesAsNull(enable);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.StringFieldBuilder#blankValuesAsNull(boolean)
		 */
		@Override
		public StringInputBuilder blankValuesAsNull(boolean enable) {
			getInstance().setBlankValuesAsNull(enable);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.builders.ValueChangeModeConfigurator#valueChangeMode(com.vaadin.shared.ui
		 * .ValueChangeMode)
		 */
		@Override
		public StringInputBuilder valueChangeMode(ValueChangeMode valueChangeMode) {
			getInstance().setValueChangeMode(valueChangeMode);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.ValueChangeModeConfigurator#valueChangeTimeout(int)
		 */
		@Override
		public StringInputBuilder valueChangeTimeout(int valueChangeTimeout) {
			getInstance().setValueChangeTimeout(valueChangeTimeout);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.internal.components.builders.AbstractFieldBuilder#build(com.vaadin.ui.AbstractField)
		 */
		@Override
		protected Input<String> build(StringField instance) {
			return instance;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.internal.components.builders.AbstractFieldBuilder#buildAsField(com.vaadin.ui.
		 * AbstractField)
		 */
		@Override
		protected Field<String> buildAsField(StringField instance) {
			return instance;
		}

	}

}
