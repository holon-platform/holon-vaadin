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
package com.holonplatform.vaadin.internal.components.builders;

import com.holonplatform.vaadin.components.builders.InputConfigurator;
import com.holonplatform.vaadin.components.builders.InputConfigurator.BaseFieldConfigurator;
import com.vaadin.ui.AbstractField;

/**
 * Default {@link InputConfigurator} implementation.
 * 
 * @param <T> Field type
 *
 * @since 5.0.0
 */
public class DefaultFieldConfigurator<T> extends
		AbstractFieldConfigurator<T, AbstractField<T>, BaseFieldConfigurator<T>> implements BaseFieldConfigurator<T> {

	/**
	 * Constructor
	 * @param instance Instance to configure (not null)
	 */
	public DefaultFieldConfigurator(AbstractField<T> instance) {
		super(instance);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentConfigurator#builder()
	 */
	@Override
	protected com.holonplatform.vaadin.components.builders.InputConfigurator.BaseFieldConfigurator<T> builder() {
		return this;
	}

}
