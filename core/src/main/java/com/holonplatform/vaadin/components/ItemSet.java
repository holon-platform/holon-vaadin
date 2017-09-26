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
package com.holonplatform.vaadin.components;

import java.io.Serializable;

import com.vaadin.server.Resource;

/**
 * Implemented by components which support an item set as data source.
 * 
 * @since 5.0.0
 */
public interface ItemSet {

	/**
	 * Refresh items set.
	 * <p>
	 * If a item data loader is not supported by concrete implementation, this method has no effect.
	 * </p>
	 */
	void refresh();

	/**
	 * Item caption generator.
	 * @param <ITEM> Item type
	 */
	@FunctionalInterface
	public interface ItemCaptionGenerator<ITEM> extends Serializable {

		/**
		 * Get the caption for given <code>item</code>.
		 * @param item the item to get caption for
		 * @return Item caption (not null)
		 */
		String getItemCaption(ITEM item);

	}

	/**
	 * Item icon generator.
	 * @param <ITEM> Item type
	 */
	@FunctionalInterface
	public interface ItemIconGenerator<ITEM> extends Serializable {

		/**
		 * Get the icon for given <code>item</code>.
		 * @param item the item to get icon for
		 * @return Item icon
		 */
		Resource getItemIcon(ITEM item);

	}

}
