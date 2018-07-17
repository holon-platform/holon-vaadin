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
import com.holonplatform.vaadin.components.builders.SecretInputBuilder;
import com.holonplatform.vaadin.internal.components.builders.AbstractFieldBuilder;
import com.vaadin.shared.Registration;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.PasswordField;

/**
 * A field that is used to enter secret text information like passwords. The entered text is not displayed on the
 * screen.
 *
 * @since 5.0.0
 */
public class SecretField extends PasswordField implements Input<String>, Field<String> {

	private static final long serialVersionUID = -288626996273192490L;

	/**
	 * Treat empty values as <code>null</code> values
	 */
	private boolean emptyValuesAsNull = true;

	/**
	 * Treat blank values as <code>null</code> values
	 */
	private boolean blankValuesAsNull = false;

	/**
	 * Constructor
	 */
	public SecretField() {
		super();
		addStyleName("h-field");
		addStyleName("h-secretfield");
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
	 * Init field with and intenal <code>null</code> value, instead of default empty String value
	 */
	public void initWithNullValue() {
		super.doSetValue(null);
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
		if (value != null) {
			if (isBlankValuesAsNull() && value.trim().equals("")) {
				return null;
			}
			if (isEmptyValuesAsNull() && value.length() == 0) {
				return null;
			}
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
		return Input.super.isEmpty();
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
	 * Default {@link SecretInputBuilder} implementation.
	 *
	 * @since 5.0.0
	 */
	public static class Builder extends AbstractFieldBuilder<String, Input<String>, SecretField, SecretInputBuilder>
			implements SecretInputBuilder {

		/**
		 * Constructor
		 */
		public Builder() {
			super(new SecretField());
			getInstance().initWithNullValue();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.SecretFieldBuilder#emptyValuesAsNull(boolean)
		 */
		@Override
		public SecretInputBuilder emptyValuesAsNull(boolean enable) {
			getInstance().setEmptyValuesAsNull(enable);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.SecretFieldBuilder#blankValuesAsNull(boolean)
		 */
		@Override
		public SecretInputBuilder blankValuesAsNull(boolean enable) {
			getInstance().setBlankValuesAsNull(enable);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentBuilder#builder()
		 */
		@Override
		protected SecretInputBuilder builder() {
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.internal.components.builders.AbstractFieldBuilder#build(com.vaadin.ui.AbstractField)
		 */
		@Override
		protected Input<String> build(SecretField instance) {
			return instance;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.internal.components.builders.AbstractFieldBuilder#buildAsField(com.vaadin.ui.
		 * AbstractField)
		 */
		@Override
		protected Field<String> buildAsField(SecretField instance) {
			return instance;
		}

	}

}
