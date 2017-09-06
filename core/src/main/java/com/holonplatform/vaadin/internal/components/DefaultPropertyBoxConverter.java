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
package com.holonplatform.vaadin.internal.components;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.vaadin.data.Converter;
import com.vaadin.data.Result;
import com.vaadin.data.ValueContext;

/**
 * Default one-way {@link Converter} to convert a {@link PropertyBox} into a property value.
 * 
 * @param <T> Property type
 * 
 * @since 5.0.0
 */
public class DefaultPropertyBoxConverter<T> implements Converter<T, PropertyBox> {

	private static final long serialVersionUID = 4380616372709849093L;

	private final Property<T> property;

	public DefaultPropertyBoxConverter(Property<T> property) {
		super();
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		this.property = property;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Converter#convertToModel(java.lang.Object, com.vaadin.data.ValueContext)
	 */
	@Override
	public Result<PropertyBox> convertToModel(T value, ValueContext context) {
		return Result.error("Conversion to model is not supported");
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Converter#convertToPresentation(java.lang.Object, com.vaadin.data.ValueContext)
	 */
	@Override
	public T convertToPresentation(PropertyBox value, ValueContext context) {
		if (value != null) {
			return value.getValue(property);
		}
		return null;
	}

}
