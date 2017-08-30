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

import com.holonplatform.vaadin.components.builders.LayoutConfigurator;
import com.holonplatform.vaadin.components.builders.LayoutConfigurator.BaseLayoutConfigurator;
import com.vaadin.event.LayoutEvents.LayoutClickNotifier;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Layout;

/**
 * Default {@link LayoutConfigurator} implementation.
 * 
 * @param <I> Internal component type
 *
 * @since 5.0.0
 */
public class DefaultLayoutConfigurator<I extends AbstractLayout & LayoutClickNotifier & Layout.AlignmentHandler & Layout.SpacingHandler & Layout.MarginHandler>
		extends AbstractLayoutConfigurator<I, BaseLayoutConfigurator> implements BaseLayoutConfigurator {

	/**
	 * Constructor
	 * @param instance Instance to configure (not null)
	 */
	public DefaultLayoutConfigurator(I instance) {
		super(instance);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentConfigurator#builder()
	 */
	@Override
	protected com.holonplatform.vaadin.components.builders.LayoutConfigurator.BaseLayoutConfigurator builder() {
		return this;
	}

}
