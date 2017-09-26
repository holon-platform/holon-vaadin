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
package com.holonplatform.vaadin.components.builders;

import java.util.Locale;

import com.holonplatform.vaadin.components.Input;
import com.holonplatform.vaadin.components.ValueHolder.ValueChangeListener;

/**
 * Interface to configure a {@link Input} component.
 * 
 * @param <T> Value type
 * @param <B> Concrete configurator type
 * 
 * @since 5.0.0
 */
public interface InputConfigurator<T, B extends InputConfigurator<T, B>> extends ComponentConfigurator<B> {

	/**
	 * Set the input component as read-only. The user can not change the value of a read-only input.
	 * @return this
	 */
	B readOnly();

	/**
	 * Sets the <i>tabulator index</i> of the component. The tab index property is used to specify the order in which
	 * the fields are focused when the user presses the Tab key. Components with a defined tab index are focused
	 * sequentially first, and then the components with no tab index.
	 * <p>
	 * If the tab index is not set (is set to zero), the default tab order is used. The order is somewhat
	 * browser-dependent, but generally follows the HTML structure of the page.
	 * </p>
	 * <p>
	 * A negative value means that the component is completely removed from the tabulation order and can not be reached
	 * by pressing the Tab key at all.
	 * </p>
	 * @param tabIndex The tab order of this component. Indexes usually start from 1. Zero means that default tab order
	 *        should be used. A negative value means that the field should not be included in the tabbing sequence.
	 * @return this
	 */
	B tabIndex(int tabIndex);

	/**
	 * Sets an initial value for the field.
	 * @param value The value to set
	 * @return this
	 */
	B withValue(T value);

	/**
	 * Add a {@link ValueChangeListener} to be notified when the input value changes.
	 * @param listener The ValueChangeListener to add
	 * @return this
	 */
	B withValueChangeListener(ValueChangeListener<T> listener);

	/**
	 * Sets the locale of this {@link Input} component.
	 * @param locale The Locale to set
	 * @return this
	 */
	B locale(Locale locale);

	/**
	 * Base field configurator.
	 * @param <T> Field value type
	 */
	public interface BaseFieldConfigurator<T> extends InputConfigurator<T, BaseFieldConfigurator<T>> {

	}

}
