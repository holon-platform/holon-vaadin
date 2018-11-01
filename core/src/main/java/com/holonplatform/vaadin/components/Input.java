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
package com.holonplatform.vaadin.components;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyRenderer;
import com.holonplatform.core.property.PropertyValueConverter;
import com.holonplatform.vaadin.components.ValueHolder.MaySupportValueChangeMode;
import com.holonplatform.vaadin.internal.components.InputConverterAdapter;
import com.holonplatform.vaadin.internal.components.InputFieldWrapper;
import com.vaadin.data.Converter;
import com.vaadin.data.HasValue;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.Component;

/**
 * Input component representation, i.e. a UI component that has a user-editable value.
 * <p>
 * Extends {@link ValueHolder} since handles a value, supporting {@link ValueChangeListener}s registration.
 * </p>
 * <p>
 * The actual UI {@link Component} which represents the input component can be obtained through {@link #getComponent()}.
 * </p>
 * <p>
 * Extends {@link MaySupportValueChangeMode} to allow value change mode and timeout configuration for input components
 * which support it.
 * </p>
 * 
 * @param <V> Value type
 * 
 * @since 5.0.0
 */
public interface Input<V> extends ValueHolder<V>, ValueComponent<V>, MaySupportValueChangeMode {

	/**
	 * Sets the read-only mode of this input component. The user can't change the value when in read-only mode.
	 * @param readOnly the read-only mode of this input component
	 */
	void setReadOnly(boolean readOnly);

	/**
	 * Returns whether this input component is in read-only mode or not.
	 * @return <code>false</code> if the user can modify the value, <code>true</code> if not
	 */
	boolean isReadOnly();

	/**
	 * Gets whether the field is <em>required</em>, i.e. a <em>required indicator</em> symbol is visible.
	 * @return <code>true</code> if the field as required, <code>false</code> otherwise
	 */
	public boolean isRequired();

	/**
	 * Sets whether the <em>required indicator</em> symbol is visible.
	 * @param required <code>true</code> to set the field as required, <code>false</code> otherwise
	 */
	void setRequired(boolean required);

	/**
	 * Sets the focus for this input component, if supported by concrete component implementation.
	 */
	void focus();

	// By default, behave as value change mode is not supported

	@Override
	default boolean isValueChangeModeSupported() {
		return false;
	}

	@Override
	default void setValueChangeMode(ValueChangeMode valueChangeMode) {
		// not supported by default
	}

	@Override
	default ValueChangeMode getValueChangeMode() {
		return ValueChangeMode.BLUR;
	}

	@Override
	default void setValueChangeTimeout(int valueChangeTimeout) {
		// not supported by default
	}

	@Override
	default int getValueChangeTimeout() {
		return -1;
	}

	// Adapters

	/**
	 * Create a {@link Input} component type from given {@link HasValue} component.
	 * @param <T> Value type
	 * @param <F> {@link HasValue} component type
	 * @param field The field instance (not null)
	 * @return A new {@link Input} component which wraps the given <code>field</code>
	 */
	static <F extends HasValue<T> & Component, T> Input<T> from(F field) {
		return new InputFieldWrapper<>(field);
	}

	// Converters

	/**
	 * Create a new {@link Input} from another {@link Input} with a different value type, using given {@link Converter}
	 * to perform value conversions.
	 * @param <T> New value type
	 * @param <V> Original value type
	 * @param input Actual input (not null)
	 * @param converter Value converter (not null)
	 * @return A new {@link Input} of the converted value type
	 */
	static <T, V> Input<T> from(Input<V> input, Converter<V, T> converter) {
		return new InputConverterAdapter<>(input, converter);
	}

	/**
	 * Create a new {@link Input} from given {@link HasValue} component with a different value type, using given
	 * {@link Converter} to perform value conversions.
	 * @param <F> {@link HasValue} component type
	 * @param <T> New value type
	 * @param <V> Original value type
	 * @param field The field (not null)
	 * @param converter Value converter (not null)
	 * @return A new {@link Input} of the converted value type
	 */
	static <F extends HasValue<V> & Component, T, V> Input<T> from(F field, Converter<V, T> converter) {
		return from(from(field), converter);
	}

	/**
	 * Create a new {@link Input} from another {@link Input} with a different value type, using given
	 * {@link PropertyValueConverter} to perform value conversions.
	 * @param <T> New value type
	 * @param <V> Original value type
	 * @param input Actual input (not null)
	 * @param property Property to provide to the converter
	 * @param converter Value converter (not null)
	 * @return A new {@link Input} of the converted value type
	 */
	static <T, V> Input<T> from(Input<V> input, Property<T> property, PropertyValueConverter<T, V> converter) {
		ObjectUtils.argumentNotNull(converter, "PropertyValueConverter must be not null");
		return new InputConverterAdapter<>(input, Converter.from(value -> converter.fromModel(value, property),
				value -> converter.toModel(value, property), e -> e.getMessage()));
	}

	/**
	 * Create a new {@link Input} from another {@link Input} with a different value type, using given
	 * {@link PropertyValueConverter} to perform value conversions.
	 * @param <F> {@link HasValue} component type
	 * @param <T> New value type
	 * @param <V> Original value type
	 * @param field The field (not null)
	 * @param property Property to provide to the converter
	 * @param converter Value converter (not null)
	 * @return A new {@link Input} of the converted value type
	 */
	static <F extends HasValue<V> & Component, T, V> Input<T> from(F field, Property<T> property,
			PropertyValueConverter<T, V> converter) {
		return from(from(field), property, converter);
	}

	// Renderers

	/**
	 * A convenience interface with a fixed {@link Input} rendering type to use a {@link Input} {@link PropertyRenderer}
	 * as a functional interface.
	 * @param <T> Property type
	 */
	@FunctionalInterface
	public interface InputPropertyRenderer<T> extends PropertyRenderer<Input<T>, T> {

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.core.property.PropertyRenderer#getRenderType()
		 */
		@SuppressWarnings("unchecked")
		@Override
		default Class<? extends Input<T>> getRenderType() {
			return (Class<? extends Input<T>>) (Class<?>) Input.class;
		}

	}

	/**
	 * A convenience interface to render a {@link Property} as a {@link Input} using a {@link HasValue} component.
	 * @param <T> Property type
	 */
	public interface InputFieldPropertyRenderer<T, F extends HasValue<T> & Component> extends InputPropertyRenderer<T> {

		/**
		 * Render given <code>property</code> as consistent value type {@link HasValue} component to handle the property
		 * value.
		 * @param property Property to render
		 * @return property {@link HasValue} component
		 */
		F renderField(Property<? extends T> property);

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.core.property.PropertyRenderer#render(com.holonplatform.core.property.Property)
		 */
		@Override
		default Input<T> render(Property<? extends T> property) {
			return Input.from(renderField(property));
		}

	}

}
