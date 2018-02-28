/*
 * Copyright 2000-2017 Holon TDCN.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.vaadin.data.ItemDataSource;
import com.vaadin.data.provider.AbstractBackEndDataProvider;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.Query;

/**
 * A {@link DataProvider} using an {@link ItemDataSource} as data source.
 * 
 * @since 5.0.0
 */
public class ItemDataSourceAdapter<ITEM> extends AbstractBackEndDataProvider<ITEM, QueryFilter> {

	private static final long serialVersionUID = -6247532604680268068L;

	/**
	 * Actual data source
	 */
	private final ItemDataSource<ITEM, ?> dataSource;

	/**
	 * Constructor.
	 * @param dataSource The data source (not null)
	 */
	public ItemDataSourceAdapter(ItemDataSource<ITEM, ?> dataSource) {
		super();
		ObjectUtils.argumentNotNull(dataSource, "ItemDataSource must be not null");
		this.dataSource = dataSource;
	}

	/**
	 * Get the data source.
	 * @return The item data source
	 */
	protected ItemDataSource<ITEM, ?> getDataSource() {
		return dataSource;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.provider.AbstractBackEndDataProvider#fetchFromBackEnd(com.vaadin.data.provider.Query)
	 */
	@Override
	protected Stream<ITEM> fetchFromBackEnd(Query<ITEM, QueryFilter> query) {
		getDataSource().setDataProviderQuery(query);
		if (query.getLimit() == Integer.MAX_VALUE) {
			return getAllItems().stream();
		}
		return getItemsByIndex(query.getOffset(), query.getLimit()).stream();
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.provider.AbstractBackEndDataProvider#sizeInBackEnd(com.vaadin.data.provider.Query)
	 */
	@Override
	protected int sizeInBackEnd(Query<ITEM, QueryFilter> query) {
		getDataSource().setDataProviderQuery(query);
		return getDataSource().size();
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.provider.DataProvider#getId(java.lang.Object)
	 */
	@Override
	public Object getId(ITEM item) {
		if (getDataSource().getConfiguration().getItemIdentifierProvider().isPresent()) {
			return getDataSource().getConfiguration().getItemIdentifierProvider().get().getItemId(item);
		}
		return super.getId(item);
	}

	/**
	 * Get all available items.
	 * @return Items list, empty if none
	 */
	private List<ITEM> getAllItems() {
		Collection<?> ids = getDataSource().getItemIds();
		if (ids != null && !ids.isEmpty()) {
			List<ITEM> items = new ArrayList<>(ids.size());
			for (Object id : ids) {
				items.add(getDataSource().get(id).orElseThrow(
						() -> new IllegalStateException("No item found in data source with id [" + id + "]")));
			}
			return items;
		}
		return Collections.emptyList();
	}

	/**
	 * Get <code>numberOfItems</code> items from given <code>startIndex</code>.
	 * @param startIndex Start index
	 * @param numberOfItems Number of items to obtain
	 * @return Items list
	 */
	private List<ITEM> getItemsByIndex(int startIndex, int numberOfItems) {

		final int size = getDataSource().size();

		if (startIndex < 0) {
			throw new IndexOutOfBoundsException("Start index cannot be negative! startIndex=" + startIndex);
		}

		if (startIndex > size) {
			throw new IndexOutOfBoundsException(
					"Start index exceeds dataSource size! startIndex=" + startIndex + " dataSource size=" + size);
		}

		if (numberOfItems < 1) {
			if (numberOfItems == 0) {
				return Collections.emptyList();
			}

			throw new IllegalArgumentException("Cannot get negative amount of items! numberOfItems=" + numberOfItems);
		}

		// not included in the range
		int endIndex = startIndex + numberOfItems;

		if (endIndex > size) {
			endIndex = size;
		}

		ArrayList<ITEM> rangeOfItems = new ArrayList<>();
		for (int i = startIndex; i < endIndex; i++) {
			ITEM item = getDataSource().getItemAt(i);
			if (item == null) {
				throw new RuntimeException("Unable to get item id for index: " + i + " from data source by index "
						+ "even though data source size > endIndex. " + "Returned item was null.");
			}
			rangeOfItems.add(item);
		}

		return rangeOfItems;
	}

}
