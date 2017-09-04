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
package com.holonplatform.vaadin.internal.components;

import com.holonplatform.vaadin.components.BeanListing;
import com.vaadin.ui.Grid;

/**
 * Default {@link BeanListing} implementation.
 * 
 * @param <T> Bean type
 *
 * @since 5.0.0
 */
public class DefaultBeanListing<T> extends DefaultItemListing<T, String> implements BeanListing<T> {

	private static final long serialVersionUID = 2163943323958351369L;

	public DefaultBeanListing(Class<T> beanType) {
		super(new Grid<>(beanType));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.DefaultItemListing#getColumnId(java.lang.Object)
	 */
	@Override
	public String getColumnId(String property) {
		return property;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.DefaultItemListing#getColumnProperty(java.lang.String)
	 */
	@Override
	protected String getColumnProperty(String columnId) {
		return columnId;
	}

}
