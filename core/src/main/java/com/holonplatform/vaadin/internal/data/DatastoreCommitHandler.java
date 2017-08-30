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
package com.holonplatform.vaadin.internal.data;

import java.util.Collection;

import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.vaadin.data.ItemDataSource.CommitHandler;

/**
 * A {@link CommitHandler} using {@link Datastore} to perform persistence operations.
 *
 * @since 5.0.0
 */
public class DatastoreCommitHandler implements CommitHandler<PropertyBox> {

	private static final long serialVersionUID = -6693653610998194516L;

	/**
	 * Datastore
	 */
	private final Datastore datastore;

	/**
	 * Data target
	 */
	private final DataTarget<?> target;

	/**
	 * Construct a new DatastoreItemDataProvider.
	 * @param datastore Datastore to use (not null)
	 * @param target Query target (not null)
	 */
	public DatastoreCommitHandler(Datastore datastore, DataTarget<?> target) {
		super();
		ObjectUtils.argumentNotNull(datastore, "Datastore must be not null");
		ObjectUtils.argumentNotNull(target, "DataTarget must be not null");
		this.datastore = datastore;
		this.target = target;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemDataSource.CommitHandler#commit(java.util.Collection,
	 * java.util.Collection, java.util.Collection)
	 */
	@Override
	public void commit(Collection<PropertyBox> addedItems, Collection<PropertyBox> modifiedItems,
			Collection<PropertyBox> removedItems) {
		addedItems.forEach(i -> datastore.save(target, i));
		modifiedItems.forEach(i -> datastore.save(target, i));
		removedItems.forEach(i -> datastore.delete(target, i));
	}

}
