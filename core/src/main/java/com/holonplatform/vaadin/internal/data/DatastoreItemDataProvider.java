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
package com.holonplatform.vaadin.internal.data;

import java.util.stream.Stream;

import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.exceptions.DataAccessException;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.query.Query;
import com.holonplatform.core.query.QueryConfigurationProvider;
import com.holonplatform.vaadin.data.ItemDataProvider;
import com.holonplatform.vaadin.data.QueryConfigurationProviderSupport;

/**
 * An {@link ItemDataProvider} using a {@link Datastore} to perform item set count and load operations, using
 * {@link PropertyBox} as item type.
 * <p>
 * Supports {@link QueryConfigurationProvider} registration through {@link QueryConfigurationProviderSupport}.
 * </p>
 * 
 * @since 5.0.0
 */
public class DatastoreItemDataProvider extends AbstractDatastoreItemDataProvider<PropertyBox> {

	private static final long serialVersionUID = 3163918924360548413L;

	/**
	 * Query projection property set
	 */
	private final PropertySet<?> propertySet;

	/**
	 * Constructor.
	 * @param datastore Datastore to use (not null)
	 * @param target Data target (not null)
	 * @param propertySet Property set to use as query projection (not null)
	 */
	public DatastoreItemDataProvider(Datastore datastore, DataTarget<?> target, PropertySet<?> propertySet) {
		super(datastore, target);
		ObjectUtils.argumentNotNull(propertySet, "PropertySet must be not null");
		this.propertySet = propertySet;
	}

	/**
	 * Get the {@link PropertySet} to use as query projection.
	 * @return the query projection {@link PropertySet}
	 */
	protected PropertySet<?> getPropertySet() {
		return propertySet;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.internal.data.AbstractDatastoreItemDataProvider#executeQuery(com.holonplatform.core.
	 * query.Query)
	 */
	@Override
	protected Stream<PropertyBox> executeQuery(Query query) {
		return query.stream(getPropertySet());
	}

	@Override
	public PropertyBox refresh(PropertyBox item) throws UnsupportedOperationException, DataAccessException {
		if (item == null) {
			return null;
		}
		return getDatastore().refresh(getTarget(), item);
	}

}
