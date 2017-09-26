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

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.vaadin.components.builders.BaseSelectInputBuilder.RenderingMode;
import com.holonplatform.vaadin.components.builders.BaseSelectModeSinglePropertySelectInputBuilder.NativeModeSinglePropertySelectInputBuilder;
import com.holonplatform.vaadin.components.builders.BaseSelectModeSinglePropertySelectInputBuilder.OptionsModeSinglePropertySelectInputBuilder;
import com.holonplatform.vaadin.components.builders.BaseSelectModeSinglePropertySelectInputBuilder.SelectModeSinglePropertySelectInputBuilder;
import com.holonplatform.vaadin.components.builders.BaseSelectModeSingleSelectInputBuilder.NativeModeSingleSelectInputBuilder;
import com.holonplatform.vaadin.components.builders.BaseSelectModeSingleSelectInputBuilder.OptionsModeSingleSelectInputBuilder;
import com.holonplatform.vaadin.components.builders.BaseSelectModeSingleSelectInputBuilder.SelectModeSingleSelectInputBuilder;
import com.holonplatform.vaadin.components.builders.SinglePropertySelectInputBuilder.GenericSinglePropertySelectInputBuilder;
import com.holonplatform.vaadin.components.builders.SingleSelectInputBuilder.GenericSingleSelectInputBuilder;
import com.holonplatform.vaadin.internal.components.SingleSelectField;

/**
 * A {@link Selectable} component in which at most one item can be selected at a time.
 * 
 * @param <T> Selection item type
 * 
 * @since 5.0.0
 */
public interface SingleSelect<T> extends Selectable<T>, Input<T>, ItemSet {

	/**
	 * Get the currently selected item.
	 * @return The currently selected item, empty if no item is selected
	 */
	Optional<T> getSelectedItem();

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Selectable#getSelectionMode()
	 */
	@Override
	default SelectionMode getSelectionMode() {
		return SelectionMode.SINGLE;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Selectable#getSelectedItems()
	 */
	@Override
	default Set<T> getSelectedItems() {
		return getSelectedItem().map(Collections::singleton).orElse(Collections.emptySet());
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Selectable#getFirstSelectedItem()
	 */
	@Override
	default Optional<T> getFirstSelectedItem() {
		return getSelectedItem();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Selectable#deselectAll()
	 */
	@Override
	default void deselectAll() {
		getSelectedItem().ifPresent(this::deselect);
	}

	// Builders

	/**
	 * Gets a builder to create a {@link SingleSelect}.
	 * @param <T> Selection value type
	 * @param type Selection value type
	 * @param renderingMode Rendering mode
	 * @return {@link SingleSelect} builder
	 */
	static <T> GenericSingleSelectInputBuilder<T> builder(Class<? extends T> type, RenderingMode renderingMode) {
		return new SingleSelectField.GenericBuilder<>(type, renderingMode);
	}

	/**
	 * Gets a builder to create a {@link SingleSelect} using {@link RenderingMode#SELECT}.
	 * @param <T> Selection value type
	 * @param type Selection value type
	 * @return {@link SingleSelect} builder
	 */
	static <T> SelectModeSingleSelectInputBuilder<T> select(Class<? extends T> type) {
		return new SingleSelectField.SelectModeBuilder<>(type);
	}

	/**
	 * Gets a builder to create a {@link SingleSelect} using {@link RenderingMode#NATIVE_SELECT}.
	 * @param <T> Selection value type
	 * @param type Selection value type
	 * @return {@link SingleSelect} builder
	 */
	static <T> NativeModeSingleSelectInputBuilder<T> nativeSelect(Class<? extends T> type) {
		return new SingleSelectField.NativeModeBuilder<>(type);
	}

	/**
	 * Gets a builder to create a {@link SingleSelect} using {@link RenderingMode#OPTIONS}.
	 * @param <T> Selection value type
	 * @param type Selection value type
	 * @return {@link SingleSelect} builder
	 */
	static <T> OptionsModeSingleSelectInputBuilder<T> options(Class<? extends T> type) {
		return new SingleSelectField.OptionsModeBuilder<>(type);
	}

	/**
	 * Gets a builder to create a {@link SingleSelect} with a {@link PropertyBox} items data source with
	 * {@link Property} as item properties.
	 * @param <T> Selection value type
	 * @param selectProperty Property to select (not null)
	 * @param renderingMode Rendering mode
	 * @return {@link SingleSelect} builder
	 */
	static <T> GenericSinglePropertySelectInputBuilder<T> property(Property<T> selectProperty,
			RenderingMode renderingMode) {
		ObjectUtils.argumentNotNull(selectProperty, "Selection property must be not null");
		return new SingleSelectField.GenericPropertyBuilder<>(selectProperty, renderingMode);
	}

	/**
	 * Gets a builder to create a {@link SingleSelect} with a {@link PropertyBox} items data source with
	 * {@link Property} as item properties using {@link RenderingMode#SELECT}.
	 * @param <T> Selection value type
	 * @param selectProperty Property to select (not null)
	 * @return {@link SingleSelect} builder
	 */
	static <T> SelectModeSinglePropertySelectInputBuilder<T> select(Property<T> selectProperty) {
		ObjectUtils.argumentNotNull(selectProperty, "Selection property must be not null");
		return new SingleSelectField.SelectModePropertyBuilder<>(selectProperty);
	}

	/**
	 * Gets a builder to create a {@link SingleSelect} with a {@link PropertyBox} items data source with
	 * {@link Property} as item properties using {@link RenderingMode#NATIVE_SELECT}.
	 * @param <T> Selection value type
	 * @param selectProperty Property to select (not null)
	 * @return {@link SingleSelect} builder
	 */
	static <T> NativeModeSinglePropertySelectInputBuilder<T> nativeSelect(Property<T> selectProperty) {
		ObjectUtils.argumentNotNull(selectProperty, "Selection property must be not null");
		return new SingleSelectField.NativeModePropertyBuilder<>(selectProperty);
	}

	/**
	 * Gets a builder to create a {@link SingleSelect} with a {@link PropertyBox} items data source with
	 * {@link Property} as item properties using {@link RenderingMode#OPTIONS}.
	 * @param <T> Selection value type
	 * @param selectProperty Property to select (not null)
	 * @return {@link SingleSelect} builder
	 */
	static <T> OptionsModeSinglePropertySelectInputBuilder<T> options(Property<T> selectProperty) {
		ObjectUtils.argumentNotNull(selectProperty, "Selection property must be not null");
		return new SingleSelectField.OptionsModePropertyBuilder<>(selectProperty);
	}

}
