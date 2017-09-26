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

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import com.holonplatform.core.internal.Logger;
import com.holonplatform.core.internal.Logger.Level;
import com.holonplatform.vaadin.internal.VaadinLogger;
import com.holonplatform.vaadin.navigator.annotations.StatefulView;
import com.holonplatform.vaadin.navigator.internal.ViewConfiguration.ViewConfigurationException;
import com.holonplatform.vaadin.navigator.internal.ViewConfiguration.ViewConfigurationProvider;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewProvider;
import com.vaadin.ui.UI;

/**
 * Default {@link ViewProvider} with {@link StatefulView} instances management.
 * 
 * <p>
 * Use {@link #registerView(String, Class)} to register {@link View} classes and mapping them to view names. A view name
 * must be unique whithin provider registered views.
 * </p>
 * 
 * <p>
 * View instances will be created according to view scope: for stateful views, an instance is created at first request
 * (for each UI) and the same instance is returned to subsequent view requests. On the contrary, for standard views, a
 * new instance is created and returned to Navigator for every view request.
 * </p>
 * 
 * <p>
 * View name fragment to match with registered view names is extracted from view request string (the URL part after
 * <code>#!</code> characters) using the longest first part before <code>/</code> character which corresponds to a
 * registered view name.
 * </p>
 * 
 * @since 5.0.0
 */
public class DefaultViewProvider implements ViewProcessorProvider {

	private static final long serialVersionUID = 875397403732197765L;

	private static final Logger LOGGER = VaadinLogger.create();

	/*
	 * View name - class map
	 */
	private final Map<String, Class<? extends View>> views;

	/*
	 * Stateful view instances for every UI
	 */
	private final Map<UI, Map<Class<? extends View>, WeakReference<View>>> statefulViews;

	/**
	 * Constructor
	 */
	public DefaultViewProvider() {
		super();
		this.views = new HashMap<>();
		this.statefulViews = new WeakHashMap<>(1);
	}

	/**
	 * Registered views
	 * @return Map (view name - view class) of registered views
	 */
	protected Map<String, Class<? extends View>> getViews() {
		return views;
	}

	/**
	 * Register a View class bounding it to given <code>viewName</code>.
	 * <p>
	 * View name must be unique within registered views, otherwise a ViewConfigurationException is thrown.
	 * </p>
	 * @param viewName View name
	 * @param viewClass View class
	 * @throws ViewConfigurationException Failed to register view
	 */
	public void registerView(String viewName, Class<? extends View> viewClass) throws ViewConfigurationException {
		synchronized (views) {
			if (viewName == null) {
				throw new ViewConfigurationException("View name must be not null");
			}
			if (viewClass == null) {
				throw new ViewConfigurationException("View class must be not null");
			}

			// check valid type
			ViewNavigationUtils.checkValidViewClass(viewClass);

			// check valid name
			ViewNavigationUtils.checkValidViewName(viewName);

			// check not already registered
			if (views.containsKey(viewName)) {
				throw new ViewConfigurationException("A view with name " + viewName
						+ " is already registered as class: " + views.get(viewName).getName());
			}

			// register
			views.put(viewName, viewClass);

			LOGGER.debug(() -> "Registered view name " + viewName + " mapped to view class " + viewClass.getName());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.navigator.ViewProvider#getViewName(java.lang.String)
	 */
	@Override
	public String getViewName(String viewAndParameters) {
		LOGGER.debug(() -> "Retreiving view name from [" + viewAndParameters + "]");

		String viewName = null;
		if (isValidViewName(viewAndParameters)) {
			viewName = viewAndParameters;
		} else {
			int lastSlash = -1;
			String viewPart = viewAndParameters;
			while ((lastSlash = viewPart.lastIndexOf('/')) > -1) {
				viewPart = viewPart.substring(0, lastSlash);
				if (isValidViewName(viewPart)) {
					viewName = viewPart;
					break;
				}
			}
		}

		if (LOGGER.isEnabled(Level.DEBUG)) {
			final String vn = viewName;
			if (vn == null) {
				LOGGER.debug(() -> "Found no valid view name in [" + viewAndParameters + "]");
			} else {
				LOGGER.debug(() -> "[" + vn + "] is a valid view name");
			}
		}

		return viewName;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.navigator.ViewProvider#getView(java.lang.String)
	 */
	@Override
	public View getView(String viewName) {
		Class<? extends View> viewClass = getViews().get(viewName);
		if (viewClass != null) {
			// found
			return getViewInstance(viewClass);
		}
		return null;
	}

	/**
	 * Check if given view name is valid, i.e. is registered in this provider
	 * @param viewName View name to check
	 * @return <code>true</code> if view name is valid
	 */
	protected boolean isValidViewName(String viewName) {
		return views.containsKey(viewName);
	}

	/**
	 * Get View instance from view class with consistent stateful views handling
	 * @param viewClass View class
	 * @return View instance
	 * @throws ViewConfigurationException Failed to create view instance
	 */
	protected View getViewInstance(Class<? extends View> viewClass) throws ViewConfigurationException {
		synchronized (statefulViews) {

			View view = null;

			final UI ui = UI.getCurrent();

			// check stateful
			boolean stateful = isStatefulView(viewClass);
			if (stateful) {
				view = getStatefulViewInstance(ui, viewClass);
			}

			// create instance
			if (view == null) {
				try {
					view = viewClass.newInstance();

					if (stateful) {
						// retain instance
						Map<Class<? extends View>, WeakReference<View>> views = statefulViews.get(ui);
						if (views == null) {
							views = new HashMap<>(8);
							statefulViews.put(ui, views);
						}
						views.put(viewClass, new WeakReference<>(view));
					}

				} catch (Exception e) {
					throw new ViewConfigurationException("Failed to istantiate view class " + viewClass.getName(), e);
				}
			}

			return view;

		}
	}

	/**
	 * Check if view is declared as stateful
	 * @param viewClass View class
	 * @return <code>true</code> if view class is {@link StatefulView} annotated
	 */
	protected boolean isStatefulView(Class<? extends View> viewClass) {
		return viewClass.isAnnotationPresent(StatefulView.class);
	}

	/**
	 * Check if staetful view instance was already created for given UI and view class and, if found, returns it.
	 * @param ui UI
	 * @param viewClass View class
	 * @return View instance, or <code>null</code> if not found
	 */
	protected View getStatefulViewInstance(UI ui, Class<? extends View> viewClass) {
		Map<Class<? extends View>, WeakReference<View>> views = statefulViews.get(ui);
		if (views != null) {
			WeakReference<View> vr = views.get(viewClass);
			if (vr != null) {
				return vr.get();
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.navigator.internal.ViewProcessorProvider#processViewInstance(com.holonplatform.vaadin.
	 * navigator.ViewConfiguration.ViewConfigurationProvider, com.vaadin.navigator.View)
	 */
	@Override
	public View processViewInstance(ViewConfigurationProvider viewConfigurationProvider, View view)
			throws ViewConfigurationException {
		return ViewNavigationUtils.injectContext(viewConfigurationProvider, view);
	}

}
