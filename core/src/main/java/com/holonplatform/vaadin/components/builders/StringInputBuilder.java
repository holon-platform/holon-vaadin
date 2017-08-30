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

import com.holonplatform.vaadin.components.Input;

/**
 * Builder to create {@link String} type {@link Input} instances backed by a text input widget.
 * 
 * @since 5.0.0
 */
public interface StringInputBuilder extends TextInputBuilder<String, Input<String>, StringInputBuilder> {

	/**
	 * If field is rendered as a text area in UI, set the number of rows of the text area
	 * @param rows the number of rows of the text area
	 * @return this
	 */
	StringInputBuilder rows(int rows);

	/**
	 * Enable or disable treating empty String values as <code>null</code> values.
	 * <p>
	 * By default this behaviour is enabled.
	 * </p>
	 * @param enable True to treat empty String values as <code>null</code> values
	 * @return this
	 */
	StringInputBuilder emptyValuesAsNull(boolean enable);

	/**
	 * Enable or disable treating blank String values (with 0 length or whitespaces only Strings) as <code>null</code>
	 * values.
	 * <p>
	 * By default this behaviour is disabled.
	 * </p>
	 * @param enable True to treat blank String values (with 0 length or whitespaces only Strings) as <code>null</code>
	 *        values
	 * @return this
	 */
	StringInputBuilder blankValuesAsNull(boolean enable);

}
