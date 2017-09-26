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

import com.holonplatform.vaadin.components.builders.PanelBuilder;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.Action.Listener;
import com.vaadin.event.MouseEvents.ClickListener;
import com.vaadin.ui.Panel;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Default {@link PanelBuilder} implementation.
 * 
 * @since 5.0.0
 */
public class DefaultPanelBuilder extends AbstractSingleComponentContainerBuilder<Panel, Panel, PanelBuilder>
		implements PanelBuilder {

	public DefaultPanelBuilder() {
		super(new Panel());
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentBuilder#builder()
	 */
	@Override
	protected PanelBuilder builder() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentBuilder#build(com.vaadin.ui.
	 * AbstractComponent)
	 */
	@Override
	protected Panel build(Panel instance) {
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.PanelBuilder#borderless()
	 */
	@Override
	public PanelBuilder borderless() {
		getInstance().addStyleName(ValoTheme.PANEL_BORDERLESS);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.PanelBuilder#tabIndex(int)
	 */
	@Override
	public PanelBuilder tabIndex(int tabIndex) {
		getInstance().setTabIndex(tabIndex);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.PanelBuilder#withAction(com.vaadin.event.Action)
	 */
	@Override
	public <T extends Action & Listener> PanelBuilder withAction(T action) {
		getInstance().addAction(action);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.PanelBuilder#withActionHandler(com.vaadin.event.Action.Handler)
	 */
	@Override
	public PanelBuilder withActionHandler(Handler actionHandler) {
		getInstance().addActionHandler(actionHandler);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.PanelBuilder#withClickListener(com.vaadin.event.MouseEvents.
	 * ClickListener)
	 */
	@Override
	public PanelBuilder withClickListener(ClickListener listener) {
		getInstance().addClickListener(listener);
		return builder();
	}

}
