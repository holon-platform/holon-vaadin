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
package com.holonplatform.vaadin.components.builders;

import com.vaadin.server.SerializablePredicate;

/**
 * A {@link MultiSelectInputBuilder} for {@link RenderingMode#NATIVE_SELECT} and {@link RenderingMode#SELECT}.
 * 
 * @param <T> Value type
 * @param <B> Actual builder type
 * 
 * @since 5.0.0
 */
public interface BaseSelectModeMultiSelectInputBuilder<T, B extends BaseSelectModeMultiSelectInputBuilder<T, B>>
		extends MultiSelectInputBuilder<T, B> {

	/**
	 * A {@link MultiSelectInputBuilder} for {@link RenderingMode#OPTIONS}.
	 * 
	 * @param <T> Value type
	 */
	public interface OptionsModeMultiSelectInputBuilder<T>
			extends BaseSelectModeMultiSelectInputBuilder<T, OptionsModeMultiSelectInputBuilder<T>> {

		/**
		 * Sets whether html is allowed in the item captions.
		 * @param htmlContentAllowed true if the captions are used as html, false if used as plain text
		 * @return this
		 */
		OptionsModeMultiSelectInputBuilder<T> htmlContentAllowed(boolean htmlContentAllowed);

		/**
		 * Sets the item enabled predicate. The predicate is applied to each item to determine whether the item should
		 * be enabled or disabled.
		 * @param itemEnabledProvider the item enabled provider to set (not null)
		 * @return this
		 */
		OptionsModeMultiSelectInputBuilder<T> itemEnabledProvider(SerializablePredicate<T> itemEnabledProvider);

	}

	/**
	 * A {@link MultiSelectInputBuilder} for {@link RenderingMode#SELECT}.
	 * 
	 * @param <T> Value type
	 */
	public interface SelectModeMultiSelectInputBuilder<T>
			extends BaseSelectModeMultiSelectInputBuilder<T, SelectModeMultiSelectInputBuilder<T>> {

		/**
		 * Sets the number of rows in the select.
		 * @param rows the number of rows to set
		 * @return this
		 */
		SelectModeMultiSelectInputBuilder<T> rows(int rows);

	}

}
