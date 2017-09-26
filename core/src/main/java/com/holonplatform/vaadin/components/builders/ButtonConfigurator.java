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
package com.holonplatform.vaadin.components.builders;

import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutAction.ModifierKey;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;

/**
 * Interface to configure a {@link Button}.
 * 
 * @param <B> Concrete configurator type
 * 
 * @since 5.0.0
 */
public interface ButtonConfigurator<B extends ButtonConfigurator<B>> extends ComponentConfigurator<B> {

	/**
	 * Sets the <i>tabulator index</i> of the component. The tab index property is used to specify the order in which
	 * the fields are focused when the user presses the Tab key. Components with a defined tab index are focused
	 * sequentially first, and then the components with no tab index.
	 * <p>
	 * If the tab index is not set (is set to zero), the default tab order is used. The order is somewhat
	 * browser-dependent, but generally follows the HTML structure of the page.
	 * </p>
	 * <p>
	 * A negative value means that the component is completely removed from the tabulation order and can not be reached
	 * by pressing the Tab key at all.
	 * </p>
	 * @param tabIndex The tab order of this component. Indexes usually start from 1. Zero means that default tab order
	 *        should be used. A negative value means that the field should not be included in the tabbing sequence.
	 * @return this
	 */
	B tabIndex(int tabIndex);

	/**
	 * Adds a listener which gets fired when the component receives keyboard focus.
	 * @param listener Focus listener to add
	 * @return this
	 */
	B withFocusListener(FocusListener listener);

	/**
	 * Adds a listener which gets fired when the the keyboard focus is lost.
	 * @param listener Blur listener to add
	 * @return this
	 */
	B withBlurListener(BlurListener listener);

	/**
	 * Sets the button icon's alternate text
	 * @param iconAltText Icon alternate text
	 * @return this
	 */
	B iconAlternateText(String iconAltText);

	/**
	 * Automatically disables button when clicked, typically to prevent (accidental) extra clicks on a button.
	 * <p>
	 * Note that this is only used when the click comes from the user, not when calling {@link Button#click()} method
	 * programmatically. Also, if developer wants to re-enable the button, it needs to be done programmatically.
	 * </p>
	 * @return this
	 */
	B disableOnClick();

	/**
	 * Add a listener to button to listen to user click events
	 * @param listener Click listener to add
	 * @return this
	 */
	B onClick(ClickListener listener);

	/**
	 * Makes it possible to invoke a click on this button by pressing the given {@link KeyCode} and (optional)
	 * {@link ModifierKey}s. The shortcut is global.
	 * @param keyCode the keycode for invoking the shortcut
	 * @param modifiers the (optional) modifiers for invoking the shortcut, null for none
	 * @return this
	 */
	B clickShortcut(int keyCode, int... modifiers);

	/**
	 * Base button configurator.
	 */
	public interface BaseButtonConfigurator extends ButtonConfigurator<BaseButtonConfigurator> {

	}

}
