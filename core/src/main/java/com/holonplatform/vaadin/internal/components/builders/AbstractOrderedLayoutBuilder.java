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
package com.holonplatform.vaadin.internal.components.builders;

import com.holonplatform.vaadin.components.builders.OrderedLayoutBuilder;
import com.vaadin.event.LayoutEvents.LayoutClickNotifier;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.Layout;

/**
 * Abstract {@link OrderedLayoutBuilder} implementation.
 * 
 * @param <C> Layout type
 * @param <I> Internal component type
 * @param <B> Concrete builder type
 * 
 * @since 5.0.0
 */
public abstract class AbstractOrderedLayoutBuilder<C extends Layout, I extends AbstractOrderedLayout & LayoutClickNotifier & Layout.AlignmentHandler & Layout.SpacingHandler & Layout.MarginHandler, B extends OrderedLayoutBuilder<C, B>>
		extends AbstractLayoutBuilder<C, I, B> implements OrderedLayoutBuilder<C, B> {

	public AbstractOrderedLayoutBuilder(I instance) {
		super(instance);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.OrderedLayoutBuilder#expand(com.vaadin.ui.Component, float)
	 */
	@Override
	public B expand(Component component, float expandRatio) {
		getInstance().setExpandRatio(component, expandRatio);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.OrderedLayoutBuilder#addAndExpand(com.vaadin.ui.Component,
	 * float)
	 */
	@Override
	public B addAndExpand(Component component, float expandRatio) {
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
	public B addAlignAndExpand(Component component, Alignment alignment, float expandRatio) {
		getInstance().addComponent(component);
		getInstance().setComponentAlignment(component, alignment);
		getInstance().setExpandRatio(component, expandRatio);
		return builder();
	}

}
