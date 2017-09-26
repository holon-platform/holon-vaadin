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

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.vaadin.components.builders.BaseSelectInputBuilder.RenderingMode;
import com.holonplatform.vaadin.components.builders.BaseSelectModeMultiPropertySelectInputBuilder.OptionsModeMultiPropertySelectInputBuilder;
import com.holonplatform.vaadin.components.builders.BaseSelectModeMultiPropertySelectInputBuilder.SelectModeMultiPropertySelectInputBuilder;
import com.holonplatform.vaadin.components.builders.BaseSelectModeMultiSelectInputBuilder.OptionsModeMultiSelectInputBuilder;
import com.holonplatform.vaadin.components.builders.BaseSelectModeMultiSelectInputBuilder.SelectModeMultiSelectInputBuilder;
import com.holonplatform.vaadin.components.builders.MultiPropertySelectInputBuilder.GenericMultiPropertySelectInputBuilder;
import com.holonplatform.vaadin.components.builders.MultiSelectInputBuilder.GenericMultiSelectInputBuilder;
import com.holonplatform.vaadin.internal.components.MultiSelectField;

/**
 * A {@link Selectable} component in which multiple items can be selected at the same time. Selecting an item adds it to
 * the selection.
 * 
 * @param <T> Selection item type
 * 
 * @since 5.0.0
 */
public interface MultiSelect<T> extends Selectable<T>, Input<Set<T>>, ItemSet {

	/**
	 * Adds the given item to the set of currently selected items.
	 * <p>
	 * By default this does not clear any previous selection. To do that, use {@link #deselectAll()}.
	 * <p>
	 * @param items Items to select (not null)
	 */
	void select(Iterable<T> items);

	/**
	 * Adds the given item to the set of currently selected items.
	 * <p>
	 * By default this does not clear any previous selection. To do that, use {@link #deselectAll()}.
	 * <p>
	 * @param items Items to select (not null)
	 */
	@SuppressWarnings("unchecked")
	default void select(T... items) {
		ObjectUtils.argumentNotNull(items, "Items to select must be not null");
		select(Stream.of(items).map(i -> {
			ObjectUtils.argumentNotNull(i, "Items to select must be not null");
			return i;
		}).collect(Collectors.toSet()));
	}

	/**
	 * Removes the given items from the set of currently selected items.
	 * @param items Items to deselect (not null)
	 */
	void deselect(Iterable<T> items);

	/**
	 * Removes the given items from the set of currently selected items.
	 * @param items Items to deselect (not null)
	 */
	@SuppressWarnings("unchecked")
	default void deselect(T... items) {
		ObjectUtils.argumentNotNull(items, "Items to deselect must be not null");
		deselect(Stream.of(items).map(i -> {
			ObjectUtils.argumentNotNull(i, "Items to deselect must be not null");
			return i;
		}).collect(Collectors.toSet()));
	}

	/**
	 * Selects all available the items.
	 */
	void selectAll();

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Selectable#getSelectionMode()
	 */
	@Override
	default SelectionMode getSelectionMode() {
		return SelectionMode.MULTI;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Selectable#getFirstSelectedItem()
	 */
	@Override
	default Optional<T> getFirstSelectedItem() {
		return getSelectedItems().stream().findFirst();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Selectable#select(java.lang.Object)
	 */
	@Override
	default void select(T item) {
		ObjectUtils.argumentNotNull(item, "Item to select must be not null");
		select(Collections.singleton(item));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Selectable#deselect(java.lang.Object)
	 */
	@Override
	default void deselect(T item) {
		ObjectUtils.argumentNotNull(item, "Item to deselect must be not null");
		deselect(Collections.singleton(item));
	}

	// Builders

	/**
	 * Gets a builder to create a {@link MultiSelect}.
	 * @param <T> Selection value type
	 * @param type Selection value type
	 * @param renderingMode Rendering mode
	 * @return {@link MultiSelect} builder
	 */
	static <T> GenericMultiSelectInputBuilder<T> builder(Class<? extends T> type, RenderingMode renderingMode) {
		return new MultiSelectField.GenericBuilder<>(type, renderingMode);
	}

	/**
	 * Gets a builder to create a {@link MultiSelect} using {@link RenderingMode#OPTIONS}.
	 * @param <T> Selection value type
	 * @param type Selection value type
	 * @return {@link MultiSelect} builder
	 */
	static <T> OptionsModeMultiSelectInputBuilder<T> options(Class<? extends T> type) {
		return new MultiSelectField.OptionsModeBuilder<>(type);
	}

	/**
	 * Gets a builder to create a {@link MultiSelect} using {@link RenderingMode#SELECT}.
	 * @param <T> Selection value type
	 * @param type Selection value type
	 * @return {@link MultiSelect} builder
	 */
	static <T> SelectModeMultiSelectInputBuilder<T> list(Class<? extends T> type) {
		return new MultiSelectField.SelectModeBuilder<>(type);
	}

	/**
	 * Gets a builder to create a {@link MultiSelect} with a {@link PropertyBox} items data source with {@link Property}
	 * as item properties.
	 * @param <T> Selection value type
	 * @param selectProperty Property to select (not null)
	 * @param renderingMode Rendering mode
	 * @return {@link MultiSelect} builder
	 */
	static <T> GenericMultiPropertySelectInputBuilder<T> property(Property<T> selectProperty,
			RenderingMode renderingMode) {
		ObjectUtils.argumentNotNull(selectProperty, "Selection property must be not null");
		return new MultiSelectField.GenericPropertyBuilder<>(selectProperty, renderingMode);
	}

	/**
	 * Gets a builder to create a {@link MultiSelect} with a {@link PropertyBox} items data source with {@link Property}
	 * as item properties using {@link RenderingMode#OPTIONS}.
	 * @param <T> Selection value type
	 * @param selectProperty Property to select (not null)
	 * @return {@link MultiSelect} builder
	 */
	static <T> OptionsModeMultiPropertySelectInputBuilder<T> options(Property<T> selectProperty) {
		ObjectUtils.argumentNotNull(selectProperty, "Selection property must be not null");
		return new MultiSelectField.OptionsModePropertyBuilder<>(selectProperty);
	}

	/**
	 * Gets a builder to create a {@link MultiSelect} with a {@link PropertyBox} items data source with {@link Property}
	 * as item properties using {@link RenderingMode#SELECT}.
	 * @param <T> Selection value type
	 * @param selectProperty Property to select (not null)
	 * @return {@link MultiSelect} builder
	 */
	static <T> SelectModeMultiPropertySelectInputBuilder<T> list(Property<T> selectProperty) {
		ObjectUtils.argumentNotNull(selectProperty, "Selection property must be not null");
		return new MultiSelectField.SelectModePropertyBuilder<>(selectProperty);
	}

}
