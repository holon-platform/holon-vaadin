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
package com.holonplatform.vaadin.components.builders;

import com.holonplatform.vaadin.components.Field;
import com.holonplatform.vaadin.components.Input;
import com.holonplatform.vaadin.components.ValidatableInput;
import com.vaadin.data.HasValue;

/**
 * Builder to create {@link Input} instances.
 * 
 * @param <T> Value type
 * @param <C> Input type
 * @param <B> Concrete builder type
 * 
 * @since 5.0.0
 */
public interface InputBuilder<T, C extends Input<T>, B extends InputBuilder<T, C, B>> extends InputConfigurator<T, B> {

	/**
	 * Instructs the builder to resolve any message localization (for example component caption and description) only
	 * after the component is attached to parent layout. By default, localization is performed immediately during
	 * component building.
	 * @return this
	 */
	B deferLocalization();

	/**
	 * Build and returns the {@link Input} instance.
	 * @return the {@link Input} instance
	 */
	C build();

	/**
	 * Build a {@link ValidatableInput} input component.
	 * @return {@link ValidatableInput} builder
	 */
	ValidatableInputBuilder<T, ValidatableInput<T>> validatable();

	/**
	 * Build the input component as a {@link HasValue} component.
	 * @return the input {@link HasValue} instance
	 */
	Field<T> asField();

}
