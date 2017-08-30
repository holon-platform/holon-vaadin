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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.core.property.Property;
import com.holonplatform.vaadin.internal.components.DefaultValidationStatusHandler;
import com.holonplatform.vaadin.internal.components.LabelValidationStatusHandler;
import com.holonplatform.vaadin.internal.components.NotificationValidationStatusHandler;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;

/**
 * Handler for validation status change events, typically bound to a {@link ValueComponent} source object.
 * 
 * @since 5.0.0
 */
@FunctionalInterface
public interface ValidationStatusHandler {

	/**
	 * Invoked when the validation status has changed.
	 * @param statusChangeEvent the changed status event, providing validation status, error message and the optional
	 *        source component
	 */
	void validationStatusChange(ValidationStatusEvent<?> statusChangeEvent);

	/**
	 * Create and return the default {@link ValidationStatusHandler}.
	 * <p>
	 * The default validation status handler uses
	 * {@link AbstractComponent#setComponentError(com.vaadin.server.ErrorMessage)} to notify the validation status on
	 * the value component against whom the validation is performed, if it is available and the component returned by
	 * {@link ValueComponent#getComponent()} is an {@link AbstractComponent} instance.
	 * </p>
	 * @return A new default {@link ValidationStatusHandler} instance
	 */
	static ValidationStatusHandler getDefault() {
		return new DefaultValidationStatusHandler();
	}

	/**
	 * Create a {@link ValidationStatusHandler} which uses a {@link Label} to notify validation errors.
	 * <p>
	 * By default, the status {@link Label} is set to visible only when the validation status is invalid, i.e. a
	 * validation error to display is available.
	 * </p>
	 * @param statusLabel The {@link Label} to use to notify validation errors (not null)
	 * @return A new {@link Label} validation status handler instance
	 */
	static ValidationStatusHandler label(Label statusLabel) {
		return label(statusLabel, true);
	}

	/**
	 * Create a {@link ValidationStatusHandler} which uses a {@link Label} to notify validation errors.
	 * @param statusLabel The {@link Label} to use to notify validation errors (not null)
	 * @param hideWhenValid whether to hide the Label when the validation status is not invalid
	 * @return A new {@link Label} validation status handler instance
	 */
	static ValidationStatusHandler label(Label statusLabel, boolean hideWhenValid) {
		return new LabelValidationStatusHandler(statusLabel, hideWhenValid);
	}

	/**
	 * Create a {@link ValidationStatusHandler} which shows a {@link Notification} of type
	 * {@link Notification#TYPE_ERROR_MESSAGE} to notify validation errors.
	 * <p>
	 * This methods creates a notification validation status handler which displays only the first validation error.
	 * </p>
	 * @return A new notification validation status handler instance
	 */
	static ValidationStatusHandler notification() {
		return new NotificationValidationStatusHandler(null, false);
	}

	/**
	 * Create a {@link ValidationStatusHandler} which shows a {@link Notification} of type
	 * {@link Notification#TYPE_ERROR_MESSAGE} to notify validation errors.
	 * @param showAllErrors <code>true</code> to display all validation errors, <code>false</code> to show only the
	 *        first
	 * @return A new notification validation status handler instance
	 */
	static ValidationStatusHandler notification(boolean showAllErrors) {
		return new NotificationValidationStatusHandler(null, showAllErrors);
	}

	/**
	 * Create a {@link ValidationStatusHandler} which shows a {@link Notification} to notify validation errors.
	 * <p>
	 * This methods creates a notification validation status handler which displays only the first validation error.
	 * </p>
	 * @param notification The notification instance to use, <code>null</code> for default
	 * @return A new notification validation status handler instance
	 */
	static ValidationStatusHandler notification(Notification notification) {
		return new NotificationValidationStatusHandler(notification, false);
	}

	/**
	 * Create a {@link ValidationStatusHandler} which shows a {@link Notification} to notify validation errors.
	 * @param notification The notification instance to use, <code>null</code> for default
	 * @param showAllErrors <code>true</code> to display all validation errors, <code>false</code> to show only the
	 *        first
	 * @return A new notification validation status handler instance
	 */
	static ValidationStatusHandler notification(Notification notification, boolean showAllErrors) {
		return new NotificationValidationStatusHandler(notification, showAllErrors);
	}

	/**
	 * Validation status.
	 */
	public enum Status {

		/**
		 * Unresolved status, e.g. no value validation has been made yet.
		 */
		UNRESOLVED,

		/**
		 * Validation passed.
		 */
		VALID,

		/**
		 * Validation failed.
		 */
		INVALID;

	}

	/**
	 * A validation status event.
	 * 
	 * @param <V> Validation value type
	 */
	public interface ValidationStatusEvent<V> extends Serializable {

		/**
		 * Get the current validation status
		 * @return the validation status (never null)
		 */
		Status getStatus();

		/**
		 * Gets whether the validation failed or not.
		 * @return <code>true</code> if validation failed (i.e. the validation status is {@link Status#INVALID}),
		 *         <code>false</code> otherwise
		 */
		default boolean isInvalid() {
			return getStatus() == Status.INVALID;
		}

		/**
		 * Get the {@link Localizable} validation error messages, if the validation status is {@link Status#INVALID}.
		 * @return the validation error messages when in invalid state, an empty list if none
		 */
		List<Localizable> getErrors();

		/**
		 * Get the localized error messages from {@link #getErrors()}, using the context {@link LocalizationContext}, if
		 * available. If a {@link LocalizationContext} is not available, the default message of each localizable error
		 * message is returned.
		 * @return the localized error messages, an empty list if none.
		 */
		default List<String> getErrorMessages() {
			return getErrors().stream().map(e -> LocalizationContext.translate(e, true)).collect(Collectors.toList());
		}

		/**
		 * Get the first {@link Localizable} validation error message, if the validation status is
		 * {@link Status#INVALID}.
		 * @return the optional first {@link Localizable} validation error message when in invalid state
		 */
		default Optional<Localizable> getError() {
			return Optional.ofNullable(getErrors().size() > 0 ? getErrors().get(0) : null);
		}

		/**
		 * Get the first localized error message, using the context {@link LocalizationContext}, if available. If a
		 * {@link LocalizationContext} is not available, the default message of the first localizable error message is
		 * returned.
		 * @return the first localized error message, or <code>null</code> if none
		 */
		default String getErrorMessage() {
			return getError().map(e -> LocalizationContext.translate(e, true)).orElse(null);
		}

		/**
		 * If the validation event refers to a {@link Property} binding, returns the property to which the value
		 * component is bound.
		 * @return Optional {@link Property} to which the value component source of the validation event is bound, empty
		 *         if no property binding is available
		 */
		Optional<Property<V>> getProperty();

		/**
		 * Get the {@link ValueComponent} against whom the validation was performed.
		 * @return the source {@link ValueComponent}, empty if not available
		 */
		Optional<ValueComponent<V>> getSource();

	}

}
