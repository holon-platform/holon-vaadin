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
package com.holonplatform.vaadin.components.builders;

import com.holonplatform.core.Validator;
import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.property.PropertyValueProvider;
import com.holonplatform.core.property.VirtualProperty;
import com.holonplatform.core.query.QueryConfigurationProvider;
import com.holonplatform.vaadin.components.ItemListing;
import com.holonplatform.vaadin.components.PropertyListing;
import com.holonplatform.vaadin.data.ItemDataProvider;
import com.holonplatform.vaadin.data.ItemDataSource.CommitHandler;
import com.holonplatform.vaadin.internal.components.ValidatorWrapper;
import com.vaadin.data.HasValue;
import com.vaadin.data.ValueProvider;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.Renderer;

/**
 * An {@link ItemListingBuilder} using {@link Property} as item properties and {@link PropertyBox} as item data type.
 *
 * @param <C> Actual listing type
 * @param <B> Concrete builder type
 * @param <X> Concrete backing component type
 *
 * @since 5.0.0
 */
@SuppressWarnings("rawtypes")
public interface PropertyListingBuilder<C extends ItemListing<PropertyBox, Property>, B extends PropertyListingBuilder<C, B, X>, X extends Component>
		extends ItemListingBuilder<PropertyBox, Property, C, B, X> {

	/**
	 * Set the items data provider.
	 * @param dataProvider Items data provider (not null)
	 * @param identifierProperties Properties wich act as item identifier
	 * @return this
	 * @deprecated Use {@link #dataSource(ItemDataProvider, com.holonplatform.vaadin.data.ItemIdentifierProvider)} to
	 *             provide a custom item identifier provider. By default, the identifier properties of the item listing
	 *             property set will be used to identify each {@link PropertyBox} item.
	 */
	@Deprecated
	B dataSource(ItemDataProvider<PropertyBox> dataProvider, Property... identifierProperties);

	/**
	 * Set given {@link Datastore} with the provided <code>dataTarget</code> as items data source. {@link PropertyBox}
	 * items will be fetched from the persistence source using a properly configured Datastore query, with given
	 * <code>dataTarget</code> representing the persistent entity to query.
	 * <p>
	 * This data source supports {@link QueryConfigurationProvider}s to provide additional query configuration. A
	 * {@link QueryConfigurationProvider} can be added using
	 * {@link #withQueryConfigurationProvider(QueryConfigurationProvider)}.
	 * </p>
	 * <p>
	 * If the item listing was built on a {@link PropertySet} which provides <em>identifier</em> properties (see
	 * {@link PropertySet#getIdentifiers()}), the identifier properties will be used as {@link PropertyBox} item
	 * identifiers, i.e. the <code>equals</code> and <code>hashCode</code> logic of the items will be implemented
	 * accordingly to the values of the identifier properties.
	 * </p>
	 * <p>
	 * A {@link Datastore} based {@link CommitHandler} is also configured by default.
	 * </p>
	 * @param datastore The {@link Datastore} to use (not null)
	 * @param dataTarget The {@link DataTarget} to use as query target (not null)
	 * @return this
	 * @see GridPropertyListingBuilder#dataSource(Datastore, DataTarget, Property...)
	 */
	B dataSource(Datastore datastore, DataTarget<?> dataTarget);

	/**
	 * Set given {@link Datastore} with the provided <code>dataTarget</code> as items data source, using given
	 * <code>identifierProperties</code> as item identifiers. {@link PropertyBox} items will be fetched from the
	 * persistence source using a properly configured Datastore query, with given <code>dataTarget</code> representing
	 * the persistent entity to query.
	 * <p>
	 * The provided identifier properties will be used as {@link PropertyBox} item identifiers, i.e. the
	 * <code>equals</code> and <code>hashCode</code> logic of the items will be implemented accordingly to the values of
	 * the identifier properties.
	 * </p>
	 * <p>
	 * This data source supports {@link QueryConfigurationProvider}s to provide additional query configuration. A
	 * {@link QueryConfigurationProvider} can be added using
	 * {@link #withQueryConfigurationProvider(QueryConfigurationProvider)}.
	 * </p>
	 * <p>
	 * A {@link Datastore} based {@link CommitHandler} is also configured by default.
	 * </p>
	 * @param datastore Datastore to use (not null)
	 * @param dataTarget Data target to use to load items (not null)
	 * @param identifierProperties Properties to use as item identifiers
	 * @return this
	 */
	B dataSource(Datastore datastore, DataTarget<?> dataTarget, Property... identifierProperties);

	/**
	 * Builder to create {@link ItemListing} component with {@link Property} as property type, {@link PropertyBox} as
	 * item type and using a {@link Grid} as backing component.
	 * 
	 * @param <C> Actual listing type
	 * @param <B> Concrete builder type
	 */
	public interface BaseGridPropertyListingBuilder<C extends ItemListing<PropertyBox, Property>, B extends BaseGridPropertyListingBuilder<C, B>>
			extends PropertyListingBuilder<C, B, Grid<PropertyBox>>,
			BaseGridItemListingBuilder<PropertyBox, Property, C, B> {

		/**
		 * Set the field to use for given property in edit mode.
		 * @param <T> Property type
		 * @param <E> Editor field type
		 * @param property Item property to set the editor for (not null)
		 * @param editor Editor field (not null)
		 * @return this
		 */
		<T, E extends HasValue<T> & Component> B editor(Property<T> property, final E editor);

		/**
		 * Adds a {@link Validator} to the field bound to given <code>property</code> in the item listing editor.
		 * @param <T> Property type
		 * @param property Property (not null)
		 * @param validator Validator to add (not null)
		 * @return this
		 */
		default <T> B withValidator(Property<T> property, Validator<T> validator) {
			return withValidator(property, new ValidatorWrapper<>(validator));
		}

		/**
		 * Adds a {@link com.vaadin.data.Validator} to the field bound to given <code>property</code> in the item
		 * listing editor.
		 * @param <T> Property type
		 * @param property Property (not null)
		 * @param validator Validator to add (not null)
		 * @return this
		 */
		<T> B withValidator(Property<T> property, com.vaadin.data.Validator<T> validator);

		/**
		 * Set a custom {@link Renderer} for given item property.
		 * @param <T> Property type
		 * @param property Item property to set the renderer for (not null)
		 * @param renderer Renderer to use
		 * @return this
		 */
		<T> B render(Property<T> property, Renderer<? super T> renderer);

		/**
		 * Set a custom {@link Renderer} and presentation provider for given item property.
		 * @param <T> Property type
		 * @param <P> Presentation value type
		 * @param property Item property to set the renderer for
		 * @param presentationProvider Presentation provider
		 * @param renderer Renderer to use
		 * @return this
		 */
		<T, P> B render(Property<T> property, ValueProvider<T, P> presentationProvider, Renderer<? super P> renderer);

		/**
		 * Add a virtual property to the listing. The returned {@link VirtualPropertyColumnBuilder} allows to configure
		 * the column bound to the property and to add/append the column to listing columns.
		 * <p>
		 * To add the virtual property as listing column and return back to the parent listing builder, the
		 * {@link VirtualPropertyColumnBuilder#add()}, {@link VirtualPropertyColumnBuilder#addBefore(Property)},
		 * {@link VirtualPropertyColumnBuilder#addAfter(Property)} or {@link VirtualPropertyColumnBuilder#append()}
		 * methods should be used.
		 * </p>
		 * @param <T> Property type
		 * @param property The {@link VirtualProperty} to use to provide the property value (not null)
		 * @return The {@link VirtualPropertyColumnBuilder} for this virtual property
		 * @since 5.1.4
		 */
		<T> VirtualPropertyColumnBuilder<T, PropertyBox, Property, C, B> withVirtualProperty(
				VirtualProperty<T> property);

		/**
		 * Add a virtual property to the listing, providing the virtual property type and the
		 * {@link PropertyValueProvider} to provide the virtual property value for each {@link PropertyBox} listing
		 * item. The returned {@link VirtualPropertyColumnBuilder} allows to configure the column bound to the property
		 * and to add/append the column to listing columns.
		 * <p>
		 * To add the virtual property as listing column and return back to the parent listing builder, the
		 * {@link VirtualPropertyColumnBuilder#add()}, {@link VirtualPropertyColumnBuilder#addBefore(Property)},
		 * {@link VirtualPropertyColumnBuilder#addAfter(Property)} or {@link VirtualPropertyColumnBuilder#append()}
		 * methods should be used.
		 * </p>
		 * @param <T> Property type
		 * @param type Virtual property type (not null)
		 * @param valueProvider Property value provider (not null)
		 * @return The {@link VirtualPropertyColumnBuilder} for this virtual property
		 * @since 5.1.4
		 */
		<T> VirtualPropertyColumnBuilder<T, PropertyBox, Property, C, B> withVirtualProperty(Class<T> type,
				PropertyValueProvider<T> valueProvider);

		/**
		 * Add a virtual property to the listing, providing the virtual property type, the virtual property name and the
		 * {@link PropertyValueProvider} to provide the virtual property value for each {@link PropertyBox} listing
		 * item. The returned {@link VirtualPropertyColumnBuilder} allows to configure the column bound to the property
		 * and to add/append the column to listing columns.
		 * <p>
		 * To add the virtual property as listing column and return back to the parent listing builder, the
		 * {@link VirtualPropertyColumnBuilder#add()}, {@link VirtualPropertyColumnBuilder#addBefore(Property)},
		 * {@link VirtualPropertyColumnBuilder#addAfter(Property)} or {@link VirtualPropertyColumnBuilder#append()}
		 * methods should be used.
		 * </p>
		 * @param <T> Property type
		 * @param type Virtual property type (not null)
		 * @param name Virtual property name, which will be used as column id (not null)
		 * @param valueProvider Property value provider (not null)
		 * @return The {@link VirtualPropertyColumnBuilder} for this virtual property
		 * @since 5.1.4
		 */
		<T> VirtualPropertyColumnBuilder<T, PropertyBox, Property, C, B> withVirtualProperty(Class<T> type, String name,
				PropertyValueProvider<T> valueProvider);

	}

	/**
	 * Builder to create a {@link PropertyListing} component using a {@link Grid} as backing component.
	 */
	public interface GridPropertyListingBuilder
			extends BaseGridPropertyListingBuilder<PropertyListing, GridPropertyListingBuilder> {

	}

}
