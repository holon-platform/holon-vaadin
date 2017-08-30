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
package com.holonplatform.vaadin.components.builders;

import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.vaadin.components.PropertyListing;
import com.holonplatform.vaadin.data.ItemDataProvider;
import com.holonplatform.vaadin.data.ItemDataSource.CommitHandler;
import com.vaadin.data.HasValue;
import com.vaadin.data.ValueProvider;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.Renderer;

/**
 * An {@link ItemListingBuilder} using {@link Property} as item properties and {@link PropertyBox} as item data type.
 *
 * @param <B> Concrete builder type
 * @param <X> Concrete backing component type
 *
 * @since 5.0.0
 */
@SuppressWarnings("rawtypes")
public interface PropertyListingBuilder<B extends PropertyListingBuilder<B, X>, X extends Component>
		extends ItemListingBuilder<PropertyBox, Property, PropertyListing, B, X> {

	/**
	 * Set the items data provider.
	 * @param dataProvider Items data provider (not null)
	 * @param identifierProperties Properties wich act as item identifier
	 * @return this
	 */
	B dataSource(ItemDataProvider<PropertyBox> dataProvider, Property... identifierProperties);

	/**
	 * Use given {@link Datastore} with given <code>dataTarget</code> as items data source.
	 * <p>
	 * A {@link Datastore} based {@link CommitHandler} is also configured by default.
	 * </p>
	 * @param datastore Datastore to use (not null)
	 * @param dataTarget Data target to use to load items (not null)
	 * @param identifierProperties Properties wich act as item identifier
	 * @return this
	 */
	B dataSource(Datastore datastore, DataTarget<?> dataTarget, Property... identifierProperties);

	/**
	 * Builder to create {@link PropertyListing} components using a {@link Grid} as backing component.
	 */
	public interface GridPropertyListingBuilder
			extends PropertyListingBuilder<GridPropertyListingBuilder, Grid<PropertyBox>>,
			BaseGridItemListingBuilder<PropertyBox, Property, PropertyListing, GridPropertyListingBuilder> {

		/**
		 * Set the field to use for given property in edit mode.
		 * @param property Item property to set the editor for (not null)
		 * @param editor Editor field (not null)
		 * @return this
		 */
		<T, E extends HasValue<T> & Component> GridPropertyListingBuilder editor(Property<T> property, final E editor);

		/**
		 * Set a custom {@link Renderer} for given item property.
		 * @param <T> Property type
		 * @param property Item property to set the renderer for (not null)
		 * @param renderer Renderer to use
		 * @return this
		 */
		<T> GridPropertyListingBuilder render(Property<T> property, Renderer<? super T> renderer);

		/**
		 * Set a custom {@link Renderer} and presentation provider for given item property.
		 * @param <T> Property type
		 * @param <P> Presentation value type
		 * @param property Item property to set the renderer for
		 * @param presentationProvider Presentation provider
		 * @param renderer Renderer to use
		 * @return this
		 */
		<T, P> GridPropertyListingBuilder render(Property<T> property, ValueProvider<T, P> presentationProvider,
				Renderer<? super P> renderer);

	}

}
