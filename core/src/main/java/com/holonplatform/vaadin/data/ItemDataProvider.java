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
package com.holonplatform.vaadin.data;

import java.util.function.Function;

import com.holonplatform.core.beans.BeanIntrospector;
import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.exceptions.DataAccessException;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.query.QueryConfigurationProvider;
import com.holonplatform.vaadin.internal.data.DatastoreBeanItemDataProvider;
import com.holonplatform.vaadin.internal.data.DatastoreItemDataProvider;
import com.holonplatform.vaadin.internal.data.DefaultItemDataProvider;
import com.holonplatform.vaadin.internal.data.ItemDataProviderWrapper;

/**
 * Iterface to load items data from a data source.
 * 
 * @param <ITEM> Item data type
 * 
 * @since 5.0.0
 */
public interface ItemDataProvider<ITEM> extends ItemSetCounter, ItemSetLoader<ITEM>, ItemRefresher<ITEM> {

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemRefresher#refresh(java.lang.Object)
	 */
	@Override
	default ITEM refresh(ITEM item) throws UnsupportedOperationException, DataAccessException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Create an {@link ItemDataProvider} using given operations.
	 * @param <ITEM> Item data type
	 * @param counter Items counter (not null)
	 * @param loader Items loader (not null)
	 * @return A new {@link ItemDataProvider} instance
	 */
	static <ITEM> ItemDataProvider<ITEM> create(ItemSetCounter counter, ItemSetLoader<ITEM> loader) {
		return new DefaultItemDataProvider<>(counter, loader);
	}

	/**
	 * Create an {@link ItemDataProvider} using given operations.
	 * @param <ITEM> Item data type
	 * @param counter Items counter (not null)
	 * @param loader Items loader (not null)
	 * @param refresher Item refresher
	 * @return A new {@link ItemDataProvider} instance
	 */
	static <ITEM> ItemDataProvider<ITEM> create(ItemSetCounter counter, ItemSetLoader<ITEM> loader,
			ItemRefresher<ITEM> refresher) {
		return new DefaultItemDataProvider<>(counter, loader, refresher);
	}

	/**
	 * Construct a {@link ItemDataProvider} using a {@link Datastore}.
	 * @param datastore Datastore to use (not null)
	 * @param target Data target (not null)
	 * @param propertySet Property set to load
	 * @return the {@link ItemDataProvider} instance
	 */
	static ItemDataProvider<PropertyBox> create(Datastore datastore, DataTarget<?> target, PropertySet<?> propertySet) {
		return new DatastoreItemDataProvider(datastore, target, propertySet);
	}

	/**
	 * Construct a {@link ItemDataProvider} using a {@link Datastore}.
	 * @param datastore Datastore to use (not null)
	 * @param target Data target (not null)
	 * @param propertySet Property set to load
	 * @param queryConfigurationProviders Optional additional {@link QueryConfigurationProvider}s
	 * @return the {@link ItemDataProvider} instance
	 */
	static ItemDataProvider<PropertyBox> create(Datastore datastore, DataTarget<?> target, PropertySet<?> propertySet,
			QueryConfigurationProvider... queryConfigurationProviders) {
		DatastoreItemDataProvider provider = new DatastoreItemDataProvider(datastore, target, propertySet);
		if (queryConfigurationProviders != null) {
			for (QueryConfigurationProvider queryConfigurationProvider : queryConfigurationProviders) {
				provider.addQueryConfigurationProvider(queryConfigurationProvider);
			}
		}
		return provider;
	}

	/**
	 * Construct a {@link ItemDataProvider} using a {@link Datastore} and given <code>beanClass</code> as item type.
	 * <p>
	 * The query projection will be configured using the bean class property names and the query results will be
	 * obtained as instances of given bean class. The default {@link BeanIntrospector} will be used to inspect bean
	 * class properties.
	 * </p>
	 * @param <T> Bean type
	 * @param datastore Datastore to use (not null)
	 * @param target Data target (not null)
	 * @param beanClass Item bean type (not null)
	 * @return the {@link ItemDataProvider} instance
	 */
	static <T> ItemDataProvider<T> create(Datastore datastore, DataTarget<?> target, Class<T> beanClass) {
		return new DatastoreBeanItemDataProvider<>(datastore, target, beanClass);
	}

	/**
	 * Construct a {@link ItemDataProvider} using a {@link Datastore} and given <code>beanClass</code> as item type.
	 * <p>
	 * The query projection will be configured using the bean class property names and the query results will be
	 * obtained as instances of given bean class. The default {@link BeanIntrospector} will be used to inspect bean
	 * class properties.
	 * </p>
	 * @param <T> Bean type
	 * @param datastore Datastore to use (not null)
	 * @param target Data target (not null)
	 * @param beanClass Item bean type (not null)
	 * @param queryConfigurationProviders Optional additional {@link QueryConfigurationProvider}s
	 * @return the {@link ItemDataProvider} instance
	 */
	static <T> ItemDataProvider<T> create(Datastore datastore, DataTarget<?> target, Class<T> beanClass,
			QueryConfigurationProvider... queryConfigurationProviders) {
		DatastoreBeanItemDataProvider<T> provider = new DatastoreBeanItemDataProvider<>(datastore, target, beanClass);
		if (queryConfigurationProviders != null) {
			for (QueryConfigurationProvider queryConfigurationProvider : queryConfigurationProviders) {
				provider.addQueryConfigurationProvider(queryConfigurationProvider);
			}
		}
		return provider;
	}

	/**
	 * Create a new {@link ItemDataProvider} which wraps a concrete data provider and converts items into a different
	 * type using a converter function.
	 * @param <ITEM> Item type
	 * @param <T> Converted type
	 * @param provider Concrete data privider (not null)
	 * @param converter Converter function (not null)
	 * @return the data provider wrapper
	 * @return the {@link ItemDataProvider} instance
	 */
	static <T, ITEM> ItemDataProvider<T> convert(ItemDataProvider<ITEM> provider, Function<ITEM, T> converter) {
		return new ItemDataProviderWrapper<>(provider, converter);
	}

}
