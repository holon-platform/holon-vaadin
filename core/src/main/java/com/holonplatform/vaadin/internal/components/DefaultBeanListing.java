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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.holonplatform.core.beans.BeanPropertySet;
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.core.internal.Logger;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.PathProperty;
import com.holonplatform.vaadin.components.BeanListing;
import com.holonplatform.vaadin.components.Field;
import com.holonplatform.vaadin.internal.VaadinLogger;
import com.vaadin.data.HasValue;
import com.vaadin.data.PropertyDefinition;
import com.vaadin.data.PropertySet;
import com.vaadin.data.ValueProvider;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FontIcon;
import com.vaadin.server.Setter;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.renderers.ImageRenderer;
import com.vaadin.ui.renderers.Renderer;
import com.vaadin.util.ReflectTools;

/**
 * Default {@link BeanListing} implementation.
 * 
 * @param <T> Bean type
 *
 * @since 5.0.0
 */
public class DefaultBeanListing<T> extends DefaultItemListing<T, String> implements BeanListing<T> {

	private static final long serialVersionUID = 2163943323958351369L;

	private static final Logger LOGGER = VaadinLogger.create();

	/**
	 * Grid property set
	 */
	protected final BeanGridPropertySet<T> propertySet;

	/**
	 * Cosntructor.
	 * @param beanType Bean class (not null)
	 */
	public DefaultBeanListing(Class<T> beanType) {
		super();
		propertySet = new BeanGridPropertySet<>(beanType);
		initGrid(Grid.withPropertySet(propertySet));
	}

	/**
	 * Add a <em>virtual</em> column to the listing, i.e. a column which is not directly bound to a bean property.
	 * @param <V> Column value type
	 * @param id Column id (not null)
	 * @param type Column value type (not null)
	 * @param valueProvider Column value provider (not null)
	 */
	public <V> void addVirtualColumn(String id, Class<V> type, ValueProvider<T, V> valueProvider) {
		getGrid().addColumn(propertySet.addVirtualProperty(type, valueProvider, id));
	}

	/**
	 * Get the default column ids of this listing, i.e. the ids of all detected bean property columns and any added
	 * virtual column.
	 * @return the default column ids
	 */
	public List<String> getDefaultColumnIds() {
		return propertySet.getProperties().map(p -> p.getName()).collect(Collectors.toList());
	}

	/**
	 * Get the property definitions of this listing.
	 * @return the property definitions
	 */
	public Stream<BeanPropertyDefinition<T, ?>> getPropertyDefinitions() {
		return propertySet.getPropertyDefinitions();
	}

	/**
	 * Get the bean property with given <code>propertyId</code>.
	 * @param propertyId Property id
	 * @return The bean property with given id, or an empty Optional if not found
	 */
	@SuppressWarnings("unchecked")
	protected Optional<PathProperty<Object>> getBeanProperty(String propertyId) {
		if (propertyId == null) {
			return Optional.empty();
		}
		return propertySet.getPropertyDefinition(propertyId)
				.flatMap(d -> ((BeanPropertyDefinition<T, Object>) d).getBeanProperty());
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.DefaultItemListing#getColumnId(java.lang.Object)
	 */
	@Override
	public String getColumnId(String property) {
		return property;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.DefaultItemListing#getColumnProperty(java.lang.String)
	 */
	@Override
	protected String getColumnProperty(String columnId) {
		return columnId;
	}

	@Override
	protected Optional<ValueProvider<?, ?>> getDefaultPropertyPresenter(String property) {
		Optional<PathProperty<Object>> beanProperty = getBeanProperty(property);
		if (beanProperty.isPresent()) {
			if (FontIcon.class.isAssignableFrom(beanProperty.get().getType())) {
				return Optional.of(v -> ((FontIcon) v).getHtml());
			}
			return Optional.of(v -> beanProperty.get().present(v));
		}
		return super.getDefaultPropertyPresenter(property);
	}

	@Override
	protected Optional<Renderer<?>> getDefaultPropertyRenderer(String property) {
		Optional<Renderer<?>> renderer = propertySet.getPropertyDefinition(property).map(d -> d.getType())
				.flatMap(t -> getDefaultPropertyRendererByType(t));
		if (renderer.isPresent()) {
			return renderer;
		}
		return super.getDefaultPropertyRenderer(property);
	}

	/**
	 * Get the default {@link Renderer} to use with given data type.
	 * @param type Data type (not null)
	 * @return Optional default renderer
	 */
	protected Optional<Renderer<?>> getDefaultPropertyRendererByType(Class<?> type) {
		if (Component.class.isAssignableFrom(type)) {
			return Optional.of(new ComponentRenderer());
		}
		if (FontIcon.class.isAssignableFrom(type)) {
			return Optional.of(new HtmlRenderer(""));
		}
		if (ExternalResource.class.isAssignableFrom(type) || ThemeResource.class.isAssignableFrom(type)) {
			return Optional.of(new ImageRenderer<>());
		}
		return Optional.empty();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.DefaultItemListing#getDefaultPropertyEditor(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected <E extends HasValue<?> & Component> Optional<E> getDefaultPropertyEditor(String property) {
		E field = null;
		try {
			field = (E) getBeanProperty(property).flatMap(p -> p.renderIfAvailable(Field.class)).orElse(null);
		} catch (Exception e) {
			if (isEditable()) {
				LOGGER.warn("No default property editor available for property [" + property + "]", e);
			}
		}
		if (field != null) {
			return Optional.of(field);
		}
		return super.getDefaultPropertyEditor(property);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.internal.components.DefaultItemListing#getDefaultPropertyValidators(java.lang.Object)
	 */
	@Override
	protected Collection<com.holonplatform.core.Validator<?>> getDefaultPropertyValidators(String property) {
		Collection<com.holonplatform.core.Validator<Object>> validators = getBeanProperty(property)
				.map(p -> p.getValidators()).orElse(Collections.emptySet());
		if (validators.isEmpty()) {
			return Collections.emptySet();
		}
		List<com.holonplatform.core.Validator<?>> vs = new ArrayList<>(validators.size());
		validators.forEach(v -> vs.add(v));
		return vs;
	}

	// --- property set

	/**
	 * Bean {@link PropertyDefinition}.
	 *
	 * @param <T> Bean type
	 * @param <V> Value type
	 */
	public interface BeanPropertyDefinition<T, V> extends PropertyDefinition<T, V> {

		/**
		 * Gets whether the bean property is read-only.
		 * @return whether the bean property is read-only
		 */
		default boolean isReadOnly() {
			return getBeanProperty().map(p -> p.isReadOnly()).orElse(true);
		}

		/**
		 * Get the {@link PathProperty} which corresponds to this bean property, if available.
		 * @return Optional path property
		 */
		Optional<PathProperty<V>> getBeanProperty();

	}

	@SuppressWarnings("serial")
	private final static class BeanGridPropertySet<T> implements com.vaadin.data.PropertySet<T> {

		private final BeanPropertySet<T> beanPropertySet;

		private final Map<String, BeanPropertyDefinition<T, ?>> propertyDefinitions = new LinkedHashMap<>();

		public BeanGridPropertySet(Class<T> beanType) {
			super();
			ObjectUtils.argumentNotNull(beanType, "Bean type must be not null");
			this.beanPropertySet = BeanPropertySet.create(beanType);
			// add bean properties
			this.beanPropertySet.stream().forEach(p -> {
				final BeanPropertyDefinition<T, ?> def = new DefaultBeanPropertyDefinition<>(this, p);
				propertyDefinitions.put(def.getName(), def);
			});
		}

		public BeanPropertySet<T> getBeanPropertySet() {
			return beanPropertySet;
		}

		public <V> String addVirtualProperty(Class<V> type, ValueProvider<T, V> valueProvider, String name) {
			ObjectUtils.argumentNotNull(type, "Type must be not null");
			ObjectUtils.argumentNotNull(valueProvider, "ValueProvider must be not null");
			ObjectUtils.argumentNotNull(name, "Name must be not null");
			propertyDefinitions.put(name, new VirtualBeanPropertyDefinition<>(this, type, valueProvider, name));
			return name;
		}

		/*
		 * (non-Javadoc)
		 * @see com.vaadin.data.PropertySet#getProperties()
		 */
		@Override
		public Stream<PropertyDefinition<T, ?>> getProperties() {
			return propertyDefinitions.entrySet().stream().map(e -> e.getValue());
		}

		public Stream<BeanPropertyDefinition<T, ?>> getPropertyDefinitions() {
			return propertyDefinitions.entrySet().stream().map(e -> e.getValue());
		}

		/*
		 * (non-Javadoc)
		 * @see com.vaadin.data.PropertySet#getProperty(java.lang.String)
		 */
		@Override
		public Optional<PropertyDefinition<T, ?>> getProperty(String name) {
			return Optional.ofNullable(propertyDefinitions.get(name));
		}

		public Optional<BeanPropertyDefinition<T, ?>> getPropertyDefinition(String name) {
			return Optional.ofNullable(propertyDefinitions.get(name));
		}

		private final static class DefaultBeanPropertyDefinition<T, V> implements BeanPropertyDefinition<T, V> {

			private final BeanGridPropertySet<T> propertySet;
			private final PathProperty<V> property;

			public DefaultBeanPropertyDefinition(BeanGridPropertySet<T> propertySet, PathProperty<V> property) {
				super();
				this.propertySet = propertySet;
				this.property = property;
			}

			@Override
			public Optional<PathProperty<V>> getBeanProperty() {
				return Optional.of(property);
			}

			/*
			 * (non-Javadoc)
			 * @see com.vaadin.data.PropertyDefinition#getGetter()
			 */
			@Override
			public ValueProvider<T, V> getGetter() {
				return bean -> propertySet.getBeanPropertySet().read(property, bean);
			}

			/*
			 * (non-Javadoc)
			 * @see com.vaadin.data.PropertyDefinition#getSetter()
			 */
			@Override
			public Optional<Setter<T, V>> getSetter() {
				if (property.isReadOnly()) {
					return Optional.empty();
				}
				return Optional.of((bean, value) -> {
					propertySet.getBeanPropertySet().write(property, value, bean);
				});
			}

			/*
			 * (non-Javadoc)
			 * @see com.vaadin.data.PropertyDefinition#getType()
			 */
			@SuppressWarnings("unchecked")
			@Override
			public Class<V> getType() {
				return (Class<V>) ReflectTools.convertPrimitiveType(property.getType());
			}

			/*
			 * (non-Javadoc)
			 * @see com.vaadin.data.PropertyDefinition#getPropertyHolderType()
			 */
			@Override
			public Class<?> getPropertyHolderType() {
				return propertySet.getBeanPropertySet().getBeanClass();
			}

			/*
			 * (non-Javadoc)
			 * @see com.vaadin.data.PropertyDefinition#getName()
			 */
			@Override
			public String getName() {
				return property.relativeName();
			}

			/*
			 * (non-Javadoc)
			 * @see com.vaadin.data.PropertyDefinition#getCaption()
			 */
			@Override
			public String getCaption() {
				String caption = LocalizationContext.translate(property, true);
				if (caption == null) {
					return getName();
				}
				return caption;
			}

			/*
			 * (non-Javadoc)
			 * @see com.vaadin.data.PropertyDefinition#getPropertySet()
			 */
			@Override
			public PropertySet<T> getPropertySet() {
				return propertySet;
			}

		}

		private final static class VirtualBeanPropertyDefinition<T, V> implements BeanPropertyDefinition<T, V> {

			private final BeanGridPropertySet<T> propertySet;
			private final ValueProvider<T, V> valueProvider;
			private final Class<V> type;
			private final String name;

			public VirtualBeanPropertyDefinition(BeanGridPropertySet<T> propertySet, Class<V> type,
					ValueProvider<T, V> valueProvider, String name) {
				super();
				this.propertySet = propertySet;
				this.type = type;
				this.valueProvider = valueProvider;
				this.name = name;
			}

			/*
			 * (non-Javadoc)
			 * @see com.vaadin.data.PropertyDefinition#getGetter()
			 */
			@Override
			public ValueProvider<T, V> getGetter() {
				return valueProvider;
			}

			/*
			 * (non-Javadoc)
			 * @see com.vaadin.data.PropertyDefinition#getSetter()
			 */
			@Override
			public Optional<Setter<T, V>> getSetter() {
				return Optional.empty();
			}

			/*
			 * (non-Javadoc)
			 * @see com.vaadin.data.PropertyDefinition#getType()
			 */
			@SuppressWarnings("unchecked")
			@Override
			public Class<V> getType() {
				return (Class<V>) ReflectTools.convertPrimitiveType(type);
			}

			/*
			 * (non-Javadoc)
			 * @see com.vaadin.data.PropertyDefinition#getPropertyHolderType()
			 */
			@Override
			public Class<?> getPropertyHolderType() {
				return propertySet.getBeanPropertySet().getBeanClass();
			}

			/*
			 * (non-Javadoc)
			 * @see com.vaadin.data.PropertyDefinition#getName()
			 */
			@Override
			public String getName() {
				return name;
			}

			/*
			 * (non-Javadoc)
			 * @see com.vaadin.data.PropertyDefinition#getCaption()
			 */
			@Override
			public String getCaption() {
				return name;
			}

			/*
			 * (non-Javadoc)
			 * @see com.vaadin.data.PropertyDefinition#getPropertySet()
			 */
			@Override
			public PropertySet<T> getPropertySet() {
				return propertySet;
			}

			/*
			 * (non-Javadoc)
			 * @see
			 * com.holonplatform.vaadin.internal.components.DefaultBeanListing.BeanPropertyDefinition#getBeanProperty()
			 */
			@Override
			public Optional<PathProperty<V>> getBeanProperty() {
				return Optional.empty();
			}

		}

	}

}
