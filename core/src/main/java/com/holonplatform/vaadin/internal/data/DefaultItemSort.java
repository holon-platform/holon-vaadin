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
package com.holonplatform.vaadin.internal.data;

import com.holonplatform.vaadin.data.ItemDataSource.ItemSort;

/**
 * Default {@link ItemSort} implementation.
 * 
 * @param <PROPERTY> Property type
 *
 * @since 5.0.0
 */
public class DefaultItemSort<PROPERTY> implements ItemSort<PROPERTY> {

	private static final long serialVersionUID = 6547344951159124335L;

	private final PROPERTY property;
	private final boolean ascending;

	public DefaultItemSort(PROPERTY property, boolean ascending) {
		super();
		this.property = property;
		this.ascending = ascending;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemDataSource.ItemSort#getProperty()
	 */
	@Override
	public PROPERTY getProperty() {
		return property;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemDataSource.ItemSort#isAscending()
	 */
	@Override
	public boolean isAscending() {
		return ascending;
	}

}
