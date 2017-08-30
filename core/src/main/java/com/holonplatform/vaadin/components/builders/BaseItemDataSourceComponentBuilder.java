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

import com.holonplatform.core.query.QueryConfigurationProvider;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.core.query.QuerySort;
import com.holonplatform.vaadin.data.ItemDataProvider;
import com.holonplatform.vaadin.data.ItemDataSource;

/**
 * Base builder for components with {@link ItemDataSource} support.
 * 
 * @param <B> Concrete builder type
 *
 * @since 5.0.0
 */
public interface BaseItemDataSourceComponentBuilder<B extends BaseItemDataSourceComponentBuilder<B>>
		extends ComponentConfigurator<B> {

	/**
	 * Set if auto-refresh is enabled for this container, i.e. items are loaded when one of the Container method which
	 * involve operations on item set is called.
	 * <p>
	 * If auto-refresh is not enabled, {@link ItemDataSource#refresh()} method must be called to load items before using
	 * this Container.
	 * </p>
	 * @param autoRefresh <code>true</code> to enable auto-refresh
	 * @return this
	 */
	B autoRefresh(boolean autoRefresh);

	/**
	 * Set batch (page) size for items loading using {@link ItemDataProvider}.
	 * <p>
	 * A value <code>&lt;=0</code> means no paging, and {@link ItemDataProvider} should behave in a consistent manner.
	 * </p>
	 * @param batchSize Batch (page) size for items loading
	 * @return this
	 */
	B batchSize(int batchSize);

	/**
	 * Set max items cache size
	 * @param maxCacheSize Max cache size to set
	 * @return this
	 */
	B maxCacheSize(int maxCacheSize);

	/**
	 * Add an external {@link QueryConfigurationProvider} for additional query configuration
	 * @param queryConfigurationProvider QueryConfigurationProvider to add
	 * @return this
	 */
	B withQueryConfigurationProvider(QueryConfigurationProvider queryConfigurationProvider);

	/**
	 * Set query fixed filter (always added to query predicates)
	 * @param filter Query fixed filter, or <code>null</code> for none
	 * @return this
	 */
	B fixedFilter(QueryFilter filter);

	/**
	 * Set query fixed sort (always added to query sorts)
	 * @param sort Query fixed sort, or <code>null</code> for none
	 * @return this
	 */
	B fixedSort(QuerySort sort);

	/**
	 * Set query default sort. If not <code>null</code> and no other sort is available, this one will be used
	 * @param sort Default query sort
	 * @return this
	 */
	B defaultSort(QuerySort sort);

	/**
	 * Add a query parameter
	 * @param name Parameter name
	 * @param value Parameter value
	 * @return this
	 */
	B queryParameter(String name, Object value);

}
