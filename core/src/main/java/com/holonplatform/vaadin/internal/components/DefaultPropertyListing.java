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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import com.holonplatform.core.Path;
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.vaadin.components.PropertyListing;
import com.vaadin.data.PropertyDefinition;
import com.vaadin.data.PropertySet;
import com.vaadin.data.ValueProvider;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FontIcon;
import com.vaadin.server.Setter;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Grid;
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

	private final Map<Property, String> propertyIds;

	public DefaultPropertyListing(Iterable<Property> properties) {
		super(Grid.withPropertySet(new GridPropertySet(properties)));
		propertyIds = new HashMap<>();
		properties.forEach(p -> {
			propertyIds.put(p, generatePropertyId(p));
		});
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.DefaultItemListing#getColumnId(java.lang.Object)
	 */
	@Override
	protected String getColumnId(Property property) {
		return propertyIds.get(property);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.DefaultItemListing#getColumnProperty(java.lang.String)
	 */
	@Override
	protected Property getColumnProperty(String columnId) {
		if (columnId != null) {
			return propertyIds.entrySet().stream().filter(e -> columnId.equals(e.getValue())).findFirst()
					.map(e -> e.getKey()).orElse(null);
		}
		return null;
	}

	private static String generatePropertyId(Property<?> property) {
		return (Path.class.isAssignableFrom(property.getClass())) ? ((Path<?>) property).relativeName()
				: String.valueOf(property.hashCode());
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Optional<ValueProvider<?, ?>> getDefaultPropertyPresenter(Property property) {
		if (property != null) {
			if (FontIcon.class.isAssignableFrom(property.getType())) {
				return Optional.of(v -> ((FontIcon) v).getHtml());
			}
			return Optional.of(v -> property.present(v));
		}
		return super.getDefaultPropertyPresenter(property);
	}

	@Override
	protected Optional<Renderer<?>> getDefaultPropertyRenderer(Property property) {
		if (FontIcon.class.isAssignableFrom(property.getType())) {
			return Optional.of(new HtmlRenderer(""));
		}
		if (ExternalResource.class.isAssignableFrom(property.getType())
				|| ThemeResource.class.isAssignableFrom(property.getType())) {
			return Optional.of(new ImageRenderer());
		}
		return super.getDefaultPropertyRenderer(property);
	}

	@SuppressWarnings("serial")
	private final static class GridPropertySet implements PropertySet<PropertyBox> {

		private final List<PropertyDefinition<PropertyBox, ?>> definitions;

		@SuppressWarnings("unchecked")
		public GridPropertySet(Iterable<Property> properties) {
			super();
			ObjectUtils.argumentNotNull(properties, "Grid property set must be not null");
			definitions = new LinkedList<>();
			properties.forEach(p -> {
				definitions.add(new GridPropertyDefinition<>(this, p));
			});
		}

		/*
		 * (non-Javadoc)
		 * @see com.vaadin.data.PropertySet#getProperties()
		 */
		@Override
		public Stream<PropertyDefinition<PropertyBox, ?>> getProperties() {
			return definitions.stream();
		}

		/*
		 * (non-Javadoc)
		 * @see com.vaadin.data.PropertySet#getProperty(java.lang.String)
		 */
		@Override
		public Optional<PropertyDefinition<PropertyBox, ?>> getProperty(String name) {
			if (name != null) {
				return definitions.stream().filter(d -> name.equals(d.getName())).findFirst();
			}
			return Optional.empty();
		}

	}

	@SuppressWarnings("serial")
	private final static class GridPropertyDefinition<V> implements PropertyDefinition<PropertyBox, V> {

		private final GridPropertySet propertySet;
		private final Property<V> property;
		private final String name;

		public GridPropertyDefinition(GridPropertySet propertySet, Property<V> property) {
			super();
			this.propertySet = propertySet;
			this.property = property;
			this.name = generatePropertyId(property);
		}

		/*
		 * (non-Javadoc)
		 * @see com.vaadin.data.PropertyDefinition#getGetter()
		 */
		@Override
		public ValueProvider<PropertyBox, V> getGetter() {
			return pb -> pb.getValue(property);
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
