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

import com.holonplatform.vaadin.navigator.ViewContentProvider;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.ui.Component;

/**
 * Base {@link ViewDisplay} class with {@link ViewContentProvider} support
 * 
 * @since 5.0.0
 */
public abstract class AbstractViewDisplay implements ViewDisplay {

	private static final long serialVersionUID = 8928206580117781353L;

	/**
	 * Show view content
	 * @param content View content
	 */
	protected abstract void showViewContent(Component content);

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.navigator.ViewDisplay#showView(com.vaadin.navigator.View)
	 */
	@Override
	public void showView(View view) {
		showViewContent(ViewDisplayUtils.getViewContent(view));
	}

}
