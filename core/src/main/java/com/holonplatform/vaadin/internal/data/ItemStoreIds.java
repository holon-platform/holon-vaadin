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
import java.util.AbstractList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.holonplatform.vaadin.data.ItemIdentifierProvider;

/**
 * Item ids List used in {@link ItemStore}.
 * 
 * <p>
 * This class was inspired by
 * https://github.com/tlaukkan/vaadin-lazyquerycontainer/blob/master/vaadin-lazyquerycontainer/src/main/java/org/vaadin/addons/lazyquerycontainer/LazyIdList.java
 * from Tommi Laukkanen.
 * </p>
 * 
 * @param <T> Item id type
 * 
 * @since 5.0.0
 */
public class ItemStoreIds<ITEM, T> extends AbstractList<T> implements Serializable {

	private static final long serialVersionUID = -2087791373003680585L;

	/**
	 * Items store
	 */
	private final ItemStore<ITEM> store;

	/**
	 * Item identifier
	 */
	private final ItemIdentifierProvider<ITEM, ?> itemIdentifier;

	/**
	 * Item Id mappings for Ids already loaded through this list
	 */
	private final Map<T, Integer> idIndexMap = new HashMap<>();

	/**
	 * Constructor
	 * @param store Items store
	 * @param itemIdentifier Item identifier provider
	 */
	public ItemStoreIds(ItemStore<ITEM> store, ItemIdentifierProvider<ITEM, ?> itemIdentifier) {
		super();
		this.store = store;
		this.itemIdentifier = itemIdentifier;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size() {
		return store.size();
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public synchronized T[] toArray() {
		List<T> ids = new LinkedList<>();
		int size = size();
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				ids.add(get(i));
			}
		}
		return (T[]) ids.toArray();
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <A> A[] toArray(final A[] a) {
		return (A[]) toArray();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T get(final int index) {
		if (index < 0 || index >= store.size()) {
			throw new IndexOutOfBoundsException();
		}
		ITEM itemAtIndex = store.getItem(index);
		if (itemAtIndex == null) {
			return null;
		}

		final T itemId = getItemId(itemAtIndex);
		// Do not put added item ids to id index map and make sure that
		// existing item indexes start from 0 i.e. ignore added items as they
		// are compensated for in indexOf method.
		final int addedItemSize = store.getAddedItems().size();
		if (index >= addedItemSize) {
			idIndexMap.put(itemId, index - addedItemSize);
		}
		return itemId;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int indexOf(final Object o) {
		if (o == null) {
			return -1;
		}
		// Brute force added items first.
		final List<ITEM> addedItems = store.getAddedItems();
		for (int i = 0; i < addedItems.size(); i++) {
			if (o.equals(getItemId(addedItems.get(i)))) {
				return i;
			}
		}
		// Check from mapping cache.
		if (idIndexMap.containsKey(o)) {
			return addedItems.size() + idIndexMap.get(o);
		}
		// Switching to brute forcing.
		for (int i = addedItems.size(); i < store.size(); i++) {
			ITEM itemAtIndex = store.getItem(i);
			if (itemAtIndex != null && o.equals(getItemId(itemAtIndex))) {
				return i;
			}
		}
		// Not found.
		return -1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean contains(final Object o) {
		return indexOf(o) != -1;
	}

	/**
	 * Get the id of given item
	 * @param item Item for which to obtain the id
	 * @return Item id
	 */
	@SuppressWarnings("unchecked")
	protected T getItemId(ITEM item) {
		Object id = itemIdentifier.getItemId(item);
		if (id == null) {
			throw new IllegalStateException("Invalid item " + item.getClass().getName()
					+ ": missing item identifier (ItemIdentifierProvider [" + itemIdentifier + "] returned null)");
		}
		return (T) id;
	}

}
