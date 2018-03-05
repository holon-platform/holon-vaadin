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

import java.util.List;

import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.components.BeanListing;
import com.holonplatform.vaadin.components.builders.BeanListingBuilder;
import com.holonplatform.vaadin.data.ItemDataProvider;
import com.holonplatform.vaadin.data.ItemDataSource.CommitHandler;
import com.holonplatform.vaadin.internal.components.DefaultBeanListing;
import com.vaadin.data.BeanPropertySet;
import com.vaadin.data.HasValue;
import com.vaadin.data.PropertySet;
import com.vaadin.data.ValueProvider;
import com.vaadin.ui.Component;
import com.vaadin.ui.renderers.Renderer;

/**
 * {@link GridItemListingBuilder} implementation.
 * 
 * @param <T> Item data type
 *
 * @since 5.0.0
 */
public class DefaultBeanListingBuilder<T>
		extends AbstractGridItemListingBuilder<T, String, BeanListing<T>, DefaultBeanListing<T>, BeanListingBuilder<T>>
		implements BeanListingBuilder<T> {

	/**
	 * Bean type
	 */
	protected final Class<T> beanType;

	/**
	 * Constructor.
	 * @param beanType Bean type (not null)
	 */
	public DefaultBeanListingBuilder(Class<T> beanType) {
		super(new DefaultBeanListing<>(beanType), String.class);
		this.beanType = beanType;
		// read bean property names
		getInstance().getPropertyDefinitions().forEach(p -> {
			dataSourceBuilder.withProperty(p.getName(), p.getType(), false);

			dataSourceBuilder.propertyId(p.getName(), p.getName());

			if (p.isReadOnly()) {
				dataSourceBuilder.readOnly(p.getName(), true);
			}
			if (p.getBeanProperty().isPresent()) {
				dataSourceBuilder.sortable(p.getName(), true);
			}
		});
		PropertySet<T> propertySet = BeanPropertySet.get(beanType);
		propertySet.getProperties().forEach(p -> {
			dataSourceBuilder.withProperty(p.getName(), p.getType());
		});
	}

	@Override
	public <V> BeanListingBuilder<T> withVirtualColumn(String id, Class<V> type, ValueProvider<T, V> valueProvider) {
		getInstance().addVirtualColumn(id, type, valueProvider);
		return builder();
	}

	@Override
	public BeanListingBuilder<T> dataSource(Datastore datastore, DataTarget<?> target) {
		// set data source
		dataSource(ItemDataProvider.create(datastore, target, beanType));
		// set commit handler
		commitHandler(CommitHandler.datastore(beanType, datastore, target));
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.ItemListingBuilder.GridItemListingBuilder#editor(java.lang.String,
	 * com.vaadin.data.HasValue)
	 */
	@Override
	public <E extends HasValue<?> & Component> BeanListingBuilder<T> editor(String property, E editor) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		ObjectUtils.argumentNotNull(editor, "Editor field must be not null");
		getInstance().getPropertyColumn(property).setEditor(editor);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.ItemListingBuilder.BaseGridItemListingBuilder#withValidator(com.
	 * vaadin.data.Validator)
	 */
	@Override
	public BeanListingBuilder<T> withValidator(com.vaadin.data.Validator<T> validator) {
		ObjectUtils.argumentNotNull(validator, "Validator must be not null");
		getInstance().addValidator(validator);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.ItemListingBuilder.GridItemListingBuilder#withValidator(java.lang.
	 * String, com.vaadin.data.Validator)
	 */
	@Override
	public BeanListingBuilder<T> withValidator(String property, com.vaadin.data.Validator<?> validator) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		ObjectUtils.argumentNotNull(validator, "Validator must be not null");
		getInstance().getPropertyColumn(property).addValidator(validator);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.ItemListingBuilder.GridItemListingBuilder#render(java.lang.String,
	 * com.vaadin.ui.renderers.Renderer)
	 */
	@Override
	public BeanListingBuilder<T> render(String property, Renderer<?> renderer) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		getInstance().getPropertyColumn(property).setRenderer(renderer);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.ItemListingBuilder.GridItemListingBuilder#render(java.lang.String,
	 * com.vaadin.data.ValueProvider, com.vaadin.ui.renderers.Renderer)
	 */
	@Override
	public <V, P> BeanListingBuilder<T> render(String property, ValueProvider<V, P> presentationProvider,
			Renderer<? super P> renderer) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		getInstance().getPropertyColumn(property).setPresentationProvider(presentationProvider);
		getInstance().getPropertyColumn(property).setRenderer(renderer);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.builders.AbstractItemListingBuilder#configureColumns(com.
	 * holonplatform.vaadin.internal.components.DefaultItemListing, java.util.List)
	 */
	@Override
	protected Iterable<? extends String> configureColumns(DefaultBeanListing<T> instance,
			List<? extends String> visibleColumns) {
		// return visible columns or default column ids if none
		return !visibleColumns.isEmpty() ? visibleColumns : instance.getDefaultColumnIds();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentBuilder#build(com.vaadin.ui.
	 * AbstractComponent)
	 */
	@Override
	protected BeanListing<T> build(DefaultBeanListing<T> instance) {
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentConfigurator#builder()
	 */
	@Override
	protected BeanListingBuilder<T> builder() {
		return this;
	}

}
