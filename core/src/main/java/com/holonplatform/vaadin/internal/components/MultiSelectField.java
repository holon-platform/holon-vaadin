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
package com.holonplatform.vaadin.internal.components;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.query.QueryConfigurationProvider;
import com.holonplatform.vaadin.components.Field;
import com.holonplatform.vaadin.components.MultiSelect;
import com.holonplatform.vaadin.components.builders.BaseSelectInputBuilder.RenderingMode;
import com.holonplatform.vaadin.components.builders.BaseSelectModeMultiPropertySelectInputBuilder.OptionsModeMultiPropertySelectInputBuilder;
import com.holonplatform.vaadin.components.builders.BaseSelectModeMultiPropertySelectInputBuilder.SelectModeMultiPropertySelectInputBuilder;
import com.holonplatform.vaadin.components.builders.BaseSelectModeMultiSelectInputBuilder.OptionsModeMultiSelectInputBuilder;
import com.holonplatform.vaadin.components.builders.BaseSelectModeMultiSelectInputBuilder.SelectModeMultiSelectInputBuilder;
import com.holonplatform.vaadin.components.builders.MultiPropertySelectInputBuilder;
import com.holonplatform.vaadin.components.builders.MultiPropertySelectInputBuilder.GenericMultiPropertySelectInputBuilder;
import com.holonplatform.vaadin.components.builders.MultiSelectInputBuilder;
import com.holonplatform.vaadin.components.builders.MultiSelectInputBuilder.GenericMultiSelectInputBuilder;
import com.holonplatform.vaadin.data.ItemConverter;
import com.holonplatform.vaadin.data.ItemDataProvider;
import com.holonplatform.vaadin.internal.components.builders.AbstractSelectFieldBuilder;
import com.holonplatform.vaadin.internal.data.PropertyItemIdentifier;
import com.vaadin.data.Converter;
import com.vaadin.data.HasDataProvider;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.Query;
import com.vaadin.event.selection.MultiSelectionEvent;
import com.vaadin.server.SerializableFunction;
import com.vaadin.server.SerializablePredicate;
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
			field.setItemDescriptionGenerator(i -> generateItemDescription(i));
			field.addSelectionListener(e -> fireSelectionListeners(buildSelectionEvent(e)));
			return field;
		}

		final ListSelect<ITEM> field = new ListSelect<>();
		field.setItemCaptionGenerator(i -> generateItemCaption(i));
		field.addSelectionListener(e -> fireSelectionListeners(buildSelectionEvent(e)));
		return field;
	}

	protected SelectionEvent<T> buildSelectionEvent(MultiSelectionEvent<ITEM> event) {
		return new DefaultSelectionEvent<>(fromInternalValue(event.getAllSelectedItems()), event.isUserOriginated());
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
	public void setDataProvider(DataProvider<ITEM, ?> dataProvider, SerializableFunction<String, ?> filterConverter) {
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

	public void setRows(int rows) {
		if (getInternalField() instanceof ListSelect) {
			((ListSelect<?>) getInternalField()).setRows(rows);
		}
	}

	public void setHtmlContentAllowed(boolean htmlContentAllowed) {
		if (getInternalField() instanceof CheckBoxGroup) {
			((CheckBoxGroup<?>) getInternalField()).setHtmlContentAllowed(htmlContentAllowed);
		}
	}

	@SuppressWarnings("unchecked")
	public void setItemEnabledProvider(SerializablePredicate<T> itemEnabledProvider) {
		if (getInternalField() instanceof CheckBoxGroup) {
			((CheckBoxGroup<T>) getInternalField()).setItemEnabledProvider(itemEnabledProvider);
		}
	}

	// Builder

	/**
	 * Base {@link MultiSelect} builder.
	 * @param <T> Selection type
	 */
	public static abstract class Builder<T, B extends MultiSelectInputBuilder<T, B>>
			extends AbstractSelectFieldBuilder<Set<T>, MultiSelect<T>, T, T, MultiSelectField<T, T>, B>
			implements MultiSelectInputBuilder<T, B> {

		/**
		 * Constructor
		 * @param type Selection value type
		 * @param renderingMode Rendering mode
		 */
		public Builder(Class<? extends T> type, RenderingMode renderingMode) {
			super(new MultiSelectField<>(type, renderingMode));
			getInstance().setItemConverter(Converter.identity());
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.builders.MultiSelectFieldBuilder#dataSource(com.holonplatform.vaadin.
		 * data.ItemDataProvider)
		 */
		@Override
		public B dataSource(ItemDataProvider<T> dataProvider) {
			ObjectUtils.argumentNotNull(dataProvider, "Item data provider must be not null");
			this.itemDataProvider = dataProvider;
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.builders.SelectItemDataSourceBuilder#dataSource(com.vaadin.data.provider.
		 * DataProvider)
		 */
		@Override
		public B dataSource(DataProvider<T, ?> dataProvider) {
			this.dataProvider = dataProvider;
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.SelectFieldBuilder#items(java.lang.Iterable)
		 */
		@Override
		public B items(Iterable<T> items) {
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
		public B addItem(T item) {
			ObjectUtils.argumentNotNull(item, "Item must be not null");
			this.items.add(item);
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

	public static class GenericBuilder<T> extends Builder<T, GenericMultiSelectInputBuilder<T>>
			implements GenericMultiSelectInputBuilder<T> {

		public GenericBuilder(Class<? extends T> type, RenderingMode renderingMode) {
			super(type, renderingMode);
		}

		@Override
		protected GenericMultiSelectInputBuilder<T> builder() {
			return this;
		}

	}

	public static class SelectModeBuilder<T> extends Builder<T, SelectModeMultiSelectInputBuilder<T>>
			implements SelectModeMultiSelectInputBuilder<T> {

		public SelectModeBuilder(Class<? extends T> type) {
			super(type, RenderingMode.SELECT);
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.BaseSelectModeMultiSelectInputBuilder.
		 * SelectModeMultiSelectInputBuilder#rows(int)
		 */
		@Override
		public SelectModeMultiSelectInputBuilder<T> rows(int rows) {
			getInstance().setRows(rows);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentConfigurator#builder()
		 */
		@Override
		protected SelectModeMultiSelectInputBuilder<T> builder() {
			return this;
		}

	}

	public static class OptionsModeBuilder<T> extends Builder<T, OptionsModeMultiSelectInputBuilder<T>>
			implements OptionsModeMultiSelectInputBuilder<T> {

		public OptionsModeBuilder(Class<? extends T> type) {
			super(type, RenderingMode.OPTIONS);
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.BaseSelectModeMultiSelectInputBuilder.
		 * OptionsModeMultiSelectInputBuilder#htmlContentAllowed(boolean)
		 */
		@Override
		public OptionsModeMultiSelectInputBuilder<T> htmlContentAllowed(boolean htmlContentAllowed) {
			getInstance().setHtmlContentAllowed(htmlContentAllowed);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.BaseSelectModeMultiSelectInputBuilder.
		 * OptionsModeMultiSelectInputBuilder#itemEnabledProvider(com.vaadin.server.SerializablePredicate)
		 */
		@Override
		public OptionsModeMultiSelectInputBuilder<T> itemEnabledProvider(SerializablePredicate<T> itemEnabledProvider) {
			ObjectUtils.argumentNotNull(itemEnabledProvider, "ItemEnabledProvider must be not null");
			getInstance().setItemEnabledProvider(itemEnabledProvider);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentConfigurator#builder()
		 */
		@Override
		protected OptionsModeMultiSelectInputBuilder<T> builder() {
			return this;
		}

	}

	// Property

	/**
	 * Base {@link MultiSelect} builder with {@link Property} data source support.
	 * @param <T> Selection type
	 */
	public static abstract class PropertyBuilder<T, B extends MultiPropertySelectInputBuilder<T, B>> extends
			AbstractSelectFieldBuilder<Set<T>, MultiSelect<T>, T, PropertyBox, MultiSelectField<T, PropertyBox>, B>
			implements MultiPropertySelectInputBuilder<T, B> {

		private final Property<T> selectProperty;

		/**
		 * Constructor
		 * @param selectProperty Selection (and identifier) property
		 * @param renderingMode Rendering mode
		 */
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public PropertyBuilder(Property<T> selectProperty, RenderingMode renderingMode) {
			super(new MultiSelectField<>(selectProperty.getType(), renderingMode));
			this.selectProperty = selectProperty;
			itemIdentifier = new PropertyItemIdentifier(selectProperty);
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.PropertySelectInputBuilder#itemConverter(com.holonplatform.
		 * vaadin.data.ItemConverter)
		 */
		@Override
		public B itemConverter(ItemConverter<T, PropertyBox> itemConverter) {
			getInstance().setItemConverter(new ReversiblePropertyBoxConverter<>(selectProperty, itemConverter));
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.builders.PropertySelectFieldBuilder#dataSource(com.holonplatform.vaadin
		 * .data.ItemDataProvider)
		 */
		@Override
		public B dataSource(ItemDataProvider<PropertyBox> dataProvider) {
			ObjectUtils.argumentNotNull(dataProvider, "ItemDataProvider must be not null");
			this.itemDataProvider = dataProvider;
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.builders.PropertySelectInputBuilder#dataSource(com.holonplatform.core.
		 * datastore.Datastore, com.holonplatform.core.datastore.DataTarget, java.lang.Iterable,
		 * com.holonplatform.core.query.QueryConfigurationProvider[])
		 */
		@Override
		public <P extends Property<?>> B dataSource(Datastore datastore, DataTarget<?> dataTarget,
				Iterable<P> properties, QueryConfigurationProvider... queryConfigurationProviders) {
			MultiPropertySelectInputBuilder.super.dataSource(datastore, dataTarget, properties,
					queryConfigurationProviders);
			setupItemConverter(datastore, dataTarget, properties);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.builders.PropertySelectInputBuilder#dataSource(com.holonplatform.core.
		 * datastore.Datastore, com.holonplatform.core.datastore.DataTarget, com.holonplatform.core.property.Property[])
		 */
		@SuppressWarnings("unchecked")
		@Override
		public <P extends Property<?>> B dataSource(Datastore datastore, DataTarget<?> dataTarget, P... properties) {
			MultiPropertySelectInputBuilder.super.dataSource(datastore, dataTarget, properties);
			setupItemConverter(datastore, dataTarget, PropertySet.of(properties));
			return builder();
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		protected <P extends Property<?>> void setupItemConverter(Datastore datastore, DataTarget<?> dataTarget,
				Iterable<P> properties) {
			if (selectProperty != null && PathProperty.class.isAssignableFrom(selectProperty.getClass())
					&& !getInstance().getItemConverter().isPresent()) {
				itemConverter(value -> {
					if (value != null) {
						return datastore.query().target(dataTarget).filter(((PathProperty) selectProperty).eq(value))
								.findOne(properties).orElse(null);
					}
					return null;
				});
			}
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.internal.components.builders.AbstractSelectFieldBuilder#preSetup(com.holonplatform.
		 * vaadin.internal.components.AbstractSelectField)
		 */
		@Override
		protected void preSetup(MultiSelectField<T, PropertyBox> instance) {
			if (!instance.getItemConverter().isPresent()) {
				instance.setItemConverter(new DefaultPropertyBoxConverter<>(selectProperty));
			}
			super.preSetup(instance);
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

	}

	public static class GenericPropertyBuilder<T> extends PropertyBuilder<T, GenericMultiPropertySelectInputBuilder<T>>
			implements GenericMultiPropertySelectInputBuilder<T> {

		public GenericPropertyBuilder(Property<T> selectProperty, RenderingMode renderingMode) {
			super(selectProperty, renderingMode);
		}

		@Override
		protected GenericMultiPropertySelectInputBuilder<T> builder() {
			return this;
		}

	}

	public static class SelectModePropertyBuilder<T>
			extends PropertyBuilder<T, SelectModeMultiPropertySelectInputBuilder<T>>
			implements SelectModeMultiPropertySelectInputBuilder<T> {

		public SelectModePropertyBuilder(Property<T> selectProperty) {
			super(selectProperty, RenderingMode.SELECT);
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.BaseSelectModeMultiPropertySelectInputBuilder.
		 * SelectModeMultiPropertySelectInputBuilder#rows(int)
		 */
		@Override
		public SelectModeMultiPropertySelectInputBuilder<T> rows(int rows) {
			getInstance().setRows(rows);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentConfigurator#builder()
		 */
		@Override
		protected SelectModeMultiPropertySelectInputBuilder<T> builder() {
			return this;
		}

	}

	public static class OptionsModePropertyBuilder<T>
			extends PropertyBuilder<T, OptionsModeMultiPropertySelectInputBuilder<T>>
			implements OptionsModeMultiPropertySelectInputBuilder<T> {

		public OptionsModePropertyBuilder(Property<T> selectProperty) {
			super(selectProperty, RenderingMode.OPTIONS);
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.BaseSelectModeMultiPropertySelectInputBuilder.
		 * OptionsModeMultiPropertySelectInputBuilder#htmlContentAllowed(boolean)
		 */
		@Override
		public OptionsModeMultiPropertySelectInputBuilder<T> htmlContentAllowed(boolean htmlContentAllowed) {
			getInstance().setHtmlContentAllowed(htmlContentAllowed);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.BaseSelectModeMultiPropertySelectInputBuilder.
		 * OptionsModeMultiPropertySelectInputBuilder#itemEnabledProvider(com.vaadin.server.SerializablePredicate)
		 */
		@Override
		public OptionsModeMultiPropertySelectInputBuilder<T> itemEnabledProvider(
				SerializablePredicate<T> itemEnabledProvider) {
			ObjectUtils.argumentNotNull(itemEnabledProvider, "ItemEnabledProvider must be not null");
			getInstance().setItemEnabledProvider(itemEnabledProvider);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentConfigurator#builder()
		 */
		@Override
		protected OptionsModeMultiPropertySelectInputBuilder<T> builder() {
			return this;
		}

	}

}
