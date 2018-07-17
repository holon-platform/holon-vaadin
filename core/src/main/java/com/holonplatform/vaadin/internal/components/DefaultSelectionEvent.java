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
package com.holonplatform.vaadin.internal.components;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import com.holonplatform.vaadin.components.Selectable.SelectionEvent;

/**
 * Default {@link SelectionEvent} implementation.
 *
 * @param <T> Selection item type
 * 
 * @since 5.0.0
 */
public class DefaultSelectionEvent<T> implements SelectionEvent<T> {

	private static final long serialVersionUID = 8499428333309600901L;

	private final Set<T> selectedItems;
	private final boolean userOriginated;

	public DefaultSelectionEvent(T selectedItem, boolean userOriginated) {
		super();
		this.selectedItems = (selectedItem == null) ? Collections.emptySet() : Collections.singleton(selectedItem);
		this.userOriginated = userOriginated;
	}

	public DefaultSelectionEvent(Set<T> selectedItems, boolean userOriginated) {
		super();
		this.selectedItems = (selectedItems == null) ? Collections.emptySet() : selectedItems;
		this.userOriginated = userOriginated;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Selectable.SelectionEvent#getFirstSelectedItem()
	 */
	@Override
	public Optional<T> getFirstSelectedItem() {
		return (selectedItems.isEmpty()) ? Optional.empty() : Optional.ofNullable(selectedItems.iterator().next());
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Selectable.SelectionEvent#getAllSelectedItems()
	 */
	@Override
	public Set<T> getAllSelectedItems() {
		return selectedItems;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Selectable.SelectionEvent#isUserOriginated()
	 */
	@Override
	public boolean isUserOriginated() {
		return userOriginated;
	}

}
