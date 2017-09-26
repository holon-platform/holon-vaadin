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
import java.util.List;
import java.util.Optional;

import com.holonplatform.vaadin.data.ItemDataSource.ItemSort;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.ui.Component;

/**
 * A component to display a set of items as tabular data, using item properties as column ids.
 * 
 * @param <T> Item data type
 * @param <P> Item property type
 * 
 * @since 5.0.0
 */
public interface ItemListing<T, P> extends ItemSet, Selectable<T>, Component {

	/**
	 * Gets the current displayed item properties as listing columns
	 * @return Property columns list, in the order they are displayed
	 */
	List<P> getPropertyColumns();

	/**
	 * Show or hide the column bound to given <code>property</code>.
	 * @param property Property to show or hide (not null)
	 * @param visible <code>true</code> to show the column bound to given <code>property</code>, <code>false</code> to
	 *        hide it
	 * @throws IllegalArgumentException If specified property is not bound to any column
	 */
	void setPropertyColumnVisible(P property, boolean visible);

	/**
	 * Returns whether the listing footer is visible.
	 * @return <code>true</code> if footer is visible, <code>false</code> otherwise
	 */
	boolean isFooterVisible();

	/**
	 * Sets whether the listing footer is visible.
	 * @param visible <code>true</code> to set the footer visible, <code>false</code> otherwise
	 */
	void setFooterVisible(boolean visible);

	/**
	 * Gets whether the row details component for given <code>item</code> is visible.
	 * @param item Item to check whether the row details component is visible (not null)
	 * @return <code>true</code> if row details component is visible for given item, <code>false</code> otherwise
	 */
	boolean isItemDetailsVisible(T item);

	/**
	 * Set whether to show the row details component for given <code>item</code>.
	 * @param item Item for which to show or hide the row details (not null)
	 * @param visible <code>true</code> to show the row details component, <code>false</code> to hide it
	 * @throws UnsupportedOperationException If the concrete listing component does not support a row details component
	 */
	void setItemDetailsVisible(T item, boolean visible) throws UnsupportedOperationException;

	/**
	 * Scrolls listing rows to top (i.e. to the first row, if any)
	 */
	void scrollToTop();

	/**
	 * Scrolls listing viewport to show the given item , if present.
	 * @param item Item to scroll to (not null)
	 */
	void scrollToItem(T item);

	/**
	 * Sort the listing using given {@link ItemSort} directives.
	 * @param sorts Item sorts to apply
	 */
	@SuppressWarnings("unchecked")
	void sort(ItemSort<P>... sorts);

	/**
	 * When in <em>buffered</em> mode, clear the buffered items in the internal cache.
	 * <p>
	 * When not in <em>buffered</em> mode, simply reset the selection, if the listing is slectable.
	 * </p>
	 * @see #isBuffered()
	 */
	void clear();

	/**
	 * Set the listing selection mode
	 * @param selectionMode The selection mode to set (not null)
	 */
	void setSelectionMode(SelectionMode selectionMode);

	/**
	 * Select all available items.
	 */
	void selectAll();

	/**
	 * Get the item identified by given <code>itemId</code>.
	 * <p>
	 * This operation is available only in <em>buffered</em> mode.
	 * </p>
	 * @param itemId Item id (not null)
	 * @return Optional item identified by given <code>itemId</code>, empty if not found
	 * @throws NotBufferedException If the listing is not in buffered mode
	 * @see #isBuffered()
	 */
	Optional<T> getItem(Object itemId);

	/**
	 * Adds an item to the data source.
	 * @param item The item to add (not null)
	 * @return Id of the added item
	 */
	Object addItem(T item);

	/**
	 * Removes given item from the data source.
	 * @param item Item to remove (not null)
	 * @return <code>true</code> if the item was successfully removed from data source
	 */
	boolean removeItem(T item);

	/**
	 * Refresh given item in data source
	 * @param item Item to refresh (not null)
	 * @throws UnsupportedOperationException If the refresh operation is not supported by concrete data store
	 */
	void refreshItem(T item);

	/**
	 * Updates all changes since the previous commit.
	 * <p>
	 * This operation is available only in <em>buffered</em> mode.
	 * </p>
	 * @throws NotBufferedException If the listing is not in buffered mode
	 * @see #isBuffered()
	 */
	void commit();

	/**
	 * Discards all changes since last commit.
	 * <p>
	 * This operation is available only in <em>buffered</em> mode.
	 * </p>
	 * @throws NotBufferedException If the listing is not in buffered mode
	 * @see #isBuffered()
	 */
	void discard();

	/**
	 * Get whether the listing is in <em>buffered</em> mode, i.e. an internal item cache is used.
	 * @return whether the listing is in buffered mode
	 */
	boolean isBuffered();

	/**
	 * Set whether the listing is editable by the user.
	 * @param editable <code>true</code> to set the listing as editable, <code>false</code> otherwise
	 */
	void setEditable(boolean editable);

	/**
	 * Get whether the listing is editable by the user.
	 * @return <code>true</code> if the listing is editable, <code>false</code> otherwise
	 */
	boolean isEditable();

	// -------

	/**
	 * Enumeration of column content alignment options
	 */
	public enum ColumnAlignment {

		/**
		 * Left aligned column content
		 */
		LEFT,

		/**
		 * Centered column content
		 */
		CENTER,

		/**
		 * Right aligned column content
		 */
		RIGHT;

	}

	/**
	 * Generator to provide the style names for a cell.
	 * @param <T> Item type
	 * @param <P> Item property type
	 */
	@FunctionalInterface
	public interface CellStyleGenerator<T, P> extends Serializable {

		/**
		 * Get the style names for the cell bound to given <code>property</code> column and <code>item</code> row.
		 * @param property Cell property (column)
		 * @param item Item bound to the row for which to generate the cell style
		 * @return Cell style names, <code>null</code> for none
		 */
		String getCellStyle(P property, T item);

	}

	/**
	 * Generator to provide the style names for an item row.
	 * @param <T> Item type
	 */
	@FunctionalInterface
	public interface RowStyleGenerator<T> extends Serializable {

		/**
		 * Get the style names for the row bound to given <code>item</code>.
		 * @param item Item bound to the row for which to generate the style
		 * @return Row style names, <code>null</code> for none
		 */
		String getRowStyle(T item);

	}

	/**
	 * Item descriptions generator.
	 * @param <T> Item type
	 */
	@FunctionalInterface
	public interface ItemDescriptionGenerator<T> extends Serializable {

		/**
		 * Get the description for given <code>item</code>.
		 * @param item Item bound to the row for which to generate the description
		 * @return Item description, <code>null</code> for none
		 */
		String getItemDescription(T item);

	}

	/**
	 * Listener for user click events on an item (a listing row).
	 * @param <T> Item type
	 * @param <P> Item property type
	 */
	@FunctionalInterface
	public interface ItemClickListener<T, P> extends Serializable {

		/**
		 * Triggered when user clicks on an item.
		 * @param item Item bound to clicked row
		 * @param clickedProperty Clicked column property
		 * @param clickEvent Event details to obtain informations on mouse button and clicked point
		 */
		void onItemClick(T item, P clickedProperty, MouseEventDetails clickEvent);

	}

	/**
	 * Listener for column reordering events.
	 */
	@FunctionalInterface
	public interface PropertyReorderListener<P> extends Serializable {

		/**
		 * Triggered when the columns order changes.
		 * @param properties New columns order, expressed through a list of column properties
		 * @param userOriginated <code>true</code> if the reordering event is originated from a user action
		 */
		void onPropertyReordered(List<P> properties, boolean userOriginated);

	}

	/**
	 * Listener for column resizing events.
	 */
	@FunctionalInterface
	public interface PropertyResizeListener<P> extends Serializable {

		/**
		 * Triggered when a column size changes.
		 * @param property Property bound to resized column
		 * @param widthInPixel New column with in pixels
		 * @param userOriginated <code>true</code> if the resizing event is originated from a user action
		 */
		void onPropertyResized(P property, int widthInPixel, boolean userOriginated);

	}

	/**
	 * Listener for column visibility change events.
	 */
	@FunctionalInterface
	public interface PropertyVisibilityListener<P> extends Serializable {

		/**
		 * Triggered when a table columns visibility changes.
		 * @param property Property bound to resized column
		 * @param hidden New visibility state
		 * @param userOriginated <code>true</code> if the event is originated from a user action
		 */
		void onPropertyVisibilityChanged(P property, boolean hidden, boolean userOriginated);

	}

	/**
	 * Generator for item (row) details components.
	 * @param <T> Item type
	 */
	@FunctionalInterface
	public interface ItemDetailsGenerator<T> extends Serializable {

		/**
		 * Get the row details component for given item.
		 * @param item Item bound to the row for which to generate the details component
		 * @return Row details component
		 */
		Component getItemDetails(T item);

	}

	/**
	 * Exception thrown when a listing operation is invoked and it is valid only in <em>buffered </em> mode.
	 */
	public class NotBufferedException extends RuntimeException {

		private static final long serialVersionUID = 6014290672296737254L;

		/**
		 * Constructor
		 * @param message Error message
		 */
		public NotBufferedException(String message) {
			super(message);
		}

	}

}
