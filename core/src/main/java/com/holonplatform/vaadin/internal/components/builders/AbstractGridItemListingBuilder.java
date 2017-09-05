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
package com.holonplatform.vaadin.internal.components.builders;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.components.ItemListing;
import com.holonplatform.vaadin.components.ItemListing.ItemDetailsGenerator;
import com.holonplatform.vaadin.components.builders.ItemListingBuilder.BaseGridItemListingBuilder;
import com.holonplatform.vaadin.internal.components.DefaultItemListing;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.components.grid.EditorCancelListener;
import com.vaadin.ui.components.grid.EditorOpenListener;
import com.vaadin.ui.components.grid.EditorSaveListener;
import com.vaadin.ui.components.grid.FooterCell;
import com.vaadin.ui.components.grid.FooterRow;
import com.vaadin.ui.components.grid.HeaderCell;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.components.grid.MultiSelectionModel.SelectAllCheckBoxVisibility;

/**
 * {@link BaseGridItemListingBuilder} implementation.
 * 
 * @param <T> Item data type
 * @param <P> Item property type
 * @param <C> Component type
 * @param <I> Internal instance
 * @param <B> Concrete builder type
 *
 * @since 5.0.0
 */
public abstract class AbstractGridItemListingBuilder<T, P, C extends ItemListing<T, P>, I extends DefaultItemListing<T, P>, B extends BaseGridItemListingBuilder<T, P, C, B>>
		extends AbstractItemListingBuilder<T, P, C, I, B, Grid<T>> implements BaseGridItemListingBuilder<T, P, C, B> {

	private int frozenColumns = 0;

	private HeaderBuilder<P> headerBuilder;
	private FooterBuilder<P> footerBuilder;
	private com.holonplatform.vaadin.components.builders.ItemListingBuilder.GridFooterGenerator<T, P> footerGenerator;

	private Localizable editorSaveCaption;
	private Localizable editorCancelCaption;

	public AbstractGridItemListingBuilder(I instance) {
		super(instance);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentBuilder#localize(com.vaadin.ui.
	 * AbstractComponent)
	 */
	@Override
	protected void localize(I instance) {
		super.localize(instance);
		if (editorSaveCaption != null) {
			getInstance().setEditorSaveCaption(LocalizationContext.translate(editorSaveCaption, true));
		}
		if (editorCancelCaption != null) {
			getInstance().setEditorCancelCaption(LocalizationContext.translate(editorCancelCaption, true));
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ItemListingBuilder#selectAllCheckBoxVisibility(com.vaadin.ui.
	 * components.grid.MultiSelectionModel.SelectAllCheckBoxVisibility)
	 */
	@Override
	public B selectAllCheckBoxVisibility(SelectAllCheckBoxVisibility selectAllCheckBoxVisibility) {
		getInstance().setSelectAllCheckBoxVisibility(selectAllCheckBoxVisibility);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.ItemListingBuilder.BaseGridItemListingBuilder#heightByContents()
	 */
	@Override
	public B heightByContents() {
		getInstance().setHeightMode(HeightMode.UNDEFINED);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.ItemListingBuilder.BaseGridItemListingBuilder#heightByRows(double)
	 */
	@Override
	public B heightByRows(double rows) {
		getInstance().setHeightMode(HeightMode.ROW);
		getInstance().setHeightByRows(rows);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ItemListingBuilder#rowHeight(double)
	 */
	@Override
	public B rowHeight(double rowHeight) {
		getInstance().setRowHeight(rowHeight);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.ItemListingBuilder.BaseGridItemListingBuilder#minWidth(java.lang.
	 * Object, int)
	 */
	@Override
	public B minWidth(P property, int widthInPixels) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		getInstance().getPropertyColumn(property).setMinWidth(widthInPixels);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.ItemListingBuilder.BaseGridItemListingBuilder#maxWidth(java.lang.
	 * Object, int)
	 */
	@Override
	public B maxWidth(P property, int widthInPixels) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		getInstance().getPropertyColumn(property).setMaxWidth(widthInPixels);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.ItemListingBuilder.BaseGridItemListingBuilder#expandRatio(java.lang
	 * .Object, int)
	 */
	@Override
	public B expandRatio(P property, int expandRatio) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		getInstance().getPropertyColumn(property).setExpandRatio(expandRatio);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.ItemListingBuilder.BaseGridItemListingBuilder#hidingToggleCaption(
	 * java.lang.Object, com.holonplatform.core.i18n.Localizable)
	 */
	@Override
	public B hidingToggleCaption(P property, Localizable hidingToggleCaption) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		getInstance().getPropertyColumn(property).setHidingToggleCaption(hidingToggleCaption);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.ItemListingBuilder.BaseGridItemListingBuilder#resizable(java.lang.
	 * Object, boolean)
	 */
	@Override
	public B resizable(P property, boolean resizable) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		getInstance().getPropertyColumn(property).setResizable(resizable);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.ItemListingBuilder.BaseGridItemListingBuilder#frozenColumns(int)
	 */
	@Override
	public B frozenColumns(int frozenColumnsCount) {
		this.frozenColumns = frozenColumnsCount;
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ItemListingBuilder.BaseGridItemListingBuilder#editorBuffered(
	 * boolean)
	 */
	@Override
	public B editorBuffered(boolean editorBuffered) {
		getInstance().setEditorBuffered(editorBuffered);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.ItemListingBuilder.BaseGridItemListingBuilder#editorSaveCaption(com
	 * .holonframework.core.i18n.Localizable)
	 */
	@Override
	public B editorSaveCaption(Localizable caption) {
		ObjectUtils.argumentNotNull(caption, "Caption must be not null");
		this.editorSaveCaption = caption;
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.ItemListingBuilder.BaseGridItemListingBuilder#editorCancelCaption(
	 * com.holonplatform.core.i18n.Localizable)
	 */
	@Override
	public B editorCancelCaption(Localizable caption) {
		ObjectUtils.argumentNotNull(caption, "Caption must be not null");
		this.editorCancelCaption = caption;
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.ItemListingBuilder.BaseGridItemListingBuilder#withEditorSaveListener
	 * (com.vaadin.ui.components.grid.EditorSaveListener)
	 */
	@Override
	public B withEditorSaveListener(EditorSaveListener<T> listener) {
		ObjectUtils.argumentNotNull(listener, "EditorSaveListener must be not null");
		getInstance().addEditorSaveListener(listener);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ItemListingBuilder.BaseGridItemListingBuilder#
	 * withEditorCancelListener(com.vaadin.ui.components.grid.EditorCancelListener)
	 */
	@Override
	public B withEditorCancelListener(EditorCancelListener<T> listener) {
		ObjectUtils.argumentNotNull(listener, "EditorCancelListener must be not null");
		getInstance().addEditorCancelListener(listener);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.ItemListingBuilder.BaseGridItemListingBuilder#withEditorOpenListener
	 * (com.vaadin.ui.components.grid.EditorOpenListener)
	 */
	@Override
	public B withEditorOpenListener(EditorOpenListener<T> listener) {
		ObjectUtils.argumentNotNull(listener, "EditorOpenListener must be not null");
		getInstance().addEditorOpenListener(listener);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.ItemListingBuilder.BaseGridItemListingBuilder#required(java.lang.
	 * Object)
	 */
	@Override
	public B required(P property) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		getInstance().getPropertyColumn(property).setRequired(true);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.ItemListingBuilder.BaseGridItemListingBuilder#required(java.lang.
	 * Object, com.holonplatform.core.i18n.Localizable)
	 */
	@Override
	public B required(P property, Localizable message) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		getInstance().getPropertyColumn(property).setRequired(true);
		getInstance().getPropertyColumn(property).setRequiredMessage(message);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ItemListingBuilder.BaseGridItemListingBuilder#commitOnSave()
	 */
	@Override
	public B commitOnSave() {
		getInstance().setCommitOnSave(true);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ItemListingBuilder.BaseGridItemListingBuilder#commitOnRemove()
	 */
	@Override
	public B commitOnRemove() {
		getInstance().setCommitOnRemove(true);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ItemListingBuilder.BaseGridItemListingBuilder#header(com.
	 * holonframework.vaadin.components.builders.ItemListingBuilder.BaseGridItemListingBuilder.HeaderBuilder)
	 */
	@Override
	public B header(HeaderBuilder<P> builder) {
		ObjectUtils.argumentNotNull(builder, "Builder must be not null");
		this.headerBuilder = builder;
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ItemListingBuilder.BaseGridItemListingBuilder#footer(com.
	 * holonframework.vaadin.components.builders.ItemListingBuilder.BaseGridItemListingBuilder.FooterBuilder)
	 */
	@Override
	public B footer(FooterBuilder<P> builder) {
		ObjectUtils.argumentNotNull(builder, "Builder must be not null");
		this.footerBuilder = builder;
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.ItemListingBuilder.BaseGridItemListingBuilder#footerGenerator(com.
	 * holonframework.vaadin.components.ItemListing.GridFooterGenerator)
	 */
	@Override
	public B footerGenerator(
			com.holonplatform.vaadin.components.builders.ItemListingBuilder.GridFooterGenerator<T, P> footerGenerator) {
		ObjectUtils.argumentNotNull(footerGenerator, "Generator must be not null");
		this.footerGenerator = footerGenerator;
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.ItemListingBuilder.BaseGridItemListingBuilder#detailsGenerator(com.
	 * holonframework.vaadin.components.ItemListing.ItemDetailsGenerator)
	 */
	@Override
	public B detailsGenerator(final ItemDetailsGenerator<T> detailsGenerator) {
		getInstance().setDetailsGenerator(detailsGenerator);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.internal.components.builders.AbstractItemListingBuilder#configure(com.holonplatform.
	 * vaadin.internal.components.DefaultItemListing)
	 */
	@Override
	protected void configure(I instance) {
		// frozen columns
		if (frozenColumns != 0) {
			instance.setFrozenColumnCount(frozenColumns);
		}

		// header and footer
		if (headerBuilder != null) {
			headerBuilder.buildHeader(new GridHeaderSection<>(instance.getGrid(), id -> instance.getColumnId(id)));
		}
		if (footerBuilder != null) {
			footerBuilder.buildFooter(new GridFooterSection<>(instance.getGrid(), id -> instance.getColumnId(id)));
		}

		if (footerGenerator != null) {
			instance.addDataProviderListener(e -> {
				if (instance.isFooterVisible()) {
					footerGenerator.updateFooter(instance,
							new GridFooterSection<>(instance.getGrid(), id -> instance.getColumnId(id)));
				}
			});
		}
	}

	private final static class GridHeaderSection<P> implements GridSection<ListingHeaderRow<P>> {

		private final Grid<?> grid;
		private final Function<P, String> converter;

		public GridHeaderSection(Grid<?> grid, Function<P, String> converter) {
			super();
			this.grid = grid;
			this.converter = converter;
		}

		@Override
		public ListingHeaderRow<P> addRowAt(int index) {
			return new HeaderRowWrapper<>(grid.getHeaderRow(index), converter);
		}

		@Override
		public ListingHeaderRow<P> appendRow() {
			return new HeaderRowWrapper<>(grid.appendHeaderRow(), converter);
		}

		@Override
		public ListingHeaderRow<P> getRowAt(int index) {
			return new HeaderRowWrapper<>(grid.getHeaderRow(index), converter);
		}

		@Override
		public ListingHeaderRow<P> getDefaultRow() {
			return new HeaderRowWrapper<>(grid.getDefaultHeaderRow(), converter);
		}

		@Override
		public int getRowCount() {
			return grid.getHeaderRowCount();
		}

		@Override
		public ListingHeaderRow<P> prependRow() {
			return new HeaderRowWrapper<>(grid.prependHeaderRow(), converter);
		}

		@Override
		public void removeRow(int rowIndex) {
			grid.removeHeaderRow(rowIndex);
		}

		@Override
		public void setDefaultRow(int rowIndex) {
			grid.setDefaultHeaderRow(grid.getHeaderRow(rowIndex));
		}

	}

	private final static class GridFooterSection<P> implements GridSection<ListingFooterRow<P>> {

		private final Grid<?> grid;
		private final Function<P, String> converter;

		public GridFooterSection(Grid<?> grid, Function<P, String> converter) {
			super();
			this.grid = grid;
			this.converter = converter;
		}

		@Override
		public ListingFooterRow<P> addRowAt(int index) {
			return new FooterRowWrapper<>(grid.getFooterRow(index), converter);
		}

		@Override
		public ListingFooterRow<P> appendRow() {
			return new FooterRowWrapper<>(grid.appendFooterRow(), converter);
		}

		@Override
		public ListingFooterRow<P> getRowAt(int index) {
			return new FooterRowWrapper<>(grid.getFooterRow(index), converter);
		}

		@Override
		public ListingFooterRow<P> getDefaultRow() {
			throw new UnsupportedOperationException("Grid footer does not support a default row");
		}

		@Override
		public int getRowCount() {
			return grid.getFooterRowCount();
		}

		@Override
		public ListingFooterRow<P> prependRow() {
			return new FooterRowWrapper<>(grid.prependFooterRow(), converter);
		}

		@Override
		public void removeRow(int rowIndex) {
			grid.removeFooterRow(rowIndex);
		}

		@Override
		public void setDefaultRow(int rowIndex) {
			throw new UnsupportedOperationException("Grid footer does not support a default row");
		}

	}

	@SuppressWarnings("serial")
	private static final class HeaderRowWrapper<P> implements ListingHeaderRow<P> {

		private final HeaderRow row;
		private final Function<P, String> converter;

		public HeaderRowWrapper(HeaderRow row, Function<P, String> converter) {
			super();
			this.row = row;
			this.converter = converter;
		}

		@Override
		public HeaderCell getCell(P propertyId) {
			return row.getCell(converter.apply(propertyId));
		}

		@SuppressWarnings("unchecked")
		@Override
		public HeaderCell join(P... propertyIdsToMerge) {
			ObjectUtils.argumentNotNull(propertyIdsToMerge, "Property ids to merge must be not null");
			return row.join(Arrays.asList(propertyIdsToMerge).stream().map(id -> converter.apply(id))
					.collect(Collectors.toList()).toArray(new String[0]));
		}

		@Override
		public HeaderCell join(Set<HeaderCell> cellsToMerge) {
			return row.join(cellsToMerge);
		}

		@Override
		public HeaderCell join(HeaderCell... cellsToMerge) {
			return row.join(cellsToMerge);
		}

		@Override
		public String getStyleName() {
			return row.getStyleName();
		}

		@Override
		public void setStyleName(String styleName) {
			row.setStyleName(styleName);
		}

		@Override
		public Collection<? extends Component> getComponents() {
			return row.getComponents();
		}

	}

	@SuppressWarnings("serial")
	private static final class FooterRowWrapper<P> implements ListingFooterRow<P> {

		private final FooterRow row;
		private final Function<P, String> converter;

		public FooterRowWrapper(FooterRow row, Function<P, String> converter) {
			super();
			this.row = row;
			this.converter = converter;
		}

		@Override
		public FooterCell getCell(P propertyId) {
			return row.getCell(converter.apply(propertyId));
		}

		@SuppressWarnings("unchecked")
		@Override
		public FooterCell join(P... propertyIdsToMerge) {
			ObjectUtils.argumentNotNull(propertyIdsToMerge, "Property ids to merge must be not null");
			return row.join(Arrays.asList(propertyIdsToMerge).stream().map(id -> converter.apply(id))
					.collect(Collectors.toList()).toArray(new String[0]));
		}

		@Override
		public FooterCell join(Set<FooterCell> cellsToMerge) {
			return row.join(cellsToMerge);
		}

		@Override
		public FooterCell join(FooterCell... cellsToMerge) {
			return row.join(cellsToMerge);
		}

		@Override
		public String getStyleName() {
			return row.getStyleName();
		}

		@Override
		public void setStyleName(String styleName) {
			row.setStyleName(styleName);
		}

		@Override
		public Collection<? extends Component> getComponents() {
			return row.getComponents();
		}

	}

}
