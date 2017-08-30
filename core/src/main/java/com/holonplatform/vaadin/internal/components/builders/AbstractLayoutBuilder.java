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

import com.holonplatform.vaadin.components.builders.LayoutBuilder;
import com.vaadin.event.LayoutEvents.LayoutClickNotifier;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.Layout;

/**
 * Abstract {@link LayoutBuilder} implementation.
 * 
 * @param <C> Layout type
 * @param <I> Internal component type
 * @param <B> Concrete builder type
 * 
 * @since 5.0.0
 */
public abstract class AbstractLayoutBuilder<C extends Layout, I extends AbstractLayout & LayoutClickNotifier & Layout.AlignmentHandler & Layout.SpacingHandler & Layout.MarginHandler, B extends LayoutBuilder<C, B>>
		extends AbstractBaseLayoutBuilder<C, I, B> implements LayoutBuilder<C, B> {

	protected final MarginInfo margins = new MarginInfo(false);

	public AbstractLayoutBuilder(I instance) {
		super(instance);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.LayoutBuilder#spacing()
	 */
	@Override
	public B spacing() {
		getInstance().setSpacing(true);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.LayoutBuilder#margin()
	 */
	@Override
	public B margin() {
		margins.setMargins(true, true, true, true);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.LayoutBuilder#marginTop()
	 */
	@Override
	public B marginTop() {
		margins.setMargins(true, margins.hasRight(), margins.hasBottom(), margins.hasLeft());
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.LayoutBuilder#marginBottom()
	 */
	@Override
	public B marginBottom() {
		margins.setMargins(margins.hasTop(), margins.hasRight(), true, margins.hasLeft());
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.LayoutBuilder#marginLeft()
	 */
	@Override
	public B marginLeft() {
		margins.setMargins(margins.hasTop(), margins.hasRight(), margins.hasBottom(), true);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.LayoutBuilder#marginRight()
	 */
	@Override
	public B marginRight() {
		margins.setMargins(margins.hasTop(), true, margins.hasBottom(), margins.hasLeft());
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.LayoutBuilder#defaultAlignment(com.vaadin.ui.Alignment)
	 */
	@Override
	public B defaultAlignment(Alignment alignment) {
		getInstance().setDefaultComponentAlignment(alignment);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.LayoutBuilder#align(com.vaadin.ui.Component,
	 * com.vaadin.ui.Alignment)
	 */
	@Override
	public B align(Component component, Alignment alignment) {
		getInstance().setComponentAlignment(component, alignment);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.LayoutBuilder#addAndAlign(com.vaadin.ui.Component,
	 * com.vaadin.ui.Alignment)
	 */
	@Override
	public B addAndAlign(Component component, Alignment alignment) {
		getInstance().addComponent(component);
		getInstance().setComponentAlignment(component, alignment);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentBuilder#build(com.vaadin.ui.
	 * AbstractComponent)
	 */
	@Override
	protected C build(I instance) {
		if (margins.hasTop() || margins.hasBottom() || margins.hasLeft() || margins.hasRight()) {
			instance.setMargin(margins);
		}
		return buildLayout(instance);
	}

	/**
	 * Actual layout instance build
	 * @param instance Building instance
	 * @return Layout instance
	 */
	protected abstract C buildLayout(I instance);

}
