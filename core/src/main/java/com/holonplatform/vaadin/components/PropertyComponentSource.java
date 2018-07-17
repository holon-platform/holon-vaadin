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

import java.util.stream.Stream;

import com.holonplatform.core.property.Property;
import com.vaadin.ui.Component;

/**
 * Represents a source of {@link Component}s associated with a {@link Property}.
 *
 * @since 5.0.6
 */
public interface PropertyComponentSource {

	/**
	 * Get the stream of available components.
	 * @return Components stream
	 */
	Stream<Component> getComponents();

	/**
	 * Return a {@link Stream} of the available {@link Property} and {@link Component}s bindings.
	 * @return Property-Component {@link PropertyBinding} stream
	 */
	Stream<PropertyBinding<?, Component>> streamOfComponents();

}
