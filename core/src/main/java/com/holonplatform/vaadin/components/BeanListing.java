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
package com.holonplatform.vaadin.components;

import com.holonplatform.vaadin.components.builders.ItemListingBuilder.GridItemListingBuilder;
import com.holonplatform.vaadin.internal.components.builders.DefaultGridItemListingBuilder;
import com.vaadin.ui.Grid;

/**
 * An {@link ItemListing} component using a bean type as item type and the bean property names as property set.
 * 
 * @param <T> Bean type
 * 
 * @since 5.0.0
 */
public interface BeanListing<T> extends ItemListing<T, String> {

	/**
	 * Builder to create an {@link BeanListing} instance using a {@link Grid} as backing component.
	 * @param <T> Item data type
	 * @param <P> Item property type
	 * @param itemType Item bean type
	 * @return Grid {@link BeanListing} builder
	 */
	static <T> GridItemListingBuilder<T> builder(Class<T> itemType) {
		return new DefaultGridItemListingBuilder<>(itemType);
	}

}
