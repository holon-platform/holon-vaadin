/*
 * Copyright 2000-2016 Holon TDCN.
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

import com.holonplatform.core.exceptions.DataAccessException;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.query.QueryConfigurationProvider;
import com.holonplatform.core.query.QuerySort;
import com.holonplatform.vaadin.internal.data.DefaultItemSort;

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
public interface ItemDataSource<ITEM, PROPERTY> extends Serializable {

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

	// Data source configuration

	public interface Configuration<ITEM, PROPERTY> extends QueryConfigurationProvider, Serializable {

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

	}

	/**
	 * An item sort directive.
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

	}

}
