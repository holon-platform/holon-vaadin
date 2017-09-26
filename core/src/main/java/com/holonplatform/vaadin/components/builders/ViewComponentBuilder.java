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
package com.holonplatform.vaadin.components.builders;

import com.holonplatform.vaadin.components.ValueHolder.ValueChangeListener;
import com.holonplatform.vaadin.components.ViewComponent;
import com.holonplatform.vaadin.internal.components.LabelViewComponent.StringValueConverter;

/**
 * Builder to create {@link ViewComponent}s.
 * 
 * @param <T> Value type
 * 
 * @since 5.0.0
 */
public interface ViewComponentBuilder<T> extends ComponentBuilder<ViewComponent<T>, ViewComponentBuilder<T>> {

	/**
	 * Build a {@link ViewComponent} using given <code>property</code> for value presentation and configuration
	 * parameters source. The {@link com.holonplatform.core.property.Property#present(Object)} method will be called
	 * when value presentation is required.
	 * @param property Property (not null)
	 * @return this
	 */
	ViewComponentBuilder<T> forProperty(com.holonplatform.core.property.Property<T> property);

	/**
	 * Sets the converter to use to display values
	 * @param stringConverter the StringValueConverter to set
	 * @return this
	 */
	ViewComponentBuilder<T> valueDisplayConverter(StringValueConverter<T> stringConverter);

	/**
	 * Add a {@link ValueChangeListener} to the component.
	 * @param listener The ValueChangeListener to add
	 * @return this
	 */
	ViewComponentBuilder<T> withValueChangeListener(ValueChangeListener<T> listener);

}
