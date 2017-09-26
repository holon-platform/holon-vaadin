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
package com.holonplatform.vaadin.navigator.internal;

import com.holonplatform.vaadin.navigator.ViewNavigator;
import com.holonplatform.vaadin.navigator.internal.ViewConfiguration.ViewConfigurationProvider;
import com.vaadin.navigator.NavigationStateManager;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.ui.UI;

/**
 * Adpater of {@link ViewNavigator} to be used with {@link NavigatorActuator}
 * 
 * @since 5.0.0
 */
public interface ViewNavigatorAdapter extends ViewNavigator, ViewConfigurationProvider {

	/**
	 * Update current navigation state relying in given View change event
	 * @param event View change event
	 */
	void updateCurrentNavigationState(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent event);

	/**
	 * Navigate to given View
	 * @param view View instance
	 * @param viewName View name
	 * @param parameters View parameters
	 */
	void navigateToView(View view, String viewName, String parameters);

	/**
	 * Init navigator
	 * @param ui UI
	 * @param stateManager NavigationStateManager
	 * @param display View display
	 */
	void setup(UI ui, NavigationStateManager stateManager, ViewDisplay display);

	/**
	 * Get the NavigatorActuator associated to this adapter
	 * @return NavigatorActuator
	 */
	NavigatorActuator<?> getActuator();

}
