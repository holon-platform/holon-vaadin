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
package com.holonplatform.vaadin.internal.components.builders;

import java.util.Optional;

import com.holonplatform.core.Validator;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.components.Input;
import com.holonplatform.vaadin.components.ValidatableInput;
import com.holonplatform.vaadin.components.ValidationStatusHandler;
import com.holonplatform.vaadin.components.builders.ValidatableInputBuilder;
import com.holonplatform.vaadin.internal.components.RequiredInputValidator;

/**
 * Default {@link ValidatableInputBuilder}.
 * 
 * @param <T> Value type
 *
 * @since 5.0.0
 */
public class DefaultValidatableInputBuilder<T> implements ValidatableInputBuilder<T, ValidatableInput<T>> {

	private final Input<T> input;

	private final ValidatableInput<T> instance;

	private boolean required = false;

	private Validator<T> requiredValidator;

	private Localizable requiredMessage;

	public DefaultValidatableInputBuilder(Input<T> input) {
		super();
		this.input = input;
		this.instance = ValidatableInput.from(input);
	}

	protected Optional<Validator<T>> getRequiredValidator() {
		return Optional.ofNullable(requiredValidator);
	}

	protected Optional<Localizable> getRequiredMessage() {
		return Optional.ofNullable(requiredMessage);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValidatableInput.Builder#withValidator(com.holonplatform.core.Validator)
	 */
	@Override
	public ValidatableInputBuilder<T, ValidatableInput<T>> withValidator(Validator<T> validator) {
		ObjectUtils.argumentNotNull(validator, "Validator must be not null");
		instance.addValidator(validator);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.ValidatableInput.Builder#validationStatusHandler(com.holonplatform.vaadin
	 * .components.ValidationStatusHandler)
	 */
	@Override
	public ValidatableInputBuilder<T, ValidatableInput<T>> validationStatusHandler(
			ValidationStatusHandler validationStatusHandler) {
		ObjectUtils.argumentNotNull(validationStatusHandler, "ValidationStatusHandler must be not null");
		instance.setValidationStatusHandler(validationStatusHandler);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValidatableInput.Builder#validateOnValueChange(boolean)
	 */
	@Override
	public ValidatableInputBuilder<T, ValidatableInput<T>> validateOnValueChange(boolean validateOnValueChange) {
		instance.setValidateOnValueChange(validateOnValueChange);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValidatableInput.Builder#required()
	 */
	@Override
	public ValidatableInputBuilder<T, ValidatableInput<T>> required() {
		this.required = true;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValidatableInput.Builder#required(com.holonplatform.core.Validator)
	 */
	@Override
	public ValidatableInputBuilder<T, ValidatableInput<T>> required(Validator<T> validator) {
		this.required = true;
		this.requiredValidator = validator;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValidatableInput.Builder#required(com.holonplatform.core.i18n.
	 * Localizable)
	 */
	@Override
	public ValidatableInputBuilder<T, ValidatableInput<T>> required(Localizable message) {
		this.required = true;
		this.requiredMessage = message;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValidatableInput.Builder#build()
	 */
	@Override
	public ValidatableInput<T> build() {
		// check required
		if (required) {
			input.setRequired(true);
			// add required validator
			instance.addValidator(getRequiredValidator().orElse(new RequiredInputValidator<>(input,
					getRequiredMessage().orElse(RequiredInputValidator.DEFAULT_REQUIRED_ERROR))));
		}
		return instance;
	}

}
