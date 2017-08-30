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
package com.holonplatform.vaadin.internal.components;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.holonplatform.core.Validator;
import com.holonplatform.core.Validator.ValidationException;
import com.holonplatform.core.beans.Validators;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.components.Input;
import com.holonplatform.vaadin.components.ValidatableInput;
import com.holonplatform.vaadin.components.ValidationStatusHandler;
import com.holonplatform.vaadin.components.ValidationStatusHandler.Status;
import com.vaadin.shared.Registration;
import com.vaadin.ui.Component;

/**
 * A {@link ValidatableInput} implementation wrapping a concrete {@link Input} instance.
 * 
 * @param <V> Value type
 *
 * @since 5.0.0
 */
public class ValidatableInputWrapper<V> implements ValidatableInput<V> {

	private static final long serialVersionUID = -2291397152828158839L;

	/**
	 * Wrapped input
	 */
	private final Input<V> input;

	/**
	 * Validators
	 */
	private final List<Validator<V>> validators = new LinkedList<>();

	/**
	 * Validation status handler
	 */
	private ValidationStatusHandler validationStatusHandler = ValidationStatusHandler.getDefault();

	/**
	 * Whether to validate the input when value changes
	 */
	private boolean validateOnValueChange = true;

	/**
	 * The validation listener registration reference
	 */
	private Registration validationListenerRegistration = null;

	/**
	 * Constructor
	 * @param input Wrapped input
	 */
	public ValidatableInputWrapper(Input<V> input) {
		super();
		ObjectUtils.argumentNotNull(input, "Input must be not null");
		this.input = input;
		// setup validation on value change by default
		setValidateOnValueChange(true);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#setReadOnly(boolean)
	 */
	@Override
	public void setReadOnly(boolean readOnly) {
		input.setReadOnly(readOnly);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#isReadOnly()
	 */
	@Override
	public boolean isReadOnly() {
		return input.isReadOnly();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#isRequired()
	 */
	@Override
	public boolean isRequired() {
		return input.isRequired();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#setRequired(boolean)
	 */
	@Override
	public void setRequired(boolean required) {
		input.setRequired(required);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#focus()
	 */
	@Override
	public void focus() {
		input.focus();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValueHolder#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(V value) {
		input.setValue(value);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValueHolder#getValue()
	 */
	@Override
	public V getValue() {
		return input.getValue();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValueHolder#clear()
	 */
	@Override
	public void clear() {
		ValidatableInput.super.clear();
		// notify ValidationStatusHandler
		getValidationStatusHandler().ifPresent(vsh -> vsh
				.validationStatusChange(new DefaultValidationStatusEvent<>(Status.UNRESOLVED, null, this, null)));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValueHolder#addValueChangeListener(com.holonplatform.vaadin.components.
	 * ValueHolder.ValueChangeListener)
	 */
	@Override
	public Registration addValueChangeListener(ValueChangeListener<V> listener) {
		return input.addValueChangeListener(listener);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValueComponent#getComponent()
	 */
	@Override
	public Component getComponent() {
		return input.getComponent();
	}

	/**
	 * Get all registered {@link Validator}s.
	 * @return the registered validators, an empty {@link List} if none
	 */
	protected List<Validator<V>> getValidators() {
		return validators;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Validatable#validate()
	 */
	@Override
	public void validate() throws ValidationException {
		validate(getValue());
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValidatableInput#addValidator(com.holonplatform.core.Validator)
	 */
	@Override
	public Registration addValidator(Validator<V> validator) {
		ObjectUtils.argumentNotNull(validator, "Validator must be not null");
		validators.add(validator);
		return () -> validators.remove(validator);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValidatableInput#setValidateOnValueChange(boolean)
	 */
	@Override
	public void setValidateOnValueChange(boolean validateOnValueChange) {
		this.validateOnValueChange = validateOnValueChange;
		if (validateOnValueChange) {
			if (validationListenerRegistration == null) {
				this.validationListenerRegistration = this.input.addValueChangeListener(e -> {
					try {
						validate(e.getValue());
					} catch (@SuppressWarnings("unused") ValidationException ve) {
						// swallow
					}
				});
			}
		} else {
			if (validationListenerRegistration != null) {
				validationListenerRegistration.remove();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValidatableInput#isValidateOnValueChange()
	 */
	@Override
	public boolean isValidateOnValueChange() {
		return validateOnValueChange;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValidatableInput#setValidationStatusHandler(com.holonplatform.vaadin.
	 * components.ValidationStatusHandler)
	 */
	@Override
	public void setValidationStatusHandler(ValidationStatusHandler validationStatusHandler) {
		this.validationStatusHandler = validationStatusHandler;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValidatableInput#getValidationStatusHandler()
	 */
	@Override
	public Optional<ValidationStatusHandler> getValidationStatusHandler() {
		return Optional.ofNullable(validationStatusHandler);
	}

	/**
	 * Validate given value using registered {@link Validators}.
	 * @param value Value to validate
	 * @throws ValidationException If the value is not valid
	 */
	protected void validate(V value) throws ValidationException {

		LinkedList<ValidationException> failures = new LinkedList<>();
		for (Validator<V> validator : getValidators()) {
			try {
				validator.validate(value);
			} catch (ValidationException ve) {
				failures.add(ve);
			}
		}

		// collect validation exceptions, if any
		if (!failures.isEmpty()) {

			ValidationException validationException = (failures.size() == 1) ? failures.getFirst()
					: new ValidationException(failures.toArray(new ValidationException[0]));

			// notify ValidationStatusHandler
			getValidationStatusHandler()
					.ifPresent(vsh -> vsh.validationStatusChange(new DefaultValidationStatusEvent<>(Status.INVALID,
							validationException.getValidationMessages(), this, null)));

			throw validationException;
		}

		// VALID: notify ValidationStatusHandler
		getValidationStatusHandler().ifPresent(
				vsh -> vsh.validationStatusChange(new DefaultValidationStatusEvent<>(Status.VALID, null, this, null)));
	}

}
