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

import com.holonplatform.vaadin.components.builders.BaseLayoutBuilder;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.event.LayoutEvents.LayoutClickNotifier;
import com.vaadin.ui.AbstractComponentContainer;
import com.vaadin.ui.Layout;

/**
 * Abstract {@link BaseLayoutBuilder} implementation.
 * 
 * @param <C> Layout type
 * @param <I> Internal component type
 * @param <B> Concrete builder type
 *
 * @since 5.0.0
 */
public abstract class AbstractBaseLayoutBuilder<C extends Layout, I extends AbstractComponentContainer & LayoutClickNotifier, B extends BaseLayoutBuilder<C, B>>
		extends AbstractComponentContainerBuilder<C, I, B> implements BaseLayoutBuilder<C, B> {

	public AbstractBaseLayoutBuilder(I instance) {
		super(instance);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.BaseLayoutBuilder#withLayoutClickListener(com.vaadin.event.
	 * LayoutEvents.LayoutClickListener)
	 */
	@Override
	public B withLayoutClickListener(LayoutClickListener listener) {
		getInstance().addLayoutClickListener(listener);
		return builder();
	}

}
