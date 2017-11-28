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
package com.holonplatform.vaadin.internal.components;

import com.holonplatform.core.internal.Logger;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.vaadin.data.ItemConverter;
import com.holonplatform.vaadin.internal.VaadinLogger;
import com.vaadin.data.Result;
import com.vaadin.data.ValueContext;

/**
 * A {@link DefaultPropertyBoxConverter} able to convert a value to the model {@link PropertyBox} using an
 * {@link ItemConverter}.
 * 
 * @param <T> Property type
 * 
 * @since 5.0.3
 */
public class ReversiblePropertyBoxConverter<T> extends DefaultPropertyBoxConverter<T> {

	private static final long serialVersionUID = -7712918341789631044L;

	private final static Logger LOGGER = VaadinLogger.create();

	private final ItemConverter<T, PropertyBox> itemConverter;

	public ReversiblePropertyBoxConverter(Property<T> property, ItemConverter<T, PropertyBox> itemConverter) {
		super(property);
		ObjectUtils.argumentNotNull(itemConverter, "ItemConverter must be not null");
		this.itemConverter = itemConverter;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.DefaultPropertyBoxConverter#convertToModel(java.lang.Object,
	 * com.vaadin.data.ValueContext)
	 */
	@Override
	public Result<PropertyBox> convertToModel(T value, ValueContext context) {
		try {
			return Result.ok(itemConverter.convert(value));
		} catch (Exception e) {
			LOGGER.error("Conversion to model failed", e);
			return Result.error(e.getMessage());
		}
	}

}
