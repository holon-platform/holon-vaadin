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

import java.util.Objects;

import com.holonplatform.core.Validator;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.components.ValueHolder;

/**
 * A {@link Validator} to check if a {@link ValueHolder} is not empty, i.e. the current value is not equal to the empty
 * value representation.
 * 
 * @param <T> Value type
 * 
 * @since 5.0.0
 */
public class RequiredInputValidator<T> implements Validator<T> {

	private static final long serialVersionUID = -3267748749995125176L;

	/**
	 * Default validation error message for required fields.
	 */
	public static final Localizable DEFAULT_REQUIRED_ERROR = Localizable.builder().message("Value is required")
			.messageCode(com.holonplatform.core.Validator.DEFAULT_MESSAGE_CODE_PREFIX + "required").build();

	private final ValueHolder<T> valueHolder;
	private final Localizable message;

	/**
	 * Constructor with default error message
	 * @param valueHolder the {@link ValueHolder} to which the validation refers (not null)
	 */
	public RequiredInputValidator(ValueHolder<T> valueHolder) {
		this(valueHolder, DEFAULT_REQUIRED_ERROR);
	}

	/**
	 * Constructor
	 * @param valueHolder the {@link ValueHolder} to which the validation refers (not null)
	 * @param message Optional validation failed message. If <code>null</code>, the {@link #DEFAULT_REQUIRED_ERROR}
	 *        message will be used.
	 */
	public RequiredInputValidator(ValueHolder<T> valueHolder, Localizable message) {
		super();
		ObjectUtils.argumentNotNull(valueHolder, "ValueHolder must be not null");
		this.valueHolder = valueHolder;
		this.message = (message != null) ? message : DEFAULT_REQUIRED_ERROR;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.Validator#validate(java.lang.Object)
	 */
	@Override
	public void validate(T value) throws com.holonplatform.core.Validator.ValidationException {
		if (Objects.equals(valueHolder.getEmptyValue(), value)) {
			throw new ValidationException(message);
		}
	}

}
