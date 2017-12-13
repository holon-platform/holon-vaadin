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

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.internal.utils.TypeUtils;
import com.holonplatform.vaadin.components.ViewComponent;
import com.vaadin.shared.Registration;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;

/**
 * Base {@link ViewComponent} implementation, using a {@link Label} as backing UI component.
 * 
 * @param <T> Value type
 * 
 * @since 5.0.0
 */
public abstract class AbstractViewComponent<T> extends CustomComponent implements ViewComponent<T> {

	private static final long serialVersionUID = 1112624954933562344L;

	/**
	 * Concrete value type
	 */
	private final Class<? extends T> type;

	/**
	 * Value change listeners
	 */
	private final List<com.holonplatform.vaadin.components.Input.ValueChangeListener<T>> valueChangeListeners = new LinkedList<>();

	/**
	 * Component value
	 */
	private T value;

	/**
	 * Constructor
	 * @param type Concrete value type
	 * @param root Root component
	 */
	public AbstractViewComponent(Class<? extends T> type, Component root) {
		super(root);
		this.type = TypeUtils.box(type);

		setSizeUndefined();

		addStyleName("h-viewcomponent");
	}

	/**
	 * Called when a new value is setted in view component using {@link #setValue(Object)}. Subclasses should update the
	 * state of the backing UI component to display the value.
	 * @param value The new value
	 */
	protected abstract void updateValue(T value);

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.ui.AbstractComponent#setHeight(float, com.vaadin.server.Sizeable.Unit)
	 */
	@Override
	public void setHeight(float height, Unit unit) {
		super.setHeight(height, unit);
		updateRootSize();
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.ui.AbstractComponent#setWidth(float, com.vaadin.server.Sizeable.Unit)
	 */
	@Override
	public void setWidth(float width, Unit unit) {
		super.setWidth(width, unit);
		updateRootSize();
	}

	/**
	 * Update root component size when component size changes
	 */
	protected void updateRootSize() {
		if (getCompositionRoot() != null) {
			if (getWidth() > -1) {
				getCompositionRoot().setWidth(100, Unit.PERCENTAGE);
			} else {
				getCompositionRoot().setWidthUndefined();
			}
			if (getHeight() > -1) {
				getCompositionRoot().setHeight(100, Unit.PERCENTAGE);
			} else {
				getCompositionRoot().setHeightUndefined();
			}
		}
	}

	/**
	 * Get the concrete content {@link Component}.
	 * @return Content {@link Component}
	 */
	protected Optional<Component> getContent() {
		return Optional.ofNullable(getCompositionRoot());
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.ui.AbstractComponent#setStyleName(java.lang.String)
	 */
	@Override
	public void setStyleName(String style) {
		super.setStyleName(style);
		// replicate to content
		getContent().ifPresent(c -> c.setStyleName(style));
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.ui.AbstractComponent#addStyleName(java.lang.String)
	 */
	@Override
	public void addStyleName(String style) {
		super.addStyleName(style);
		// replicate to content
		getContent().ifPresent(c -> c.addStyleName(style));
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.ui.AbstractComponent#removeStyleName(java.lang.String)
	 */
	@Override
	public void removeStyleName(String style) {
		super.removeStyleName(style);
		// replicate to content
		getContent().ifPresent(c -> c.removeStyleName(style));
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Property#getValue()
	 */
	@Override
	public T getValue() {
		return value;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValueHolder#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(T value) {
		T oldValue = this.value;
		this.value = value;
		// update internal component
		updateValue(value);
		// fire value change
		fireValueChange(oldValue, value);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValueComponent#getComponent()
	 */
	@Override
	public Component getComponent() {
		return this;
	}

	/**
	 * Get the value type.
	 * @return the value type
	 */
	public Class<? extends T> getType() {
		return type;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValueHolder#addValueChangeListener(com.holonplatform.vaadin.components.
	 * ValueHolder.ValueChangeListener)
	 */
	@Override
	public Registration addValueChangeListener(
			com.holonplatform.vaadin.components.Input.ValueChangeListener<T> listener) {
		ObjectUtils.argumentNotNull(listener, "ValueChangeListener must be not null");
		valueChangeListeners.add(listener);
		return () -> valueChangeListeners.remove(listener);
	}

	/**
	 * Emits the value change event
	 * @param oldValue the old value
	 * @param value the changed value
	 */
	protected void fireValueChange(T oldValue, T value) {
		final com.holonplatform.vaadin.components.Input.ValueChangeEvent<T> valueChangeEvent = new DefaultValueChangeEvent<>(
				this, oldValue, value, false);
		valueChangeListeners.forEach(l -> l.valueChange(valueChangeEvent));
	}

}
