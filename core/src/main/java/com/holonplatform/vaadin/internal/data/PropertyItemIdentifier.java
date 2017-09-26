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
package com.holonplatform.vaadin.internal.data;

import com.holonplatform.core.exceptions.DataAccessException;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.vaadin.data.ItemIdentifierProvider;

/**
 * An {@link ItemIdentifierProvider} using a {@link Property} to obtain a {@link PropertyBox} item identifier.
 *
 * @since 5.0.0
 */
public class PropertyItemIdentifier<ID> implements ItemIdentifierProvider<PropertyBox, ID> {

	private static final long serialVersionUID = -1930051385936109313L;

	private final Property<ID> identifierProperty;

	/**
	 * Constructor
	 * @param identifierProperty Property to use to obtain the item identifier value from {@link PropertyBox} (not null)
	 */
	public PropertyItemIdentifier(Property<ID> identifierProperty) {
		super();
		ObjectUtils.argumentNotNull(identifierProperty, "Identifier property must be not null");
		this.identifierProperty = identifierProperty;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemIdentifierProvider#getItemId(java.lang.Object)
	 */
	@Override
	public ID getItemId(PropertyBox item) {
		if (item != null) {
			return item.getValueIfPresent(identifierProperty)
					.orElseThrow(() -> new DataAccessException("The identifier property [" + identifierProperty
							+ "] is not present in PropertyBox item [" + item + "]"));
		}
		return null;
	}

}
