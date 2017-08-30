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
package com.holonplatform.vaadin.data.container;

import com.holonplatform.core.query.QueryConfigurationProvider;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.core.query.QuerySort;
import com.holonplatform.vaadin.data.ItemDataProvider;
import com.holonplatform.vaadin.data.ItemDataSource;
import com.holonplatform.vaadin.data.ItemDataSource.CommitHandler;
import com.holonplatform.vaadin.data.ItemDataSource.PropertySortGenerator;
import com.holonplatform.vaadin.data.ItemIdentifierProvider;
import com.vaadin.data.provider.DataProviderListener;

/**
 * Builder to create {@link ItemDataSourceContainer} instances.
 * 
 * @param <PROPERTY> Item property type
 * @param <ITEM> Item type
 * 
 * @since 5.0.0
 */
public interface ItemDataSourceContainerBuilder<ITEM, PROPERTY, B extends ItemDataSourceContainerBuilder<ITEM, PROPERTY, B>> {

	/**
	 * Set the items data provider.
	 * @param dataProvider The items data provider to set
	 * @return this
	 */
	B dataSource(ItemDataProvider<ITEM> dataProvider);

	/**
	 * Set the item identifier provider to use to obtain item ids.
	 * @param <ID> Item id type
	 * @param itemIdentifierProvider the item identifier provider to set
	 * @return this
	 */
	<ID> B itemIdentifier(ItemIdentifierProvider<ITEM, ID> itemIdentifierProvider);

	/**
	 * Add an Item property to this container
	 * @param propertyId Property id
	 * @param type Property value type
	 * @return this
	 */
	B withProperty(PROPERTY propertyId, Class<?> type);

	/**
	 * Add an Item property to this container and declares it as sortable
	 * @param propertyId Property id
	 * @param type Property value type
	 * @return this
	 */
	B withSortableProperty(PROPERTY propertyId, Class<?> type);

	/**
	 * Add an Item property to this container and declares it as read-only
	 * @param propertyId Property id
	 * @param type Property value type
	 * @return this
	 */
	B withReadOnlyProperty(PROPERTY propertyId, Class<?> type);

	/**
	 * Add an Item property to this container and declares it as read-only and sortable
	 * @param propertyId Property id
	 * @param type Property value type
	 * @return this
	 */
	B withReadOnlySortableProperty(PROPERTY propertyId, Class<?> type);

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
	 * Set whether given property id is sortable.
	 * @param propertyId Property id
	 * @param sortable Whether given property id is sortable
	 * @return this
	 */
	B sortable(PROPERTY propertyId, boolean sortable);

	/**
	 * Set whether given property id is read-only.
	 * @param propertyId Property id
	 * @param readOnly Whether given property id is read-only
	 * @return this
	 */
	B readOnly(PROPERTY propertyId, boolean readOnly);

	/**
	 * Set a default value to initialize the given <code>propertyId</code>
	 * @param propertyId Property id
	 * @param defaultValue Default value
	 * @return this
	 */
	B defaultValue(PROPERTY propertyId, Object defaultValue);

	/**
	 * Set a {@link PropertySortGenerator} to generate {@link QuerySort}s for given <code>property</code>
	 * @param property Property (not null)
	 * @param propertySortGenerator PropertySortGenerator (not null)
	 * @return this
	 */
	B withPropertySortGenerator(PROPERTY property, PropertySortGenerator<PROPERTY> propertySortGenerator);

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

	/**
	 * Set the handler to manage item set modifications.
	 * <p>
	 * This is required to activate item set modification support.
	 * </p>
	 * @param commitHandler Item commit handler
	 * @return this
	 */
	B commitHandler(CommitHandler<ITEM> commitHandler);

	/**
	 * Add an {@link DataProviderListener}
	 * @param listener Listener to add
	 * @return this
	 */
	B withDataProviderListener(DataProviderListener<ITEM> listener);

	/**
	 * Build {@link ItemDataSourceContainer} instance
	 * @return ItemDataSourceContainer instance
	 */
	ItemDataSourceContainer<ITEM, PROPERTY> build();

	/**
	 * Default {@link ItemDataSourceContainerBuilder}.
	 * @param <ITEM> Item type
	 * @param <PROPERTY> Item property type
	 */
	public interface BaseItemDataSourceContainerBuilder<ITEM, PROPERTY>
			extends ItemDataSourceContainerBuilder<ITEM, PROPERTY, BaseItemDataSourceContainerBuilder<ITEM, PROPERTY>> {

		/**
		 * Get the current data source properties.
		 * @return data source properties
		 */
		Iterable<PROPERTY> getProperties();

	}

}
