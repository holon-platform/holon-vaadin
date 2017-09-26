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
package com.holonplatform.vaadin.internal.components.builders;

import java.util.LinkedList;
import java.util.List;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.components.Field;
import com.holonplatform.vaadin.components.Input;
import com.holonplatform.vaadin.components.ItemSet.ItemCaptionGenerator;
import com.holonplatform.vaadin.components.ItemSet.ItemIconGenerator;
import com.holonplatform.vaadin.components.builders.SelectInputBuilder;
import com.holonplatform.vaadin.data.ItemDataProvider;
import com.holonplatform.vaadin.data.ItemIdentifierProvider;
import com.holonplatform.vaadin.internal.components.AbstractSelectField;
import com.holonplatform.vaadin.internal.data.ItemDataProviderAdapter;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.server.Resource;

/**
 * Base {@link SelectInputBuilder} implementation.
 * 
 * @param <T> Field type
 * @param <S> Selection type
 * @param <ITEM> Selection item type
 * @param <C> Internal field type
 * @param <B> Concrete builder type
 * @param <I> Internal component type
 * 
 * @since 5.0.0
 */
public abstract class AbstractSelectFieldBuilder<T, C extends Input<T>, S, ITEM, I extends AbstractSelectField<T, S, ITEM, ?, ?>, B extends SelectInputBuilder<T, C, S, ITEM, B>>
		extends AbstractFieldBuilder<T, C, I, B> implements SelectInputBuilder<T, C, S, ITEM, B> {

	/**
	 * Explicitly added selection items
	 */
	protected final List<ITEM> items = new LinkedList<>();

	/**
	 * Item data provider
	 */
	protected ItemDataProvider<ITEM> itemDataProvider = null;

	/**
	 * Vaadin data provider
	 */
	protected DataProvider<ITEM, ?> dataProvider = null;

	/**
	 * Item identifier
	 */
	protected ItemIdentifierProvider<ITEM, Object> itemIdentifier = null;

	/**
	 * Constructor
	 * @param instance Field instance to build
	 */
	public AbstractSelectFieldBuilder(I instance) {
		super(instance);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.SelectFieldBuilder#itemCaptionGenerator(com.holonplatform.vaadin.
	 * components.ItemSetComponent.ItemCaptionGenerator)
	 */
	@Override
	public B itemCaptionGenerator(ItemCaptionGenerator<ITEM> itemCaptionGenerator) {
		getInstance().setItemCaptionGenerator(itemCaptionGenerator);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.SelectFieldBuilder#itemIconGenerator(com.holonplatform.vaadin.
	 * components.ItemSetComponent.ItemIconGenerator)
	 */
	@Override
	public B itemIconGenerator(ItemIconGenerator<ITEM> itemIconGenerator) {
		getInstance().setItemIconGenerator(itemIconGenerator);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.SelectFieldBuilder#itemCaption(java.lang.Object,
	 * com.holonplatform.core.i18n.Localizable)
	 */
	@Override
	public B itemCaption(ITEM item, Localizable caption) {
		getInstance().setItemCaption(item, caption);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.SelectFieldBuilder#itemIcon(java.lang.Object,
	 * com.vaadin.server.Resource)
	 */
	@Override
	public B itemIcon(ITEM item, Resource icon) {
		getInstance().setItemIcon(item, icon);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.SelectInputBuilder#items(java.lang.Iterable)
	 */
	@Override
	public B items(Iterable<ITEM> items) {
		this.items.clear();
		if (items != null) {
			items.forEach(i -> this.items.add(i));
		}
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.SelectInputBuilder#addItem(java.lang.Object)
	 */
	@Override
	public B addItem(ITEM item) {
		ObjectUtils.argumentNotNull(item, "Item must be not null");
		this.items.add(item);
		return builder();
	}

	/**
	 * Configure items data source
	 * @param instance Building instance
	 */
	protected void configureDataSource(I instance) {
		if (!items.isEmpty()) {
			instance.setItems(items);
		} else if (itemDataProvider != null) {
			instance.setDataProvider(new ItemDataProviderAdapter<>(itemDataProvider, itemIdentifier));
		} else if (dataProvider != null) {
			instance.setDataProvider(dataProvider);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentBuilder#build(com.vaadin.ui.
	 * AbstractComponent)
	 */
	@Override
	protected C build(I instance) {
		configureDataSource(instance);
		return buildSelect(instance);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.builders.AbstractFieldBuilder#buildAsField(com.vaadin.ui.
	 * AbstractField)
	 */
	@Override
	protected Field<T> buildAsField(I instance) {
		configureDataSource(instance);
		return buildSelectAsField(instance);
	}

	protected abstract C buildSelect(I instance);

	protected abstract Field<T> buildSelectAsField(I instance);

}
