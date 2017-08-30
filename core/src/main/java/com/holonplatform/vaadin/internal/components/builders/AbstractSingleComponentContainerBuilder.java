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

import com.holonplatform.vaadin.components.builders.SingleComponentContainerBuilder;
import com.vaadin.ui.AbstractSingleComponentContainer;
import com.vaadin.ui.Component;
import com.vaadin.ui.HasComponents.ComponentAttachListener;
import com.vaadin.ui.HasComponents.ComponentDetachListener;
import com.vaadin.ui.SingleComponentContainer;

/**
 * Base class to build {@link SingleComponentContainer} components.
 * 
 * @param <C> Component type
 * @param <I> Internal component type
 * @param <B> Concrete builder type
 * 
 * @since 5.0.0
 */
public abstract class AbstractSingleComponentContainerBuilder<C extends SingleComponentContainer, I extends AbstractSingleComponentContainer, B extends SingleComponentContainerBuilder<C, B>>
		extends AbstractComponentBuilder<C, I, B> implements SingleComponentContainerBuilder<C, B> {

	public AbstractSingleComponentContainerBuilder(I instance) {
		super(instance);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.SingleComponentContainerBuilder#content(com.vaadin.ui.Component)
	 */
	@Override
	public B content(Component content) {
		getInstance().setContent(content);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.SingleComponentContainerBuilder#withComponentAttachListener(com.
	 * vaadin.ui.HasComponents.ComponentAttachListener)
	 */
	@Override
	public B withComponentAttachListener(ComponentAttachListener listener) {
		getInstance().addComponentAttachListener(listener);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.SingleComponentContainerBuilder#withComponentDetachListener(com.
	 * vaadin.ui.HasComponents.ComponentDetachListener)
	 */
	@Override
	public B withComponentDetachListener(ComponentDetachListener listener) {
		getInstance().addComponentDetachListener(listener);
		return builder();
	}

}
