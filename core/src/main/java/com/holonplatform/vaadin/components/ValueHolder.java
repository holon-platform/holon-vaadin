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

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import com.vaadin.shared.Registration;
import com.vaadin.shared.ui.ValueChangeMode;

/**
 * Represents an object which holds a value and provide methods to handle such value.
 * 
 * @param <V> Value type
 * 
 * @since 5.0.0
 */
public interface ValueHolder<V> extends Serializable {

	/**
	 * Sets the <code>value</code> of this value holder.
	 * @param value the value to set
	 * @throws IllegalArgumentException if the value is not valid
	 */
	void setValue(V value);

	/**
	 * Gets the current value of this value holder.
	 * @return the current value
	 */
	V getValue();

	/**
	 * Get the current value of this value holder, if available (i.e. not <code>null</code>).
	 * @return Optional current value
	 */
	default Optional<V> getValueIfPresent() {
		return Optional.ofNullable(getValue());
	}

	/**
	 * Returns the value that represents an empty value.
	 * @return the value that represents an empty value (<code>null</code> by default)
	 */
	default V getEmptyValue() {
		return null;
	}

	/**
	 * Returns whether this value holder is considered to be empty, according to its current value.
	 * <p>
	 * By default this is an equality check between current value and empty value.
	 * </p>
	 * @return <code>true</code> if considered empty, <code>false</code> if not
	 */
	default boolean isEmpty() {
		return Objects.equals(getValue(), getEmptyValue());
	}

	/**
	 * Clears this value holder.
	 * <p>
	 * By default, resets the value to the empty one.
	 * </p>
	 */
	default void clear() {
		setValue(getEmptyValue());
	}

	/**
	 * Gets the current value of this value holder as an {@link Optional}, which will be empty if the value holder is
	 * considered to be empty.
	 * @return the current optional value
	 */
	default Optional<V> getOptionalValue() {
		return isEmpty() ? Optional.empty() : Optional.ofNullable(getValue());
	}

	// Value change handling

	/**
	 * Adds a value change listener, called when the value changes.
	 * @param listener the value change listener to add (not null)
	 * @return a registration for the listener, which provides the <em>remove</em> operation
	 */
	public Registration addValueChangeListener(ValueChangeListener<V> listener);

	/**
	 * A listener for {@link ValueHolder} value change events.
	 * @param <V> Value type
	 */
	@FunctionalInterface
	public interface ValueChangeListener<V> extends Serializable {

		/**
		 * Invoked when this listener receives a value change event from an {@link ValueHolder} source to which it has
		 * been added.
		 * @param event the value change event
		 */
		void valueChange(ValueChangeEvent<V> event);

	}

	/**
	 * A {@link ValueChangeListener} event.
	 * @param <V> Value type
	 */
	public interface ValueChangeEvent<V> extends Serializable {

		/**
		 * Returns whether this event was triggered by user interaction, on the client side, or programmatically, on the
		 * server side.
		 * @return <code>true</code> if this event originates from the client, <code>false</code> otherwise.
		 * @since 5.0.5
		 */
		boolean isUserOriginated();

		/**
		 * Get the source of this value change event.
		 * @return the {@link ValueHolder} source
		 */
		ValueHolder<V> getSource();

		/**
		 * Returns the value of the source before this value change event occurred.
		 * @return the old value
		 */
		V getOldValue();

		/**
		 * Returns the new value that triggered this value change event.
		 * @return the new value
		 */
		V getValue();

	}

	/**
	 * Declares that the {@link ValueChangeMode} handling may be supported and provides methods to configure it.
	 */
	public interface MaySupportValueChangeMode {

		/**
		 * Gets whether the {@link ValueChangeMode} is supported for this component.
		 * @return <code>true</code> if the {@link ValueChangeMode} is supported, <code>false</code> otherwise
		 */
		boolean isValueChangeModeSupported();

		/**
		 * Sets the mode how value change events are triggered.
		 * <p>
		 * If {@link ValueChangeMode} is not supported, this method has no effect.
		 * </p>
		 * @param valueChangeMode the value change mode to set (not null)
		 * @see #isValueChangeModeSupported()
		 */
		void setValueChangeMode(ValueChangeMode valueChangeMode);

		/**
		 * Get the mode how value change events are triggered.
		 * <p>
		 * If {@link ValueChangeMode} is not supported, {@link ValueChangeMode#BLUR} is returned.
		 * </p>
		 * @return the value change mode
		 * @see #isValueChangeModeSupported()
		 */
		ValueChangeMode getValueChangeMode();

		/**
		 * Sets how often value change events are triggered when the {@link ValueChangeMode} is set to either
		 * {@link ValueChangeMode#LAZY} or {@link ValueChangeMode#TIMEOUT}.
		 * <p>
		 * If {@link ValueChangeMode} is not supported, this method has no effect.
		 * </p>
		 * @param valueChangeTimeout the timeout in milliseconds, (greater or equal to 0)
		 * @see #isValueChangeModeSupported()
		 */
		void setValueChangeTimeout(int valueChangeTimeout);

		/**
		 * Returns the currently set timeout, in milliseconds, for how often {@link ValueChangeEvent}s are triggered if
		 * the current {@link ValueChangeMode} is set to either {@link ValueChangeMode#LAZY} or
		 * {@link ValueChangeMode#TIMEOUT}.
		 * <p>
		 * If {@link ValueChangeMode} is not supported, this method always returns <code>-1</code>.
		 * </p>
		 * @return the timeout in milliseconds of how often value change events are triggered.
		 * @see #isValueChangeModeSupported()
		 */
		int getValueChangeTimeout();

	}

}
