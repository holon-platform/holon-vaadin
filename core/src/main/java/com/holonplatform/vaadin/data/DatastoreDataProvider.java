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
package com.holonplatform.vaadin.data;

import java.util.Arrays;

import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.query.QueryConfigurationProvider;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.core.query.QuerySort;
import com.holonplatform.vaadin.internal.data.DefaultDatastoreDataProvider;
import com.holonplatform.vaadin.internal.data.PropertiesItemIdentifier;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.shared.Registration;

/**
 * A {@link DataProvider} backed by a {@link Datastore}.
 * 
 * TODO docs
 *
 * @since 5.0.0
 */
public interface DatastoreDataProvider extends DataProvider<PropertyBox, QueryFilter> {

	/**
	 * Add a {@link QueryConfigurationProvider} to provide additional query parameters, such as {@link QueryFilter}s and
	 * {@link QuerySort}s.
	 * @param queryConfigurationProvider The provider to add (not null)
	 * @return the provider {@link Registration}.
	 */
	Registration addQueryConfigurationProvider(QueryConfigurationProvider queryConfigurationProvider);

	/**
	 * Create a {@link DatastoreDataProvider}.
	 * @param datastore The {@link Datastore} to use (not null)
	 * @param target The query target (not null)
	 * @param propertySet The query projection property set (not null)
	 * @return A new {@link DatastoreDataProvider} instance
	 */
	static DatastoreDataProvider create(Datastore datastore, DataTarget<?> target, PropertySet<?> propertySet) {
		return builder().datastore(datastore).target(target).withProperties(propertySet).build();
	}

	/**
	 * Create a {@link DatastoreDataProvider}.
	 * @param datastore The {@link Datastore} to use (not null)
	 * @param target The query target (not null)
	 * @param propertySet The query projection property set (not null)
	 * @param identifierProperties The properties which act as item identifier(s)
	 * @return A new {@link DatastoreDataProvider} instance
	 */
	static DatastoreDataProvider create(Datastore datastore, DataTarget<?> target, PropertySet<?> propertySet,
			Property<?>... identifierProperties) {
		return builder().datastore(datastore).target(target).withProperties(propertySet)
				.itemIdentifierProvider(new PropertiesItemIdentifier(identifierProperties)).build();
	}

	/**
	 * Create a {@link DatastoreDataProvider}.
	 * @param datastore The {@link Datastore} to use (not null)
	 * @param target The query target (not null)
	 * @param propertySet The query projection property set (not null)
	 * @param itemIdentifier Item identifier provider
	 * @return A new {@link DatastoreDataProvider} instance
	 */
	static DatastoreDataProvider create(Datastore datastore, DataTarget<?> target, PropertySet<?> propertySet,
			ItemIdentifierProvider<PropertyBox, Object> itemIdentifier) {
		return builder().datastore(datastore).target(target).withProperties(propertySet)
				.itemIdentifierProvider(itemIdentifier).build();
	}

	/**
	 * Get a builder to create {@link DatastoreDataProvider} instances.
	 * @return DatastoreDataProvider builder
	 */
	static Builder builder() {
		return new DefaultDatastoreDataProvider.DefaultBuilder();
	}

	/**
	 * Builder to create {@link DatastoreDataProvider} instances.
	 */
	public interface Builder {

		/**
		 * Set the {@link Datastore} to use to perform query operations.
		 * @param datastore the Datastore to set
		 * @return this
		 */
		Builder datastore(Datastore datastore);

		/**
		 * Set the {@link DataTarget} to use.
		 * @param target the target to set
		 * @return this
		 */
		Builder target(DataTarget<?> target);

		/**
		 * Set the {@link ItemIdentifierProvider} to use to obtain the item identifiers.
		 * @param itemIdentifierProvider the provider to set
		 * @return this
		 */
		Builder itemIdentifierProvider(ItemIdentifierProvider<PropertyBox, ?> itemIdentifierProvider);

		/**
		 * Add properties to include in {@link PropertyBox} selection items.
		 * @param <P> Property type
		 * @param properties Properties to add (not null)
		 * @return this
		 */
		@SuppressWarnings("rawtypes")
		<P extends Property> Builder withProperties(Iterable<P> properties);

		/**
		 * Add properties to include in {@link PropertyBox} selection items.
		 * @param properties Properties to add (not null)
		 * @return this
		 */
		@SuppressWarnings("rawtypes")
		default Builder withProperties(Property... properties) {
			ObjectUtils.argumentNotNull(properties, "Properties must be not null");
			return withProperties(Arrays.asList(properties));
		}

		/**
		 * Add a {@link QueryConfigurationProvider} to provide additional query configuration parameters.
		 * @param queryConfigurationProvider the QueryConfigurationProvider to add
		 * @return this
		 */
		Builder withQueryConfigurationProvider(QueryConfigurationProvider queryConfigurationProvider);

		/**
		 * Build the {@link DatastoreDataProvider} instance.
		 * @return the {@link DatastoreDataProvider} instance
		 */
		DatastoreDataProvider build();

	}

}
