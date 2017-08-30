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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.vaadin.components.Field;
import com.holonplatform.vaadin.components.MultiSelect;
import com.holonplatform.vaadin.components.builders.BaseSelectInputBuilder.RenderingMode;
import com.holonplatform.vaadin.components.builders.MultiPropertySelectInputBuilder;
import com.holonplatform.vaadin.components.builders.MultiSelectInputBuilder;
import com.holonplatform.vaadin.data.ItemDataProvider;
import com.holonplatform.vaadin.internal.components.builders.AbstractSelectFieldBuilder;
import com.holonplatform.vaadin.internal.data.PropertyItemIdentifier;
import com.vaadin.data.Converter;
import com.vaadin.data.HasDataProvider;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.Query;
import com.vaadin.server.SerializableFunction;
import com.vaadin.ui.AbstractMultiSelect;
import com.vaadin.ui.CheckBoxGroup;
import com.vaadin.ui.ComboBox.CaptionFilter;
import com.vaadin.ui.ListSelect;

/**
 * Default multiple select {@link Field} implementation.
 * 
 * @param <T> Field type
 * 
 * @since 5.0.0
 */
public class MultiSelectField<T, ITEM>
		extends AbstractSelectField<Set<T>, T, ITEM, Set<ITEM>, AbstractMultiSelect<ITEM>> implements MultiSelect<T> {

	private static final long serialVersionUID = -7662977233168084151L;

	/**
	 * Constructor
	 * @param type Selection value type
	 * @param renderingMode Rendering mode
	 */
	@SuppressWarnings("unchecked")
	public MultiSelectField(Class<? extends T> type, RenderingMode renderingMode) {
		super((Class<? extends Set<T>>) (Class<?>) Set.class, renderingMode);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.AbstractCustomField#buildInternalField(java.lang.Class)
	 */
	@Override
	protected AbstractMultiSelect<ITEM> buildInternalField(Class<? extends Set<T>> type) {
		RenderingMode mode = getRenderingMode();
		if (mode == null) {
			mode = RenderingMode.OPTIONS;
		}

		if (mode == RenderingMode.OPTIONS) {
			final CheckBoxGroup<ITEM> field = new CheckBoxGroup<>();
			field.setItemCaptionGenerator(i -> generateItemCaption(i));
			field.setItemIconGenerator(i -> generateItemIcon(i));
			field.addSelectionListener(e -> fireSelectionListeners());
			return field;
		}

		final ListSelect<ITEM> field = new ListSelect<>();
		field.setItemCaptionGenerator(i -> generateItemCaption(i));
		field.addSelectionListener(e -> fireSelectionListeners());
		return field;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.AbstractCustomField#fromInternalValue(java.lang.Object)
	 */
	@Override
	protected Set<T> fromInternalValue(Set<ITEM> value) {
		if (value != null) {
			final Set<T> set = new HashSet<>(value.size());
			value.forEach(v -> set.add(toSelection(v)));
			return set;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.AbstractCustomField#toInternalValue(java.lang.Object)
	 */
	@Override
	protected Set<ITEM> toInternalValue(Set<T> value) {
		if (value != null) {
			final Set<ITEM> set = new HashSet<>(value.size());
			value.forEach(v -> set.add(toItem(v)));
			return set;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.AbstractSelectField#setItems(java.util.Collection)
	 */
	@Override
	public void setItems(Collection<ITEM> items) {
		getInternalField().setItems(items);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.AbstractSelectField#setDataProvider(com.vaadin.data.provider.
	 * DataProvider)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void setDataProvider(DataProvider<ITEM, ?> dataProvider) {
		if (getInternalField() instanceof HasDataProvider) {
			((HasDataProvider) getInternalField()).setDataProvider(dataProvider);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.AbstractSelectField#setDataProvider(com.vaadin.data.provider.
	 * DataProvider, com.vaadin.server.SerializableFunction)
	 */
	@Override
	public void setDataProvider(DataProvider<ITEM, QueryFilter> dataProvider,
			SerializableFunction<String, QueryFilter> filterConverter) {
		setDataProvider(dataProvider);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.AbstractSelectField#setItems(java.util.Collection,
	 * com.vaadin.ui.ComboBox.CaptionFilter)
	 */
	@Override
	public void setItems(Collection<ITEM> items, CaptionFilter filter) {
		setItems(items);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.AbstractSelectField#getDataProvider()
	 */
	@Override
	public Optional<DataProvider<ITEM, ?>> getDataProvider() {
		return Optional.ofNullable(getInternalField().getDataProvider());
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Selectable#getSelectedItems()
	 */
	@Override
	public Set<T> getSelectedItems() {
		final Set<T> value = getValue();
		return (value != null) ? Collections.unmodifiableSet(value) : Collections.emptySet();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Selectable#deselectAll()
	 */
	@Override
	public void deselectAll() {
		clear();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.MultiSelect#select(java.lang.Iterable)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void select(Iterable<T> items) {
		ObjectUtils.argumentNotNull(items, "Items to select must be not null");
		final Set<T> set = new HashSet<>();
		items.forEach(i -> {
			ObjectUtils.argumentNotNull(i, "Items to select must be not null");
			set.add(i);
		});
		Set<ITEM> toSelect = toInternalValue(set);
		getInternalField().select((ITEM[]) toSelect.toArray());
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.MultiSelect#deselect(java.lang.Iterable)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void deselect(Iterable<T> items) {
		ObjectUtils.argumentNotNull(items, "Items to deselect must be not null");
		final Set<T> set = new HashSet<>();
		items.forEach(i -> {
			if (i != null) {
				set.add(i);
			}
		});
		Set<ITEM> toDeselect = toInternalValue(set);
		getInternalField().deselect((ITEM[]) toDeselect.toArray());
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.MultiSelect#selectAll()
	 */
	@Override
	public void selectAll() {
		getDataProvider().ifPresent(dp -> {
			Stream<ITEM> allItemsStream = dp.fetch(new Query<>());
			LinkedHashSet<ITEM> allItems = new LinkedHashSet<>();
			allItemsStream.forEach(allItems::add);
			getInternalField().setValue(allItems);
		});
	}

	// Builder

	/**
	 * Base {@link MultiSelect} builder.
	 * @param <T> Selection type
	 */
	public static class Builder<T> extends
			AbstractSelectFieldBuilder<Set<T>, MultiSelect<T>, T, T, MultiSelectField<T, T>, MultiSelectInputBuilder<T>>
			implements MultiSelectInputBuilder<T> {

		/**
		 * Constructor
		 * @param type Selection value type
		 * @param renderingMode Rendering mode
		 */
		public Builder(Class<? extends T> type, RenderingMode renderingMode) {
			super(new MultiSelectField<>(type, renderingMode));
			itemConverter(Converter.identity());
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentBuilder#builder()
		 */
		@Override
		protected Builder<T> builder() {
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.builders.MultiSelectFieldBuilder#dataSource(com.holonplatform.vaadin.
		 * data.ItemDataProvider)
		 */
		@Override
		public MultiSelectInputBuilder<T> dataSource(ItemDataProvider<T> dataProvider) {
			ObjectUtils.argumentNotNull(dataProvider, "Item data provider must be not null");
			this.itemDataProvider = dataProvider;
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.SelectFieldBuilder#items(java.lang.Iterable)
		 */
		@Override
		public MultiSelectInputBuilder<T> items(Iterable<T> items) {
			ObjectUtils.argumentNotNull(items, "Items must be not null");
			this.items.clear();
			items.forEach(i -> this.items.add(i));
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.SelectFieldBuilder#addItem(java.lang.Object)
		 */
		@Override
		public MultiSelectInputBuilder<T> addItem(T item) {
			ObjectUtils.argumentNotNull(item, "Item must be not null");
			this.items.add(item);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.SelectItemDataSourceBuilder#itemConverter(com.vaadin.data.
		 * Converter)
		 */
		@Override
		public MultiSelectInputBuilder<T> itemConverter(Converter<T, T> converter) {
			getInstance().setItemConverter(converter);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.internal.components.builders.AbstractSelectFieldBuilder#buildSelect(com.
		 * holonframework.vaadin.internal.components.AbstractSelectField)
		 */
		@Override
		protected MultiSelect<T> buildSelect(MultiSelectField<T, T> instance) {
			return instance;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.internal.components.builders.AbstractSelectFieldBuilder#buildSelectAsField(com.
		 * holonplatform.vaadin.internal.components.AbstractSelectField)
		 */
		@Override
		protected Field<Set<T>> buildSelectAsField(MultiSelectField<T, T> instance) {
			return instance;
		}

	}

	/**
	 * Base {@link MultiSelect} builder with {@link Property} data source support.
	 * @param <T> Selection type
	 */
	public static class PropertyBuilder<T> extends
			AbstractSelectFieldBuilder<Set<T>, MultiSelect<T>, T, PropertyBox, MultiSelectField<T, PropertyBox>, MultiPropertySelectInputBuilder<T>>
			implements MultiPropertySelectInputBuilder<T> {

		/**
		 * Constructor
		 * @param selectProperty Selection (and identifier) property
		 * @param renderingMode Rendering mode
		 */
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public PropertyBuilder(Property<T> selectProperty, RenderingMode renderingMode) {
			super(new MultiSelectField<>(selectProperty.getType(), renderingMode));
			itemIdentifier = new PropertyItemIdentifier(selectProperty);
			itemConverter(new DefaultPropertyBoxConverter<>(selectProperty));
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.builders.PropertySelectFieldBuilder#dataSource(com.holonplatform.vaadin
		 * .data.ItemDataProvider)
		 */
		@Override
		public MultiPropertySelectInputBuilder<T> dataSource(ItemDataProvider<PropertyBox> dataProvider) {
			ObjectUtils.argumentNotNull(dataProvider, "ItemDataProvider must be not null");
			this.itemDataProvider = dataProvider;
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.internal.components.builders.AbstractSelectFieldBuilder#buildSelect(com.
		 * holonframework.vaadin.internal.components.AbstractSelectField)
		 */
		@Override
		protected MultiSelect<T> buildSelect(MultiSelectField<T, PropertyBox> instance) {
			return instance;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.internal.components.builders.AbstractSelectFieldBuilder#buildSelectAsField(com.
		 * holonplatform.vaadin.internal.components.AbstractSelectField)
		 */
		@Override
		protected Field<Set<T>> buildSelectAsField(MultiSelectField<T, PropertyBox> instance) {
			return instance;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentConfigurator#builder()
		 */
		@Override
		protected MultiPropertySelectInputBuilder<T> builder() {
			return this;
		}

	}

}
