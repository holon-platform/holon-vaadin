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

import com.holonplatform.core.ParameterSet;
import com.holonplatform.core.Path;
import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.query.QueryConfigurationProvider;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.core.query.QuerySort;
import com.holonplatform.core.query.QuerySort.SortDirection;
import com.holonplatform.vaadin.data.DatastoreDataProvider;
import com.holonplatform.vaadin.data.ItemIdentifierProvider;
import com.vaadin.data.provider.AbstractBackEndDataProvider;
import com.vaadin.data.provider.Query;
import com.vaadin.data.provider.QuerySortOrder;
import com.vaadin.shared.Registration;

/**
 * Default {@link DatastoreDataProvider} implementation.
 *
 * @since 5.0.0
 */
public class DefaultDatastoreDataProvider extends AbstractBackEndDataProvider<PropertyBox, QueryFilter>
		implements DatastoreDataProvider {

	private static final long serialVersionUID = -6164535815798337361L;

	/**
	 * Datastore
	 */
	private Datastore datastore;

	/**
	 * Data target
	 */
	private DataTarget<?> target;

	/**
	 * Item identifier
	 */
	private ItemIdentifierProvider<PropertyBox, ?> itemIdentifier;

	/**
	 * Property set provider
	 */
	private PropertySet<?> propertySet;

	/**
	 * Query configuration providers
	 */
	private List<QueryConfigurationProvider> queryConfigurationProviders = new LinkedList<>();

	/**
	 * Default constructor.
	 */
	public DefaultDatastoreDataProvider() {
		super();
	}

	/**
	 * Constructor.
	 * @param datastore Datastore
	 * @param target Query target
	 * @param propertySet Projection
	 */
	public DefaultDatastoreDataProvider(Datastore datastore, DataTarget<?> target, PropertySet<?> propertySet) {
		super();
		this.datastore = datastore;
		this.target = target;
		this.propertySet = propertySet;
	}

	/**
	 * Get the {@link Datastore} to use to perform count and load operations.
	 * @return the datastore
	 */
	protected Datastore getDatastore() {
		return datastore;
	}

	/**
	 * Get the data target to use.
	 * @return the data target
	 */
	protected DataTarget<?> getTarget() {
		return target;
	}

	/**
	 * Get the item identifier provider.
	 * @return the optional item identifier provider
	 */
	protected Optional<ItemIdentifierProvider<PropertyBox, ?>> getItemIdentifier() {
		return Optional.ofNullable(itemIdentifier);
	}

	/**
	 * Get the query projection.
	 * @return the query projection property set
	 */
	protected PropertySet<?> getPropertySet() {
		return propertySet;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.provider.DataProvider#isInMemory()
	 */
	@Override
	public boolean isInMemory() {
		return false;
	}

	/**
	 * Set the datastore to use.
	 * @param datastore the datastore to set
	 */
	public void setDatastore(Datastore datastore) {
		this.datastore = datastore;
	}

	/**
	 * Set the data target.
	 * @param target the target to set
	 */
	public void setTarget(DataTarget<?> target) {
		this.target = target;
	}

	/**
	 * Set the item identifier provider.
	 * @param itemIdentifier the item identifier provider to set
	 */
	public void setItemIdentifier(ItemIdentifierProvider<PropertyBox, ?> itemIdentifier) {
		this.itemIdentifier = itemIdentifier;
	}

	/**
	 * Set the property set to use as query projection.
	 * @param propertySet the propertySet to set
	 */
	public void setPropertySet(PropertySet<?> propertySet) {
		this.propertySet = propertySet;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.data.DatastoreDataProvider#addQueryConfigurationProvider(com.holonplatform.core.query.
	 * QueryConfigurationProvider)
	 */
	@Override
	public Registration addQueryConfigurationProvider(QueryConfigurationProvider queryConfigurationProvider) {
		ObjectUtils.argumentNotNull(queryConfigurationProvider, "QueryConfigurationProvider must be not null");
		queryConfigurationProviders.add(queryConfigurationProvider);
		return () -> queryConfigurationProviders.remove(queryConfigurationProvider);
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.provider.AbstractBackEndDataProvider#sizeInBackEnd(com.vaadin.data.provider.Query)
	 */
	@Override
	protected int sizeInBackEnd(Query<PropertyBox, QueryFilter> query) {
		return Long.valueOf(buildQuery(query, false).count()).intValue();
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.provider.AbstractBackEndDataProvider#fetchFromBackEnd(com.vaadin.data.provider.Query)
	 */
	@Override
	protected Stream<PropertyBox> fetchFromBackEnd(Query<PropertyBox, QueryFilter> query) {
		Stream<PropertyBox> results = buildQuery(query, true).stream(propertySet);
		if (getItemIdentifier().isPresent()) {
			return results.map(pb -> new IdentifiablePropertyBox(pb, getItemIdentifier().get()));
		}
		return results;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.provider.DataProvider#getId(java.lang.Object)
	 */
	@Override
	public Object getId(PropertyBox item) {
		if (getItemIdentifier().isPresent()) {
			return getItemIdentifier().get().getItemId(item);
		}
		return super.getId(item);
	}

	/**
	 * Build a {@link Query} using the Datastore and configuring query filters and sorts.
	 * @param query Query
	 * @param withSorts Whether to apply sorts, if any, to query
	 * @return Query instance
	 */
	protected com.holonplatform.core.query.Query buildQuery(Query<PropertyBox, QueryFilter> query, boolean withSorts) {

		com.holonplatform.core.query.Query q = getDatastore().query();

		// target
		q.target(getTarget());

		// filters
		final List<QueryFilter> filters = new LinkedList<>();
		query.getFilter().ifPresent(f -> filters.add(f));
		queryConfigurationProviders.forEach(p -> {
			QueryFilter qf = p.getQueryFilter();
			if (qf != null) {
				filters.add(qf);
			}
		});
		QueryFilter.allOf(filters).ifPresent(f -> q.filter(f));

		// sorts
		if (withSorts) {
			final List<QuerySort> sorts = new LinkedList<>();

			List<QuerySortOrder> orders = query.getSortOrders();
			if (orders != null && !orders.isEmpty()) {
				orders.forEach(o -> sorts.add(fromOrder(getPropertySet(), o)));
			}

			queryConfigurationProviders.forEach(p -> {
				QuerySort qs = p.getQuerySort();
				if (qs != null) {
					sorts.add(qs);
				}
			});

			if (!sorts.isEmpty()) {
				if (sorts.size() == 1) {
					q.sort(sorts.get(0));
				} else {
					q.sort(QuerySort.of(sorts));
				}
			}
		}

		// parameters
		queryConfigurationProviders.forEach(p -> {
			ParameterSet parameters = p.getQueryParameters();
			if (parameters != null) {
				parameters.forEachParameter((n, v) -> q.parameter(n, v));
			}
		});

		// paging
		if (query.getLimit() < Integer.MAX_VALUE) {
			q.limit(query.getLimit());
			q.offset(query.getOffset());
		}

		return q;
	}

	protected void checkConfiguration() throws IllegalStateException {
		if (getDatastore() == null) {
			throw new IllegalStateException("Missing Datastore");
		}
		if (getTarget() == null) {
			throw new IllegalStateException("Missing DataTarget");
		}
		if (getPropertySet() == null) {
			throw new IllegalStateException("Missing PropertySet");
		}
	}

	private static QuerySort fromOrder(PropertySet<?> set, QuerySortOrder order) {
		Path<?> path = getPathByName(set, order.getSorted()).orElseThrow(() -> new IllegalArgumentException(
				"No property of the set matches with sort name: " + order.getSorted()));
		SortDirection direction = (order.getDirection() != null
				&& order.getDirection() == com.vaadin.shared.data.sort.SortDirection.DESCENDING)
						? SortDirection.DESCENDING : SortDirection.ASCENDING;
		return QuerySort.of(path, direction);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Optional<Path<?>> getPathByName(PropertySet set, String propertyName) {
		if (set != null && propertyName != null) {
			return set.stream().filter(p -> Path.class.isAssignableFrom(p.getClass()))
					.filter(p -> propertyName.equals(((Path) p).getName())).findFirst();
		}
		return Optional.empty();
	}

	public static class DefaultBuilder implements Builder {

		private final DefaultDatastoreDataProvider instance = new DefaultDatastoreDataProvider();

		private final List<Property<?>> properties = new LinkedList<>();

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.data.DatastoreDataProvider.Builder#datastore(com.holonplatform.core.datastore.
		 * Datastore)
		 */
		@Override
		public Builder datastore(Datastore datastore) {
			instance.setDatastore(datastore);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.data.DatastoreDataProvider.Builder#target(com.holonplatform.core.datastore.
		 * DataTarget)
		 */
		@Override
		public Builder target(DataTarget<?> target) {
			instance.setTarget(target);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.data.DatastoreDataProvider.Builder#itemIdentifierProvider(com.holonplatform.vaadin.
		 * data.ItemIdentifierProvider)
		 */
		@Override
		public Builder itemIdentifierProvider(ItemIdentifierProvider<PropertyBox, ?> itemIdentifierProvider) {
			instance.setItemIdentifier(itemIdentifierProvider);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.data.DatastoreDataProvider.Builder#withProperties(java.lang.Iterable)
		 */
		@SuppressWarnings("rawtypes")
		@Override
		public <P extends Property> Builder withProperties(Iterable<P> properties) {
			ObjectUtils.argumentNotNull(properties, "Properties must be not null");
			properties.forEach(p -> this.properties.add(p));
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.data.DatastoreDataProvider.Builder#withQueryConfigurationProvider(com.holonplatform.
		 * core.query.QueryConfigurationProvider)
		 */
		@Override
		public Builder withQueryConfigurationProvider(QueryConfigurationProvider queryConfigurationProvider) {
			instance.addQueryConfigurationProvider(queryConfigurationProvider);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.data.DatastoreDataProvider.Builder#build()
		 */
		@Override
		public DatastoreDataProvider build() {
			instance.setPropertySet(PropertySet.of(this.properties));
			instance.checkConfiguration();
			return instance;
		}

	}

}
