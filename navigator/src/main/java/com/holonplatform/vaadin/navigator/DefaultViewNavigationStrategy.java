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
package com.holonplatform.vaadin.navigator;

import com.vaadin.ui.UI;

/**
 * A strategy to obtain the default view navigation state which can be provided to {@link ViewNavigator}.
 *
 * @since 5.0.0
 */
@FunctionalInterface
public interface DefaultViewNavigationStrategy {

	/**
	 * Get the default navigation state.
	 * @param ui Current UI
	 * @param navigator View navigator
	 * @return the default navigation state, ignored if <code>null</code> is returned
	 */
	String getDefaultNavigationState(UI ui, ViewNavigator navigator);

}
