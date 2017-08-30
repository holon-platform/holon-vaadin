/*
 * Copyright 2000-2016 Holon TDCN.
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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.holonplatform.core.Validator;
import com.holonplatform.core.Validator.ValidationException;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertyRenderer;
import com.holonplatform.core.property.PropertyRendererRegistry.NoSuitableRendererAvailableException;
import com.holonplatform.core.property.VirtualProperty;
import com.holonplatform.vaadin.components.Input;
import com.holonplatform.vaadin.components.PropertyBinding;
import com.holonplatform.vaadin.components.PropertyBinding.PostProcessor;
import com.holonplatform.vaadin.components.PropertyInputGroup;
import com.holonplatform.vaadin.components.PropertyValueComponentSource;
import com.holonplatform.vaadin.components.ValidationStatusHandler;
import com.holonplatform.vaadin.components.ValidationStatusHandler.Status;
import com.holonplatform.vaadin.components.ValueComponent;
import com.vaadin.shared.Registration;

/**
 * Default {@link PropertyInputGroup} implementation.
 *
 * @since 5.0.0
 */
public class DefaultPropertyInputGroup implements PropertyInputGroup, PropertyValueComponentSource {

	private static final long serialVersionUID = -5441417959315472240L;

	/**
	 * Current value
	 */
	private PropertyBox value;

	/**
	 * Property set
	 */
	@SuppressWarnings("rawtypes")
	private final List<Property> propertySet = new LinkedList<>();

	/**
	 * Property configurations
	 */
	@SuppressWarnings("rawtypes")
	private final Map<Property, PropertyConfiguration> properties = new LinkedHashMap<>();

	/**
	 * Value change listeners
	 */
	private final List<ValueChangeListener<PropertyBox>> valueChangeListeners = new LinkedList<>();

	/**
	 * Validators
	 */
	private final List<Validator<PropertyBox>> validators = new LinkedList<>();

	/**
	 * Input post-processors
	 */
	private final List<PostProcessor<Input<?>>> postProcessors = new LinkedList<>();

	/**
	 * Overall validation status handler
	 */
	private ValidationStatusHandler validationStatusHandler = ValidationStatusHandler.notification();

	/**
	 * Validation status handler for all the properties
	 */
	private ValidationStatusHandler propertiesValidationStatusHandler = null;

	/**
	 * Whether to validate inputs at value change
	 */
	private boolean validateOnValueChange = true;

	/**
	 * Validation behaviour
	 */
	private boolean stopValidationAtFirstFailure = false;

	/**
	 * Overall validation behaviour
	 */
	private boolean stopOverallValidationAtFirstFailure = false;

	/**
	 * Ignore validation
	 */
	private boolean ignorePropertyValidation = false;

	/**
	 * Whether to ignore missing inputs
	 */
	private boolean ignoreMissingInputs = false;

	/**
	 * External {@link ValueComponent} supplier
	 */
	private Supplier<ValueComponent<PropertyBox>> valueComponentSupplier;

	/**
	 * Constructor
	 */
	public DefaultPropertyInputGroup() {
		super();
	}

	/**
	 * Get the external {@link ValueComponent} supplier.
	 * @return Optional external {@link ValueComponent} supplier
	 */
	protected Optional<ValueComponent<PropertyBox>> getOverallValueComponent() {
		return Optional.ofNullable(valueComponentSupplier != null ? valueComponentSupplier.get() : null);
	}

	/**
	 * Set the external {@link ValueComponent} supplier.
	 * @param valueComponentSupplier the {@link ValueComponent} supplier to set
	 */
	void setValueComponentSupplier(Supplier<ValueComponent<PropertyBox>> valueComponentSupplier) {
		this.valueComponentSupplier = valueComponentSupplier;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertySetBound#getProperties()
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Iterable<Property> getProperties() {
		return Collections.unmodifiableList(propertySet);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertySetBound#hasProperty(com.holonplatform.core.property.Property)
	 */
	@Override
	public boolean hasProperty(Property<?> property) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		return propertySet.contains(property);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertySetBound#propertyStream()
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Stream<Property> propertyStream() {
		return propertySet.stream();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyInputContainer#getInputs()
	 */
	@Override
	public Iterable<Input<?>> getInputs() {
		return propertySet.stream().filter(p -> _propertyConfiguration(p).getInput().isPresent())
				.map(p -> _propertyConfiguration(p).getInput().get()).collect(Collectors.toList());
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.PropertyInputContainer#getInput(com.holonplatform.core.property.Property)
	 */
	@Override
	public <T> Optional<Input<T>> getInput(Property<T> property) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		if (propertySet.contains(property)) {
			return getPropertyConfiguration(property).getInput();
		}
		return Optional.empty();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyInputContainer#stream()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> Stream<PropertyBinding<T, Input<T>>> stream() {
		return propertySet.stream().filter(p -> _propertyConfiguration(p).getInput().isPresent())
				.map(p -> PropertyBinding.create(p, _propertyConfiguration(p).getInput().get()));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyValueComponentSource#getValueComponents()
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Iterable<ValueComponent> getValueComponents() {
		return propertySet.stream().filter(p -> _propertyConfiguration(p).getInput().isPresent())
				.map(p -> _propertyConfiguration(p).getInput().get()).collect(Collectors.toList());
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyValueComponentSource#getValueComponent(com.holonplatform.core.
	 * property.Property)
	 */
	@Override
	public <T> Optional<ValueComponent<T>> getValueComponent(Property<T> property) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		if (propertySet.contains(property)) {
			return getPropertyConfiguration(property).getInput().map(i -> i);
		}
		return Optional.empty();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyValueComponentSource#streamOfValueComponents()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Stream<PropertyBinding<?, ValueComponent<?>>> streamOfValueComponents() {
		return propertySet.stream().filter(p -> _propertyConfiguration(p).getInput().isPresent())
				.map(p -> PropertyBinding.create(p, _propertyConfiguration(p).getInput().get()));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.InputGroup#clear()
	 */
	@Override
	public void clear() {
		setValue(null, false);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Validatable#validate()
	 */
	@Override
	public void validate() throws ValidationException {
		// validate inputs
		validateInputs();
		// validate value
		validate(getValue(false));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyInputGroup#getValue(boolean)
	 */
	@Override
	public PropertyBox getValue(boolean validate) {
		PropertyBox value = PropertyBox.builder(propertySet).build();
		flush(value, validate);
		return value;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyInputGroup#getValueIfValid()
	 */
	@Override
	public Optional<PropertyBox> getValueIfValid() {
		try {
			return Optional.of(getValue(true));
		} catch (@SuppressWarnings("unused") ValidationException e) {
			return Optional.empty();
		}
	}

	/**
	 * Writes the property bound {@link Input} components values, obtained through the {@link Input#getValue()} method,
	 * to given <code>propertyBox</code>,
	 * @param propertyBox the {@link PropertyBox} into which to write the property values (not null)
	 * @param validate whether to perform inputs and overall validation
	 */
	@SuppressWarnings("unchecked")
	private void flush(PropertyBox propertyBox, boolean validate) {
		ObjectUtils.argumentNotNull(propertyBox, "PropertyBox must be not null");

		if (validate) {
			// inputs validation
			validateInputs();
		}

		propertySet.forEach(p -> {
			final PropertyConfiguration<?> cfg = getPropertyConfiguration(p);
			if (cfg.isHidden()) {
				getCurrentPropertyValue(p).ifPresent(v -> {
					propertyBox.setValue(p, v);
				});
			} else {
				cfg.getInput().ifPresent(i -> {
					propertyBox.setValue(p, i.getValue());
				});
			}
		});

		if (validate) {
			// Overall validation
			validate(propertyBox);
		}
	}

	/**
	 * Reset all the {@link Input}s values.
	 * @param setDefaultValue Whether to set the default value when available
	 */
	@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
	protected void resetValues(boolean setDefaultValue) {
		propertySet.forEach(p -> {
			final PropertyConfiguration<?> cfg = _propertyConfiguration(p);
			cfg.getInput().ifPresent(i -> {
				try {
					// clear input
					boolean ro = i.isReadOnly();
					if (ro)
						i.setReadOnly(false);
					i.clear();
					if (ro)
						i.setReadOnly(true);

					// check default value
					if (setDefaultValue && !cfg.isReadOnly()) {
						cfg.getDefaultValueProvider().ifPresent(dvp -> ((Input) i).setValue(dvp.getDefaultValue(p)));
					}
				} catch (ValidationException ve) {
					// ignore any validation error
				}

				// reset validation status
				resetValidationStatus(i, p);
			});
		});

		// reset overall validation status
		resetValidationStatus(getOverallValueComponent().orElse(null), null);

	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyInputGroup#setValue(com.holonplatform.core.property.PropertyBox,
	 * boolean)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void setValue(final PropertyBox propertyBox, boolean validate) {
		final PropertyBox oldValue = this.value;
		this.value = propertyBox;

		// reset
		resetValues(propertyBox == null);

		// load
		if (propertyBox != null) {
			propertySet.forEach(p -> {
				final PropertyConfiguration<Object> cfg = getPropertyConfiguration(p);
				cfg.getInput().ifPresent(i -> {
					Object value = getPropertyValue(propertyBox, p);
					if (value != null) {
						// ignore read-only
						boolean ro = i.isReadOnly();
						if (ro)
							i.setReadOnly(false);
						i.setValue(value);
						if (ro)
							i.setReadOnly(true);
					} else {
						i.clear();
					}
				});
			});
		}

		// check validation
		if (validate) {
			validate();
		}

		// fire value change
		fireValueChange(oldValue, propertyBox);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValueHolder#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return getValue(false).propertyValues().filter(v -> v.hasValue()).findAny().isPresent();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValueHolder#addValueChangeListener(com.holonplatform.vaadin.components.
	 * ValueHolder.ValueChangeListener)
	 */
	@Override
	public Registration addValueChangeListener(ValueChangeListener<PropertyBox> listener) {
		ObjectUtils.argumentNotNull(listener, "ValueChangeListener must be not null");
		valueChangeListeners.add(listener);
		return () -> valueChangeListeners.remove(listener);
	}

	/**
	 * Emits the value change event
	 * @param oldValue Old value
	 * @param value the changed value
	 */
	protected void fireValueChange(PropertyBox oldValue, PropertyBox value) {
		final ValueChangeEvent<PropertyBox> valueChangeEvent = new DefaultValueChangeEvent<>(this, oldValue, value);
		valueChangeListeners.forEach(l -> l.valueChange(valueChangeEvent));
	}

	/**
	 * Get whether to validate inputs at value change.
	 * @return <code>true</code> to validate inputs at value change
	 */
	protected boolean isValidateOnValueChange() {
		return validateOnValueChange;
	}

	/**
	 * Set whether to validate inputs at value change.
	 * @param validateOnValueChange <code>true</code> to validate inputs at value change
	 */
	public void setValidateOnValueChange(boolean validateOnValueChange) {
		this.validateOnValueChange = validateOnValueChange;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyInputGroup#setEnabled(boolean)
	 */
	@Override
	public void setEnabled(boolean enabled) {
		getInputs().forEach(i -> i.getComponent().setEnabled(enabled));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyInputGroup#setReadOnly(boolean)
	 */
	@Override
	public void setReadOnly(boolean readOnly) {
		propertySet.stream().filter(p -> !_propertyConfiguration(p).isReadOnly()).forEach(p -> {
			_propertyConfiguration(p).getInput().ifPresent(i -> {
				i.setReadOnly(readOnly);
			});
		});
	}

	/**
	 * Get the property configuration bound to given <code>property</code>.
	 * @param property Property for which to obtain the configuration
	 * @return The {@link PropertyConfiguration}
	 */
	protected PropertyConfiguration<?> _propertyConfiguration(Property<?> property) {
		return getPropertyConfiguration(property);
	}

	/**
	 * Get the property configuration bound to given <code>property</code>.
	 * @param property Property for which to obtain the configuration
	 * @param <T> Property type
	 * @return The {@link PropertyConfiguration}
	 */
	@SuppressWarnings("unchecked")
	protected <T> PropertyConfiguration<T> getPropertyConfiguration(Property<T> property) {
		properties.putIfAbsent(property, new PropertyConfiguration<>(property));
		return properties.get(property);
	}

	/**
	 * Add a property to the property set
	 * @param property Property to add (not null)
	 */
	public void addProperty(Property<?> property) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		if (!propertySet.contains(property)) {
			propertySet.add(property);
		}
	}

	/**
	 * Add an overall validator
	 * @param validator the {@link Validator} to add (not null)
	 */
	public void addValidator(Validator<PropertyBox> validator) {
		ObjectUtils.argumentNotNull(validator, "Validator must be not null");
		validators.add(validator);
	}

	/**
	 * Get the overall {@link Validator}s.
	 * @return the overall validators
	 */
	protected List<Validator<PropertyBox>> getValidators() {
		return validators;
	}

	/**
	 * Set the overall {@link ValidationStatusHandler}.
	 * @param validationStatusHandler the {@link ValidationStatusHandler} to set
	 */
	public void setValidationStatusHandler(ValidationStatusHandler validationStatusHandler) {
		this.validationStatusHandler = validationStatusHandler;
	}

	/**
	 * Get the overall {@link ValidationStatusHandler}, if available.
	 * @return the optional overall {@link ValidationStatusHandler}
	 */
	protected Optional<ValidationStatusHandler> getValidationStatusHandler() {
		return Optional.ofNullable(validationStatusHandler);
	}

	/**
	 * Set the {@link ValidationStatusHandler} to use for all the properties.
	 * @param propertiesValidationStatusHandler the {@link ValidationStatusHandler} to set
	 */
	public void setPropertiesValidationStatusHandler(ValidationStatusHandler propertiesValidationStatusHandler) {
		this.propertiesValidationStatusHandler = propertiesValidationStatusHandler;
	}

	/**
	 * Get the {@link ValidationStatusHandler} to use for all the properties.
	 * @return properties {@link ValidationStatusHandler}
	 */
	protected Optional<ValidationStatusHandler> getPropertiesValidationStatusHandler() {
		return Optional.ofNullable(propertiesValidationStatusHandler);
	}

	/**
	 * Get whether to stop validation at first validation failure.
	 * @return whether to stop validation at first validation failure
	 */
	protected boolean isStopValidationAtFirstFailure() {
		return stopValidationAtFirstFailure;
	}

	/**
	 * Set whether to stop validation at first validation failure.
	 * @param stopValidationAtFirstFailure <code>true</code> to stop validation at first validation failure
	 */
	public void setStopValidationAtFirstFailure(boolean stopValidationAtFirstFailure) {
		this.stopValidationAtFirstFailure = stopValidationAtFirstFailure;
	}

	/**
	 * Get whether to stop overall validation at first validation failure.
	 * @return whether to stop overall validation at first validation failure
	 */
	protected boolean isStopOverallValidationAtFirstFailure() {
		return stopOverallValidationAtFirstFailure;
	}

	/**
	 * Set whether to stop overall validation at first validation failure.
	 * @param stopOverallValidationAtFirstFailure <code>true</code> to stop overall validation at first validation
	 *        failure
	 */
	public void setStopOverallValidationAtFirstFailure(boolean stopOverallValidationAtFirstFailure) {
		this.stopOverallValidationAtFirstFailure = stopOverallValidationAtFirstFailure;
	}

	/**
	 * Get whether to ignore {@link Property} validators.
	 * @return <code>true</code> if {@link Property} validators must be ignored
	 */
	protected boolean isIgnorePropertyValidation() {
		return ignorePropertyValidation;
	}

	/**
	 * Set whether to ignore {@link Property} validators.
	 * @param ignorePropertyValidation <code>true</code> to ignore {@link Property} validators
	 */
	public void setIgnorePropertyValidation(boolean ignorePropertyValidation) {
		this.ignorePropertyValidation = ignorePropertyValidation;
	}

	/**
	 * Whether to ignore missing property {@link Input}s.
	 * @return <code>true</code> if missing property inputs must be ignored
	 */
	protected boolean isIgnoreMissingInputs() {
		return ignoreMissingInputs;
	}

	/**
	 * Set whether to ignore missing property inputs
	 * @param ignoreMissingInputs <code>true</code> to ignore missing property inputs
	 */
	public void setIgnoreMissingInputs(boolean ignoreMissingInputs) {
		this.ignoreMissingInputs = ignoreMissingInputs;
	}

	/**
	 * Add an {@link Input} {@link PostProcessor}.
	 * @param postProcessor the post-processor to add
	 */
	public void addInputPostProcessor(PostProcessor<Input<?>> postProcessor) {
		ObjectUtils.argumentNotNull(postProcessor, "InputPostProcessor must be not null");
		postProcessors.add(postProcessor);
	}

	/**
	 * Get the registered {@link Input} post processors.
	 * @return the {@link Input} post processors
	 */
	protected List<PostProcessor<Input<?>>> getPostProcessors() {
		return postProcessors;
	}

	/**
	 * Build and bind {@link Input}s to the properties of the property set.
	 */
	public void build() {
		// render and bind inputs
		propertySet.forEach(p -> {
			// exclude hidden properties
			PropertyConfiguration<?> configuration = _propertyConfiguration(p);
			if (!configuration.isHidden()) {
				renderAndBind(configuration);
			}
		});
	}

	/**
	 * Render the {@link Input} and set it up in given property configuration.
	 * @param <T> Property type
	 * @param configuration Property configuration
	 */
	private <T> void renderAndBind(PropertyConfiguration<T> configuration) {
		final Optional<Input<T>> input = render(configuration.getProperty());
		if (!input.isPresent() && !isIgnoreMissingInputs()) {
			throw new NoSuitableRendererAvailableException(
					"No Input renderer available to render the property [" + configuration.getProperty() + "]");
		}
		input.ifPresent(i -> {
			// configure
			configureInput(configuration, i);
			// bind
			configuration.setInput(i);
		});
	}

	/**
	 * Render given property as a {@link Input}.
	 * @param <T> Property type
	 * @param property Property to render
	 * @return Rendered input
	 */
	@SuppressWarnings("unchecked")
	protected <T> Optional<Input<T>> render(Property<T> property) {
		// check custom renderer
		final PropertyConfiguration<T> cfg = getPropertyConfiguration(property);
		if (cfg.getRenderer().isPresent()) {
			final PropertyRenderer<Input<T>, T> r = cfg.getRenderer().get();
			// check render type
			if (!Input.class.isAssignableFrom(r.getRenderType())) {
				throw new IllegalStateException(
						"Renderer for property [" + property + "] is not of Input type: [" + r.getRenderType() + "]");
			}
			return Optional.ofNullable(r.render(property));
		}
		// use registry
		return property.renderIfAvailable(Input.class).map(i -> i);
	}

	/**
	 * Configure {@link Input} component before binding it to a {@link Property}.
	 * @param <T> Property type
	 * @param configuration Property configuration (not null)
	 * @param input {@link Input} component to configure
	 */
	protected <T> void configureInput(final PropertyConfiguration<T> configuration, final Input<T> input) {
		// Read-only
		if (configuration.isReadOnly()) {
			input.setReadOnly(true);
		}
		// Required
		if (configuration.isRequired()) {
			input.setRequired(true);
			// add required validator
			configuration
					.addValidatorAsFirst(configuration.getRequiredValidator().orElse(new RequiredInputValidator<>(input,
							configuration.getRequiredMessage().orElse(RequiredInputValidator.DEFAULT_REQUIRED_ERROR))));
		}
		// Check validation status handler
		if (!configuration.getPropertyValidationStatusHandler().isPresent()) {
			configuration.setPropertyValidationStatusHandler(
					getPropertiesValidationStatusHandler().orElse(ValidationStatusHandler.getDefault()));
		}
		// Validate on value change
		if (isValidateOnValueChange()) {
			input.addValueChangeListener(e -> validateOnChange(configuration, e.getValue()));
		}
		// post processors
		getPostProcessors().forEach(fc -> fc.process(configuration.getProperty(), input));
	}

	/**
	 * Overall validation
	 * @param value Value to validate
	 * @throws OverallValidationException If validation fails
	 */
	protected void validate(PropertyBox value) throws OverallValidationException {

		LinkedList<ValidationException> failures = new LinkedList<>();
		for (Validator<PropertyBox> validator : getValidators()) {
			try {
				validator.validate(value);
			} catch (ValidationException ve) {
				failures.add(ve);
				if (isStopOverallValidationAtFirstFailure()) {
					break;
				}
			}
		}

		// collect validation exceptions, if any
		if (!failures.isEmpty()) {

			OverallValidationException validationException = (failures.size() == 1)
					? new OverallValidationException(failures.getFirst().getMessage(),
							failures.getFirst().getMessageCode(), failures.getFirst().getMessageArguments())
					: new OverallValidationException(failures.toArray(new ValidationException[failures.size()]));

			// notify validation status
			notifyInvalidValidationStatus(validationException, getOverallValueComponent().orElse(null), null);

			throw validationException;
		}

		// notify validation status
		notifyValidValidationStatus(getOverallValueComponent().orElse(null), null);
	}

	/**
	 * Validate all the {@link Input}s.
	 * @throws ValidationException If one or more input is not valid
	 */
	private void validateInputs() throws ValidationException {

		LinkedList<ValidationException> failures = new LinkedList<>();

		// get all property configurations
		List<PropertyConfiguration<?>> configurations = propertySet.stream().map(p -> _propertyConfiguration(p))
				.filter(cfg -> !cfg.isReadOnly() && cfg.getInput().isPresent()).collect(Collectors.toList());

		if (configurations != null) {

			if (isStopValidationAtFirstFailure()) {
				// reset validation status
				configurations.forEach(c -> resetValidationStatus(c.getInput().get(), c.getProperty()));
			}

			for (PropertyConfiguration<?> configuration : configurations) {
				try {
					validateProperty(configuration);
				} catch (ValidationException e) {
					failures.add(e);

					if (isStopValidationAtFirstFailure()) {
						// break if stop validation at first failure
						break;
					}
				}
			}
		}

		// collect validation exceptions, if any
		if (!failures.isEmpty()) {
			if (failures.size() == 1) {
				throw failures.getFirst();
			} else {
				throw new ValidationException(failures.toArray(new ValidationException[0]));
			}
		}
	}

	/**
	 * Reset the validation status, if a {@link ValidationStatusHandler} is available.
	 * @param source Source component
	 * @param property Validation property, if <code>null</code> resets the overall validation status
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void resetValidationStatus(ValueComponent<?> source, Property<?> property) {
		if (property != null) {
			getPropertyConfiguration(property).getPropertyValidationStatusHandler()
					.ifPresent(vsh -> vsh.validationStatusChange(
							new DefaultValidationStatusEvent(Status.UNRESOLVED, null, source, property)));
		} else {
			getValidationStatusHandler().ifPresent(vsh -> vsh
					.validationStatusChange(new DefaultValidationStatusEvent<>(Status.UNRESOLVED, null, null, null)));
		}
	}

	/**
	 * Notify a valid validation status, if a {@link ValidationStatusHandler} is available.
	 * @param <T> Property type
	 * @param source Source component
	 * @param property Validation property, if <code>null</code> notify the overall validation status
	 */
	protected <T> void notifyValidValidationStatus(ValueComponent<T> source, Property<T> property) {
		if (property != null) {
			getPropertyConfiguration(property).getPropertyValidationStatusHandler().ifPresent(vsh -> vsh
					.validationStatusChange(new DefaultValidationStatusEvent<>(Status.VALID, null, source, property)));
		} else {
			getValidationStatusHandler().ifPresent(vsh -> vsh
					.validationStatusChange(new DefaultValidationStatusEvent<>(Status.VALID, null, null, null)));
		}
	}

	/**
	 * Notify a invalid validation status, if a {@link ValidationStatusHandler} is available.
	 * @param <T> Property type
	 * @param e Validation exception
	 * @param source Source component
	 * @param property Validation property, if <code>null</code> notify the overall validation status
	 */
	protected <T> void notifyInvalidValidationStatus(ValidationException e, ValueComponent<T> source,
			Property<T> property) {
		if (property != null) {
			getPropertyConfiguration(property).getPropertyValidationStatusHandler()
					.ifPresent(vsh -> vsh.validationStatusChange(new DefaultValidationStatusEvent<>(Status.INVALID,
							e.getValidationMessages(), source, property)));
		} else {
			getValidationStatusHandler().ifPresent(vsh -> vsh.validationStatusChange(
					new DefaultValidationStatusEvent<>(Status.INVALID, e.getValidationMessages(), null, null)));
		}
	}

	/**
	 * Perform the validation of the input bound to given property configuration.
	 * @param <T> Property type
	 * @param configuration Property configuration
	 * @throws ValidationException If a validation error occurred
	 */
	private <T> void validateProperty(PropertyConfiguration<T> configuration) throws ValidationException {
		if (configuration.getInput().isPresent()) {
			validateProperty(configuration, configuration.getInput().get().getValue());
		}
	}

	/**
	 * Validate the input bound to given property configuration, if available, swallowing any
	 * {@link ValidationException}.
	 * @param <T> Property type
	 * @param configuration Property configuration
	 * @param value Value to validate
	 */
	private <T> void validateOnChange(final PropertyConfiguration<T> configuration, final T value) {
		try {
			validateProperty(configuration, value);
		} catch (@SuppressWarnings("unused") ValidationException e) {
			// ignore
		}
	}

	/**
	 * Validate the input bound to given property configuration.
	 * @param <T> Property type
	 * @param configuration Property configuration
	 * @param value Value to validate
	 * @throws ValidationException If a validation error occurred
	 */
	private <T> void validateProperty(final PropertyConfiguration<T> configuration, final T value)
			throws ValidationException {
		if (configuration.getInput().isPresent()) {
			// input
			final Input<T> input = configuration.getInput().get();

			final LinkedList<ValidationException> failures = new LinkedList<>();

			try {
				// property validators
				if (!isIgnorePropertyValidation()) {
					configuration.getProperty().getValidators().forEach(v -> {
						v.validate(value);
					});
				}
				// input validators
				configuration.getValidators().forEach(v -> {
					v.validate(value);
				});
			} catch (ValidationException ve) {
				failures.add(ve);
			}

			if (!failures.isEmpty()) {

				ValidationException ve = (failures.size() == 1) ? failures.getFirst()
						: new ValidationException(failures.toArray(new ValidationException[0]));

				// notify status
				notifyInvalidValidationStatus(ve, input, configuration.getProperty());

				throw ve;
			}

			// notify validation status
			notifyValidValidationStatus(input, configuration.getProperty());
		}
	}

	/**
	 * Get the value of given <code>property</code> using given <code>propertyBox</code>.
	 * @param <T> Property type
	 * @param propertyBox PropertyBox
	 * @param property Property
	 * @return Property value
	 */
	protected <T> T getPropertyValue(PropertyBox propertyBox, Property<T> property) {
		if (VirtualProperty.class.isAssignableFrom(property.getClass())) {
			if (((VirtualProperty<T>) property).getValueProvider() != null) {
				return ((VirtualProperty<T>) property).getValueProvider().getPropertyValue(propertyBox);
			}
			return null;
		}
		if (propertyBox.containsValue(property)) {
			return propertyBox.getValue(property);
		}
		return null;
	}

	/**
	 * Get the value of given <code>property</code> using current value, if available.
	 * @param <T> Property type
	 * @param property Property for which to obtain the value
	 * @return Property value, empty if current value is not available or the given property is not available in current
	 *         value
	 */
	private <T> Optional<T> getCurrentPropertyValue(Property<T> property) {
		if (value != null && value.contains(property)) {
			return Optional.ofNullable(value.getValue(property));
		}
		return Optional.empty();
	}

	// Builder

	/**
	 * {@link PropertyInputGroup} builder.
	 */
	static class InternalBuilder
			extends AbstractBuilder<DefaultPropertyInputGroup, DefaultPropertyInputGroup, InternalBuilder> {

		public InternalBuilder() {
			super(new DefaultPropertyInputGroup());
		}

		@Override
		protected InternalBuilder builder() {
			return this;
		}

		@Override
		public DefaultPropertyInputGroup build() {
			instance.build();
			return instance;
		}

	}

	/**
	 * Default {@link PropertyInputGroupBuilder} implementation.
	 */
	public static class DefaultBuilder
			extends AbstractBuilder<DefaultPropertyInputGroup, PropertyInputGroup, PropertyInputGroupBuilder>
			implements PropertyInputGroupBuilder {

		public DefaultBuilder() {
			super(new DefaultPropertyInputGroup());
		}

		@Override
		protected PropertyInputGroupBuilder builder() {
			return this;
		}

		@Override
		public PropertyInputGroup build() {
			instance.build();
			return instance;
		}

	}

	/**
	 * Abstract {@link Builder} implementation.
	 * @param <G> Actual {@link PropertyInputGroup} type
	 * @param <B> Concrete builder type
	 */
	public abstract static class AbstractBuilder<C extends DefaultPropertyInputGroup, G extends PropertyInputGroup, B extends Builder<G, B>>
			implements Builder<G, B> {

		/**
		 * Instance to build
		 */
		protected final C instance;

		/**
		 * Constructor
		 * @param instance Instance to build
		 */
		public AbstractBuilder(C instance) {
			super();
			this.instance = instance;
		}

		/**
		 * Actual builder
		 * @return Builder
		 */
		protected abstract B builder();

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.PropertyInputGroup.Builder#properties(com.holonplatform.core.property.
		 * Property[])
		 */
		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public <P extends Property> B properties(P... properties) {
			if (properties != null) {
				for (P property : properties) {
					instance.addProperty(property);
				}
			}
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyInputGroup.Builder#properties(java.lang.Iterable)
		 */
		@SuppressWarnings("rawtypes")
		@Override
		public <P extends Property> B properties(Iterable<P> properties) {
			ObjectUtils.argumentNotNull(properties, "Properties must be not null");
			for (P property : properties) {
				instance.addProperty(property);
			}
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyInputGroup.Builder#readOnly(com.holonplatform.core.property.
		 * Property)
		 */
		@Override
		public <T> B readOnly(Property<T> property) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			instance.getPropertyConfiguration(property).setReadOnly(true);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyInputGroup.Builder#required(com.holonplatform.core.property.
		 * Property)
		 */
		@Override
		public <T> B required(Property<T> property) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			instance.getPropertyConfiguration(property).setRequired(true);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyInputGroup.Builder#required(com.holonplatform.core.property.
		 * Property, com.holonplatform.core.Validator)
		 */
		@Override
		public <T> B required(Property<T> property, Validator<T> validator) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			instance.getPropertyConfiguration(property).setRequired(true);
			instance.getPropertyConfiguration(property).setRequiredValidator(validator);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyInputGroup.Builder#required(com.holonplatform.core.property.
		 * Property, com.holonplatform.core.i18n.Localizable)
		 */
		@Override
		public <T> B required(Property<T> property, Localizable message) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			instance.getPropertyConfiguration(property).setRequired(true);
			instance.getPropertyConfiguration(property).setRequiredMessage(message);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyInputGroup.Builder#hidden(com.holonplatform.core.property.
		 * Property)
		 */
		@Override
		public <T> B hidden(Property<T> property) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			instance.getPropertyConfiguration(property).setHidden(true);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.PropertyInputGroup.Builder#bind(com.holonplatform.core.property.Property,
		 * com.holonplatform.core.property.PropertyRenderer)
		 */
		@Override
		public <T> B bind(Property<T> property, PropertyRenderer<Input<T>, T> renderer) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			ObjectUtils.argumentNotNull(renderer, "Renderer must be not null");
			instance.getPropertyConfiguration(property).setRenderer(renderer);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.PropertyInputGroup.Builder#defaultValue(com.holonplatform.core.property
		 * .Property, com.holonplatform.vaadin.components.PropertyInputGroup.DefaultValueProvider)
		 */
		@Override
		public <T> B defaultValue(Property<T> property, DefaultValueProvider<T> defaultValueProvider) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			ObjectUtils.argumentNotNull(defaultValueProvider, "DefaultValueProvider must be not null");
			instance.getPropertyConfiguration(property).setDefaultValueProvider(defaultValueProvider);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyInputGroup.Builder#withValidator(com.holonplatform.core.
		 * property.Property, com.holonplatform.core.Validator)
		 */
		@Override
		public <T> B withValidator(Property<T> property, Validator<T> validator) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			ObjectUtils.argumentNotNull(validator, "Validator must be not null");
			instance.getPropertyConfiguration(property).addValidator(validator);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.PropertyInputGroup.Builder#validationStatusHandler(com.holonplatform.core
		 * .property.Property, com.holonplatform.vaadin.components.ValidationStatusHandler)
		 */
		@Override
		public <T> B validationStatusHandler(Property<T> property, ValidationStatusHandler validationStatusHandler) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			ObjectUtils.argumentNotNull(validationStatusHandler, "ValidationStatusHandler must be not null");
			instance.getPropertyConfiguration(property).setPropertyValidationStatusHandler(validationStatusHandler);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyInputGroup.Builder#propertiesValidationStatusHandler(com.
		 * holonplatform.vaadin.components.ValidationStatusHandler)
		 */
		@Override
		public B propertiesValidationStatusHandler(ValidationStatusHandler validationStatusHandler) {
			ObjectUtils.argumentNotNull(validationStatusHandler, "ValidationStatusHandler must be not null");
			instance.setPropertiesValidationStatusHandler(validationStatusHandler);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.PropertyInputGroup.Builder#validationStatusHandler(com.holonplatform.
		 * vaadin.components.ValidationStatusHandler)
		 */
		@Override
		public B validationStatusHandler(ValidationStatusHandler validationStatusHandler) {
			instance.setValidationStatusHandler(validationStatusHandler);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyInputGroup.Builder#withValidator(com.holonplatform.core.
		 * Validator)
		 */
		@Override
		public B withValidator(Validator<PropertyBox> validator) {
			instance.addValidator(validator);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyInputGroup.Builder#validateOnValueChange(boolean)
		 */
		@Override
		public B validateOnValueChange(boolean validateOnValueChange) {
			instance.setValidateOnValueChange(validateOnValueChange);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyInputGroup.Builder#ignorePropertyValidation()
		 */
		@Override
		public B ignorePropertyValidation() {
			instance.setIgnorePropertyValidation(true);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyInputGroup.Builder#stopValidationAtFirstFailure(boolean)
		 */
		@Override
		public B stopValidationAtFirstFailure(boolean stopValidationAtFirstFailure) {
			instance.setStopValidationAtFirstFailure(stopValidationAtFirstFailure);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyInputGroup.Builder#setStopOverallValidationAtFirstFailure(
		 * boolean)
		 */
		@Override
		public B stopOverallValidationAtFirstFailure(boolean stopOverallValidationAtFirstFailure) {
			instance.setStopOverallValidationAtFirstFailure(stopOverallValidationAtFirstFailure);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyInputGroup.Builder#ignoreMissingInputs(boolean)
		 */
		@Override
		public B ignoreMissingInputs(boolean ignoreMissingInputs) {
			instance.setIgnoreMissingInputs(ignoreMissingInputs);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.PropertyInputGroup.Builder#withPostProcessor(com.holonplatform.vaadin.
		 * components.PropertyBinding.PostProcessor)
		 */
		@Override
		public B withPostProcessor(PostProcessor<Input<?>> postProcessor) {
			ObjectUtils.argumentNotNull(postProcessor, "PostProcessor must be not null");
			instance.addInputPostProcessor(postProcessor);
			return builder();
		}

	}

	// Internal

	private class PropertyConfiguration<T> {

		private final Property<T> property;
		private boolean hidden;
		private boolean required;
		private boolean readOnly;
		private PropertyRenderer<Input<T>, T> renderer;
		private DefaultValueProvider<T> defaultValueProvider;
		private List<Validator<T>> propertyValidators;
		private Validator<T> requiredValidator;
		private Localizable requiredMessage;
		private ValidationStatusHandler propertyValidationStatusHandler;

		private Input<T> input;

		/**
		 * Constructor
		 * @param property The property (not null)
		 */
		public PropertyConfiguration(Property<T> property) {
			super();
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			this.property = property;
		}

		/**
		 * Get the property
		 * @return the property
		 */
		public Property<T> getProperty() {
			return property;
		}

		/**
		 * Get whether the property is hidden.
		 * @return <code>true</code> if the property is hidden
		 */
		public boolean isHidden() {
			return hidden;
		}

		/**
		 * Set the property as hidden.
		 * @param hidden <code>true</code> to set the property as hidden
		 */
		public void setHidden(boolean hidden) {
			this.hidden = hidden;
		}

		/**
		 * Get whether the property is required.
		 * @return <code>true</code> if the property is required
		 */
		public boolean isRequired() {
			return required;
		}

		/**
		 * Set the property as required.
		 * @param required <code>true</code> to set the property as required
		 */
		public void setRequired(boolean required) {
			this.required = required;
		}

		/**
		 * Get the {@link Validator} to use to check a required property value.
		 * @return Optional {@link Validator} to use to check a required property valu
		 */
		public Optional<Validator<T>> getRequiredValidator() {
			return Optional.ofNullable(requiredValidator);
		}

		/**
		 * Set the {@link Validator} to use to check a required property value.
		 * @param requiredValidator the required {@link Validator} to set
		 */
		public void setRequiredValidator(Validator<T> requiredValidator) {
			this.requiredValidator = requiredValidator;
		}

		/**
		 * Get the required validation message to show when using the default required property validator.
		 * @return the optional required validation message
		 */
		public Optional<Localizable> getRequiredMessage() {
			return Optional.ofNullable(requiredMessage);
		}

		/**
		 * Set the required validation message to show when using the default required property validator.
		 * @param requiredMessage the required validation message to set
		 */
		public void setRequiredMessage(Localizable requiredMessage) {
			this.requiredMessage = requiredMessage;
		}

		/**
		 * Get whether the property is read-only.
		 * @return <code>true</code> if the property is read-only
		 */
		public boolean isReadOnly() {
			return property.isReadOnly() || readOnly;
		}

		/**
		 * Set the property as read-only.
		 * @param readOnly <code>true</code> to set the property as read-only
		 */
		public void setReadOnly(boolean readOnly) {
			this.readOnly = readOnly;
		}

		/**
		 * Get the {@link PropertyRenderer} to use to create the bound {@link Input}.
		 * @return the {@link PropertyRenderer} to use to create the bound {@link Input}
		 */
		public Optional<PropertyRenderer<Input<T>, T>> getRenderer() {
			return Optional.ofNullable(renderer);
		}

		/**
		 * Set the {@link PropertyRenderer} to use to create the bound {@link Input}.
		 * @param renderer the {@link PropertyRenderer} to set
		 */
		public void setRenderer(PropertyRenderer<Input<T>, T> renderer) {
			this.renderer = renderer;
		}

		/**
		 * Get the property default value provider.
		 * @return Optional property default value provider
		 */
		public Optional<DefaultValueProvider<T>> getDefaultValueProvider() {
			return Optional.ofNullable(defaultValueProvider);
		}

		/**
		 * Set the property default value provider.
		 * @param defaultValueProvider the {@link DefaultValueProvider} to set
		 */
		public void setDefaultValueProvider(DefaultValueProvider<T> defaultValueProvider) {
			this.defaultValueProvider = defaultValueProvider;
		}

		/**
		 * Get the registered property {@link Validator}s.
		 * @return the property validators, empty if none
		 */
		public List<Validator<T>> getValidators() {
			return (propertyValidators != null) ? propertyValidators : Collections.emptyList();
		}

		/**
		 * Add a property {@link Validator}s.
		 * @param validator the {@link Validator} to add
		 */
		public void addValidator(Validator<T> validator) {
			if (validator != null) {
				if (this.propertyValidators == null) {
					this.propertyValidators = new LinkedList<>();
				}
				this.propertyValidators.add(validator);
			}
		}

		/**
		 * Add a property {@link Validator}s as first.
		 * @param validator the {@link Validator} to add
		 */
		public void addValidatorAsFirst(Validator<T> validator) {
			if (validator != null) {
				if (this.propertyValidators == null) {
					this.propertyValidators = new LinkedList<>();
				}
				this.propertyValidators.add(0, validator);
			}
		}

		/**
		 * Get the property {@link ValidationStatusHandler}.
		 * @return Optional property {@link ValidationStatusHandler}
		 */
		public Optional<ValidationStatusHandler> getPropertyValidationStatusHandler() {
			return Optional.ofNullable(propertyValidationStatusHandler);
		}

		/**
		 * Set the property {@link ValidationStatusHandler}.
		 * @param propertyValidationStatusHandler the property {@link ValidationStatusHandler} to set
		 */
		public void setPropertyValidationStatusHandler(ValidationStatusHandler propertyValidationStatusHandler) {
			this.propertyValidationStatusHandler = propertyValidationStatusHandler;
		}

		/**
		 * Get the {@link Input} bound to the property, if available.
		 * @return the optional property input
		 */
		public Optional<Input<T>> getInput() {
			return Optional.ofNullable(input);
		}

		/**
		 * Set the {@link Input} bound to the property.
		 * @param input the {@link Input} to set
		 */
		public void setInput(Input<T> input) {
			this.input = input;
		}

	}

}
