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

import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.vaadin.components.Input;
import com.holonplatform.vaadin.components.SingleSelect;
import com.vaadin.server.SerializableFunction;

/**
 * Builder to create a single selection {@link Input} with {@link Property} data source support.
 * 
 * @param <T> Value type
 * 
 * @since 5.0.0
 */
public interface SinglePropertySelectInputBuilder<T>
		extends SelectInputBuilder.SingleSelectConfigurator<T, PropertyBox, SinglePropertySelectInputBuilder<T>>,
		PropertySelectInputBuilder<T, SingleSelect<T>, T, SinglePropertySelectInputBuilder<T>> {

	/**
	 * Set the function to provide the {@link QueryFilter} to use with the data provider when user types a caption
	 * filter String.
	 * @param filterProvider caption {@link QueryFilter} provider
	 * @return this
	 */
	SinglePropertySelectInputBuilder<T> captionQueryFilter(SerializableFunction<String, QueryFilter> filterProvider);

}
