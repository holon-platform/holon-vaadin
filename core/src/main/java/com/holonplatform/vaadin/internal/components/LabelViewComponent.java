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

import com.holonplatform.core.presentation.StringValuePresenter;
import com.holonplatform.vaadin.components.ViewComponent;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;

/**
 * {@link ViewComponent} implementation using a {@link Label} as backing UI component.
 * 
 * <p>
 * To display null values, this component uses <code>&nbsp;</code> HTML entity to avoid label invisibility.
 * </p>
 * 
 * @param <T> Value type
 * 
 * @since 5.0.0
 */
public class LabelViewComponent<T> extends AbstractViewComponent<T> {

	private static final long serialVersionUID = 4100065784829844784L;

	/**
	 * Converter to use to convert value into the String to display.
	 * @param <T> Value type
	 */
	@FunctionalInterface
	public interface StringValueConverter<T> {

		/**
		 * Convert value into String
		 * @param value Value to convert
		 * @return Converted value
		 */
		String convert(T value);

	}

	/**
	 * Label content for empty value
	 */
	private final static String EMPTY_VALUE_DISPLAY = "&nbsp;";

	/**
	 * String values converter
	 */
	private StringValueConverter<T> stringConverter;

	/**
	 * Constructor
	 * @param type Concrete value type
	 */
	public LabelViewComponent(Class<? extends T> type) {
		super(type, new Label());
		getLabel().setContentMode(ContentMode.HTML);

		// default converter
		stringConverter = (v) -> StringValuePresenter.getDefault().present(null, v, null);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValueComponent#getComponent()
	 */
	@Override
	public Component getComponent() {
		return this;
	}

	/**
	 * Internal Label
	 * @return the internal Label
	 */
	protected Label getLabel() {
		return (Label) getCompositionRoot();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.AbstractViewComponent#updateValue(java.lang.Object)
	 */
	@Override
	protected void updateValue(T value) {
		String display = convertValue(value);
		getLabel().setValue((display != null) ? display : EMPTY_VALUE_DISPLAY);
	}

	/**
	 * Convert given value into a String
	 * @param value Value to convert
	 * @return Converted value
	 */
	protected String convertValue(T value) {
		StringValueConverter<T> converter = getStringConverter();
		if (converter == null) {
			throw new IllegalStateException("StringValueConverter not available");
		}
		return converter.convert(value);
	}

	/**
	 * Gets the converter to use to display values
	 * @return the StringValueConverter
	 */
	public StringValueConverter<T> getStringConverter() {
		return stringConverter;
	}

	/**
	 * Sets the converter to use to display values
	 * @param stringConverter the StringValueConverter to set
	 */
	public void setStringConverter(StringValueConverter<T> stringConverter) {
		this.stringConverter = stringConverter;
	}

}
