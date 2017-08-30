/*
 * Copyright 2000-2017 Holon TDCN.
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
package com.holonplatform.vaadin.components;

import java.util.Optional;

import com.holonplatform.core.Validator;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.components.builders.ValidatableInputBuilder;
import com.holonplatform.vaadin.internal.components.ValidatableInputWrapper;
import com.holonplatform.vaadin.internal.components.builders.DefaultValidatableInputBuilder;
import com.vaadin.data.HasValue;
import com.vaadin.shared.Registration;
import com.vaadin.ui.Component;

/**
 * An {@link Input} component with validation support using {@link Validator}s.
 * 
 * @param <V> Value type
 *
 * @since 5.0.0
 */
public interface ValidatableInput<V> extends Input<V>, Validatable {

	/**
	 * Adds a {@link Validator} to validate the input value.
	 * @param validator The validator to add (not null)
	 * @return The validator registration reference
	 */
	Registration addValidator(Validator<V> validator);

	/**
	 * Sets whether to validate the value, using registered {@link Validator}s, every time the {@link Input} value
	 * changes.
	 * @param validateOnValueChange <code>true</code> to perform value validation every time the {@link Input} value
	 *        changes, <code>false</code> if not
	 */
	void setValidateOnValueChange(boolean validateOnValueChange);

	/**
	 * Gets whether to validate the value, using registered {@link Validator}s, every time the {@link Input} value
	 * changes.
	 * <p>
	 * Default is <code>true</code>.
	 * </p>
	 * @return <code>true</code> if the value validation must be performed every time the {@link Input} value changes
	 */
	boolean isValidateOnValueChange();

	/**
	 * Set the {@link ValidationStatusHandler} to use to track validation status changes.
	 * @param validationStatusHandler the {@link ValidationStatusHandler} to set
	 */
	void setValidationStatusHandler(ValidationStatusHandler validationStatusHandler);

	/**
	 * Get the {@link ValidationStatusHandler} to use to track validation status changes, if available.
	 * @return the optional {@link ValidationStatusHandler}
	 */
	Optional<ValidationStatusHandler> getValidationStatusHandler();

	/**
	 * Create a {@link ValidatableInput} from given {@link Input} instance.
	 * @param <T> Value type
	 * @param input The {@link Input} instance (not null)
	 * @return A new {@link ValidatableInput} component which wraps the given <code>input</code>
	 */
	static <T> ValidatableInput<T> from(Input<T> input) {
		ObjectUtils.argumentNotNull(input, "Input must be not null");
		return (input instanceof ValidatableInput) ? (ValidatableInput<T>) input : new ValidatableInputWrapper<>(input);
	}

	/**
	 * Create a {@link ValidatableInput} component type from given {@link HasValue} component.
	 * @param <T> Value type
	 * @param <F> {@link HasValue} component type
	 * @param field The field instance (not null)
	 * @return A new {@link ValidatableInput} component which wraps the given <code>field</code>
	 */
	static <F extends HasValue<T> & Component, T> ValidatableInput<T> from(F field) {
		return from(Input.from(field));
	}

	/**
	 * Get a fluent builder to create and setup a {@link ValidatableInput} from given {@link Input}.
	 * @param <T> Value type
	 * @param input Concrete input component (not null)
	 * @return {@link ValidatableInput} builder
	 */
	static <T> ValidatableInputBuilder<T, ValidatableInput<T>> builder(Input<T> input) {
		return new DefaultValidatableInputBuilder<>(input);
	}

}
