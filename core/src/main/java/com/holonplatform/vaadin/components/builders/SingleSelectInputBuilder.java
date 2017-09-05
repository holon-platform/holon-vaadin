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

import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.vaadin.components.Input;
import com.holonplatform.vaadin.components.SingleSelect;
import com.holonplatform.vaadin.data.ItemDataProvider;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.server.SerializableFunction;

/**
 * Builder to create a single selection {@link Input}.
 * 
 * @param <T> Value type
 * @param <B> Actual builder type
 * 
 * @since 5.0.0
 */
public interface SingleSelectInputBuilder<T, B extends SingleSelectInputBuilder<T, B>> extends
		SelectInputBuilder.SingleSelectConfigurator<T, T, B>, SelectItemDataSourceBuilder<T, SingleSelect<T>, T, T, B> {

	/**
	 * Set the selection items data provider.
	 * @param dataProvider Items data provider (not null)
	 * @param filterProvider Optional caption {@link QueryFilter} provider for {@link RenderingMode#SELECT} type inputs
	 * @return this
	 */
	B dataSource(ItemDataProvider<T> dataProvider, SerializableFunction<String, QueryFilter> filterProvider);

	/**
	 * Set the selection items data provider.
	 * @param <F> Caption filter type
	 * @param dataProvider Items data provider (not null)
	 * @param filterProvider Optional caption {@link QueryFilter} provider for {@link RenderingMode#SELECT} type inputs
	 * @return this
	 */
	<F> B dataSource(DataProvider<T, F> dataProvider, SerializableFunction<String, F> filterProvider);

	public interface GenericSingleSelectInputBuilder<T>
			extends SingleSelectInputBuilder<T, GenericSingleSelectInputBuilder<T>> {

	}

}
