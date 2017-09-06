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

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Stack;

import com.holonplatform.auth.AuthContext;
import com.holonplatform.auth.annotations.Authenticate;
import com.holonplatform.auth.exceptions.AuthenticationException;
import com.holonplatform.core.Context;
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.core.internal.Logger;
import com.holonplatform.core.internal.utils.AnnotationUtils;
import com.holonplatform.core.utils.SizedStack;
import com.holonplatform.vaadin.VaadinHttpRequest;
import com.holonplatform.vaadin.internal.VaadinLogger;
import com.holonplatform.vaadin.navigator.SubViewContainer;
import com.holonplatform.vaadin.navigator.ViewNavigator;
import com.holonplatform.vaadin.navigator.ViewNavigator.ViewNavigationException;
import com.holonplatform.vaadin.navigator.ViewWindowConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.navigator.ViewProvider;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.util.SharedUtil;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;

/**
 * Adds {@link ViewNavigator} specific logic to a {@link Navigator}
 * 
 * @see ViewNavigator
 * @see DefaultViewNavigator
 * 
 * @since 5.0.0
 */
public class NavigatorActuator<N extends Navigator & ViewNavigatorAdapter> implements ViewDisplay {

	private static final long serialVersionUID = -5686299684526237717L;

	/**
	 * Logger
	 */
	private static final Logger LOGGER = VaadinLogger.create();

	/**
	 * Default navigation history limit
	 */
	public static final int DEFAULT_NAVIGATION_HISTORY_LIMIT = 100;

	/**
	 * Navigation fragments history
	 */
	private Stack<String> navigationHistory = new SizedStack<>(DEFAULT_NAVIGATION_HISTORY_LIMIT);

	/**
	 * View providers adapters registered in navigator (duplicates similar but private property in Navigator superclass)
	 */
	protected final List<ViewProviderAdapter> viewProviders = new LinkedList<>();

	/**
	 * Window references bound to window-displayed navigation states
	 */
	protected final Map<String, WeakReference<Window>> viewWindows = new HashMap<>(4);

	/**
	 * Whether to navigate to default view (if setted) when a view is not available from current navigation state
	 */
	private boolean navigateToDefaultViewWhenViewNotAvailable;

	/**
	 * Default view display, i.e. the ViewDisplay to use to display Views when is not requested to display View in a
	 * Window
	 */
	private ViewDisplay defaultViewDisplay;

	/**
	 * Current view name
	 */
	private String currentViewName;

	/**
	 * Default view name
	 */
	private String defaultViewName;

	/**
	 * Display current view in window
	 */
	private Window showInWindow = null;

	/**
	 * Wheter to navigate back when a View Window is closed
	 */
	private boolean navigateBackOnWindowClose = true;

	/**
	 * Whether the support for {@link Authenticate} annotation is enabled
	 */
	private boolean authenticationEnabled = true;

	/**
	 * Optional UI {@link Authenticate} annotation
	 */
	private Authenticate uiAuthenticate = null;

	/**
	 * Internal flag for redirect
	 */
	private boolean suspendAuthenticationCheck = false;

	/**
	 * Concrete Navigator
	 */
	protected final N navigator;

	/**
	 * Constructor
	 * @param navigator Concrete Navigator
	 */
	public NavigatorActuator(N navigator) {
		super();
		this.navigator = navigator;
	}

	/**
	 * Init actuator
	 * @param ui Navigator UI
	 * @param display Navigator ViewDisplay
	 * @return ViewDisplay to use with concrete Navigator
	 */
	public ViewDisplay initNavigator(UI ui, ViewDisplay display) {
		this.defaultViewDisplay = display;
		// check Authenticate annotation on UI
		uiAuthenticate = ui.getClass().getAnnotation(Authenticate.class);
		return this;
	}

	/**
	 * Default view display, i.e. the ViewDisplay to use to display Views when is not requested to display View in a
	 * Window
	 * @return the defaultViewDisplay Default ViewDisplay
	 */
	public ViewDisplay getDefaultViewDisplay() {
		return defaultViewDisplay;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.navigator.ViewDisplay#showView(com.vaadin.navigator.View)
	 */
	@Override
	public void showView(View view) {
		// check display in window
		try {
			if (showInWindow != null) {
				// set window contents
				showInWindow.setContent(ViewDisplayUtils.getViewContent(view));
				// open window
				UI ui = navigator.getUI();
				if (ui == null) {
					throw new ViewNavigationException(null,
							"Failed display View " + view.getClass().getName() + " in Window: no UI available");
				}
				openWindow(ui, showInWindow);
				// clear reference
				showInWindow = null;
			} else {
				// default
				if (getDefaultViewDisplay() != null) {
					getDefaultViewDisplay().showView(view);
				}
			}
		} finally {
			showInWindow = null;
		}
	}

	/**
	 * Navigation fragments history
	 * @return Navigation history stack
	 */
	protected Stack<String> getNavigationHistory() {
		if (navigationHistory == null) {
			navigationHistory = new SizedStack<>(DEFAULT_NAVIGATION_HISTORY_LIMIT);
		}
		return navigationHistory;
	}

	/**
	 * Set navigation history max size
	 * @param maxSize Max size of retained navigation fragments
	 */
	public void setMaxNavigationHistorySize(int maxSize) {
		this.navigationHistory = new SizedStack<>(maxSize);
	}

	/**
	 * Track given <code>navigationState</code> in navigation history
	 * @param navigationState Navigation state to track. If <code>null</code>, it is ignored
	 */
	private void trackInHistory(String navigationState) {
		if (navigationState != null) {
			String state = sanitizeNavigationState(navigationState);
			if (getNavigationHistory().isEmpty()) {
				getNavigationHistory().push(state);
			} else {
				String current = getNavigationHistory().peek();
				if (current == null || !current.equals(state)) {
					getNavigationHistory().push(state);
				}
			}
		}
	}

	/**
	 * Register and adapt a {@link ViewProvider}
	 * @param provider ViewProvider to add
	 * @return Adapted view provider
	 */
	public ViewProvider addViewProvider(ViewProvider provider) {
		ViewProviderAdapter adapted = adaptViewProvider(provider);
		this.viewProviders.add(adapted);
		return adapted;
	}

	/**
	 * Unregister a {@link ViewProvider}
	 * @param provider ViewProvider to remove
	 * @return Adapted view provider
	 */
	public ViewProvider removeViewProvider(ViewProvider provider) {
		ViewProviderAdapter viewProviderAdapter = findProviderAdapter(provider);
		if (viewProviderAdapter != null) {
			this.viewProviders.remove(viewProviderAdapter);
		}
		return viewProviderAdapter;
	}

	/**
	 * FInd the {@link ViewProviderAdapter} which wraps given concrete <code>provider</code>
	 * @param provider Concrete view provider
	 * @return Associated ViewProviderAdapter, or <code>null</code> if not found
	 */
	protected ViewProviderAdapter findProviderAdapter(ViewProvider provider) {
		if (provider != null) {
			for (ViewProviderAdapter adapter : viewProviders) {
				if (provider == adapter.getWrappedProvider()) {
					return adapter;
				}
			}
		}
		return null;
	}

	/**
	 * Apply ViewProvider adapter before adding given <code>provider</code> to navigator
	 * @param provider ViewProvider
	 * @return Adapted view provider
	 */
	protected ViewProviderAdapter adaptViewProvider(ViewProvider provider) {
		if (provider != null) {
			return new DefaultViewProviderAdapter(navigator, provider);
		}
		return null;
	}

	/**
	 * Gets whether to navigate to default view (if setted) when a view is not available from current navigation state
	 * @return <code>true</code> to navigate to default view (if setted) when a view is not available from current
	 *         navigation state
	 */
	public boolean isNavigateToDefaultViewWhenViewNotAvailable() {
		return navigateToDefaultViewWhenViewNotAvailable;
	}

	/**
	 * Set whether to navigate to default view (if setted) when a view is not available from current navigation state
	 * @param navigateToDefaultViewWhenViewNotAvailable <code>true</code> to navigate to default view (if setted) when a
	 *        view is not available from current navigation state
	 */
	public void setNavigateToDefaultViewWhenViewNotAvailable(boolean navigateToDefaultViewWhenViewNotAvailable) {
		this.navigateToDefaultViewWhenViewNotAvailable = navigateToDefaultViewWhenViewNotAvailable;
	}

	/**
	 * Finalization operations when a Window used to display a View is closed
	 * @param navigationState Navigation state
	 * @param view Displayed view
	 * @param viewConfiguration View configuration
	 * @param window view window
	 */
	protected void onViewWindowClose(final String navigationState, final View view,
			final ViewConfiguration viewConfiguration, final Window window) {
		if (navigateBackOnWindowClose) {
			// navigate back
			navigateBack();
		}
	}

	/**
	 * Navigate to given navigation state
	 * @param navigationState Navigation state
	 */
	public void navigateTo(String navigationState) {
		final Optional<ViewNavigator> previous = Context.get().threadScope()
				.flatMap(s -> s.get(ViewNavigator.CONTEXT_KEY, ViewNavigator.class));
		try {

			Context.get().threadScope().map(s -> s.put(ViewNavigator.CONTEXT_KEY, navigator));

			// check fallback to default
			if (isDefaultViewAvailable() && !isViewAvailable(navigationState)) {
				if (navigationState == null || navigationState.trim().equals("")
						|| isNavigateToDefaultViewWhenViewNotAvailable()) {
					navigateToDefault();
					return;
				}
			}

			// call default navigation method
			try {
				navigateToState(null, sanitizeNavigationState(navigationState));
			} catch (Exception e) {
				throw new ViewNavigationException(null, "Failed to navigate to state: [" + navigationState + "]", e);
			}

			// check navigation is not towards a Window
			if (!viewWindows.containsKey(navigationState)) {
				// close all current Views displayed in a Window
				closeAllViewWindows();
			}

			// track navigation state in history if current View is not volatile
			if (navigator.getCurrentView() != null && !isVolatile(navigator.getCurrentView(), navigationState)) {
				trackInHistory(navigationState);
			}

		} finally {
			Context.get().threadScope().map(s -> s.remove(ViewNavigator.CONTEXT_KEY));
			previous.ifPresent((n) -> Context.get().threadScope().map(s -> s.put(ViewNavigator.CONTEXT_KEY, n)));
		}
	}

	/**
	 * Navigate to given View
	 * @param view View instance
	 * @param viewName View name
	 * @param parameters View parameters
	 */
	public void navigateTo(View view, String viewName, String parameters) {
		// check view display
		if (getDefaultViewDisplay() == null) {
			LOGGER.warn("No ViewDisplay is bound to this Navigator! Check ViewNavigator init and setup.");
		}

		// check sub-view
		String parentViewName = null;
		final ViewConfiguration viewConfiguration = getViewConfiguration(view.getClass());
		if (viewConfiguration != null && viewConfiguration.isSubView()) {
			parentViewName = viewConfiguration.getParentViewName();
		}
		if (parentViewName != null) {
			// navigate to parent view container
			try {
				navigateToState(viewConfiguration, parentViewName);
			} catch (Exception e) {
				throw new ViewNavigationException(parentViewName, e);
			}
			// check navigation succeeded
			if (parentViewName.equals(getCurrentViewName())) {
				// check parent container
				View parent = navigator.getCurrentView();
				if (!(parent instanceof SubViewContainer)) {
					throw new ViewNavigationException(buildNavigationState(viewName, parameters),
							"Declared parent view name " + parentViewName + " of sub view " + viewName
									+ " is not a SubViewContainer");
				}
				// process view parameters
				Map<String, String> parsedParameters;
				try {
					parsedParameters = ViewNavigationUtils.parseParametersString(parameters, null);
					ViewNavigationUtils.setViewParameters(view, viewConfiguration, parsedParameters);
				} catch (Exception e) {
					throw new ViewNavigationException(viewName, e);
				}
				// display sub view
				View oldView = ((SubViewContainer) parent).getCurrentView();
				boolean accepted = ((SubViewContainer) parent).display(view, viewName, parsedParameters);
				// if accepted, fire lifecycle methods
				if (accepted) {
					DefaultViewNavigatorChangeEvent evt = new DefaultViewNavigatorChangeEvent(navigator, oldView, view,
							viewName, parameters, getViewWindow(buildNavigationState(viewName, parameters)));
					// onLeave on old view
					if (oldView != null) {
						ViewNavigationUtils.fireViewOnLeave(oldView, viewConfiguration, evt);
					}
					// enter and onShow on new view
					view.enter(evt);
					ViewNavigationUtils.fireViewOnShow(view, viewConfiguration, evt, false);
				}
			}
		} else {
			// default behaviour
			String navigationState = buildNavigationState(viewName, parameters);
			if (viewConfiguration != null && viewConfiguration.isForceInWindow()) {
				// force navigation in window
				Window window = buildViewWindow(navigationState, view, viewName, viewConfiguration, null);
				viewWindows.put(navigationState, new WeakReference<>(window));
				showInWindow = window;
			}
			navigateToView(viewConfiguration, view, viewName, parameters, navigationState);
		}
	}

	/**
	 * Navigate to previous View, if available
	 * @return true if back navigation succeded
	 * @throws ViewNavigationException Navigation error
	 */
	public boolean navigateBack() throws ViewNavigationException {

		// check current View is displayed in Window
		if (!closeCurrentViewWindow()) {
			// pop current if page not excluded from history
			if (!getNavigationHistory().isEmpty() && !isVolatile(navigator.getCurrentView(), null)) {
				getNavigationHistory().pop();
			}
		}

		// peek previous
		if (!getNavigationHistory().isEmpty()) {
			String navigationState = getNavigationHistory().peek();
			if (navigationState != null) {
				try {
					// check View was displayed in a window
					WeakReference<Window> windowRef = viewWindows.get(navigationState);
					if (windowRef != null && windowRef.get() != null) {
						// if was displayed in Window, focus the Window
						windowRef.get().focus();
						// update navigation state and current view
						String[] vnp = getViewNameAndParameters(navigationState);
						ViewChangeEvent event = new ViewChangeEvent(navigator, null, null, vnp[0], vnp[1]);
						navigator.updateCurrentNavigationState(event);
						postUpdateNavigationState(event);
					} else {
						// navigate to previous state
						navigateToState(null, navigationState);
					}
				} catch (Exception e) {
					throw new ViewNavigationException(null, "Failed to navigate to state: [" + navigationState + "]",
							e);
				}
				return true;
			}
		}
		// go to default view if nothing valid in navigation stack and a default view is available
		if (isDefaultViewAvailable()) {
			navigateToDefault();
			return true;
		}
		return false;
	}

	/**
	 * Navigate to default View, if available
	 * @throws ViewNavigationException Navigation error
	 */
	public void navigateToDefault() throws ViewNavigationException {
		String viewName = getDefaultViewName();
		if (viewName == null) {
			throw new ViewNavigationException(null, "No default view name defined");
		}
		try {
			navigateToState(null, getDefaultViewName());
		} catch (Exception e) {
			throw new ViewNavigationException(viewName, e);
		}
	}

	/**
	 * Navigate to the {@link View} identified by given <code>viewName</code> using the same behaviour of
	 * {@link #navigateTo(String)} but rendering the View contents in an application Window, using optional
	 * <code>windowConfiguration</code> to setup Window features.
	 * @param viewName View name
	 * @param windowConfiguration Optional Window configuration settings
	 * @param parameters Optional view parameters
	 * @return The UI Window in which the View is displayed
	 * @throws ViewNavigationException View with given name cannot be found or other view handling error
	 */
	public Window navigateInWindow(String viewName, ViewWindowConfiguration windowConfiguration,
			Map<String, Object> parameters) throws ViewNavigationException {

		// Get view instance
		final String navigationState = buildNavigationState(viewName, parameters);

		final View view;
		try {
			view = getView(navigationState);
		} catch (Exception e) {
			throw new ViewNavigationException(viewName, e);
		}

		if (view == null) {
			throw new ViewNavigationException(navigationState,
					"Failed to obtain a View using view name " + viewName + " from registered ViewProviders");
		}

		// Get view configuration
		final ViewConfiguration viewConfiguration = getViewConfiguration(view.getClass());

		Window window = buildViewWindow(navigationState, view, viewName, viewConfiguration, windowConfiguration);
		viewWindows.put(navigationState, new WeakReference<>(window));
		showInWindow = window;

		try {
			navigateTo(navigationState);
		} catch (Exception e) {
			throw new ViewNavigationException(viewName, e);
		}

		return window;

	}

	/**
	 * Method to call after navigation state update
	 * @param event View change event
	 */
	public void postUpdateNavigationState(ViewChangeEvent event) {
		this.currentViewName = event.getViewName();

		// navigation state
		final String navigationState = buildNavigationState(event.getViewName(), event.getParameters());

		// fire OnLeave on old view
		if (!viewWindows.containsKey(navigationState)) {
			// avoid to fire OnLeave listeners when the view was displayed in window
			if (event.getOldView() != null && !SharedUtil.equals(event.getOldView(), event.getNewView())) {
				ViewConfiguration configuration = getViewConfiguration(event.getOldView().getClass());
				if (configuration != null) {
					ViewNavigationUtils.fireViewOnLeave(event.getOldView(), configuration,
							DefaultViewNavigatorChangeEvent.create(event, navigator, null));
				} else {
					LOGGER.warn("Failed to obtain ViewConfiguration for view class " + event.getOldView().getClass()
							+ ": OnLeave methods firing skipped");
				}
			}
		}

		// set view parameters
		if (event.getNewView() != null) {
			ViewConfiguration configuration = getViewConfiguration(event.getNewView().getClass());
			if (configuration != null) {
				ViewNavigationUtils.setViewParameters(event.getNewView(), configuration, event.getParameters(), null);
			} else {
				LOGGER.warn("Failed to obtain ViewConfiguration for view class " + event.getNewView().getClass()
						+ ": View parameters setting skipped");
			}

			// fire OnShow methods declared to be fired at refresh
			if (configuration != null && SharedUtil.equals(event.getOldView(), event.getNewView())) {
				// is a refresh
				ViewNavigationUtils.fireViewOnShow(event.getNewView(), configuration,
						DefaultViewNavigatorChangeEvent.create(event, navigator, getViewWindow(navigationState)), true);
			}
		}
	}

	public void preAfterViewChange(ViewChangeEvent event) {
		// fire OnShow on new view
		if (event.getNewView() != null) {
			ViewConfiguration configuration = getViewConfiguration(event.getNewView().getClass());
			if (configuration != null) {
				ViewNavigationUtils.fireViewOnShow(event.getNewView(), configuration,
						DefaultViewNavigatorChangeEvent.create(event, navigator,
								getViewWindow(buildNavigationState(event.getViewName(), event.getParameters()))),
						false);
			} else {
				LOGGER.warn("Failed to obtain ViewConfiguration for view class " + event.getOldView().getClass()
						+ ": OnShow methods firing skipped");
			}
		}
	}

	/**
	 * Navigate to given state using concrete {@link Navigator}.
	 * <p>
	 * Authentication check using {@link Authenticate} annotation is performed before the actual view navigation.
	 * </p>
	 * @param viewConfiguration View configuration. If <code>null</code> the view configuration is obtained using
	 *        {@link #getViewConfiguration(Class)}
	 * @param navigationState Navigation state
	 */
	private void navigateToState(ViewConfiguration viewConfiguration, String navigationState) {
		// check authentication
		if (checkAuthentication(navigationState, viewConfiguration)) {
			navigator.navigateToState(navigationState);
		} else {
			// track view in history to allow backward navigation
			if (!isVolatile(viewConfiguration, navigationState)) {
				trackInHistory(navigationState);
			}
		}
	}

	/**
	 * Navigate to given View using concrete {@link Navigator}.
	 * <p>
	 * Authentication check using {@link Authenticate} annotation is performed before the actual view navigation.
	 * </p>
	 * @param viewConfiguration View configuration. If <code>null</code> the view configuration is obtained using
	 *        {@link #getViewConfiguration(Class)}
	 * @param view View instance
	 * @param viewName View name
	 * @param parameters View parameters
	 * @param navigationState Full navigation state
	 */
	private void navigateToView(ViewConfiguration viewConfiguration, View view, String viewName, String parameters,
			String navigationState) {
		ViewConfiguration cfg = (viewConfiguration != null) ? viewConfiguration
				: (view != null) ? getViewConfiguration(view.getClass()) : null;
		// check authentication
		if (checkAuthentication(navigationState, cfg)) {
			navigator.navigateToView(view, viewName, parameters);
		} else {
			// track view in history to allow backward navigation
			if (!isVolatile(cfg, navigationState)) {
				trackInHistory(navigationState);
			}
		}
	}

	/**
	 * Get the optional default view name. If a {@link View} with given <code>defaultViewName</code> is valid and
	 * provided by registered ViewProviders, it is used as target of {@link ViewNavigator#navigateToDefault()} method
	 * and as a fallback by {@link ViewNavigator#navigateBack()} method if no other View is available in history.
	 * @return Default view name
	 */
	public String getDefaultViewName() {
		return defaultViewName;
	}

	/**
	 * Set the default view name. If a {@link View} with given <code>defaultViewName</code> is valid and provided by
	 * registered ViewProviders, it is used as target of {@link ViewNavigator#navigateToDefault()} method and as a
	 * fallback by {@link ViewNavigator#navigateBack()} method if no other View is available in history.
	 * @param defaultViewName the default view name to set
	 */
	public void setDefaultViewName(String defaultViewName) {
		this.defaultViewName = defaultViewName;
	}

	/**
	 * Check if a default View is available
	 * @return <code>true</code> if a default View is available
	 */
	protected boolean isDefaultViewAvailable() {
		return getDefaultViewName() != null;
	}

	/**
	 * Get current (active) view name in navigator
	 * @return Current view name, or <code>null</code> if no view is currently active in navigator
	 */
	public String getCurrentViewName() {
		return currentViewName;
	}

	/**
	 * Get whether the support for {@link Authenticate} annotation on {@link View}s or {@link UI} is enabled.
	 * @return <code>true</code> if the support for {@link Authenticate} annotation on {@link View}s or {@link UI} is
	 *         enabled
	 * @see #setAuthenticationEnabled(boolean)
	 */
	public boolean isAuthenticationEnabled() {
		return authenticationEnabled;
	}

	/**
	 * Set whether the support for {@link Authenticate} annotation on {@link View}s or {@link UI} is enabled.
	 * <p>
	 * If enabled, the navigator will check the {@link Authenticate} annotation on {@link View}s or {@link UI} during
	 * navigation, and if present, requires the presence of an authenticated {@link AuthContext} obtained through
	 * {@link AuthContext#getCurrent()}. If an {@link AuthContext} is present but not authenticated, try to perform
	 * authentication using current {@link VaadinRequest} and the optional authentication schemes specified using
	 * {@link Authenticate#schemes()}. If the authentication is not successfull and a valid redirect URI is provided
	 * through {@link Authenticate#redirectURI()}, the navigation is redirected to that URI. If the URI does not specify
	 * a scheme, or the scheme is equal to <code>view://</code>, the navigation is redirected to the navigation state
	 * following the <code>view://</code> scheme or to the navigation state equal to the
	 * </p>
	 * <p>
	 * The authentication support is enabled by default.
	 * </p>
	 * {@link Authenticate#redirectURI()} attribute if a URI scheme is not specified.
	 * @param authenticationEnabled <code>true</code> to enable the {@link Authenticate} support, <code>false</code> to
	 *        disable.
	 */
	public void setAuthenticationEnabled(boolean authenticationEnabled) {
		this.authenticationEnabled = authenticationEnabled;
	}

	/**
	 * Check if given <code>view</code> is volatile, i.e. not to be tracked in navigation history
	 * @param view View to check
	 * @param navigationState Navigation state bound to View
	 * @return <code>true</code> if given <code>view</code> is volatile
	 */
	protected boolean isVolatile(View view, String navigationState) {
		if (view != null) {
			return isVolatile(getViewConfiguration(view.getClass()), navigationState);
		}
		return false;
	}

	/**
	 * Check if the view bound to given <code>viewConfiguration</code> is volatile, i.e. not to be tracked in navigation
	 * history
	 * @param viewConfiguration ViewConfiguration to check
	 * @param navigationState Navigation state
	 * @return <code>true</code> if volatile
	 */
	protected boolean isVolatile(ViewConfiguration viewConfiguration, String navigationState) {
		if (navigationState != null && viewWindows.containsKey(navigationState)) {
			// Views displayed in Window are never volatile
			return false;
		}
		if (viewConfiguration != null) {
			return viewConfiguration.isVolatile();
		}
		return false;
	}

	/**
	 * Build a full navigation state String using given <code>viewName</code> and <code>parameters</code> String.
	 * @param viewName View name
	 * @param parameters Optional parameters String
	 * @return Navigation state String
	 */
	protected String buildNavigationState(String viewName, String parameters) {
		String navigationState = viewName;
		if (parameters != null && !parameters.isEmpty()) {
			navigationState += "/" + parameters;
		}
		return navigationState;
	}

	/**
	 * Build a full navigation state String using given <code>viewName</code> and appending any parameter in serialized
	 * form
	 * @param viewName View name
	 * @param parameters View parameters
	 * @return Navigation state String
	 */
	public String buildNavigationState(String viewName, Map<String, Object> parameters) {
		StringBuilder sb = new StringBuilder();

		boolean addSeparator = false;
		if (viewName != null) {
			addSeparator = !viewName.trim().endsWith("/");
			sb.append(viewName.trim());
		}
		String parametersString = ViewNavigationUtils.generateParametersString(parameters, null);
		if (parametersString != null && !parametersString.isEmpty()) {
			if (addSeparator) {
				sb.append("/");
			}
			sb.append(parametersString);
		}

		return sb.toString();
	}

	public ViewConfiguration getViewConfiguration(Class<? extends View> viewClass) {
		ViewConfiguration cfg = getViewConfigurationCache().getViewConfiguration(viewClass);
		if (cfg == null) {
			// build view configuration and store in cache
			cfg = getViewConfigurationCache().storeViewConfiguration(viewClass,
					ViewNavigationUtils.buildViewConfiguration(viewClass));
		}
		return cfg;
	}

	/**
	 * Get the {@link ViewConfigurationCache} to use
	 * @return By default returns {@link DefaultViewConfigurationCache#INSTANCE}
	 */
	protected ViewConfigurationCache getViewConfigurationCache() {
		return DefaultViewConfigurationCache.INSTANCE;
	}

	/**
	 * Remove ! character in navigation state if present at the start of the state string
	 * @param navigationState Navigation state string
	 * @return Sanitized navigation state string
	 */
	protected static String sanitizeNavigationState(String navigationState) {
		if (navigationState != null && navigationState.startsWith("!")) {
			return navigationState.substring(1);
		}
		return navigationState;
	}

	/**
	 * Check if a valid View is available using given <code>navigationState</code>
	 * @param navigationState Navigation state
	 * @return <code>true</code> if a valid View is available using given <code>navigationState</code>,
	 *         <code>false</code> otherwise
	 */
	protected boolean isViewAvailable(String navigationState) {
		ViewProvider longestViewNameProvider = getViewProvider(navigationState);
		if (longestViewNameProvider != null) {
			return longestViewNameProvider.getViewName(navigationState) != null;
		}
		return false;
	}

	/**
	 * Get View instance using given <code>navigationState</code>
	 * @param navigationState Navigation state
	 * @return View instance, or <code>null</code> if not available from any ViewProvider
	 */
	protected View getView(String navigationState) {
		ViewProvider longestViewNameProvider = getViewProvider(navigationState);
		if (longestViewNameProvider != null) {
			String viewName = longestViewNameProvider.getViewName(navigationState);
			if (viewName != null) {
				return longestViewNameProvider.getView(viewName);
			}
		}
		return null;
	}

	/**
	 * Get the View instance and the view name form given <code>navigationState</code>
	 * @param navigationState Navigation state
	 * @return View instance and view name
	 */
	/*
	 * protected ViewAndName getViewAndName(String navigationState) { ViewAndName vn = new ViewAndName(); ViewProvider
	 * longestViewNameProvider = getViewProvider(navigationState); if (longestViewNameProvider != null) { String
	 * viewName = longestViewNameProvider.getViewName(navigationState); vn.name = viewName; if (viewName != null) {
	 * vn.view = longestViewNameProvider.getView(viewName); } } return vn; }
	 */

	/**
	 * Get View name and parameters from given navigation state
	 * @param navigationState Navigation state
	 * @return An array of view name at index 0 and view parameters at index 1
	 */
	protected String[] getViewNameAndParameters(String navigationState) {
		String[] result = new String[] { "", "" };
		if (navigationState != null) {
			String viewName = null;
			ViewProvider longestViewNameProvider = getViewProvider(navigationState);
			if (longestViewNameProvider != null) {
				viewName = longestViewNameProvider.getViewName(navigationState);
			}
			if (viewName != null) {
				result[0] = viewName;
				if (navigationState.length() > viewName.length()) {
					String parameters = navigationState.substring(viewName.length());
					if (parameters.startsWith("/")) {
						parameters = (parameters.length() > 1) ? parameters.substring(1) : "";
					} else {
						result[1] = parameters;
					}
				}
			}
		}
		return result;
	}

	/**
	 * Get view provider that handles the given navigation state.
	 * @param state Navigation state
	 * @return suitable provider
	 */
	protected ViewProvider getViewProvider(String state) {
		String longestViewName = null;
		ViewProvider longestViewNameProvider = null;
		for (ViewProvider provider : viewProviders) {
			String viewName = provider.getViewName(state);
			if (null != viewName && (longestViewName == null || viewName.length() > longestViewName.length())) {
				longestViewName = viewName;
				longestViewNameProvider = provider;
			}
		}
		return longestViewNameProvider;
	}

	/**
	 * Build and configure the Window to use to display a View.
	 * @param navigationState Navigation state
	 * @param view View to display
	 * @param viewName View name
	 * @param viewConfiguration View configuration
	 * @param windowConfiguration Optional Window configuration
	 * @return Window to use to display the given View
	 */
	@SuppressWarnings("serial")
	protected Window buildViewWindow(final String navigationState, final View view, final String viewName,
			final ViewConfiguration viewConfiguration, ViewWindowConfiguration windowConfiguration) {

		// window configuration
		ViewWindowConfiguration viewWindowConfiguration = windowConfiguration;
		if (viewWindowConfiguration == null && viewConfiguration != null) {
			viewWindowConfiguration = viewConfiguration.getWindowConfiguration();
		}

		final Window window = createViewWindow(navigationState, viewConfiguration, viewWindowConfiguration);
		window.setModal(true);

		window.addCloseListener(new CloseListener() {

			@Override
			public void windowClose(CloseEvent e) {
				onViewWindowClose(navigationState, view, viewConfiguration, e.getWindow());
			}
		});

		return window;

	}

	/**
	 * Create a {@link Window} to display a View.
	 * @param navigationState Navigation state
	 * @param viewConfiguration View Configuration
	 * @param windowConfiguration Window configuration settings, can be <code>null</code>
	 * @return Window to display a View
	 */
	protected Window createViewWindow(String navigationState, ViewConfiguration viewConfiguration,
			ViewWindowConfiguration windowConfiguration) throws ViewNavigationException {
		String caption = null;
		if (viewConfiguration != null) {
			caption = LocalizationContext.translate(viewConfiguration.getCaptionMessageCode(),
					viewConfiguration.getCaption(), true);
		}

		final Window wnd = new DefaultViewWindow(caption, navigationState);

		String w = ViewWindowConfiguration.DEFAULT_WINDOW_WIDTH;
		String h = ViewWindowConfiguration.DEFAULT_WINDOW_WIDTH;

		boolean closable = true;
		boolean resizable = true;

		if (windowConfiguration != null) {
			w = windowConfiguration.getWindowWidth();
			h = windowConfiguration.getWindowHeight();
			closable = windowConfiguration.isClosable();
			resizable = windowConfiguration.isResizable();
		}

		wnd.setWidth(w);
		wnd.setHeight(h);

		wnd.setClosable(closable);
		wnd.setResizable(resizable);
		wnd.setDraggable(true);

		wnd.addStyleName(ViewWindowConfiguration.DEFAULT_WINDOW_STYLE_NAME);

		return wnd;
	}

	/**
	 * Get the view navigation window associated to given state, if present.
	 * @param navigationState Navigation state
	 * @return view navigation window, <code>null</code> if not present
	 */
	protected Window getViewWindow(String navigationState) {
		WeakReference<Window> wr = viewWindows.get(navigationState);
		if (wr != null) {
			return wr.get();
		}
		return null;
	}

	/**
	 * Close all opened Windows displaying Views
	 */
	protected void closeAllViewWindows() {
		for (Entry<String, WeakReference<Window>> entry : viewWindows.entrySet()) {

			// remove Window
			WeakReference<Window> windowRef = viewWindows.get(entry.getKey());
			if (windowRef != null && windowRef.get() != null && windowRef.get().getParent() != null) {
				// if was displayed in Window, close the Window
				try {
					navigateBackOnWindowClose = false;
					windowRef.get().close();
				} finally {
					navigateBackOnWindowClose = true;
				}
			}
			viewWindows.remove(entry.getKey());

			// remove from history
			if (!getNavigationHistory().isEmpty()) {
				getNavigationHistory().remove(entry.getKey());
			}

		}
	}

	/**
	 * If current View is displayed in a Window, close the Window and removes navigation state from history
	 * @return <code>true</code> if current View was displayed in a Window and it was closed
	 */
	protected boolean closeCurrentViewWindow() {
		// check current View is displayed in Window
		if (!getNavigationHistory().isEmpty() && viewWindows.containsKey(getNavigationHistory().peek())) {

			// remove from history
			String navigationState = getNavigationHistory().pop();

			WeakReference<Window> windowRef = viewWindows.get(navigationState);
			if (windowRef != null && windowRef.get() != null && windowRef.get().getParent() != null) {
				// if was displayed in Window, close the Window
				try {
					navigateBackOnWindowClose = false;
					windowRef.get().close();
				} finally {
					navigateBackOnWindowClose = true;
				}
			}
			viewWindows.remove(navigationState);

			// closed and removed from history
			return true;

		}
		return false;
	}

	/**
	 * Open a Window using given <code>ui</code>. Any Window which <code>equals</code> to <code>window</code> is removed
	 * from UI before adding the new Window.
	 * @param window Window to open
	 */
	private static void openWindow(UI ui, Window window) {
		List<Window> toRemove = new LinkedList<>();
		for (Window wnd : ui.getWindows()) {
			if (window.equals(wnd)) {
				toRemove.add(wnd);
			}
		}
		for (Window wnd : toRemove) {
			ui.removeWindow(wnd);
		}
		ui.addWindow(window);
	}

	// Authentication

	/**
	 * Get the optional UI {@link Authenticate} annotation.
	 * @return UI {@link Authenticate} annotation
	 */
	protected Optional<Authenticate> getUIAuthentication() {
		return Optional.ofNullable(uiAuthenticate);
	}

	/**
	 * Check authentication using {@link Authenticate} annotation on given view class or the {@link UI} class to which
	 * navigator is bound.
	 * @param navigationState Navigation state
	 * @param viewConfiguration View configuration
	 * @return <code>true</code> if authentication is not required or if it is required and the current
	 *         {@link AuthContext} is authenticated, <code>false</code> otherwise
	 * @throws ViewNavigationException Missing {@link AuthContext} from context or other unexpected error
	 */
	protected boolean checkAuthentication(final String navigationState, final ViewConfiguration viewConfiguration)
			throws ViewNavigationException {
		if (!suspendAuthenticationCheck) {
			Authenticate authc = (viewConfiguration != null)
					? viewConfiguration.getAuthentication().orElse(uiAuthenticate) : uiAuthenticate;
			if (authc != null) {

				// check auth context
				final AuthContext authContext = AuthContext.getCurrent().orElseThrow(() -> new ViewNavigationException(
						navigationState,
						"No AuthContext available as Context resource: failed to process Authenticate annotation on View or UI"));

				if (!authContext.getAuthentication().isPresent()) {
					// not authenticated, try to authenticate from request
					final VaadinRequest request = VaadinService.getCurrentRequest();
					if (request != null) {
						try {
							authContext.authenticate(VaadinHttpRequest.create(request));
							// authentication succeded
							return true;
						} catch (@SuppressWarnings("unused") AuthenticationException e) {
							// failed, ignore
						}
					}
					onAuthenticationFailed(authc, navigationState);
					return false;
				}

			}
		}
		return true;
	}

	/**
	 * Invoked when authentication is missing or failed using {@link AuthContext} during {@link Authenticate} annotation
	 * processing.
	 * @param authc Authenticate annotation
	 * @param navigationState Navigation state
	 * @throws ViewNavigationException If cannot be performed any redirection using {@link Authenticate#redirectURI()}
	 */
	protected void onAuthenticationFailed(final Authenticate authc, final String navigationState)
			throws ViewNavigationException {
		// redirect
		String redirectURI = AnnotationUtils.getStringValue(authc.redirectURI());
		if (redirectURI != null) {
			// check view scheme
			String viewNavigationState = getViewNavigationState(redirectURI);
			if (viewNavigationState != null) {
				try {
					suspendAuthenticationCheck = true;
					navigator.navigateTo(viewNavigationState);
				} finally {
					suspendAuthenticationCheck = false;
				}
			} else {
				// try to open the URI as an URL
				Page.getCurrent().open(redirectURI, null);
			}
		} else {
			throw new ViewNavigationException(navigationState, "Authentication required");
		}
	}

	/**
	 * Get a view navigation state from redirect URI, if URI scheme is absent or is
	 * {@link ViewNavigator#VIEW_URI_SCHEME}.
	 * @param redirectURI Redirect URI
	 * @return View navigation state
	 */
	private static String getViewNavigationState(String redirectURI) {
		if (redirectURI != null) {
			int schemeSeparator = redirectURI.indexOf("://");
			if (schemeSeparator < 0) {
				return redirectURI;
			}
			if (redirectURI.startsWith(ViewNavigator.VIEW_URI_SCHEME)) {
				return redirectURI.substring(ViewNavigator.VIEW_URI_SCHEME.length());
			}
		}
		return null;
	}

}
