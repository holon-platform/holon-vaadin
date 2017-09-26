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

import com.holonplatform.vaadin.navigator.internal.ViewConfiguration.ViewConfigurationException;
import com.holonplatform.vaadin.navigator.internal.ViewConfiguration.ViewConfigurationProvider;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewProvider;

/**
 * {@link ViewProvider} with additional view processing capability.
 * 
 * <p>
 * The <code>processViewInstance</code> method is called by Navigator before returning {@link View} instances.
 * </p>
 *
 * @since 5.0.0
 */
public interface ViewProcessorProvider extends ViewProvider {

	/**
	 * Process given View instance for additional operations
	 * @param viewConfigurationProvider ViewConfigurationProvider
	 * @param view View instance
	 * @return Processed View
	 * @throws ViewConfigurationException Error processing View instance
	 */
	View processViewInstance(ViewConfigurationProvider viewConfigurationProvider, View view)
			throws ViewConfigurationException;

}
