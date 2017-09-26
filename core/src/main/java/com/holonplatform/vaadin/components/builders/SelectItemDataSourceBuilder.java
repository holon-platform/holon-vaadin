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

import com.holonplatform.vaadin.components.Input;
import com.holonplatform.vaadin.data.ItemDataProvider;
import com.holonplatform.vaadin.data.ItemDataSource;
import com.vaadin.data.provider.DataProvider;

/**
 * Select component builder with {@link ItemDataSource} support.
 * 
 * @param <T> Field type
 * @param <C> Field component type
 * @param <S> Selection type
 * @param <ITEM> Selection items type
 * @param <B> Concrete builder type
 *
 * @since 5.0.0
 */
public interface SelectItemDataSourceBuilder<T, C extends Input<T>, S, ITEM, B extends SelectItemDataSourceBuilder<T, C, S, ITEM, B>>
		extends SelectInputBuilder<T, C, S, ITEM, B> {

	/**
	 * Set the selection items data provider.
	 * @param dataProvider Items data provider (not null)
	 * @return this
	 */
	B dataSource(ItemDataProvider<ITEM> dataProvider);

	/**
	 * Set the selection items data provider.
	 * @param dataProvider Items data provider (not null)
	 * @return this
	 */
	B dataSource(DataProvider<ITEM, ?> dataProvider);

}
