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
package com.holonplatform.vaadin.components;

import java.io.Serializable;

import com.holonplatform.core.property.Property;
import com.holonplatform.vaadin.internal.components.DefaultPropertyBinding;

/**
 * Represents a binding between a {@link Property} and a specific type component.
 * 
 * @param <T> Property value type
 * @param <B> Bound component type
 *
 * @since 5.0.0
 */
public interface PropertyBinding<T, B> extends Serializable {

	/**
	 * Get the {@link Property}.
	 * @return The property
	 */
	Property<T> getProperty();

	/**
	 * Get the component to which the property is bound.
	 * @return The bound component
	 */
	B getComponent();

	/**
	 * Create a new {@link PropertyBinding} instance.
	 * @param <T> Property type
	 * @param <B> Bound type
	 * @param property Property
	 * @param component Bound component
	 * @return A new {@link PropertyBinding} instance
	 */
	static <T, B> PropertyBinding<T, B> create(Property<T> property, B component) {
		return new DefaultPropertyBinding<>(property, component);
	}

	/**
	 * Callback interface to perform configuration operations on a component bound to a {@link Property}.
	 * @param <B> Bound component type
	 */
	@FunctionalInterface
	public interface PostProcessor<B> {

		/**
		 * Process the given <code>component</code>
		 * @param property Property to which the component is bound (never null)
		 * @param component Bound component (never null)
		 */
		void process(Property<?> property, B component);

	}

}
