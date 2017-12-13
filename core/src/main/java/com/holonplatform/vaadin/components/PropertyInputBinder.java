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
package com.holonplatform.vaadin.components;

import java.io.Serializable;
import java.util.Optional;
import java.util.stream.Stream;

import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.Property.PropertyNotFoundException;
import com.holonplatform.vaadin.components.ValueHolder.ValueChangeEvent;

/**
 * Represents a component which binds a property set to {@link Input} components.
 * <p>
 * Provides methods to obtain the property set and the property-input bindings, for example to get the {@link Input}
 * bound to a specific property.
 * </p>
 *
 * @since 5.0.5
 */
public interface PropertyInputBinder extends PropertySetBound {

	/**
	 * Gets all the {@link Input}s that have been bound to a property.
	 * @return An {@link Iterable} on all bound {@link Input}s
	 */
	Iterable<Input<?>> getInputs();

	/**
	 * Get the {@link Input} bound to given <code>property</code>, if any.
	 * @param <T> Property type
	 * @param property Property for which to get the associated {@link Input} (not null)
	 * @return Optional {@link Input} bound to given <code>property</code>
	 */
	<T> Optional<Input<T>> getInput(Property<T> property);

	/**
	 * Get the {@link Input} bound to given <code>property</code>, if any. If not available, a
	 * {@link PropertyNotFoundException} is thrown.
	 * @param <T> Property type
	 * @param property Property for which to get the associated {@link Input} (not null)
	 * @return the {@link Input} bound to given <code>property</code>
	 * @throws PropertyNotFoundException If no Input is available for given property
	 */
	default <T> Input<T> requireInput(Property<T> property) {
		return getInput(property).orElseThrow(
				() -> new PropertyNotFoundException(property, "No Input available for property [" + property + "]"));
	}

	/**
	 * Return a {@link Stream} of the properties and their bound {@link Input}s of this input group.
	 * @param <T> Property type
	 * @return Property-Input {@link PropertyBinding} stream
	 */
	<T> Stream<PropertyBinding<T, Input<T>>> stream();

	// Value change listener

	/**
	 * A listener for {@link Input} value change events when the {@link Input} is bound to a {@link Property} within a
	 * {@link PropertyInputBinder} component.
	 * @param <V> Value type
	 */
	@FunctionalInterface
	public interface PropertyInputValueChangeListener<V> extends Serializable {

		/**
		 * Invoked when this listener receives a value change event from an {@link ValueHolder} source to which it has
		 * been added within a {@link PropertyInputBinder}.
		 * @param event the value change event
		 * @param binder The {@link PropertyInputBinder} to which the value change source belongs
		 */
		void valueChange(ValueChangeEvent<V> event, PropertyInputBinder binder);

	}

}
