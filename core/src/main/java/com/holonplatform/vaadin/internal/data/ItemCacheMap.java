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

import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Item cache using a {@link Map}.
 *
 * @since 5.0.0
 */
public class ItemCacheMap<ITEM> extends LinkedHashMap<Integer, WeakReference<ITEM>> {

	private static final long serialVersionUID = 4602931764691405659L;

	/**
	 * Max cache size
	 */
	private final int maxSize;

	/**
	 * Constructor
	 * @param maxSize Cache max size
	 */
	public ItemCacheMap(final int maxSize) {
		super(maxSize * 10 / 7, 0.7f, true);
		this.maxSize = maxSize;
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.LinkedHashMap#removeEldestEntry(java.util.Map.Entry)
	 */
	@Override
	protected boolean removeEldestEntry(java.util.Map.Entry<Integer, WeakReference<ITEM>> eldest) {
		return size() > maxSize;
	}

	/**
	 * Max cache size
	 * @return the max cache size
	 */
	public int getMaxSize() {
		return maxSize;
	}

	/**
	 * Check if an Item is cached and bound to given <code>index</code>
	 * @param index Index
	 * @return <code>true</code> if Item i present in cache at given index
	 */
	public boolean containsItem(int index) {
		return getItem(index) != null;
	}

	/**
	 * Get a cached Item by index
	 * @param index Index
	 * @return Cached Item, or <code>null</code> if Item was not cached or was garbage collected according to
	 *         {@link WeakReference} behaviour
	 */
	public ITEM getItem(int index) {
		WeakReference<ITEM> itemReference = get(index);
		if (itemReference != null) {
			return itemReference.get();
		}
		return null;
	}

	/**
	 * Put or replace Item at given index
	 * @param index Index
	 * @param item New item
	 * @return Previous Item, or <code>null</code> if none
	 */
	public ITEM putItem(int index, ITEM item) {
		synchronized (this) {
			final ITEM previous;
			WeakReference<ITEM> itemReference = get(index);
			if (itemReference != null) {
				previous = itemReference.get();
				remove(index);
			} else {
				previous = null;
			}
			put(index, new WeakReference<>(item));
			return previous;
		}
	}

}
