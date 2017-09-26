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
package com.holonplatform.vaadin.navigator.internal;

import java.lang.reflect.Field;

/**
 * Default {@link ViewContextField} implementation.
 *
 * @since 5.0.0
 */
public class DefaultViewContextField implements ViewContextField {

	/**
	 * Optional context resource key to inject
	 */
	private final String contextResourceKey;

	/**
	 * Required injection
	 */
	private final boolean required;

	/**
	 * Field reference
	 */
	private final Field field;

	/**
	 * Constructor
	 * @param contextResourceKey Context resource key
	 * @param required Whether the injection is required
	 * @param field Field reference
	 */
	public DefaultViewContextField(String contextResourceKey, boolean required, Field field) {
		super();
		this.contextResourceKey = contextResourceKey;
		this.required = required;
		this.field = field;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.navigator.internal.ViewContextField#getContextResourceKey()
	 */
	@Override
	public String getContextResourceKey() {
		return contextResourceKey;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.navigator.internal.ViewContextField#isRequired()
	 */
	@Override
	public boolean isRequired() {
		return required;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.navigator.internal.ViewContextField#getField()
	 */
	@Override
	public Field getField() {
		return field;
	}

}
