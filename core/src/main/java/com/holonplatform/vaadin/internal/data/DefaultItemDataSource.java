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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.holonplatform.core.ParameterSet;
import com.holonplatform.core.Path;
import com.holonplatform.core.exceptions.DataAccessException;
import com.holonplatform.core.internal.DefaultParameterSet;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.internal.utils.TypeUtils;
import com.holonplatform.core.query.QueryConfigurationProvider;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.core.query.QuerySort;
import com.holonplatform.core.query.QuerySort.SortDirection;
import com.holonplatform.vaadin.data.ItemDataProvider;
import com.holonplatform.vaadin.data.ItemDataSource;
import com.holonplatform.vaadin.data.ItemDataSource.Configuration;
import com.holonplatform.vaadin.data.ItemIdentifierProvider;
import com.vaadin.data.provider.Query;
import com.vaadin.data.provider.QuerySortOrder;
import com.vaadin.shared.Registration;

/**
 * Default {@link ItemDataSource} implementation.
 * 
 * @param <ITEM> Item type
 * @param <PROPERTY> Item property type
 * 
 * @since 5.0.0
 */
public class DefaultItemDataSource<ITEM, PROPERTY>
		implements ItemDataSource<ITEM, PROPERTY>, Configuration<ITEM, PROPERTY> {

	private static final long serialVersionUID = 5690427592609021861L;

	/**
	 * Auto refresh
	 */
	private boolean autoRefresh = true;

	/**
	 * Property type
	 */
	private final Class<?> propertyType;

	/**
	 * Properties
	 */
	private final List<PROPERTY> properties = new LinkedList<>();

	/**
	 * Property ids
	 */
	private final Map<String, PROPERTY> propertyIds = new HashMap<>();

	/**
	 * Property types
	 */
	private final Map<PROPERTY, Class<?>> propertyTypes = new HashMap<>();

	/**
	 * Property default values
	 */
	private Map<PROPERTY, Object> defaultValues;

	/**
	 * Read-only properties
	 */
	private final Collection<PROPERTY> readOnlyProperties = new LinkedList<>();

	/**
	 * Sortable properties
	 */
	private final Collection<PROPERTY> sortableProperties = new LinkedList<>();

	/**
	 * Item sorts
	 */
	private final List<ItemSort<PROPERTY>> itemSorts = new LinkedList<>();

	/**
	 * Item store
	 */
	private ItemStore<ITEM> itemStore;

	/**
	 * Item data provider
	 */
	private ItemDataProvider<ITEM> dataProvider;

	/**
	 * Item identifier provider
	 */
	private ItemIdentifierProvider<ITEM, ?> itemIdentifierProvider;

	/**
	 * Item commit handler
	 */
	private CommitHandler<ITEM> commitHandler;

	/**
	 * Additional QueryConfigurationProviders
	 */
	private List<QueryConfigurationProvider> queryConfigurationProviders = new LinkedList<>();

	/**
	 * Fixed query filter: if not null, it will always be added to query filters
	 */
	private QueryFilter fixedFilter;

	/**
	 * Fixed query sort: if not null, it will always be added to query sorts
	 */
	private QuerySort fixedSort;

	/**
	 * Default query sort: if no other sort is provided, this sort will be used
	 */
	private QuerySort defaultSort;

	/**
	 * {@link PropertySortGenerator}s bound to properties
	 */
	private Map<PROPERTY, PropertySortGenerator<PROPERTY>> propertySortGenerators;

	/**
	 * Query parameters
	 */
	private final DefaultParameterSet queryParameters = new DefaultParameterSet();

	/**
	 * Data provider query
	 */
	private Query<ITEM, QueryFilter> dataProviderQuery;

	/**
	 * Track generated property ids count to avoid duplicates
	 */
	private final Map<String, Integer> generatedPropertyIds = new HashMap<>();

	/**
	 * Constructor.
	 * @param propertyType Property representation type (not null)
	 * @param dataProvider {@link ItemDataProvider} to be used as items data source
	 * @param itemIdentifierProvider Item identifier provider
	 * @param batchSize Batch size
	 */
	public DefaultItemDataSource(Class<?> propertyType, ItemDataProvider<ITEM> dataProvider,
			ItemIdentifierProvider<ITEM, Object> itemIdentifierProvider, int batchSize) {
		this(propertyType);
		this.dataProvider = dataProvider;
		this.itemIdentifierProvider = itemIdentifierProvider;
		init(batchSize);
	}

	/**
	 * Constructor which do not perform internal initialization. The container initialization must be performed later
	 * using the init method.
	 * @param propertyType Property representation type (not null)
	 */
	protected DefaultItemDataSource(Class<?> propertyType) {
		super();
		ObjectUtils.argumentNotNull(propertyType, "Property type must be not null");
		this.propertyType = propertyType;

		// include data provider filters and sorts
		addQueryConfigurationProvider(new QueryConfigurationProvider() {

			@Override
			public QueryFilter getQueryFilter() {
				return getDataProviderQuery().flatMap(q -> q.getFilter()).orElse(null);
			}

			/*
			 * (non-Javadoc)
			 * @see com.holonplatform.core.query.QueryConfigurationProvider#getQuerySort()
			 */
			@Override
			public QuerySort getQuerySort() {
				List<QuerySort> sorts = getDataProviderQuery().map(q -> q.getSortOrders())
						.orElse(Collections.emptyList()).stream().map(o -> sortFromOrder(o))
						.flatMap(o -> o.isPresent() ? Stream.of(o.get()) : Stream.empty()).collect(Collectors.toList());
				return sorts.isEmpty() ? null : QuerySort.of(sorts);
			}

		});
	}

	/**
	 * Init the data source container, configuring the internal {@link ItemStore}.
	 * @param batchSize the batch size to use
	 */
	protected void init(int batchSize) {
		final QueryConfigurationProvider qcp = new QueryConfigurationProvider() {

			@Override
			public QueryFilter getQueryFilter() {
				return DefaultItemDataSource.this.getQueryFilter().orElse(null);
			}

			@Override
			public QuerySort getQuerySort() {
				return DefaultItemDataSource.this.getQuerySort(Collections.emptySet()).orElse(null);
			}

			@Override
			public ParameterSet getQueryParameters() {
				return DefaultItemDataSource.this.getQueryParameters();
			}

		};
		this.itemStore = new DefaultItemStore<>(qcp,
				getDataProvider().orElseThrow(() -> new IllegalStateException("Missing ItemDataProvider")),
				getItemIdentifierProvider().orElse(null), batchSize, determineMaxCacheSize(batchSize));
		this.itemStore.setFreezed(!isAutoRefresh());
	}

	/**
	 * Get the items store.
	 * @return the items store, empty if not setted
	 */
	protected Optional<ItemStore<ITEM>> getItemStore() {
		return Optional.ofNullable(itemStore);
	}

	/**
	 * Get the items store.
	 * @return the items store
	 * @throws IllegalStateException If the items store is not available
	 */
	protected ItemStore<ITEM> requireItemStore() {
		return getItemStore()
				.orElseThrow(() -> new IllegalStateException("Missing ItemStore: check container configuration"));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemDataSource.Configuration#getPropertyType()
	 */
	@Override
	public Class<?> getPropertyType() {
		return propertyType;
	}

	/**
	 * Set the {@link ItemIdentifierProvider}.
	 * @param <ID> Item id type
	 * @param itemIdentifierProvider the item identifier provider to set
	 */
	public <ID> void setItemIdentifierProvider(ItemIdentifierProvider<ITEM, ID> itemIdentifierProvider) {
		this.itemIdentifierProvider = itemIdentifierProvider;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemDataSource.Configuration#getItemIdentifierProvider()
	 */
	@Override
	public Optional<ItemIdentifierProvider<ITEM, ?>> getItemIdentifierProvider() {
		return Optional.ofNullable(itemIdentifierProvider);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemDataSource.Configuration#getDataProvider()
	 */
	@Override
	public Optional<ItemDataProvider<ITEM>> getDataProvider() {
		return Optional.ofNullable(dataProvider);
	}

	/**
	 * Set the item data provider.
	 * @param dataProvider the item data provider to set
	 */
	public void setDataProvider(ItemDataProvider<ITEM> dataProvider) {
		this.dataProvider = dataProvider;
	}

	/**
	 * Get the {@link ItemDataProvider}, throwing an exception if not available.
	 * @return the item data provider
	 * @throws IllegalStateException If not available
	 */
	protected ItemDataProvider<ITEM> requireDataProvider() {
		return getDataProvider()
				.orElseThrow(() -> new IllegalStateException("Missing ItemDataProvider: check configuration"));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemDataSource#getConfiguration()
	 */
	@Override
	public Configuration<ITEM, PROPERTY> getConfiguration() {
		return this;
	}

	/**
	 * Calculate max item store cache size using batch size, if positive or default.
	 * @param batchSize Batch size
	 * @return Item store max cache size
	 */
	protected int determineMaxCacheSize(int batchSize) {
		if (batchSize > 0) {
			return batchSize * 10;
		} else {
			return ItemStore.DEFAULT_MAX_CACHE_SIZE;
		}
	}

	/**
	 * Set the {@link ItemStore} max cache size.
	 * @param maxCacheSize The max cache size to set
	 */
	public void setMaxCacheSize(int maxCacheSize) {
		getItemStore().ifPresent(s -> {
			s.setMaxCacheSize(maxCacheSize);
		});
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.querycontainer.ItemQueryContainer#isAutoRefresh()
	 */
	@Override
	public boolean isAutoRefresh() {
		return autoRefresh;
	}

	/**
	 * Set the auto-refresh mode.
	 * @param autoRefresh <code>true</code> to enable auto-refresh mode, <code>false</code> to disable
	 */
	public void setAutoRefresh(boolean autoRefresh) {
		this.autoRefresh = autoRefresh;
		// if auto refresh not enabled, freeze the item store
		getItemStore().ifPresent(i -> i.setFreezed(!autoRefresh));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemDataSource.Configuration#getProperties()
	 */
	@Override
	public Iterable<PROPERTY> getProperties() {
		return properties;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemDataSource.Configuration#getPropertyId(java.lang.Object)
	 */
	@Override
	public Optional<String> getPropertyId(PROPERTY property) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		return propertyIds.entrySet().stream().filter(e -> property.equals(e.getValue())).findFirst()
				.map(e -> e.getKey());
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemDataSource.Configuration#getPropertyById(java.lang.String)
	 */
	@Override
	public Optional<PROPERTY> getPropertyById(String propertyId) {
		return Optional.ofNullable(propertyIds.get(propertyId));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.querycontainer.ItemQueryDefinition# isPropertyReadOnly(java.lang.Object)
	 */
	@Override
	public boolean isPropertyReadOnly(Object propertyId) {
		return (propertyId != null && readOnlyProperties.contains(propertyId));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.querycontainer.ItemQueryDefinition# isPropertySortable(java.lang.Object)
	 */
	@Override
	public boolean isPropertySortable(Object propertyId) {
		return (propertyId != null && sortableProperties.contains(propertyId));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.querycontainer.ItemQueryDefinition# getPropertyDefaultValue(java.lang.Object)
	 */
	@Override
	public Object getPropertyDefaultValue(Object propertyId) {
		if (defaultValues != null && propertyId != null) {
			return defaultValues.get(propertyId);
		}
		return null;
	}

	/**
	 * Register a property in this {@link ItemDataSource}.
	 * @param property The property to add (not null)
	 * @param type The property type (not null)
	 * @param readOnly Whether to set the property as read-only
	 * @param sortable Whether to set the property as sortable
	 * @param defaultValue Optional property default value
	 * @param generatePropertyId Whether to auto generate a property id
	 */
	public <T> void addProperty(PROPERTY property, Class<T> type, boolean readOnly, boolean sortable, T defaultValue,
			boolean generatePropertyId) {
		ObjectUtils.argumentNotNull(property, "Property id must be not null");
		ObjectUtils.argumentNotNull(type, "Property type must be not null");

		// remove any previous property with same id
		if (properties.contains(property)) {
			properties.remove(property);
		}

		properties.add(property);
		propertyTypes.put(property, type);

		// property id
		if (generatePropertyId) {
			propertyIds.put(generatePropertyId(property), property);
		}

		if (readOnly) {
			readOnlyProperties.add(property);
		}
		if (sortable) {
			sortableProperties.add(property);
		}
		if (defaultValue != null) {
			if (defaultValues == null) {
				defaultValues = new HashMap<>();
			}
			defaultValues.put(property, defaultValue);
		}
	}

	/**
	 * Generate a property id for given property.
	 * @param property Property (not null)
	 * @return Property id
	 */
	protected String generatePropertyId(PROPERTY property) {
		String id = (TypeUtils.isString(property.getClass())) ? (String) property : "property";

		// check duplicates
		Integer count = generatedPropertyIds.get(id);
		if (count != null && count > 0) {
			int sequence = count.intValue() + 1;
			generatedPropertyIds.put(id, sequence);
			return id + sequence;
		} else {
			generatedPropertyIds.put(id, 1);
			return id;
		}

	}

	/**
	 * Set given property sortable mode.
	 * @param propertyId Property id (not null)
	 * @param sortable Whether to set the property as sortable
	 */
	public void setPropertySortable(PROPERTY propertyId, boolean sortable) {
		ObjectUtils.argumentNotNull(propertyId, "Property id must be not null");
		if (sortable) {
			if (!sortableProperties.contains(propertyId)) {
				sortableProperties.add(propertyId);
			}
		} else {
			sortableProperties.remove(propertyId);
		}
	}

	/**
	 * Set given property read-only mode.
	 * @param propertyId Property id (not null)
	 * @param readOnly Whether to set the property as read-only
	 */
	public void setPropertyReadOnly(PROPERTY propertyId, boolean readOnly) {
		ObjectUtils.argumentNotNull(propertyId, "Property id must be not null");
		if (readOnly) {
			if (!readOnlyProperties.contains(propertyId)) {
				readOnlyProperties.add(propertyId);
			}
		} else {
			readOnlyProperties.remove(propertyId);
		}
	}

	/**
	 * Set given property default value.
	 * @param propertyId Property id (not null)
	 * @param defaultValue Default value (may be null)
	 */
	public void setPropertyDefaultValue(PROPERTY propertyId, Object defaultValue) {
		ObjectUtils.argumentNotNull(propertyId, "Property id must be not null");
		if (defaultValues == null) {
			defaultValues = new HashMap<>();
		}
		defaultValues.put(propertyId, defaultValue);
	}

	/**
	 * Set the id associated to given property.
	 * @param property Property id (not null)
	 * @param id Property id
	 */
	public void setPropertyId(PROPERTY property, String id) {
		ObjectUtils.argumentNotNull(property, "Property id must be not null");
		propertyIds.put(id, property);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.querycontainer.ItemQueryContainer#refresh()
	 */
	@Override
	public void refresh() throws DataAccessException {
		requireItemStore().reset(true, false);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.querycontainer.ItemQueryContainer#clear()
	 */
	@Override
	public void clear() {
		requireItemStore().reset(true, !isAutoRefresh());
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Container#getItemIds()
	 */
	@Override
	public Collection<?> getItemIds() {
		return requireItemStore().getItemIds();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemDataSource.Configuration#getPropertyType(java.lang.Object)
	 */
	@Override
	public Class<?> getPropertyType(PROPERTY property) {
		return propertyTypes.get(property);
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Container#size()
	 */
	@Override
	public int size() {
		return requireItemStore().size();
	}

	/**
	 * Checks whether given item id is available in the item store.
	 * @param itemId The item id
	 * @return <code>true</code> if the item store contains an item with given id, <code>false</code> otherwise
	 */
	public boolean containsId(Object itemId) {
		return requireItemStore().containsItem(itemId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemDataSource.Configuration#getCommitHandler()
	 */
	@Override
	public Optional<CommitHandler<ITEM>> getCommitHandler() {
		return Optional.ofNullable(commitHandler);
	}

	/**
	 * Set the {@link CommitHandler} to use.
	 * @param commitHandler The commit handler to set
	 */
	public void setCommitHandler(CommitHandler<ITEM> commitHandler) {
		this.commitHandler = commitHandler;
	}

	@Override
	public Registration addQueryConfigurationProvider(QueryConfigurationProvider queryConfigurationProvider) {
		ObjectUtils.argumentNotNull(queryConfigurationProvider, "QueryConfigurationProvider must be not null");
		if (!queryConfigurationProviders.contains(queryConfigurationProvider)) {
			queryConfigurationProviders.add(queryConfigurationProvider);
			// reset store
			resetStorePreservingFreezeState();
		}
		return () -> {
			queryConfigurationProviders.remove(queryConfigurationProvider);
			// reset store
			resetStorePreservingFreezeState();
		};
	}

	/**
	 * Set the fixed query filter.
	 * @param filter Filter to set
	 */
	public void setFixedFilter(QueryFilter filter) {
		this.fixedFilter = filter;
		// reset store
		resetStorePreservingFreezeState();
	}

	/**
	 * Set the fixed query sort.
	 * @param sort Sort to set
	 */
	public void setFixedSort(QuerySort sort) {
		this.fixedSort = sort;
		// reset store
		resetStorePreservingFreezeState();
	}

	/**
	 * Set the default query sort.
	 * @param sort Sort to set
	 */
	public void setDefaultSort(QuerySort sort) {
		this.defaultSort = sort;
		// reset store
		resetStorePreservingFreezeState();
	}

	/**
	 * Add a query parameter.
	 * @param name Parameter name (not null)
	 * @param value Parameter value
	 */
	public void addQueryParameter(String name, Object value) {
		queryParameters.addParameter(name, value);
		// reset store
		resetStorePreservingFreezeState();
	}

	/**
	 * Remove a query parameter.
	 * @param name Parameter name (not null)
	 */
	public void removeQueryParameter(String name) {
		queryParameters.removeParameter(name);
		// reset store
		resetStorePreservingFreezeState();
	}

	/**
	 * Set a {@link PropertySortGenerator} for given property.
	 * @param property Property (not null)
	 * @param propertySortGenerator Sort generator (not null)
	 */
	public void setPropertySortGenerator(PROPERTY property, PropertySortGenerator<PROPERTY> propertySortGenerator) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		ObjectUtils.argumentNotNull(propertySortGenerator, "PropertySortGenerator must be not null");
		if (propertySortGenerators == null) {
			propertySortGenerators = new HashMap<>(4);
		}
		propertySortGenerators.put(property, propertySortGenerator);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<PropertySortGenerator<PROPERTY>> getPropertySortGenerator(PROPERTY property) {
		if (propertySortGenerators != null) {
			return Optional.ofNullable(propertySortGenerators.get(property));
		}
		return Optional.empty();
	}

	/**
	 * Additional QueryConfigurationProvider
	 * @return QueryConfigurationProviders, an empty List if none
	 */
	public List<QueryConfigurationProvider> getQueryConfigurationProviders() {
		return queryConfigurationProviders;
	}

	/**
	 * Fixed query filter
	 * @return Fixed filter, or <code>null</code> if not setted
	 */
	public QueryFilter getFixedFilter() {
		return fixedFilter;
	}

	/**
	 * Fixed query sort
	 * @return Fixed sort, or <code>null</code> if not setted
	 */
	public QuerySort getFixedSort() {
		return fixedSort;
	}

	/**
	 * Default query sort
	 * @return Default sort, or <code>null</code> if not setted
	 */
	public QuerySort getDefaultSort() {
		return defaultSort;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemDataSource#sort(com.holonplatform.vaadin.data.ItemDataSource.ItemSort[])
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void sort(ItemSort<PROPERTY>... sorts) {
		List<ItemSort<PROPERTY>> itemSorts = (sorts == null) ? null : Arrays.asList(sorts);
		setItemSorts(itemSorts);
		// refresh
		refresh();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemDataSource.Configuration#getItemSorts()
	 */
	@Override
	public List<ItemSort<PROPERTY>> getItemSorts() {
		return itemSorts;
	}

	/**
	 * Set the item sort directives.
	 * @param sorts Item sorts
	 */
	public void setItemSorts(List<ItemSort<PROPERTY>> sorts) {
		itemSorts.clear();
		if (sorts != null) {
			sorts.forEach(s -> itemSorts.add(s));
		}
	}

	@Override
	public Optional<QueryFilter> getQueryFilter() {
		final LinkedList<QueryFilter> filters = new LinkedList<>();

		// fixed
		QueryFilter fixed = getFixedFilter();
		if (fixed != null) {
			filters.add(fixed);
		}

		// externally provided
		getQueryConfigurationProviders().forEach(p -> {
			QueryFilter filter = p.getQueryFilter();
			if (filter != null) {
				filters.add(filter);
			}
		});

		return QueryFilter.allOf(filters);
	}

	@Override
	public Optional<QuerySort> getQuerySort(Collection<QuerySort> currentSorts) {

		final boolean hasPreviousSorts = currentSorts != null && currentSorts.size() > 0;

		LinkedList<QuerySort> sorts = new LinkedList<>();

		// sorts
		List<ItemSort<PROPERTY>> itemSorts = getItemSorts();
		if (!itemSorts.isEmpty()) {
			// item sorts
			for (ItemSort<PROPERTY> itemSort : itemSorts) {
				sortFromItemSort(itemSort).ifPresent(s -> sorts.add(s));
			}
		} else {
			// externally provided
			getQueryConfigurationProviders().forEach(p -> {
				QuerySort sort = p.getQuerySort();
				if (sort != null) {
					sorts.add(sort);
				}
			});
		}

		// default sort
		if (!hasPreviousSorts && sorts.isEmpty()) {
			QuerySort dft = getDefaultSort();
			if (dft != null) {
				sorts.add(dft);
			}
		}

		// fixed
		QuerySort fixed = getFixedSort();
		if (fixed != null) {
			sorts.add(fixed);
		}

		return sorts.isEmpty() ? Optional.empty() : Optional.of(QuerySort.of(sorts));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.query.QueryConfigurationProvider#getQueryParameters()
	 */
	@Override
	public ParameterSet getQueryParameters() {
		final ParameterSet.Builder<?> builder = ParameterSet.builder().parameters(queryParameters);

		// externally provided
		getQueryConfigurationProviders().forEach(p -> {
			ParameterSet parameters = p.getQueryParameters();
			if (parameters != null) {
				builder.parameters(parameters);
			}
		});

		return queryParameters;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemDataSource#indexOfItem(java.lang.Object)
	 */
	@Override
	public int indexOfItem(ITEM item) {
		Object itemId = getId(item);
		if (itemId != null) {
			return requireItemStore().indexOfItem(itemId);
		}
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemDataSource#getItemAt(int)
	 */
	@Override
	public ITEM getItemAt(int index) {
		return requireItemStore().getItem(index);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemDataSource#getId(java.lang.Object)
	 */
	@Override
	public Object getId(ITEM item) {
		if (item != null) {
			return requireItemStore().getItemId(item);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemDataSource#get(java.lang.Object)
	 */
	@Override
	public Optional<ITEM> get(Object itemId) {
		ObjectUtils.argumentNotNull(itemId, "Item id must be not null");
		int index = requireItemStore().indexOfItem(itemId);
		if (index > -1) {
			ITEM item = requireItemStore().getItem(index);
			if (item != null) {
				return Optional.ofNullable(item);
			}
		}
		return Optional.empty();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemDataSource#add(java.lang.Object)
	 */
	@Override
	public Object add(ITEM item) {
		ObjectUtils.argumentNotNull(item, "Item to add must be not null");
		return requireItemStore().addItem(item);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemDataSource#update(java.lang.Object)
	 */
	@Override
	public void update(ITEM item) {
		ObjectUtils.argumentNotNull(item, "Item to update must be not null");
		requireItemStore().setItemModified(item);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemDataSource#remove(java.lang.Object)
	 */
	@Override
	public boolean remove(ITEM item) {
		ObjectUtils.argumentNotNull(item, "Item to remove must be not null");
		return requireItemStore().removeItem(item);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemDataSource#refresh(java.lang.Object)
	 */
	@Override
	public void refresh(ITEM item) {
		ObjectUtils.argumentNotNull(item, "Item to refresh must be not null");
		requireItemStore().refreshItem(item);
	}

	/**
	 * Get the data provider {@link Query} to use to obtain filters and sorts, if available.
	 * @return Optional data provider query
	 */
	public Optional<Query<ITEM, QueryFilter>> getDataProviderQuery() {
		return Optional.ofNullable(dataProviderQuery);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemDataSource#setDataProviderQuery(com.vaadin.data.provider.Query)
	 */
	@Override
	public void setDataProviderQuery(Query<ITEM, QueryFilter> dataProviderQuery) {
		this.dataProviderQuery = dataProviderQuery;
	}

	/**
	 * Reset item store content preserving the <em>freezed</em> state
	 */
	protected void resetStorePreservingFreezeState() {
		getItemStore().ifPresent(i -> {
			boolean freezed = i.isFreezed();
			try {
				i.reset(false, false);
			} finally {
				i.setFreezed(freezed);
			}
		});
	}

	/**
	 * Get a {@link QuerySort} form given {@link QuerySortOrder}, if a {@link Path} property which corresponds to the
	 * ordered property id is available.
	 * <p>
	 * If a {@link PropertySortGenerator} is bound to the property to sort, it will be used to provide the query sort.
	 * </p>
	 * @param order Sort order
	 * @return Optional sort
	 */
	protected Optional<QuerySort> sortFromOrder(QuerySortOrder order) {
		QuerySort sort = null;
		Optional<PROPERTY> p = getPropertyById(order.getSorted());
		if (p.isPresent()) {
			final PROPERTY property = p.get();
			sort = getPropertySortGenerator(property).map(g -> g.getQuerySort(property,
					order.getDirection() == com.vaadin.shared.data.sort.SortDirection.ASCENDING)).orElse(null);
			if (sort == null && Path.class.isAssignableFrom(property.getClass())) {
				sort = QuerySort.of((Path<?>) property,
						(order.getDirection() == com.vaadin.shared.data.sort.SortDirection.DESCENDING)
								? SortDirection.DESCENDING
								: SortDirection.ASCENDING);
			}
		}
		return Optional.ofNullable(sort);
	}

	/**
	 * Get a {@link QuerySort} form given {@link ItemSort}.
	 * <p>
	 * If a {@link PropertySortGenerator} is bound to the property to sort, it will be used to provide the query sort.
	 * </p>
	 * @param itemSort Item sort
	 * @return Optional sort
	 */
	protected Optional<QuerySort> sortFromItemSort(ItemSort<PROPERTY> itemSort) {
		QuerySort sort = null;
		sort = getPropertySortGenerator(itemSort.getProperty())
				.map(g -> g.getQuerySort(itemSort.getProperty(), itemSort.isAscending())).orElse(null);
		if (sort == null && Path.class.isAssignableFrom(itemSort.getProperty().getClass())) {
			sort = QuerySort.of((Path<?>) itemSort.getProperty(),
					itemSort.isAscending() ? SortDirection.ASCENDING : SortDirection.DESCENDING);
		}
		return Optional.ofNullable(sort);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemDataSource#commit()
	 */
	@Override
	public void commit() {
		final CommitHandler<ITEM> handler = getCommitHandler()
				.orElseThrow(() -> new IllegalStateException("Missing CommitHandler"));
		List<ITEM> added = requireItemStore().getAddedItems().stream().collect(Collectors.toList());
		List<ITEM> modified = requireItemStore().getModifiedItems().stream().collect(Collectors.toList());
		List<ITEM> removed = requireItemStore().getRemovedItems().stream().collect(Collectors.toList());
		if (!added.isEmpty() || !modified.isEmpty() || !removed.isEmpty()) {
			final List<ITEM> addedItemReversed = new ArrayList<>(added);
			Collections.reverse(addedItemReversed);
			handler.commit(addedItemReversed, modified, removed);
			// reset items store
			requireItemStore().reset(false, false);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemDataSource#discard()
	 */
	@Override
	public void discard() {
		requireItemStore().discard();
	}

	/**
	 * Default {@link Builder} implementation.
	 *
	 * @param <PROPERTY> Item property type
	 * @param <ITEM> Item type
	 */
	public static class DefaultItemDataSourceBuilder<ITEM, PROPERTY> implements ItemDataSource.Builder<ITEM, PROPERTY> {

		/**
		 * Container instance to build and setup
		 */
		protected final DefaultItemDataSource<ITEM, PROPERTY> instance;

		/**
		 * Container batch size
		 */
		protected int batchSize = ItemDataSource.DEFAULT_BATCH_SIZE;

		/**
		 * Constructor
		 * @param propertyType Property representation type (not null)
		 */
		public DefaultItemDataSourceBuilder(Class<?> propertyType) {
			super();
			this.instance = new DefaultItemDataSource<>(propertyType);
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.data.container.ItemDataSourceContainerBuilder#dataSource(com.holonplatform.vaadin.
		 * data .ItemDataProvider)
		 */
		@Override
		public Builder<ITEM, PROPERTY> dataSource(ItemDataProvider<ITEM> dataProvider) {
			instance.setDataProvider(dataProvider);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.data.container.ItemDataSourceContainerBuilder#itemIdentifier(com.holonplatform.
		 * vaadin. data.ItemIdentifierProvider)
		 */
		@Override
		public <ID> Builder<ITEM, PROPERTY> itemIdentifier(ItemIdentifierProvider<ITEM, ID> itemIdentifierProvider) {
			instance.setItemIdentifierProvider(itemIdentifierProvider);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.data.QueryContainerBuilder#autoRefresh(boolean)
		 */
		@Override
		public Builder<ITEM, PROPERTY> autoRefresh(boolean autoRefresh) {
			instance.setAutoRefresh(autoRefresh);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.data.QueryContainerBuilder#batchSize(int)
		 */
		@Override
		public Builder<ITEM, PROPERTY> batchSize(int batchSize) {
			this.batchSize = batchSize;
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.data.QueryContainerBuilder#maxCacheSize(int)
		 */
		@Override
		public Builder<ITEM, PROPERTY> maxCacheSize(int maxCacheSize) {
			instance.setMaxCacheSize(maxCacheSize);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.data.QueryContainerBuilder#defaultValue(java.lang.Object, java.lang.Object)
		 */
		@Override
		public Builder<ITEM, PROPERTY> defaultValue(PROPERTY propertyId, Object defaultValue) {
			instance.setPropertyDefaultValue(propertyId, defaultValue);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.data.ItemDataSource.Builder#withProperty(java.lang.Object, java.lang.Class,
		 * boolean)
		 */
		@Override
		public Builder<ITEM, PROPERTY> withProperty(PROPERTY property, Class<?> type, boolean generatePropertyId) {
			instance.addProperty(property, type, false, false, null, generatePropertyId);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.data.container.ItemDataSourceContainerBuilder#withSortableProperty(java.lang.Object,
		 * java.lang.Class)
		 */
		@Override
		public Builder<ITEM, PROPERTY> withSortableProperty(PROPERTY propertyId, Class<?> type) {
			instance.addProperty(propertyId, type, false, true, null, true);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.data.container.ItemDataSourceContainerBuilder#withReadOnlyProperty(java.lang.Object,
		 * java.lang.Class)
		 */
		@Override
		public Builder<ITEM, PROPERTY> withReadOnlyProperty(PROPERTY propertyId, Class<?> type) {
			instance.addProperty(propertyId, type, true, false, null, true);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.data.container.ItemDataSourceContainerBuilder#withReadOnlySortableProperty(java.
		 * lang. Object, java.lang.Class)
		 */
		@Override
		public Builder<ITEM, PROPERTY> withReadOnlySortableProperty(PROPERTY propertyId, Class<?> type) {
			instance.addProperty(propertyId, type, true, true, null, true);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.data.ItemDataSource.Builder#propertyId(java.lang.Object, java.lang.String)
		 */
		@Override
		public Builder<ITEM, PROPERTY> propertyId(PROPERTY property, String propertyId) {
			instance.setPropertyId(property, propertyId);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.data.ItemDataSource.Builder#sortable(boolean)
		 */
		@Override
		public Builder<ITEM, PROPERTY> sortable(boolean sortable) {
			instance.getProperties().forEach(p -> instance.setPropertySortable(p, sortable));
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.data.container.ItemDataSourceContainerBuilder#sortable(java.lang.Object,
		 * boolean)
		 */
		@Override
		public Builder<ITEM, PROPERTY> sortable(PROPERTY propertyId, boolean sortable) {
			instance.setPropertySortable(propertyId, sortable);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.data.container.ItemDataSourceContainerBuilder#readOnly(java.lang.Object,
		 * boolean)
		 */
		@Override
		public Builder<ITEM, PROPERTY> readOnly(PROPERTY propertyId, boolean readOnly) {
			instance.setPropertyReadOnly(propertyId, readOnly);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.data.container.ItemDataSourceContainerBuilder#withPropertySortGenerator(java.lang.
		 * Object, com.holonplatform.vaadin.data.ItemDataSource.PropertySortGenerator)
		 */
		@Override
		public Builder<ITEM, PROPERTY> withPropertySortGenerator(PROPERTY property,
				PropertySortGenerator<PROPERTY> propertySortGenerator) {
			instance.setPropertySortGenerator(property, propertySortGenerator);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.data.container.ItemDataSourceContainerBuilder#withQueryConfigurationProvider(com.
		 * holonframework.core.query.QueryConfigurationProvider)
		 */
		@Override
		public Builder<ITEM, PROPERTY> withQueryConfigurationProvider(
				QueryConfigurationProvider queryConfigurationProvider) {
			instance.addQueryConfigurationProvider(queryConfigurationProvider);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.data.container.ItemDataSourceContainerBuilder#fixedFilter(com.holonplatform.core.
		 * query .QueryFilter)
		 */
		@Override
		public Builder<ITEM, PROPERTY> fixedFilter(QueryFilter filter) {
			instance.setFixedFilter(filter);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.data.container.ItemDataSourceContainerBuilder#fixedSort(com.holonplatform.core.
		 * query. QuerySort)
		 */
		@Override
		public Builder<ITEM, PROPERTY> fixedSort(QuerySort sort) {
			instance.setFixedSort(sort);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.data.container.ItemDataSourceContainerBuilder#defaultSort(com.holonplatform.core.
		 * query .QuerySort)
		 */
		@Override
		public Builder<ITEM, PROPERTY> defaultSort(QuerySort sort) {
			instance.setDefaultSort(sort);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.data.container.ItemDataSourceContainerBuilder#queryParameter(java.lang.String,
		 * java.lang.Object)
		 */
		@Override
		public Builder<ITEM, PROPERTY> queryParameter(String name, Object value) {
			instance.addQueryParameter(name, value);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.data.container.ItemDataSourceContainerBuilder#commitHandler(com.holonplatform.
		 * vaadin. data.ItemDataSource.CommitHandler)
		 */
		@Override
		public Builder<ITEM, PROPERTY> commitHandler(CommitHandler<ITEM> commitHandler) {
			instance.setCommitHandler(commitHandler);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.data.QueryContainerBuilder#build()
		 */
		@Override
		public ItemDataSource<ITEM, PROPERTY> build() {
			// init container
			instance.init(batchSize);
			return instance;
		}

	}

}
