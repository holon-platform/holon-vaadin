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

import java.io.Serializable;

import com.holonplatform.core.exceptions.DataAccessException;
import com.holonplatform.core.query.QueryConfigurationProvider;

/**
 * Data interface to obtain the size of an item set.
 *
 * @since 5.0.0
 */
@FunctionalInterface
public interface ItemSetCounter extends Serializable {

	/**
	 * Get the data source item set size (number of items) according to given <code>configuration</code>.
	 * @param configuration Query configuration
	 * @return Item set size (number of items), or <code>0</code> if no item available
	 * @throws DataAccessException Error accessing underlying data store
	 */
	long size(QueryConfigurationProvider configuration) throws DataAccessException;

}
