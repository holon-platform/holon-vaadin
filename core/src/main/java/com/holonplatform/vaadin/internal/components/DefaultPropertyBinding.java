/*
 * Copyright 2000-2017 Holon TDCN.
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

import com.holonplatform.core.property.Property;
import com.holonplatform.vaadin.components.PropertyBinding;

/**
 * Default {@link PropertyBinding} implementation.
 * 
 * @param <T> Property value type
 * @param <B> Bound component type
 *
 * @since 5.0.0
 */
public class DefaultPropertyBinding<T, B> implements PropertyBinding<T, B> {

	private static final long serialVersionUID = -6134557608912391745L;

	private final Property<T> property;
	private final B component;

	/**
	 * Constructor
	 * @param property Property
	 * @param component Bound component
	 */
	public DefaultPropertyBinding(Property<T> property, B component) {
		super();
		this.property = property;
		this.component = component;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyBinding#getProperty()
	 */
	@Override
	public Property<T> getProperty() {
		return property;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyBinding#getComponent()
	 */
	@Override
	public B getComponent() {
		return component;
	}

}
