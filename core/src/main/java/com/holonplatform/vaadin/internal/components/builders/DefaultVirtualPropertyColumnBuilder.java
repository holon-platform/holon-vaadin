/*
 * Copyright 2016-2018 Axioma srl.
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

import com.holonplatform.core.Path;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.query.QuerySort;
import com.holonplatform.vaadin.components.ItemListing;
import com.holonplatform.vaadin.components.ItemListing.CellStyleGenerator;
import com.holonplatform.vaadin.components.ItemListing.ColumnAlignment;
import com.holonplatform.vaadin.components.builders.ItemListingBuilder;
import com.holonplatform.vaadin.components.builders.ItemListingBuilder.ColumnHeaderMode;
import com.holonplatform.vaadin.components.builders.VirtualPropertyColumnBuilder;
import com.holonplatform.vaadin.data.ItemDataSource.PropertySortGenerator;
import com.holonplatform.vaadin.internal.components.DefaultPropertyColumn;
import com.holonplatform.vaadin.internal.components.PropertyColumn;
import com.holonplatform.vaadin.internal.components.PropertyColumn.DisplayPosition;
import com.holonplatform.vaadin.internal.components.PropertyColumnManager;
import com.vaadin.data.HasValue;
import com.vaadin.data.ValueProvider;
import com.vaadin.ui.Component;
import com.vaadin.ui.renderers.Renderer;

/**
 * Default {@link VirtualPropertyColumnBuilder} implementation.
 * 
 * @param <T> Virtual property type
 * @param <I> Item type
 * @param <P> Item property type
 * @param <C> Item listing type
 * @param <B> Parent build type
 * 
 * @since 5.1.4
 */
public class DefaultVirtualPropertyColumnBuilder<T, I, P, C extends ItemListing<I, P>, B extends ItemListingBuilder<I, P, C, B, ?>>
		implements VirtualPropertyColumnBuilder<T, I, P, C, B> {

	private final B parent;
	private final PropertyColumnManager<I, P> columnManager;
	private final P property;
	private final PropertyColumn<I, P> column;

	/**
	 * Constructor.
	 * @param parent Parent builder (not null)
	 * @param columnManager Property column definitions manager (not null)
	 * @param property Virtual property (not null)
	 */
	public DefaultVirtualPropertyColumnBuilder(B parent, PropertyColumnManager<I, P> columnManager, P property) {
		super();
		ObjectUtils.argumentNotNull(parent, "Parent builder must be not null");
		ObjectUtils.argumentNotNull(columnManager, "PropertyColumnManager must be not null");
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		this.parent = parent;
		this.columnManager = columnManager;
		this.property = property;
		this.column = new DefaultPropertyColumn<>(property, true);
		this.column.setDisplayPosition(DisplayPosition.DEFAULT);
		this.columnManager.addColumnDefinition(getProperty(), getColumn());
	}

	/**
	 * Get the parent builder.
	 * @return the parent builder
	 */
	protected B getParent() {
		return parent;
	}

	/**
	 * Get the property column definitions manager.
	 * @return the property column definitions manager
	 */
	protected PropertyColumnManager<I, P> getColumnManager() {
		return columnManager;
	}

	/**
	 * Get the virtual property.
	 * @return the property
	 */
	protected P getProperty() {
		return property;
	}

	/**
	 * Get the property column definition.
	 * @return the column definition
	 */
	protected PropertyColumn<I, P> getColumn() {
		return column;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.PropertyListingBuilder.VirtualPropertyColumnBuilder#header(com.
	 * holonplatform.core.i18n.Localizable)
	 */
	@Override
	public VirtualPropertyColumnBuilder<T, I, P, C, B> header(Localizable header) {
		ObjectUtils.argumentNotNull(header, "Header must be not null");
		column.setCaption(header);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.PropertyListingBuilder.VirtualPropertyColumnBuilder#alignment(com.
	 * holonplatform.vaadin.components.ItemListing.ColumnAlignment)
	 */
	@Override
	public VirtualPropertyColumnBuilder<T, I, P, C, B> alignment(ColumnAlignment alignment) {
		ObjectUtils.argumentNotNull(alignment, "Aligment must be not null");
		column.setAlignment(alignment);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.PropertyListingBuilder.VirtualPropertyColumnBuilder#width(int)
	 */
	@Override
	public VirtualPropertyColumnBuilder<T, I, P, C, B> width(int widthInPixels) {
		column.setWidth(widthInPixels);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.PropertyListingBuilder.VirtualPropertyColumnBuilder#hidable(boolean)
	 */
	@Override
	public VirtualPropertyColumnBuilder<T, I, P, C, B> hidable(boolean hidable) {
		column.setHidable(hidable);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.PropertyListingBuilder.VirtualPropertyColumnBuilder#hidden(boolean)
	 */
	@Override
	public VirtualPropertyColumnBuilder<T, I, P, C, B> hidden(boolean hidden) {
		column.setHidden(hidden);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.PropertyListingBuilder.VirtualPropertyColumnBuilder#style(com.
	 * holonplatform.vaadin.components.ItemListing.CellStyleGenerator)
	 */
	@Override
	public VirtualPropertyColumnBuilder<T, I, P, C, B> style(CellStyleGenerator<I, P> cellStyleGenerator) {
		column.setStyle(cellStyleGenerator);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.PropertyListingBuilder.VirtualPropertyColumnBuilder#minWidth(int)
	 */
	@Override
	public VirtualPropertyColumnBuilder<T, I, P, C, B> minWidth(int widthInPixels) {
		column.setMinWidth(widthInPixels);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.PropertyListingBuilder.VirtualPropertyColumnBuilder#maxWidth(int)
	 */
	@Override
	public VirtualPropertyColumnBuilder<T, I, P, C, B> maxWidth(int widthInPixels) {
		column.setMaxWidth(widthInPixels);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.PropertyListingBuilder.VirtualPropertyColumnBuilder#expandRatio(int)
	 */
	@Override
	public VirtualPropertyColumnBuilder<T, I, P, C, B> expandRatio(int expandRatio) {
		column.setExpandRatio(expandRatio);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.PropertyListingBuilder.VirtualPropertyColumnBuilder#
	 * minimumWidthFromContent(boolean)
	 */
	@Override
	public VirtualPropertyColumnBuilder<T, I, P, C, B> minimumWidthFromContent(boolean minimumWidthFromContent) {
		column.setMinimumWidthFromContent(minimumWidthFromContent);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.PropertyListingBuilder.VirtualPropertyColumnBuilder#
	 * hidingToggleCaption(com.holonplatform.core.i18n.Localizable)
	 */
	@Override
	public VirtualPropertyColumnBuilder<T, I, P, C, B> hidingToggleCaption(Localizable hidingToggleCaption) {
		column.setHidingToggleCaption(hidingToggleCaption);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.PropertyListingBuilder.VirtualPropertyColumnBuilder#headerMode(com.
	 * holonplatform.vaadin.components.builders.ItemListingBuilder.ColumnHeaderMode)
	 */
	@Override
	public VirtualPropertyColumnBuilder<T, I, P, C, B> headerMode(ColumnHeaderMode headerMode) {
		column.setColumnHeaderMode(headerMode);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.PropertyListingBuilder.VirtualPropertyColumnBuilder#headerHTML(com.
	 * holonplatform.core.i18n.Localizable)
	 */
	@Override
	public VirtualPropertyColumnBuilder<T, I, P, C, B> headerHTML(Localizable header) {
		column.setColumnHeaderMode(ColumnHeaderMode.HTML);
		return header(header);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.PropertyListingBuilder.VirtualPropertyColumnBuilder#resizable(
	 * boolean)
	 */
	@Override
	public VirtualPropertyColumnBuilder<T, I, P, C, B> resizable(boolean resizable) {
		column.setResizable(resizable);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.PropertyListingBuilder.VirtualPropertyColumnBuilder#sortUsing(com.
	 * holonplatform.core.Path)
	 */
	@Override
	public VirtualPropertyColumnBuilder<T, I, P, C, B> sortUsing(Path<?> sortPath) {
		column.setPropertySortGenerator((p, asc) -> QuerySort.of(sortPath, asc));
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.PropertyListingBuilder.VirtualPropertyColumnBuilder#sortGenerator(
	 * com.holonplatform.vaadin.data.ItemDataSource.PropertySortGenerator)
	 */
	@Override
	public VirtualPropertyColumnBuilder<T, I, P, C, B> sortGenerator(PropertySortGenerator<P> generator) {
		column.setPropertySortGenerator(generator);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.PropertyListingBuilder.VirtualPropertyColumnBuilder#render(com.
	 * vaadin.ui.renderers.Renderer)
	 */
	@Override
	public VirtualPropertyColumnBuilder<T, I, P, C, B> render(Renderer<? super T> renderer) {
		column.setRenderer(renderer);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.PropertyListingBuilder.VirtualPropertyColumnBuilder#render(com.
	 * vaadin.data.ValueProvider, com.vaadin.ui.renderers.Renderer)
	 */
	@Override
	public <D> VirtualPropertyColumnBuilder<T, I, P, C, B> render(ValueProvider<T, D> presentationProvider,
			Renderer<? super D> renderer) {
		column.setPresentationProvider(presentationProvider);
		column.setRenderer(renderer);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.PropertyListingBuilder.VirtualPropertyColumnBuilder#editor(com.
	 * vaadin.data.HasValue)
	 */
	@Override
	public <E extends HasValue<T> & Component> VirtualPropertyColumnBuilder<T, I, P, C, B> editor(E editor) {
		column.setEditor(editor);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.VirtualPropertyColumnBuilder#displayAsFirst()
	 */
	@Override
	public VirtualPropertyColumnBuilder<T, I, P, C, B> displayAsFirst() {
		column.setDisplayPosition(DisplayPosition.HEAD);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.VirtualPropertyColumnBuilder#displayAsLast()
	 */
	@Override
	public VirtualPropertyColumnBuilder<T, I, P, C, B> displayAsLast() {
		column.setDisplayPosition(DisplayPosition.TAIL);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.VirtualPropertyColumnBuilder#displayBefore(java.lang.Object)
	 */
	@Override
	public VirtualPropertyColumnBuilder<T, I, P, C, B> displayBefore(P property) {
		column.setDisplayPosition(DisplayPosition.RELATIVE_BEFORE);
		column.setDisplayRelativeTo(property);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.VirtualPropertyColumnBuilder#displayAfter(java.lang.Object)
	 */
	@Override
	public VirtualPropertyColumnBuilder<T, I, P, C, B> displayAfter(P property) {
		column.setDisplayPosition(DisplayPosition.RELATIVE_AFTER);
		column.setDisplayRelativeTo(property);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.VirtualPropertyColumnBuilder#displayBeforeColumnId(java.lang.String)
	 */
	@Override
	public VirtualPropertyColumnBuilder<T, I, P, C, B> displayBeforeColumnId(String columnId) {
		column.setDisplayPosition(DisplayPosition.RELATIVE_BEFORE);
		column.setDisplayRelativeToColumnId(columnId);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.VirtualPropertyColumnBuilder#displayAfterColumnId(java.lang.String)
	 */
	@Override
	public VirtualPropertyColumnBuilder<T, I, P, C, B> displayAfterColumnId(String columnId) {
		column.setDisplayPosition(DisplayPosition.RELATIVE_AFTER);
		column.setDisplayRelativeToColumnId(columnId);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.PropertyListingBuilder.VirtualPropertyColumnBuilder#add()
	 */
	@Override
	public B add() {
		return getParent();
	}

}
