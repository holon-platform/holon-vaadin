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
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.query.BeanProjection;
import com.holonplatform.core.query.Query;
import com.holonplatform.core.query.QueryConfigurationProvider;
import com.holonplatform.vaadin.data.ItemDataProvider;
import com.holonplatform.vaadin.data.QueryConfigurationProviderSupport;

/**
 * An {@link ItemDataProvider} using a {@link Datastore} to perform item set count and load operations, using a bean
 * projection to obtain results in the required bean type.
 * <p>
 * Supports {@link QueryConfigurationProvider} registration through {@link QueryConfigurationProviderSupport}.
 * </p>
 * 
 * @param <T> Bean class
 * 
 * @since 5.1.0
 */
public class DatastoreBeanItemDataProvider<T> extends AbstractDatastoreItemDataProvider<T> {

	private static final long serialVersionUID = 6593984041773960514L;
	
	/**
	 * Query projection bean class
	 */
	private final Class<T> beanClass;

	/**
	 * Constructor.
	 * @param datastore Datastore to use (not null)
	 * @param target Data target (not null)
	 * @param beanPropertySet Bean property set (not null)
	 */
	public DatastoreBeanItemDataProvider(Datastore datastore, DataTarget<?> target, Class<T> beanClass) {
		super(datastore, target);
		ObjectUtils.argumentNotNull(beanClass, "Bean class must be not null");
		this.beanClass = beanClass;
	}

	/**
	 * Get the query projection bean class.
	 * @return the bean class
	 */
	protected Class<T> getBeanClass() {
		return beanClass;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.internal.data.AbstractDatastoreItemDataProvider#executeQuery(com.holonplatform.core.
	 * query.Query)
	 */
	@Override
	protected Stream<T> executeQuery(Query query) {
		return query.stream(BeanProjection.of(beanClass));
	}

}
