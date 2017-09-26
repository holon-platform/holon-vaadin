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

import com.holonplatform.vaadin.js.ElementPropertyHandler;
import com.vaadin.ui.TextField;

/**
 * A {@link TextField} using {@link ElementPropertyHandler} extension to setup html5 input type and allow only numeric
 * digits and symbols as user input.
 * 
 * @since 5.0.0
 */
public class NumericTextField extends TextField {

	private static final long serialVersionUID = 5322236596887014875L;

	/**
	 * Set html5 input type property as "number"
	 */
	private boolean html5NumberInputType = false;

	/**
	 * Input validation JavaScript regular expression
	 */
	private String inputValidationRegExp = "/^[\\d\\n\\t\\r]+$/";

	/**
	 * Whether to allow negative numbers input
	 */
	private boolean allowNegative = true;

	/**
	 * Allowed symbols in addition to digits and minus sign (e.g. groups/decimals separators)
	 */
	private char[] allowedSymbols;

	/**
	 * Client side element property handler
	 */
	protected final ElementPropertyHandler elementPropertyHandler;

	/**
	 * Flag to track ElementPropertyHandler first setup
	 */
	private boolean settedUp = false;

	/**
	 * Construct a new NumericTextField, setting an empty String as <code>null</code> representation.
	 */
	public NumericTextField() {
		super();
		this.elementPropertyHandler = new ElementPropertyHandler(this);
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.ui.AbstractTextField#setValue(java.lang.String)
	 */
	@Override
	public void setValue(String value) {
		super.setValue((value != null) ? value : "");
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.ui.AbstractTextField#getValue()
	 */
	@Override
	public String getValue() {
		String value = super.getValue();
		return (value != null && value.trim().length() == 0) ? null : value;
	}

	/**
	 * Gets whether to set html5 input type property as "number"
	 * @return <code>true</code> to set html5 input type property as "number"
	 */
	public boolean isHtml5NumberInputType() {
		return html5NumberInputType;
	}

	/**
	 * Sets whether to set html5 input type property as "number"
	 * @param html5NumberInputType <code>true</code> to set html5 input type property as "number"
	 */
	public void setHtml5NumberInputType(boolean html5NumberInputType) {
		this.html5NumberInputType = html5NumberInputType;
		if (settedUp) {
			setupInputType(false);
		}
	}

	/**
	 * Gets input validation JavaScript regular expression
	 * @return Input validation JavaScript regular expression
	 */
	public String getInputValidationRegExp() {
		return inputValidationRegExp;
	}

	/**
	 * Sets input validation JavaScript regular expression
	 * @param inputValidationRegExp Input validation JavaScript regular expression
	 */
	public void setInputValidationRegExp(String inputValidationRegExp) {
		this.inputValidationRegExp = inputValidationRegExp;
		if (settedUp) {
			setupInputValidation(false);
		}
	}

	/**
	 * Gets whether to allow negative numbers input
	 * @return <code>true</code> to allow negative numbers input
	 */
	public boolean isAllowNegative() {
		return allowNegative;
	}

	/**
	 * Sets whether to allow negative numbers input
	 * @param allowNegative <code>true</code> to allow negative numbers input
	 */
	public void setAllowNegative(boolean allowNegative) {
		this.allowNegative = allowNegative;
		if (settedUp) {
			setupInputValidation(false);
			if (isHtml5NumberInputType()) {
				setupInputType(false);
			}
		}
	}

	/**
	 * Gets the allowed symbols in addition to digits and minus sign (e.g. groups/decimals separators)
	 * @return Allowed symbols in addition to digits and minus sign
	 */
	public char[] getAllowedSymbols() {
		return allowedSymbols;
	}

	/**
	 * Sets the allowed symbols in addition to digits and minus sign (e.g. groups/decimals separators)
	 * @param allowedSymbols Allowed symbols in addition to digits and minus sign
	 */
	public void setAllowedSymbols(char[] allowedSymbols) {
		this.allowedSymbols = allowedSymbols;
		if (settedUp) {
			setupInputValidation(false);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.ui.AbstractTextField#beforeClientResponse(boolean)
	 */
	@Override
	public void beforeClientResponse(boolean initial) {
		super.beforeClientResponse(initial);

		if (initial) {
			setup();
			settedUp = true;
		}
	}

	/**
	 * Setup Component client element according to {@link #isHtml5NumberInputType()} and
	 * {@link #getInputValidationRegExp()}.
	 */
	protected void setup() {
		setupInputType(true);
		setupInputValidation(true);
	}

	/**
	 * Setup html element input type
	 * @param initial Initial setup
	 */
	protected void setupInputType(boolean initial) {
		if (isHtml5NumberInputType()) {
			elementPropertyHandler.setProperty("type", "number");
			if (!isAllowNegative()) {
				elementPropertyHandler.setProperty("min", "0");
				elementPropertyHandler.setProperty("oninput", "validity.valid||(value='');");
			}
		} else if (!initial) {
			elementPropertyHandler.removeProperty("type");
			elementPropertyHandler.removeProperty("min");
			elementPropertyHandler.removeProperty("oninput");
		}
	}

	/**
	 * Setup client side input validation
	 * @param initial Initial setup
	 */
	protected void setupInputValidation(boolean initial) {
		if (getInputValidationRegExp() != null) {
			elementPropertyHandler.setEventHandler("onkeypress", buildInputValidationFunction());
		} else if (!initial) {
			elementPropertyHandler.removeProperty("onkeypress");
		}
	}

	/**
	 * Build the input validation JavaScript function
	 * @return Input validation JavaScript function
	 */
	protected String buildInputValidationFunction() {
		StringBuilder sb = new StringBuilder();
		sb.append("function(e) {");
		sb.append("var c = holon_vaadin.getChar(e); ");
		sb.append("return c==null || ");
		if (isAllowNegative()) {
			// sb.append("c=='-' || ");
			sb.append("(c=='-' && this.value=='') || ");
		}
		if (getAllowedSymbols() != null) {
			for (char symbol : getAllowedSymbols()) {
				sb.append("c=='");
				sb.append(symbol);
				sb.append("' || ");
			}
		}
		sb.append(getInputValidationRegExp());
		sb.append(".test(c);}");
		return sb.toString();
	}

}
