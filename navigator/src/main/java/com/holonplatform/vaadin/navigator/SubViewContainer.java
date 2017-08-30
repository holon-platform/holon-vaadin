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
package com.holonplatform.vaadin.navigator;

import java.util.Map;

import com.holonplatform.vaadin.navigator.ViewNavigator.ViewNavigationException;
import com.holonplatform.vaadin.navigator.annotations.OnShow;
import com.holonplatform.vaadin.navigator.annotations.SubViewOf;
import com.vaadin.navigator.View;

/**
 * Define a {@link View} as container for other sub-views.
 * <p>
 * Sub views bound to a SubViewContainer must declare parent container view name using {@link SubViewOf}.
 * </p>
 * <p>
 * When a sub-view bound to this parent container is requested using {@link ViewNavigator}, the
 * {@link #display(View, String, Map)} method is called and the container is delegated to display the view contents.
 * </p>
 * 
 * @since 4.4.0
 */
public interface SubViewContainer extends View {

	/**
	 * Display request for the given <code>view</code>: returns <code>true</code> if view is accepted and than diplayed
	 * by this sub-view container. If <code>false</code> is returned, View lifecycle hooks and methods will be ignored
	 * (for example {@link OnShow} methods).
	 * @param view View instance to display
	 * @param viewName View name
	 * @param parameters View parameters is serialized form, or an empty map if none
	 * @return <code>true</code> if this container accepted to display the view, <code>false</code> otherwise.
	 * @throws ViewNavigationException Error navigating to view
	 */
	boolean display(View view, String viewName, Map<String, String> parameters) throws ViewNavigationException;

	/**
	 * Get current active {@link View} in this container
	 * @return Current active View
	 */
	View getCurrentView();

}
