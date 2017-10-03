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
package com.holonplatform.vaadin.components;

import java.io.Serializable;
import java.util.Optional;
import java.util.Set;

import com.vaadin.shared.Registration;

/**
 * Represents a component which supports items selection.
 *
 * @param <T> Selection item type
 *
 * @since 5.0.0
 */
public interface Selectable<T> {

	/**
	 * Get the selection mode
	 * @return the selection mode
	 */
	SelectionMode getSelectionMode();

	/**
	 * Returns whether the given <code>item</code> is selected.
	 * @param item Item to check (not null)
	 * @return <code>true</code> if the given item is selected, <code>false</code> otherwise
	 */
	default boolean isSelected(T item) {
		return getSelectedItems().contains(item);
	}

	/**
	 * Get an immutable set of the currently selected items.
	 * <p>
	 * The iteration order of the items in the returned set is implementation dependent.
	 * </p>
	 * @return Selected items set, empty if none
	 */
	Set<T> getSelectedItems();

	/**
	 * Get the first selected item.
	 * @return the first selected item, empty if none
	 */
	Optional<T> getFirstSelectedItem();

	/**
	 * Selects the given item. If the item is already selected, does nothing.
	 * <p>
	 * When in {@link SelectionMode#SINGLE}, any previously selected item is deselected.
	 * </p>
	 * @param item the item to select (not null)
	 */
	void select(T item);

	/**
	 * Deselects the given item. If the item is not currently selected, does nothing.
	 * @param item the item to deselect (not null)
	 */
	void deselect(T item);

	/**
	 * Deselects all currently selected items, if any.
	 */
	void deselectAll();

	/**
	 * Adds a {@link SelectionListener} to listener to selection changes.
	 * @param selectionListener The listener to add
	 * @return the listener {@link Registration}
	 */
	Registration addSelectionListener(SelectionListener<T> selectionListener);

	/**
	 * Selection modes enumeration.
	 */
	public enum SelectionMode {

		/**
		 * The selection is not active.
		 */
		NONE,

		/**
		 * Single selection mode.
		 */
		SINGLE,

		/**
		 * Multiple selection mode.
		 */
		MULTI;

	}

	/**
	 * Selection event.
	 * @param <T> Selection item type
	 */
	public interface SelectionEvent<T> extends Serializable {

		/**
		 * Get first selected data item, if any.
		 * @return the first selected item, empty if none
		 */
		Optional<T> getFirstSelectedItem();

		/**
		 * Gets all the currently selected items. For single selection it returns a set containing the only selected
		 * item.
		 * @return return all the selected items, if any, never <code>null</code>
		 */
		Set<T> getAllSelectedItems();

		/**
		 * Returns whether this event was triggered by user interaction, on the client side, or programmatically, on the
		 * server side.
		 * @return <code>true</code> if this event originates from the client, <code>false</code> otherwise.
		 */
		boolean isUserOriginated();

	}

	/**
	 * A listener for listening for selection changes from a {@link Selectable}.
	 * @param <T> Selection item type
	 */
	@FunctionalInterface
	public interface SelectionListener<T> extends Serializable {

		/**
		 * Invoked when the selection has changed.
		 * @param selectionEvent The selection event to inspect the selected items
		 */
		void onSelectionChange(SelectionEvent<T> selectionEvent);

	}

}
