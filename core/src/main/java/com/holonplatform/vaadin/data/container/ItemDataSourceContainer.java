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
import com.holonplatform.vaadin.data.container.ItemDataSourceContainerBuilder.BaseItemDataSourceContainerBuilder;
import com.holonplatform.vaadin.internal.data.container.DefaultItemDataSourceContainerBuilder;
import com.vaadin.shared.Registration;

/**
 * An {@link ItemDataSource} using an {@link ItemDataProvider} to load items on demand.
 * 
 * @param <PROPERTY> Item property type
 * @param <ITEM> Item type
 * 
 * @since 3.5.0
 */
public interface ItemDataSourceContainer<ITEM, PROPERTY> extends ItemDataSource<ITEM, PROPERTY> {

	/**
	 * Default batch (page) size for items loading using {@link ItemDataProvider}
	 */
	public static final int DEFAULT_BATCH_SIZE = 20;

	/**
	 * Set if auto-refresh is enabled for this container, i.e. items are loaded when one of the Container method which
	 * involve operations on item set is called.
	 * <p>
	 * If auto-refresh is not enabled, {@link #refresh()} method must be called to load items before using this
	 * Container.
	 * </p>
	 * @param autoRefresh <code>true</code> to enable auto-refresh
	 */
	void setAutoRefresh(boolean autoRefresh);

	/**
	 * Set max items cache size
	 * @param maxCacheSize Max cache size to set
	 */
	void setMaxCacheSize(int maxCacheSize);

	/**
	 * Adds an Item property to this container
	 * @param <T> Property type
	 * @param propertyId Property id
	 * @param type Property value type
	 * @param readOnly Whether property is read-only
	 * @param sortable Whether property is sortable
	 * @param defaultValue Property default value
	 * @return <code>true</code> if property was successfully added
	 */
	<T> boolean addContainerProperty(PROPERTY propertyId, Class<T> type, boolean readOnly, boolean sortable,
			T defaultValue);

	/**
	 * Adds an Item property to this container
	 * @param propertyId Property id
	 * @param type Property value type
	 * @param readOnly Whether property is read-only
	 * @param sortable Whether property is sortable
	 * @return <code>true</code> if property was successfully added
	 */
	default boolean addContainerProperty(PROPERTY propertyId, Class<?> type, boolean readOnly, boolean sortable) {
		return addContainerProperty(propertyId, type, readOnly, sortable, null);
	}

	/**
	 * Set whether the given <code>propertyId</code> can partecipate in container sorting
	 * @param propertyId Property id
	 * @param sortable Whether the property is sortable or not
	 */
	void setPropertySortable(PROPERTY propertyId, boolean sortable);

	/**
	 * Set whether the given <code>propertyId</code> is read-only
	 * @param propertyId Property id
	 * @param readOnly Whether the property is read-only
	 */
	void setPropertyReadOnly(PROPERTY propertyId, boolean readOnly);

	/**
	 * Set a default value to initialize the given <code>propertyId</code>
	 * @param propertyId Property id
	 * @param defaultValue Property default value
	 */
	void setPropertyDefaultValue(PROPERTY propertyId, Object defaultValue);

	/**
	 * Add an external {@link QueryConfigurationProvider} for additional query configuration
	 * @param queryConfigurationProvider QueryConfigurationProvider to add
	 * @return the configuration provider {@link Registration}
	 */
	Registration addQueryConfigurationProvider(QueryConfigurationProvider queryConfigurationProvider);

	/**
	 * Set query fixed filter (always added to query predicates)
	 * @param filter Query fixed filter, or <code>null</code> for none
	 */
	void setFixedFilter(QueryFilter filter);

	/**
	 * Set query default sort. If not <code>null</code> and no other sort is available, this one will be used
	 * @param sort Default query sort
	 */
	void setDefaultSort(QuerySort sort);

	/**
	 * Set query fixed sort (always added to query sorts)
	 * @param sort Query fixed sort, or <code>null</code> for none
	 */
	void setFixedSort(QuerySort sort);

	/**
	 * Add a query parameter
	 * @param name Parameter name
	 * @param value Parameter value
	 */
	void addQueryParameter(String name, Object value);

	/**
	 * Remove a query parameter
	 * @param name Parameter name to remove
	 */
	void removeQueryParameter(String name);

	/**
	 * Set a {@link PropertySortGenerator} to generate {@link QuerySort}s for given <code>property</code>
	 * @param property Property (not null)
	 * @param propertySortGenerator PropertySortGenerator (not null)
	 */
	void setPropertySortGenerator(PROPERTY property, PropertySortGenerator<PROPERTY> propertySortGenerator);

	/**
	 * Handler to manage items and item set modifications.
	 * @param commitHandler Item set commit handler
	 */
	void setCommitHandler(CommitHandler<ITEM> commitHandler);

	// Builders

	/**
	 * Get a builder to create an {@link ItemDataSourceContainer}.
	 * @param <PROPERTY> Item property type
	 * @param <ITEM> Item type
	 * @return {@link ItemDataSourceContainer} builder
	 */
	static <PROPERTY, ITEM> BaseItemDataSourceContainerBuilder<PROPERTY, ITEM> builder() {
		return new DefaultItemDataSourceContainerBuilder<>();
	}

}
