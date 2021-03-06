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
package com.holonplatform.vaadin.internal.components.builders;

import java.util.Arrays;

import com.holonplatform.core.Path;
import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.property.PropertyValueProvider;
import com.holonplatform.core.property.VirtualProperty;
import com.holonplatform.vaadin.components.ItemListing;
import com.holonplatform.vaadin.components.builders.PropertyListingBuilder.BaseGridPropertyListingBuilder;
import com.holonplatform.vaadin.components.builders.VirtualPropertyColumnBuilder;
import com.holonplatform.vaadin.data.ItemDataProvider;
import com.holonplatform.vaadin.data.ItemDataSource.CommitHandler;
import com.holonplatform.vaadin.internal.components.DefaultPropertyListing;
import com.vaadin.data.HasValue;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueProvider;
import com.vaadin.ui.Component;
import com.vaadin.ui.renderers.Renderer;

/**
 * Abstract {@link BaseGridPropertyListingBuilder} implementation.
 * 
 * @param <C> Actual listing type
 * @param <I> Concrete instance type
 * @param <B> Concrete builder type
 *
 * @since 5.1.0
 */
@SuppressWarnings("rawtypes")
public abstract class AbstractGridPropertyListingBuilder<C extends ItemListing<PropertyBox, Property>, I extends DefaultPropertyListing, B extends BaseGridPropertyListingBuilder<C, B>>
		extends AbstractGridItemListingBuilder<PropertyBox, Property, C, I, B>
		implements BaseGridPropertyListingBuilder<C, B> {

	/**
	 * The listing property set
	 */
	private final Iterable<Property<?>> properties;

	/**
	 * Constructor.
	 * @param <P> Property type
	 * @param instance The instance to build (not null)
	 * @param properties The listing property set (not null)
	 */
	@SuppressWarnings("unchecked")
	public <P extends Property<?>> AbstractGridPropertyListingBuilder(I instance, Iterable<P> properties) {
		super(instance, Property.class);
		this.properties = (Iterable<Property<?>>) properties;
		// setup datasource
		properties.forEach(p -> {
			dataSourceBuilder.withProperty(p, p.getType(), false);

			dataSourceBuilder.propertyId(p, getInstance().getColumnId(p));

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
	@SuppressWarnings("unchecked")
	@Override
	public B dataSource(ItemDataProvider<PropertyBox> dataProvider, final Property... identifierProperties) {
		ObjectUtils.argumentNotNull(identifierProperties, "Identifier properties must be not null");
		if (identifierProperties.length == 0) {
			throw new IllegalArgumentException("Identifier properties must be not empty");
		}

		final PropertySet<?> propertySet = PropertySet.builderOf(identifierProperties)
				.identifiers(Arrays.asList(identifierProperties)).build();

		return dataSource(dataProvider, item -> {
			PropertyBox.Builder builder = PropertyBox.builder(propertySet);
			for (Property p : identifierProperties) {
				item.getValueIfPresent(p).ifPresent(v -> builder.set(p, v));
			}
			return builder.build();
		});
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.PropertyListingBuilder#dataSource(com.holonplatform.core.datastore.
	 * Datastore, com.holonplatform.core.datastore.DataTarget)
	 */
	@Override
	public B dataSource(Datastore datastore, DataTarget<?> dataTarget) {
		// Use item listing property set
		PropertySet<?> propertySet = (properties instanceof PropertySet) ? (PropertySet<?>) properties
				: PropertySet.of(properties);
		// set data source
		dataSource(ItemDataProvider.create(datastore, dataTarget, propertySet));
		// set commit handler
		commitHandler(CommitHandler.datastore(datastore, dataTarget));
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.PropertyListingBuilder#dataSource(com.holonplatform.core.datastore.
	 * Datastore, com.holonplatform.core.datastore.DataTarget, com.holonplatform.core.property.Property[])
	 */
	@Override
	public B dataSource(Datastore datastore, DataTarget<?> dataTarget, Property... identifierProperties) {
		if (identifierProperties == null || identifierProperties.length == 0) {
			return dataSource(datastore, dataTarget);
		}
		// set given identifier properties ad property set identifiers
		PropertySet<?> propertySet = PropertySet.builder().add(properties)
				.identifiers(Arrays.asList(identifierProperties)).build();
		// set data source
		dataSource(ItemDataProvider.create(datastore, dataTarget, propertySet));
		// set commit handler
		commitHandler(CommitHandler.datastore(datastore, dataTarget));
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.PropertyListingBuilder.GridPropertyListingBuilder#editor(com.
	 * holonplatform.core.property.Property, com.vaadin.data.HasValue)
	 */
	@Override
	public <T, E extends HasValue<T> & Component> B editor(Property<T> property, E editor) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		ObjectUtils.argumentNotNull(editor, "Editor field must be not null");
		getInstance().getPropertyColumn(property).setEditor(editor);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.ItemListingBuilder.BaseGridItemListingBuilder#withValidator(com.
	 * vaadin.data.Validator)
	 */
	@Override
	public B withValidator(Validator<PropertyBox> validator) {
		ObjectUtils.argumentNotNull(validator, "Validator must be not null");
		getInstance().addValidator(validator);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.PropertyListingBuilder.GridPropertyListingBuilder#withValidator(com.
	 * holonplatform.core.property.Property, com.vaadin.data.Validator)
	 */
	@Override
	public <T> B withValidator(Property<T> property, Validator<T> validator) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		ObjectUtils.argumentNotNull(validator, "Validator must be not null");
		getInstance().getPropertyColumn(property).addValidator(validator);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.PropertyListingBuilder.GridPropertyListingBuilder#renderer(com.
	 * holonframework.core.property.Property, com.vaadin.ui.renderers.Renderer)
	 */
	@Override
	public <T> B render(Property<T> property, Renderer<? super T> renderer) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		getInstance().getPropertyColumn(property).setRenderer(renderer);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.PropertyListingBuilder.GridPropertyListingBuilder#render(com.
	 * holonplatform.core.property.Property, com.vaadin.data.ValueProvider, com.vaadin.ui.renderers.Renderer)
	 */
	@Override
	public <T, P> B render(Property<T> property, ValueProvider<T, P> presentationProvider,
			Renderer<? super P> renderer) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		getInstance().getPropertyColumn(property).setPresentationProvider(presentationProvider);
		getInstance().getPropertyColumn(property).setRenderer(renderer);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.PropertyListingBuilder.BaseGridPropertyListingBuilder#
	 * withVirtualProperty(com.holonplatform.core.property.VirtualProperty)
	 */
	@Override
	public <T> VirtualPropertyColumnBuilder<T, PropertyBox, Property, C, B> withVirtualProperty(
			VirtualProperty<T> property) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		getInstance().addColumn(property);
		return new DefaultVirtualPropertyColumnBuilder<>(builder(), getInstance(), property);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.PropertyListingBuilder.BaseGridPropertyListingBuilder#
	 * withVirtualProperty(java.lang.Class, com.holonplatform.core.property.PropertyValueProvider)
	 */
	@Override
	public <T> VirtualPropertyColumnBuilder<T, PropertyBox, Property, C, B> withVirtualProperty(Class<T> type,
			PropertyValueProvider<T> valueProvider) {
		return withVirtualProperty(VirtualProperty.create(type, valueProvider));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.PropertyListingBuilder.BaseGridPropertyListingBuilder#
	 * withVirtualProperty(java.lang.Class, java.lang.String, com.holonplatform.core.property.PropertyValueProvider)
	 */
	@Override
	public <T> VirtualPropertyColumnBuilder<T, PropertyBox, Property, C, B> withVirtualProperty(Class<T> type,
			String name, PropertyValueProvider<T> valueProvider) {
		return withVirtualProperty(VirtualProperty.create(type, valueProvider).name(name));
	}

}
