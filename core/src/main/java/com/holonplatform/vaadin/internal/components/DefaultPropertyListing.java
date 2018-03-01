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

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.core.internal.Logger;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertyValueProvider;
import com.holonplatform.core.property.VirtualProperty;
import com.holonplatform.vaadin.components.Field;
import com.holonplatform.vaadin.components.PropertyListing;
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
 * Default {@link PropertyListing} implementation.
 *
 * @since 5.0.0
 */
@SuppressWarnings("rawtypes")
public class DefaultPropertyListing extends DefaultItemListing<PropertyBox, Property> implements PropertyListing {

	private static final long serialVersionUID = 681884060927291257L;

	private static final Logger LOGGER = VaadinLogger.create();

	/**
	 * Grid property set
	 */
	private final GridPropertySet propertySet;

	/**
	 * Constructor.
	 * @param properties Listing property set (not null)
	 */
	public <P extends Property<?>> DefaultPropertyListing(Iterable<P> properties) {
		super();
		ObjectUtils.argumentNotNull(properties, "Listing property set must be not null");
		propertySet = new GridPropertySet(properties);
		initGrid(Grid.withPropertySet(propertySet));
	}

	/**
	 * Get the {@link Property} set to which this listing is bound.
	 * @return The listing property set
	 */
	public Set<Property> getPropertySet() {
		return propertySet.getPropertySet();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertySetBound#getProperties()
	 */
	@Override
	public Iterable<Property> getProperties() {
		return getPropertySet();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertySetBound#hasProperty(com.holonplatform.core.property.Property)
	 */
	@Override
	public boolean hasProperty(Property<?> property) {
		return (property != null) && getPropertySet().contains(property);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertySetBound#propertyStream()
	 */
	@Override
	public Stream<Property> propertyStream() {
		return getPropertySet().stream();
	}

	/**
	 * Adds a column definition which uses given {@link VirtualProperty} value provider to provide the column contents.
	 * @param property Column property
	 * @return if the property was not already present in listing property set and it was added to the set, return the
	 *         column name, <code>null</code> otherwise
	 */
	public <T> String addColumn(VirtualProperty<T> property) {
		String name = propertySet.addVirtualProperty(property);
		if (name != null) {
			getGrid().addColumn(name);
		}
		return name;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.DefaultItemListing#getColumnId(java.lang.Object)
	 */
	@Override
	public String getColumnId(Property property) {
		return propertySet.getPropertyName(property);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.DefaultItemListing#getColumnProperty(java.lang.String)
	 */
	@Override
	protected Property getColumnProperty(String columnId) {
		return propertySet.getPropertyByName(columnId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.DefaultItemListing#setupVisibileColumns(java.lang.Iterable)
	 */
	@Override
	protected void setupVisibileColumns(Iterable<? extends Property> visibleColumns) {
		ObjectUtils.argumentNotNull(visibleColumns, "Visible columns must be not null");

		final List<String> ids = new LinkedList<>();
		final Set<Property> propertySet = getPropertySet();

		for (Property visibleColumn : visibleColumns) {
			// check virtual columns
			if (!propertySet.contains(visibleColumn)) {
				// add as virtual column if it is a VirtualProperty
				if (visibleColumn instanceof VirtualProperty) {
					addColumn((VirtualProperty<?>) visibleColumn);
				}
			}

			final String columnId = getColumnId(visibleColumn);
			setupPropertyColumn(visibleColumn, getGrid().getColumn(columnId));
			ids.add(columnId);
		}

		getGrid().setColumns(ids.toArray(new String[ids.size()]));
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Optional<ValueProvider<?, ?>> getDefaultPropertyPresenter(Property property) {
		if (property != null) {
			if (Component.class.isAssignableFrom(property.getType())) {
				return Optional.empty();
			}
			if (FontIcon.class.isAssignableFrom(property.getType())) {
				return Optional.of(v -> ((FontIcon) v).getHtml());
			}
			return Optional.of(v -> property.present(v));
		}
		return super.getDefaultPropertyPresenter(property);
	}

	@Override
	protected Optional<Renderer<?>> getDefaultPropertyRenderer(Property property) {
		if (Component.class.isAssignableFrom(property.getType())) {
			return Optional.of(new ComponentRenderer());
		}
		if (FontIcon.class.isAssignableFrom(property.getType())) {
			return Optional.of(new HtmlRenderer(""));
		}
		if (ExternalResource.class.isAssignableFrom(property.getType())
				|| ThemeResource.class.isAssignableFrom(property.getType())) {
			return Optional.of(new ImageRenderer());
		}
		return super.getDefaultPropertyRenderer(property);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.DefaultItemListing#getDefaultPropertyEditor(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected <E extends HasValue<?> & Component> Optional<E> getDefaultPropertyEditor(Property property) {
		try {
			return property.renderIfAvailable(Field.class);
		} catch (Exception e) {
			if (isEditable()) {
				LOGGER.warn("No default property editor available for property [" + property + "]", e);
			}
			return Optional.empty();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.internal.components.DefaultItemListing#getDefaultPropertyValidators(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected Collection<com.holonplatform.core.Validator<?>> getDefaultPropertyValidators(Property property) {
		return property.getValidators();
	}

	@SuppressWarnings("serial")
	private final static class GridPropertySet implements PropertySet<PropertyBox> {

		private final Map<Property, GridPropertyDefinition> propertyDefinitions = new LinkedHashMap<>();

		private final Map<String, Integer> generatedPropertyIds = new HashMap<>();

		public <P extends Property<?>> GridPropertySet(Iterable<P> properties) {
			super();
			ObjectUtils.argumentNotNull(properties, "Grid property set must be not null");
			properties.forEach(p -> {
				propertyDefinitions.put(p,
						new GridPropertyDefinition<>(this, (Property<?>) p, generatePropertyName(p)));
			});
		}

		public Set<Property> getPropertySet() {
			return propertyDefinitions.keySet();
		}

		/**
		 * Get the column id associated with given <code>property</code>.
		 * @param property The property for which to obtain the column name
		 * @return The column name, or <code>null</code> if the property is not part of the property set
		 */
		public String getPropertyName(Property<?> property) {
			if (property != null && propertyDefinitions.containsKey(property)) {
				return propertyDefinitions.get(property).getName();
			}
			return null;
		}

		/**
		 * Get the {@link Property} which corresponds to given column name.
		 * @param name Column name
		 * @return The {@link Property} which corresponds to given column name, <code>null</code> if none
		 */
		public Property<?> getPropertyByName(String name) {
			if (name == null) {
				return null;
			}
			return propertyDefinitions.entrySet().stream().filter(e -> name.equals(e.getValue().getName())).findFirst()
					.map(d -> d.getKey()).orElse(null);
		}

		/*
		 * (non-Javadoc)
		 * @see com.vaadin.data.PropertySet#getProperties()
		 */
		@SuppressWarnings("unchecked")
		@Override
		public Stream<PropertyDefinition<PropertyBox, ?>> getProperties() {
			return propertyDefinitions.entrySet().stream().map(e -> e.getValue());
		}

		/*
		 * (non-Javadoc)
		 * @see com.vaadin.data.PropertySet#getProperty(java.lang.String)
		 */
		@SuppressWarnings("unchecked")
		@Override
		public Optional<PropertyDefinition<PropertyBox, ?>> getProperty(String name) {
			if (name != null) {
				return propertyDefinitions.entrySet().stream().filter(e -> name.equals(e.getValue().getName()))
						.findFirst().map(d -> d.getValue());
			}
			return Optional.empty();
		}

		/**
		 * Add a property definition using given {@link VirtualProperty}, if not already present.
		 * @param property Property to add
		 * @return Column name, or <code>null</code> if the property column was already present
		 */
		public <V> String addVirtualProperty(VirtualProperty<V> property) {
			if (property != null && !propertyDefinitions.containsKey(property)) {
				GridPropertyDefinition definition = new GridPropertyDefinition<>(this, property,
						generatePropertyName(property), property.getValueProvider());
				propertyDefinitions.put(property, definition);
				return definition.getName();
			}
			return null;
		}

		private String generatePropertyName(Property<?> property) {
			String propertyName = property.getName();
			if (propertyName == null) {
				propertyName = "property";
			}
			// check duplicates
			Integer count = generatedPropertyIds.get(propertyName);
			if (count != null && count > 0) {
				int sequence = count.intValue() + 1;
				generatedPropertyIds.put(propertyName, sequence);
				return propertyName + sequence;
			} else {
				generatedPropertyIds.put(propertyName, 1);
				return propertyName;
			}
		}

	}

	@SuppressWarnings("serial")
	private final static class GridPropertyDefinition<V> implements PropertyDefinition<PropertyBox, V> {

		private final GridPropertySet propertySet;
		private final Property<V> property;
		private final PropertyValueProvider<V> valueProvider;
		private final String name;

		public GridPropertyDefinition(GridPropertySet propertySet, Property<V> property, String name) {
			this(propertySet, property, name, null);
		}

		public GridPropertyDefinition(GridPropertySet propertySet, Property<V> property, String name,
				PropertyValueProvider<V> valueProvider) {
			super();
			this.propertySet = propertySet;
			this.property = property;
			this.valueProvider = valueProvider;
			this.name = name;
		}

		/**
		 * Get the property bound to this definition
		 * @return the property
		 */
		@SuppressWarnings("unused")
		public Property<V> getProperty() {
			return property;
		}

		/*
		 * (non-Javadoc)
		 * @see com.vaadin.data.PropertyDefinition#getGetter()
		 */
		@Override
		public ValueProvider<PropertyBox, V> getGetter() {
			return pb -> (valueProvider != null) ? valueProvider.getPropertyValue(pb) : pb.getValue(property);
		}

		/*
		 * (non-Javadoc)
		 * @see com.vaadin.data.PropertyDefinition#getSetter()
		 */
		@Override
		public Optional<Setter<PropertyBox, V>> getSetter() {
			if (property.isReadOnly()) {
				return Optional.empty();
			}
			return Optional.of((pb, value) -> {
				pb.setValue(property, value);
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
			return PropertyBox.class;
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
		public PropertySet<PropertyBox> getPropertySet() {
			return propertySet;
		}

	}

}
