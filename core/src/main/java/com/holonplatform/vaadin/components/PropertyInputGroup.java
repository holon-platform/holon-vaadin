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
package com.holonplatform.vaadin.components;

import java.util.Optional;
import java.util.stream.Stream;

import com.holonplatform.core.Validator;
import com.holonplatform.core.Validator.ValidationException;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertyRenderer;
import com.holonplatform.core.property.PropertyRendererRegistry;
import com.holonplatform.vaadin.components.Input.InputPropertyRenderer;
import com.holonplatform.vaadin.components.PropertyBinding.PostProcessor;
import com.holonplatform.vaadin.internal.components.DefaultPropertyInputGroup;
import com.holonplatform.vaadin.internal.components.VaadinValidatorWrapper;
import com.vaadin.data.HasValue;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;

/**
 * A class to manage a group of {@link Input}s bound to a {@link Property} set, loading and obtaining property values in
 * and from {@link Input}s using the {@link PropertyBox} data container type.
 * <p>
 * Supports overall {@link Validator}s registration to validate all the {@link Input} values, allowing cross input
 * validation, using a {@link PropertyBox} to represent the inputs value set.
 * </p>
 * <p>
 * By default, property {@link Input} components are obtained from the {@link PropertyRenderer}s registered in the
 * context {@link PropertyRendererRegistry}, if available. Custom {@link PropertyRenderer} registration is supported to
 * provide a custom behaviour for specific properties.
 * </p>
 * <p>
 * Default property values are supported using a {@link DefaultValueProvider}. The property default values are loaded
 * when {@link #clear()} or {@link #setValue(PropertyBox)} methods are invoked.
 * </p>
 * <p>
 * Convenience methods {@link #setEnabled(boolean)} and {@link #setReadOnly(boolean)} can be used to change the enabled
 * / read-only state for all the property bound {@link Input}s.
 * </p>
 * 
 * @since 5.0.0
 */
public interface PropertyInputGroup extends PropertySetBound, ValueHolder<PropertyBox>, Validatable {

	/**
	 * Gets all the {@link Input}s that have been bound to a property.
	 * @return An {@link Iterable} on all bound {@link Input}s
	 */
	Iterable<Input<?>> getInputs();

	/**
	 * Get the {@link Input} bound to given <code>property</code>, if any.
	 * @param <T> Property type
	 * @param property Property for which to get the associated {@link Input} (not null)
	 * @return Optional {@link Input} bound to given <code>property</code>
	 */
	<T> Optional<Input<T>> getInput(Property<T> property);

	/**
	 * Return a {@link Stream} of the properties and their bound {@link Input}s of this input group.
	 * @param <T> Property type
	 * @return Property-Input {@link PropertyBinding} stream
	 */
	<T> Stream<PropertyBinding<T, Input<T>>> stream();

	/**
	 * Get the current property values collected into a {@link PropertyBox}, using the group configured properties as
	 * property set.
	 * <p>
	 * For each property with a bound {@link Input} component, the property value is obtained from the {@link Input}
	 * component through the {@link Input#getValue()} method.
	 * </p>
	 * @param validate <code>true</code> to check the validity of the property bound {@link Input}s and of this
	 *        {@link PropertyInputGroup} before returing the value, throwing a {@link ValidationException} if the
	 *        validation is not successful.
	 * @return A {@link PropertyBox} containing the property values (never null)
	 * @throws ValidationException If <code>validate</code> is <code>true</code> and an {@link Input} value is not valid
	 * @throws OverallValidationException If the overall validation failed
	 */
	PropertyBox getValue(boolean validate);

	/**
	 * Get the current property values collected into a {@link PropertyBox}, using the group configured properties as
	 * property set.
	 * <p>
	 * For each property with a bound {@link Input} component, the property value is obtained from the {@link Input}
	 * component through the {@link Input#getValue()} method.
	 * </p>
	 * <p>
	 * The available {@link Input}s and the overall group validation is performed before returning the value, throwing a
	 * {@link ValidationException} if the validation is not successful.
	 * </p>
	 * @return A {@link PropertyBox} containing the property values (never null)
	 * @throws ValidationException If one or more input value is not valid, providing the validation error messages
	 * @throws OverallValidationException If the overall validation failed
	 * @see #getValue(boolean)
	 * @see #getValueIfValid()
	 */
	@Override
	default PropertyBox getValue() {
		return getValue(true);
	}

	/**
	 * Get the current property values collected into a {@link PropertyBox}, using the group configured properties as
	 * property set, only if the property bound {@link Input}s and this {@link PropertyInputGroup} are valid
	 * <p>
	 * For each property with a bound {@link Input} component, the property value is obtained from the {@link Input}
	 * component through the {@link Input#getValue()} method.
	 * </p>
	 * @return A {@link PropertyBox} containing the property values, or an empty Optional if validation failed
	 */
	Optional<PropertyBox> getValueIfValid();

	/**
	 * Set the current property values using a {@link PropertyBox}, loading the values to the available property bound
	 * {@link Input}s through the {@link Input#setValue(Object)} method.
	 * <p>
	 * Only the properties which belong to the group's property set are taken into account.
	 * </p>
	 * @param propertyBox the {@link PropertyBox} which contains the property values to load. If <code>null</code>, all
	 *        the {@link Input} components are cleared.
	 * @param validate <code>true</code> to check the validity of the property bound {@link Input}s and of this
	 *        {@link PropertyInputGroup}, throwing a {@link ValidationException} if the validation is not successful.
	 * @throws ValidationException If <code>validate</code> is <code>true</code> and an {@link Input} value is not valid
	 * @throws OverallValidationException If overall validation failed
	 */
	void setValue(PropertyBox propertyBox, boolean validate);

	/**
	 * Set the current property values using a {@link PropertyBox}, loading the values to the available property bound
	 * {@link Input}s through the {@link Input#setValue(Object)} method.
	 * <p>
	 * Only the properties which belong to the group's property set are taken into account.
	 * </p>
	 * <p>
	 * By default, no value validation is performed using this method.
	 * </p>
	 * @param propertyBox the {@link PropertyBox} which contains the property values to load. If <code>null</code>, all
	 *        the {@link Input} components are cleared.
	 * @see #setValue(PropertyBox, boolean)
	 */
	@Override
	default void setValue(PropertyBox propertyBox) {
		setValue(propertyBox, false);
	}

	/**
	 * Set the read-only mode for all the group inputs.
	 * @param readOnly <code>true</code> to set all inputs as read-only, <code>false</code> to unset
	 */
	void setReadOnly(boolean readOnly);

	/**
	 * Updates the enabled state of all the group inputs.
	 * @param enabled <code>true</code> to enable all group inputs, <code>false</code> to disable them
	 */
	void setEnabled(boolean enabled);

	/**
	 * Get a {@link Builder} to create and setup a {@link PropertyInputGroup}.
	 * @return {@link PropertyInputGroup} builder
	 */
	static PropertyInputGroupBuilder builder() {
		return new DefaultPropertyInputGroup.DefaultBuilder();
	}

	// -------

	/**
	 * Interface to provide the default value for a {@link Property}.
	 * @param <T> Property type
	 */
	@FunctionalInterface
	public interface DefaultValueProvider<T> {

		/**
		 * Get the property default value
		 * @param property Property (never null)
		 * @return Default value
		 */
		T getDefaultValue(Property<T> property);

	}

	// Builder

	/**
	 * {@link PropertyInputGroup} builder.
	 */
	public interface PropertyInputGroupBuilder extends Builder<PropertyInputGroup, PropertyInputGroupBuilder> {

	}

	/**
	 * Base {@link PropertyInputGroup} builder.
	 * @param <G> Actual {@link PropertyInputGroup} type
	 * @param <B> Concrete builder type
	 */
	public interface Builder<G extends PropertyInputGroup, B extends Builder<G, B>> {

		/**
		 * Add given properties to the {@link PropertyInputGroup} property set.
		 * @param <P> Property type
		 * @param properties Properties to add
		 * @return this
		 */
		@SuppressWarnings({ "rawtypes", "unchecked" })
		<P extends Property> B properties(P... properties);

		/**
		 * Add given properties to the {@link PropertyInputGroup} property set.
		 * @param <P> Property type
		 * @param properties Properties to add (not null)
		 * @return this
		 */
		@SuppressWarnings("rawtypes")
		<P extends Property> B properties(Iterable<P> properties);

		/**
		 * Set the given property as read-only. If a property is read-only, the {@link Input} bound to the property will
		 * be setted as read-only too, and its value will never be written to a {@link PropertyBox} nor validated.
		 * @param <T> Property type
		 * @param property Property to set as read-only (not null)
		 * @return this
		 */
		<T> B readOnly(Property<T> property);

		/**
		 * Set the given property as required. If a property is required, the {@link Input} bound to the property will
		 * be setted as required, and its validation will fail when empty.
		 * @param <T> Property type
		 * @param property Property to set as required (not null)
		 * @return this
		 */
		<T> B required(Property<T> property);

		/**
		 * Set the given property as required, using given {@link Validator} to check the property value. If a property
		 * is required, the {@link Input} bound to the property will be setted as required, and its validation will fail
		 * when empty.
		 * @param <T> Property type
		 * @param property Property to set as required (not null)
		 * @param validator The {@link Validator} to use to check the required property value (not null)
		 * @return this
		 */
		<T> B required(Property<T> property, Validator<T> validator);

		/**
		 * Set the given property as required. If a property is required, the {@link Input} bound to the property will
		 * be setted as required, and its validation will fail when empty.
		 * @param <T> Property type
		 * @param property Property to set as required (not null)
		 * @param message The message to use to notify the required validation failure
		 * @return this
		 */
		<T> B required(Property<T> property, Localizable message);

		/**
		 * Set the given property as required. If a property is required, the {@link Input} bound to the property will
		 * be setted as required, and its validation will fail when empty.
		 * @param <T> Property type
		 * @param property Property to set as required (not null)
		 * @param message The default message to use to notify the required validation failure
		 * @param messageCode The message localization code
		 * @param arguments Optional message translation arguments
		 * @return this
		 */
		default <T> B required(Property<T> property, String message, String messageCode, Object... arguments) {
			return required(property, Localizable.builder().message(message).messageCode(messageCode)
					.messageArguments(arguments).build());
		}

		/**
		 * Set the given property as required. If a property is required, the {@link Input} bound to the property will
		 * be setted as required, and its validation will fail when empty.
		 * @param <T> Property type
		 * @param property Property to set as required (not null)
		 * @param message The default message to use to notify the required validation failure
		 * @return this
		 */
		default <T> B required(Property<T> property, String message) {
			return required(property, Localizable.builder().message(message).build());
		}

		/**
		 * Set the given property as hidden. If a property is hidden, the {@link Input} bound to the property will never
		 * be generated, but its value will be written to a {@link PropertyBox} using
		 * {@link PropertyInputGroup#getValue()}.
		 * @param <T> Property type
		 * @param property Property to set as hidden (not null)
		 * @return this
		 */
		<T> B hidden(Property<T> property);

		/**
		 * Set the default value provider for given <code>property</code>.
		 * @param <T> Property type
		 * @param property Property (not null)
		 * @param defaultValueProvider DefaultValueProvider (not null)
		 * @return this
		 */
		<T> B defaultValue(Property<T> property, DefaultValueProvider<T> defaultValueProvider);

		/**
		 * Adds a {@link Validator} to the {@link Input} bound to given <code>property</code>.
		 * @param <T> Property type
		 * @param property Property (not null)
		 * @param validator Validator to add (not null)
		 * @return this
		 */
		<T> B withValidator(Property<T> property, Validator<T> validator);

		/**
		 * Adds a {@link com.vaadin.data.Validator} to the {@link Input} bound to given <code>property</code>.
		 * @param <T> Property type
		 * @param property Property (not null)
		 * @param validator Validator to add (not null)
		 * @return this
		 */
		default <T> B withValidator(Property<T> property, com.vaadin.data.Validator<T> validator) {
			return withValidator(property, new VaadinValidatorWrapper<>(validator, null, null));
		}

		/**
		 * Adds a {@link Validator} to the {@link PropertyInputGroup}, using a {@link PropertyBox} to provide the
		 * property values to validate.
		 * @param validator Validator to add (not null)
		 * @return this
		 */
		B withValidator(Validator<PropertyBox> validator);

		/**
		 * Adds a {@link com.vaadin.data.Validator} to the {@link PropertyInputGroup}.
		 * @param validator Validator to add (not null)
		 * @return this
		 */
		default B withValidator(com.vaadin.data.Validator<PropertyBox> validator) {
			return withValidator(new VaadinValidatorWrapper<>(validator, null, null));
		}

		/**
		 * Set the {@link ValidationStatusHandler} to use to track given <code>property</code> validation status
		 * changes.
		 * @param <T> Property type
		 * @param property Property for which to set the validation status handler
		 * @param validationStatusHandler the {@link ValidationStatusHandler} to associate to given
		 *        <code>property</code> (not null)
		 * @return this
		 */
		<T> B validationStatusHandler(Property<T> property, ValidationStatusHandler validationStatusHandler);

		/**
		 * Set the {@link Label} to use to track given <code>property</code> validation status changes.
		 * @param <T> Property type
		 * @param property Property for which to set the validation status label
		 * @param statusLabel the status {@link Label} to use to track given <code>property</code> validation status
		 *        (not null)
		 * @return this
		 */
		default <T> B validationStatusHandler(Property<T> property, Label statusLabel) {
			return validationStatusHandler(property, ValidationStatusHandler.label(statusLabel));
		}

		/**
		 * Set the {@link ValidationStatusHandler} to use to track all the properties validation status changes.
		 * <p>
		 * A specific {@link ValidationStatusHandler} for each property can be configured using
		 * {@link #validationStatusHandler(Property, ValidationStatusHandler)}.
		 * </p>
		 * @param validationStatusHandler the {@link ValidationStatusHandler} to set
		 * @return this
		 */
		B propertiesValidationStatusHandler(ValidationStatusHandler validationStatusHandler);

		/**
		 * Set the {@link ValidationStatusHandler} to use to track overall validation status changes.
		 * @param validationStatusHandler the {@link ValidationStatusHandler} to set (not null)
		 * @return this
		 */
		B validationStatusHandler(ValidationStatusHandler validationStatusHandler);

		/**
		 * Set the {@link Label} to use as status label to track overall validation status changes.
		 * @param statusLabel the status {@link Label} to set (not null)
		 * @return this
		 */
		default B validationStatusHandler(Label statusLabel) {
			return validationStatusHandler(ValidationStatusHandler.label(statusLabel));
		}

		/**
		 * Sets whether to validate the available {@link Input}s value every time the {@link Input} value changes.
		 * <p>
		 * Default is <code>true</code>.
		 * </p>
		 * @param validateOnValueChange <code>true</code> to perform value validation every time a {@link Input} value
		 *        changes, <code>false</code> if not
		 * @return this
		 */
		B validateOnValueChange(boolean validateOnValueChange);

		/**
		 * Set whether to stop validation at first validation failure. If <code>true</code>, only the first
		 * {@link ValidationException} is thrown at validation, otherwise a {@link ValidationException} containing all
		 * the occurred validation exception is thrown.
		 * @param stopValidationAtFirstFailure <code>true</code> to stop validation at first validation failure
		 * @return this
		 */
		B stopValidationAtFirstFailure(boolean stopValidationAtFirstFailure);

		/**
		 * Set whether to stop overall validation at first validation failure. If <code>true</code>, only the first
		 * {@link OverallValidationException} is thrown at validation, otherwise a {@link OverallValidationException}
		 * containing all the occurred validation exception is thrown.
		 * <p>
		 * The overall validation is the one which is performed using validators added with
		 * {@link #withValidator(Validator)} method.
		 * </p>
		 * @param stopOverallValidationAtFirstFailure <code>true</code> to stop overall validation at first validation
		 *        failure
		 * @return this
		 */
		B stopOverallValidationAtFirstFailure(boolean stopOverallValidationAtFirstFailure);

		/**
		 * Set to ignore any {@link Property} registered {@link Validator} when binding the property to an {@link Input}
		 * component, i.e. to not inherit property {@link Validator}s when the property-input binding is performed.
		 * @return this
		 */
		B ignorePropertyValidation();

		/**
		 * Set the specific {@link PropertyRenderer} to use to render the {@link Input} to bind to given
		 * <code>property</code>.
		 * @param <T> Property type
		 * @param property Property (not null)
		 * @param renderer Property renderer (not null)
		 * @return this
		 */
		<T> B bind(Property<T> property, PropertyRenderer<Input<T>, T> renderer);

		/**
		 * Convenience method to set a specific {@link PropertyRenderer} to use to render the {@link Input} to bind to
		 * given <code>property</code> using the {@link InputPropertyRenderer} functional interface.
		 * @param <T> Property type
		 * @param property Property (not null)
		 * @param renderer Property renderer (not null)
		 * @return this
		 */
		default <T> B bind(Property<T> property, InputPropertyRenderer<T> renderer) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			ObjectUtils.argumentNotNull(renderer, "Renderer must be not null");
			return bind(property, (PropertyRenderer<Input<T>, T>) renderer);
		}

		/**
		 * Bind the given <code>property</code> to given <code>input</code> instance. If the property was already bound
		 * to a {@link Input}, the old input will be replaced by the new input.
		 * <p>
		 * This method also adds property validators to given {@link Input} when applicable.
		 * </p>
		 * @param <T> Property type
		 * @param property Property (not null)
		 * @param input Input to bind (not null)
		 * @return this
		 */
		default <T> B bind(Property<T> property, Input<T> input) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			ObjectUtils.argumentNotNull(input, "Input must be not null");
			return bind(property, (InputPropertyRenderer<T>) p -> input);
		}

		/**
		 * Bind the given <code>property</code> to given {@link HasValue} component. If the property was already bound
		 * to a {@link Input}, the old input will be replaced by the new input.
		 * @param <T> Property type
		 * @param property Property (not null)
		 * @param field HasValue component to bind (not null)
		 * @return this
		 */
		default <T, F extends HasValue<T> & Component> B bind(Property<T> property, F field) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			ObjectUtils.argumentNotNull(field, "Field must be not null");
			return bind(property, Input.from(field));
		}

		/**
		 * Set whether to ignore properties without a bound {@link Input}. Default is <code>false</code>, and an
		 * exception is thrown if a property of the {@link PropertyInputGroup} cannot be bound to any input component.
		 * @param ignoreMissingInputs Whether to ignore when the {@link Input} for a property is missing
		 * @return this
		 */
		B ignoreMissingInputs(boolean ignoreMissingInputs);

		/**
		 * Add a {@link PostProcessor} to allow further {@link Input} configuration before the input is actually bound
		 * to a property.
		 * @param postProcessor the {@link PostProcessor} to add (not null)
		 * @return this
		 */
		B withPostProcessor(PostProcessor<Input<?>> postProcessor);

		/**
		 * Add a {@link ValueChangeListener} to be notified when the input group value changes.
		 * @param listener The ValueChangeListener to add (not null)
		 * @return this
		 */
		B withValueChangeListener(ValueChangeListener<PropertyBox> listener);

		/**
		 * Build the {@link PropertyInputGroup}.
		 * @return a new {@link PropertyInputGroup} instance
		 */
		G build();

	}

	/**
	 * {@link ValidationException} extension to discern inputs validation and overall container validation exceptions.
	 */
	@SuppressWarnings("serial")
	public class OverallValidationException extends ValidationException {

		/**
		 * {@inheritDoc}
		 */
		public OverallValidationException(String message) {
			super(message);
		}

		/**
		 * {@inheritDoc}
		 */
		public OverallValidationException(String message, String messageCode, Object... messageArguments) {
			super(message, messageCode, messageArguments);
		}

		/**
		 * {@inheritDoc}
		 */
		public OverallValidationException(ValidationException... causes) {
			super(causes);
		}

	}

}
