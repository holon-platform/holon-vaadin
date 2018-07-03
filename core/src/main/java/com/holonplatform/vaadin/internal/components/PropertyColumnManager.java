/*
 * Copyright 2016-2018 Axioma srl.
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

import java.util.Map;
import java.util.Optional;

/**
 * {@link PropertyColumn} definitions manager.
 *
 * @param <T> Item data type
 * @param <P> Item property type
 * 
 * @since 5.1.4
 */
public interface PropertyColumnManager<T, P> {

	/**
	 * Get the property column definitions.
	 * @return The property column definitions
	 */
	Map<P, PropertyColumn<T, P>> getColumnDefinitions();

	/**
	 * Checks if a {@link PropertyColumn} definition bound to given property is available.
	 * @param property Property to get the definition for (not null)
	 * @return The {@link PropertyColumn} definition bound to given property, or an empty Optional if not available
	 */
	Optional<PropertyColumn<T, P>> hasColumnDefinition(P property);

	/**
	 * Add (or replace) a {@link PropertyColumn} definition bound to given property.
	 * @param property Property (not null)
	 * @param propertyColumn PropertyColumn definition (not null)
	 */
	void addColumnDefinition(P property, PropertyColumn<T, P> propertyColumn);

}
