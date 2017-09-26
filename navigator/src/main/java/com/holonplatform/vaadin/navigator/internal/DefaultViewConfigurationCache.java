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

import java.util.Map;
import java.util.WeakHashMap;

import com.vaadin.navigator.View;

/**
 * Default {@link ViewConfigurationCache} implementation
 * 
 * @since 5.0.0
 */
public enum DefaultViewConfigurationCache implements ViewConfigurationCache {

	/**
	 * Singleton instance
	 */
	INSTANCE;

	/*
	 * Cache: view class <-> ViewConfiguration
	 */
	private final Map<Class<? extends View>, ViewConfiguration> cache = new WeakHashMap<>(16, 0.9f);

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.ui.navigator.ViewConfigurationCache#hasViewConfiguration(java.lang.Class)
	 */
	@Override
	public boolean hasViewConfiguration(Class<? extends View> viewClass) {
		synchronized (cache) {
			return cache.containsKey(viewClass);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.ui.navigator.ViewConfigurationCache#clearCache()
	 */
	@Override
	public void clearCache() {
		synchronized (cache) {
			cache.clear();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.ui.navigator.ViewConfigurationCache#getViewConfiguration(java.lang.Class)
	 */
	@Override
	public ViewConfiguration getViewConfiguration(Class<? extends View> viewClass) {
		synchronized (cache) {
			return cache.get(viewClass);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.ui.navigator.ViewConfigurationCache#storeViewConfiguration(java.lang.Class,
	 * com.holonplatform.vaadin.ui.navigator.ViewConfiguration)
	 */
	@Override
	public ViewConfiguration storeViewConfiguration(Class<? extends View> viewClass,
			ViewConfiguration viewConfiguration) {
		synchronized (cache) {
			ViewConfiguration existing = cache.put(viewClass, viewConfiguration);
			return (existing != null ? existing : viewConfiguration);
		}
	}

}
