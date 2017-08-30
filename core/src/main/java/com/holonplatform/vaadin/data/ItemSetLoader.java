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
import java.util.stream.Stream;

import com.holonplatform.core.exceptions.DataAccessException;
import com.holonplatform.core.query.QueryConfigurationProvider;

/**
 * Data interface to load a set of items.
 * 
 * @param <ITEM> Item type
 *
 * @since 5.0.0
 */
@FunctionalInterface
public interface ItemSetLoader<ITEM> extends Serializable {

	/**
	 * Load items according to given data source <code>configuration</code>.
	 * @param configuration Query configuration
	 * @param offset Starts the query results at given zero-based offset
	 * @param limit Limit the fetched result set to given max value
	 * @return Items stream
	 * @throws DataAccessException Error accessing underlying data store
	 */
	Stream<ITEM> load(QueryConfigurationProvider configuration, int offset, int limit) throws DataAccessException;

}
