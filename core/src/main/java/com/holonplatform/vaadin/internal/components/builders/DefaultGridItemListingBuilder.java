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

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.components.BeanListing;
import com.holonplatform.vaadin.components.builders.ItemListingBuilder.GridItemListingBuilder;
import com.holonplatform.vaadin.internal.components.DefaultBeanListing;
import com.vaadin.data.HasValue;
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
public class DefaultGridItemListingBuilder<T> extends
		AbstractGridItemListingBuilder<T, String, BeanListing<T>, DefaultBeanListing<T>, GridItemListingBuilder<T>>
		implements GridItemListingBuilder<T> {

	public DefaultGridItemListingBuilder(Class<T> beanType) {
		super(new DefaultBeanListing<>(beanType), String.class);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.ItemListingBuilder.GridItemListingBuilder#editor(java.lang.String,
	 * com.vaadin.data.HasValue)
	 */
	@Override
	public <E extends HasValue<?> & Component> GridItemListingBuilder<T> editor(String property, E editor) {
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
	public GridItemListingBuilder<T> withValidator(com.vaadin.data.Validator<T> validator) {
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
	public GridItemListingBuilder<T> withValidator(String property, com.vaadin.data.Validator<?> validator) {
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
	public GridItemListingBuilder<T> render(String property, Renderer<?> renderer) {
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
	public <V, P> GridItemListingBuilder<T> render(String property, ValueProvider<V, P> presentationProvider,
			Renderer<? super P> renderer) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		getInstance().getPropertyColumn(property).setPresentationProvider(presentationProvider);
		getInstance().getPropertyColumn(property).setRenderer(renderer);
		return builder();
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
	protected GridItemListingBuilder<T> builder() {
		return this;
	}

}
