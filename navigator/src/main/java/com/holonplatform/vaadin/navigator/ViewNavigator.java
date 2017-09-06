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
package com.holonplatform.vaadin.navigator;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import com.holonplatform.auth.AuthContext;
import com.holonplatform.auth.annotations.Authenticate;
import com.holonplatform.core.Context;
import com.holonplatform.vaadin.navigator.annotations.OnLeave;
import com.holonplatform.vaadin.navigator.annotations.OnShow;
import com.holonplatform.vaadin.navigator.annotations.StatefulView;
import com.holonplatform.vaadin.navigator.annotations.SubViewOf;
import com.holonplatform.vaadin.navigator.annotations.ViewParameter;
import com.holonplatform.vaadin.navigator.internal.DefaultNavigationBuilder;
import com.holonplatform.vaadin.navigator.internal.DefaultViewNavigator;
import com.holonplatform.vaadin.navigator.internal.ViewConfiguration.ViewWindowConfiguration;
import com.holonplatform.vaadin.navigator.internal.ViewDisplayUtils;
import com.holonplatform.vaadin.navigator.internal.ViewNavigationUtils;
import com.vaadin.navigator.NavigationStateManager;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.navigator.ViewProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.Registration;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.SingleComponentContainer;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

/**
 * Navigator to manage application UI {@link View} configuration and display. Extends the features and the behaviour of
 * a standard Vaadin {@link Navigator} component.
 * 
 * <p>
 * View parameters will be automatically injected in View instance using {@link ViewParameter} annotated fields. <br>
 * Supported parameter value types are:
 * <ul>
 * <li>{@link String}</li>
 * <li>{@link Number}s</li>
 * <li>{@link Boolean}</li>
 * <li>{@link Enum} (ordinal value must be used for enum values serialization)</li>
 * <li>{@link Date} using date format pattern {@link ViewParameter#DEFAULT_DATE_PATTERN}</li>
 * <li>{@link LocalDate} using date format pattern ISO local date (yyyy-MM-dd)</li>
 * <li>{@link LocalTime} using date format pattern ISO local time (HH:mm:ss)</li>
 * <li>{@link LocalDateTime} using date format pattern ISO local date/time ('yyyy-MM-ddTHH:mm:ss')</li>
 * </ul>
 * 
 * <p>
 * This navigator provides the {@link #navigateTo(String, Map)} method to trigger navigation using a Map of parameters
 * name and values instead of a fully serialized navigation state string.
 * </p>
 * 
 * <p>
 * A <code>navigateInWindow</code> method is provided to display a View using a {@link Window} instead of the navigator
 * default {@link ViewDisplay} component.
 * </p>
 * 
 * <p>
 * View display lifecycle can be intercepted by the view instance itself, using {@link OnShow} and {@link OnLeave}
 * annotated methods. This methods must be <code>public</code> and provide zero or only one parameter of
 * {@link ViewNavigatorChangeEvent} or default {@link ViewChangeEvent} type. The {@link OnShow} annotated method will be
 * called when the View is displayed in application interface, the {@link OnLeave} annotated methods will be called when
 * the View is about to be deactivated to be replaced by another view in the navigation flow.
 * </p>
 * 
 * <p>
 * This navigator is expected to keep a history of the navigation states of the navigation flow, allowing to navigate
 * back in navigation history using {@link #navigateBack()} method.
 * </p>
 * 
 * <p>
 * If correctly configured in concrete subclasses, {@link #navigateToDefault()} allow to navigate to a predefined
 * default View (something like a <i>homepage</i> View).
 * </p>
 * 
 * <p>
 * Sub-view are supported, i.e. View instances intended to be displayed in a parent View which must implement
 * {@link SubViewContainer} interface and take care of sub view instances display in application UI. Sub view are
 * declared using {@link SubViewOf} annotation on View classes.
 * </p>
 * 
 * @see DefaultViewNavigator
 * 
 * @since 5.0.0
 */
public interface ViewNavigator extends Serializable {

	/**
	 * Default {@link Context} resource reference
	 */
	public static final String CONTEXT_KEY = ViewNavigator.class.getName();

	/**
	 * URI scheme which represents a navigation to a {@link View}.
	 */
	public static final String VIEW_URI_SCHEME = "view://";

	/**
	 * Navigates to a view and initialize the view with given parameters.
	 * <p>
	 * The view string consists of a view name optionally followed by a slash and a parameters part that is passed as-is
	 * to the view. ViewProviders are used to find and create the correct type of view.
	 * <p>
	 * If multiple providers return a matching view, the view with the longest name is selected. This way, e.g.
	 * hierarchies of subviews can be registered like "admin/", "admin/users", "admin/settings" and the longest match is
	 * used.
	 * <p>
	 * If the view being deactivated indicates it wants a confirmation for the navigation operation, the user is asked
	 * for the confirmation.
	 * <p>
	 * Registered {@link ViewChangeListener}s are called upon successful view change.
	 * 
	 * @param navigationState View name and parameters
	 */
	void navigateTo(String navigationState);

	/**
	 * Navigates to the {@link View} identified by given <code>viewName</code> using given <code>parameters</code>, if
	 * any, and linking them to View fields using {@link ViewParameter} annotated view class fields.
	 * <p>
	 * A new view instance is created and used for each view navigation request, unless View class is declared as
	 * stateful using {@link StatefulView} annotation: in this case the same view instance for each view class/name is
	 * shared during application UI lifecycle.
	 * </p>
	 * <p>
	 * If the view being deactivated indicates it wants a confirmation for the navigation operation, the user is asked
	 * for the confirmation.
	 * </p>
	 * <p>
	 * Registered {@link ViewChangeListener}s are called upon successful view change.
	 * </p>
	 * @param viewName View name
	 * @param parameters Optional view parameters
	 * @throws ViewNavigationException View with given name cannot be found or other view handling error
	 */
	void navigateTo(String viewName, Map<String, Object> parameters) throws ViewNavigationException;

	/**
	 * Navigate to the {@link View} identified by given <code>viewName</code> using the same behaviour of
	 * {@link #navigateTo(String, Map)} but rendering the View contents in an application Window, using optional
	 * <code>windowConfiguration</code> to setup Window features.
	 * @param viewName View name
	 * @param windowConfiguration Optional Window configuration settings
	 * @param parameters Optional view parameters
	 * @return The UI Window in which the View is displayed
	 * @throws ViewNavigationException View with given name cannot be found or other view handling error
	 */
	Window navigateInWindow(String viewName, ViewWindowConfiguration windowConfiguration,
			Map<String, Object> parameters) throws ViewNavigationException;

	/**
	 * Navigates back to previous {@link View}, if any. In no previous View is available and a default view is defined,
	 * navigator will navigate to the default view.
	 * @return <code>true</code> if a previous view in navigation history, or the default view, was available and back
	 *         navigation succeeded
	 * @throws ViewNavigationException View handling error
	 */
	boolean navigateBack() throws ViewNavigationException;

	/**
	 * Navigates to the default View
	 * @throws ViewNavigationException If no default View is available or other view handling error
	 */
	void navigateToDefault() throws ViewNavigationException;

	/**
	 * Get a {@link NavigationBuilder} to create a navigation declaration to navigate to given <code>viewName</code>
	 * using parameters.
	 * @param viewName View name (not null)
	 * @return NavigationBuilder
	 */
	default NavigationBuilder toView(String viewName) {
		return new DefaultNavigationBuilder(this, viewName);
	}

	/**
	 * Get default view name
	 * @return Default view name, or <code>null</code> if not defined
	 */
	String getDefaultViewName();

	/**
	 * Get current (active) view name in navigator
	 * @return Current view name, or <code>null</code> if no view is currently active in navigator
	 */
	String getCurrentViewName();

	/**
	 * Get current (active) view in navigator.
	 * <p>
	 * Note that this methods returns always the current top-level View in navigator: this means that sub-view instances
	 * will be never returned. To obtain current sub-view check if returned View is a {@link SubViewContainer} instance
	 * and than call {@link SubViewContainer#getCurrentView()} method.
	 * </p>
	 * @return Current view, or <code>null</code> if no view is currently active in navigator
	 */
	View getCurrentView();

	/**
	 * Listen to changes of the active view
	 * @param listener Listener to invoke during a view change
	 * @return the listener {@link Registration}
	 */
	Registration addViewChangeListener(ViewChangeListener listener);

	/**
	 * Get the current {@link ViewNavigator}, if available as {@link Context} resource or from current UI.
	 * @return Current ViewNavigator, or an empty Optinal if not available
	 */
	static Optional<ViewNavigator> getCurrent() {
		return Optional.ofNullable(Context.get().resource(ViewNavigator.CONTEXT_KEY, ViewNavigator.class)
				.orElse(ViewNavigationUtils.getCurrentUIViewNavigator()));
	}

	/**
	 * Requires the current {@link ViewNavigator}. If not available using {@link #getCurrent()}, an
	 * {@link IllegalStateException} is thrown.
	 * @return Current ViewNavigator
	 * @throws IllegalStateException ViewNavigator is not available as a {@link Context} resource of from current UI
	 */
	static ViewNavigator require() {
		return getCurrent().orElseThrow(() -> new IllegalStateException(
				"ViewNavigator is not available as context resource or from current UI"));
	}

	/**
	 * Builder to create {@link ViewNavigator} instance in fluent-style mode
	 * @return ViewNavigatorBuilder
	 */
	static Builder builder() {
		return new DefaultViewNavigator.ViewNavigatorBuilder();
	}

	/**
	 * Helper method to get given <code>view</code> content: if View is a {@link ViewContentProvider}, than
	 * {@link ViewContentProvider#getViewContent()} is returned, else if view is a {@link Component}, view instance
	 * itself is returned. Otherwise, a {@link IllegalArgumentException} is thrown.
	 * @param view View for which retrieve the content
	 * @return View content as {@link Component}, or <code>null</code> if given view was <code>null</code>
	 * @throws IllegalArgumentException if view instance is not a {@link ViewContentProvider} nor a {@link Component}
	 */
	static Component getViewContent(View view) {
		return ViewDisplayUtils.getViewContent(view);
	}

	// Events

	/**
	 * View change event.
	 */
	public interface ViewNavigatorChangeEvent extends Serializable {

		/**
		 * Returns the navigator that triggered this event.
		 * @return the {@link ViewNavigator} (not null)
		 */
		ViewNavigator getViewNavigator();

		/**
		 * Returns the view being deactivated.
		 * @return the old View (may be null)
		 */
		View getOldView();

		/**
		 * Returns the view being activated.
		 * @return the new View (not null)
		 */
		View getNewView();

		/**
		 * Returns the view name of the view being activated.
		 * @return view name of the new View (not null)
		 */
		String getViewName();

		/**
		 * Returns the parameters for the view being activated.
		 * @return the navigation parameters for the new view (may be null)
		 */
		String getParameters();

		/**
		 * If the navigation to the view being activated is requested in window, returns the {@link Window} into which
		 * the view is displayed.
		 * @return Optional view container window
		 */
		Optional<Window> getWindow();

	}

	// Builder

	/**
	 * Builder to create {@link ViewNavigator} instances
	 */
	public interface Builder extends NavigatorBuilder<Builder> {

		/**
		 * Add a default {@link ViewProvider} (if not already present) and register the given {@link View} class bound
		 * to given view name. This {@link ViewProvider} supports {@link StatefulView} view instances.
		 * <p>
		 * View instances will be created according to view scope: for stateful views, an instance is created at
		 * first request (for each UI) and the same instance is returned to subsequent view requests. On the contrary,
		 * for standard views, a new instance is created and returned to navigator for every view request.
		 * </p>
		 * @param viewName View name (not null)
		 * @param viewClass View class (not null)
		 * @return this
		 */
		Builder withView(String viewName, Class<? extends View> viewClass);

		/**
		 * Set the NavigationStateManager keeping track of the active view and enabling bookmarking and direct
		 * navigation.
		 * @param navigationStateManager NavigationStateManager or <code>null</code> to use the default implementation
		 * @return this
		 */
		Builder navigationStateManager(NavigationStateManager navigationStateManager);

		/**
		 * ViewDisplay used to display {@link View} instances
		 * @param viewDisplay The ViewDisplay
		 * @return this
		 */
		Builder viewDisplay(ViewDisplay viewDisplay);

		/**
		 * Set the {@link SingleComponentContainer} which has to used to display {@link View} instances using
		 * {@link SingleComponentContainer#setContent(com.vaadin.ui.Component)}.
		 * @param container The {@link SingleComponentContainer} which has to used to display {@link View} instances
		 * @return this
		 */
		Builder viewDisplay(SingleComponentContainer container);

		/**
		 * Set the {@link ComponentContainer} which has to used to display {@link View} instances, replacing the
		 * contents of the ComponentContainer with the active view.
		 * @param container The {@link ComponentContainer} which has to used to display {@link View} instances
		 * @return this
		 */
		Builder viewDisplay(ComponentContainer container);

		/**
		 * Build ViewNavigator instance and bind it to given <code>ui</code>
		 * @param ui UI to which to bind the navigator
		 * @return ViewNavigator instance
		 */
		ViewNavigator buildAndBind(UI ui);

	}

	/**
	 * Base {@link ViewNavigator} builder
	 */
	public interface NavigatorBuilder<B extends NavigatorBuilder<B>> {

		/**
		 * Set navigation history max size
		 * @param maxSize Max size of retained navigation states
		 * @return this
		 */
		B maxNavigationHistorySize(int maxSize);

		/**
		 * Add a {@link ViewProvider} to navigator. Providers are called in order of registration until one that can
		 * handle the requested view name is found.
		 * @param provider View provider to add (not null)
		 * @return this
		 */
		B addProvider(ViewProvider provider);

		/**
		 * Set the default view name. If a {@link View} with given <code>defaultViewName</code> is valid and provided by
		 * registered ViewProviders, it is used as target of {@link ViewNavigator#navigateToDefault()} method and as a
		 * fallback by {@link ViewNavigator#navigateBack()} method if no other View is available in history.
		 * @param defaultViewName the default view name to set
		 * @return this
		 */
		B defaultViewName(String defaultViewName);

		/**
		 * Set whether to navigate to default view (if setted) when a view is not available from current navigation
		 * state
		 * @param navigateToDefaultViewWhenViewNotAvailable <code>true</code> to navigate to default view (if setted)
		 *        when a view is not available from current navigation state
		 * @return this
		 */
		B navigateToDefaultViewWhenViewNotAvailable(boolean navigateToDefaultViewWhenViewNotAvailable);

		/**
		 * Set the view to be displayed when no other view matches the navigation state.
		 * @param errorView Error view
		 * @return this
		 */
		B errorView(View errorView);

		/**
		 * Registers a view provider that is queried for a view when no other view matches the navigation state. An
		 * error view provider should match any navigation state, but could return different views for different states.
		 * Its <code>getViewName(String navigationState)</code> should return <code>navigationState</code>.
		 * @param errorViewProvider Error view provider
		 * @return this
		 */
		B errorViewProvider(ViewProvider errorViewProvider);

		/**
		 * Add a {@link ViewChangeListener} to Navigator
		 * @param viewChangeListener ViewChangeListener to add
		 * @return this
		 */
		B withViewChangeListener(ViewChangeListener viewChangeListener);

		/**
		 * Set whether the support for {@link Authenticate} annotation on {@link View}s or {@link UI} is enabled.
		 * <p>
		 * If enabled, the navigator will check the {@link Authenticate} annotation on {@link View}s or {@link UI}
		 * during navigation, and if present, requires the presence of an authenticated {@link AuthContext} obtained
		 * through {@link AuthContext#getCurrent()}. If an {@link AuthContext} is present but not authenticated, try to
		 * perform authentication using current {@link VaadinRequest} and the optional authentication schemes specified
		 * using {@link Authenticate#schemes()}. If the authentication is not successfull and a valid redirect URI is
		 * provided through {@link Authenticate#redirectURI()}, the navigation is redirected to that URI. If the URI
		 * does not specify a scheme, or the scheme is equal to <code>view://</code>, the navigation is redirected to
		 * the navigation state following the <code>view://</code> scheme or to the navigation state equal to the
		 * </p>
		 * <p>
		 * The authentication support is enabled by default.
		 * </p>
		 * {@link Authenticate#redirectURI()} attribute if a URI scheme is not specified.
		 * @param authenticationEnabled <code>true</code> to enable the {@link Authenticate} support, <code>false</code>
		 *        to disable.
		 * @return this
		 */
		B authenticationEnabled(boolean authenticationEnabled);

	}

	/**
	 * Builder to create navigation calls using view name and parameters
	 */
	public interface NavigationBuilder {

		/**
		 * Add view parameter
		 * @param name Parameter name (required)
		 * @param value Parameter value
		 * @return this
		 */
		NavigationBuilder withParameter(String name, Object value);

		/**
		 * Navigate to view with registered parameters, if any
		 * @throws ViewNavigationException Navigation failed
		 */
		void navigate() throws ViewNavigationException;

		/**
		 * Navigate to view with registered parameters, rendering the View contents in an application Window, using
		 * optional <code>windowConfiguration</code> to setup Window features.
		 * @param windowConfiguration Optional Window configuration settings
		 * @return The UI Window in which the View is displayed
		 * @throws ViewNavigationException Navigation failed
		 */
		Window navigateInWindow(ViewWindowConfiguration windowConfiguration) throws ViewNavigationException;

	}

	/**
	 * Exception related to {@link View} navigation errors.
	 */
	@SuppressWarnings("serial")
	public class ViewNavigationException extends RuntimeException {

		/**
		 * View name to which this exception is related
		 */
		private final String navigationState;

		/**
		 * Constructor with error message
		 * @param navigationState Navigation state
		 * @param message Error message
		 */
		public ViewNavigationException(String navigationState, String message) {
			super(message);
			this.navigationState = navigationState;
		}

		/**
		 * Constructor with nested exception
		 * @param navigationState Navigation state
		 * @param cause Nested exception
		 */
		public ViewNavigationException(String navigationState, Throwable cause) {
			super(cause);
			this.navigationState = navigationState;
		}

		/**
		 * Constructor with error message and nested exception
		 * @param navigationState Navigation state
		 * @param message Error message
		 * @param cause Nested exception
		 */
		public ViewNavigationException(String navigationState, String message, Throwable cause) {
			super(message, cause);
			this.navigationState = navigationState;
		}

		/**
		 * Get the navigation state to which this exception is related, if available
		 * @return the navigation state
		 */
		public Optional<String> getNavigationState() {
			return Optional.ofNullable(navigationState);
		}

	}

}
