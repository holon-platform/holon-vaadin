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

import com.holonplatform.vaadin.components.builders.ButtonBuilder;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.NativeButton;

/**
 * Default {@link ButtonBuilder} implementation.
 * 
 * @since 5.0.0
 */
public class DefaultButtonBuilder extends AbstractComponentBuilder<Button, Button, ButtonBuilder>
		implements ButtonBuilder {

	/**
	 * Constructor
	 * @param nativeMode <code>true</code> to create a "native" button, i.e. implemented using the native button of web
	 *        browsers, using the HTML <code>&lt;button&gt;</code> element.
	 */
	public DefaultButtonBuilder(boolean nativeMode) {
		super(nativeMode ? new NativeButton() : new Button());
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentBuilder#builder()
	 */
	@Override
	protected ButtonBuilder builder() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentBuilder#build(com.vaadin.ui.
	 * AbstractComponent)
	 */
	@Override
	protected Button build(Button instance) {
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ButtonBuilder#iconAlternateText(java.lang.String)
	 */
	@Override
	public ButtonBuilder iconAlternateText(String iconAltText) {
		getInstance().setIconAlternateText(iconAltText);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ButtonBuilder#disableOnClick()
	 */
	@Override
	public ButtonBuilder disableOnClick() {
		getInstance().setDisableOnClick(true);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ButtonBuilder#onClick(com.vaadin.ui.Button.ClickListener)
	 */
	@Override
	public ButtonBuilder onClick(ClickListener listener) {
		getInstance().addClickListener(listener);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ButtonBuilder#clickShortcut(int, int[])
	 */
	@Override
	public ButtonBuilder clickShortcut(int keyCode, int... modifiers) {
		getInstance().setClickShortcut(keyCode, modifiers);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ButtonBuilder#tabIndex(int)
	 */
	@Override
	public ButtonBuilder tabIndex(int tabIndex) {
		getInstance().setTabIndex(tabIndex);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ButtonBuilder#withFocusListener(com.vaadin.event.FieldEvents.
	 * FocusListener)
	 */
	@Override
	public ButtonBuilder withFocusListener(FocusListener listener) {
		getInstance().addFocusListener(listener);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ButtonBuilder#withBlurListener(com.vaadin.event.FieldEvents.
	 * BlurListener)
	 */
	@Override
	public ButtonBuilder withBlurListener(BlurListener listener) {
		getInstance().addBlurListener(listener);
		return this;
	}

}
