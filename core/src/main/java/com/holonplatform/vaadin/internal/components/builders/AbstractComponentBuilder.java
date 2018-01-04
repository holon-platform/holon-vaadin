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

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.holonplatform.vaadin.components.builders.ComponentBuilder;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.dnd.DragSourceExtension;
import com.vaadin.ui.dnd.DropTargetExtension;

/**
 * Base class for {@link ComponentBuilder}s.
 * 
 * @param <C> Component type
 * @param <I> Internal component type
 * @param <B> Concrete builder type
 * 
 * @since 5.0.0
 */
public abstract class AbstractComponentBuilder<C extends Component, I extends AbstractComponent, B extends ComponentBuilder<C, B>>
		extends AbstractLocalizableComponentConfigurator<I, B> implements ComponentBuilder<C, B> {

	protected DropTargetExtension<I> dropTargetExtension;
	protected BiConsumer<DropTargetExtension<? extends AbstractComponent>, C> dropTargetConfigurator;

	/**
	 * Constructor
	 * @param instance Instance to build and return (not null)
	 */
	public AbstractComponentBuilder(I instance) {
		super(instance);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ComponentBuilder#dragSource(java.util.function.Consumer)
	 */
	@Override
	public B dragSource(Consumer<DragSourceExtension<? extends AbstractComponent>> configurator) {
		final DragSourceExtension<I> extension = new DragSourceExtension<>(getInstance());
		if (configurator != null) {
			configurator.accept(extension);
		}
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ComponentBuilder#dropTarget(java.util.function.BiConsumer)
	 */
	@Override
	public B dropTarget(BiConsumer<DropTargetExtension<? extends AbstractComponent>, C> configurator) {
		this.dropTargetExtension = new DropTargetExtension<>(getInstance());
		this.dropTargetConfigurator = configurator;
		return builder();
	}

	/**
	 * Build concrete instance in expected type
	 * @param instance Building instance
	 * @return Instance in expected type
	 */
	protected abstract C build(I instance);

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ComponentBuilder#deferLocalization()
	 */
	@Override
	public B deferLocalization() {
		this.deferLocalization = true;
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ComponentBuilder#build()
	 */
	@Override
	public C build() {
		C component = build(setupLocalization(instance));
		// setup drop target extension
		if (dropTargetExtension != null && dropTargetConfigurator != null) {
			dropTargetConfigurator.accept(dropTargetExtension, component);
		}
		return component;
	}

}
