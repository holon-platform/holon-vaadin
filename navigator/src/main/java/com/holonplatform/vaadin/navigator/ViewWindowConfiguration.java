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

import com.vaadin.navigator.View;
import com.vaadin.server.Sizeable;

/**
 * Configuration settings for a Window used as a {@link View} display.
 *
 * @since 5.0.0
 */
public interface ViewWindowConfiguration extends Serializable {

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
	 * Whether to show a close button in the view Window header
	 * @return <code>true</code> to show a close button in the view Window header
	 */
	boolean isClosable();

	/**
	 * Whether to allow the view Window to be resized
	 * @return <code>true</code> to allow the view Window to be resized
	 */
	boolean isResizable();

	/**
	 * View Window width using String representation. See {@link Sizeable#setWidth(String)} for details.
	 * @return View Window width. Default is {@link ViewWindowConfiguration#DEFAULT_WINDOW_WIDTH}
	 */
	String getWindowWidth();

	/**
	 * View Window height using String representation. See {@link Sizeable#setHeight(String)} for details.
	 * @return View Window height. Default is {@link ViewWindowConfiguration#DEFAULT_WINDOW_HEIGHT}
	 */
	String getWindowHeight();

}
