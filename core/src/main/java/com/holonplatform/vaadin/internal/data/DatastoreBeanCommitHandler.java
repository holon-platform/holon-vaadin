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

import java.util.Collection;

import com.holonplatform.core.beans.BeanPropertySet;
import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.exceptions.DataAccessException;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.vaadin.data.ItemDataSource.CommitHandler;

/**
 * A {@link CommitHandler} which uses a {@link Datastore} to perform persistence operations and convert bean instances
 * into {@link PropertyBox} values.
 * 
 * @param <T> Bean type
 *
 * @since 5.1.0
 */
public class DatastoreBeanCommitHandler<T> implements CommitHandler<T> {

	private static final long serialVersionUID = 7706525137623029512L;

	/**
	 * Bean class
	 */
	private final Class<? extends T> beanClass;

	/**
	 * Datastore
	 */
	private final Datastore datastore;

	/**
	 * Data target
	 */
	private final DataTarget<?> target;

	/**
	 * Bean property set
	 */
	private BeanPropertySet<T> beanPropertySet = null;

	/**
	 * Constructor.
	 * @param beanClass Bean class (not null)
	 * @param datastore Datastore to use (not null)
	 * @param target Data target (not null)
	 */
	public DatastoreBeanCommitHandler(Class<? extends T> beanClass, Datastore datastore, DataTarget<?> target) {
		super();
		ObjectUtils.argumentNotNull(beanClass, "Bean class must be not null");
		ObjectUtils.argumentNotNull(datastore, "Datastore must be not null");
		ObjectUtils.argumentNotNull(target, "DataTarget must be not null");
		this.beanClass = beanClass;
		this.datastore = datastore;
		this.target = target;
	}

	/**
	 * Get the bean property set.
	 * @return The bean property set
	 */
	protected BeanPropertySet<T> getBeanPropertySet() {
		if (beanPropertySet == null) {
			beanPropertySet = BeanPropertySet.create(beanClass);
		}
		return beanPropertySet;
	}

	/**
	 * Convert given bean instance into a {@link PropertyBox}.
	 * @param instance Bean instance to convert
	 * @return Bean instance property values as {@link PropertyBox}
	 */
	protected PropertyBox convert(T instance) {
		if (instance != null) {
			try {
				return getBeanPropertySet().read(instance);
			} catch (Exception e) {
				throw new DataAccessException("Failed to convert bean instance [" + instance + "] into a PropertyBox",
						e);
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemDataSource.CommitHandler#commit(java.util.Collection,
	 * java.util.Collection, java.util.Collection)
	 */
	@Override
	public void commit(Collection<T> addedItems, Collection<T> modifiedItems, Collection<T> removedItems) {
		addedItems.forEach(i -> datastore.save(target, convert(i)));
		modifiedItems.forEach(i -> datastore.save(target, convert(i)));
		removedItems.forEach(i -> datastore.delete(target, convert(i)));
	}

}
