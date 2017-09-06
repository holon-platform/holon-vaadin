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

import java.util.HashMap;
import java.util.Map;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.navigator.ViewNavigator;
import com.holonplatform.vaadin.navigator.ViewNavigator.NavigationBuilder;
import com.holonplatform.vaadin.navigator.ViewNavigator.ViewNavigationException;
import com.holonplatform.vaadin.navigator.ViewWindowConfiguration;
import com.vaadin.ui.Window;

/**
 * Default {@link NavigationBuilder} implementation
 *
 * @since 5.0.0
 */
public class DefaultNavigationBuilder implements NavigationBuilder {

	private final ViewNavigator navigator;
	private final String viewName;

	private final Map<String, Object> parameters;

	public DefaultNavigationBuilder(ViewNavigator navigator, String viewName) {
		super();

		ObjectUtils.argumentNotNull(navigator, "ViewNavigator must be not null");
		ObjectUtils.argumentNotNull(viewName, "View name must be not null");

		this.navigator = navigator;
		this.viewName = viewName;
		this.parameters = new HashMap<>(8);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.navigator.ViewNavigator.NavigationBuilder#withParameter(java.lang.String,
	 * java.lang.Object)
	 */
	@Override
	public NavigationBuilder withParameter(String name, Object value) {
		ObjectUtils.argumentNotNull(name, "Parameter name must be not null");
		parameters.put(name, value);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.navigator.ViewNavigator.NavigationBuilder#navigate()
	 */
	@Override
	public void navigate() throws ViewNavigationException {
		navigator.navigateTo(viewName, parameters);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.navigator.ViewNavigator.NavigationBuilder#navigateInWindow(com.holonplatform.
	 * vaadin.navigator.ViewWindowConfiguration)
	 */
	@Override
	public Window navigateInWindow(ViewWindowConfiguration windowConfiguration) throws ViewNavigationException {
		return navigator.navigateInWindow(viewName, windowConfiguration, parameters);
	}

}
