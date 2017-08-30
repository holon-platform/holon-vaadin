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
package com.holonplatform.vaadin.internal.components.builders;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.components.BeanListing;
import com.holonplatform.vaadin.components.builders.ItemListingBuilder.GridItemListingBuilder;
import com.holonplatform.vaadin.internal.components.DefaultBeanListing;
import com.vaadin.data.ValueProvider;
import com.vaadin.ui.renderers.Renderer;

/**
 * {@link GridItemListingBuilder} implementation.
 * 
 * @param <T> Item data type
 * @param <P> Item property type
 *
 * @since 5.0.0
 */
public class DefaultGridItemListingBuilder<T> extends
		AbstractGridItemListingBuilder<T, String, BeanListing<T>, DefaultBeanListing<T>, GridItemListingBuilder<T>>
		implements GridItemListingBuilder<T> {

	public DefaultGridItemListingBuilder(Class<T> beanType) {
		super(new DefaultBeanListing<>(beanType));
	}

	/* (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ItemListingBuilder.GridItemListingBuilder#converter(java.lang.String, com.vaadin.data.ValueProvider)
	 */
	@Override
	public GridItemListingBuilder<T> converter(String property, ValueProvider<?, ?> converter) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		getInstance().getPropertyColumn(property).setPresentationProvider(converter);
		return builder();
	}

	/* (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ItemListingBuilder.GridItemListingBuilder#renderer(java.lang.String, com.vaadin.ui.renderers.Renderer)
	 */
	@Override
	public GridItemListingBuilder<T> renderer(String property, Renderer<?> renderer) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
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
