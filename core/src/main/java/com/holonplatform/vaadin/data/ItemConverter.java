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

/**
 * A converter interface to obtain an item from a different type value.
 * 
 * @param <V> Value type
 * @param <I> Item type
 * 
 * @since 5.0.3
 */
@FunctionalInterface
public interface ItemConverter<V, I> {

	/**
	 * Convert given value to required item type.
	 * @param value The value to convert (may be null)
	 * @return The item value
	 */
	I convert(V value);

}
