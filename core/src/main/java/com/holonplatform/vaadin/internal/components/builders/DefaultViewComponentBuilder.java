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
import com.holonplatform.vaadin.components.ValueHolder.ValueChangeListener;
import com.holonplatform.vaadin.components.ViewComponent;
import com.holonplatform.vaadin.components.builders.ViewComponentBuilder;
import com.holonplatform.vaadin.internal.components.LabelViewComponent;
import com.holonplatform.vaadin.internal.components.LabelViewComponent.StringValueConverter;

/**
 * Default {@link ViewComponentBuilder} implementation.
 * 
 * @param <T> Value type
 * 
 * @since 5.0.0
 */
public class DefaultViewComponentBuilder<T>
		extends AbstractComponentBuilder<ViewComponent<T>, LabelViewComponent<T>, ViewComponentBuilder<T>>
		implements ViewComponentBuilder<T> {

	/**
	 * Constructor
	 * @param type Value type
	 */
	public DefaultViewComponentBuilder(Class<? extends T> type) {
		super(new LabelViewComponent<>(type));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentBuilder#builder()
	 */
	@Override
	protected ViewComponentBuilder<T> builder() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentBuilder#build(com.vaadin.ui.
	 * AbstractComponent)
	 */
	@Override
	protected ViewComponent<T> build(LabelViewComponent<T> instance) {
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ViewComponentBuilder#valueDisplayConverter(com.holonplatform.
	 * vaadin.internal.components.LabelViewComponent.StringValueConverter)
	 */
	@Override
	public ViewComponentBuilder<T> valueDisplayConverter(StringValueConverter<T> stringConverter) {
		getInstance().setStringConverter(stringConverter);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ViewComponentBuilder#forProperty(com.vaadin.data.Property)
	 */
	@Override
	public ViewComponentBuilder<T> forProperty(final com.holonplatform.core.property.Property<T> property) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		getInstance().setStringConverter(v -> property.present(v));
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ViewComponentBuilder#withValueChangeListener(com.vaadin.data.
	 * Property.ValueChangeListener)
	 */
	@Override
	public ViewComponentBuilder<T> withValueChangeListener(ValueChangeListener<T> listener) {
		getInstance().addValueChangeListener(listener);
		return builder();
	}

}
