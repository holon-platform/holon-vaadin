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
package com.holonplatform.vaadin.internal.components.builders;

import com.holonplatform.core.Path;
import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.vaadin.components.PropertyListing;
import com.holonplatform.vaadin.components.builders.PropertyListingBuilder.GridPropertyListingBuilder;
import com.holonplatform.vaadin.data.ItemDataProvider;
import com.holonplatform.vaadin.internal.components.DefaultPropertyListing;
import com.holonplatform.vaadin.internal.data.DatastoreCommitHandler;
import com.holonplatform.vaadin.internal.data.DatastoreItemDataProvider;
import com.holonplatform.vaadin.internal.data.PropertiesItemIdentifier;
import com.vaadin.data.ValueProvider;
import com.vaadin.ui.renderers.Renderer;

/**
 * Defauklt {@link DefaultGridPropertyListingBuilder} implementation.
 *
 * @since 5.0.0
 */
@SuppressWarnings("rawtypes")
public class DefaultGridPropertyListingBuilder extends
		AbstractGridItemListingBuilder<PropertyBox, Property, PropertyListing, DefaultPropertyListing, GridPropertyListingBuilder>
		implements GridPropertyListingBuilder {

	private final Iterable<Property> properties;

	@SuppressWarnings("unchecked")
	public <P extends Property<?>> DefaultGridPropertyListingBuilder(Iterable<P> properties) {
		super(new DefaultPropertyListing((Iterable<Property>) properties));
		this.properties = (Iterable<Property>) properties;
		// setup datasource
		properties.forEach(p -> {
			dataSourceBuilder.withProperty(p, p.getType());
			if (p.isReadOnly()) {
				dataSourceBuilder.readOnly(p, true);
			}
			if (Path.class.isAssignableFrom(p.getClass())) {
				dataSourceBuilder.sortable(p, true);
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.PropertyListingBuilder#dataSource(com.holonplatform.vaadin.data.
	 * ItemDataProvider, com.holonplatform.core.property.Property[])
	 */
	@Override
	public GridPropertyListingBuilder dataSource(ItemDataProvider<PropertyBox> dataProvider,
			Property... identifierProperties) {
		ObjectUtils.argumentNotNull(identifierProperties, "Identifier properties must be not null");
		if (identifierProperties.length == 0) {
			throw new IllegalArgumentException("Identifier properties must be not empty");
		}
		return dataSource(dataProvider, new PropertiesItemIdentifier(identifierProperties));
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.PropertyListingBuilder#dataSource(com.holonplatform.core.datastore.
	 * Datastore, com.holonplatform.core.datastore.DataTarget, com.holonplatform.core.property.Property[])
	 */
	@Override
	public GridPropertyListingBuilder dataSource(Datastore datastore, DataTarget<?> dataTarget,
			Property... identifierProperties) {
		commitHandler(new DatastoreCommitHandler(datastore, dataTarget));
		dataSource(new DatastoreItemDataProvider(datastore, dataTarget, PropertySet.of(properties)),
				identifierProperties);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.PropertyListingBuilder.GridPropertyListingBuilder#converter(com.
	 * holonplatform.core.property.Property, com.vaadin.data.ValueProvider)
	 */
	@Override
	public <T> GridPropertyListingBuilder converter(Property<T> property, ValueProvider<?, T> converter) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		getInstance().getPropertyColumn(property).setPresentationProvider(converter);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.PropertyListingBuilder.GridPropertyListingBuilder#renderer(com.
	 * holonframework.core.property.Property, com.vaadin.ui.renderers.Renderer)
	 */
	@Override
	public <T> GridPropertyListingBuilder renderer(Property<T> property, Renderer<? super T> renderer) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		getInstance().getPropertyColumn(property).setRenderer(renderer);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentBuilder#build(com.vaadin.ui.
	 * AbstractComponent)
	 */
	@Override
	protected PropertyListing build(DefaultPropertyListing instance) {
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentConfigurator#builder()
	 */
	@Override
	protected GridPropertyListingBuilder builder() {
		return this;
	}

}
