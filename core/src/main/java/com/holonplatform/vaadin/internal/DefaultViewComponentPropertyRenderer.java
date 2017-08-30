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
package com.holonplatform.vaadin.internal;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyRenderer;
import com.holonplatform.vaadin.components.ViewComponent;
import com.holonplatform.vaadin.components.builders.ViewComponentBuilder;

/**
 * Default {@link PropertyRenderer} to create {@link ViewComponent} type {@link Property} representations.
 * 
 * @param <T> Property type
 *
 * @since 5.0.0
 */
@SuppressWarnings("rawtypes")
public class DefaultViewComponentPropertyRenderer<T> implements PropertyRenderer<ViewComponent, T> {

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.property.PropertyRenderer#getRenderType()
	 */
	@Override
	public Class<? extends ViewComponent> getRenderType() {
		return ViewComponent.class;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.property.PropertyRenderer#render(com.holonplatform.core.property.Property)
	 */
	@Override
	public ViewComponent render(final Property<T> property) {

		ObjectUtils.argumentNotNull(property, "Property must be not null");

		// builder
		@SuppressWarnings("unchecked")
		ViewComponentBuilder<T> builder = ViewComponent.builder(property.getType()).caption(property)
				.forProperty((Property) property);
		return builder.build();
	}

}
