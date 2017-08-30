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

import java.util.stream.Stream;

import com.holonplatform.core.property.Property;

/**
 * Represents an object bound to a {@link Property} set.
 *
 * @since 5.0.0
 */
public interface PropertySetBound {

	/**
	 * Gets the set of {@link Property}s to which this component is bound.
	 * @return An {@link Iterable} on the property set (never null)
	 */
	@SuppressWarnings("rawtypes")
	Iterable<Property> getProperties();

	/**
	 * Gets whether given <code>property</code> is part of the property set to which this component is bound.
	 * @param property Property to check (not null)
	 * @return <code>true</code> if given <code>property</code> is part of the property set to which this component is
	 *         bound, <code>false</code> otherwise
	 */
	boolean hasProperty(Property<?> property);

	/**
	 * Get a {@link Stream} of the properties to which this component is bound.
	 * @return the property set {@link Stream}
	 */
	@SuppressWarnings("rawtypes")
	Stream<Property> propertyStream();

}
