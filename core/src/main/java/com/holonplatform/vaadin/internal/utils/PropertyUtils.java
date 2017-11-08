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
package com.holonplatform.vaadin.internal.utils;

import java.io.Serializable;

import com.holonplatform.core.Path;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;

/**
 * Utility class for {@link Property} management in Vaadin components.
 * 
 * @since 5.0.0
 */
public final class PropertyUtils implements Serializable {

	private static final long serialVersionUID = -2351742854548713009L;

	private PropertyUtils() {
	}

	/**
	 * Generate a String type property id for given property.
	 * @param property Property for which to generate the property id (not null)
	 * @return Property id
	 */
	public static String generatePropertyId(Property<?> property) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		return (Path.class.isAssignableFrom(property.getClass())) ? ((Path<?>) property).relativeName()
				: String.valueOf(property.hashCode());
	}

}
