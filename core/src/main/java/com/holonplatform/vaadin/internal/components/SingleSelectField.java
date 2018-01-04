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
import java.util.Optional;

import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.query.QueryConfigurationProvider;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.vaadin.components.Field;
import com.holonplatform.vaadin.components.SingleSelect;
import com.holonplatform.vaadin.components.builders.BaseSelectInputBuilder.RenderingMode;
import com.holonplatform.vaadin.components.builders.BaseSelectModeSinglePropertySelectInputBuilder.NativeModeSinglePropertySelectInputBuilder;
import com.holonplatform.vaadin.components.builders.BaseSelectModeSinglePropertySelectInputBuilder.OptionsModeSinglePropertySelectInputBuilder;
import com.holonplatform.vaadin.components.builders.BaseSelectModeSinglePropertySelectInputBuilder.SelectModeSinglePropertySelectInputBuilder;
import com.holonplatform.vaadin.components.builders.BaseSelectModeSingleSelectInputBuilder.NativeModeSingleSelectInputBuilder;
import com.holonplatform.vaadin.components.builders.BaseSelectModeSingleSelectInputBuilder.OptionsModeSingleSelectInputBuilder;
import com.holonplatform.vaadin.components.builders.BaseSelectModeSingleSelectInputBuilder.SelectModeSingleSelectInputBuilder;
import com.holonplatform.vaadin.components.builders.SelectInputBuilder;
import com.holonplatform.vaadin.components.builders.SinglePropertySelectInputBuilder;
import com.holonplatform.vaadin.components.builders.SinglePropertySelectInputBuilder.GenericSinglePropertySelectInputBuilder;
import com.holonplatform.vaadin.components.builders.SingleSelectInputBuilder;
import com.holonplatform.vaadin.components.builders.SingleSelectInputBuilder.GenericSingleSelectInputBuilder;
import com.holonplatform.vaadin.data.ItemConverter;
import com.holonplatform.vaadin.data.ItemDataProvider;
import com.holonplatform.vaadin.internal.components.builders.AbstractSelectFieldBuilder;
import com.holonplatform.vaadin.internal.data.ItemDataProviderAdapter;
import com.holonplatform.vaadin.internal.data.PropertyItemIdentifier;
import com.vaadin.data.Converter;
import com.vaadin.data.HasDataProvider;
import com.vaadin.data.HasFilterableDataProvider;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.event.selection.SingleSelectionEvent;
import com.vaadin.server.SerializableFunction;
import com.vaadin.server.SerializablePredicate;
import com.vaadin.ui.AbstractSingleSelect;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.ComboBox.CaptionFilter;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.RadioButtonGroup;

/**
 * Default single select {@link Field} implementation.
 * 
 * @param <T> Field type
 * 
 * @since 5.0.0
 */
public class SingleSelectField<T, ITEM> extends AbstractSelectField<T, T, ITEM, ITEM, AbstractSingleSelect<ITEM>>
		implements SingleSelect<T> {

	private static final long serialVersionUID = -52116276228997170L;

	/**
	 * Constructor
	 * @param type Selection value type
	 * @param renderingMode Rendering mode
	 */
	public SingleSelectField(Class<? extends T> type, RenderingMode renderingMode) {
		super(type, renderingMode);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.AbstractCustomField#buildInternalField(java.lang.Class)
	 */
	@Override
	protected AbstractSingleSelect<ITEM> buildInternalField(Class<? extends T> type) {
		RenderingMode mode = getRenderingMode();
		if (mode == null) {
			mode = RenderingMode.SELECT;
		}

		if (mode == RenderingMode.NATIVE_SELECT) {
			final NativeSelect<ITEM> field = new NativeSelect<>();
			field.setItemCaptionGenerator(i -> generateItemCaption(i));
			field.addSelectionListener(e -> setupDescriptionFromSelection(e));
			field.addSelectionListener(e -> fireSelectionListeners(buildSelectionEvent(e)));
			return field;
		}

		if (mode == RenderingMode.OPTIONS) {
			final RadioButtonGroup<ITEM> field = new RadioButtonGroup<>();
			field.setItemCaptionGenerator(i -> generateItemCaption(i));
			field.setItemIconGenerator(i -> generateItemIcon(i));
			field.setItemDescriptionGenerator(i -> generateItemDescription(i));
			field.addSelectionListener(e -> fireSelectionListeners(buildSelectionEvent(e)));
			return field;
		}

		final ComboBox<ITEM> field = new ComboBox<>();
		field.setItemCaptionGenerator(i -> generateItemCaption(i));
		field.setItemIconGenerator(i -> generateItemIcon(i));
		field.addSelectionListener(e -> setupDescriptionFromSelection(e));
		field.addSelectionListener(e -> fireSelectionListeners(buildSelectionEvent(e)));
		return field;
	}

	protected SelectionEvent<T> buildSelectionEvent(SingleSelectionEvent<ITEM> event) {
		return new DefaultSelectionEvent<>(
				event.getFirstSelectedItem().map(item -> fromInternalValue(item)).orElse(null),
				event.isUserOriginated());
	}

	protected void setupDescriptionFromSelection(SingleSelectionEvent<ITEM> event) {
		getItemDescriptionGenerator().ifPresent(g -> {
			getInternalField().setDescription(generateItemDescription(event.getFirstSelectedItem().orElse(null)));
		});
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.AbstractCustomField#fromInternalValue(java.lang.Object)
	 */
	@Override
	protected T fromInternalValue(ITEM value) {
		return toSelection(value);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.AbstractCustomField#toInternalValue(java.lang.Object)
	 */
	@Override
	protected ITEM toInternalValue(T value) {
		return toItem(value);
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
		} else if (getInternalField() instanceof HasFilterableDataProvider) {
			((HasFilterableDataProvider) getInternalField()).setDataProvider(dataProvider, t -> null);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.AbstractSelectField#setDataProvider(com.vaadin.data.provider.
	 * DataProvider, com.vaadin.server.SerializableFunction)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void setDataProvider(DataProvider<ITEM, ?> dataProvider, SerializableFunction<String, ?> filterConverter) {
		if (filterConverter != null && getInternalField() instanceof ComboBox) {
			((ComboBox) getInternalField()).setDataProvider(dataProvider, filterConverter);
		} else {
			setDataProvider(dataProvider);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.AbstractSelectField#setItems(java.util.Collection,
	 * com.vaadin.ui.ComboBox.CaptionFilter)
	 */
	@Override
	public void setItems(Collection<ITEM> items, CaptionFilter filter) {
		if (filter != null && getInternalField() instanceof ComboBox) {
			((ComboBox<ITEM>) getInternalField()).setItems(filter, items);
		} else {
			setItems(items);
		}
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
	 * @see com.holonplatform.vaadin.components.Selectable#select(java.lang.Object)
	 */
	@Override
	public void select(T item) {
		getInternalField().setSelectedItem(toInternalValue(item));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Selectable#deselect(java.lang.Object)
	 */
	@Override
	public void deselect(T item) {
		if (item != null) {
			getSelectedItem().filter(i -> i.equals(item)).ifPresent(i -> getInternalField().setSelectedItem(null));
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.SingleSelect#getSelectedItem()
	 */
	@Override
	public Optional<T> getSelectedItem() {
		return Optional.of(getValue());
	}

	/**
	 * Set whether is possible to input text into the field. If the concrete select component does not support user
	 * input, this method has no effect.
	 * @param textInputAllowed true to allow entering text, false to just show the current selection
	 */
	public void setTextInputAllowed(boolean textInputAllowed) {
		if (getInternalField() instanceof ComboBox) {
			((ComboBox<?>) getInternalField()).setTextInputAllowed(textInputAllowed);
		}
	}

	/**
	 * Sets the placeholder string - a textual prompt that is displayed when the select would otherwise be empty, to
	 * prompt the user for input.
	 * @param inputPrompt the desired placeholder, or null to disable
	 */
	public void setInputPrompt(String inputPrompt) {
		if (getInternalField() instanceof ComboBox) {
			((ComboBox<?>) getInternalField()).setPlaceholder(inputPrompt);
		}
	}

	/**
	 * Sets whether to scroll the selected item visible (directly open the page on which it is) when opening the
	 * suggestions popup or not. This requires finding the index of the item, which can be expensive in many large lazy
	 * loading containers.
	 * <p>
	 * Only applies to select field with backing components supporting a suggestion popup.
	 * </p>
	 * @param scrollToSelectedItem true to find the page with the selected item when opening the selection popup
	 */
	public void setScrollToSelectedItem(boolean scrollToSelectedItem) {
		if (getInternalField() instanceof ComboBox) {
			((ComboBox<?>) getInternalField()).setScrollToSelectedItem(scrollToSelectedItem);
		}
	}

	/**
	 * Sets the suggestion pop-up's width as a CSS string. By using relative units (e.g. "50%") it's possible to set the
	 * popup's width relative to the selection component itself.
	 * <p>
	 * Only applies to select field with backing components supporting a suggestion popup.
	 * </p>
	 * @param width the suggestion pop-up width
	 */
	public void setSuggestionPopupWidth(String width) {
		if (getInternalField() instanceof ComboBox) {
			((ComboBox<?>) getInternalField()).setPopupWidth(width);
		}
	}

	public void setEmptySelectionAllowed(boolean emptySelectionAllowed) {
		if (getInternalField() instanceof ComboBox) {
			((ComboBox<?>) getInternalField()).setEmptySelectionAllowed(emptySelectionAllowed);
		}
		if (getInternalField() instanceof NativeSelect) {
			((NativeSelect<?>) getInternalField()).setEmptySelectionAllowed(emptySelectionAllowed);
		}
	}

	public void setEmptySelectionCaption(String caption) {
		if (getInternalField() instanceof ComboBox) {
			((ComboBox<?>) getInternalField()).setEmptySelectionCaption(caption);
		}
		if (getInternalField() instanceof NativeSelect) {
			((NativeSelect<?>) getInternalField()).setEmptySelectionCaption(caption);
		}
	}

	public void setHtmlContentAllowed(boolean htmlContentAllowed) {
		if (getInternalField() instanceof RadioButtonGroup) {
			((RadioButtonGroup<?>) getInternalField()).setHtmlContentAllowed(htmlContentAllowed);
		}
	}

	@SuppressWarnings("unchecked")
	public void setItemEnabledProvider(SerializablePredicate<T> itemEnabledProvider) {
		if (getInternalField() instanceof RadioButtonGroup) {
			((RadioButtonGroup<T>) getInternalField()).setItemEnabledProvider(itemEnabledProvider);
		}
	}

	// Builders

	/**
	 * Base {@link SingleSelect} builder.
	 * @param <T> Selection type
	 */
	static abstract class AbstractSingleSelectFieldBuilder<T, ITEM, B extends SelectInputBuilder.SingleSelectConfigurator<T, ITEM, B>>
			extends AbstractSelectFieldBuilder<T, SingleSelect<T>, T, ITEM, SingleSelectField<T, ITEM>, B>
			implements SelectInputBuilder.SingleSelectConfigurator<T, ITEM, B> {

		protected CaptionFilter captionFilter;
		protected SerializableFunction<String, ?> filterProvider;

		/**
		 * Constructor
		 * @param type Selection value type
		 * @param renderingMode Rendering mode
		 */
		public AbstractSingleSelectFieldBuilder(Class<? extends T> type, RenderingMode renderingMode) {
			super(new SingleSelectField<>(type, renderingMode));
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.internal.components.builders.AbstractSelectFieldBuilder#configureDataSource(com.
		 * holonplatform.vaadin.internal.components.AbstractSelectField)
		 */
		@Override
		protected void configureDataSource(SingleSelectField<T, ITEM> instance) {
			if (!items.isEmpty()) {
				instance.setItems(items, captionFilter);
			} else if (itemDataProvider != null) {
				instance.setDataProvider(new ItemDataProviderAdapter<>(itemDataProvider, itemIdentifier, null),
						filterProvider);
			} else {
				instance.setDataProvider(dataProvider, filterProvider);
			}
		}

	}

	/**
	 * Default {@link SingleSelectInputBuilder} implementation.
	 * @param <T> Selection type
	 */
	public static abstract class Builder<T, B extends SingleSelectInputBuilder<T, B>>
			extends AbstractSingleSelectFieldBuilder<T, T, B> implements SingleSelectInputBuilder<T, B> {

		/**
		 * Constructor
		 * @param type Selection value type
		 * @param renderingMode Rendering mode
		 */
		public Builder(Class<? extends T> type, RenderingMode renderingMode) {
			super(type, renderingMode);
			getInstance().setItemConverter(Converter.identity());
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.builders.SelectItemDataSourceBuilder#dataSource(com.holonplatform.vaadin.
		 * data.ItemDataProvider)
		 */
		@Override
		public B dataSource(ItemDataProvider<T> dataProvider) {
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
		 * @see
		 * com.holonplatform.vaadin.components.builders.SingleSelectInputBuilder#dataSource(com.holonplatform.vaadin.
		 * data.ItemDataProvider, com.vaadin.server.SerializableFunction)
		 */
		@Override
		public B dataSource(ItemDataProvider<T> dataProvider,
				SerializableFunction<String, QueryFilter> filterProvider) {
			this.itemDataProvider = dataProvider;
			this.filterProvider = filterProvider;
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.builders.SingleSelectInputBuilder#dataSource(com.vaadin.data.provider.
		 * DataProvider, com.vaadin.server.SerializableFunction)
		 */
		@Override
		public <F> B dataSource(DataProvider<T, F> dataProvider, SerializableFunction<String, F> filterProvider) {
			this.dataProvider = dataProvider;
			this.filterProvider = filterProvider;
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.internal.components.builders.AbstractSelectFieldBuilder#buildSelect(com.
		 * holonplatform.vaadin.internal.components.AbstractSelectField)
		 */
		@Override
		protected SingleSelect<T> buildSelect(SingleSelectField<T, T> instance) {
			return instance;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.internal.components.builders.AbstractSelectFieldBuilder#buildSelectAsField(com.
		 * holonplatform.vaadin.internal.components.AbstractSelectField)
		 */
		@Override
		protected Field<T> buildSelectAsField(SingleSelectField<T, T> instance) {
			return instance;
		}

	}

	public static class GenericBuilder<T> extends Builder<T, GenericSingleSelectInputBuilder<T>>
			implements GenericSingleSelectInputBuilder<T> {

		public GenericBuilder(Class<? extends T> type, RenderingMode renderingMode) {
			super(type, renderingMode);
		}

		@Override
		protected GenericSingleSelectInputBuilder<T> builder() {
			return this;
		}

	}

	public static class NativeModeBuilder<T> extends Builder<T, NativeModeSingleSelectInputBuilder<T>>
			implements NativeModeSingleSelectInputBuilder<T> {

		protected Localizable emptySelectionCaption;

		public NativeModeBuilder(Class<? extends T> type) {
			super(type, RenderingMode.NATIVE_SELECT);
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentConfigurator#builder()
		 */
		@Override
		protected NativeModeSingleSelectInputBuilder<T> builder() {
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.internal.components.SingleSelectField.AbstractSingleSelectFieldBuilder#localize(com.
		 * holonplatform.vaadin.internal.components.SingleSelectField)
		 */
		@Override
		protected void localize(SingleSelectField<T, T> instance) {
			super.localize(instance);
			if (emptySelectionCaption != null) {
				getInstance().setEmptySelectionCaption(LocalizationContext.translate(emptySelectionCaption, true));
			}
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.builders.BaseSelectModeSingleSelectInputBuilder#emptySelectionAllowed(
		 * boolean)
		 */
		@Override
		public NativeModeSingleSelectInputBuilder<T> emptySelectionAllowed(boolean emptySelectionAllowed) {
			getInstance().setEmptySelectionAllowed(emptySelectionAllowed);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.builders.BaseSelectModeSingleSelectInputBuilder#emptySelectionCaption(com
		 * .holonplatform.core.i18n.Localizable)
		 */
		@Override
		public NativeModeSingleSelectInputBuilder<T> emptySelectionCaption(Localizable caption) {
			this.emptySelectionCaption = caption;
			return this;
		}

	}

	public static class OptionsModeBuilder<T> extends Builder<T, OptionsModeSingleSelectInputBuilder<T>>
			implements OptionsModeSingleSelectInputBuilder<T> {

		public OptionsModeBuilder(Class<? extends T> type) {
			super(type, RenderingMode.OPTIONS);
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.BaseSelectModeSingleSelectInputBuilder.
		 * OptionsModeSingleSelectInputBuilder#htmlContentAllowed(boolean)
		 */
		@Override
		public OptionsModeSingleSelectInputBuilder<T> htmlContentAllowed(boolean htmlContentAllowed) {
			getInstance().setHtmlContentAllowed(htmlContentAllowed);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.BaseSelectModeSingleSelectInputBuilder.
		 * OptionsModeSingleSelectInputBuilder#itemEnabledProvider(com.vaadin.server.SerializablePredicate)
		 */
		@Override
		public OptionsModeSingleSelectInputBuilder<T> itemEnabledProvider(
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
		protected OptionsModeSingleSelectInputBuilder<T> builder() {
			return this;
		}

	}

	public static class SelectModeBuilder<T> extends Builder<T, SelectModeSingleSelectInputBuilder<T>>
			implements SelectModeSingleSelectInputBuilder<T> {

		protected Localizable inputPrompt;
		protected Localizable emptySelectionCaption;

		public SelectModeBuilder(Class<? extends T> type) {
			super(type, RenderingMode.SELECT);
		}

		@Override
		protected SelectModeSingleSelectInputBuilder<T> builder() {
			return this;
		}

		@Override
		protected void localize(SingleSelectField<T, T> instance) {
			super.localize(instance);
			if (inputPrompt != null) {
				getInstance().setInputPrompt(LocalizationContext.translate(inputPrompt, true));
			}
			if (emptySelectionCaption != null) {
				getInstance().setEmptySelectionCaption(LocalizationContext.translate(emptySelectionCaption, true));
			}
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.SingleSelectFieldBuilder#disableTextInput()
		 */
		@Override
		public SelectModeSingleSelectInputBuilder<T> disableTextInput() {
			getInstance().setTextInputAllowed(false);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.SingleSelectFieldBuilder#scrollToSelectedItem(boolean)
		 */
		@Override
		public SelectModeSingleSelectInputBuilder<T> scrollToSelectedItem(boolean scrollToSelectedItem) {
			getInstance().setScrollToSelectedItem(scrollToSelectedItem);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.builders.SingleSelectFieldBuilder#suggestionPopupWidth(java.lang.String)
		 */
		@Override
		public SelectModeSingleSelectInputBuilder<T> suggestionPopupWidth(String width) {
			getInstance().setSuggestionPopupWidth(width);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.builders.BaseSelectInputBuilder.SingleSelectConfigurator#filteringMode(
		 * com.vaadin.ui.ComboBox.CaptionFilter)
		 */
		@Override
		public SelectModeSingleSelectInputBuilder<T> filteringMode(CaptionFilter captionFilter) {
			this.captionFilter = captionFilter;
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.builders.BaseSelectInputBuilder.SingleSelectConfigurator#inputPrompt(com.
		 * holonplatform.core.i18n.Localizable)
		 */
		@Override
		public SelectModeSingleSelectInputBuilder<T> inputPrompt(Localizable inputPrompt) {
			this.inputPrompt = inputPrompt;
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.builders.BaseSelectModeSingleSelectInputBuilder#emptySelectionAllowed(
		 * boolean)
		 */
		@Override
		public SelectModeSingleSelectInputBuilder<T> emptySelectionAllowed(boolean emptySelectionAllowed) {
			getInstance().setEmptySelectionAllowed(emptySelectionAllowed);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.builders.BaseSelectModeSingleSelectInputBuilder#emptySelectionCaption(com
		 * .holonplatform.core.i18n.Localizable)
		 */
		@Override
		public SelectModeSingleSelectInputBuilder<T> emptySelectionCaption(Localizable caption) {
			this.emptySelectionCaption = caption;
			return this;
		}

	}

	/**
	 * Default {@link SinglePropertySelectInputBuilder} implementation.
	 * @param <T> Selection type
	 */
	public static abstract class PropertyBuilder<T, B extends SinglePropertySelectInputBuilder<T, B>> extends
			AbstractSingleSelectFieldBuilder<T, PropertyBox, B> implements SinglePropertySelectInputBuilder<T, B> {

		private final Property<T> selectProperty;

		/**
		 * Constructor
		 * @param selectProperty Selection (and identifier) property
		 * @param renderingMode Rendering mode
		 */
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public PropertyBuilder(Property<T> selectProperty, RenderingMode renderingMode) {
			super(selectProperty.getType(), renderingMode);
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
		 * com.holonplatform.vaadin.components.builders.PropertySelectInputBuilder#dataSource(com.holonplatform.vaadin.
		 * data.ItemDataProvider)
		 */
		@Override
		public B dataSource(ItemDataProvider<PropertyBox> dataProvider) {
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
			SinglePropertySelectInputBuilder.super.dataSource(datastore, dataTarget, properties,
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
			SinglePropertySelectInputBuilder.super.dataSource(datastore, dataTarget, properties);
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
		protected void preSetup(SingleSelectField<T, PropertyBox> instance) {
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
		protected SingleSelect<T> buildSelect(SingleSelectField<T, PropertyBox> instance) {
			return instance;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.internal.components.builders.AbstractSelectFieldBuilder#buildSelectAsField(com.
		 * holonplatform.vaadin.internal.components.AbstractSelectField)
		 */
		@Override
		protected Field<T> buildSelectAsField(SingleSelectField<T, PropertyBox> instance) {
			return instance;
		}

	}

	public static class GenericPropertyBuilder<T> extends PropertyBuilder<T, GenericSinglePropertySelectInputBuilder<T>>
			implements GenericSinglePropertySelectInputBuilder<T> {

		public GenericPropertyBuilder(Property<T> selectProperty, RenderingMode renderingMode) {
			super(selectProperty, renderingMode);
		}

		@Override
		protected GenericSinglePropertySelectInputBuilder<T> builder() {
			return this;
		}

	}

	public static class NativeModePropertyBuilder<T>
			extends PropertyBuilder<T, NativeModeSinglePropertySelectInputBuilder<T>>
			implements NativeModeSinglePropertySelectInputBuilder<T> {

		protected Localizable emptySelectionCaption;

		public NativeModePropertyBuilder(Property<T> selectProperty) {
			super(selectProperty, RenderingMode.NATIVE_SELECT);
		}

		@Override
		protected NativeModeSinglePropertySelectInputBuilder<T> builder() {
			return this;
		}

		@Override
		protected void localize(SingleSelectField<T, PropertyBox> instance) {
			super.localize(instance);
			if (emptySelectionCaption != null) {
				getInstance().setEmptySelectionCaption(LocalizationContext.translate(emptySelectionCaption, true));
			}
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.BaseSelectModeSinglePropertySelectInputBuilder#
		 * emptySelectionAllowed(boolean)
		 */
		@Override
		public NativeModeSinglePropertySelectInputBuilder<T> emptySelectionAllowed(boolean emptySelectionAllowed) {
			getInstance().setEmptySelectionAllowed(emptySelectionAllowed);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.BaseSelectModeSinglePropertySelectInputBuilder#
		 * emptySelectionCaption(com.holonplatform.core.i18n.Localizable)
		 */
		@Override
		public NativeModeSinglePropertySelectInputBuilder<T> emptySelectionCaption(Localizable caption) {
			this.emptySelectionCaption = caption;
			return this;
		}

	}

	public static class OptionsModePropertyBuilder<T>
			extends PropertyBuilder<T, OptionsModeSinglePropertySelectInputBuilder<T>>
			implements OptionsModeSinglePropertySelectInputBuilder<T> {

		public OptionsModePropertyBuilder(Property<T> selectProperty) {
			super(selectProperty, RenderingMode.OPTIONS);
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.BaseSelectModeSinglePropertySelectInputBuilder.
		 * OptionsModeSinglePropertySelectInputBuilder#htmlContentAllowed(boolean)
		 */
		@Override
		public OptionsModeSinglePropertySelectInputBuilder<T> htmlContentAllowed(boolean htmlContentAllowed) {
			getInstance().setHtmlContentAllowed(htmlContentAllowed);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.BaseSelectModeSinglePropertySelectInputBuilder.
		 * OptionsModeSinglePropertySelectInputBuilder#itemEnabledProvider(com.vaadin.server.SerializablePredicate)
		 */
		@Override
		public OptionsModeSinglePropertySelectInputBuilder<T> itemEnabledProvider(
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
		protected OptionsModeSinglePropertySelectInputBuilder<T> builder() {
			return this;
		}

	}

	public static class SelectModePropertyBuilder<T>
			extends PropertyBuilder<T, SelectModeSinglePropertySelectInputBuilder<T>>
			implements SelectModeSinglePropertySelectInputBuilder<T> {

		protected Localizable inputPrompt;
		protected Localizable emptySelectionCaption;

		public SelectModePropertyBuilder(Property<T> selectProperty) {
			super(selectProperty, RenderingMode.SELECT);
		}

		@Override
		protected SelectModeSinglePropertySelectInputBuilder<T> builder() {
			return this;
		}

		@Override
		public SelectModeSinglePropertySelectInputBuilder<T> captionQueryFilter(
				SerializableFunction<String, QueryFilter> filterProvider) {
			this.filterProvider = filterProvider;
			return this;
		}

		@Override
		protected void localize(SingleSelectField<T, PropertyBox> instance) {
			super.localize(instance);
			if (inputPrompt != null) {
				getInstance().setInputPrompt(LocalizationContext.translate(inputPrompt, true));
			}
			if (emptySelectionCaption != null) {
				getInstance().setEmptySelectionCaption(LocalizationContext.translate(emptySelectionCaption, true));
			}
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.BaseSelectModeSinglePropertySelectInputBuilder#
		 * emptySelectionAllowed(boolean)
		 */
		@Override
		public SelectModeSinglePropertySelectInputBuilder<T> emptySelectionAllowed(boolean emptySelectionAllowed) {
			getInstance().setEmptySelectionAllowed(emptySelectionAllowed);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.BaseSelectModeSinglePropertySelectInputBuilder#
		 * emptySelectionCaption(com.holonplatform.core.i18n.Localizable)
		 */
		@Override
		public SelectModeSinglePropertySelectInputBuilder<T> emptySelectionCaption(Localizable caption) {
			this.emptySelectionCaption = caption;
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.BaseSelectModeSinglePropertySelectInputBuilder.
		 * SelectModeSinglePropertySelectInputBuilder#inputPrompt(com.holonplatform.core.i18n.Localizable)
		 */
		@Override
		public SelectModeSinglePropertySelectInputBuilder<T> inputPrompt(Localizable inputPrompt) {
			this.inputPrompt = inputPrompt;
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.BaseSelectModeSinglePropertySelectInputBuilder.
		 * SelectModeSinglePropertySelectInputBuilder#disableTextInput()
		 */
		@Override
		public SelectModeSinglePropertySelectInputBuilder<T> disableTextInput() {
			getInstance().setTextInputAllowed(false);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.BaseSelectModeSinglePropertySelectInputBuilder.
		 * SelectModeSinglePropertySelectInputBuilder#scrollToSelectedItem(boolean)
		 */
		@Override
		public SelectModeSinglePropertySelectInputBuilder<T> scrollToSelectedItem(boolean scrollToSelectedItem) {
			getInstance().setScrollToSelectedItem(scrollToSelectedItem);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.BaseSelectModeSinglePropertySelectInputBuilder.
		 * SelectModeSinglePropertySelectInputBuilder#suggestionPopupWidth(java.lang.String)
		 */
		@Override
		public SelectModeSinglePropertySelectInputBuilder<T> suggestionPopupWidth(String width) {
			getInstance().setSuggestionPopupWidth(width);
			return this;
		}

	}

}
