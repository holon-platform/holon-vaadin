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

import com.holonplatform.core.internal.utils.ConversionUtils;
import com.vaadin.data.Converter;
import com.vaadin.data.Result;
import com.vaadin.data.ValueContext;

/**
 * A {@link Converter}s that convert from a {@link Number} types to another {@link Number} type and back.
 * 
 * @param <PRESENTATION> Presentation type
 * @param <MODEL> Model type
 *
 * @since 5.0.0
 */
public class NumberToNumberConverter<PRESENTATION extends Number, MODEL extends Number>
		implements Converter<PRESENTATION, MODEL> {

	private static final long serialVersionUID = 3540797381624942323L;

	private final Class<? extends PRESENTATION> presentationType;
	private final Class<? extends MODEL> modelType;

	/**
	 * Constructor
	 * @param presentationType Presentation Number type
	 * @param modelType Model Number type
	 */
	public NumberToNumberConverter(Class<? extends PRESENTATION> presentationType, Class<? extends MODEL> modelType) {
		super();
		this.presentationType = presentationType;
		this.modelType = modelType;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Converter#convertToModel(java.lang.Object, com.vaadin.data.ValueContext)
	 */
	@Override
	public Result<MODEL> convertToModel(PRESENTATION value, ValueContext context) {
		if (value != null) {
			return Result.ok(ConversionUtils.convertNumberToTargetClass(value, modelType));
		}
		return Result.ok(null);
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Converter#convertToPresentation(java.lang.Object, com.vaadin.data.ValueContext)
	 */
	@Override
	public PRESENTATION convertToPresentation(MODEL value, ValueContext context) {
		if (value != null) {
			return ConversionUtils.convertNumberToTargetClass(value, presentationType);
		}
		return null;
	}

}
