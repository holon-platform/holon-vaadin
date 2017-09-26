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

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertyRenderer;
import com.holonplatform.core.property.PropertyRendererRegistry.NoSuitableRendererAvailableException;
import com.holonplatform.core.property.VirtualProperty;
import com.holonplatform.vaadin.components.PropertyBinding;
import com.holonplatform.vaadin.components.PropertyBinding.PostProcessor;
import com.holonplatform.vaadin.components.PropertyValueComponentSource;
import com.holonplatform.vaadin.components.PropertyViewGroup;
import com.holonplatform.vaadin.components.ValueComponent;
import com.holonplatform.vaadin.components.ViewComponent;
import com.vaadin.shared.Registration;

/**
 * Default {@link PropertyViewGroup} implementation.
 *
 * @since 5.0.0
 */
public class DefaultPropertyViewGroup implements PropertyViewGroup, PropertyValueComponentSource {

	private static final long serialVersionUID = -2110591918893531742L;

	/**
	 * Current value
	 */
	private PropertyBox value;

	/**
	 * Property set
	 */
	@SuppressWarnings("rawtypes")
	private final List<Property> properties = new LinkedList<>();

	/**
	 * Bound ViewComponents
	 */
	@SuppressWarnings("rawtypes")
	private final Map<Property, ViewComponent> propertyViews = new HashMap<>();

	/**
	 * Custom ViewComponentPropertyRenderers
	 */
	@SuppressWarnings("rawtypes")
	private final Map<Property, ViewComponentPropertyRenderer> propertyRenderers = new HashMap<>(8);

	/**
	 * Hidden properties
	 */
	@SuppressWarnings("rawtypes")
	private final Map<Property, Object> hiddenProperties = new HashMap<>();

	/**
	 * ViewComponent post-processors
	 */
	private final List<PostProcessor<ViewComponent<?>>> postProcessors = new LinkedList<>();

	/**
	 * Value change listeners
	 */
	private final List<ValueChangeListener<PropertyBox>> valueChangeListeners = new LinkedList<>();

	/**
	 * Ignore missing ViewComponents
	 */
	private boolean ignoreMissingViewComponent = false;

	/**
	 * Constructor
	 */
	public DefaultPropertyViewGroup() {
		super();
	}

	/**
	 * Add a property to the property set
	 * @param property Property to add
	 */
	@SuppressWarnings("rawtypes")
	protected void addProperty(Property property) {
		if (property != null) {
			properties.add(property);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertySetBound#getProperties()
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Iterable<Property> getProperties() {
		return Collections.unmodifiableList(properties);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertySetBound#hasProperty(com.holonplatform.core.property.Property)
	 */
	@Override
	public boolean hasProperty(Property<?> property) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		return properties.contains(property);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertySetBound#propertyStream()
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Stream<Property> propertyStream() {
		return properties.stream();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyViewGroup#getViewComponents()
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Iterable<ViewComponent> getViewComponents() {
		return properties.stream().filter(p -> propertyViews.containsKey(p)).map(p -> propertyViews.get(p))
				.collect(Collectors.toList());
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyViewGroup#getViewComponent(com.holonplatform.core.property.
	 * Property)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> Optional<ViewComponent<T>> getViewComponent(Property<T> property) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		return Optional.ofNullable(propertyViews.get(property));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyViewGroup#stream()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> Stream<PropertyBinding<T, ViewComponent<T>>> stream() {
		return propertyViews.entrySet().stream().map(e -> PropertyBinding.create(e.getKey(), e.getValue()));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyValueComponentSource#getValueComponents()
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Iterable<ValueComponent> getValueComponents() {
		return properties.stream().filter(p -> propertyViews.containsKey(p)).map(p -> propertyViews.get(p))
				.collect(Collectors.toList());
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyValueComponentSource#getValueComponent(com.holonplatform.core.
	 * property.Property)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> Optional<ValueComponent<T>> getValueComponent(Property<T> property) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		return Optional.ofNullable(propertyViews.get(property));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyValueComponentSource#streamOfValueComponents()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Stream<PropertyBinding<?, ValueComponent<?>>> streamOfValueComponents() {
		return propertyViews.entrySet().stream().map(e -> PropertyBinding.create(e.getKey(), e.getValue()));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyViewGroup#clear()
	 */
	@Override
	public void clear() {
		setValue(null);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyViewGroup#getValue()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public PropertyBox getValue() {
		final PropertyBox propertyBox = PropertyBox.builder(properties).invalidAllowed(true).build();
		if (value != null) {
			properties.forEach(p -> {
				value.getValueIfPresent(p).ifPresent(v -> propertyBox.setValue(p, v));
			});
		}
		return propertyBox;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValueHolder#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return getValue().propertyValues().filter(v -> v.hasValue()).findAny().isPresent();
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
		return () -> removeValueChangeListener(listener);
	}

	/**
	 * Removes a {@link ValueChangeListener}.
	 * @param listener the listener to remove
	 */
	public void removeValueChangeListener(ValueChangeListener<PropertyBox> listener) {
		if (listener != null) {
			valueChangeListeners.remove(listener);
		}
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

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyViewGroup#setValue(com.holonplatform.core.property.PropertyBox)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void setValue(PropertyBox propertyBox) {
		final PropertyBox oldValue = this.value;
		this.value = propertyBox;
		if (propertyBox == null) {
			// reset all values
			properties.forEach(p -> propertyViews.get(p).clear());
		} else {
			properties.forEach(p -> {
				final ViewComponent vc = propertyViews.get(p);
				if (vc != null) {
					Object value = getPropertyValue(propertyBox, p);
					if (value != null) {
						vc.setValue(value);
					} else {
						vc.clear();
					}
				}
			});
		}

		// fire value change
		fireValueChange(oldValue, propertyBox);
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
	 * Whether to ignore missing property ViewComponents
	 * @return <code>true</code> if missing property ViewComponents must be ignored
	 */
	protected boolean isIgnoreMissingViewComponent() {
		return ignoreMissingViewComponent;
	}

	/**
	 * Set whether to ignore missing property ViewComponents
	 * @param ignoreMissingViewComponent <code>true</code> to ignore missing property ViewComponents
	 */
	protected void setIgnoreMissingViewComponent(boolean ignoreMissingViewComponent) {
		this.ignoreMissingViewComponent = ignoreMissingViewComponent;
	}

	/**
	 * Register a {@link ViewComponent} {@link PostProcessor}.
	 * @param postProcessor the post-processor to register
	 */
	protected void addViewComponentPostProcessor(PostProcessor<ViewComponent<?>> postProcessor) {
		if (postProcessor != null) {
			postProcessors.add(postProcessor);
		}
	}

	/**
	 * Set a property as hidden in UI
	 * @param property Property to set as hidden
	 */
	@SuppressWarnings("rawtypes")
	public void setPropertyHidden(Property property) {
		if (property != null && !hiddenProperties.containsKey(property)) {
			hiddenProperties.put(property, null);
		}
	}

	@SuppressWarnings("rawtypes")
	protected boolean isPropertyHidden(Property property) {
		return property != null && hiddenProperties.containsKey(property);
	}

	/**
	 * Set the {@link ViewComponentPropertyRenderer} to use with given property
	 * @param <T> Property type
	 * @param property Property
	 * @param renderer Renderer
	 */
	protected <T> void setPropertyRenderer(Property<T> property, ViewComponentPropertyRenderer<T> renderer) {
		if (property != null && renderer != null) {
			propertyRenderers.put(property, renderer);
		}
	}

	/**
	 * Render given property as a {@link ViewComponent}, using custom {@link ViewComponentPropertyRenderer} or default
	 * {@link Property#render(Class)} method
	 * @param <T> Property type
	 * @param property Property to render
	 * @return Rendered ViewComponent
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected <T> Optional<ViewComponent> render(Property<T> property) {
		// check custom renderer
		if (propertyRenderers.containsKey(property)) {
			final ViewComponentPropertyRenderer<T> r = propertyRenderers.get(property);
			return Optional.ofNullable(r.render(property));
		}
		// use registry
		return property.renderIfAvailable(ViewComponent.class);
	}

	/**
	 * Configure ViewComponent before binding
	 * @param property Property
	 * @param viewComponent ViewComponent to configure
	 */
	protected void configureViewComponent(final Property<?> property, final ViewComponent<?> viewComponent) {
		// Configurators
		postProcessors.forEach(fc -> fc.process(property, viewComponent));
	}

	/**
	 * Build and bind {@link ViewComponent}s to the properties of the property set.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void build() {
		propertyViews.clear();
		// render and bind ViewComponents
		properties.forEach(p -> {
			// exclude hidden properties
			if (!isPropertyHidden(p)) {
				final Optional<ViewComponent> viewComponent = render(p);
				if (!viewComponent.isPresent() && !isIgnoreMissingViewComponent()) {
					throw new NoSuitableRendererAvailableException(
							"No ViewComponent render available to render the property [" + p.toString()
									+ "] as a ViewComponent");
				}
				viewComponent.ifPresent(v -> {
					// configure
					configureViewComponent(p, v);
					// bind
					propertyViews.put(p, v);
				});
			}
		});
	}

	// Builder

	static class InternalBuilder extends AbstractBuilder<DefaultPropertyViewGroup, InternalBuilder> {

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.internal.components.DefaultPropertyViewGroup.AbstractBuilder#builder()
		 */
		@Override
		protected InternalBuilder builder() {
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyViewGroup.Builder#build()
		 */
		@Override
		public DefaultPropertyViewGroup build() {
			instance.build();
			return instance;
		}

	}

	public static class DefaultBuilder extends AbstractBuilder<PropertyViewGroup, PropertyViewGroupBuilder>
			implements PropertyViewGroupBuilder {

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.internal.components.DefaultPropertyViewGroup.AbstractBuilder#builder()
		 */
		@Override
		protected PropertyViewGroupBuilder builder() {
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyViewGroup.Builder#build()
		 */
		@Override
		public PropertyViewGroup build() {
			instance.build();
			return instance;
		}

	}

	public static abstract class AbstractBuilder<G extends PropertyViewGroup, B extends Builder<G, B>>
			implements Builder<G, B> {

		protected final DefaultPropertyViewGroup instance = new DefaultPropertyViewGroup();

		/**
		 * Actual builder
		 * @return Builder
		 */
		protected abstract B builder();

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.PropertyViewGroup.Builder#properties(com.holonplatform.core.property.
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
		 * @see com.holonplatform.vaadin.components.PropertyViewGroup.Builder#properties(java.lang.Iterable)
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
		 * @see com.holonplatform.vaadin.components.PropertyViewGroup.Builder#hidden(com.holonplatform.core.property.
		 * Property)
		 */
		@Override
		public <T> B hidden(Property<T> property) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			instance.setPropertyHidden(property);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyViewGroup.Builder#bind(com.holonplatform.core.property.
		 * Property, com.holonplatform.core.property.PropertyRenderer)
		 */
		@Override
		public <T, F extends T> B bind(Property<T> property, PropertyRenderer<ViewComponent<F>, T> renderer) {
			ObjectUtils.argumentNotNull(renderer, "PropertyRenderer must be not null");
			return bind(property, p -> renderer.render(p));
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyViewGroup.Builder#bind(com.holonplatform.core.property.
		 * Property, com.holonplatform.vaadin.components.PropertyViewGroup.ViewComponentPropertyRenderer)
		 */
		@Override
		public <T> B bind(Property<T> property, ViewComponentPropertyRenderer<T> renderer) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			ObjectUtils.argumentNotNull(renderer, "PropertyRenderer must be not null");
			instance.setPropertyRenderer(property, renderer);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.PropertyViewGroup.Builder#withPostProcessor(com.holonplatform.vaadin.
		 * components.PropertyBinding.PostProcessor)
		 */
		@Override
		public B withPostProcessor(PostProcessor<ViewComponent<?>> postProcessor) {
			ObjectUtils.argumentNotNull(postProcessor, "PostProcessor must be not null");
			instance.addViewComponentPostProcessor(postProcessor);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyViewGroup.Builder#ignoreMissingViewComponents(boolean)
		 */
		@Override
		public B ignoreMissingViewComponents(boolean ignoreMissingViewComponents) {
			instance.setIgnoreMissingViewComponent(ignoreMissingViewComponents);
			return builder();
		}

	}

}
