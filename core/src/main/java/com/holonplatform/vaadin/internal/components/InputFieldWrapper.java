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
import com.holonplatform.vaadin.components.Input;
import com.vaadin.data.HasValue;
import com.vaadin.shared.Registration;
import com.vaadin.ui.Component;
import com.vaadin.ui.Component.Focusable;

/**
 * A wrapper to wrap a {@link HasValue} component into a {@link Input} component.
 * 
 * @param <V> Value type
 * 
 * @since 5.0.0
 */
public class InputFieldWrapper<V> implements Input<V> {

	private static final long serialVersionUID = -2456516308895591627L;

	/**
	 * Wrapped field
	 */
	private final HasValue<V> field;
	/**
	 * Field component
	 */
	private final Component component;

	/**
	 * Constructor
	 * @param field Wrapped field and component (not null)
	 */
	public <H extends HasValue<V> & Component> InputFieldWrapper(H field) {
		this(field, field);
	}

	/**
	 * Constructor
	 * @param field Wrapped field (not null)
	 * @param component Field component
	 */
	public InputFieldWrapper(HasValue<V> field, Component component) {
		super();
		ObjectUtils.argumentNotNull(field, "Field must be not null");
		this.field = field;
		this.component = component;
	}

	/**
	 * Get the wrapped {@link HasValue}.
	 * @return the wrapped field
	 */
	public HasValue<V> getField() {
		return field;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(V value) {
		field.setValue(value);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#getValue()
	 */
	@Override
	public V getValue() {
		return field.getValue();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return field.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#clear()
	 */
	@Override
	public void clear() {
		field.clear();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#setReadOnly(boolean)
	 */
	@Override
	public void setReadOnly(boolean readOnly) {
		field.setReadOnly(readOnly);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#isReadOnly()
	 */
	@Override
	public boolean isReadOnly() {
		return field.isReadOnly();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#isRequired()
	 */
	@Override
	public boolean isRequired() {
		return field.isRequiredIndicatorVisible();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#setRequired(boolean)
	 */
	@Override
	public void setRequired(boolean required) {
		field.setRequiredIndicatorVisible(required);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#focus()
	 */
	@Override
	public void focus() {
		if (field instanceof Focusable) {
			((Focusable) field).focus();
		} else if (component instanceof Focusable) {
			((Focusable) component).focus();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#getComponent()
	 */
	@Override
	public Component getComponent() {
		return component;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#addValueChangeListener(com.holonplatform.vaadin.components.Input.
	 * ValueChangeListener)
	 */
	@Override
	public Registration addValueChangeListener(
			final com.holonplatform.vaadin.components.Input.ValueChangeListener<V> listener) {
		return ValueChangeListenerUtils.adapt(field, this, listener);
	}

}
