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
package com.holonplatform.vaadin.components.builders;

import com.holonplatform.vaadin.data.ItemDataProvider;
import com.holonplatform.vaadin.data.ItemDataSource;
import com.holonplatform.vaadin.data.ItemIdentifierProvider;
import com.vaadin.ui.Component;

/**
 * Builder for components with {@link ItemDataSource} and item property configuration support.
 * 
 * @param <ITEM> Item data type
 * @param <C> Concrete component type
 * @param <B> Concrete builder type
 *
 * @since 5.0.0
 */
public interface ItemDataSourceComponentBuilder<ITEM, C extends Component, B extends ItemDataSourceComponentBuilder<ITEM, C, B>>
		extends BaseItemDataSourceComponentBuilder<B>, ComponentBuilder<C, B> {

	/**
	 * Set the data source items data provider and the {@link ItemIdentifierProvider} to obtain item identifiers.
	 * @param dataProvider Items data provider (not null)
	 * @param itemIdentifierProvider Item identifier provider (not null)
	 * @return this
	 */
	B dataSource(ItemDataProvider<ITEM> dataProvider, ItemIdentifierProvider<ITEM, ?> itemIdentifierProvider);

	/**
	 * Set the data source items data provider, using the item itself as item identifier.
	 * @param dataProvider Items data provider (not null)
	 * @return this
	 */
	default B dataSource(ItemDataProvider<ITEM> dataProvider) {
		return dataSource(dataProvider, ItemIdentifierProvider.identity());
	}

}
