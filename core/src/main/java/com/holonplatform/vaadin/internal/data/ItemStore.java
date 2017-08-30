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
package com.holonplatform.vaadin.internal.data;

import java.io.Serializable;
import java.util.List;

import com.holonplatform.vaadin.data.ItemDataSource.ItemAction;

/**
 * Data source items store with caching capability.
 * 
 * @since 5.0.0
 */
public interface ItemStore<ITEM> extends Serializable {

	/**
	 * Default items cache max size
	 */
	public static final int DEFAULT_MAX_CACHE_SIZE = 1000;

	/**
	 * Listener for actions performed on store items
	 * @param <ITEM> Item type
	 */
	public interface ItemActionListener<ITEM> {

		/**
		 * An action was performed on <code>item</code>
		 * @param item Item on which action occured
		 * @param itemId Item id
		 * @param previous Previous item (only not null for refresh actions)
		 * @param action Action type
		 */
		void onItemAction(ITEM item, Object itemId, ITEM previous, ItemAction action);

	}

	/**
	 * Max items cache size
	 * @return Max cache size
	 */
	int getMaxCacheSize();

	/**
	 * Set max items cache size
	 * @param maxCacheSize Max cache size to set
	 */
	void setMaxCacheSize(int maxCacheSize);

	/**
	 * Returns the number of items currently available in this strore
	 * @return Number of available items
	 */
	int size();

	/**
	 * List of item ids currently present in this store
	 * @return List of item ids, an empty List in no item is present in store
	 */
	List<?> getItemIds();

	/**
	 * Get the identifier of given item
	 * @param item Item to get the identifier for
	 * @return Item identifier
	 */
	Object getItemId(ITEM item);

	/**
	 * Gets the item at the given index
	 * @param index The index of the item
	 * @return The item identified by the index, or <code>null</code> if not found
	 */
	ITEM getItem(int index);

	/**
	 * Adds the given item to the end of item set contained in this store
	 * @param item The item to add
	 * @return The id of added item
	 */
	Object addItem(ITEM item);

	/**
	 * Get index of given item id
	 * @param itemId Item id
	 * @return Item index, or <code>-1</code> if not found
	 */
	int indexOfItem(Object itemId);

	/**
	 * Check if given item id is present in store
	 * @param itemId Item id
	 * @return <code>true</code> if store contains given item id
	 */
	boolean containsItem(Object itemId);

	/**
	 * Refresh given item
	 * @param item Item to refresh (not null)
	 */
	void refreshItem(ITEM item);

	/**
	 * Removes item at given index.
	 * @param index Index of the Item to be removed
	 * @return <code>true</code> if item was found and removed
	 */
	boolean removeItem(int index);

	/**
	 * Removes given item.
	 * @param item Item to remove
	 * @return <code>true</code> if item was found and removed
	 */
	boolean removeItem(ITEM item);

	/**
	 * Set as modified the given item
	 * @param item Item
	 */
	void setItemModified(ITEM item);

	/**
	 * Check if any item contained in this store contains modifications
	 * @return true if some item has been modified
	 */
	boolean isModified();

	/**
	 * Get added buffered items
	 * @return List of added items, or an empty List of none
	 */
	List<ITEM> getAddedItems();

	/**
	 * Get modified buffered items
	 * @return List of modified items, or an empty List of none
	 */
	List<ITEM> getModifiedItems();

	/**
	 * Get removed buffered items
	 * @return List of removed items, or an empty List of none
	 */
	List<ITEM> getRemovedItems();

	/**
	 * Discard any item modification
	 */
	void discard();

	/**
	 * Clear store contents and reset store state.
	 * <p>
	 * This includes to set the item store as not <em>freezed</em>.
	 * </p>
	 * @param fireListeners Whether to fire item action listeners
	 * @param freeze Whether to <em>freeze</em> the item store
	 */
	void reset(boolean fireListeners, boolean freeze);

	/**
	 * Set the item store as <em>freezed</em>. When the store is freezed, it behaves as if contains no items
	 * @param freezed <code>true</code> to freeze the store
	 */
	void setFreezed(boolean freezed);

	/**
	 * Gets whether the item store is <em>freezed</em>, i.e. it behaves as if contains no items
	 * @return <code>true</code> if the store is freezed
	 */
	boolean isFreezed();

	/**
	 * Add an item action listener.
	 * @param listener Listener to add (not null)
	 */
	void addItemActionListener(ItemActionListener<ITEM> listener);

	/**
	 * Remove an item action listener.
	 * @param listener Listener to remove (not null)
	 */
	void removeItemActionListener(ItemActionListener<ITEM> listener);

}
