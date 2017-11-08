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
import com.holonplatform.core.property.Property;
import com.holonplatform.core.query.QueryConfigurationProvider;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.core.query.QuerySort;
import com.holonplatform.core.query.QuerySort.SortDirection;
import com.holonplatform.vaadin.data.ItemDataProvider;
import com.holonplatform.vaadin.data.ItemDataSource.Configuration;
import com.holonplatform.vaadin.data.ItemDataSource.PropertySortGenerator;
import com.holonplatform.vaadin.data.ItemIdentifierProvider;
import com.holonplatform.vaadin.internal.utils.PropertyUtils;
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

	private final Configuration<ITEM, ?> configuration;

	public ItemDataProviderAdapter(ItemDataProvider<ITEM> dataProvider) {
		this(dataProvider, null, null);
	}

	public ItemDataProviderAdapter(ItemDataProvider<ITEM> dataProvider, ItemIdentifierProvider<ITEM, ?> itemIdentifier,
			Configuration<ITEM, ?> configuration) {
		super();
		ObjectUtils.argumentNotNull(dataProvider, "ItemDataProvider must be not null");
		this.dataProvider = dataProvider;
		this.itemIdentifier = itemIdentifier;
		this.configuration = configuration;
	}

	protected ItemDataProvider<ITEM> getDataProvider() {
		return dataProvider;
	}

	protected Optional<ItemIdentifierProvider<ITEM, ?>> getItemIdentifier() {
		return Optional.ofNullable(itemIdentifier);
	}

	protected Optional<Configuration<ITEM, ?>> getConfiguration() {
		return Optional.ofNullable(configuration);
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
			return getItemIdentifier().get().getItemId(item);
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

	private QuerySort fromOrder(QuerySortOrder order) {
		final SortDirection direction = (order.getDirection() != null
				&& order.getDirection() == com.vaadin.shared.data.sort.SortDirection.DESCENDING)
						? SortDirection.DESCENDING : SortDirection.ASCENDING;

		return getConfiguration().flatMap(cfg -> getSortUsingConfiguration(order.getSorted(), direction, cfg))
				.orElse(getSortFromOrder(order, direction));
	}

	private static QuerySort getSortFromOrder(QuerySortOrder order, SortDirection direction) {
		return QuerySort.of(Path.of(order.getSorted(), Object.class), direction);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Optional<QuerySort> getSortUsingConfiguration(String sortId, SortDirection direction,
			Configuration<ITEM, ?> configuration) {
		if (String.class.isAssignableFrom(configuration.getPropertyType())) {
			Optional<PropertySortGenerator<String>> generator = ((Configuration<ITEM, String>) configuration)
					.getPropertySortGenerator(sortId);
			if (generator.isPresent()) {
				return Optional.ofNullable(generator.get().getQuerySort(sortId, SortDirection.ASCENDING == direction));
			}
			return Optional.of(QuerySort.of(Path.of(sortId, Object.class), SortDirection.ASCENDING == direction));
		} else if (Property.class.isAssignableFrom(configuration.getPropertyType())) {
			Property<?> property = getPropertyById(sortId, (Configuration<ITEM, Property>) configuration);
			if (property != null) {
				Optional<PropertySortGenerator<Property>> generator = ((Configuration<ITEM, Property>) configuration)
						.getPropertySortGenerator(property);
				if (generator.isPresent()) {
					return Optional
							.ofNullable(generator.get().getQuerySort(property, SortDirection.ASCENDING == direction));
				}
				if (Path.class.isAssignableFrom(property.getClass())) {
					return Optional.of(QuerySort.of((Path<?>) property, SortDirection.ASCENDING == direction));
				}
			}
		}
		return Optional.empty();
	}

	@SuppressWarnings("rawtypes")
	private static Property<?> getPropertyById(String propertyId, Configuration<?, Property> configuration) {
		if (propertyId != null && configuration.getProperties() != null) {
			for (Property<?> property : configuration.getProperties()) {
				if (propertyId.equals(PropertyUtils.generatePropertyId(property))) {
					return property;
				}
			}
		}
		return null;
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
