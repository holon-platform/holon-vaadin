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

import com.vaadin.navigator.View;

/**
 * Cache to store and retrieve {@link ViewConfiguration} for view classes
 * 
 * @since 5.0.0
 */
public interface ViewConfigurationCache {

	/**
	 * Clear cache
	 */
	void clearCache();

	/**
	 * Check if cache contains a view configuration associated to given <code>viewClass</code>
	 * @param viewClass View class
	 * @return <code>true</code> if cache contains a view configuration associated to given <code>viewClass</code>
	 */
	boolean hasViewConfiguration(Class<? extends View> viewClass);

	/**
	 * Get cached view configuration
	 * @param viewClass View class
	 * @return ViewConfiguration associated to given view class, or <code>null</code> if it is not present in cache
	 */
	ViewConfiguration getViewConfiguration(Class<? extends View> viewClass);

	/**
	 * Put given <code>viewConfiguration</code> associated to view class if not already present in cache.
	 * @param viewClass View class
	 * @param viewConfiguration View configuration to store in cache
	 * @return If a view configuration for the view class was already in cache, this will be returned. Otherwise, new
	 *         stored configuration is returned.
	 */
	ViewConfiguration storeViewConfiguration(Class<? extends View> viewClass, ViewConfiguration viewConfiguration);

}
