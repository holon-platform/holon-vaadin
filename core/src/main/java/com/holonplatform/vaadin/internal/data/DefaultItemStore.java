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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.query.QueryConfigurationProvider;
import com.holonplatform.vaadin.data.ItemDataProvider;
import com.holonplatform.vaadin.data.ItemDataSource.ItemAction;
import com.holonplatform.vaadin.data.ItemIdentifierProvider;

/**
 * Default {@link ItemStore} implementation using {@link ItemDataProvider} to load items on demand.
 * 
 * @since 5.0.0
 */
public class DefaultItemStore<ITEM> implements ItemStore<ITEM> {

	private static final long serialVersionUID = 3190254710573117380L;

	/**
	 * Data source configuration
	 */
	private final QueryConfigurationProvider configuration;

	/**
	 * {@link ItemDataProvider} factory
	 */
	private final Supplier<ItemDataProvider<ITEM>> dataProviderFactory;

	/**
	 * Current query
	 */
	private ItemDataProvider<ITEM> query;

	/**
	 * Item identifier provider
	 */
	private final ItemIdentifierProvider<ITEM, ?> itemIdentifierProvider;

	/**
	 * Current query size
	 */
	private int querySize = -1;

	/**
	 * Item ids
	 */
	private List<?> itemIds;

	/**
	 * Items cache
	 */
	private ItemCacheMap<ITEM> itemCache;

	/**
	 * List of added items since last commit or discard
	 */
	private List<ITEM> addedItems;
	/**
	 * List of modified items since last commit or discard
	 */
	private List<ITEM> modifiedItems;
	/**
	 * List of deleted items since last commit or discard
	 */
	private List<ITEM> removedItems;

	/**
	 * Item action listeners
	 */
	private final List<ItemActionListener<ITEM>> itemActionListeners = new LinkedList<>();

	/**
	 * Freezed state
	 */
	private boolean freezed;
	
	/**
	 * Batch size
	 */
	private final int batchSize;

	/**
	 * Constructor
	 * @param configuration Data source configuration (not null)
	 * @param dataProvider Items provider (not null)
	 * @param itemIdentifierProvider Item identifier provider
	 * @param maxCacheSize Max cache size
	 */
	public DefaultItemStore(QueryConfigurationProvider configuration, ItemDataProvider<ITEM> dataProvider,
			ItemIdentifierProvider<ITEM, ?> itemIdentifierProvider, int batchSize, int maxCacheSize) {
		super();
		ObjectUtils.argumentNotNull(configuration, "Configuration must be not null");
		ObjectUtils.argumentNotNull(dataProvider, "ItemDataProvider must be not null");
		this.configuration = configuration;
		this.dataProviderFactory = () -> dataProvider;
		this.itemIdentifierProvider = itemIdentifierProvider;
		this.batchSize = batchSize;
		this.itemCache = new ItemCacheMap<>(maxCacheSize);
	}

	/**
	 * Get the data source configuration
	 * @return Data source configuration
	 */
	protected QueryConfigurationProvider getConfiguration() {
		return configuration;
	}

	/**
	 * Get the batch size
	 * @return the batch size
	 */
	public int getBatchSize() {
		return batchSize;
	}

	/**
	 * Get the items cache
	 * @return the items cache
	 */
	protected ItemCacheMap<ITEM> getItemCache() {
		return itemCache;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.internal.data.ItemStore#addItemActionListener(com.holonplatform.vaadin.internal.data.
	 * ItemStore.ItemActionListener)
	 */
	@Override
	public void addItemActionListener(ItemActionListener<ITEM> listener) {
		ObjectUtils.argumentNotNull(listener, "ItemActionListener must be not null");
		itemActionListeners.add(listener);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.data.ItemStore#removeItemActionListener(com.holonplatform.vaadin.internal.
	 * data.ItemStore.ItemActionListener)
	 */
	@Override
	public void removeItemActionListener(ItemActionListener<ITEM> listener) {
		ObjectUtils.argumentNotNull(listener, "ItemActionListener must be not null");
		itemActionListeners.remove(listener);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.internal.container.ItemStore#setFreezed(boolean)
	 */
	@Override
	public void setFreezed(boolean freezed) {
		this.freezed = freezed;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.internal.container.ItemStore#isFreezed()
	 */
	@Override
	public boolean isFreezed() {
		return freezed;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemStore#getMaxCacheSize()
	 */
	@Override
	public int getMaxCacheSize() {
		return itemCache.getMaxSize();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemStore#setMaxCacheSize(int)
	 */
	@Override
	public void setMaxCacheSize(int maxCacheSize) {
		reset(false, false);
		this.itemCache = new ItemCacheMap<>(maxCacheSize);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemStore#size()
	 */
	@Override
	public int size() {
		return isFreezed() ? 0 : (getItemQuerySize() + getAddedItemsSize() - getRemovedItemsSize());
	}

	/**
	 * Size of added items
	 * @return Added items count
	 */
	protected int getAddedItemsSize() {
		return (addedItems == null) ? 0 : addedItems.size();
	}

	/**
	 * Size of removed items
	 * @return Removed items count
	 */
	protected int getRemovedItemsSize() {
		return (removedItems == null) ? 0 : removedItems.size();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemStore#getItemIds()
	 */
	@Override
	public List<?> getItemIds() {
		if (isFreezed()) {
			return Collections.emptyList();
		}
		if (itemIds == null) {
			if (itemIdentifierProvider != null) {
				itemIds = new ItemStoreIds<>(this, itemIdentifierProvider);
			} else {
				itemIds = new NaturalNumberIdsList(size());
			}
		}
		return itemIds;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemStore#indexOfItem(java.lang.Object)
	 */
	@Override
	public int indexOfItem(Object itemId) {
		return isFreezed() ? -1 : getItemIds().indexOf(itemId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemStore#containsItem(java.lang.Object)
	 */
	@Override
	public boolean containsItem(Object itemId) {
		return isFreezed() ? false : getItemIds().contains(itemId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemStore#getItem(int)
	 */
	@Override
	public ITEM getItem(int index) {
		if (index < 0 || index >= size()) {
			throw new IndexOutOfBoundsException("Container size: " + size() + " and item index requested: " + index);
		}

		if (isFreezed()) {
			return null;
		}

		final int addedItemCount = getAddedItemsSize();
		if (addedItemCount > 0 && index < addedItemCount) {
			// an item from the addedItems was requested
			return addedItems.get(index);
		}
		// get from cache or load using query
		final int itemIndex = index - addedItemCount;
		ITEM item = getItemCache().getItem(itemIndex);
		if (item == null) {
			// item not in cache, query for more items
			return loadItem(itemIndex);
		}
		return item;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.data.ItemStore#refreshItem(java.lang.Object)
	 */
	@Override
	public void refreshItem(ITEM item) {
		ObjectUtils.argumentNotNull(item, "Item to refresh must be not null");
		int index = indexOfItem(getItemId(item));
		if (index > -1) {
			ITEM refreshed = getItemQuery().refresh(item);
			if (refreshed != null) {
				refreshItem(refreshed, index);
			}
		}
	}

	/**
	 * Refresh given Item instance at given item <code>index</code>
	 * @param item Item to refresh
	 * @param index Item index
	 * @return <code>true</code> if found and refreshed
	 */
	protected boolean refreshItem(ITEM item, int index) {
		ITEM previous = null;
		final int addedItemCount = getAddedItemsSize();
		if (addedItemCount > 0 && index < addedItemCount) {
			previous = addedItems.get(index);
			addedItems.remove(index);
			addedItems.add(index, item);
		} else {
			int cacheIndex = index - addedItemCount;
			previous = getItemCache().putItem(cacheIndex, item);
		}

		// fire listeners
		fireItemActionListeners(item, getItemId(item), previous, ItemAction.REFRESHED);

		return previous != null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemStore#addItem(com.vaadin.data.Item)
	 */
	@Override
	public Object addItem(ITEM item) {
		if (item != null) {

			Object itemId = getNewItemId(item);

			if (addedItems == null) {
				addedItems = new ArrayList<>();
			}
			addedItems.add(0, item);

			// fire listeners
			fireItemActionListeners(item, itemId, null, ItemAction.ADDED);

			return itemId;

		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemStore#removeItem(int)
	 */
	@Override
	public boolean removeItem(int index) {
		final ITEM item = getItem(index);
		if (item != null) {
			if (removedItems == null) {
				removedItems = new ArrayList<>();
			}
			removedItems.add(item);

			// fire listeners
			fireItemActionListeners(item, getItemId(item), null, ItemAction.REMOVED);

			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.data.ItemStore#removeItem(java.lang.Object)
	 */
	@Override
	public boolean removeItem(ITEM item) {
		if (item != null) {
			if (removedItems == null) {
				removedItems = new ArrayList<>();
			}
			removedItems.add(item);

			// fire listeners
			fireItemActionListeners(item, getItemId(item), null, ItemAction.REMOVED);

			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemStore#setItemModified(com.vaadin.data.Item)
	 */
	@Override
	public void setItemModified(ITEM item) {
		if (item != null) {
			if (addedItems == null || !addedItems.contains(item)) {
				if (modifiedItems == null) {
					modifiedItems = new ArrayList<>();
				}
				if (!modifiedItems.contains(item)) {
					modifiedItems.add(item);
				}

				// fire listeners
				fireItemActionListeners(item, getItemId(item), null, ItemAction.MODIFIED);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemStore#isModified()
	 */
	@Override
	public boolean isModified() {
		return getAddedItemsSize() > 0 || getModifiedItems().size() > 0 || getRemovedItems().size() > 0;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemStore#getAddedItems()
	 */
	@Override
	public List<ITEM> getAddedItems() {
		if (addedItems != null) {
			return Collections.unmodifiableList(addedItems);
		}
		return Collections.emptyList();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemStore#getModifiedItems()
	 */
	@Override
	public List<ITEM> getModifiedItems() {
		if (modifiedItems != null) {
			return Collections.unmodifiableList(modifiedItems);
		}
		return Collections.emptyList();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemStore#getRemovedItems()
	 */
	@Override
	public List<ITEM> getRemovedItems() {
		if (removedItems != null) {
			return Collections.unmodifiableList(removedItems);
		}
		return Collections.emptyList();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemStore#clear()
	 */
	@Override
	public void reset(boolean fireListeners, boolean freeze) {
		freezed = freeze;
		query = null;
		itemIds = null;
		if (getItemCache() != null) {
			getItemCache().clear();
		}
		discard();

		// fire listeners
		if (fireListeners) {
			fireItemActionListeners(null, null, null, ItemAction.SET_CHANGED);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemStore#discard()
	 */
	@Override
	public void discard() {
		addedItems = null;
		modifiedItems = null;
		removedItems = null;
	}

	/**
	 * Get the query size.
	 * @return the query size
	 */
	protected int getItemQuerySize() {
		getItemQuery();
		return querySize;
	}

	/**
	 * Get current {@link ItemDataProvider} or build a new one if not available.
	 * @return Item query
	 */
	protected ItemDataProvider<ITEM> getItemQuery() {
		if (query == null) {
			query = dataProviderFactory.get();
			// size
			querySize = (int) query.size(getConfiguration());
		}
		return query;
	}

	/**
	 * Load item for given index and the surrounding batch of items
	 * @param index The index of requested item
	 * @return Loaded Item, if found
	 */
	protected ITEM loadItem(final int index) {

		ITEM requestedItem = null;

		final int batchSize = getBatchSize();
		final int startIndex = index - index % batchSize;
		final int count = Math.min(batchSize, getItemQuerySize() - startIndex);

		// load more items using query
		List<ITEM> items = getItemQuery().load(getConfiguration(), startIndex, count).collect(Collectors.toList());
		if (items == null) {
			items = Collections.emptyList();
		}

		// put Items in cache and setup value change listeners
		for (int i = 0; i < count; i++) {
			final int itemIndex = startIndex + i;
			if (i < items.size()) {
				ITEM item = items.get(i);
				if (itemIndex == index) {
					// requested item
					requestedItem = item;
				}
				ITEM previous = getItemCache().putItem(itemIndex, item);

				// fire listeners
				fireItemActionListeners(item, null, previous, ItemAction.LOADED);
			}
		}

		return requestedItem;
	}

	/**
	 * Get the id of given item
	 * @param item Item to obtain the id for
	 * @return Item id
	 */
	@Override
	public Object getItemId(ITEM item) {
		if (itemIdentifierProvider != null) {
			return itemIdentifierProvider.getItemId(item);
		} else {
			for (int i = 0; i < size(); i++) {
				ITEM itm = getItem(i);
				if (itm != null && itm.equals(item)) {
					return i;
				}
			}
		}
		return null;
	}

	/**
	 * Get the id of an added item
	 * @param item Added item
	 * @return Item id
	 */
	protected Object getNewItemId(ITEM item) {
		if (itemIdentifierProvider != null) {
			Object itemId = itemIdentifierProvider.getItemId(item);
			if (itemId == null) {
				throw new IllegalStateException("Null item id for added item: " + item);
			}
			return itemId;
		} else {
			itemIds = null;
			return 0;
		}
	}

	/**
	 * Fire registered {@link ItemActionListener}s.
	 * @param item Item subject of the action
	 * @param itemId Item id
	 * @param previous Previous item, if available
	 * @param action Action type
	 */
	protected void fireItemActionListeners(ITEM item, Object itemId, ITEM previous, ItemAction action) {
		for (ItemActionListener<ITEM> listener : itemActionListeners) {
			listener.onItemAction(item, itemId, previous, action);
		}
	}

}
