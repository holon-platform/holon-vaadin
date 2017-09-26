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

import java.text.NumberFormat;

import com.holonplatform.vaadin.components.Input;

/**
 * Builder to create {@link Input} instances to handle {@link Number} type values.
 * 
 * @param <T> Number type
 * 
 * @since 5.0.0
 */
public interface NumberInputBuilder<T extends Number> extends TextInputBuilder<T, Input<T>, NumberInputBuilder<T>> {

	/**
	 * Sets the {@link NumberFormat} to use to represent number values in UI
	 * @param numberFormat the NumberFormat to set
	 * @return this
	 */
	NumberInputBuilder<T> numberFormat(NumberFormat numberFormat);

	/**
	 * Sets whether to allow negative numbers input
	 * @param allowNegative <code>true</code> to allow negative numbers input
	 * @return this
	 */
	NumberInputBuilder<T> allowNegative(boolean allowNegative);

	/**
	 * Sets whether to set html5 input type property as "number"
	 * @param html5NumberInputType <code>true</code> to set html5 input type property as "number"
	 * @return this
	 */
	NumberInputBuilder<T> html5NumberInputType(boolean html5NumberInputType);

}
