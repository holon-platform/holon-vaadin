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

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.navigator.ViewNavigator.NavigatorBuilder;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.navigator.ViewProvider;

/**
 * Base {@link NavigatorBuilder} class.
 * 
 * @since 5.0.0
 */
public abstract class AbstractNavigatorBuilder<B extends NavigatorBuilder<B>, C extends Navigator & ViewNavigatorAdapter>
		implements NavigatorBuilder<B> {

	protected final C navigator;

	public AbstractNavigatorBuilder(C navigator) {
		super();
		this.navigator = navigator;
	}

	/**
	 * Gets the concrete builder
	 * @return Concrete builder
	 */
	protected abstract B builder();

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.navigator.ViewNavigator.NavigatorBuilder#maxNavigationHistorySize(int)
	 */
	@Override
	public B maxNavigationHistorySize(int maxSize) {
		navigator.getActuator().setMaxNavigationHistorySize(maxSize);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.navigator.ViewNavigator.NavigatorBuilder#addProvider(com.vaadin.navigator.ViewProvider)
	 */
	@Override
	public B addProvider(ViewProvider provider) {
		navigator.addProvider(provider);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.navigator.ViewNavigator.NavigatorBuilder#defaultViewName(java.lang.String)
	 */
	@Override
	public B defaultViewName(String defaultViewName) {
		navigator.getActuator().setDefaultViewName(defaultViewName);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.navigator.ViewNavigator.NavigatorBuilder#navigateToDefaultViewWhenViewNotAvailable(
	 * boolean)
	 */
	@Override
	public B navigateToDefaultViewWhenViewNotAvailable(boolean navigateToDefaultViewWhenViewNotAvailable) {
		navigator.getActuator().setNavigateToDefaultViewWhenViewNotAvailable(navigateToDefaultViewWhenViewNotAvailable);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.navigator.ViewNavigator.NavigatorBuilder#errorView(com.vaadin.navigator.View)
	 */
	@Override
	public B errorView(View errorView) {
		ObjectUtils.argumentNotNull(errorView, "View to set must not be null");
		navigator.setErrorView(errorView);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.navigator.ViewNavigator.NavigatorBuilder#errorViewProvider(com.vaadin.navigator.
	 * ViewProvider)
	 */
	@Override
	public B errorViewProvider(ViewProvider errorViewProvider) {
		navigator.setErrorProvider(errorViewProvider);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.navigator.ViewNavigator.NavigatorBuilder#withViewChangeListener(com.vaadin.navigator.
	 * ViewChangeListener)
	 */
	@Override
	public B withViewChangeListener(ViewChangeListener viewChangeListener) {
		navigator.addViewChangeListener(viewChangeListener);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.navigator.ViewNavigator.NavigatorBuilder#authenticationEnabled(boolean)
	 */
	@Override
	public B authenticationEnabled(boolean authenticationEnabled) {
		navigator.getActuator().setAuthenticationEnabled(authenticationEnabled);
		return builder();
	}

}
