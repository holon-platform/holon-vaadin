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

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.holonplatform.core.ParameterSet;
import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.exceptions.DataAccessException;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.query.QueryConfigurationProvider;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.core.query.QuerySort;
import com.holonplatform.vaadin.internal.data.DatastoreCommitHandler;
import com.holonplatform.vaadin.internal.data.DefaultItemDataSource;
import com.holonplatform.vaadin.internal.data.DefaultItemSort;
import com.vaadin.data.provider.Query;

/**
 * Represents an item set data source, providing operations to retrieve items by id and change the item set composition
 * adding a removing items.
 * <p>
 * Items values are accessed using a defined set of properties.
 * </p>
 * <p>
 * Item set modifications can be persisted in a backend data store using a {@link CommitHandler}, triggered through the
 * {@link #commit()} method.
 * </p>
 * 
 * @param <ITEM> Item type
 * @param <PROPERTY> Item property type
 * 
 * @since 5.0.0
 * 
 * @see ItemDataProvider
 */
public interface ItemDataSource<ITEM, PROPERTY> extends QueryConfigurationProviderSupport, Serializable {

	/**
	 * Default batch (page) size for items loading using {@link ItemDataProvider}
	 */
	public static final int DEFAULT_BATCH_SIZE = 50;

	/**
	 * Get the data source configuration.
	 * @return Data source configuration (never null)
	 */
	Configuration<ITEM, PROPERTY> getConfiguration();

	/**
	 * Refresh data source items.
	 * @throws DataAccessException Error performing concrete items loading operations
	 */
	void refresh();

	/**
	 * Clear data source contents (current item set)
	 */
	void clear();

	/**
	 * Returns current data source size (considering any applied filter), i.e. the total number of available items.
	 * @return the total number of available items in data source
	 */
	int size();

	/**
	 * Get all item ids available from this data source
	 * @return Item ids, empty if none
	 */
	Collection<?> getItemIds();

	/**
	 * Get the identifier of given item
	 * @param item Item to get the identifier for
	 * @return Item identifier
	 */
	Object getId(ITEM item);

	/**
	 * Get index of given item
	 * @param item Item
	 * @return Item index, or <code>-1</code> if not found
	 */
	int indexOfItem(ITEM item);

	/**
	 * Get the item at given index.
	 * @param index Item index
	 * @return The itemat given index, or <code>null</code> if not available
	 */
	ITEM getItemAt(int index);

	/**
	 * Get the item identified by given <code>itemId</code>.
	 * @param itemId Item id (not null)
	 * @return Optional item identified by given <code>itemId</code>, empty if not found
	 */
	Optional<ITEM> get(Object itemId);

	/**
	 * Adds an item to the data source.
	 * @param item The item to add (not null)
	 * @return Id of the added item
	 */
	Object add(ITEM item);

	/**
	 * Update given item in data source.
	 * @param item Item to update (not null)
	 */
	void update(ITEM item);

	/**
	 * Removes given item from the data source.
	 * @param item Item to remove (not null)
	 * @return <code>true</code> if the item was successfully removed from data source
	 */
	boolean remove(ITEM item);

	/**
	 * Refresh given item in data source
	 * @param item Item to refresh (not null)
	 * @throws UnsupportedOperationException If the refresh operation is not supported by concrete data store
	 */
	void refresh(ITEM item);

	/**
	 * Updates all changes since the previous commit.
	 */
	void commit();

	/**
	 * Discards all changes since last commit.
	 */
	void discard();

	/**
	 * Sort the data source items using given {@link ItemSort} directives.
	 * @param sorts Item sorts to apply
	 */
	@SuppressWarnings("unchecked")
	void sort(ItemSort<PROPERTY>... sorts);

	/**
	 * Set the data provider {@link Query} to use to obtain filters and sorts.
	 * @param dataProviderQuery the query to set
	 */
	void setDataProviderQuery(Query<ITEM, QueryFilter> dataProviderQuery);

	/**
	 * Item actions enumeration.
	 */
	public enum ItemAction {

		/**
		 * An item was loaded
		 */
		LOADED,

		/**
		 * An item was added
		 */
		ADDED,

		/**
		 * An item was modified
		 */
		MODIFIED,

		/**
		 * An item was modified
		 */
		REFRESHED,

		/**
		 * An item was removed
		 */
		REMOVED,

		/**
		 * The item set changed
		 */
		SET_CHANGED;

	}

	/**
	 * Item data source configuration.
	 * 
	 * @param <ITEM> Item type
	 * @param <PROPERTY> Item property type
	 */
	public interface Configuration<ITEM, PROPERTY> extends Serializable {

		/**
		 * Get the property representation type.
		 * @return Property type
		 */
		Class<?> getPropertyType();

		/**
		 * Get the {@link ItemIdentifierProvider}.
		 * @return the item identifier provider
		 */
		Optional<ItemIdentifierProvider<ITEM, ?>> getItemIdentifierProvider();

		/**
		 * Get the item data provider.
		 * @return the item data provider
		 */
		Optional<ItemDataProvider<ITEM>> getDataProvider();

		/**
		 * Get the commit handler
		 * @return the commit handler
		 */
		Optional<CommitHandler<ITEM>> getCommitHandler();

		/**
		 * Get available item properties.
		 * @return Available item properties iterable
		 */
		Iterable<PROPERTY> getProperties();

		/**
		 * Gets the data type of the given data source property.
		 * @param property Property (not null)
		 * @return Property data type
		 */
		Class<?> getPropertyType(PROPERTY property);

		/**
		 * Get the String id associated to given property, if available.
		 * @param property Property (not null)
		 * @return Optional property id
		 */
		Optional<String> getPropertyId(PROPERTY property);

		/**
		 * Get the property which corresponds to given id, if available.
		 * @param propertyId Property id
		 * @return Optional property which corresponds to given id
		 */
		Optional<PROPERTY> getPropertyById(String propertyId);

		/**
		 * Get whether the given property is read only.
		 * @param property Property (not null)
		 * @return <code>true</code> if property is read only, <code>false</code> otherwise
		 */
		boolean isPropertyReadOnly(PROPERTY property);

		/**
		 * Get whether the given property is sortable.
		 * @param property Property (not null)
		 * @return <code>true</code> if property is sortable, <code>false</code> otherwise
		 */
		boolean isPropertySortable(PROPERTY property);

		/**
		 * Get the {@link PropertySortGenerator} bound to given property, if any.
		 * @param property Property (not null)
		 * @return Optional property sort generator
		 */
		Optional<PropertySortGenerator<PROPERTY>> getPropertySortGenerator(PROPERTY property);

		/**
		 * Get the default value for the given property.
		 * @param property Property (not null)
		 * @return Property default value, or <code>null</code> if not available
		 */
		Object getPropertyDefaultValue(PROPERTY property);

		/**
		 * Returns whether auto-refresh is enabled for this data source, i.e. items are loaded when one of the data
		 * source methods which involve operations on item set is called.
		 * <p>
		 * If auto-refresh is not enabled, {@link #refresh()} method must be called to load items.
		 * </p>
		 * <p>
		 * Default is <code>true</code>.
		 * </p>
		 * @return Whether auto-refresh is enabled for this data source
		 */
		boolean isAutoRefresh();

		/**
		 * Get current data source items sorting directives properties, providing item property to sort and direction.
		 * @return Data source items sorting directives, or an empty list if none
		 */
		List<ItemSort<PROPERTY>> getItemSorts();

		/**
		 * Get the {@link QuerySort} according to this configuration, taking into account the item sorts, default and
		 * fixed sorts and the query sorts from any registered {@link QueryConfigurationProvider}.
		 * @param currentSorts Optional corrent query sorts
		 * @return The {@link QuerySort}, if available
		 */
		Optional<QuerySort> getQuerySort(Collection<QuerySort> currentSorts);

		/**
		 * Get the {@link QueryFilter} according to this configuration, taking into account the fixed filters and the
		 * query filters from any registered {@link QueryConfigurationProvider}.
		 * @return The {@link QueryFilter}, if available
		 */
		Optional<QueryFilter> getQueryFilter();

		/**
		 * Get the query parameters according to this configuration, taking into account the query parameters from any
		 * registered {@link QueryConfigurationProvider}.
		 * @return The query parameters set, empty if no parameters available
		 */
		ParameterSet getQueryParameters();

	}

	/**
	 * Item sort directive.
	 * 
	 * @param <PROPERTY> Item property type
	 */
	public interface ItemSort<PROPERTY> extends Serializable {

		/**
		 * Item property to sort
		 * @return Item property to sort
		 */
		PROPERTY getProperty();

		/**
		 * Sort direction
		 * @return <code>true</code> if ascending, <code>false</code> if descending
		 */
		boolean isAscending();

		/**
		 * Create an {@link ItemSort} using given property and sort direction.
		 * @param <PROPERTY> Property type
		 * @param property Property to sort (not null)
		 * @param ascending <code>true</code> to sort ascending, <code>false</code> for descending
		 * @return Item sort
		 */
		static <PROPERTY> ItemSort<PROPERTY> of(PROPERTY property, boolean ascending) {
			ObjectUtils.argumentNotNull(property, "Sort property must be not null");
			return new DefaultItemSort<>(property, ascending);
		}

		/**
		 * Create an ascending {@link ItemSort} using given property.
		 * @param <PROPERTY> Property type
		 * @param property Property to sort (not null)
		 * @return Item sort
		 */
		static <PROPERTY> ItemSort<PROPERTY> asc(PROPERTY property) {
			return of(property, true);
		}

		/**
		 * Create an descending {@link ItemSort} using given property.
		 * @param <PROPERTY> Property type
		 * @param property Property to sort (not null)
		 * @return Item sort
		 */
		static <PROPERTY> ItemSort<PROPERTY> desc(PROPERTY property) {
			return of(property, false);
		}

	}

	/**
	 * {@link QuerySort} generator to provide query sorts for an item property.
	 */
	@FunctionalInterface
	public interface PropertySortGenerator<PROPERTY> extends Serializable {

		/**
		 * Get the {@link QuerySort} to use for given <code>property</code> with specified sort direction.
		 * @param property Property to sort
		 * @param ascending Sort direction
		 * @return QuerySort
		 */
		QuerySort getQuerySort(PROPERTY property, boolean ascending);

	}

	/**
	 * Data source commit handler to perform concrete persistence operations when data is committed invoking
	 * {@link ItemDataSource#commit()}.
	 * @param <ITEM> Item type
	 */
	@FunctionalInterface
	public interface CommitHandler<ITEM> extends Serializable {

		/**
		 * Commit item modifications.
		 * @param addedItems Added items: an empty collection if none
		 * @param modifiedItems Modified items: an empty collection if none
		 * @param removedItems Removed items: an empty collection if none
		 */
		void commit(Collection<ITEM> addedItems, Collection<ITEM> modifiedItems, Collection<ITEM> removedItems);

		/**
		 * Construct a new {@link CommitHandler} for {@link PropertyBox} type items using a {@link Datastore} to perform
		 * persistence operations.
		 * @param datastore The datastore to use (not null)
		 * @param target The data target to use (not null)
		 */
		static CommitHandler<PropertyBox> datastore(Datastore datastore, DataTarget<?> target) {
			return new DatastoreCommitHandler(datastore, target);
		}

	}

	// Builders

	/**
	 * Get a builder to create an {@link ItemDataSource}.
	 * @param <ITEM> Item type
	 * @param <PROPERTY> Item property type
	 * @param propertyType Property representation type (not null)
	 * @return {@link ItemDataSource} builder
	 */
	static <ITEM, PROPERTY> Builder<ITEM, PROPERTY> builder(Class<?> propertyType) {
		return new DefaultItemDataSource.DefaultItemDataSourceBuilder<>(propertyType);
	}

	/**
	 * Builder to create {@link ItemDataSource} instances.
	 * @param <PROPERTY> Item property type
	 * @param <ITEM> Item type
	 */
	public interface Builder<ITEM, PROPERTY> {

		/**
		 * Set the items data provider.
		 * @param dataProvider The items data provider to set
		 * @return this
		 */
		Builder<ITEM, PROPERTY> dataSource(ItemDataProvider<ITEM> dataProvider);

		/**
		 * Set the item identifier provider to use to obtain item ids.
		 * @param <ID> Item id type
		 * @param itemIdentifierProvider the item identifier provider to set
		 * @return this
		 */
		<ID> Builder<ITEM, PROPERTY> itemIdentifier(ItemIdentifierProvider<ITEM, ID> itemIdentifierProvider);

		/**
		 * Add an Item property to this data source.
		 * @param propertyId Property to add (not null)
		 * @param type Property value type (not null)
		 * @param generatePropertyId Whether to auto generate a property id as String
		 * @return this
		 */
		Builder<ITEM, PROPERTY> withProperty(PROPERTY property, Class<?> type, boolean generatePropertyId);

		/**
		 * Add an Item property to this data source.
		 * @param propertyId Property to add (not null)
		 * @param type Property value type (not null)
		 * @return this
		 */
		default Builder<ITEM, PROPERTY> withProperty(PROPERTY property, Class<?> type) {
			return withProperty(property, type, true);
		}

		/**
		 * Add an Item property to this data source and declares it as sortable.
		 * @param property Property to add (not null)
		 * @param type Property value type (not null)
		 * @return this
		 */
		Builder<ITEM, PROPERTY> withSortableProperty(PROPERTY property, Class<?> type);

		/**
		 * Add an Item property to this data source and declares it as read-only.
		 * @param property Property to add (not null)
		 * @param type Property value type (not null)
		 * @return this
		 */
		Builder<ITEM, PROPERTY> withReadOnlyProperty(PROPERTY property, Class<?> type);

		/**
		 * Add an Item property to this data source and declares it as read-only and sortable.
		 * @param property Property to add (not null)
		 * @param type Property value type (not null)
		 * @return this
		 */
		Builder<ITEM, PROPERTY> withReadOnlySortableProperty(PROPERTY property, Class<?> type);

		/**
		 * Set the property String id of given <code>property</code>.
		 * @param property Property (not null)
		 * @param propertyId Property id
		 * @return this
		 */
		Builder<ITEM, PROPERTY> propertyId(PROPERTY property, String propertyId);

		/**
		 * Set if auto-refresh is enabled for this container, i.e. items are loaded when one of the Container method
		 * which involve operations on item set is called.
		 * <p>
		 * If auto-refresh is not enabled, {@link ItemDataSource#refresh()} method must be called to load items before
		 * using this Container.
		 * </p>
		 * @param autoRefresh <code>true</code> to enable auto-refresh
		 * @return this
		 */
		Builder<ITEM, PROPERTY> autoRefresh(boolean autoRefresh);

		/**
		 * Set batch (page) size for items loading using {@link ItemDataProvider}.
		 * <p>
		 * A value <code>&lt;=0</code> means no paging, and {@link ItemDataProvider} should behave in a consistent
		 * manner.
		 * </p>
		 * @param batchSize Batch (page) size for items loading
		 * @return this
		 */
		Builder<ITEM, PROPERTY> batchSize(int batchSize);

		/**
		 * Set max items cache size
		 * @param maxCacheSize Max cache size to set
		 * @return this
		 */
		Builder<ITEM, PROPERTY> maxCacheSize(int maxCacheSize);

		/**
		 * Set whether all the properties are sortable.
		 * <p>
		 * NOTE: It only applies to already added properties.
		 * </p>
		 * @param sortable Whether all the properties are sortable
		 * @return this
		 */
		Builder<ITEM, PROPERTY> sortable(boolean sortable);

		/**
		 * Set whether given property id is sortable.
		 * @param propertyId Property id
		 * @param sortable Whether given property id is sortable
		 * @return this
		 */
		Builder<ITEM, PROPERTY> sortable(PROPERTY propertyId, boolean sortable);

		/**
		 * Set whether given property id is read-only.
		 * @param propertyId Property id
		 * @param readOnly Whether given property id is read-only
		 * @return this
		 */
		Builder<ITEM, PROPERTY> readOnly(PROPERTY propertyId, boolean readOnly);

		/**
		 * Set a default value to initialize the given <code>propertyId</code>
		 * @param propertyId Property id
		 * @param defaultValue Default value
		 * @return this
		 */
		Builder<ITEM, PROPERTY> defaultValue(PROPERTY propertyId, Object defaultValue);

		/**
		 * Set a {@link PropertySortGenerator} to generate {@link QuerySort}s for given <code>property</code>
		 * @param property Property (not null)
		 * @param propertySortGenerator PropertySortGenerator (not null)
		 * @return this
		 */
		Builder<ITEM, PROPERTY> withPropertySortGenerator(PROPERTY property,
				PropertySortGenerator<PROPERTY> propertySortGenerator);

		/**
		 * Add an external {@link QueryConfigurationProvider} for additional query configuration
		 * @param queryConfigurationProvider QueryConfigurationProvider to add
		 * @return this
		 */
		Builder<ITEM, PROPERTY> withQueryConfigurationProvider(QueryConfigurationProvider queryConfigurationProvider);

		/**
		 * Set query fixed filter (always added to query predicates)
		 * @param filter Query fixed filter, or <code>null</code> for none
		 * @return this
		 */
		Builder<ITEM, PROPERTY> fixedFilter(QueryFilter filter);

		/**
		 * Set query fixed sort (always added to query sorts)
		 * @param sort Query fixed sort, or <code>null</code> for none
		 * @return this
		 */
		Builder<ITEM, PROPERTY> fixedSort(QuerySort sort);

		/**
		 * Set query default sort. If not <code>null</code> and no other sort is available, this one will be used
		 * @param sort Default query sort
		 * @return this
		 */
		Builder<ITEM, PROPERTY> defaultSort(QuerySort sort);

		/**
		 * Add a query parameter
		 * @param name Parameter name
		 * @param value Parameter value
		 * @return this
		 */
		Builder<ITEM, PROPERTY> queryParameter(String name, Object value);

		/**
		 * Set the handler to manage item set modifications.
		 * <p>
		 * This is required to activate item set modification support.
		 * </p>
		 * @param commitHandler Item commit handler
		 * @return this
		 */
		Builder<ITEM, PROPERTY> commitHandler(CommitHandler<ITEM> commitHandler);

		/**
		 * Build {@link ItemDataSource} instance
		 * @return ItemDataSource instance
		 */
		ItemDataSource<ITEM, PROPERTY> build();

	}

}
