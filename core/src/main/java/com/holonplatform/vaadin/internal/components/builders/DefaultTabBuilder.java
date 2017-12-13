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

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.components.builders.TabsBuilder;
import com.holonplatform.vaadin.components.builders.TabsBuilder.TabBuilder;
import com.vaadin.server.Resource;
import com.vaadin.ui.Component.Focusable;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;

/**
 * Default {@link TabBuilder} implementation.
 * 
 * @param <C> Actual tabs component type
 *
 * @since 5.0.5
 */
public class DefaultTabBuilder<C extends TabSheet> implements TabBuilder<C> {

	private final TabsBuilder<C> parentBuilder;
	private final Tab tab;

	public DefaultTabBuilder(TabsBuilder<C> parentBuilder, Tab tab) {
		super();
		this.parentBuilder = parentBuilder;
		this.tab = tab;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.TabsBuilder.TabBuilder#caption(com.holonplatform.core.i18n.
	 * Localizable)
	 */
	@Override
	public TabBuilder<C> caption(Localizable caption) {
		ObjectUtils.argumentNotNull(caption, "Localizable must be not null");
		tab.setCaption(LocalizationContext.translate(caption, true));
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.TabsBuilder.TabBuilder#description(com.holonplatform.core.i18n.
	 * Localizable)
	 */
	@Override
	public TabBuilder<C> description(Localizable description) {
		ObjectUtils.argumentNotNull(description, "Localizable must be not null");
		tab.setDescription(LocalizationContext.translate(description, true));
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.TabsBuilder.TabBuilder#icon(com.vaadin.server.Resource)
	 */
	@Override
	public TabBuilder<C> icon(Resource icon) {
		tab.setIcon(icon);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.TabsBuilder.TabBuilder#visible(boolean)
	 */
	@Override
	public TabBuilder<C> visible(boolean visible) {
		tab.setVisible(visible);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.TabsBuilder.TabBuilder#enabled(boolean)
	 */
	@Override
	public TabBuilder<C> enabled(boolean enabled) {
		tab.setEnabled(enabled);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.TabsBuilder.TabBuilder#closable(boolean)
	 */
	@Override
	public TabBuilder<C> closable(boolean closable) {
		tab.setClosable(closable);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.TabsBuilder.TabBuilder#styleName(java.lang.String)
	 */
	@Override
	public TabBuilder<C> styleName(String styleName) {
		tab.setStyleName(styleName);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.TabsBuilder.TabBuilder#defaultFocusComponent(com.vaadin.ui.Component
	 * .Focusable)
	 */
	@Override
	public TabBuilder<C> defaultFocusComponent(Focusable component) {
		tab.setDefaultFocusComponent(component);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.TabsBuilder.TabBuilder#add()
	 */
	@Override
	public TabsBuilder<C> add() {
		return parentBuilder;
	}

}
