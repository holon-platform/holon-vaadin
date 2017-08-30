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
package com.holonplatform.vaadin.internal.data.container;

import com.holonplatform.vaadin.data.container.ItemDataSourceContainerBuilder.BaseItemDataSourceContainerBuilder;

/**
 * Default {@link BaseItemDataSourceContainerBuilder} implementation.
 * 
 * @param <ITEM> Item type
 * @param <PROPERTY> Item property type
 * 
 * @since 5.0.0
 */
public class DefaultItemDataSourceContainerBuilder<PROPERTY, ITEM> extends
		AbstractItemDataSourceContainerBuilder<PROPERTY, ITEM, BaseItemDataSourceContainerBuilder<PROPERTY, ITEM>>
		implements BaseItemDataSourceContainerBuilder<PROPERTY, ITEM> {

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.data.container.AbstractQueryContainerBuilder#buider()
	 */
	@Override
	protected BaseItemDataSourceContainerBuilder<PROPERTY, ITEM> builder() {
		return this;
	}

	/* (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.container.ItemDataSourceContainerBuilder.BaseItemDataSourceContainerBuilder#getProperties()
	 */
	@Override
	public Iterable<ITEM> getProperties() {
		return container.getProperties();
	}

}
