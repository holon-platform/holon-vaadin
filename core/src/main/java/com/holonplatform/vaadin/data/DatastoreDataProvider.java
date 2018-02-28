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
import com.holonplatform.vaadin.internal.data.DefaultDatastoreDataProvider;
import com.vaadin.data.provider.DataProvider;

/**
 * A {@link DataProvider} backed by a {@link Datastore}.
 * <p>
 * The data type is {@link PropertyBox} and the supported filter type is {@link QueryFilter}.
 * </p>
 * <p>
 * Supports {@link QueryConfigurationProvider} registration through {@link QueryConfigurationProviderSupport}.
 * </p>
 *
 * @since 5.0.0
 */
public interface DatastoreDataProvider
		extends DataProvider<PropertyBox, QueryFilter>, QueryConfigurationProviderSupport {

	/**
	 * Create a new {@link DatastoreDataProvider}.
	 * <p>
	 * The {@link PropertyBox} items will be fetched from the persistence source using a properly configured Datastore
	 * query, with given <code>dataTarget</code> representing the persistent entity to query. The {@link PropertyBox}
	 * items will be built using given <code>propertySet</code>.
	 * </p>
	 * <p>
	 * The {@link DataProvider#getId(Object)} method will simply return the {@link PropertyBox} item instance, relying
	 * on the {@link PropertyBox} <code>equals</code> and <code>hashCode</code> logic to identify each item.
	 * </p>
	 * <p>
	 * If the given {@link PropertySet} provides <em>identifier</em> properties (see
	 * {@link PropertySet#getIdentifiers()}), the identifier properties will be used as {@link PropertyBox} item
	 * identifiers, i.e. the <code>equals</code> and <code>hashCode</code> logic of the items will be implemented
	 * accordingly to the values of the identifier properties.
	 * </p>
	 * @param datastore The {@link Datastore} to use (not null)
	 * @param target The data target to use (not null)
	 * @param propertySet The query projection property set (not null)
	 * @return A new {@link DatastoreDataProvider} instance
	 */
	static DatastoreDataProvider create(Datastore datastore, DataTarget<?> target, PropertySet<?> propertySet) {
		return builder().datastore(datastore).target(target).withProperties(propertySet).build();
	}

	/**
	 * Create a {@link DatastoreDataProvider} and use given <code>identifierProperties</code> as {@link PropertyBox}
	 * items identifiers.
	 * <p>
	 * The {@link PropertyBox} items will be fetched from the persistence source using a properly configured Datastore
	 * query, with given <code>dataTarget</code> representing the persistent entity to query. The {@link PropertyBox}
	 * items will be built using given <code>propertySet</code>.
	 * </p>
	 * <p>
	 * The provided identifier properties will be used as {@link PropertyBox} item identifiers, i.e. the
	 * <code>equals</code> and <code>hashCode</code> logic of the items will be implemented accordingly to the values of
	 * the identifier properties.
	 * </p>
	 * @param datastore The {@link Datastore} to use (not null)
	 * @param target The data target to use (not null)
	 * @param propertySet The query projection property set (not null)
	 * @param identifierProperties Properties to use as item identifiers
	 * @return A new {@link DatastoreDataProvider} instance
	 */
	static DatastoreDataProvider create(Datastore datastore, DataTarget<?> target, PropertySet<?> propertySet,
			Property<?>... identifierProperties) {
		if (identifierProperties == null || identifierProperties.length == 0) {
			return create(datastore, target, propertySet);
		}

		// set given identifier properties as property set identifiers
		PropertySet<?> propertySetWithIds = PropertySet.builder().add(propertySet)
				.identifiers(Arrays.asList(identifierProperties)).build();

		return builder().datastore(datastore).target(target).withProperties(propertySetWithIds).build();
	}

	/**
	 * Create a {@link DatastoreDataProvider}, using given {@link ItemIdentifierProvider} to obtain the
	 * {@link PropertyBox} item identifier.
	 * <p>
	 * The {@link PropertyBox} items will be fetched from the persistence source using a properly configured Datastore
	 * query, with given <code>dataTarget</code> representing the persistent entity to query. The {@link PropertyBox}
	 * items will be built using given <code>propertySet</code>.
	 * </p>
	 * <p>
	 * The given <code>itemIdentifier</code> will be used to provide the item identifier through the
	 * {@link DataProvider#getId(Object)} method.
	 * </p>
	 * @param datastore The {@link Datastore} to use (not null)
	 * @param target The data target to use (not null)
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
		 * @param datastore the Datastore to set (not null)
		 * @return this
		 */
		Builder datastore(Datastore datastore);

		/**
		 * Set the {@link DataTarget} to use.
		 * @param target the data target to set (not null)
		 * @return this
		 */
		Builder target(DataTarget<?> target);

		/**
		 * Use given <code>identifierProperty</code> value as item identifier.
		 * @param identifierProperty The property which acts as item identifier (not null)
		 * @return this
		 */
		Builder itemIdentifier(Property<?> identifierProperty);

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
		 * @param queryConfigurationProvider the QueryConfigurationProvider to add (not null)
		 * @return this
		 */
		Builder withQueryConfigurationProvider(QueryConfigurationProvider queryConfigurationProvider);

		/**
		 * Build the {@link DatastoreDataProvider} instance.
		 * @return A new {@link DatastoreDataProvider} instance
		 */
		DatastoreDataProvider build();

	}

}
