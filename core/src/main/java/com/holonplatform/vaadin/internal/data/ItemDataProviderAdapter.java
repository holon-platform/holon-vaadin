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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.holonplatform.core.Path;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.query.QueryConfigurationProvider;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.core.query.QuerySort;
import com.holonplatform.core.query.QuerySort.SortDirection;
import com.holonplatform.vaadin.data.ItemDataProvider;
import com.holonplatform.vaadin.data.ItemDataSource.Configuration;
import com.holonplatform.vaadin.data.ItemDataSource.PropertySortGenerator;
import com.holonplatform.vaadin.data.ItemIdentifierProvider;
import com.vaadin.data.provider.AbstractBackEndDataProvider;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.Query;
import com.vaadin.data.provider.QuerySortOrder;

/**
 * A {@link DataProvider} using an {@link ItemDataProvider} as data source.
 * 
 * @param <ITEM> Item type
 * 
 * @since 5.0.0
 */
public class ItemDataProviderAdapter<ITEM> extends AbstractBackEndDataProvider<ITEM, QueryFilter> {

	private static final long serialVersionUID = -5011712229278252796L;

	/**
	 * Actual item data provider
	 */
	private final ItemDataProvider<ITEM> dataProvider;

	/**
	 * Item identifier provider
	 */
	private final ItemIdentifierProvider<ITEM, ?> itemIdentifier;

	/**
	 * Configuration
	 */
	private final Configuration<ITEM, ?> configuration;

	/**
	 * Constructor.
	 * @param dataProvider Actual item data provider (not null)
	 */
	public ItemDataProviderAdapter(ItemDataProvider<ITEM> dataProvider) {
		super();
		ObjectUtils.argumentNotNull(dataProvider, "ItemDataProvider must be not null");
		this.dataProvider = dataProvider;
		this.itemIdentifier = null;
		this.configuration = null;
	}

	/**
	 * Constructor.
	 * @param dataProvider Actual item data provider (not null)
	 * @param itemIdentifier Item identifier provider
	 */
	public ItemDataProviderAdapter(ItemDataProvider<ITEM> dataProvider,
			ItemIdentifierProvider<ITEM, ?> itemIdentifier) {
		super();
		ObjectUtils.argumentNotNull(dataProvider, "ItemDataProvider must be not null");
		this.dataProvider = dataProvider;
		this.itemIdentifier = itemIdentifier;
		this.configuration = null;
	}

	/**
	 * Constructor using an item data source configuration.
	 * @param configuration Configuration (not null)
	 */
	public ItemDataProviderAdapter(Configuration<ITEM, ?> configuration) {
		super();
		ObjectUtils.argumentNotNull(configuration, "Configuration must be not null");
		this.configuration = configuration;
		this.dataProvider = null;
		this.itemIdentifier = null;
	}

	/**
	 * Get the item data provider.
	 * @return the item data provider
	 */
	protected ItemDataProvider<ITEM> getDataProvider() {
		return getConfiguration().map(c -> c.getDataProvider()).orElse(Optional.ofNullable(dataProvider))
				.orElseThrow(() -> new IllegalStateException("No ItemDataProvider available"));
	}

	/**
	 * Ge the item identifier provider, if available.
	 * @return Optional item identifier provider
	 */
	protected Optional<ItemIdentifierProvider<ITEM, ?>> getItemIdentifier() {
		return getConfiguration().map(c -> c.getItemIdentifierProvider()).orElse(Optional.ofNullable(itemIdentifier));
	}

	/**
	 * Get the configuration, if available.
	 * @return Optional configuration
	 */
	protected Optional<Configuration<ITEM, ?>> getConfiguration() {
		return Optional.ofNullable(configuration);
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.provider.AbstractBackEndDataProvider#fetchFromBackEnd(com.vaadin.data.provider.Query)
	 */
	@Override
	protected Stream<ITEM> fetchFromBackEnd(Query<ITEM, QueryFilter> query) {
		return getDataProvider().load(getConfiguration(query), query.getOffset(), query.getLimit());
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.provider.AbstractBackEndDataProvider#sizeInBackEnd(com.vaadin.data.provider.Query)
	 */
	@Override
	protected int sizeInBackEnd(Query<ITEM, QueryFilter> query) {
		return Long.valueOf(getDataProvider().size(getConfiguration(query))).intValue();
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.provider.DataProvider#getId(java.lang.Object)
	 */
	@Override
	public Object getId(ITEM item) {
		if (getItemIdentifier().isPresent()) {
			return getItemIdentifier().get().getItemId(item);
		}
		return super.getId(item);
	}

	/**
	 * Get the query configuration form given data provider {@link Query}.
	 * @param query Data provider query (not null)
	 * @return Query configuration
	 */
	protected QueryConfigurationProvider getConfiguration(final Query<ITEM, QueryFilter> query) {
		ObjectUtils.argumentNotNull(query, "Query must be not null");

		// filters
		final List<QueryFilter> filters = new LinkedList<>();
		// from data source configuration
		getConfiguration().flatMap(c -> c.getQueryFilter()).ifPresent(f -> filters.add(f));
		// from query definition
		query.getFilter().ifPresent(f -> filters.add(f));

		// sorts
		final List<QuerySort> sorts = new LinkedList<>();
		// from query definition
		sorts.addAll((query.getSortOrders() == null) ? Collections.emptyList()
				: query.getSortOrders().stream().map(o -> sortFromOrder(o))
						.flatMap(o -> o.isPresent() ? Stream.of(o.get()) : Stream.empty())
						.collect(Collectors.toList()));
		// from data source configuration
		getConfiguration().flatMap(c -> c.getQuerySort(sorts)).ifPresent(s -> sorts.add(s));

		return new QueryConfigurationProvider() {

			@Override
			public QueryFilter getQueryFilter() {
				return QueryFilter.allOf(filters).orElse(null);
			}

			@Override
			public QuerySort getQuerySort() {
				return sorts.isEmpty() ? null : QuerySort.of(sorts);
			}

		};
	}

	/**
	 * Get a {@link QuerySort} form given {@link QuerySortOrder}, if a {@link Path} property which corresponds to the
	 * ordered property id if available.
	 * <p>
	 * If a {@link PropertySortGenerator} is bound to the property to sort, it will be used to provide the query sort.
	 * </p>
	 * @param order Sort order
	 * @return Optional sort
	 */
	@SuppressWarnings("unchecked")
	protected Optional<QuerySort> sortFromOrder(QuerySortOrder order) {
		QuerySort sort = null;

		final SortDirection direction = (order.getDirection() != null
				&& order.getDirection() == com.vaadin.shared.data.sort.SortDirection.DESCENDING)
						? SortDirection.DESCENDING
						: SortDirection.ASCENDING;

		// check property
		Optional<?> p = getConfiguration().flatMap(c -> c.getPropertyById(order.getSorted()));
		if (p.isPresent()) {
			final Object property = p.get();
			sort = getConfiguration().map(c -> (Configuration<ITEM, Object>) c)
					.flatMap(c -> c.getPropertySortGenerator(property))
					.map(g -> g.getQuerySort(property, direction == SortDirection.ASCENDING)).orElse(null);
			if (sort == null && Path.class.isAssignableFrom(property.getClass())) {
				sort = QuerySort.of((Path<?>) property, direction);
			}
		}
		if (sort == null) {
			// use a default path
			sort = QuerySort.of(Path.of(order.getSorted(), Object.class), direction);
		}

		return Optional.ofNullable(sort);
	}

}
