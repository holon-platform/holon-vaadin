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
package com.holonplatform.vaadin.internal.data;

import java.util.stream.Stream;

import com.holonplatform.core.exceptions.DataAccessException;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.query.QueryConfigurationProvider;
import com.holonplatform.vaadin.data.ItemDataProvider;
import com.holonplatform.vaadin.data.ItemRefresher;
import com.holonplatform.vaadin.data.ItemSetCounter;
import com.holonplatform.vaadin.data.ItemSetLoader;

/**
 * Default {@link ItemDataProvider} implementation.
 * 
 * @param <ITEM> Item data type
 *
 * @since 5.0.0
 */
public class DefaultItemDataProvider<ITEM> implements ItemDataProvider<ITEM> {

	private static final long serialVersionUID = -4073530931433433105L;

	private final ItemSetCounter counter;
	private final ItemSetLoader<ITEM> loader;
	private final ItemRefresher<ITEM> refresher;

	/**
	 * Constructor
	 * @param counter Item set counter (not null)
	 * @param loader Item set loader (not null)
	 */
	public DefaultItemDataProvider(ItemSetCounter counter, ItemSetLoader<ITEM> loader) {
		this(counter, loader, null);
	}

	/**
	 * Constructor
	 * @param counter Item set counter (not null)
	 * @param loader Item set loader (not null)
	 * @param refresher Item refresher (optional)
	 */
	public DefaultItemDataProvider(ItemSetCounter counter, ItemSetLoader<ITEM> loader, ItemRefresher<ITEM> refresher) {
		super();
		ObjectUtils.argumentNotNull(counter, "ItemSetCounter must be not null");
		ObjectUtils.argumentNotNull(loader, "ItemSetLoader must be not null");
		this.counter = counter;
		this.loader = loader;
		this.refresher = refresher;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemSetCounter#size(com.holonplatform.core.query.QueryConfigurationProvider)
	 */
	@Override
	public long size(QueryConfigurationProvider configuration) throws DataAccessException {
		return counter.size(configuration);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemSetLoader#load(com.holonplatform.core.query.QueryConfigurationProvider,
	 * int, int)
	 */
	@Override
	public Stream<ITEM> load(QueryConfigurationProvider configuration, int offset, int limit)
			throws DataAccessException {
		return loader.load(configuration, offset, limit);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemDataProvider#refresh(java.lang.Object)
	 */
	@Override
	public ITEM refresh(ITEM item) throws UnsupportedOperationException, DataAccessException {
		if (refresher == null) {
			throw new UnsupportedOperationException();
		}
		return refresher.refresh(item);
	}

}
