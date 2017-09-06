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
package com.holonplatform.vaadin.navigator.internal;

import com.holonplatform.vaadin.navigator.ViewWindowConfiguration;
import com.vaadin.server.Sizeable;

/**
 * Default {@link ViewWindowConfiguration} implementation
 * 
 * @since 5.0.0
 */
public class DefaultViewWindowConfiguration implements ViewWindowConfiguration {

	private static final long serialVersionUID = 4215065564729451488L;

	private boolean closable = true;
	private boolean resizable = true;
	private String windowWidth = DEFAULT_WINDOW_WIDTH;
	private String windowHeight = DEFAULT_WINDOW_HEIGHT;

	/**
	 * Set whether to show a close button in the view Window header
	 * @param closable <code>true</code> to show a close button in the view Window header
	 */
	public void setClosable(boolean closable) {
		this.closable = closable;
	}

	/**
	 * Set whether to allow the view Window to be resized
	 * @param resizable <code>true</code> to allow the view Window to be resized
	 */
	public void setResizable(boolean resizable) {
		this.resizable = resizable;
	}

	/**
	 * Set view Window width using String representation. See {@link Sizeable#setWidth(String)} for details.
	 * @param windowWidth View Window width. Default is {@link ViewWindowConfiguration#DEFAULT_WINDOW_WIDTH}
	 */
	public void setWindowWidth(String windowWidth) {
		this.windowWidth = windowWidth;
	}

	/**
	 * Set view Window height using String representation. See {@link Sizeable#setHeight(String)} for details.
	 * @param windowHeight View Window height. Default is {@link ViewWindowConfiguration#DEFAULT_WINDOW_HEIGHT}
	 */
	public void setWindowHeight(String windowHeight) {
		this.windowHeight = windowHeight;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.ui.navigator.ViewWindowConfiguration#isClosable()
	 */
	@Override
	public boolean isClosable() {
		return closable;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.ui.navigator.ViewWindowConfiguration#isResizeable()
	 */
	@Override
	public boolean isResizable() {
		return resizable;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.ui.navigator.ViewWindowConfiguration#getWindowWidth()
	 */
	@Override
	public String getWindowWidth() {
		return windowWidth;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.ui.navigator.ViewWindowConfiguration#getWindowHeight()
	 */
	@Override
	public String getWindowHeight() {
		return windowHeight;
	}

}
