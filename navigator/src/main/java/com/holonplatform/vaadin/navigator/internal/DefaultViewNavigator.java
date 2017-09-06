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

import java.util.Map;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.navigator.ViewNavigator;
import com.holonplatform.vaadin.navigator.ViewWindowConfiguration;
import com.vaadin.navigator.NavigationStateManager;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.navigator.ViewProvider;
import com.vaadin.server.Page;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.SingleComponentContainer;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

/**
 * Default {@link ViewNavigator} implementation extending Vaadin default {@link Navigator}
 * 
 * @since 5.0.0
 */
public class DefaultViewNavigator extends Navigator implements ViewNavigatorAdapter {

	private static final long serialVersionUID = 8692627814103603131L;

	/**
	 * Actuator
	 */
	private final NavigatorActuator<DefaultViewNavigator> actuator;

	/**
	 * Creates a navigator that is tracking the active view using URI fragments of the {@link Page} containing the given
	 * UI and replacing the contents of a {@link ComponentContainer} with the active view.
	 * <p>
	 * All components of the container are removed each time before adding the active {@link View}.
	 * <p>
	 * @param ui The UI to which this Navigator is attached.
	 * @param container The ComponentContainer whose contents should be replaced with the active view on view change
	 */
	public DefaultViewNavigator(UI ui, ComponentContainer container) {
		this(ui, new ComponentContainerViewDisplay(container));
	}

	/**
	 * Creates a navigator that is tracking the active view using URI fragments of the {@link Page} containing the given
	 * UI and replacing the contents of a {@link SingleComponentContainer} with the active view.
	 * @param ui The UI to which this Navigator is attached.
	 * @param container The SingleComponentContainer whose contents should be replaced with the active view on view
	 *        change
	 */
	public DefaultViewNavigator(UI ui, SingleComponentContainer container) {
		this(ui, new SingleComponentContainerViewDisplay(container));
	}

	/**
	 * Creates a navigator that is tracking the active view using URI fragments of the {@link Page} containing the given
	 * UI.
	 * @param ui The UI to which this Navigator is attached.
	 * @param display The ViewDisplay used to display the views.
	 */
	public DefaultViewNavigator(UI ui, ViewDisplay display) {
		this(ui, new UriFragmentManager(ui.getPage()), display);
	}

	/**
	 * Creates a navigator.
	 * <p>
	 * When a custom navigation state manager is not needed, use one of the other constructors which use a URI fragment
	 * based state manager.
	 * </p>
	 * @param ui The UI to which this Navigator is attached.
	 * @param stateManager The NavigationStateManager keeping track of the active view and enabling bookmarking and
	 *        direct navigation or null to use the default implementation
	 * @param display The ViewDisplay used to display the views handled by this navigator
	 */
	public DefaultViewNavigator(UI ui, NavigationStateManager stateManager, ViewDisplay display) {
		this();
		init(ui, stateManager, display);
	}

	/**
	 * Default construtor.
	 * <p>
	 * With this constructor, {@link #init(UI, NavigationStateManager, ViewDisplay)} method is not called, since
	 * navigator initialization is delegated to subclasses.
	 * </p>
	 */
	public DefaultViewNavigator() {
		super();
		this.actuator = new NavigatorActuator<>(this);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.navigator.internal.ViewNavigatorAdapter#getActuator()
	 */
	@Override
	public NavigatorActuator<?> getActuator() {
		return actuator;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.navigator.Navigator#init(com.vaadin.ui.UI, com.vaadin.navigator.NavigationStateManager,
	 * com.vaadin.navigator.ViewDisplay)
	 */
	@Override
	protected void init(UI ui, NavigationStateManager stateManager, ViewDisplay display) {
		super.init(ui, stateManager, actuator.initNavigator(ui, display));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.navigator.internal.ViewNavigatorAdapter#setup(com.vaadin.ui.UI,
	 * com.vaadin.navigator.NavigationStateManager, com.vaadin.navigator.ViewDisplay)
	 */
	@Override
	public void setup(UI ui, NavigationStateManager stateManager, ViewDisplay display) {
		init(ui, stateManager, display);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.navigator.internal.ViewNavigatorAdapter#updateCurrentNavigationState(com.vaadin.
	 * navigator.ViewChangeListener.ViewChangeEvent)
	 */
	@Override
	public void updateCurrentNavigationState(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent event) {
		super.updateNavigationState(event);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.navigator.internal.ViewNavigatorAdapter#navigateToState(java.lang.String)
	 */
	@Override
	public void navigateToState(String navigationState) {
		super.navigateTo(navigationState);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.navigator.internal.ViewNavigatorAdapter#navigateToView(com.vaadin.navigator.View,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public void navigateToView(View view, String viewName, String parameters) {
		super.navigateTo(view, viewName, parameters);
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.navigator.Navigator#addProvider(com.vaadin.navigator.ViewProvider)
	 */
	@Override
	public void addProvider(ViewProvider provider) {
		super.addProvider(actuator.addViewProvider(provider));
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.navigator.Navigator#removeProvider(com.vaadin.navigator.ViewProvider)
	 */
	@Override
	public void removeProvider(ViewProvider provider) {
		ViewProvider removed = actuator.removeViewProvider(provider);
		if (removed != null) {
			super.removeProvider(removed);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.navigator.Navigator#navigateTo(java.lang.String)
	 */
	@Override
	public void navigateTo(String navigationState) {
		actuator.navigateTo(navigationState);
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.navigator.Navigator#navigateTo(com.vaadin.navigator.View, java.lang.String, java.lang.String)
	 */
	@Override
	protected void navigateTo(View view, String viewName, String parameters) {
		actuator.navigateTo(view, viewName, parameters);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.vaadin.navigator.Navigator#updateNavigationState(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
	 */
	@Override
	protected void updateNavigationState(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent event) {
		super.updateNavigationState(event);
		actuator.postUpdateNavigationState(event);
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.navigator.Navigator#fireAfterViewChange(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
	 */
	@Override
	protected void fireAfterViewChange(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent event) {
		actuator.preAfterViewChange(event);
		super.fireAfterViewChange(event);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.ui.navigator.ViewNavigator#navigateTo(java.lang.String, java.util.Map)
	 */
	@Override
	public void navigateTo(String viewName, Map<String, Object> parameters) throws ViewNavigationException {
		try {
			navigateTo(actuator.buildNavigationState(viewName, parameters));
		} catch (Exception e) {
			throw new ViewNavigationException(viewName, e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.ui.navigator.ViewNavigator#navigateInWindow(java.lang.String,
	 * com.holonplatform.vaadin.ui.navigator.ViewWindowConfiguration, java.util.Map)
	 */
	@Override
	public Window navigateInWindow(String viewName, ViewWindowConfiguration windowConfiguration,
			Map<String, Object> parameters) throws ViewNavigationException {
		return actuator.navigateInWindow(viewName, windowConfiguration, parameters);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.ui.navigator.ViewNavigator#navigateBack()
	 */
	@Override
	public boolean navigateBack() throws ViewNavigationException {
		return actuator.navigateBack();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.ui.navigator.ViewNavigator#navigateToHome()
	 */
	@Override
	public void navigateToDefault() throws ViewNavigationException {
		actuator.navigateToDefault();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.ui.navigator.ViewNavigator#getDefaultViewName()
	 */
	@Override
	public String getDefaultViewName() {
		return actuator.getDefaultViewName();
	}

	/**
	 * Set the default view name. If a {@link View} with given <code>defaultViewName</code> is valid and provided by
	 * registered ViewProviders, it is used as target of {@link ViewNavigator#navigateToDefault()} method and as a
	 * fallback by {@link ViewNavigator#navigateBack()} method if no other View is available in history.
	 * @param defaultViewName the default view name to set
	 */
	public void setDefaultViewName(String defaultViewName) {
		actuator.setDefaultViewName(defaultViewName);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.ui.navigator.ViewNavigator#getCurrentViewName()
	 */
	@Override
	public String getCurrentViewName() {
		return actuator.getCurrentViewName();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.ui.navigator.ViewConfigurationProvider#getViewConfiguration(java.lang.Class)
	 */
	@Override
	public ViewConfiguration getViewConfiguration(Class<? extends View> viewClass) {
		return actuator.getViewConfiguration(viewClass);
	}

	/**
	 * {@link Builder} implementation for fluent-style {@link ViewNavigator} creation
	 */
	public static class ViewNavigatorBuilder extends AbstractNavigatorBuilder<Builder, DefaultViewNavigator>
			implements Builder {

		protected NavigationStateManager navigationStateManager;
		protected ViewDisplay viewDisplay;
		protected DefaultViewProvider defaultViewProvider;

		public ViewNavigatorBuilder() {
			super(new DefaultViewNavigator());
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.navigator.internal.AbstractNavigatorBuilder#builder()
		 */
		@Override
		protected Builder builder() {
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.navigator.ViewNavigator.Builder#withView(java.lang.String, java.lang.Class)
		 */
		@Override
		public Builder withView(String viewName, Class<? extends View> viewClass) {
			ObjectUtils.argumentNotNull(viewName, "View name must be not null");
			ObjectUtils.argumentNotNull(viewClass, "View class must be not null");
			if (defaultViewProvider == null) {
				defaultViewProvider = new DefaultViewProvider();
				addProvider(defaultViewProvider);
			}
			defaultViewProvider.registerView(viewName, viewClass);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.navigator.ViewNavigator.NavigatorBuilder#navigationStateManager(com.vaadin.
		 * navigator.NavigationStateManager)
		 */
		@Override
		public Builder navigationStateManager(NavigationStateManager navigationStateManager) {
			this.navigationStateManager = navigationStateManager;
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.navigator.ViewNavigator.NavigatorBuilder#viewDisplay(com.vaadin.navigator.
		 * ViewDisplay)
		 */
		@Override
		public Builder viewDisplay(ViewDisplay viewDisplay) {
			this.viewDisplay = viewDisplay;
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.navigator.ViewNavigator.NavigatorBuilder#viewDisplay(com.vaadin.ui.
		 * SingleComponentContainer)
		 */
		@Override
		public Builder viewDisplay(SingleComponentContainer container) {
			this.viewDisplay = new SingleContainerViewDisplay(container);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.navigator.ViewNavigator.NavigatorBuilder#viewDisplay(com.vaadin.ui.
		 * ComponentContainer)
		 */
		@Override
		public Builder viewDisplay(ComponentContainer container) {
			this.viewDisplay = new ContainerViewDisplay(container);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.navigator.ViewNavigator.NavigatorBuilder#buildAndBind(com.vaadin.ui.UI)
		 */
		@Override
		public ViewNavigator buildAndBind(UI ui) {
			navigator.setup(ui, navigationStateManager, viewDisplay);
			return navigator;
		}

	}

}
