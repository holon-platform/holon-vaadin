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

import com.holonplatform.core.Validator.ValidationException;
import com.holonplatform.core.i18n.Localizable;

/**
 * Interface to provide value validation support for a component, using the current component value as the value to
 * validate.
 * 
 * @since 5.0.0
 */
public interface Validatable {

	/**
	 * Checks the validity of the current component value against every registered validator, if any. If the value is
	 * not valid, an {@link ValidationException} is thrown.
	 * <p>
	 * The {@link ValidationException} is {@link Localizable}, providing optional message code and arguments for
	 * validation message localization.
	 * </p>
	 * @throws ValidationException If the value is not valid
	 */
	void validate() throws ValidationException;

	/**
	 * Check if the current value is valid, swallowing any validation exception.
	 * @return <code>true</code> if the current value is valid, <code>false</code> otherwise
	 * @see #validate()
	 */
	default boolean isValid() {
		try {
			validate();
		} catch (@SuppressWarnings("unused") ValidationException e) {
			return false;
		}
		return true;
	}

}
