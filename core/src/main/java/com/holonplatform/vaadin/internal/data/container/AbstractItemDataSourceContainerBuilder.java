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
package com.holonplatform.vaadin.internal.data.container;

import com.holonplatform.core.query.QueryConfigurationProvider;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.core.query.QuerySort;
import com.holonplatform.vaadin.data.ItemDataProvider;
import com.holonplatform.vaadin.data.ItemIdentifierProvider;
import com.holonplatform.vaadin.data.ItemDataSource.CommitHandler;
import com.holonplatform.vaadin.data.ItemDataSource.PropertySortGenerator;
import com.holonplatform.vaadin.data.container.ItemDataSourceContainer;
import com.holonplatform.vaadin.data.container.ItemDataSourceContainerBuilder;
import com.vaadin.data.provider.DataProviderListener;

/**
 * Base {@link ItemDataSourceContainerBuilder} implementation.
 * 
 * @param <ITEM> Item type
 * @param <PROPERTY> Item property type
 * @param <B> Concrete builder type
 * 
 * @since 5.0.0
 */
public abstract class AbstractItemDataSourceContainerBuilder<ITEM, PROPERTY, B extends ItemDataSourceContainerBuilder<ITEM, PROPERTY, B>>
		implements ItemDataSourceContainerBuilder<ITEM, PROPERTY, B> {

	/**
	 * Container instance to build and setup
	 */
	protected final DefaultItemDataSourceContainer<ITEM, PROPERTY> container = new DefaultItemDataSourceContainer<>();

	/**
	 * Container batch size
	 */
	protected int batchSize = ItemDataSourceContainer.DEFAULT_BATCH_SIZE;
	
	/**
	 * Actual builder
	 * @return Builder
	 */
	protected abstract B builder();

	/**
	 * Constructor
	 */
	public AbstractItemDataSourceContainerBuilder() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.data.container.ItemDataSourceContainerBuilder#dataSource(com.holonplatform.vaadin.data
	 * .ItemDataProvider)
	 */
	@Override
	public B dataSource(ItemDataProvider<ITEM> dataProvider) {
		container.setDataProvider(dataProvider);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.data.container.ItemDataSourceContainerBuilder#itemIdentifier(com.holonplatform.vaadin.
	 * data.ItemIdentifierProvider)
	 */
	@Override
	public <ID> B itemIdentifier(ItemIdentifierProvider<ITEM, ID> itemIdentifierProvider) {
		container.setItemIdentifierProvider(itemIdentifierProvider);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.QueryContainerBuilder#autoRefresh(boolean)
	 */
	@Override
	public B autoRefresh(boolean autoRefresh) {
		container.setAutoRefresh(autoRefresh);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.QueryContainerBuilder#batchSize(int)
	 */
	@Override
	public B batchSize(int batchSize) {
		this.batchSize = batchSize;
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.QueryContainerBuilder#maxCacheSize(int)
	 */
	@Override
	public B maxCacheSize(int maxCacheSize) {
		container.setMaxCacheSize(maxCacheSize);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.QueryContainerBuilder#defaultValue(java.lang.Object, java.lang.Object)
	 */
	@Override
	public B defaultValue(PROPERTY propertyId, Object defaultValue) {
		container.setPropertyDefaultValue(propertyId, defaultValue);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.container.ItemDataSourceContainerBuilder#withProperty(java.lang.Object,
	 * java.lang.Class)
	 */
	@Override
	public B withProperty(PROPERTY propertyId, Class<?> type) {
		container.addContainerProperty(propertyId, type, false, false);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.data.container.ItemDataSourceContainerBuilder#withSortableProperty(java.lang.Object,
	 * java.lang.Class)
	 */
	@Override
	public B withSortableProperty(PROPERTY propertyId, Class<?> type) {
		container.addContainerProperty(propertyId, type, false, true);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.data.container.ItemDataSourceContainerBuilder#withReadOnlyProperty(java.lang.Object,
	 * java.lang.Class)
	 */
	@Override
	public B withReadOnlyProperty(PROPERTY propertyId, Class<?> type) {
		container.addContainerProperty(propertyId, type, true, false);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.data.container.ItemDataSourceContainerBuilder#withReadOnlySortableProperty(java.lang.
	 * Object, java.lang.Class)
	 */
	@Override
	public B withReadOnlySortableProperty(PROPERTY propertyId, Class<?> type) {
		container.addContainerProperty(propertyId, type, true, true);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.container.ItemDataSourceContainerBuilder#sortable(java.lang.Object, boolean)
	 */
	@Override
	public B sortable(PROPERTY propertyId, boolean sortable) {
		container.setPropertySortable(propertyId, sortable);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.container.ItemDataSourceContainerBuilder#readOnly(java.lang.Object, boolean)
	 */
	@Override
	public B readOnly(PROPERTY propertyId, boolean readOnly) {
		container.setPropertyReadOnly(propertyId, readOnly);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.container.ItemDataSourceContainerBuilder#withPropertySortGenerator(java.lang.
	 * Object, com.holonplatform.vaadin.data.ItemDataSource.PropertySortGenerator)
	 */
	@Override
	public B withPropertySortGenerator(PROPERTY property, PropertySortGenerator<PROPERTY> propertySortGenerator) {
		container.setPropertySortGenerator(property, propertySortGenerator);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.container.ItemDataSourceContainerBuilder#withQueryConfigurationProvider(com.
	 * holonframework.core.query.QueryConfigurationProvider)
	 */
	@Override
	public B withQueryConfigurationProvider(QueryConfigurationProvider queryConfigurationProvider) {
		container.addQueryConfigurationProvider(queryConfigurationProvider);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.data.container.ItemDataSourceContainerBuilder#fixedFilter(com.holonplatform.core.query
	 * .QueryFilter)
	 */
	@Override
	public B fixedFilter(QueryFilter filter) {
		container.setFixedFilter(filter);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.data.container.ItemDataSourceContainerBuilder#fixedSort(com.holonplatform.core.query.
	 * QuerySort)
	 */
	@Override
	public B fixedSort(QuerySort sort) {
		container.setFixedSort(sort);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.data.container.ItemDataSourceContainerBuilder#defaultSort(com.holonplatform.core.query
	 * .QuerySort)
	 */
	@Override
	public B defaultSort(QuerySort sort) {
		container.setDefaultSort(sort);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.container.ItemDataSourceContainerBuilder#queryParameter(java.lang.String,
	 * java.lang.Object)
	 */
	@Override
	public B queryParameter(String name, Object value) {
		container.addQueryParameter(name, value);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.data.container.ItemDataSourceContainerBuilder#commitHandler(com.holonplatform.vaadin.
	 * data.ItemDataSource.CommitHandler)
	 */
	@Override
	public B commitHandler(CommitHandler<ITEM> commitHandler) {
		container.setCommitHandler(commitHandler);
		return builder();
	}

	/* (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.container.ItemDataSourceContainerBuilder#withDataProviderListener(com.vaadin.data.provider.DataProviderListener)
	 */
	@Override
	public B withDataProviderListener(DataProviderListener<ITEM> listener) {
		// TODO
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.QueryContainerBuilder#build()
	 */
	@Override
	public ItemDataSourceContainer<ITEM, PROPERTY> build() {
		// init container
		container.init(batchSize);
		return container;
	}

}
