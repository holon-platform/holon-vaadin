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

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.components.PropertyInputBinder;
import com.holonplatform.vaadin.components.PropertyInputBinder.PropertyInputValueChangeListener;
import com.holonplatform.vaadin.components.ValueHolder.ValueChangeEvent;
import com.holonplatform.vaadin.components.ValueHolder.ValueChangeListener;

/**
 * Adapter to handle a {@link PropertyInputValueChangeListener} using a {@link ValueChangeListener}.
 * 
 * @param <T> Value type
 * 
 * @since 5.0.5
 */
public class PropertyInputValueChangeListenerAdapter<T> implements ValueChangeListener<T> {

	private static final long serialVersionUID = 1977003312358192506L;

	private final PropertyInputBinder binder;
	private final PropertyInputValueChangeListener<T> propertyInputValueChangeListener;

	/**
	 * Constructor
	 * @param binder The binder (not null)
	 * @param propertyInputValueChangeListener Value change listener (not null)
	 */
	public PropertyInputValueChangeListenerAdapter(PropertyInputBinder binder,
			PropertyInputValueChangeListener<T> propertyInputValueChangeListener) {
		super();
		ObjectUtils.argumentNotNull(binder, "PropertyInputBinder must be not null");
		ObjectUtils.argumentNotNull(propertyInputValueChangeListener,
				"PropertyInputValueChangeListener must be not null");
		this.binder = binder;
		this.propertyInputValueChangeListener = propertyInputValueChangeListener;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValueHolder.ValueChangeListener#valueChange(com.holonplatform.vaadin.
	 * components.ValueHolder.ValueChangeEvent)
	 */
	@Override
	public void valueChange(ValueChangeEvent<T> event) {
		propertyInputValueChangeListener.valueChange(event, binder);
	}

}
