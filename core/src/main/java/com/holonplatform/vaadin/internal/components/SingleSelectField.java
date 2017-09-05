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
import java.util.Optional;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.vaadin.components.Field;
import com.holonplatform.vaadin.components.SingleSelect;
import com.holonplatform.vaadin.components.builders.BaseSelectInputBuilder.RenderingMode;
import com.holonplatform.vaadin.components.builders.SelectInputBuilder;
import com.holonplatform.vaadin.components.builders.SinglePropertySelectInputBuilder;
import com.holonplatform.vaadin.components.builders.SingleSelectInputBuilder;
import com.holonplatform.vaadin.data.ItemDataProvider;
import com.holonplatform.vaadin.internal.components.builders.AbstractSelectFieldBuilder;
import com.holonplatform.vaadin.internal.data.ItemDataProviderAdapter;
import com.holonplatform.vaadin.internal.data.PropertyItemIdentifier;
import com.vaadin.data.Converter;
import com.vaadin.data.HasDataProvider;
import com.vaadin.data.HasFilterableDataProvider;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.server.SerializableFunction;
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
			field.addSelectionListener(e -> fireSelectionListeners());
			return field;
		}

		if (mode == RenderingMode.OPTIONS) {
			final RadioButtonGroup<ITEM> field = new RadioButtonGroup<>();
			field.setItemCaptionGenerator(i -> generateItemCaption(i));
			field.setItemIconGenerator(i -> generateItemIcon(i));
			field.addSelectionListener(e -> fireSelectionListeners());
			return field;
		}

		final ComboBox<ITEM> field = new ComboBox<>();
		field.setItemCaptionGenerator(i -> generateItemCaption(i));
		field.setItemIconGenerator(i -> generateItemIcon(i));
		field.addSelectionListener(e -> fireSelectionListeners());
		return field;
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

		protected Localizable inputPrompt;

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
		 * com.holonplatform.vaadin.internal.components.builders.AbstractLocalizableComponentConfigurator#localize(com.
		 * vaadin.ui.AbstractComponent)
		 */
		@Override
		protected void localize(SingleSelectField<T, ITEM> instance) {
			super.localize(instance);
			if (inputPrompt != null) {
				getInstance().setInputPrompt(LocalizationContext.translate(inputPrompt, true));
			}
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.SingleSelectFieldBuilder#disableTextInput()
		 */
		@Override
		public B disableTextInput() {
			getInstance().setTextInputAllowed(false);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.builders.SingleSelectFieldBuilder#scrollToSelectedItem(boolean)
		 */
		@Override
		public B scrollToSelectedItem(boolean scrollToSelectedItem) {
			getInstance().setScrollToSelectedItem(scrollToSelectedItem);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.builders.SingleSelectFieldBuilder#suggestionPopupWidth(java.lang.String)
		 */
		@Override
		public B suggestionPopupWidth(String width) {
			getInstance().setSuggestionPopupWidth(width);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.builders.BaseSelectInputBuilder.SingleSelectConfigurator#filteringMode(
		 * com.vaadin.ui.ComboBox.CaptionFilter)
		 */
		@Override
		public B filteringMode(CaptionFilter captionFilter) {
			this.captionFilter = captionFilter;
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.builders.BaseSelectInputBuilder.SingleSelectConfigurator#inputPrompt(com.
		 * holonplatform.core.i18n.Localizable)
		 */
		@Override
		public B inputPrompt(Localizable inputPrompt) {
			this.inputPrompt = inputPrompt;
			return builder();
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
				instance.setDataProvider(new ItemDataProviderAdapter<>(itemDataProvider, itemIdentifier),
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
	public static class Builder<T> extends AbstractSingleSelectFieldBuilder<T, T, SingleSelectInputBuilder<T>>
			implements SingleSelectInputBuilder<T> {

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
		 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentBuilder#builder()
		 */
		@Override
		protected Builder<T> builder() {
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.builders.SelectItemDataSourceBuilder#dataSource(com.holonplatform.vaadin.
		 * data.ItemDataProvider)
		 */
		@Override
		public SingleSelectInputBuilder<T> dataSource(ItemDataProvider<T> dataProvider) {
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
		public SingleSelectInputBuilder<T> dataSource(DataProvider<T, ?> dataProvider) {
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
		public SingleSelectInputBuilder<T> dataSource(ItemDataProvider<T> dataProvider,
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
		public <F> SingleSelectInputBuilder<T> dataSource(DataProvider<T, F> dataProvider,
				SerializableFunction<String, F> filterProvider) {
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

	/**
	 * Default {@link SinglePropertySelectInputBuilder} implementation.
	 * @param <T> Selection type
	 */
	public static class PropertyBuilder<T>
			extends AbstractSingleSelectFieldBuilder<T, PropertyBox, SinglePropertySelectInputBuilder<T>>
			implements SinglePropertySelectInputBuilder<T> {

		/**
		 * Constructor
		 * @param selectProperty Selection (and identifier) property
		 * @param renderingMode Rendering mode
		 */
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public PropertyBuilder(Property<T> selectProperty, RenderingMode renderingMode) {
			super(selectProperty.getType(), renderingMode);
			itemIdentifier = new PropertyItemIdentifier(selectProperty);
			getInstance().setItemConverter(new DefaultPropertyBoxConverter<>(selectProperty));
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.builders.PropertySelectInputBuilder#dataSource(com.holonplatform.vaadin.
		 * data.ItemDataProvider)
		 */
		@Override
		public SinglePropertySelectInputBuilder<T> dataSource(ItemDataProvider<PropertyBox> dataProvider) {
			this.itemDataProvider = dataProvider;
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.builders.SinglePropertySelectInputBuilder#captionQueryFilter(com.vaadin.
		 * server.SerializableFunction)
		 */
		@Override
		public SinglePropertySelectInputBuilder<T> captionQueryFilter(
				SerializableFunction<String, QueryFilter> filterProvider) {
			this.filterProvider = filterProvider;
			return builder();
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

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentConfigurator#builder()
		 */
		@Override
		protected SinglePropertySelectInputBuilder<T> builder() {
			return this;
		}

	}

}
