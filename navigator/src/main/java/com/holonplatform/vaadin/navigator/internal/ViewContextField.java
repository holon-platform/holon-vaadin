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

import com.holonplatform.vaadin.navigator.annotations.ViewContext;

/**
 * A reference to a {@link ViewContext} annotated {@link Field}. For internal use.
 * 
 * @since 5.0.0
 */
public interface ViewContextField {

	/**
	 * Optional context resource key to inject
	 * @return Context resource key
	 */
	String getContextResourceKey();

	/**
	 * Whether the context injection is required
	 * @return Required, defaults to <code>true</code>
	 */
	boolean isRequired();

	/**
	 * Field reference
	 * @return Field
	 */
	Field getField();

	/**
	 * Build a ViewContextField
	 * @param contextResourceKey Context resource key
	 * @param required Required context injection
	 * @param field Field reference
	 * @return ViewContextField
	 */
	static ViewContextField build(String contextResourceKey, boolean required, Field field) {
		return new DefaultViewContextField(contextResourceKey, required, field);
	}

}
