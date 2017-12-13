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

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.components.builders.TabsBuilder;
import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.CloseHandler;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;

/**
 * Base {@link TabsBuilder} implementation.
 * 
 * @param <C> Actual tabs component type
 *
 * @since 5.0.5
 */
public abstract class AbstractTabsBuilder<C extends TabSheet> extends AbstractComponentBuilder<C, C, TabsBuilder<C>>
		implements TabsBuilder<C> {

	public AbstractTabsBuilder(C instance) {
		super(instance);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.TabsBuilder#withTab(com.vaadin.ui.Component)
	 */
	@Override
	public com.holonplatform.vaadin.components.builders.TabsBuilder.TabBuilder<C> withTab(Component component) {
		ObjectUtils.argumentNotNull(component, "Tab component must be not null");
		return new DefaultTabBuilder<>(builder(), instance.addTab(component));
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.TabsBuilder#withSelectedTabChangeListener(com.vaadin.ui.TabSheet.
	 * SelectedTabChangeListener)
	 */
	@Override
	public TabsBuilder<C> withSelectedTabChangeListener(SelectedTabChangeListener listener) {
		ObjectUtils.argumentNotNull(listener, "SelectedTabChangeListener must be not null");
		instance.addSelectedTabChangeListener(listener);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.TabsBuilder#closeHandler(com.vaadin.ui.TabSheet.CloseHandler)
	 */
	@Override
	public TabsBuilder<C> closeHandler(CloseHandler closeHandler) {
		instance.setCloseHandler(closeHandler);
		return builder();
	}

}
