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
package com.holonplatform.vaadin.spring;

import com.holonplatform.vaadin.navigator.ViewNavigator;
import com.holonplatform.vaadin.spring.internal.DefaultSpringViewNavigator;
import com.vaadin.navigator.NavigationStateManager;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.spring.access.ViewAccessControl;
import com.vaadin.spring.access.ViewInstanceAccessControl;
import com.vaadin.spring.navigator.SpringNavigator;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.SingleComponentContainer;
import com.vaadin.ui.UI;

/**
 * A {@link ViewNavigator} with Spring support.
 * 
 * <p>
 * Note that default implementation (the one returned by the builder obtained through {@link #builder()}) extends Vaadin
 * {@link SpringNavigator} ad it is designed to be used as a registered Spring bean with default
 * {@link SpringViewProvider} as View provider.
 * </p>
 * 
 * @since 5.0.0
 */
public interface SpringViewNavigator extends ViewNavigator {

	/**
	 * Initializes an injected navigator and registers {@link SpringViewProvider} for it.
	 * <p>
	 * The default navigation state manager (based on URI fragments) is used.
	 * </p>
	 * <p>
	 * Navigation is automatically initiated after {@code UI.init()} if a navigator was created. If at a later point
	 * changes are made to the navigator, {@code navigator.navigateTo(navigator.getState())} may need to be explicitly
	 * called to ensure the current view matches the navigation state.
	 * </p>
	 * @param ui The UI to which this Navigator is attached
	 * @param container The component container used to display the views handled by this navigator
	 */
	void init(UI ui, ComponentContainer container);

	/**
	 * Initializes an injected navigator and registers {@link SpringViewProvider} for it.
	 * <p>
	 * The default navigation state manager (based on URI fragments) is used.
	 * </p>
	 * <p>
	 * Navigation is automatically initiated after {@code UI.init()} if a navigator was created. If at a later point
	 * changes are made to the navigator, {@code navigator.navigateTo(navigator.getState())} may need to be explicitly
	 * called to ensure the current view matches the navigation state.
	 * </p>
	 * @param ui The UI to which this Navigator is attached
	 * @param container The single component container used to display the views handled by this navigator
	 */
	void init(UI ui, SingleComponentContainer container);

	/**
	 * Initializes an injected navigator and registers {@link SpringViewProvider} for it.
	 * <p>
	 * The default navigation state manager (based on URI fragments) is used.
	 * </p>
	 * <p>
	 * Navigation is automatically initiated after {@code UI.init()} if a navigator was created. If at a later point
	 * changes are made to the navigator, {@code navigator.navigateTo(navigator.getState())} may need to be explicitly
	 * called to ensure the current view matches the navigation state.
	 * </p>
	 * @param ui The UI to which this Navigator is attached
	 * @param viewDisplay The ViewDisplay used to display the views handled by this navigator
	 */
	void init(UI ui, ViewDisplay viewDisplay);

	/**
	 * Initializes an injected navigator and registers {@link SpringViewProvider} for it, using a custom
	 * {@link NavigationStateManager}.
	 * <p>
	 * Navigation is automatically initiated after {@code UI.init()} if a navigator was created. If at a later point
	 * changes are made to the navigator, {@code navigator.navigateTo(navigator.getState())} may need to be explicitly
	 * called to ensure the current view matches the navigation state.
	 * </p>
	 *
	 * @param ui The UI to which this Navigator is attached
	 * @param stateManager The NavigationStateManager keeping track of the active view and enabling bookmarking and
	 *        direct navigation or null for default
	 * @param viewDisplay The ViewDisplay used to display the views handled by this navigator
	 */
	void init(UI ui, NavigationStateManager stateManager, ViewDisplay viewDisplay);

	// Builder

	/**
	 * Builder to create {@link SpringViewNavigator} instances
	 * @return SpringViewNavigator builder
	 */
	static Builder builder() {
		return new DefaultSpringViewNavigator.SpringViewNavigatorBuilder();
	}

	/**
	 * Builder to create {@link SpringViewNavigator} instances
	 */
	public interface Builder extends NavigatorBuilder<Builder> {

		/**
		 * Registers a view class for the view to show when no other view matches the navigation state.
		 * <p>
		 * A bean of the given type is fetched on demand from the application context to be used as the error view. As a
		 * fallback mechanism for backwards compatibility, {@link Class#newInstance()} is used if no such bean is found.
		 * </p>
		 * <p>
		 * Note that an error view bean must be UI or prototype scoped.
		 * </p>
		 * @param viewClass The View class whose instance should be used as the error view
		 * @return this
		 */
		Builder errorView(final Class<? extends View> viewClass);

		/**
		 * Registers a view class for the view to show when a {@link ViewAccessControl} or a
		 * {@link ViewInstanceAccessControl} denies access to a view.
		 * <p>
		 * A bean of the given type is fetched on demand from the application context to be used as the access denied
		 * view. As a fallback mechanism for backwards compatibility, {@link Class#newInstance()} is used if no such
		 * bean is found.
		 * </p>
		 * <p>
		 * Note that an access denied view bean must be UI or prototype scoped.
		 * </p>
		 * @param viewClass The View class whose instance should be used as the access denied view
		 * @return this
		 */
		Builder accessDeniedView(final Class<? extends View> viewClass);

		/**
		 * Build the navigator instance
		 * @return SpringViewNavigator instance
		 */
		SpringViewNavigator build();

	}

}
