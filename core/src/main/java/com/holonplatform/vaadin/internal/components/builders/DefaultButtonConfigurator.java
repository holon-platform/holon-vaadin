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

import com.holonplatform.vaadin.components.builders.ButtonConfigurator;
import com.holonplatform.vaadin.components.builders.ButtonConfigurator.BaseButtonConfigurator;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;

/**
 * Default {@link ButtonConfigurator} implementation.
 *
 * @since 5.0.0
 */
public class DefaultButtonConfigurator extends AbstractComponentConfigurator<Button, BaseButtonConfigurator>
		implements BaseButtonConfigurator {

	/**
	 * Constructor
	 * @param instance Instance to configure (not null)
	 */
	public DefaultButtonConfigurator(Button instance) {
		super(instance);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ButtonConfigurator#tabIndex(int)
	 */
	@Override
	public com.holonplatform.vaadin.components.builders.ButtonConfigurator.BaseButtonConfigurator tabIndex(
			int tabIndex) {
		getInstance().setTabIndex(tabIndex);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.ButtonConfigurator#withFocusListener(com.vaadin.event.FieldEvents.
	 * FocusListener)
	 */
	@Override
	public com.holonplatform.vaadin.components.builders.ButtonConfigurator.BaseButtonConfigurator withFocusListener(
			FocusListener listener) {
		getInstance().addFocusListener(listener);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.ButtonConfigurator#withBlurListener(com.vaadin.event.FieldEvents.
	 * BlurListener)
	 */
	@Override
	public com.holonplatform.vaadin.components.builders.ButtonConfigurator.BaseButtonConfigurator withBlurListener(
			BlurListener listener) {
		getInstance().addBlurListener(listener);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ButtonConfigurator#iconAlternateText(java.lang.String)
	 */
	@Override
	public com.holonplatform.vaadin.components.builders.ButtonConfigurator.BaseButtonConfigurator iconAlternateText(
			String iconAltText) {
		getInstance().setIconAlternateText(iconAltText);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ButtonConfigurator#disableOnClick()
	 */
	@Override
	public com.holonplatform.vaadin.components.builders.ButtonConfigurator.BaseButtonConfigurator disableOnClick() {
		getInstance().setDisableOnClick(true);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ButtonConfigurator#onClick(com.vaadin.ui.Button.ClickListener)
	 */
	@Override
	public com.holonplatform.vaadin.components.builders.ButtonConfigurator.BaseButtonConfigurator onClick(
			ClickListener listener) {
		getInstance().addClickListener(listener);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ButtonConfigurator#clickShortcut(int, int[])
	 */
	@Override
	public com.holonplatform.vaadin.components.builders.ButtonConfigurator.BaseButtonConfigurator clickShortcut(
			int keyCode, int... modifiers) {
		getInstance().setClickShortcut(keyCode, modifiers);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentConfigurator#builder()
	 */
	@Override
	protected com.holonplatform.vaadin.components.builders.ButtonConfigurator.BaseButtonConfigurator builder() {
		return this;
	}

}
