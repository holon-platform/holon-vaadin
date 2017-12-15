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

import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

import com.holonplatform.core.Validator;
import com.holonplatform.core.Validator.ValidationException;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertyRenderer;
import com.holonplatform.vaadin.components.ComposableComponent;
import com.holonplatform.vaadin.components.Input;
import com.holonplatform.vaadin.components.PropertyBinding;
import com.holonplatform.vaadin.components.PropertyBinding.PostProcessor;
import com.holonplatform.vaadin.components.PropertyInputForm;
import com.holonplatform.vaadin.components.PropertyInputGroup;
import com.holonplatform.vaadin.components.PropertyValueComponentSource;
import com.holonplatform.vaadin.components.ValidationStatusHandler;
import com.holonplatform.vaadin.internal.components.builders.AbstractComponentBuilder;
import com.vaadin.shared.Registration;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;

/**
 * Default {@link PropertyInputForm} implementation.
 * 
 * @param <C> Content component type.
 * 
 * @since 5.0.0
 */
public class DefaultPropertyInputForm<C extends Component> extends
		AbstractComposableForm<C, PropertyValueComponentSource> implements PropertyInputForm, PostProcessor<Input<?>> {

	private static final long serialVersionUID = 6071630379695045884L;

	/**
	 * Backing input group
	 */
	private PropertyInputGroup inputGroup;

	/**
	 * Value components source
	 */
	private PropertyValueComponentSource valueComponentSource;

	/**
	 * Constructor
	 */
	public DefaultPropertyInputForm() {
		this(null);
	}

	/**
	 * Constructor with form content
	 * @param content Form composition content
	 */
	public DefaultPropertyInputForm(C content) {
		super(content);
		addStyleName("h-inputform");

		setComponentsWidthMode(ComponentsWidthMode.NONE);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.AbstractComposableForm#getComponentSource()
	 */
	@Override
	protected PropertyValueComponentSource getComponentSource() {
		return valueComponentSource;
	}

	/**
	 * Sets the backing input group.
	 * @param <G> Input group type
	 * @param inputGroup the {@link PropertyInputGroup} to set
	 */
	protected <G extends PropertyInputGroup & PropertyValueComponentSource> void setInputGroup(G inputGroup) {
		this.inputGroup = inputGroup;
		this.valueComponentSource = inputGroup;
	}

	/**
	 * Gets the backing input group
	 * @return the {@link PropertyInputGroup}
	 */
	protected PropertyInputGroup getInputGroup() {
		return inputGroup;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValueComponent#getComponent()
	 */
	@Override
	public Component getComponent() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.PropertyInputGroup.InputPostProcessor#process(com.holonplatform.core.property
	 * .Property, com.holonplatform.vaadin.components.Input)
	 */
	@Override
	public void process(Property<?> property, Input<?> input) {
		final Component inputComponent = input.getComponent();
		if (inputComponent != null) {
			configureComponent(property, inputComponent);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyInputGroup#clear()
	 */
	@Override
	public void clear() {
		getInputGroup().clear();
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.ui.AbstractComponent#setReadOnly(boolean)
	 */
	@Override
	public void setReadOnly(boolean readOnly) {
		getInputGroup().setReadOnly(readOnly);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyInputGroup#getValueIfValid()
	 */
	@Override
	public Optional<PropertyBox> getValueIfValid() {
		return getInputGroup().getValueIfValid();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Validatable#validate()
	 */
	@Override
	public void validate() throws ValidationException {
		getInputGroup().validate();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyInputGroup#getValue(boolean)
	 */
	@Override
	public PropertyBox getValue(boolean validate) {
		return getInputGroup().getValue(validate);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyInputGroup#setValue(com.holonplatform.core.property.PropertyBox,
	 * boolean)
	 */
	@Override
	public void setValue(PropertyBox propertyBox, boolean validate) {
		getInputGroup().setValue(propertyBox, validate);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyInputContainer#getProperties()
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Iterable<Property> getProperties() {
		return getInputGroup().getProperties();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertySetBound#hasProperty(com.holonplatform.core.property.Property)
	 */
	@Override
	public boolean hasProperty(Property<?> property) {
		return getInputGroup().hasProperty(property);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertySetBound#propertyStream()
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Stream<Property> propertyStream() {
		return getInputGroup().propertyStream();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValueHolder#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return getInputGroup().isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyInputBinder#refresh(boolean)
	 */
	@Override
	public void refresh(boolean readOnly) {
		getInputGroup().refresh(readOnly);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyInputBinder#refresh(com.holonplatform.core.property.Property)
	 */
	@Override
	public <T> void refresh(Property<T> property) {
		getInputGroup().refresh(property);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValueHolder#addValueChangeListener(com.holonplatform.vaadin.components.
	 * ValueHolder.ValueChangeListener)
	 */
	@Override
	public Registration addValueChangeListener(ValueChangeListener<PropertyBox> listener) {
		return getInputGroup().addValueChangeListener(listener);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyInputContainer#getInputs()
	 */
	@Override
	public Iterable<Input<?>> getInputs() {
		return getInputGroup().getInputs();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.PropertyInputContainer#getInput(com.holonplatform.core.property.Property)
	 */
	@Override
	public <T> Optional<Input<T>> getInput(Property<T> property) {
		return getInputGroup().getInput(property);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyInputContainer#stream()
	 */
	@Override
	public <T> Stream<PropertyBinding<T, Input<T>>> stream() {
		return getInputGroup().stream();
	}

	// Builder

	/**
	 * Default {@link PropertyInputFormBuilder}.
	 * @param <C> Content type
	 */
	public static class DefaultBuilder<C extends Component> extends
			AbstractComponentBuilder<PropertyInputForm, DefaultPropertyInputForm<C>, PropertyInputFormBuilder<C>>
			implements PropertyInputFormBuilder<C> {

		private final DefaultPropertyInputGroup.InternalBuilder inputGroupBuilder;

		/**
		 * Constructor
		 * @param content Form composition content
		 */
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public DefaultBuilder(C content) {
			super(new DefaultPropertyInputForm<>(content));
			this.inputGroupBuilder = new DefaultPropertyInputGroup.InternalBuilder();

			// setup default composer
			if (getInstance().getComposer() == null) {
				if (content instanceof ComponentContainer) {
					getInstance().setComposer((Composer) ComposableComponent.componentContainerComposer());
				}
			}
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.PropertyInputGroup.Builder#properties(com.holonplatform.core.property.
		 * Property[])
		 */
		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public <P extends Property> PropertyInputFormBuilder<C> properties(P... properties) {
			inputGroupBuilder.properties(properties);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyInputGroup.Builder#properties(java.lang.Iterable)
		 */
		@SuppressWarnings("rawtypes")
		@Override
		public <P extends Property> PropertyInputFormBuilder<C> properties(Iterable<P> properties) {
			inputGroupBuilder.properties(properties);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyInputGroup.Builder#readOnly(com.holonplatform.core.property.
		 * Property)
		 */
		@Override
		public <T> PropertyInputFormBuilder<C> readOnly(Property<T> property) {
			inputGroupBuilder.readOnly(property);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyInputGroup.Builder#required(com.holonplatform.core.property.
		 * Property)
		 */
		@Override
		public <T> PropertyInputFormBuilder<C> required(Property<T> property) {
			inputGroupBuilder.required(property);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyInputGroup.Builder#required(com.holonplatform.core.property.
		 * Property, com.holonplatform.core.Validator)
		 */
		@Override
		public <T> PropertyInputFormBuilder<C> required(Property<T> property, Validator<T> validator) {
			inputGroupBuilder.required(property, validator);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyInputGroup.Builder#required(com.holonplatform.core.property.
		 * Property, com.holonplatform.core.i18n.Localizable)
		 */
		@Override
		public <T> PropertyInputFormBuilder<C> required(Property<T> property, Localizable message) {
			inputGroupBuilder.required(property, message);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyInputGroup.Builder#hidden(com.holonplatform.core.property.
		 * Property)
		 */
		@Override
		public <T> PropertyInputFormBuilder<C> hidden(Property<T> property) {
			inputGroupBuilder.hidden(property);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.PropertyInputGroup.Builder#defaultValue(com.holonplatform.core.property
		 * .Property, com.holonplatform.vaadin.components.PropertyInputGroup.DefaultValueProvider)
		 */
		@Override
		public <T> PropertyInputFormBuilder<C> defaultValue(Property<T> property,
				DefaultValueProvider<T> defaultValueProvider) {
			inputGroupBuilder.defaultValue(property, defaultValueProvider);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyInputGroup.Builder#withValidator(com.holonplatform.core.
		 * property.Property, com.holonplatform.core.Validator)
		 */
		@Override
		public <T> PropertyInputFormBuilder<C> withValidator(Property<T> property, Validator<T> validator) {
			inputGroupBuilder.withValidator(property, validator);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.PropertyInputGroup.Builder#validationStatusHandler(com.holonplatform.core
		 * .property.Property, com.holonplatform.vaadin.components.ValidationStatusHandler)
		 */
		@Override
		public <T> PropertyInputFormBuilder<C> validationStatusHandler(Property<T> property,
				ValidationStatusHandler validationStatusHandler) {
			inputGroupBuilder.validationStatusHandler(property, validationStatusHandler);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyInputGroup.Builder#propertiesValidationStatusHandler(com.
		 * holonplatform.vaadin.components.ValidationStatusHandler)
		 */
		@Override
		public PropertyInputFormBuilder<C> propertiesValidationStatusHandler(
				ValidationStatusHandler validationStatusHandler) {
			inputGroupBuilder.propertiesValidationStatusHandler(validationStatusHandler);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.PropertyInputGroup.Builder#validationStatusHandler(com.holonplatform.
		 * vaadin.components.ValidationStatusHandler)
		 */
		@Override
		public PropertyInputFormBuilder<C> validationStatusHandler(ValidationStatusHandler validationStatusHandler) {
			inputGroupBuilder.validationStatusHandler(validationStatusHandler);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyInputGroup.Builder#validateOnValueChange(boolean)
		 */
		@Override
		public PropertyInputFormBuilder<C> validateOnValueChange(boolean validateOnValueChange) {
			inputGroupBuilder.validateOnValueChange(validateOnValueChange);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyInputGroup.Builder#ignorePropertyValidation()
		 */
		@Override
		public PropertyInputFormBuilder<C> ignorePropertyValidation() {
			inputGroupBuilder.ignorePropertyValidation();
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.PropertyInputGroup.Builder#bind(com.holonplatform.core.property.Property,
		 * com.holonplatform.core.property.PropertyRenderer)
		 */
		@Override
		public <T> PropertyInputFormBuilder<C> bind(Property<T> property, PropertyRenderer<Input<T>, T> renderer) {
			inputGroupBuilder.bind(property, renderer);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyInputGroup.Builder#withValidator(com.holonplatform.core.
		 * Validator)
		 */
		@Override
		public PropertyInputFormBuilder<C> withValidator(Validator<PropertyBox> validator) {
			inputGroupBuilder.withValidator(validator);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyInputGroup.Builder#stopValidationAtFirstFailure(boolean)
		 */
		@Override
		public PropertyInputFormBuilder<C> stopValidationAtFirstFailure(boolean stopValidationAtFirstFailure) {
			inputGroupBuilder.stopValidationAtFirstFailure(stopValidationAtFirstFailure);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.PropertyInputGroup.Builder#stopOverallValidationAtFirstFailure(boolean)
		 */
		@Override
		public PropertyInputFormBuilder<C> stopOverallValidationAtFirstFailure(
				boolean stopOverallValidationAtFirstFailure) {
			inputGroupBuilder.stopOverallValidationAtFirstFailure(stopOverallValidationAtFirstFailure);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyInputGroup.Builder#ignoreMissingInputs(boolean)
		 */
		@Override
		public PropertyInputFormBuilder<C> ignoreMissingInputs(boolean ignoreMissingInputs) {
			inputGroupBuilder.ignoreMissingInputs(ignoreMissingInputs);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyInputGroup.Builder#excludeReadOnlyProperties()
		 */
		@Override
		public PropertyInputFormBuilder<C> excludeReadOnlyProperties() {
			inputGroupBuilder.excludeReadOnlyProperties();
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.PropertyInputGroup.Builder#withPostProcessor(com.holonplatform.vaadin.
		 * components.PropertyBinding.PostProcessor)
		 */
		@Override
		public PropertyInputFormBuilder<C> withPostProcessor(PostProcessor<Input<?>> postProcessor) {
			inputGroupBuilder.withPostProcessor(postProcessor);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.PropertyInputGroup.Builder#withValueChangeListener(com.holonplatform.
		 * vaadin.components.ValueHolder.ValueChangeListener)
		 */
		@Override
		public PropertyInputFormBuilder<C> withValueChangeListener(ValueChangeListener<PropertyBox> listener) {
			inputGroupBuilder.withValueChangeListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyInputGroup.Builder#valueChangeMode(com.vaadin.shared.ui.
		 * ValueChangeMode)
		 */
		@Override
		public PropertyInputFormBuilder<C> valueChangeMode(ValueChangeMode valueChangeMode) {
			inputGroupBuilder.valueChangeMode(valueChangeMode);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.PropertyInputGroup.Builder#withValueChangeListener(com.holonplatform.core
		 * .property.Property, com.holonplatform.vaadin.components.ValueHolder.ValueChangeListener)
		 */
		@Override
		public <T> PropertyInputFormBuilder<C> withValueChangeListener(Property<T> property,
				ValueChangeListener<T> listener) {
			inputGroupBuilder.withValueChangeListener(property, listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.PropertyInputGroup.Builder#withValueChangeListener(com.holonplatform.core
		 * .property.Property, com.holonplatform.vaadin.components.PropertyInputBinder.PropertyInputValueChangeListener)
		 */
		@Override
		public <T> PropertyInputFormBuilder<C> withValueChangeListener(Property<T> property,
				PropertyInputValueChangeListener<T> listener) {
			inputGroupBuilder.withValueChangeListener(property, listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyInputGroup.Builder#valueChangeMode(com.holonplatform.core.
		 * property.Property, com.vaadin.shared.ui.ValueChangeMode)
		 */
		@Override
		public <T> PropertyInputFormBuilder<C> valueChangeMode(Property<T> property, ValueChangeMode valueChangeMode) {
			inputGroupBuilder.valueChangeMode(property, valueChangeMode);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.PropertyInputGroup.Builder#valueChangeTimeout(com.holonplatform.core.
		 * property.Property, int)
		 */
		@Override
		public <T> PropertyInputFormBuilder<C> valueChangeTimeout(Property<T> property, int valueChangeTimeout) {
			inputGroupBuilder.valueChangeTimeout(property, valueChangeTimeout);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyForm.PropertyFormBuilder#initializer(java.util.function.
		 * Consumer)
		 */
		@Override
		public PropertyInputFormBuilder<C> initializer(Consumer<C> initializer) {
			ObjectUtils.argumentNotNull(initializer, "Form content initializer must be not null");
			getInstance().setInitializer(initializer);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.ComposableComponent.Builder#composer(com.holonplatform.vaadin.components.
		 * ComposableComponent.Composer)
		 */
		@Override
		public PropertyInputFormBuilder<C> composer(Composer<? super C, PropertyValueComponentSource> composer) {
			ObjectUtils.argumentNotNull(composer, "Composer must be not null");
			getInstance().setComposer(composer);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyForm.PropertyFormBuilder#composeOnAttach(boolean)
		 */
		@Override
		public PropertyInputFormBuilder<C> composeOnAttach(boolean composeOnAttach) {
			getInstance().setComposeOnAttach(composeOnAttach);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.ComposableComponent.Builder#componentsWidthMode(com.holonplatform.vaadin.
		 * components.ComposableComponent.ComponentsWidthMode)
		 */
		@Override
		public PropertyInputFormBuilder<C> componentsWidthMode(ComponentsWidthMode componentsWidthMode) {
			getInstance().setComponentsWidthMode(componentsWidthMode);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.PropertyForm.PropertyFormBuilder#propertyCaption(com.holonplatform.core
		 * .property.Property, com.holonplatform.core.i18n.Localizable)
		 */
		@Override
		public PropertyInputFormBuilder<C> propertyCaption(Property<?> property, Localizable caption) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			ObjectUtils.argumentNotNull(caption, "Caption must be not null");
			getInstance().setPropertyCaption(property, caption);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.PropertyForm.PropertyFormBuilder#propertyCaption(com.holonplatform.core
		 * .property.Property, java.lang.String)
		 */
		@Override
		public PropertyInputFormBuilder<C> propertyCaption(Property<?> property, String caption) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			getInstance().setPropertyCaption(property, Localizable.builder().message(caption).build());
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.PropertyForm.PropertyFormBuilder#propertyCaption(com.holonplatform.core
		 * .property.Property, java.lang.String, java.lang.String, java.lang.Object[])
		 */
		@Override
		public PropertyInputFormBuilder<C> propertyCaption(Property<?> property, String defaultCaption,
				String messageCode, Object... arguments) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			getInstance().setPropertyCaption(property, Localizable.builder().message(defaultCaption)
					.messageCode(messageCode).messageArguments(arguments).build());
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.PropertyForm.PropertyFormBuilder#hidePropertyCaption(com.holonplatform.
		 * core.property.Property)
		 */
		@Override
		public PropertyInputFormBuilder<C> hidePropertyCaption(Property<?> property) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			getInstance().hidePropertyCaption(property);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentBuilder#builder()
		 */
		@Override
		protected com.holonplatform.vaadin.components.PropertyInputForm.PropertyInputFormBuilder<C> builder() {
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentBuilder#build(com.vaadin.ui.
		 * AbstractComponent)
		 */
		@Override
		protected PropertyInputForm build(DefaultPropertyInputForm<C> instance) {
			DefaultPropertyInputGroup inputGroup = inputGroupBuilder.withPostProcessor(instance).build();
			inputGroup.setValueComponentSupplier(() -> instance);
			instance.setInputGroup(inputGroup);
			return instance;
		}

	}

}
