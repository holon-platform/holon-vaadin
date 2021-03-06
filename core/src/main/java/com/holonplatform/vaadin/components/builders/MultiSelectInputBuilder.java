/*
 * Copyright 2016-2017 Axioma srl.
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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.holonplatform.vaadin.components.Input;
import com.holonplatform.vaadin.components.MultiSelect;

/**
 * Builder to create a multi selection {@link Input}s.
 * 
 * @param <T> Selection type
 * @param <B> Actual builder type
 * 
 * @since 5.0.0
 */
public interface MultiSelectInputBuilder<T, B extends MultiSelectInputBuilder<T, B>>
		extends SelectInputBuilder.MultiSelectConfigurator<T, T, B>,
		SelectItemDataSourceBuilder<Set<T>, MultiSelect<T>, T, T, B> {

	/**
	 * Sets the initial values for the field.
	 * @param values The values to set
	 * @return this
	 */
	@SuppressWarnings("unchecked")
	default B withValue(T... values) {
		return withValue(
				(values == null || values.length == 0) ? Collections.emptySet() : new HashSet<>(Arrays.asList(values)));
	}

	public interface GenericMultiSelectInputBuilder<T>
			extends MultiSelectInputBuilder<T, GenericMultiSelectInputBuilder<T>> {

	}

}
