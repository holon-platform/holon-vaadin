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
package com.holonplatform.vaadin.spring.internal;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.holonplatform.core.internal.Logger;
import com.holonplatform.vaadin.internal.VaadinLogger;
import com.holonplatform.vaadin.navigator.ViewNavigator;
import com.holonplatform.vaadin.navigator.internal.AbstractNavigatorBuilder;
import com.holonplatform.vaadin.navigator.internal.ContainerViewDisplay;
import com.holonplatform.vaadin.navigator.internal.NavigatorActuator;
import com.holonplatform.vaadin.navigator.internal.SingleContainerViewDisplay;
import com.holonplatform.vaadin.navigator.internal.ViewConfiguration;
import com.holonplatform.vaadin.navigator.internal.ViewConfiguration.ViewConfigurationException;
import com.holonplatform.vaadin.navigator.internal.ViewConfiguration.ViewWindowConfiguration;
import com.holonplatform.vaadin.navigator.internal.ViewNavigatorAdapter;
import com.holonplatform.vaadin.spring.AccessDeniedView;
import com.holonplatform.vaadin.spring.DefaultView;
import com.holonplatform.vaadin.spring.ErrorView;
import com.holonplatform.vaadin.spring.SpringViewNavigator;
import com.vaadin.navigator.NavigationStateManager;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.navigator.ViewProvider;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.spring.internal.Conventions;
import com.vaadin.spring.navigator.SpringNavigator;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.SingleComponentContainer;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

/**
 * Default {@link SpringViewNavigator} implementation.
 * 
 * @since 5.0.0
 */
@UIScope
public class DefaultSpringViewNavigator extends SpringNavigator implements ViewNavigatorAdapter, SpringViewNavigator {

	private static final long serialVersionUID = 8020818607275925064L;

	private static final Logger LOGGER = VaadinLogger.create();

	@Autowired(required = false)
	private SpringViewProvider viewProvider;

	/**
	 * Actuator
	 */
	private final NavigatorActuator<DefaultSpringViewNavigator> actuator;

	/**
	 * Error view provider
	 */
	protected ViewProvider errorViewProvider;

	/**
	 * Deferred error view configuration
	 */
	protected Class<? extends View> errorViewClass;

	/**
	 * Deferred access denied view configuration
	 */
	protected Class<? extends View> accessDeniedViewClass;

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
	public DefaultSpringViewNavigator(UI ui, NavigationStateManager stateManager, ViewDisplay display) {
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
	public DefaultSpringViewNavigator() {
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
	 * @see com.vaadin.navigator.Navigator#setErrorProvider(com.vaadin.navigator.ViewProvider)
	 */
	@Override
	public void setErrorProvider(ViewProvider provider) {
		super.setErrorProvider(provider);
		this.errorViewProvider = provider;
	}

	/**
	 * Deferred error view class configuration
	 * @param errorViewClass View class to use as error view
	 */
	public void setErrorViewClass(Class<? extends View> errorViewClass) {
		this.errorViewClass = errorViewClass;
	}

	/**
	 * Deferred access denied view class configuration
	 * @param accessDeniedViewClass View class to use as access denied view
	 */
	public void setAccessDeniedViewClass(Class<? extends View> accessDeniedViewClass) {
		this.accessDeniedViewClass = accessDeniedViewClass;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.spring.navigator.SpringNavigator#init(com.vaadin.ui.UI, com.vaadin.ui.ComponentContainer)
	 */
	@Override
	public void init(UI ui, ComponentContainer container) {
		init(ui, new UriFragmentManager(ui.getPage()), new ContainerViewDisplay(container));
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.spring.navigator.SpringNavigator#init(com.vaadin.ui.UI, com.vaadin.ui.SingleComponentContainer)
	 */
	@Override
	public void init(UI ui, SingleComponentContainer container) {
		init(ui, new UriFragmentManager(ui.getPage()), new SingleContainerViewDisplay(container));
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.spring.navigator.SpringNavigator#init(com.vaadin.ui.UI, com.vaadin.navigator.ViewDisplay)
	 */
	@Override
	public void init(UI ui, ViewDisplay display) {
		init(ui, new UriFragmentManager(ui.getPage()), display);
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.navigator.Navigator#init(com.vaadin.ui.UI, com.vaadin.navigator.NavigationStateManager,
	 * com.vaadin.navigator.ViewDisplay)
	 */
	@Override
	public void init(UI ui, NavigationStateManager stateManager, ViewDisplay display) {
		super.init(ui, stateManager, actuator.initNavigator(ui, display));
		// setup special views
		configureViews();
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
	 * Configure special type Views
	 * @throws ViewConfigurationException Error during view configuration
	 */
	@SuppressWarnings("unchecked")
	protected void configureViews() throws ViewConfigurationException {
		ApplicationContext applicationContext = getWebApplicationContext();
		if (applicationContext != null) {

			// default view
			if (getActuator().getDefaultViewName() == null) {
				// if not explicitly setted, lookup in context
				String[] beanNames = applicationContext.getBeanNamesForAnnotation(DefaultView.class);
				if (beanNames != null && beanNames.length > 0) {
					if (beanNames.length > 1) {
						LOGGER.warn("More than one bean annotated with @DefaultView was found in context " + "("
								+ beanNames.length + "), no default view will be configured in Navigator.");
					} else {
						Class<?> type = applicationContext.getType(beanNames[0]);
						if (!View.class.isAssignableFrom(type)) {
							throw new ViewConfigurationException(
									"A bean annotated with @DefaultView was found but does "
											+ "not implement navigator View class: " + type.getName());
						}
						// set default view name
						String viewName = Conventions.deriveMappingForView(type, type.getAnnotation(SpringView.class));
						if (viewName != null) {
							getActuator().setDefaultViewName(viewName);
							LOGGER.info("Configured default view " + type.getName() + " with name: " + viewName);
						}
					}
				}
			}

			// Error view
			if (errorViewProvider == null) {
				// check deferred config
				if (errorViewClass != null) {
					setErrorView(errorViewClass);
				} else {
					// if not explicitly setted, lookup in context
					String[] beanNames = applicationContext.getBeanNamesForAnnotation(ErrorView.class);
					if (beanNames != null && beanNames.length > 0) {
						if (beanNames.length > 1) {
							LOGGER.warn("More than one bean annotated with @ErrorView was found in context " + "("
									+ beanNames.length + "), no error view will be configured in Navigator.");
						} else {
							Class<?> type = applicationContext.getType(beanNames[0]);
							if (!View.class.isAssignableFrom(type)) {
								throw new ViewConfigurationException(
										"A bean annotated with @ErrorView was found but does "
												+ "not implement navigator View class: " + type.getName());
							}
							// set error view
							setErrorView((Class<? extends View>) type);
							LOGGER.info("Configured error view: " + type.getName());
						}
					}
				}
			}

			// Access denied view
			if (viewProvider != null) {
				if (accessDeniedViewClass != null) {
					viewProvider.setAccessDeniedViewClass(accessDeniedViewClass);
				} else {
					// if not explicitly setted, lookup in context
					String[] beanNames = applicationContext.getBeanNamesForAnnotation(AccessDeniedView.class);
					if (beanNames != null && beanNames.length > 0) {
						if (beanNames.length > 1) {
							LOGGER.warn("More than one bean annotated with @AccessDeniedView was found in context "
									+ "(" + beanNames.length
									+ "), no access denied view will be configured in Navigator.");
						} else {
							Class<?> type = applicationContext.getType(beanNames[0]);
							if (!View.class.isAssignableFrom(type)) {
								throw new ViewConfigurationException(
										"A bean annotated with @AccessDeniedView was found but does "
												+ "not implement navigator View class: " + type.getName());
							}
							// set error view
							viewProvider.setAccessDeniedViewClass((Class<? extends View>) type);
							LOGGER.info("Configured access denied view: " + type.getName());
						}
					}
				}
			}

		}
	}

	// ------- Builder

	/**
	 * Builder to create {@link SpringViewNavigator} instances.
	 */
	public static class SpringViewNavigatorBuilder
			extends AbstractNavigatorBuilder<SpringViewNavigator.Builder, DefaultSpringViewNavigator>
			implements SpringViewNavigator.Builder {

		public SpringViewNavigatorBuilder() {
			super(new DefaultSpringViewNavigator());
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.navigator.internal.AbstractNavigatorBuilder#builder()
		 */
		@Override
		protected com.holonplatform.vaadin.spring.SpringViewNavigator.Builder builder() {
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.spring.navigator.SpringViewNavigator.Builder#errorView(java.lang.Class)
		 */
		@Override
		public com.holonplatform.vaadin.spring.SpringViewNavigator.Builder errorView(Class<? extends View> viewClass) {
			navigator.setErrorViewClass(viewClass);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.spring.SpringViewNavigator.Builder#accessDeniedView(java.lang.Class)
		 */
		@Override
		public com.holonplatform.vaadin.spring.SpringViewNavigator.Builder accessDeniedView(
				Class<? extends View> viewClass) {
			navigator.setAccessDeniedViewClass(viewClass);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.spring.SpringViewNavigator.Builder#build()
		 */
		@Override
		public SpringViewNavigator build() {
			return navigator;
		}

		/**
		 * Builder method for internal use
		 * @return DefaultSpringViewNavigator instance
		 */
		public DefaultSpringViewNavigator buildInternal() {
			return navigator;
		}

	}

}
