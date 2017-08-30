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

import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertyRenderer;
import com.holonplatform.vaadin.components.PropertyBinding;
import com.holonplatform.vaadin.components.PropertyBinding.PostProcessor;
import com.holonplatform.vaadin.components.PropertyValueComponentSource;
import com.holonplatform.vaadin.components.PropertyViewForm;
import com.holonplatform.vaadin.components.PropertyViewGroup;
import com.holonplatform.vaadin.components.ViewComponent;
import com.holonplatform.vaadin.internal.components.builders.AbstractComponentBuilder;
import com.vaadin.shared.Registration;
import com.vaadin.ui.Component;

/**
 * Default {@link PropertyViewForm} implementation.
 * 
 * @param <C> Content component type.
 *
 * @since 5.0.0
 */
public class DefaultPropertyViewForm<C extends Component>
		extends AbstractComposableForm<C, PropertyValueComponentSource>
		implements PropertyViewForm, PostProcessor<ViewComponent<?>> {

	private static final long serialVersionUID = -4202049108110710744L;

	/**
	 * Backing view group
	 */
	private PropertyViewGroup viewGroup;

	/**
	 * Value components source
	 */
	private PropertyValueComponentSource valueComponentSource;

	/**
	 * Constructor
	 */
	public DefaultPropertyViewForm() {
		super();
	}

	/**
	 * Constructor with form content
	 * @param content Form composition content
	 */
	public DefaultPropertyViewForm(C content) {
		super(content);
		addStyleName("h-viewform");
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
	 * Sets the backing view group.
	 * @param viewGroup the view group to set
	 */
	protected <G extends PropertyViewGroup & PropertyValueComponentSource> void setViewGroup(G viewGroup) {
		this.viewGroup = viewGroup;
		this.valueComponentSource = viewGroup;
	}

	/**
	 * Get the backing view group.
	 * @return the backing view group
	 */
	protected PropertyViewGroup getViewGroup() {
		return viewGroup;
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
	 * @see com.holonplatform.vaadin.components.PropertyViewGroup#clear()
	 */
	@Override
	public void clear() {
		getViewGroup().clear();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyViewGroup#getValue()
	 */
	@Override
	public PropertyBox getValue() {
		return getViewGroup().getValue();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyViewGroup#setValue(com.holonplatform.core.property.PropertyBox)
	 */
	@Override
	public void setValue(PropertyBox propertyBox) {
		getViewGroup().setValue(propertyBox);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyViewSource#getProperties()
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Iterable<Property> getProperties() {
		return getViewGroup().getProperties();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertySetBound#hasProperty(com.holonplatform.core.property.Property)
	 */
	@Override
	public boolean hasProperty(Property<?> property) {
		return getViewGroup().hasProperty(property);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertySetBound#propertyStream()
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Stream<Property> propertyStream() {
		return getViewGroup().propertyStream();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValueHolder#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return getViewGroup().isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValueHolder#addValueChangeListener(com.holonplatform.vaadin.components.
	 * ValueHolder.ValueChangeListener)
	 */
	@Override
	public Registration addValueChangeListener(ValueChangeListener<PropertyBox> listener) {
		return getViewGroup().addValueChangeListener(listener);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyViewSource#getViewComponents()
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Iterable<ViewComponent> getViewComponents() {
		return getViewGroup().getViewComponents();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.PropertyViewSource#getViewComponent(com.holonplatform.core.property.Property)
	 */
	@Override
	public <T> Optional<ViewComponent<T>> getViewComponent(Property<T> property) {
		return getViewGroup().getViewComponent(property);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyViewSource#stream()
	 */
	@Override
	public <T> Stream<PropertyBinding<T, ViewComponent<T>>> stream() {
		return getViewGroup().stream();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyBinding.PostProcessor#process(com.holonplatform.core.property.
	 * Property, java.lang.Object)
	 */
	@Override
	public void process(Property<?> property, ViewComponent<?> component) {
		configureComponent(property, component);
	}

	// Builder

	/**
	 * Default {@link PropertyViewFormBuilder}.
	 * @param <C> Content type
	 */
	public static class DefaultBuilder<C extends Component>
			extends AbstractComponentBuilder<PropertyViewForm, DefaultPropertyViewForm<C>, PropertyViewFormBuilder<C>>
			implements PropertyViewFormBuilder<C> {

		private final DefaultPropertyViewGroup.InternalBuilder viewGroupBuilder;

		/**
		 * Constructor
		 * @param content Form composition content
		 */
		public DefaultBuilder(C content) {
			super(new DefaultPropertyViewForm<>(content));
			this.viewGroupBuilder = new DefaultPropertyViewGroup.InternalBuilder();
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public <P extends Property> PropertyViewFormBuilder<C> properties(P... properties) {
			viewGroupBuilder.properties(properties);
			return this;
		}

		@SuppressWarnings("rawtypes")
		@Override
		public <P extends Property> PropertyViewFormBuilder<C> properties(Iterable<P> properties) {
			viewGroupBuilder.properties(properties);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyViewGroup.Builder#hidden(com.holonplatform.core.property.
		 * Property)
		 */
		@Override
		public <T> PropertyViewFormBuilder<C> hidden(Property<T> property) {
			viewGroupBuilder.hidden(property);
			return this;
		}

		@Override
		public <T, F extends T> PropertyViewFormBuilder<C> bind(Property<T> property,
				PropertyRenderer<ViewComponent<F>, T> renderer) {
			viewGroupBuilder.bind(property, renderer);
			return this;
		}

		@Override
		public <T> PropertyViewFormBuilder<C> bind(Property<T> property, ViewComponentPropertyRenderer<T> renderer) {
			viewGroupBuilder.bind(property, renderer);
			return this;
		}

		@Override
		public PropertyViewFormBuilder<C> withPostProcessor(PostProcessor<ViewComponent<?>> postProcessor) {
			viewGroupBuilder.withPostProcessor(postProcessor);
			return this;
		}

		@Override
		public PropertyViewFormBuilder<C> ignoreMissingViewComponents(boolean ignoreMissingViewComponents) {
			viewGroupBuilder.ignoreMissingViewComponents(ignoreMissingViewComponents);
			return this;
		}

		@Override
		public PropertyViewFormBuilder<C> initializer(Consumer<C> initializer) {
			ObjectUtils.argumentNotNull(initializer, "Form content initializer must be not null");
			getInstance().setInitializer(initializer);
			return this;
		}

		@Override
		public PropertyViewFormBuilder<C> composer(Composer<? super C, PropertyValueComponentSource> composer) {
			ObjectUtils.argumentNotNull(composer, "Composer must be not null");
			getInstance().setComposer(composer);
			return this;
		}

		@Override
		public PropertyViewFormBuilder<C> composeOnAttach(boolean composeOnAttach) {
			getInstance().setComposeOnAttach(composeOnAttach);
			return this;
		}

		@Override
		public PropertyViewFormBuilder<C> propertyCaption(Property<?> property, Localizable caption) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			ObjectUtils.argumentNotNull(caption, "Caption must be not null");
			getInstance().setPropertyCaption(property, caption);
			return this;
		}

		@Override
		public PropertyViewFormBuilder<C> propertyCaption(Property<?> property, String caption) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			getInstance().setPropertyCaption(property, Localizable.builder().message(caption).build());
			return this;
		}

		@Override
		public PropertyViewFormBuilder<C> propertyCaption(Property<?> property, String defaultCaption,
				String messageCode, Object... arguments) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			getInstance().setPropertyCaption(property, Localizable.builder().message(defaultCaption)
					.messageCode(messageCode).messageArguments(arguments).build());
			return this;
		}

		@Override
		public PropertyViewFormBuilder<C> hidePropertyCaption(Property<?> property) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			getInstance().hidePropertyCaption(property);
			return this;
		}

		@Override
		protected com.holonplatform.vaadin.components.PropertyViewForm.PropertyViewFormBuilder<C> builder() {
			return this;
		}

		@Override
		protected PropertyViewForm build(DefaultPropertyViewForm<C> instance) {
			instance.setViewGroup(viewGroupBuilder.withPostProcessor(instance).build());
			return instance;
		}

	}

}
