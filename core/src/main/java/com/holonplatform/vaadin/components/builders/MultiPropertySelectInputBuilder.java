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

import java.util.Set;

import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.vaadin.components.Input;
import com.holonplatform.vaadin.components.MultiSelect;

/**
 * Builder to create a multiple selection {@link Input} with {@link Property} data source support.
 * 
 * @param <T> Value type
 * @param <B> Actual builder type
 * 
 * @since 5.0.0
 */
public interface MultiPropertySelectInputBuilder<T, B extends MultiPropertySelectInputBuilder<T, B>>
		extends SelectInputBuilder.MultiSelectConfigurator<T, PropertyBox, B>,
		PropertySelectInputBuilder<Set<T>, MultiSelect<T>, T, B> {

	public interface GenericMultiPropertySelectInputBuilder<T>
			extends MultiPropertySelectInputBuilder<T, GenericMultiPropertySelectInputBuilder<T>> {

	}

}
