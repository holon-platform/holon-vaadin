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

import com.holonplatform.vaadin.components.builders.OrderedLayoutConfigurator;
import com.holonplatform.vaadin.components.builders.OrderedLayoutConfigurator.BaseOrderedLayoutConfigurator;
import com.vaadin.event.LayoutEvents.LayoutClickNotifier;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.Layout;

/**
 * Default {@link OrderedLayoutConfigurator} implementation.
 * 
 * @param <I> Internal component type
 *
 * @since 5.0.0
 */
public class DefaultOrderedLayoutConfigurator<I extends AbstractOrderedLayout & LayoutClickNotifier & Layout.AlignmentHandler & Layout.SpacingHandler & Layout.MarginHandler>
		extends AbstractLayoutConfigurator<I, BaseOrderedLayoutConfigurator> implements BaseOrderedLayoutConfigurator {

	/**
	 * Constructor
	 * @param instance Instance to configure (not null)
	 */
	public DefaultOrderedLayoutConfigurator(I instance) {
		super(instance);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.OrderedLayoutConfigurator#expand(com.vaadin.ui.Component,
	 * float)
	 */
	@Override
	public com.holonplatform.vaadin.components.builders.OrderedLayoutConfigurator.BaseOrderedLayoutConfigurator expand(
			Component component, float expandRatio) {
		getInstance().setExpandRatio(component, expandRatio);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.OrderedLayoutConfigurator#addAndExpand(com.vaadin.ui.Component,
	 * float)
	 */
	@Override
	public com.holonplatform.vaadin.components.builders.OrderedLayoutConfigurator.BaseOrderedLayoutConfigurator addAndExpand(
			Component component, float expandRatio) {
		getInstance().addComponent(component);
		getInstance().setExpandRatio(component, expandRatio);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.OrderedLayoutConfigurator#addAlignAndExpand(com.vaadin.ui.
	 * Component, com.vaadin.ui.Alignment, float)
	 */
	@Override
	public com.holonplatform.vaadin.components.builders.OrderedLayoutConfigurator.BaseOrderedLayoutConfigurator addAlignAndExpand(
			Component component, Alignment alignment, float expandRatio) {
		getInstance().addComponent(component);
		getInstance().setComponentAlignment(component, alignment);
		getInstance().setExpandRatio(component, expandRatio);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentConfigurator#builder()
	 */
	@Override
	protected com.holonplatform.vaadin.components.builders.OrderedLayoutConfigurator.BaseOrderedLayoutConfigurator builder() {
		return this;
	}

}
