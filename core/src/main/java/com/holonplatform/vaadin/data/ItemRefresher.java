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

/**
 * Data interface to refresh item data.
 * 
 * @param <ITEM> Item type
 *
 * @since 5.0.0
 */
@FunctionalInterface
public interface ItemRefresher<ITEM> extends Serializable {

	/**
	 * Refresh given item from concrete data store.
	 * @param item Item to refresh (not null)
	 * @return Refreshed item
	 * @throws UnsupportedOperationException If the refresh operation is not supported by concrete implementation
	 * @throws DataAccessException Error accessing underlying data store
	 */
	ITEM refresh(ITEM item) throws UnsupportedOperationException, DataAccessException;

}
