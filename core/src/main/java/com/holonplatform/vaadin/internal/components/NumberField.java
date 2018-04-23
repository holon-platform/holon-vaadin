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

import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.core.internal.utils.TypeUtils;
import com.holonplatform.vaadin.components.Field;
import com.holonplatform.vaadin.components.Input;
import com.holonplatform.vaadin.components.builders.NumberInputBuilder;
import com.holonplatform.vaadin.internal.components.builders.AbstractFieldBuilder;
import com.holonplatform.vaadin.internal.converters.StringToNumberConverter;
import com.vaadin.data.ValueContext;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.shared.Registration;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.TextField;

/**
 * A field for {@link Number} value type input.
 * 
 * <p>
 * A standard {@link TextField} is used as UI widget for user input, so no client-side character validation is
 * performed.
 * </p>
 * 
 * <p>
 * This field provides common {@link TextField} properties getters and setters, for example {@link #setMaxLength(int)}
 * or {@link #setInputPrompt(String)}.
 * </p>
 * 
 * <p>
 * A {@link StringToNumberConverter} is used for String to numeric value conversions and backward.
 * </p>
 * 
 * @param <T> Number type
 * 
 * @since 5.0.0
 */
public class NumberField<T extends Number> extends AbstractCustomField<T, String, NumericTextField> {

	private static final long serialVersionUID = -6791184576381210657L;

	private StringToNumberConverter<T> converter;

	/**
	 * Default constructor
	 * @param <N> Number type
	 * @param numberClass Number field type (not null)
	 */
	public <N extends T> NumberField(Class<N> numberClass) {
		super(TypeUtils.box(numberClass));

		addStyleName("h-numberfield", false);
	}

	/**
	 * Constructor with caption
	 * @param <N> Number type
	 * @param numberClass Number field type (not null)
	 * @param caption The field caption
	 */
	public <N extends T> NumberField(Class<N> numberClass, String caption) {
		this(numberClass);
		setCaption(caption);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.AbstractCustomField#buildInternalField(java.lang.Class)
	 */
	@Override
	protected NumericTextField buildInternalField(Class<? extends T> type) {
		return new NumericTextField();
	}

	/**
	 * Sets a fixed NumberFormat to use for value conversions from user input field and back.
	 * @param numberFormat the NumberFormat to set
	 */
	public void setNumberFormat(NumberFormat numberFormat) {
		getConverter().setNumberFormat(numberFormat);
	}

	/**
	 * Get the string to number converter to use.
	 * @return the converter
	 */
	protected StringToNumberConverter<T> getConverter() {
		if (converter == null) {
			converter = new StringToNumberConverter<>(getType());
		}
		return converter;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.AbstractCustomField#fromInternalValue(java.lang.Object)
	 */
	@Override
	protected T fromInternalValue(String value) {
		return getConverter().convertToModel(value, new ValueContext(findLocale()))
				.getOrThrow(msg -> new IllegalArgumentException(msg));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.AbstractCustomField#toInternalValue(java.lang.Object)
	 */
	@Override
	protected String toInternalValue(T value) {
		return getConverter().convertToPresentation(value, new ValueContext(findLocale()));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.AbstractCustomField#initContent()
	 */
	@Override
	protected Component initContent() {
		setupLocaleSymbolsInputValidation();
		return super.initContent();
	}

	/**
	 * Setup user input validation allowed symbols according to current Locale and number type
	 */
	protected void setupLocaleSymbolsInputValidation() {

		Locale locale = LocalizationContext.getCurrent().filter(l -> l.isLocalized()).flatMap(l -> l.getLocale())
				.orElse(getLocale());
		if (locale == null) {
			// use default
			locale = Locale.getDefault();
		}

		// check grouping
		boolean useGrouping = true;
		NumberFormat nf = getConverter().getNumberFormat(locale);
		if (nf != null) {
			useGrouping = nf.isGroupingUsed();
		}

		DecimalFormatSymbols dfs = new DecimalFormatSymbols(locale);

		char[] symbols = null;
		if (useGrouping) {
			if (TypeUtils.isDecimalNumber(getType())) {
				symbols = new char[] { dfs.getGroupingSeparator(), dfs.getDecimalSeparator() };
			} else {
				symbols = new char[] { dfs.getGroupingSeparator() };
			}
		} else {
			if (TypeUtils.isDecimalNumber(getType())) {
				symbols = new char[] { dfs.getDecimalSeparator() };
			}
		}

		getInternalField().setAllowedSymbols(symbols);
	}

	/**
	 * Gets whether to allow negative numbers input
	 * @return <code>true</code> to allow negative numbers input
	 */
	public boolean isAllowNegative() {
		return getInternalField().isAllowNegative();
	}

	/**
	 * Sets whether to allow negative numbers input
	 * @param allowNegative <code>true</code> to allow negative numbers input
	 */
	public void setAllowNegative(boolean allowNegative) {
		getInternalField().setAllowNegative(allowNegative);
	}

	/**
	 * Gets whether to set html5 input type property as "number"
	 * @return <code>true</code> to set html5 input type property as "number"
	 */
	public boolean isHtml5NumberInputType() {
		return getInternalField().isHtml5NumberInputType();
	}

	/**
	 * Sets whether to set html5 input type property as "number"
	 * @param html5NumberInputType <code>true</code> to set html5 input type property as "number"
	 */
	public void setHtml5NumberInputType(boolean html5NumberInputType) {
		getInternalField().setHtml5NumberInputType(html5NumberInputType);
	}

	/**
	 * Returns the maximum number of characters in the field. Value -1 is considered unlimited.
	 * @return the maxLength Maximum number of characters in the field
	 */
	public int getMaxLength() {
		return getInternalField().getMaxLength();
	}

	/**
	 * Set the maximum number of characters in the field
	 * @param maxLength Maximum number of characters in the field, -1 is considered unlimited
	 */
	public void setMaxLength(int maxLength) {
		getInternalField().setMaxLength(maxLength);
	}

	/**
	 * Gets the current input prompt
	 * @return the current input prompt, or <code>null</code> if not enabled
	 */
	public String getInputPrompt() {
		return getInternalField().getPlaceholder();
	}

	/**
	 * Sets the input prompt - a textual prompt that is displayed when the field would otherwise be empty, to prompt the
	 * user for input.
	 * @param inputPrompt the input prompt to set, <code>null</code> for none
	 */
	public void setInputPrompt(String inputPrompt) {
		getInternalField().setPlaceholder(inputPrompt);
	}

	/**
	 * Sets the mode how the TextField triggers value change events.
	 * @param inputEventMode the new mode
	 */
	public void setTextChangeEventMode(ValueChangeMode inputEventMode) {
		getInternalField().setValueChangeMode(inputEventMode);
	}

	/**
	 * Gets the current {@link ValueChangeMode}
	 * @return the mode used to trigger value change events.
	 */
	public ValueChangeMode getTextChangeEventMode() {
		return getInternalField().getValueChangeMode();
	}

	/**
	 * The text change timeout modifies how often text change events are communicated to the application.
	 * @param timeout the timeout in milliseconds
	 */
	public void setTextChangeTimeout(int timeout) {
		getInternalField().setValueChangeTimeout(timeout);
	}

	/**
	 * Gets the timeout used to fire text change events.
	 * @return the timeout value in milliseconds
	 */
	public int getTextChangeTimeout() {
		return getInternalField().getValueChangeTimeout();
	}

	/**
	 * Sets the cursor position in the field. As a side effect the field will become focused.
	 * @param pos the position for the cursor
	 */
	public void setCursorPosition(int pos) {
		getInternalField().setCursorPosition(pos);
	}

	/**
	 * Returns the last known cursor position of the field.
	 * @return the cursor position
	 */
	public int getCursorPosition() {
		return getInternalField().getCursorPosition();
	}

	/**
	 * Selects all text in the field.
	 */
	public void selectAll() {
		getInternalField().selectAll();
	}

	/**
	 * Adds a listener for focus gained event
	 * @param listener Listener to add
	 * @return the listener {@link Registration}
	 */
	public Registration addFocusListener(FocusListener listener) {
		return getInternalField().addFocusListener(listener);
	}

	/**
	 * Adds a listener for focus lost event
	 * @param listener Listener to add
	 * @return the listener {@link Registration}
	 */
	public Registration addBlurListener(BlurListener listener) {
		return getInternalField().addBlurListener(listener);
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
		getInternalField().setValueChangeMode(valueChangeMode);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#getValueChangeMode()
	 */
	@Override
	public ValueChangeMode getValueChangeMode() {
		return getInternalField().getValueChangeMode();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#setValueChangeTimeout(int)
	 */
	@Override
	public void setValueChangeTimeout(int valueChangeTimeout) {
		getInternalField().setValueChangeTimeout(valueChangeTimeout);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#getValueChangeTimeout()
	 */
	@Override
	public int getValueChangeTimeout() {
		return getInternalField().getValueChangeTimeout();
	}

	// Builder

	public static class Builder<T extends Number> extends
			AbstractFieldBuilder<T, Input<T>, NumberField<T>, NumberInputBuilder<T>> implements NumberInputBuilder<T> {

		protected Localizable inputPrompt;

		public <N extends T> Builder(Class<N> numberClass) {
			super(new NumberField<>(numberClass));
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.TextInputFieldBuilder#maxLength(int)
		 */
		@Override
		public NumberInputBuilder<T> maxLength(int maxLength) {
			getInstance().setMaxLength(maxLength);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.TextInputFieldBuilder#inputPrompt(java.lang.String)
		 */
		@Override
		public NumberInputBuilder<T> inputPrompt(String inputPrompt) {
			getInstance().setInputPrompt(inputPrompt);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.TextInputFieldBuilder#inputPrompt(java.lang.String,
		 * java.lang.String, java.lang.Object[])
		 */
		@Override
		public NumberInputBuilder<T> inputPrompt(String defaultInputPrompt, String messageCode, Object... arguments) {
			this.inputPrompt = Localizable.builder().message(defaultInputPrompt).messageCode(messageCode)
					.messageArguments(arguments).build();
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.builders.TextInputFieldBuilder#inputPrompt(com.holonplatform.core.i18n.
		 * Localizable)
		 */
		@Override
		public NumberInputBuilder<T> inputPrompt(Localizable inputPrompt) {
			this.inputPrompt = inputPrompt;
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.TextInputFieldBuilder#textChangeEventMode(com.vaadin.ui.
		 * AbstractTextField.TextChangeEventMode)
		 */
		@Override
		public NumberInputBuilder<T> textChangeEventMode(ValueChangeMode inputEventMode) {
			getInstance().setTextChangeEventMode(inputEventMode);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.TextInputFieldBuilder#textChangeTimeout(int)
		 */
		@Override
		public NumberInputBuilder<T> textChangeTimeout(int timeout) {
			getInstance().setTextChangeTimeout(timeout);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.builders.ValueChangeModeConfigurator#valueChangeMode(com.vaadin.shared.ui
		 * .ValueChangeMode)
		 */
		@Override
		public NumberInputBuilder<T> valueChangeMode(ValueChangeMode valueChangeMode) {
			getInstance().setValueChangeMode(valueChangeMode);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.ValueChangeModeConfigurator#valueChangeTimeout(int)
		 */
		@Override
		public NumberInputBuilder<T> valueChangeTimeout(int valueChangeTimeout) {
			getInstance().setValueChangeTimeout(valueChangeTimeout);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.NumberFieldBuilder#numberFormat(java.text.NumberFormat)
		 */
		@Override
		public NumberInputBuilder<T> numberFormat(NumberFormat numberFormat) {
			getInstance().setNumberFormat(numberFormat);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.NumberFieldBuilder#allowNegative(boolean)
		 */
		@Override
		public NumberInputBuilder<T> allowNegative(boolean allowNegative) {
			getInstance().setAllowNegative(allowNegative);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.NumberFieldBuilder#html5NumberInputType(boolean)
		 */
		@Override
		public NumberInputBuilder<T> html5NumberInputType(boolean html5NumberInputType) {
			getInstance().setHtml5NumberInputType(html5NumberInputType);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.TextInputFieldBuilder#withFocusListener(com.vaadin.event.
		 * FieldEvents.FocusListener)
		 */
		@Override
		public NumberInputBuilder<T> withFocusListener(FocusListener listener) {
			getInstance().addFocusListener(listener);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.TextInputFieldBuilder#withBlurListener(com.vaadin.event.
		 * FieldEvents.BlurListener)
		 */
		@Override
		public NumberInputBuilder<T> withBlurListener(BlurListener listener) {
			getInstance().addBlurListener(listener);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentBuilder#builder()
		 */
		@Override
		protected NumberInputBuilder<T> builder() {
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.internal.components.builders.AbstractFieldBuilder#localize(com.vaadin.ui.
		 * AbstractField)
		 */
		@Override
		protected void localize(NumberField<T> instance) {
			super.localize(instance);

			if (inputPrompt != null) {
				instance.setInputPrompt(LocalizationContext.translate(inputPrompt, true));
			}
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.internal.components.builders.AbstractFieldBuilder#build(com.vaadin.ui.AbstractField)
		 */
		@Override
		protected Input<T> build(NumberField<T> instance) {
			return instance;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.internal.components.builders.AbstractFieldBuilder#buildAsField(com.vaadin.ui.
		 * AbstractField)
		 */
		@Override
		protected Field<T> buildAsField(NumberField<T> instance) {
			return instance;
		}

	}

}
