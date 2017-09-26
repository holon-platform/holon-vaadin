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
package com.holonplatform.vaadin.internal.converters;

import com.vaadin.data.Converter;
import com.vaadin.data.Result;
import com.vaadin.data.ValueContext;
import com.vaadin.server.FontIcon;

/**
 * A converter to present {@link FontIcon} resources as HTML values, using {@link FontIcon#getHtml()}.
 * 
 * <p>
 * Backward to-model conversion is not supported.
 * </p>
 * 
 * @since 5.0.0
 */
public class FontIconPresentationConverter implements Converter<String, FontIcon> {

	private static final long serialVersionUID = 1967667430051792945L;

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Converter#convertToModel(java.lang.Object, com.vaadin.data.ValueContext)
	 */
	@Override
	public Result<FontIcon> convertToModel(String value, ValueContext context) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Converter#convertToPresentation(java.lang.Object, com.vaadin.data.ValueContext)
	 */
	@Override
	public String convertToPresentation(FontIcon value, ValueContext context) {
		if (value != null) {
			return value.getHtml();
		}
		return null;
	}

}
