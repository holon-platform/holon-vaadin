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

import com.holonplatform.core.ParameterSet;
import com.holonplatform.core.Path;
import com.holonplatform.core.exceptions.DataAccessException;
import com.holonplatform.core.internal.DefaultParameterSet;
import com.holonplatform.core.internal.utils.ObjectUtils;
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
	 * Property ids
	 */
	private List<PROPERTY> propertyIds = new LinkedList<>();

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
	private Collection<PROPERTY> readOnlyPropertyIds = new LinkedList<>();
	/**
	 * Sortable properties
	 */
	private final Collection<PROPERTY> sortablePropertyIds = new LinkedList<>();

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
	private List<QueryConfigurationProvider> queryConfigurationProviders;

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
				final List<QuerySort> sorts = new LinkedList<>();
				List<QuerySortOrder> orders = getDataProviderQuery().map(q -> q.getSortOrders()).orElse(null);
				if (orders != null && !orders.isEmpty()) {
					for (QuerySortOrder order : orders) {
						QuerySort sort = fromOrder(order, getProperties());
						if (sort != null) {
							sorts.add(sort);
						}
					}
				}
				if (!sorts.isEmpty()) {
					if (sorts.size() == 1) {
						return sorts.get(0);
					} else {
						return QuerySort.of(sorts);
					}
				}
				return null;
			}

		});
	}

	/**
	 * Init the data source container, configuring the internal {@link ItemStore}.
	 * @param batchSize the batch size to use
	 */
	protected void init(int batchSize) {
		this.itemStore = new DefaultItemStore<>(this,
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

	/* (non-Javadoc)
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
	 * Calculate max item store cache size using batch size, if positive or default
	 * 
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
		return propertyIds;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.querycontainer.ItemQueryDefinition# isPropertyReadOnly(java.lang.Object)
	 */
	@Override
	public boolean isPropertyReadOnly(Object propertyId) {
		return (propertyId != null && readOnlyPropertyIds.contains(propertyId));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.querycontainer.ItemQueryDefinition# isPropertySortable(java.lang.Object)
	 */
	@Override
	public boolean isPropertySortable(Object propertyId) {
		return (propertyId != null && sortablePropertyIds.contains(propertyId));
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

	public <T> boolean addContainerProperty(PROPERTY propertyId, Class<T> type, boolean readOnly, boolean sortable,
			T defaultValue) {
		if (propertyId != null) {
			// remove any previous property with same id
			if (propertyIds.contains(propertyId)) {
				propertyIds.remove(propertyId);
			}
			propertyIds.add(propertyId);
			Class<?> pt = (type != null) ? type : Object.class;
			propertyTypes.put(propertyId, pt);
			if (readOnly) {
				readOnlyPropertyIds.add(propertyId);
			}
			if (sortable) {
				sortablePropertyIds.add(propertyId);
			}
			if (defaultValue != null) {
				if (defaultValues == null) {
					defaultValues = new HashMap<>();
				}
				defaultValues.put(propertyId, defaultValue);
			}
			return true;
		}
		return false;
	}

	public boolean addContainerProperty(PROPERTY propertyId, Class<?> type, boolean readOnly, boolean sortable) {
		return addContainerProperty(propertyId, type, readOnly, sortable, null);
	}

	public void setPropertySortable(PROPERTY propertyId, boolean sortable) {
		if (propertyId != null) {
			if (sortable) {
				if (!sortablePropertyIds.contains(propertyId)) {
					sortablePropertyIds.add(propertyId);
				}
			} else {
				sortablePropertyIds.remove(propertyId);
			}
		}
	}

	public void setPropertyReadOnly(PROPERTY propertyId, boolean readOnly) {
		if (propertyId != null) {
			if (readOnly) {
				if (!readOnlyPropertyIds.contains(propertyId)) {
					readOnlyPropertyIds.add(propertyId);
				}
			} else {
				readOnlyPropertyIds.remove(propertyId);
			}
		}
	}

	public void setPropertyDefaultValue(PROPERTY propertyId, Object defaultValue) {
		if (propertyId != null) {
			if (defaultValues == null) {
				defaultValues = new HashMap<>();
			}
			defaultValues.put(propertyId, defaultValue);
		}
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
	 * @Override public boolean removeContainerProperty(Object propertyId) throws UnsupportedOperationException { if
	 * (propertyId != null && propertyIds.contains(propertyId)) { propertyIds.remove(propertyId); if
	 * (readOnlyPropertyIds.contains(propertyId)) { readOnlyPropertyIds.remove(propertyId); } if
	 * (sortablePropertyIds.contains(propertyId)) { sortablePropertyIds.remove(propertyId); } if (defaultValues != null
	 * && defaultValues.containsKey(propertyId)) { defaultValues.remove(propertyId); } // event
	 * notifyPropertySetChanged(); return true; } return false; }
	 */

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

	public boolean containsId(Object itemId) {
		return requireItemStore().containsItem(itemId);
	}

	@Override
	public Optional<CommitHandler<ITEM>> getCommitHandler() {
		return Optional.ofNullable(commitHandler);
	}

	public void setCommitHandler(CommitHandler<ITEM> commitHandler) {
		this.commitHandler = commitHandler;
	}

	public Registration addQueryConfigurationProvider(QueryConfigurationProvider queryConfigurationProvider) {
		ObjectUtils.argumentNotNull(queryConfigurationProvider, "QueryConfigurationProvider must be not null");
		if (queryConfigurationProviders == null) {
			queryConfigurationProviders = new LinkedList<>();
		}
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

	public void setFixedFilter(QueryFilter filter) {
		this.fixedFilter = filter;
		// reset store
		resetStorePreservingFreezeState();
	}

	public void setFixedSort(QuerySort sort) {
		this.fixedSort = sort;
		// reset store
		resetStorePreservingFreezeState();
	}

	public void setDefaultSort(QuerySort sort) {
		this.defaultSort = sort;
		// reset store
		resetStorePreservingFreezeState();
	}

	public void addQueryParameter(String name, Object value) {
		queryParameters.addParameter(name, value);
		// reset store
		resetStorePreservingFreezeState();
	}

	public void removeQueryParameter(String name) {
		queryParameters.removeParameter(name);
		// reset store
		resetStorePreservingFreezeState();
	}

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
		return null;
	}

	/**
	 * Additional QueryConfigurationProvider
	 * @return QueryConfigurationProviders, or <code>null</code> if none
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

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.query.QueryConfigurationProvider#getQueryFilter()
	 */
	@Override
	public QueryFilter getQueryFilter() {
		final LinkedList<QueryFilter> filters = new LinkedList<>();

		// fixed
		QueryFilter fixed = getFixedFilter();
		if (fixed != null) {
			filters.add(fixed);
		}

		// externally provided
		if (getQueryConfigurationProviders() != null) {
			for (QueryConfigurationProvider provider : getQueryConfigurationProviders()) {
				QueryFilter filter = provider.getQueryFilter();
				if (filter != null) {
					filters.add(filter);
				}
			}
		}

		// return overall filter
		return QueryFilter.allOf(filters).orElse(null);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.query.QueryConfigurationProvider#getQuerySort()
	 */
	@Override
	public QuerySort getQuerySort() {
		LinkedList<QuerySort> sorts = new LinkedList<>();

		// sorts
		List<ItemSort<PROPERTY>> itemSorts = getItemSorts();
		if (!itemSorts.isEmpty()) {

			// item sorts
			for (ItemSort<PROPERTY> itemSort : itemSorts) {
				// sort property
				PROPERTY sortId = itemSort.getProperty();
				// check delegate
				Optional<PropertySortGenerator<PROPERTY>> generator = getPropertySortGenerator(sortId);
				if (generator.isPresent()) {
					QuerySort sort = generator.get().getQuerySort(sortId, itemSort.isAscending());
					if (sort != null) {
						sorts.add(sort);
					}
				} else {
					getPropertyPath(sortId, getProperties()).ifPresent(p -> {
						sorts.add(QuerySort.of(p,
								itemSort.isAscending() ? SortDirection.ASCENDING : SortDirection.DESCENDING));
					});
				}
			}

		} else {

			// externally provided
			if (getQueryConfigurationProviders() != null) {
				for (QueryConfigurationProvider provider : getQueryConfigurationProviders()) {
					QuerySort sort = provider.getQuerySort();
					if (sort != null) {
						sorts.add(sort);
					}
				}
			}

		}

		// default sort
		if (sorts.isEmpty()) {
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

		if (!sorts.isEmpty()) {
			if (sorts.size() == 1) {
				return sorts.getFirst();
			}
			return QuerySort.of(sorts);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.query.QueryConfigurationProvider#getQueryParameters()
	 */
	@Override
	public ParameterSet getQueryParameters() {
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

	public static Optional<Path<?>> getPropertyPath(Object propertyId, Iterable<?> properties) {
		if (propertyId != null) {
			if (propertyId instanceof Path) {
				return Optional.of((Path<?>) propertyId);
			} else {
				Path<?> property = getPathByName(propertyId.toString(), properties);
				if (property != null) {
					return Optional.of(property);
				}
			}
		}
		return Optional.empty();
	}

	private static Path<?> getPathByName(String propertyName, Iterable<?> properties) {
		if (propertyName != null && properties != null) {
			for (Object property : properties) {
				if (property instanceof Path && propertyName.equals(((Path<?>) property).getName())) {
					return (Path<?>) property;
				}
			}
		}
		return null;
	}

	private static QuerySort fromOrder(QuerySortOrder order, Iterable<?> properties) {
		return getPropertyPath(order.getSorted(), properties).map(path -> QuerySort.of(path,
				(order.getDirection() != null
						&& order.getDirection() == com.vaadin.shared.data.sort.SortDirection.DESCENDING)
								? SortDirection.DESCENDING : SortDirection.ASCENDING))
				.orElse(null);
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
		 * @see com.holonplatform.vaadin.data.container.ItemDataSourceContainerBuilder#withProperty(java.lang.Object,
		 * java.lang.Class)
		 */
		@Override
		public Builder<ITEM, PROPERTY> withProperty(PROPERTY propertyId, Class<?> type) {
			instance.addContainerProperty(propertyId, type, false, false);
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
			instance.addContainerProperty(propertyId, type, false, true);
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
			instance.addContainerProperty(propertyId, type, true, false);
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
			instance.addContainerProperty(propertyId, type, true, true);
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
