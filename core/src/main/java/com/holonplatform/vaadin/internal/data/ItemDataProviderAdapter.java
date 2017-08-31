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

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.holonplatform.core.Path;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.query.QueryConfigurationProvider;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.core.query.QuerySort;
import com.holonplatform.core.query.QuerySort.SortDirection;
import com.holonplatform.vaadin.data.ItemDataProvider;
import com.holonplatform.vaadin.data.ItemIdentifierProvider;
import com.vaadin.data.provider.AbstractBackEndDataProvider;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.Query;
import com.vaadin.data.provider.QuerySortOrder;

/**
 * A {@link DataProvider} using an {@link ItemDataProvider} as data source.
 * 
 * @since 5.0.0
 */
public class ItemDataProviderAdapter<ITEM> extends AbstractBackEndDataProvider<ITEM, QueryFilter> {

	private static final long serialVersionUID = -5011712229278252796L;

	private final ItemDataProvider<ITEM> dataProvider;

	private final ItemIdentifierProvider<ITEM, ?> itemIdentifier;

	public ItemDataProviderAdapter(ItemDataProvider<ITEM> dataProvider) {
		this(dataProvider, null);
	}

	public ItemDataProviderAdapter(ItemDataProvider<ITEM> dataProvider,
			ItemIdentifierProvider<ITEM, ?> itemIdentifier) {
		super();
		ObjectUtils.argumentNotNull(dataProvider, "ItemDataProvider must be not null");
		this.dataProvider = dataProvider;
		this.itemIdentifier = itemIdentifier;
	}

	protected ItemDataProvider<ITEM> getDataProvider() {
		return dataProvider;
	}

	protected Optional<ItemIdentifierProvider<ITEM, ?>> getItemIdentifier() {
		return Optional.ofNullable(itemIdentifier);
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.provider.AbstractBackEndDataProvider#fetchFromBackEnd(com.vaadin.data.provider.Query)
	 */
	@Override
	protected Stream<ITEM> fetchFromBackEnd(Query<ITEM, QueryFilter> query) {
		return dataProvider.load(getConfiguration(query), query.getOffset(), query.getLimit());
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.provider.AbstractBackEndDataProvider#sizeInBackEnd(com.vaadin.data.provider.Query)
	 */
	@Override
	protected int sizeInBackEnd(Query<ITEM, QueryFilter> query) {
		return Long.valueOf(dataProvider.size(getConfiguration(query))).intValue();
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.provider.DataProvider#getId(java.lang.Object)
	 */
	@Override
	public Object getId(ITEM item) {
		if (getItemIdentifier().isPresent()) {
			return  getItemIdentifier().get().getItemId(item);
		}
		return super.getId(item);
	}

	protected QueryConfigurationProvider getConfiguration(final Query<ITEM, QueryFilter> query) {
		final QueryFilter filter = query.getFilter().orElse(null);

		final List<QuerySort> sorts = new LinkedList<>();
		List<QuerySortOrder> orders = query.getSortOrders();
		if (orders != null && !orders.isEmpty()) {
			orders.forEach(o -> sorts.add(fromOrder(o)));
		}
		final QuerySort sort = getSort(sorts);

		return new QueryConfigurationProvider() {

			@Override
			public QueryFilter getQueryFilter() {
				return filter;
			}

			@Override
			public QuerySort getQuerySort() {
				return sort;
			}

		};
	}

	private static QuerySort fromOrder(QuerySortOrder order) {
		Path<?> path = Path.of(order.getSorted(), Object.class);
		SortDirection direction = (order.getDirection() != null
				&& order.getDirection() == com.vaadin.shared.data.sort.SortDirection.DESCENDING)
						? SortDirection.DESCENDING : SortDirection.ASCENDING;
		return QuerySort.of(path, direction);
	}

	private static QuerySort getSort(List<QuerySort> sorts) {
		if (!sorts.isEmpty()) {
			if (sorts.size() == 1) {
				return sorts.get(0);
			} else {
				return QuerySort.of(sorts);
			}
		}
		return null;
	}

}
