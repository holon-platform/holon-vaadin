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
package com.holonplatform.vaadin.internal.data;

import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import com.holonplatform.core.ParameterSet;
import com.holonplatform.core.Validator.ValidationException;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.vaadin.data.ItemIdentifierProvider;

/**
 * A {@link PropertyBox} wrapper which uses an {@link ItemIdentifierProvider} to extract the identifiers to be used for
 * <code>equals</code> and <code>hashCode</code> methods.
 * 
 * @since 5.0.0
 */
public class IdentifiablePropertyBox implements PropertyBox {

	private final PropertyBox propertyBox;
	private final Object identifier;

	public IdentifiablePropertyBox(PropertyBox propertyBox, ItemIdentifierProvider<PropertyBox, ?> identifier) {
		this(propertyBox, (Function<PropertyBox, Object>) pb -> identifier.getItemId(pb));
	}

	public IdentifiablePropertyBox(PropertyBox propertyBox, Function<PropertyBox, Object> identifier) {
		super();
		ObjectUtils.argumentNotNull(propertyBox, "PropertyBox must be not null");
		ObjectUtils.argumentNotNull(identifier, "Identifier function must be not null");
		this.propertyBox = propertyBox;
		this.identifier = identifier.apply(propertyBox);
		if (this.identifier == null) {
			throw new IllegalStateException(
					"The identifier function returned a null identifier for PropertyBox [" + propertyBox + "]");
		}
	}

	/**
	 * Get the {@link PropertyBox} identifier.
	 * @return the identifier (not null)
	 */
	public Object getIdentifier() {
		return identifier;
	}

	// TODO
	/* (non-Javadoc)
	 * @see com.holonplatform.core.property.PropertySet#getIdentifiers()
	 */
	@Override
	public Set<Property> getIdentifiers() {
		return propertyBox.getIdentifiers();
	}

	/* (non-Javadoc)
	 * @see com.holonplatform.core.property.PropertySet#getConfiguration()
	 */
	@Override
	public ParameterSet getConfiguration() {
		return propertyBox.getConfiguration();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.property.PropertySet#size()
	 */
	@Override
	public int size() {
		return propertyBox.size();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.property.PropertySet#contains(com.holonplatform.core.property.Property)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public boolean contains(Property property) {
		return propertyBox.contains(property);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.property.PropertySet#stream()
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Stream<Property> stream() {
		return propertyBox.stream();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Iterator<Property> iterator() {
		return propertyBox.iterator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.property.PropertyBox#containsValue(com.holonplatform.core.property.Property)
	 */
	@Override
	public <T> boolean containsValue(Property<T> property) {
		return propertyBox.containsValue(property);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.property.PropertyBox#getValue(com.holonplatform.core.property.Property)
	 */
	@Override
	public <T> T getValue(Property<T> property) {
		return propertyBox.getValue(property);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.property.PropertyBox#getValueIfPresent(com.holonplatform.core.property.Property)
	 */
	@Override
	public <T> Optional<T> getValueIfPresent(Property<T> property) {
		return propertyBox.getValueIfPresent(property);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.property.PropertyBox#propertyValues()
	 */
	@Override
	public <T> Stream<PropertyValue<T>> propertyValues() {
		return propertyBox.propertyValues();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.property.PropertyBox#setValue(com.holonplatform.core.property.Property,
	 * java.lang.Object)
	 */
	@Override
	public <T> void setValue(Property<T> property, T value) {
		propertyBox.setValue(property, value);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.property.PropertyBox#isInvalidAllowed()
	 */
	@Override
	public boolean isInvalidAllowed() {
		return propertyBox.isInvalidAllowed();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.property.PropertyBox#setInvalidAllowed(boolean)
	 */
	@Override
	public void setInvalidAllowed(boolean invalidAllowed) {
		propertyBox.setInvalidAllowed(invalidAllowed);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.property.PropertyBox#validate()
	 */
	@Override
	public void validate() throws ValidationException {
		propertyBox.validate();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
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
		IdentifiablePropertyBox other = (IdentifiablePropertyBox) obj;
		if (identifier == null) {
			if (other.identifier != null)
				return false;
		} else if (!identifier.equals(other.identifier))
			return false;
		return true;
	}

}
