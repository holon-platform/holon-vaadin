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
package com.holonplatform.vaadin.components.builders;

import java.io.Serializable;
import java.util.Arrays;

import com.holonplatform.core.Path;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.query.QuerySort;
import com.holonplatform.vaadin.components.BeanListing;
import com.holonplatform.vaadin.components.ItemListing;
import com.holonplatform.vaadin.components.ItemListing.CellStyleGenerator;
import com.holonplatform.vaadin.components.ItemListing.ColumnAlignment;
import com.holonplatform.vaadin.components.ItemListing.ItemClickListener;
import com.holonplatform.vaadin.components.ItemListing.ItemDescriptionGenerator;
import com.holonplatform.vaadin.components.ItemListing.ItemDetailsGenerator;
import com.holonplatform.vaadin.components.ItemListing.PropertyEditorFactory;
import com.holonplatform.vaadin.components.ItemListing.PropertyReorderListener;
import com.holonplatform.vaadin.components.ItemListing.PropertyResizeListener;
import com.holonplatform.vaadin.components.ItemListing.PropertyVisibilityListener;
import com.holonplatform.vaadin.components.ItemListing.RowStyleGenerator;
import com.holonplatform.vaadin.components.Selectable.SelectionListener;
import com.holonplatform.vaadin.components.Selectable.SelectionMode;
import com.holonplatform.vaadin.data.ItemDataSource.CommitHandler;
import com.holonplatform.vaadin.data.ItemDataSource.PropertySortGenerator;
import com.vaadin.data.ValueProvider;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.components.grid.FooterRow;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.renderers.Renderer;

/**
 * Base {@link ItemListing} builder.
 * 
 * @param <T> Item data type
 * @param <C> Component type
 * @param <P> Item property type
 * @param <B> Concrete builder type
 * @param <X> Concrete backing component type
 * 
 * @since 5.0.0
 */
public interface ItemListingBuilder<T, P, C extends ItemListing<T, P>, B extends ItemListingBuilder<T, P, C, B, X>, X extends Component>
		extends ItemDataSourceComponentBuilder<T, C, B>, ComponentPostProcessorSupport<X, B> {

	/**
	 * Set whether given property is sortable.
	 * @param property Property (not null)
	 * @param sortable Whether given property id is sortable
	 * @return this
	 */
	B sortable(P property, boolean sortable);

	/**
	 * Set whether given property is read-only.
	 * @param property Property (not null)
	 * @param readOnly Whether given property id is read-only
	 * @return this
	 */
	B readOnly(P property, boolean readOnly);

	/**
	 * Set a default value to initialize the given <code>property</code>.
	 * @param property Property (not null)
	 * @param defaultValue Default value
	 * @return this
	 */
	B defaultValue(P property, Object defaultValue);

	/**
	 * Declares to use specified {@link Path} to generate query sorts for given <code>property</code>.
	 * @param property Property for which to declare the sort path (not null)
	 * @param sortPath Sort path to use (not null)
	 * @return this
	 */
	B sortUsing(P property, Path<?> sortPath);

	/**
	 * Set a {@link PropertySortGenerator} to generate {@link QuerySort}s for given <code>property</code>
	 * @param property Property to sort (not null)
	 * @param generator PropertySortGenerator (not null)
	 * @return this
	 */
	B sortGenerator(P property, PropertySortGenerator<P> generator);

	/**
	 * Set the handler to use to persist item set modifications.
	 * @param commitHandler Handler to set (not null)
	 * @return this
	 */
	B commitHandler(CommitHandler<T> commitHandler);

	/**
	 * Hides the table/grid column headers
	 * @return this
	 */
	B hideHeaders();

	/**
	 * Add a {@link RowStyleGenerator} to generate row style names.
	 * @param rowStyleGenerator Row style generator
	 * @return this
	 */
	B withRowStyle(RowStyleGenerator<T> rowStyleGenerator);

	/**
	 * Set the column header to show for given <code>property</code>.
	 * <p>
	 * By default, if the property is {@link Localizable}, the {@link Localizable#getMessage()} (and
	 * {@link Localizable#getMessageCode()} if a {@link LocalizationContext} is available) is used as column header.
	 * </p>
	 * @param property Item property to set the header for (not null)
	 * @param header Localizable column header (not null)
	 * @return this
	 */
	B header(P property, Localizable header);

	/**
	 * Set the column header to show for given <code>property</code>.
	 * @param property Item property to set the header for (not null)
	 * @param header Column header
	 * @return this
	 */
	default B header(P property, String header) {
		return header(property, Localizable.builder().message(header).build());
	}

	/**
	 * Set the column header to show for given <code>property</code>.
	 * @param property Item property to set the header for (not null)
	 * @param defaultHeader Default column header
	 * @param headerMessageCode Column header translation message code
	 * @return this
	 */
	default B header(P property, String defaultHeader, String headerMessageCode) {
		return header(property, Localizable.builder().message(defaultHeader).messageCode(headerMessageCode).build());
	}

	/**
	 * Set the text alignment for the column which corresponds to given property.
	 * @param property Item property to set the alignment for (not null)
	 * @param alignment Alignment
	 * @return this
	 */
	B alignment(P property, ColumnAlignment alignment);

	/**
	 * Set the width in pixels for the column which corresponds to given property.
	 * @param property Item property to set the width for (not null)
	 * @param widthInPixels Width in pixel
	 * @return this
	 */
	B width(P property, int widthInPixels);

	/**
	 * Set whether the column which corresponds to given property is editable when listing is in edit mode
	 * (<code>true</code> by default).
	 * @param property Item property to set editable or not (not null)
	 * @param editable <code>true</code> to set the column editable, <code>false</code> otherwise
	 * @return this
	 */
	B editable(P property, boolean editable);

	/**
	 * Set the {@link PropertyEditorFactory} to use for given property in edit mode to obtain the field to use as
	 * property editor.
	 * @param property Item property to set the editor for (not null)
	 * @param propertyEditorFactory {@link PropertyEditorFactory} to set (not null)
	 * @return this
	 */
	B editor(P property, PropertyEditorFactory<P> propertyEditorFactory);

	/**
	 * Sets whether the column which corresponds to given property can be hidden by the user.
	 * @param property Item property to set hidable or not (not null)
	 * @param hidable <code>true</code> if the column which corresponds to given property can be hidden by the user
	 * @return this
	 */
	B hidable(P property, boolean hidable);

	/**
	 * Sets whether this column which corresponds to given property is hidden.
	 * @param property Item property to set hidden or not (not null)
	 * @param hidden <code>true</code> if column is hidden
	 * @return this
	 */
	B hidden(P property, boolean hidden);

	/**
	 * Set the {@link CellStyleGenerator} to call for given <code>property</code> to generate column cell style.
	 * @param property Item property to set the style generator for (not null)
	 * @param cellStyleGenerator Cell style generator (not null)
	 * @return this
	 */
	B style(P property, CellStyleGenerator<T, P> cellStyleGenerator);

	/**
	 * Set the listing selection mode.
	 * @param selectionMode Selection mode to set (not null). Use {@link SelectionMode#NONE} to disable selection.
	 * @return this
	 */
	B selectionMode(SelectionMode selectionMode);

	/**
	 * Add a {@link SelectionListener} to listen to items selection changes.
	 * <p>
	 * {@link SelectionListener}s are triggred only when listing is selectable, i.e. (i.e. {@link SelectionMode} is not
	 * {@link SelectionMode#NONE}).
	 * </p>
	 * @param selectionListener Selection listener to add (not null)
	 * @return this
	 */
	B withSelectionListener(SelectionListener<T> selectionListener);

	/**
	 * Set whether the listing is editable.
	 * @param editable <code>true</code> to set the listing editable, <code>false</code> otherwise
	 * @return this
	 */
	B editable(boolean editable);

	/**
	 * Adds a {@link ItemClickListener} that gets notified when an item is clicked by the user.
	 * @param listener ItemClickListener to add (not null)
	 * @return this
	 */
	B withItemClickListener(ItemClickListener<T, P> listener);

	/**
	 * Adds a {@link PropertyReorderListener} that gets notified when property columns order changes.
	 * @param listener Listener to add (not null)
	 * @return this
	 */
	B withPropertyReorderListener(PropertyReorderListener<P> listener);

	/**
	 * Adds a {@link PropertyResizeListener} that gets notified when a property column is resized.
	 * @param listener Listener to add (not null)
	 * @return this
	 */
	B withPropertyResizeListener(PropertyResizeListener<P> listener);

	/**
	 * Adds a {@link PropertyVisibilityListener} that gets notified when a property column is hidden or shown.
	 * @param listener Listener to add (not null)
	 * @return this
	 */
	B withPropertyVisibilityListener(PropertyVisibilityListener<P> listener);

	/**
	 * Set the {@link ItemDescriptionGenerator} to use to generate item descriptions (tooltips).
	 * @param rowDescriptionGenerator Generator to set (not null)
	 * @return this
	 */
	B itemDescriptionGenerator(ItemDescriptionGenerator<T> rowDescriptionGenerator);

	/**
	 * Sets whether column hiding by user is allowed or not.
	 * @param columnHidingAllowed <code>true</code> if column hiding is allowed
	 * @return this
	 */
	B columnHidingAllowed(boolean columnHidingAllowed);

	/**
	 * Sets whether column reordering is allowed or not.
	 * @param columnReorderingAllowed <code>true</code> if column reordering is allowed
	 * @return this
	 */
	B columnReorderingAllowed(boolean columnReorderingAllowed);

	/**
	 * Set whether the listing footer is visible.
	 * @param footerVisible whether the listing footer is visible
	 * @return this
	 */
	B footerVisible(boolean footerVisible);

	/**
	 * Build the {@link ItemListing} instance, setting given properties as visible columns.
	 * <p>
	 * Use {@link #build()} to build an {@link ItemListing} using all data source properties as visible columns.
	 * </p>
	 * @param visibleColumns Visible column properties (not null)
	 * @return {@link ItemListing} component
	 */
	@SuppressWarnings("unchecked")
	default C build(P... visibleColumns) {
		ObjectUtils.argumentNotNull(visibleColumns, "Visible columns must be not null");
		if (visibleColumns.length == 0) {
			throw new IllegalArgumentException("Visible columns must be not null and not empty");
		}
		return build(Arrays.asList(visibleColumns));
	}

	/**
	 * Build the {@link ItemListing} instance, setting given properties as visible columns.
	 * <p>
	 * Use {@link #build()} to build an {@link ItemListing} using all data source properties as visible columns.
	 * </p>
	 * @param visibleColumns Visible column properties (not null)
	 * @return {@link ItemListing} component
	 */
	C build(Iterable<P> visibleColumns);

	// Using Grid

	/**
	 * Base builder to create an {@link ItemListing} with a {@link Grid} as backing component.
	 * @param <T> Item data type
	 * @param <P> Item property type
	 * @param <C> Component type
	 * @param <B> Concrete builder type
	 */
	public interface BaseGridItemListingBuilder<T, P, C extends ItemListing<T, P>, B extends BaseGridItemListingBuilder<T, P, C, B>>
			extends ItemListingBuilder<T, P, C, B, Grid<T>> {

		/**
		 * Set the height of the listing defined by its contents.
		 * @return this
		 */
		B heightByContents();

		/**
		 * Set the height of the listing by defined by a number of rows.
		 * @param rows Number of rows that should be visible in grid's body
		 * @return this
		 */
		B heightByRows(double rows);

		/**
		 * Set the minimum width in pixels for the column which corresponds to given property.
		 * @param property Item property to set the width for (not null)
		 * @param widthInPixels Minimum width in pixel
		 * @return this
		 */
		B minWidth(P property, int widthInPixels);

		/**
		 * Set the maximum width in pixels for the column which corresponds to given property.
		 * @param property Item property to set the width for (not null)
		 * @param widthInPixels Maximum width in pixel
		 * @return this
		 */
		B maxWidth(P property, int widthInPixels);

		/**
		 * Set the expandRatio for the column which corresponds to given property.
		 * <p>
		 * By default, all columns expand equally (treated as if all of them had an expand ratio of 1). Once at least
		 * one column gets a defined expand ratio, the implicit expand ratio is removed, and only the defined expand
		 * ratios are taken into account.
		 * </p>
		 * <p>
		 * If a column has a defined width, it overrides this method's effects.
		 * </p>
		 * <p>
		 * <em>Example:</em> A grid with three columns, with expand ratios 0, 1 and 2, respectively. The column with a
		 * <strong>ratio of 0 is exactly as wide as its contents requires</strong>. The column with a ratio of 1 is as
		 * wide as it needs, <strong>plus a third of any excess space</strong>, because we have 3 parts total, and this
		 * column reserves only one of those. The column with a ratio of 2, is as wide as it needs to be, <strong>plus
		 * two thirds</strong> of the excess width.
		 * </p>
		 * @param property Item property to set the expand ratio for (not null)
		 * @param expandRatio Column expand ratio. <code>0</code> to not have it expand at all
		 * @return this
		 */
		B expandRatio(P property, int expandRatio);

		/**
		 * Sets the caption of the hiding toggle for the column which corresponds to given property. Shown in the toggle
		 * for this column in the grid's sidebar when the column is hidable.
		 * @param property Item property to set the caption for (not null)
		 * @param hidingToggleCaption Localizable hiding toggle caption (not null)
		 * @return this
		 */
		B hidingToggleCaption(P property, Localizable hidingToggleCaption);

		/**
		 * Sets the caption of the hiding toggle for the column which corresponds to given property. Shown in the toggle
		 * for this column in the grid's sidebar when the column is hidable.
		 * @param property Item property to set the caption for (not null)
		 * @param hidingToggleCaption Hiding toggle caption
		 * @return this
		 */
		default B hidingToggleCaption(P property, String hidingToggleCaption) {
			return hidingToggleCaption(property, Localizable.builder().message(hidingToggleCaption).build());
		}

		/**
		 * Sets the caption of the hiding toggle for the column which corresponds to given property. Shown in the toggle
		 * for this column in the grid's sidebar when the column is hidable.
		 * @param property Item property to set the caption for (not null)
		 * @param hidingToggleCaption Hiding toggle caption default message
		 * @param messageCode Hiding toggle caption localization message code
		 * @return this
		 */
		default B hidingToggleCaption(P property, String hidingToggleCaption, String messageCode) {
			return hidingToggleCaption(property,
					Localizable.builder().message(hidingToggleCaption).messageCode(messageCode).build());
		}

		/**
		 * Sets whether the column which corresponds to given property is resizable by the user.
		 * @param property Item property to set resizable or not (not null)
		 * @param resizable <code>true</code> if the column which corresponds to given property is resizable by the user
		 * @return this
		 */
		B resizable(P property, boolean resizable);

		/**
		 * Sets the number of frozen columns in this listing. Setting the count to 0 means that no data columns will be
		 * frozen, but the built-in selection checkbox column will still be frozen if it's in use. Setting the count to
		 * -1 will also disable the selection column.
		 * @param frozenColumnsCount The number of columns that should be frozen
		 * @return this
		 */
		B frozenColumns(int frozenColumnsCount);

		/**
		 * Set the caption for the editor <em>Save</em> button.
		 * @param caption Localizable caption (not null)
		 * @return this
		 */
		B editorSaveCaption(Localizable caption);

		/**
		 * Set the caption for the editor <em>Save</em> button.
		 * @param caption Button caption
		 * @return this
		 */
		default B editorSaveCaption(String caption) {
			return editorSaveCaption(Localizable.builder().message(caption).build());
		}

		/**
		 * Set the caption for the editor <em>Save</em> button.
		 * @param caption Button caption
		 * @param messageCode Caption translation message code
		 * @return this
		 */
		default B editorSaveCaption(String caption, String messageCode) {
			return editorSaveCaption(Localizable.builder().message(caption).messageCode(messageCode).build());
		}

		/**
		 * Set the caption for the editor <em>Cancel</em> button.
		 * @param caption Localizable caption (not null)
		 * @return this
		 */
		B editorCancelCaption(Localizable caption);

		/**
		 * Set the caption for the editor <em>Cancel</em> button
		 * @param caption Button caption
		 * @return this
		 */
		default B editorCancelCaption(String caption) {
			return editorCancelCaption(Localizable.builder().message(caption).build());
		}

		/**
		 * Set the caption for the editor <em>Cancel</em> button
		 * @param caption Button caption
		 * @param messageCode Optional caption translation message code
		 * @return this
		 */
		default B editorCancelCaption(String caption, String messageCode) {
			return editorCancelCaption(Localizable.builder().message(caption).messageCode(messageCode).build());
		}

		/**
		 * Sets to call {@link ItemListing#commit()} to confirm item modifications in data source when the editor
		 * <em>Save</em> action is triggered.
		 * @return this
		 */
		B commitOnSave();

		/**
		 * Sets to call {@link ItemListing#commit()} to confirm item modifications in data source when an item is
		 * removed using {@link ItemListing#removeItem(Object)}.
		 * @return this
		 */
		B commitOnRemove();

		/**
		 * Set the listing header builder to create and manage header rows.
		 * @param builder Header builder (not null)
		 * @return this
		 */
		B header(HeaderBuilder builder);

		/**
		 * Set the listing footer builder to create and manage footer rows
		 * @param builder Footer builder (not null)
		 * @return this
		 */
		B footer(FooterBuilder builder);

		/**
		 * Set a {@link GridFooterGenerator} to update footer contents when item set changes.
		 * @param footerGenerator Footer generator
		 * @return this
		 */
		B footerGenerator(GridFooterGenerator<T, P> footerGenerator);

		/**
		 * Set the {@link ItemDetailsGenerator} to generate row details component
		 * @param detailsGenerator Item details generator (not null)
		 * @return this
		 */
		B detailsGenerator(ItemDetailsGenerator<T> detailsGenerator);

	}

	/**
	 * Builder to create an {@link ItemListing} with a {@link Grid} as backing component.
	 * @param <T> Item data type
	 */
	public interface GridItemListingBuilder<T>
			extends BaseGridItemListingBuilder<T, String, BeanListing<T>, GridItemListingBuilder<T>> {

		/**
		 * Set a custom {@link Renderer} for given item property.
		 * @param <V> Property value type
		 * @param property Item property to set the renderer for (not null)
		 * @param renderer Renderer to use
		 * @return this
		 */
		GridItemListingBuilder<T> render(String property, Renderer<?> renderer);

		/**
		 * Set a custom {@link Renderer} and presentation provider for given item property.
		 * @param <V> Property value type
		 * @param <P> Presentation value type
		 * @param property Item property to set the renderer for
		 * @param presentationProvider Presentation provider
		 * @param renderer Renderer to use
		 * @return this
		 */
		<V, P> GridItemListingBuilder<T> render(String property, ValueProvider<V, P> presentationProvider,
				Renderer<? super P> renderer);

	}

	// Support interfaces

	public interface GridSection<ROWTYPE> {

		/**
		 * Add a section row at given index.
		 * @param index Row index
		 * @return Added row
		 */
		ROWTYPE addRowAt(int index);

		/**
		 * Adds a new row at the bottom of the section.
		 * @return the new row
		 */
		ROWTYPE appendRow();

		/**
		 * Get the section row at given index.
		 * @param index Row index
		 * @return Section row
		 */
		ROWTYPE getRowAt(int index);

		/**
		 * Returns the current default row of the section.
		 * @return the default row or null if no default row set
		 */
		ROWTYPE getDefaultRow();

		/**
		 * Gets the row count for the section.
		 * @return row count
		 */
		int getRowCount();

		/**
		 * Adds a new row at the top of the section.
		 * @return the new row
		 */
		ROWTYPE prependRow();

		/**
		 * Removes the given row from the section.
		 * @param row the row to be removed
		 */
		void removeRow(ROWTYPE row);

		/**
		 * Removes the row at the given position from the section.
		 * @param rowIndex the position of the row
		 */
		void removeRow(int rowIndex);

		/**
		 * Sets the default row of the section.
		 * @param row the new default row, or null for no default row
		 */
		void setDefaultRow(ROWTYPE row);

	}

	/**
	 * Builder to create and manage Header rows.
	 */
	@FunctionalInterface
	public interface HeaderBuilder {

		/**
		 * Build Grid header rows.
		 * @param header Header rows container
		 */
		void buildHeader(GridSection<HeaderRow> header);

	}

	/**
	 * Builder to create and manage Footer rows.
	 */
	@FunctionalInterface
	public interface FooterBuilder {

		/**
		 * Build Grid footer rows.
		 * @param footer Footer rows container
		 */
		void buildFooter(GridSection<FooterRow> footer);

	}

	/**
	 * Generator for footer contents.
	 */
	@FunctionalInterface
	public interface GridFooterGenerator<T, P> extends Serializable {

		/**
		 * Updates the footer row contents.
		 * @param listing Source listing component
		 * @param footer Footer row reference
		 */
		void updateFooter(ItemListing<T, P> listing, GridSection<FooterRow> footer);

	}

}
