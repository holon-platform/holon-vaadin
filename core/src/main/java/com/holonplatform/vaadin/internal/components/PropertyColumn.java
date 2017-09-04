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
package com.holonplatform.vaadin.internal.components;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.components.ItemListing;
import com.holonplatform.vaadin.components.ItemListing.CellStyleGenerator;
import com.holonplatform.vaadin.components.ItemListing.ColumnAlignment;
import com.vaadin.data.HasValue;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueProvider;
import com.vaadin.server.Resource;
import com.vaadin.ui.Component;
import com.vaadin.ui.renderers.Renderer;

/**
 * Interface to collect and provide {@link ItemListing} column properties and settings.
 * 
 * @param <T> Item data type
 * @param <P> Item property type
 * 
 * @since 5.0.0
 */
public interface PropertyColumn<T, P> extends Serializable {
	
	/**
	 * Gets column caption (header)
	 * @return Column caption (header)
	 */
	Localizable getCaption();

	/**
	 * Sets column caption (header)
	 * @param caption Column caption (header)
	 */
	void setCaption(Localizable caption);

	/**
	 * Gets column content alignment mode
	 * @return Column content alignment mode
	 */
	ColumnAlignment getAlignment();

	/**
	 * Sets column content alignment mode
	 * @param alignment Column content alignment mode
	 */
	void setAlignment(ColumnAlignment alignment);

	/**
	 * Gets column width in pixels
	 * @return Column width in pixels
	 */
	int getWidth();

	/**
	 * Sets column width in pixels
	 * @param width Column width in pixels
	 */
	void setWidth(int width);

	/**
	 * Gets minimum column width in pixels
	 * @return Minimum column width in pixels
	 */
	int getMinWidth();

	/**
	 * Sets minimum column width in pixels
	 * @param minWidth Minimum column width in pixels
	 */
	void setMinWidth(int minWidth);

	/**
	 * Gets maximum column width in pixels
	 * @return Maximum column width in pixels
	 */
	int getMaxWidth();

	/**
	 * Sets maximum column width in pixels
	 * @param maxWidth Maximum column width in pixels
	 */
	void setMaxWidth(int maxWidth);

	/**
	 * Gets column expand ratio
	 * @return Grid column expand ratio
	 */
	int getExpandRatio();

	/**
	 * Sets column expand ratio
	 * @param gridExpandRatio Grid column expand ratio
	 */
	void setExpandRatio(int gridExpandRatio);

	/**
	 * Gets whether the column is editable
	 * @return <code>true</code> if the column is editable, <code>false</code> otherwise (read-only)
	 */
	boolean isEditable();

	/**
	 * Sets whether the column is editable
	 * @param editable <code>true</code> if the column is editable, <code>false</code> otherwise
	 */
	void setEditable(boolean editable);
	
	/**
	 * Get the editor field to use for this property column.
	 * @return the optional editor field
	 */
	<E extends HasValue<?> & Component> Optional<E> getEditor();
	
	/**
	 * Set the editor field to use for this property column.
	 * @param editor Editor field
	 */
	<E extends HasValue<?> & Component> void setEditor(E editor);
	
	void addValidator(Validator<?> validator);
	
	List<Validator<?>> getValidators();

	/**
	 * Gets whether the column is initially hidden when rendered in table
	 * @return <code>true</code> if the column is initially hidden when rendered in table
	 */
	boolean isHidden();

	/**
	 * Sets whether the column is initially hidden when rendered in table
	 * @param hidden <code>true</code> if the column must be initially hidden when rendered in table
	 */
	void setHidden(boolean hidden);

	/**
	 * Gets whether the column visibility can be toggled by user
	 * @return <code>true</code> if the column visibility can be toggled by user
	 */
	boolean isHidable();

	/**
	 * Sets whether the column visibility can be controlled by user
	 * @param hidable <code>true</code> if the column visibility can be controlled by user
	 */
	void setHidable(boolean hidable);

	/**
	 * Gets the caption to use for the table menu with which the user can control column visibility
	 * @return The caption to use for the table menu with which the user can control column visibility
	 */
	Optional<Localizable> getHidingToggleCaption();

	/**
	 * Sets the caption to use for the table menu with which the user can control column visibility
	 * @param hidingToggleCaption The caption to use for the table menu with which the user can control column
	 *        visibility
	 */
	void setHidingToggleCaption(Localizable hidingToggleCaption);

	/**
	 * Gets whether the column can be resized by user
	 * @return <code>true</code> if the column can be resized by user
	 */
	boolean isResizable();

	/**
	 * Sets whether the column can be resized by user
	 * @param resizable <code>true</code> if the column can be resized by user, <code>false</code> otherwise
	 */
	void setResizable(boolean resizable);

	/**
	 * Gets the column header icon
	 * @return The column header icon
	 */
	Optional<Resource> getIcon();

	/**
	 * Sets the column header icon
	 * @param icon The column header icon to set
	 */
	void setIcon(Resource icon);

	/**
	 * Gets an optional {@link CellStyleGenerator} to generate column's cells style
	 * @return {@link CellStyleGenerator} to generate column's cells style
	 */
	Optional<CellStyleGenerator<T, P>> getStyle();

	/**
	 * Sets a {@link CellStyleGenerator} to generate column's cells style
	 * @param cellStyleGenerator The {@link CellStyleGenerator} to generate column's cells style
	 */
	void setStyle(CellStyleGenerator<T, P> cellStyleGenerator);

	Optional<ValueProvider<?, ?>> getPresentationProvider();

	void setPresentationProvider(ValueProvider<?, ?> presentationProvider);

	/**
	 * Gets the {@link Renderer} to use to display column value
	 * @return The {@link Renderer} to use to display column value
	 */
	Optional<Renderer<?>> getRenderer();

	/**
	 * Sets the {@link Renderer} to use to display column value
	 * @param renderer The {@link Renderer} to use to display column value
	 */
	void setRenderer(Renderer<?> renderer);

}
