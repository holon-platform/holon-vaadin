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

import java.util.Optional;
import java.util.stream.Stream;

import com.holonplatform.core.property.Property;

/**
 * Represent a source of {@link ValueComponent}s bound to a {@link Property} set, i.e. a container of
 * {@link ValueComponent} and {@link Property} bindings.
 *
 * @since 5.0.0
 */
public interface PropertyValueComponentSource extends PropertySetBound {

	/**
	 * Gets all the {@link ValueComponent}s that have been bound to a property.
	 * @return An {@link Iterable} on all property bound {@link ValueComponent}s
	 */
	@SuppressWarnings("rawtypes")
	Iterable<ValueComponent> getValueComponents();

	/**
	 * Get the {@link ValueComponent} bound to given <code>property</code>, if any.
	 * @param <T> Component type
	 * @param property Property for which to get the associated {@link ValueComponent} (not null)
	 * @return Optional {@link ValueComponent} bound to given <code>property</code>
	 */
	<T> Optional<ValueComponent<T>> getValueComponent(Property<T> property);

	/**
	 * Return a {@link Stream} of the available {@link Property} and {@link ValueComponent}s bindings.
	 * @return Property-ValueComponent {@link PropertyBinding} stream
	 */
	Stream<PropertyBinding<?, ValueComponent<?>>> streamOfValueComponents();
}
