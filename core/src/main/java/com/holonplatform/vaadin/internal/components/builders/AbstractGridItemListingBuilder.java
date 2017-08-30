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

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.components.ItemListing;
import com.holonplatform.vaadin.components.ItemListing.ItemDetailsGenerator;
import com.holonplatform.vaadin.components.builders.ItemListingBuilder.BaseGridItemListingBuilder;
import com.holonplatform.vaadin.internal.components.DefaultItemListing;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Grid;
import com.vaadin.ui.components.grid.FooterRow;
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

	private com.holonplatform.vaadin.components.builders.ItemListingBuilder.HeaderBuilder headerBuilder;
	private com.holonplatform.vaadin.components.builders.ItemListingBuilder.FooterBuilder footerBuilder;
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
	public B header(com.holonplatform.vaadin.components.builders.ItemListingBuilder.HeaderBuilder builder) {
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
	public B footer(com.holonplatform.vaadin.components.builders.ItemListingBuilder.FooterBuilder builder) {
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
			headerBuilder.buildHeader(new GridHeaderSection(instance.getGrid()));
		}
		if (footerBuilder != null) {
			footerBuilder.buildFooter(new GridFooterSection(instance.getGrid()));
		}

		if (footerGenerator != null) {
			// TODO check
			instance.addDataProviderListener(e -> {
				if (instance.isFooterVisible()) {
					footerGenerator.updateFooter(instance, new GridFooterSection(instance.getGrid()));
				}
			});
		}
	}

	private final static class GridHeaderSection
			implements com.holonplatform.vaadin.components.builders.ItemListingBuilder.GridSection<HeaderRow> {

		private final Grid<?> grid;

		public GridHeaderSection(Grid<?> grid) {
			super();
			this.grid = grid;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.ItemListingBuilder.BaseGridItemListingBuilder.GridSection#
		 * addRowAt(int)
		 */
		@Override
		public HeaderRow addRowAt(int index) {
			return grid.getHeaderRow(index);
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.ItemListingBuilder.BaseGridItemListingBuilder.GridSection#
		 * appendRow()
		 */
		@Override
		public HeaderRow appendRow() {
			return grid.appendHeaderRow();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.ItemListingBuilder.GridSection#getRowAt(int)
		 */
		@Override
		public HeaderRow getRowAt(int index) {
			return grid.getHeaderRow(index);
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.ItemListingBuilder.BaseGridItemListingBuilder.GridSection#
		 * getDefaultHeaderRow()
		 */
		@Override
		public HeaderRow getDefaultRow() {
			return grid.getDefaultHeaderRow();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.ItemListingBuilder.BaseGridItemListingBuilder.GridSection#
		 * getRowCount()
		 */
		@Override
		public int getRowCount() {
			return grid.getHeaderRowCount();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.ItemListingBuilder.BaseGridItemListingBuilder.GridSection#
		 * prependRow()
		 */
		@Override
		public HeaderRow prependRow() {
			return grid.prependHeaderRow();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.ItemListingBuilder.BaseGridItemListingBuilder.GridSection#
		 * removeRow(com.vaadin.ui.Grid.StaticSection.StaticRow)
		 */
		@Override
		public void removeRow(HeaderRow row) {
			grid.removeHeaderRow(row);
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.ItemListingBuilder.BaseGridItemListingBuilder.GridSection#
		 * removeRow(int)
		 */
		@Override
		public void removeRow(int rowIndex) {
			grid.removeHeaderRow(rowIndex);
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.ItemListingBuilder.BaseGridItemListingBuilder.GridSection#
		 * setDefaultRow(com.vaadin.ui.Grid.StaticSection.StaticRow)
		 */
		@Override
		public void setDefaultRow(HeaderRow row) {
			grid.setDefaultHeaderRow(row);
		}

	}

	private final static class GridFooterSection
			implements com.holonplatform.vaadin.components.builders.ItemListingBuilder.GridSection<FooterRow> {

		private final Grid<?> grid;

		public GridFooterSection(Grid<?> grid) {
			super();
			this.grid = grid;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.ItemListingBuilder.BaseGridItemListingBuilder.GridSection#
		 * addRowAt(int)
		 */
		@Override
		public FooterRow addRowAt(int index) {
			return grid.getFooterRow(index);
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.ItemListingBuilder.BaseGridItemListingBuilder.GridSection#
		 * appendRow()
		 */
		@Override
		public FooterRow appendRow() {
			return grid.appendFooterRow();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.ItemListingBuilder.GridSection#getRowAt(int)
		 */
		@Override
		public FooterRow getRowAt(int index) {
			return grid.getFooterRow(index);
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.ItemListingBuilder.BaseGridItemListingBuilder.GridSection#
		 * getDefaultHeaderRow()
		 */
		@Override
		public FooterRow getDefaultRow() {
			throw new UnsupportedOperationException("Grid footer does not support a default row");
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.ItemListingBuilder.BaseGridItemListingBuilder.GridSection#
		 * getRowCount()
		 */
		@Override
		public int getRowCount() {
			return grid.getFooterRowCount();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.ItemListingBuilder.BaseGridItemListingBuilder.GridSection#
		 * prependRow()
		 */
		@Override
		public FooterRow prependRow() {
			return grid.prependFooterRow();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.ItemListingBuilder.BaseGridItemListingBuilder.GridSection#
		 * removeRow(com.vaadin.ui.Grid.StaticSection.StaticRow)
		 */
		@Override
		public void removeRow(FooterRow row) {
			grid.removeFooterRow(row);
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.ItemListingBuilder.BaseGridItemListingBuilder.GridSection#
		 * removeRow(int)
		 */
		@Override
		public void removeRow(int rowIndex) {
			grid.removeFooterRow(rowIndex);
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.ItemListingBuilder.BaseGridItemListingBuilder.GridSection#
		 * setDefaultRow(com.vaadin.ui.Grid.StaticSection.StaticRow)
		 */
		@Override
		public void setDefaultRow(FooterRow row) {
			throw new UnsupportedOperationException("Grid footer does not support a default row");
		}

	}

}
