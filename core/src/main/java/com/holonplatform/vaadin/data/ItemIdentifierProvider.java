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
package com.holonplatform.vaadin.data;

import java.io.Serializable;

/**
 * Provider to obtain the identifier of an item.
 * 
 * @param <ITEM> Item type
 * @param <ID> Item id type
 * 
 * @since 5.0.0
 */
@FunctionalInterface
public interface ItemIdentifierProvider<ITEM, ID> extends Serializable {

	/**
	 * Get the item identifier.
	 * @param item Item for which to obtain the identifier (not null)
	 * @return The item identifier, which must not be <code>null</code>. {@link Object#equals(Object)} and
	 *         {@link Object#hashCode()} methods are used to compare item identifiers, so the returned identifier should
	 *         implement such methods in a consistent manner
	 */
	ID getItemId(ITEM item);

	/**
	 * Returns an {@link ItemIdentifierProvider} which returns the item itself as item identifier.
	 * @param <T> Item type
	 * @return Identity {@link ItemIdentifierProvider}
	 */
	static <T> ItemIdentifierProvider<T, T> identity() {
		return i -> i;
	}

}
