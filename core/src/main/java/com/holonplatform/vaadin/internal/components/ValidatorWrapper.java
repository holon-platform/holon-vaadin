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

import com.holonplatform.core.Validator;
import com.holonplatform.core.Validator.ValidationException;
import com.holonplatform.core.i18n.LocalizationContext;
import com.vaadin.data.ValidationResult;
import com.vaadin.data.ValueContext;

/**
 * {@link Validator} wrapper to be used as vaddin {@link com.vaadin.data.Validator}.
 * 
 * @param <T> Value type
 * 
 * @since 5.0.0
 */
public class ValidatorWrapper<T> implements com.vaadin.data.Validator<T> {

	private static final long serialVersionUID = -2684870439086075458L;

	private final Validator<T> validator;

	public ValidatorWrapper(Validator<T> validator) {
		super();
		this.validator = validator;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Validator#apply(java.lang.Object, com.vaadin.data.ValueContext)
	 */
	@Override
	public ValidationResult apply(T value, ValueContext context) {
		try {
			validator.validate(value);
		} catch (ValidationException e) {
			return ValidationResult.error(e.getValidationMessages().isEmpty() ? e.getLocalizedMessage()
					: LocalizationContext.translate(e.getValidationMessages().get(0), true));
		}
		return ValidationResult.ok();
	}

}
