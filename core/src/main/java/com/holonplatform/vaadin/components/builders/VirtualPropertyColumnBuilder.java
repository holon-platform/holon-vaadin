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
package com.holonplatform.vaadin.components.builders;

import com.holonplatform.core.Path;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.property.VirtualProperty;
import com.holonplatform.core.query.QuerySort;
import com.holonplatform.vaadin.components.ItemListing;
import com.holonplatform.vaadin.components.ItemListing.CellStyleGenerator;
import com.holonplatform.vaadin.components.ItemListing.ColumnAlignment;
import com.holonplatform.vaadin.components.PropertyListing;
import com.holonplatform.vaadin.components.builders.ItemListingBuilder.ColumnHeaderMode;
import com.holonplatform.vaadin.data.ItemDataSource.PropertySortGenerator;
import com.vaadin.data.HasValue;
import com.vaadin.data.ValueProvider;
import com.vaadin.ui.Component;
import com.vaadin.ui.renderers.Renderer;

/**
 * Builder to configure and add a {@link VirtualProperty} column to a {@link PropertyListing} component.
 * 
 * @param <T> Virtual property type
 * @param <I> Item type
 * @param <P> Item property type
 * @param <C> Item listing type
 * @param <B> Parent build type
 * 
 * @since 5.1.4
 */
public interface VirtualPropertyColumnBuilder<T, I, P, C extends ItemListing<I, P>, B extends ItemListingBuilder<I, P, C, B, ?>> {

	/**
	 * Set the column header to show for this virtual property.
	 * @param header Localizable column header (not null)
	 * @return this
	 */
	VirtualPropertyColumnBuilder<T, I, P, C, B> header(Localizable header);

	/**
	 * Set the column header to show for this virtual property.
	 * @param header Column header
	 * @return this
	 */
	default VirtualPropertyColumnBuilder<T, I, P, C, B> header(String header) {
		return header(Localizable.builder().message(header).build());
	}

	/**
	 * Set the column header to show for this virtual property.
	 * @param defaultHeader Default column header
	 * @param headerMessageCode Column header translation message code
	 * @return this
	 */
	default VirtualPropertyColumnBuilder<T, I, P, C, B> header(String defaultHeader, String headerMessageCode) {
		return header(Localizable.builder().message(defaultHeader).messageCode(headerMessageCode).build());
	}

	/**
	 * Set the text alignment for the column which corresponds to this virtual property.
	 * @param alignment Alignment
	 * @return this
	 */
	VirtualPropertyColumnBuilder<T, I, P, C, B> alignment(ColumnAlignment alignment);

	/**
	 * Set the width in pixels for the column which corresponds to this virtual property.
	 * @param widthInPixels Width in pixel
	 * @return this
	 */
	VirtualPropertyColumnBuilder<T, I, P, C, B> width(int widthInPixels);

	/**
	 * Sets whether the column which corresponds to this virtual property can be hidden by the user.
	 * @param hidable <code>true</code> if the column which corresponds to this property can be hidden by the user
	 * @return this
	 */
	VirtualPropertyColumnBuilder<T, I, P, C, B> hidable(boolean hidable);

	/**
	 * Sets whether the column which corresponds to this virtual property is hidden.
	 * @param hidden <code>true</code> if column is hidden
	 * @return this
	 */
	VirtualPropertyColumnBuilder<T, I, P, C, B> hidden(boolean hidden);

	/**
	 * Set the {@link CellStyleGenerator} to call for this virtual property to generate column cell style.
	 * @param cellStyleGenerator Cell style generator (not null)
	 * @return this
	 */
	VirtualPropertyColumnBuilder<T, I, P, C, B> style(CellStyleGenerator<I, P> cellStyleGenerator);

	/**
	 * Set the column cell style name for this virtual property.
	 * @param styleName The property column style name
	 * @return this
	 */
	default VirtualPropertyColumnBuilder<T, I, P, C, B> style(String styleName) {
		return style((p, item) -> styleName);
	}

	/**
	 * Set the minimum width in pixels for the column which corresponds to this virtual property.
	 * @param widthInPixels Minimum width in pixel
	 * @return this
	 */
	VirtualPropertyColumnBuilder<T, I, P, C, B> minWidth(int widthInPixels);

	/**
	 * Set the maximum width in pixels for the column which corresponds to this virtual property.
	 * @param widthInPixels Maximum width in pixel
	 * @return this
	 */
	VirtualPropertyColumnBuilder<T, I, P, C, B> maxWidth(int widthInPixels);

	/**
	 * Set the expandRatio for the column which corresponds to this virtual property.
	 * <p>
	 * By default, all columns expand equally (treated as if all of them had an expand ratio of 1). Once at least one
	 * column gets a defined expand ratio, the implicit expand ratio is removed, and only the defined expand ratios are
	 * taken into account.
	 * </p>
	 * <p>
	 * If a column has a defined width, it overrides this method's effects.
	 * </p>
	 * @param expandRatio Column expand ratio. <code>0</code> to not have it expand at all
	 * @return this
	 */
	VirtualPropertyColumnBuilder<T, I, P, C, B> expandRatio(int expandRatio);

	/**
	 * Sets whether the width of the contents in the column which corresponds to this virtual property should be
	 * considered minimum width for this column.
	 * <p>
	 * If this is set to <code>true</code> (default), then a column will not shrink to smaller than the width required
	 * to show the contents available when calculating the widths (only the widths of the initially rendered rows are
	 * considered).
	 * </p>
	 * <p>
	 * If this is set to <code>false</code> and the column has been set to expand using <code>expandRatio(...)</code>,
	 * then the contents of the column will be ignored when calculating the width, and the column will thus shrink down
	 * to the minimum width if necessary.
	 * </p>
	 * @param minimumWidthFromContent <code>true</code> to reserve space for all contents, <code>false</code> to allow
	 *        the column to shrink smaller than the contents
	 * @return this
	 */
	VirtualPropertyColumnBuilder<T, I, P, C, B> minimumWidthFromContent(boolean minimumWidthFromContent);

	/**
	 * Sets the caption of the hiding toggle for the column which corresponds to this virtual property. Shown in the
	 * toggle for this column in the grid's sidebar when the column is hidable.
	 * @param hidingToggleCaption Localizable hiding toggle caption (not null)
	 * @return this
	 */
	VirtualPropertyColumnBuilder<T, I, P, C, B> hidingToggleCaption(Localizable hidingToggleCaption);

	/**
	 * Sets the caption of the hiding toggle for the column which corresponds to this virtual property. Shown in the
	 * toggle for this column in the grid's sidebar when the column is hidable.
	 * @param hidingToggleCaption Hiding toggle caption
	 * @return this
	 */
	default VirtualPropertyColumnBuilder<T, I, P, C, B> hidingToggleCaption(String hidingToggleCaption) {
		return hidingToggleCaption(Localizable.builder().message(hidingToggleCaption).build());
	}

	/**
	 * Sets the caption of the hiding toggle for the column which corresponds to this virtual property. Shown in the
	 * toggle for this column in the grid's sidebar when the column is hidable.
	 * @param hidingToggleCaption Hiding toggle caption default message
	 * @param messageCode Hiding toggle caption localization message code
	 * @return this
	 */
	default VirtualPropertyColumnBuilder<T, I, P, C, B> hidingToggleCaption(String hidingToggleCaption,
			String messageCode) {
		return hidingToggleCaption(Localizable.builder().message(hidingToggleCaption).messageCode(messageCode).build());
	}

	/**
	 * Set the column header display mode for the column which corresponds to this virtual property.
	 * @param headerMode Column header mode
	 * @return this
	 */
	VirtualPropertyColumnBuilder<T, I, P, C, B> headerMode(ColumnHeaderMode headerMode);

	/**
	 * Set the column header for given this virtual, displaying it as HTML. The property/column {@link ColumnHeaderMode}
	 * will be configured as {@link ColumnHeaderMode#HTML}.
	 * @param header Localizable column header (not null)
	 * @return this
	 * @see #headerMode(ColumnHeaderMode)
	 */
	VirtualPropertyColumnBuilder<T, I, P, C, B> headerHTML(Localizable header);

	/**
	 * Set the column header for given this virtual property, displaying it as HTML. The property/column
	 * {@link ColumnHeaderMode} will be configured as {@link ColumnHeaderMode#HTML}.
	 * @param header Column header
	 * @return this
	 * @see #headerMode(ColumnHeaderMode)
	 */
	default VirtualPropertyColumnBuilder<T, I, P, C, B> headerHTML(String header) {
		return headerHTML(Localizable.builder().message(header).build());
	}

	/**
	 * Set the column header for this virtual property, displaying it as HTML. The property/column
	 * {@link ColumnHeaderMode} will be configured as {@link ColumnHeaderMode#HTML}.
	 * @param defaultHeader Default column header
	 * @param headerMessageCode Column header translation message code
	 * @return this
	 * @see #headerMode(ColumnHeaderMode)
	 */
	default VirtualPropertyColumnBuilder<T, I, P, C, B> headerHTML(String defaultHeader, String headerMessageCode) {
		return headerHTML(Localizable.builder().message(defaultHeader).messageCode(headerMessageCode).build());
	}

	/**
	 * Sets whether the column which corresponds to this virtual property is resizable by the user.
	 * @param resizable <code>true</code> if the column which corresponds to this property is resizable by the user
	 * @return this
	 */
	VirtualPropertyColumnBuilder<T, I, P, C, B> resizable(boolean resizable);

	/**
	 * Declares to use specified {@link Path} to generate query sorts for this virtual property.
	 * @param sortPath Sort path to use (not null)
	 * @return this
	 */
	VirtualPropertyColumnBuilder<T, I, P, C, B> sortUsing(Path<?> sortPath);

	/**
	 * Set a {@link PropertySortGenerator} to generate {@link QuerySort}s for this virtual property.
	 * @param generator PropertySortGenerator (not null)
	 * @return this
	 */
	VirtualPropertyColumnBuilder<T, I, P, C, B> sortGenerator(PropertySortGenerator<P> generator);

	/**
	 * Set a custom {@link Renderer} for this virtual property.
	 * @param renderer Renderer to use
	 * @return this
	 */
	VirtualPropertyColumnBuilder<T, I, P, C, B> render(Renderer<? super T> renderer);

	/**
	 * Set a custom {@link Renderer} and presentation provider for this virtual property.
	 * @param <D> Presentation value type
	 * @param presentationProvider Presentation provider
	 * @param renderer Renderer to use
	 * @return this
	 */
	<D> VirtualPropertyColumnBuilder<T, I, P, C, B> render(ValueProvider<T, D> presentationProvider,
			Renderer<? super D> renderer);

	/**
	 * Set the field to use for this virtual property in edit mode.
	 * @param <E> Editor field type
	 * @param editor Editor field (not null)
	 * @return this
	 */
	<E extends HasValue<T> & Component> VirtualPropertyColumnBuilder<T, I, P, C, B> editor(E editor);

	/**
	 * Configure this virtual property column to be displayed before any other standard listing column by default.
	 * @return this
	 */
	VirtualPropertyColumnBuilder<T, I, P, C, B> displayAsFirst();

	/**
	 * Configure this virtual property column to be displayed after any other standard listing column by default.
	 * @return this
	 */
	VirtualPropertyColumnBuilder<T, I, P, C, B> displayAsLast();

	/**
	 * Configure this virtual property column to be displayed before the listing column bound to given
	 * <code>property</code> by default.
	 * @param property Property to which the position is relative
	 * @return this
	 */
	VirtualPropertyColumnBuilder<T, I, P, C, B> displayBefore(P property);

	/**
	 * Configure this virtual property column to be displayed after the listing column bound to given
	 * <code>property</code> by default.
	 * @param property Property to which the position is relative
	 * @return this
	 */
	VirtualPropertyColumnBuilder<T, I, P, C, B> displayAfter(P property);

	/**
	 * Configure this virtual property column to be displayed before the given listing column id by default.
	 * @param columnId Column id to which the position is relative
	 * @return this
	 */
	VirtualPropertyColumnBuilder<T, I, P, C, B> displayBeforeColumnId(String columnId);

	/**
	 * Configure this virtual property column to be displayed after the given listing column id by default.
	 * @param columnId Column id to which the position is relative
	 * @return this
	 */
	VirtualPropertyColumnBuilder<T, I, P, C, B> displayAfterColumnId(String columnId);

	/**
	 * Add this virtual property column to the {@link PropertyListing}.
	 * <p>
	 * The column will be displayed after any other listing column by default, or according to the position configured
	 * using either {@link #displayAsFirst()}, {@link #displayAsLast()}, {@link #displayBefore(Object)} or
	 * {@link #displayAfter(Object)}.
	 * </p>
	 * @return The parent {@link PropertyListing} builder
	 */
	B add();

}
