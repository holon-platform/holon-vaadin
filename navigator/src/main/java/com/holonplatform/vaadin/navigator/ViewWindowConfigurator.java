/*
 * Copyright 2000-2017 Holon TDCN.
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
package com.holonplatform.vaadin.navigator;

import java.io.Serializable;

import com.holonplatform.vaadin.components.builders.ComponentConfigurator;
import com.vaadin.navigator.View;

/**
 * Configuration settings for a Window used as a {@link View} display.
 *
 * @since 5.0.0
 */
public interface ViewWindowConfigurator extends ComponentConfigurator<ViewWindowConfigurator>, Serializable {

	/**
	 * Default View Window style name
	 */
	public static final String DEFAULT_WINDOW_STYLE_NAME = "navigation-view-window";

	/**
	 * Default window width
	 */
	public static final String DEFAULT_WINDOW_WIDTH = "95%";

	/**
	 * Default window height
	 */
	public static final String DEFAULT_WINDOW_HEIGHT = "95%";

	/**
	 * Sets whether to show a close button in the view Window header.
	 * @param closable <code>true</code> to show a close button in the view Window header, <code>false</code> to not
	 *        show it
	 * @return this
	 */
	ViewWindowConfigurator closable(boolean closable);

	/**
	 * Sets whether to allow the view Window to be resized.
	 * @param resizable whether to allow the view Window to be resized
	 * @return this
	 */
	ViewWindowConfigurator resizable(boolean resizable);

	/**
	 * Set whether the view Window is draggable.
	 * @param draggable whether the view Window is draggable
	 * @return this
	 */
	ViewWindowConfigurator draggable(boolean draggable);

	/**
	 * Sets the position of the view window on the screen.
	 * @param x The x coordinate for the window
	 * @param y The y coordinate for the window
	 * @return this
	 */
	ViewWindowConfigurator position(int x, int y);

	/**
	 * Adds a close shortcut to the view window - pressing this key while holding down all (if any) modifiers specified
	 * while the window is in focus will close the window.
	 * @param keyCode the keycode for invoking the shortcut
	 * @param modifiers the (optional) modifiers for invoking the shortcut. Can be set to null to be explicit about not
	 *        having modifiers.
	 * @return this
	 */
	ViewWindowConfigurator withCloseShortcut(int keyCode, int... modifiers);

}
