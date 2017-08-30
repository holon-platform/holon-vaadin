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
package com.holonplatform.vaadin.internal.data;

import java.io.Serializable;
import java.util.Arrays;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.vaadin.data.ItemIdentifierProvider;

/**
 * A {@link ItemIdentifierProvider} using a set a {@link Property}s as identifiers, providing consistent
 * {@link Object#equals(Object)} and {@link Object#hashCode()} methods.
 * 
 * @since 5.0.0
 */
@SuppressWarnings("rawtypes")
public class PropertiesItemIdentifier implements ItemIdentifierProvider<PropertyBox, Object> {

	private static final long serialVersionUID = 1302051519350991920L;

	private final Property[] properties;

	public PropertiesItemIdentifier(Property... properties) {
		super();
		ObjectUtils.argumentNotNull(properties, "Identifier properties must be not null");
		if (properties.length == 0) {
			throw new IllegalArgumentException("Identifier properties must be not empty");
		}
		this.properties = properties;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemIdentifierProvider#getItemId(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object getItemId(PropertyBox item) {
		if (item != null) {
			if (properties.length == 1) {
				return item.getValue(properties[0]);
			}
			Object[] values = new Object[properties.length];
			for (int i = 0; i < properties.length; i++) {
				values[i] = item.getValue(properties[i]);
			}
			return new Identifier(values);
		}
		return null;
	}

	@SuppressWarnings("serial")
	private class Identifier implements Serializable {

		private final Object[] values;

		public Identifier(Object[] values) {
			super();
			this.values = values;
		}

		/*
		 * (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + Arrays.hashCode(values);
			return result;
		}

		/*
		 * (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Identifier other = (Identifier) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (!Arrays.equals(values, other.values))
				return false;
			return true;
		}

		private PropertiesItemIdentifier getOuterType() {
			return PropertiesItemIdentifier.this;
		}

	}

}
