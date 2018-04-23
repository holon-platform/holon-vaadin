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
import com.holonplatform.vaadin.components.Input;
import com.vaadin.data.Converter;
import com.vaadin.data.ValueContext;
import com.vaadin.shared.Registration;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.Component;

/**
 * Adapter class to build a {@link Input} of a different value type from another {@link Input}, using a suitable
 * {@link Converter}.
 * 
 * @param <T> Presentation value type
 * @param <V> Model value type
 *
 * @since 5.0.5
 */
public class InputConverterAdapter<T, V> implements Input<V> {

	private static final long serialVersionUID = -2429215257047725962L;

	private final Input<T> input;
	private final Converter<T, V> converter;

	/**
	 * Constructor.
	 * @param input The actual Input (not null)
	 * @param converter The value converter (not null)
	 */
	public InputConverterAdapter(Input<T> input, Converter<T, V> converter) {
		super();
		ObjectUtils.argumentNotNull(input, "Input must be not null");
		ObjectUtils.argumentNotNull(converter, "Converter must be not null");
		this.input = input;
		this.converter = converter;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValueHolder#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(V value) {
		input.setValue(convertToPresentation(value));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValueHolder#getValue()
	 */
	@Override
	public V getValue() {
		return convertToModel(input.getValue());
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValueHolder#addValueChangeListener(com.holonplatform.vaadin.components.
	 * ValueHolder.ValueChangeListener)
	 */
	@Override
	public Registration addValueChangeListener(
			com.holonplatform.vaadin.components.ValueHolder.ValueChangeListener<V> listener) {
		ObjectUtils.argumentNotNull(listener, "ValueChangeListener must be not null");
		return input.addValueChangeListener(e -> listener.valueChange(new DefaultValueChangeEvent<>(this,
				convertToModel(e.getOldValue()), convertToModel(e.getValue()), e.isUserOriginated())));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValueComponent#getComponent()
	 */
	@Override
	public Component getComponent() {
		return input.getComponent();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#setReadOnly(boolean)
	 */
	@Override
	public void setReadOnly(boolean readOnly) {
		input.setReadOnly(readOnly);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#isReadOnly()
	 */
	@Override
	public boolean isReadOnly() {
		return input.isReadOnly();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#isRequired()
	 */
	@Override
	public boolean isRequired() {
		return input.isRequired();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#setRequired(boolean)
	 */
	@Override
	public void setRequired(boolean required) {
		input.setRequired(required);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#focus()
	 */
	@Override
	public void focus() {
		input.focus();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValueHolder#getEmptyValue()
	 */
	@Override
	public V getEmptyValue() {
		return convertToModel(input.getEmptyValue());
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValueHolder#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return input.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValueHolder#clear()
	 */
	@Override
	public void clear() {
		input.clear();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#isValueChangeModeSupported()
	 */
	@Override
	public boolean isValueChangeModeSupported() {
		return input.isValueChangeModeSupported();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#setValueChangeMode(com.vaadin.shared.ui.ValueChangeMode)
	 */
	@Override
	public void setValueChangeMode(ValueChangeMode valueChangeMode) {
		input.setValueChangeMode(valueChangeMode);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#getValueChangeMode()
	 */
	@Override
	public ValueChangeMode getValueChangeMode() {
		return input.getValueChangeMode();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#setValueChangeTimeout(int)
	 */
	@Override
	public void setValueChangeTimeout(int valueChangeTimeout) {
		input.setValueChangeTimeout(valueChangeTimeout);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#getValueChangeTimeout()
	 */
	@Override
	public int getValueChangeTimeout() {
		return input.getValueChangeTimeout();
	}

	/**
	 * Convert the presentation value type to model value type.
	 * @param value Value to convert
	 * @return Converted value
	 */
	private V convertToModel(T value) {
		return converter.convertToModel(value, _valueContext()).getOrThrow(error -> new RuntimeException(error));
	}

	/**
	 * Convert the model value type to presentation value type.
	 * @param value Value to convert
	 * @return Converted value
	 */
	private T convertToPresentation(V value) {
		return converter.convertToPresentation(value, _valueContext());
	}

	/**
	 * Build the {@link ValueContext} to be used with the converter.
	 * @return the {@link ValueContext}
	 */
	private ValueContext _valueContext() {
		final Component component = getComponent();
		if (component != null) {
			return new ValueContext(component);
		} else {
			return new ValueContext();
		}
	}

}
