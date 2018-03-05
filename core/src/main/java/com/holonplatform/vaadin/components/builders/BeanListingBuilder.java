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

import com.holonplatform.vaadin.components.BeanListing;
import com.holonplatform.vaadin.components.builders.ItemListingBuilder.GridItemListingBuilder;
import com.vaadin.data.ValueProvider;

/**
 * A builder to create {@link BeanListing} instances.
 *
 * @param <T> Bean type
 *
 * @since 5.1.0
 */
public interface BeanListingBuilder<T> extends GridItemListingBuilder<T, BeanListing<T>, BeanListingBuilder<T>> {

	/**
	 * Add a <em>virtual</em> column to the listing, i.e. a column which is not directly bound to a bean property.
	 * <p>
	 * The column id can be later used to set the column position providing the visibile columns order through the final
	 * builder method {@link #build(Object...)}.
	 * </p>
	 * @param <V> Column value type
	 * @param id Column id (not null)
	 * @param type Column value type (not null)
	 * @param valueProvider Column value provider (not null)
	 * @return this
	 */
	<V> BeanListingBuilder<T> withVirtualColumn(String id, Class<V> type, ValueProvider<T, V> valueProvider);

}
