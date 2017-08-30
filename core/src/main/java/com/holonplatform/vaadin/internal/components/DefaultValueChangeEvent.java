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

import com.holonplatform.vaadin.components.ValueHolder;
import com.holonplatform.vaadin.components.ValueHolder.ValueChangeEvent;

/**
 * Default {@link ValueChangeEvent} implementation.
 * 
 * @param <V> Value type
 *
 * @since 5.0.0
 */
public class DefaultValueChangeEvent<V> implements ValueChangeEvent<V> {

	private static final long serialVersionUID = 1L;

	private final ValueHolder<V> source;
	private final V oldValue;
	private final V value;

	/**
	 * Constructor
	 * @param source Source
	 * @param oldValue Old value
	 * @param value New value
	 */
	public DefaultValueChangeEvent(ValueHolder<V> source, V oldValue, V value) {
		super();
		this.source = source;
		this.oldValue = oldValue;
		this.value = value;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValueHolder.ValueChangeEvent#getSource()
	 */
	@Override
	public ValueHolder<V> getSource() {
		return source;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValueHolder.ValueChangeEvent#getOldValue()
	 */
	@Override
	public V getOldValue() {
		return oldValue;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValueHolder.ValueChangeEvent#getValue()
	 */
	@Override
	public V getValue() {
		return value;
	}

}
