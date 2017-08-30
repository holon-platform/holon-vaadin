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
import com.holonplatform.vaadin.components.Input;
import com.holonplatform.vaadin.components.ValidationStatusHandler;

/**
 * @author BODSI08
 *
 */
public interface ValidatableInputConfigurator<V> {

	/**
	 * Adds a {@link Validator} to validate the input value.
	 * @param validator The validator to add (not null)
	 */
	void addValidator(Validator<V> validator);

	/**
	 * Sets whether to validate the value, using registered {@link Validator}s, every time the {@link Input} value
	 * changes.
	 * @param validateOnValueChange <code>true</code> to perform value validation every time the {@link Input} value
	 *        changes, <code>false</code> if not
	 */
	void setValidateOnValueChange(boolean validateOnValueChange);

	/**
	 * Set the {@link ValidationStatusHandler} to use to track validation status changes.
	 * @param validationStatusHandler the {@link ValidationStatusHandler} to set
	 */
	void setValidationStatusHandler(ValidationStatusHandler validationStatusHandler);

}
