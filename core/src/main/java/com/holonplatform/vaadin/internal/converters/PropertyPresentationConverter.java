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
package com.holonplatform.vaadin.internal.converters;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.vaadin.data.Converter;
import com.vaadin.data.Result;
import com.vaadin.data.ValueContext;

/**
 * A {@link Converter} which uses {@link Property#present(Object)} method to convert a {@link Property} value to a
 * {@link String} presentation type.
 * <p>
 * Backward to-model conversion is not supported.
 * </p>
 * 
 * @param <T> Property type
 * 
 * @since 5.0.0
 */
public class PropertyPresentationConverter<T> implements Converter<String, T> {

	private static final long serialVersionUID = 7645087071499012088L;

	/**
	 * Property
	 */
	private final Property<T> property;

	/**
	 * Construct a new PropertyPresentationConverter
	 * @param property Property to convert (not null)
	 */
	public PropertyPresentationConverter(Property<T> property) {
		super();
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		this.property = property;
	}

	/**
	 * Gets the Property
	 * @return the property
	 */
	protected Property<T> getProperty() {
		return property;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Converter#convertToModel(java.lang.Object, com.vaadin.data.ValueContext)
	 */
	@Override
	public Result<T> convertToModel(String value, ValueContext context) {
		return Result.error("Conversion to model is not supported");
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Converter#convertToPresentation(java.lang.Object, com.vaadin.data.ValueContext)
	 */
	@Override
	public String convertToPresentation(T value, ValueContext context) {
		return getProperty().present(value);
	}

}
