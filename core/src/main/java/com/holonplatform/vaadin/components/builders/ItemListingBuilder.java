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
package com.holonplatform.vaadin.components.builders;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import com.holonplatform.core.Path;
import com.holonplatform.core.Validator;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.query.QuerySort;
import com.holonplatform.vaadin.components.BeanListing;
import com.holonplatform.vaadin.components.Input;
import com.holonplatform.vaadin.components.ItemListing;
import com.holonplatform.vaadin.components.ItemListing.CellStyleGenerator;
import com.holonplatform.vaadin.components.ItemListing.ColumnAlignment;
import com.holonplatform.vaadin.components.ItemListing.ItemClickListener;
import com.holonplatform.vaadin.components.ItemListing.ItemDetailsGenerator;
import com.holonplatform.vaadin.components.ItemListing.PropertyReorderListener;
import com.holonplatform.vaadin.components.ItemListing.PropertyResizeListener;
import com.holonplatform.vaadin.components.ItemListing.PropertyVisibilityListener;
import com.holonplatform.vaadin.components.ItemListing.RowStyleGenerator;
import com.holonplatform.vaadin.components.ItemSet.ItemDescriptionGenerator;
import com.holonplatform.vaadin.components.Selectable.SelectionListener;
import com.holonplatform.vaadin.components.Selectable.SelectionMode;
import com.holonplatform.vaadin.data.ItemDataSource.CommitHandler;
import com.holonplatform.vaadin.data.ItemDataSource.PropertySortGenerator;
import com.holonplatform.vaadin.internal.components.ValidatorWrapper;
import com.vaadin.data.HasValue;
import com.vaadin.data.ValueProvider;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.components.grid.EditorCancelListener;
import com.vaadin.ui.components.grid.EditorOpenListener;
import com.vaadin.ui.components.grid.EditorSaveListener;
import com.vaadin.ui.components.grid.FooterCell;
import com.vaadin.ui.components.grid.HeaderCell;
import com.vaadin.ui.components.grid.MultiSelectionModel.SelectAllCheckBoxVisibility;
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
	 * Set whether the item listing is in buffered mode. Default is <code>false</code>.
	 * <p>
	 * When buffered, the listing component requires a specific action to commit item modifications to persistence
	 * store, using {@link ItemListing#commit()}, and changes can be discarded using {@link ItemListing#discard()}.
	 * </p>
	 * <p>
	 * When not buffered, the {@link CommitHandler}, if configured, will be invoked at any item set modification (item
	 * added, updated or removed).
	 * @param buffered Buffered mode to set
	 * @return this
	 */
	B buffered(boolean buffered);

	/**
	 * Set the handler to use to persist item set modifications.
	 * @param commitHandler Handler to set (not null)
	 * @return this
	 */
	B commitHandler(CommitHandler<T> commitHandler);

	/**
	 * Sets the height of a row. If -1 (default), the row height is calculated based on the theme for an empty row
	 * before the listing is displayed.
	 * <p>
	 * Note that all header, body and footer rows get the same height if explicitly set. In automatic mode, each section
	 * is calculated separately based on an empty row of that type.
	 * </p>
	 * @param rowHeight The height of a row in pixels or -1 for automatic calculation
	 * @return this
	 * @see #bodyRowHeight(double)
	 * @see #headerRowHeight(double)
	 * @see #footerRowHeight(double)
	 */
	B rowHeight(double rowHeight);

	/**
	 * Sets the height of a body row. If -1 (default), the row height is calculated based on the theme for an empty row
	 * before the listing is displayed.
	 * @param rowHeight The height of a body row in pixels or -1 for automatic calculation
	 * @return this
	 */
	B bodyRowHeight(double rowHeight);

	/**
	 * Sets the height of a header row. If -1 (default), the row height is calculated based on the theme for an empty
	 * row before the listing is displayed.
	 * @param rowHeight The height of a header row in pixels or -1 for automatic calculation
	 * @return this
	 */
	B headerRowHeight(double rowHeight);

	/**
	 * Sets the height of a footer row. If -1 (default), the row height is calculated based on the theme for an empty
	 * row before the listing is displayed.
	 * @param rowHeight The height of a footer row in pixels or -1 for automatic calculation
	 * @return this
	 */
	B footerRowHeight(double rowHeight);

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
	 * Set the column cell style name for given <code>property</code>
	 * @param property Item property to set the style for (not null)
	 * @param styleName The property column style name
	 * @return this
	 */
	default B style(P property, String styleName) {
		return style(property, (p, item) -> styleName);
	}

	/**
	 * Set the listing selection mode.
	 * @param selectionMode Selection mode to set (not null). Use {@link SelectionMode#NONE} to disable selection.
	 * @return this
	 */
	B selectionMode(SelectionMode selectionMode);

	/**
	 * Sets the select all checkbox visibility mode when the listing is in {@link SelectionMode#MULTI} mode.
	 * @param selectAllCheckBoxVisibility the visiblity mode to use
	 * @return this
	 */
	B selectAllCheckBoxVisibility(SelectAllCheckBoxVisibility selectAllCheckBoxVisibility);

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
		 * Sets whether the width of the contents in the column which corresponds to given property should be considered
		 * minimum width for this column.
		 * <p>
		 * If this is set to <code>true</code> (default), then a column will not shrink to smaller than the width
		 * required to show the contents available when calculating the widths (only the widths of the initially
		 * rendered rows are considered).
		 * </p>
		 * <p>
		 * If this is set to <code>false</code> and the column has been set to expand using
		 * <code>expandRatio(...)</code>, then the contents of the column will be ignored when calculating the width,
		 * and the column will thus shrink down to the minimum width if necessary.
		 * </p>
		 * @param property Item property (not null)
		 * @param minimumWidthFromContent <code>true</code> to reserve space for all contents, <code>false</code> to
		 *        allow the column to shrink smaller than the contents
		 * @return this
		 */
		B minimumWidthFromContent(P property, boolean minimumWidthFromContent);

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
		 * Set whether the row editor is in buffered mode. In buffered mode, the editor has the <code>SAVE</code> and
		 * <code>CANCEL</code> buttons to commit or discard the item modifications (default mode). In the unbuffered
		 * mode, the editor has no buttons and all changed data is committed directly.
		 * @param editorBuffered whether the row editor is in buffered mode
		 * @return this
		 */
		B editorBuffered(boolean editorBuffered);

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
		 * Register an item editor save listener.
		 * @param listener The listener to add
		 * @return this
		 */
		B withEditorSaveListener(EditorSaveListener<T> listener);

		/**
		 * Register an item editor cancel listener.
		 * @param listener The listener to add
		 * @return this
		 */
		B withEditorCancelListener(EditorCancelListener<T> listener);

		/**
		 * Register an item editor open listener.
		 * @param listener The listener to add
		 * @return this
		 */
		B withEditorOpenListener(EditorOpenListener<T> listener);

		/**
		 * Adds an item {@link Validator} to item listing editor.
		 * @param validator Validator to add (not null)
		 * @return this
		 */
		default B withValidator(Validator<T> validator) {
			return withValidator(new ValidatorWrapper<>(validator));
		}

		/**
		 * Adds an item {@link com.vaadin.data.Validator} to item listing editor.
		 * @param validator Validator to add (not null)
		 * @return this
		 */
		B withValidator(com.vaadin.data.Validator<T> validator);

		/**
		 * Set the given property as required in the item listing editor. If a property is required, the field bound to
		 * the property will be setted as required, and its validation will fail when empty.
		 * @param property Property to set as required (not null)
		 * @return this
		 */
		B required(P property);

		/**
		 * Set the given property as required in the item listing editor. If a property is required, the field bound to
		 * the property will be setted as required, and its validation will fail when empty.
		 * @param property Property to set as required (not null)
		 * @param message The message to use to notify the required validation failure
		 * @return this
		 */
		B required(P property, Localizable message);

		/**
		 * Set the given property as required in the item listing editor. If a property is required, the field bound to
		 * the property will be setted as required, and its validation will fail when empty.
		 * @param property Property to set as required (not null)
		 * @param message The default message to use to notify the required validation failure
		 * @param messageCode The message localization code
		 * @param arguments Optional message translation arguments
		 * @return this
		 */
		default B required(P property, String message, String messageCode, Object... arguments) {
			return required(property, Localizable.builder().message(message).messageCode(messageCode)
					.messageArguments(arguments).build());
		}

		/**
		 * Set the given property as required. If a property is required, the {@link Input} bound to the property will
		 * be setted as required, and its validation will fail when empty.
		 * @param property Property to set as required (not null)
		 * @param message The default message to use to notify the required validation failure
		 * @return this
		 */
		default B required(P property, String message) {
			return required(property, Localizable.builder().message(message).build());
		}

		/**
		 * Sets to call {@link ItemListing#commit()} to confirm item modifications in data source when the editor
		 * <em>Save</em> action is triggered and the listing is in <em>buffered</em> mode.
		 * @return this
		 */
		B commitOnSave();

		/**
		 * Sets to call {@link ItemListing#commit()} to confirm item modifications in data source when an item is
		 * removed using {@link ItemListing#removeItem(Object)} and the listing is in <em>buffered</em> mode.
		 * @return this
		 */
		B commitOnRemove();

		/**
		 * Set the listing header builder to create and manage header rows.
		 * @param builder Header builder (not null)
		 * @return this
		 */
		B header(HeaderBuilder<P> builder);

		/**
		 * Set the listing footer builder to create and manage footer rows
		 * @param builder Footer builder (not null)
		 * @return this
		 */
		B footer(FooterBuilder<P> builder);

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
		 * Set the field to use for given property in edit mode.
		 * @param <E> Editor field type
		 * @param property Item property to set the editor for (not null)
		 * @param editor Editor field (not null)
		 * @return this
		 */
		<E extends HasValue<?> & Component> GridItemListingBuilder<T> editor(String property, E editor);

		/**
		 * Adds a {@link Validator} to the field bound to given <code>property</code> in the item listing editor.
		 * @param property Property (not null)
		 * @param validator Validator to add (not null)
		 * @return this
		 */
		default GridItemListingBuilder<T> withValidator(String property, Validator<?> validator) {
			return withValidator(property, new ValidatorWrapper<>(validator));
		}

		/**
		 * Adds a {@link com.vaadin.data.Validator} to the field bound to given <code>property</code> in the item
		 * listing editor.
		 * @param property Property (not null)
		 * @param validator Validator to add (not null)
		 * @return this
		 */
		GridItemListingBuilder<T> withValidator(String property, com.vaadin.data.Validator<?> validator);

		/**
		 * Set a custom {@link Renderer} for given item property.
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
		 * Removes the row at the given position from the section.
		 * @param rowIndex the position of the row
		 */
		void removeRow(int rowIndex);

		/**
		 * Sets the default row of the section.
		 * @param rowIndex the position of the row
		 */
		void setDefaultRow(int rowIndex);

	}

	/**
	 * A header row in a {@link ItemListing}.
	 * @param <P> Item property type
	 */
	public interface ListingHeaderRow<P> extends Serializable {

		/**
		 * Returns the cell on this row corresponding to the given property id.
		 * @param propertyId the id of the property/column whose header cell to get, not null
		 * @return the header cell
		 * @throws IllegalArgumentException if there is no such column in the grid
		 */
		HeaderCell getCell(P propertyId);

		/**
		 * Merges cells corresponding to the given property ids in the row. Original cells are hidden, and new merged
		 * cell is shown instead. The cell has a width of all merged cells together, inherits styles of the first merged
		 * cell but has empty caption.
		 * @param propertyIdsToMerge the ids of the property/column of the cells that should be merged. The cells should
		 *        not be merged to any other cell set.
		 * @return the remaining visible cell after the merge
		 */
		@SuppressWarnings("unchecked")
		HeaderCell join(P... propertyIdsToMerge);

		/**
		 * Merges column cells in the row. Original cells are hidden, and new merged cell is shown instead. The cell has
		 * a width of all merged cells together, inherits styles of the first merged cell but has empty caption.
		 * @param cellsToMerge the cells which should be merged. The cells should not be merged to any other cell set.
		 * @return the remaining visible cell after the merge
		 */
		HeaderCell join(Set<HeaderCell> cellsToMerge);

		/**
		 * Merges column cells in the row. Original cells are hidden, and new merged cell is shown instead. The cell has
		 * a width of all merged cells together, inherits styles of the first merged cell but has empty caption.
		 * @param cellsToMerge the cells which should be merged. The cells should not be merged to any other cell set.
		 * @return the remaining visible cell after the merge
		 */
		HeaderCell join(HeaderCell... cellsToMerge);

		/**
		 * Returns the custom style name for this row.
		 * @return the style name or null if no style name has been set
		 */
		String getStyleName();

		/**
		 * Sets a custom style name for this row.
		 * @param styleName the style name to set or null to not use any style name
		 */
		void setStyleName(String styleName);

		/**
		 * Gets a collection of all components inside this row.
		 * <p>
		 * The order of the components in the returned collection is not specified.
		 * @return a collection of components in the row
		 */
		Collection<? extends Component> getComponents();

	}

	/**
	 * A footer row in a {@link ItemListing}.
	 * @param <P> Item property type
	 */
	public interface ListingFooterRow<P> extends Serializable {

		/**
		 * Returns the cell on this row corresponding to the given property id.
		 * @param propertyId the id of the property/column whose footer cell to get, not null
		 * @return the footer cell
		 * @throws IllegalArgumentException if there is no such column in the grid
		 */
		FooterCell getCell(P propertyId);

		/**
		 * Merges cells corresponding to the given property ids in the row. Original cells are hidden, and new merged
		 * cell is shown instead. The cell has a width of all merged cells together, inherits styles of the first merged
		 * cell but has empty caption.
		 * @param propertyIdsToMerge the ids of the property/column of the cells that should be merged. The cells should
		 *        not be merged to any other cell set.
		 * @return the remaining visible cell after the merge
		 */
		@SuppressWarnings("unchecked")
		FooterCell join(P... propertyIdsToMerge);

		/**
		 * Merges column cells in the row. Original cells are hidden, and new merged cell is shown instead. The cell has
		 * a width of all merged cells together, inherits styles of the first merged cell but has empty caption.
		 * @param cellsToMerge the cells which should be merged. The cells should not be merged to any other cell set.
		 * @return the remaining visible cell after the merge
		 */
		FooterCell join(Set<FooterCell> cellsToMerge);

		/**
		 * Merges column cells in the row. Original cells are hidden, and new merged cell is shown instead. The cell has
		 * a width of all merged cells together, inherits styles of the first merged cell but has empty caption.
		 * @param cellsToMerge the cells which should be merged. The cells should not be merged to any other cell set.
		 * @return the remaining visible cell after the merge
		 */
		FooterCell join(FooterCell... cellsToMerge);

		/**
		 * Returns the custom style name for this row.
		 * @return the style name or null if no style name has been set
		 */
		String getStyleName();

		/**
		 * Sets a custom style name for this row.
		 * @param styleName the style name to set or null to not use any style name
		 */
		void setStyleName(String styleName);

		/**
		 * Gets a collection of all components inside this row.
		 * <p>
		 * The order of the components in the returned collection is not specified.
		 * @return a collection of components in the row
		 */
		Collection<? extends Component> getComponents();

	}

	/**
	 * Builder to create and manage Header rows.
	 * @param <P> Item property type
	 */
	@FunctionalInterface
	public interface HeaderBuilder<P> {

		/**
		 * Build Grid header rows.
		 * @param header Header rows container
		 */
		void buildHeader(GridSection<ListingHeaderRow<P>> header);

	}

	/**
	 * Builder to create and manage Footer rows.
	 * @param <P> Item property type
	 */
	@FunctionalInterface
	public interface FooterBuilder<P> {

		/**
		 * Build Grid footer rows.
		 * @param footer Footer rows container
		 */
		void buildFooter(GridSection<ListingFooterRow<P>> footer);

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
		void updateFooter(ItemListing<T, P> listing, GridSection<ListingFooterRow<P>> footer);

	}

}
