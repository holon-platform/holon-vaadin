/*
 * Copyright 2000-2017 Holon TDCN.
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

import java.util.Optional;

import com.holonplatform.vaadin.navigator.ViewNavigator;
import com.holonplatform.vaadin.navigator.ViewNavigator.ViewNavigatorChangeEvent;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Window;

/**
 * Default {@link ViewNavigatorChangeEvent} implementation.
 * 
 * @since 5.0.0
 */
public class DefaultViewNavigatorChangeEvent extends ViewChangeEvent implements ViewNavigatorChangeEvent {

	private static final long serialVersionUID = 5477036944760324282L;

	private final String oldViewName;
	private final Window containerWindow;

	public <N extends Navigator & ViewNavigator> DefaultViewNavigatorChangeEvent(N navigator, View oldView,
			String oldViewName, View newView, String viewName, String parameters, Window containerWindow) {
		super(navigator, oldView, newView, viewName, parameters);
		this.oldViewName = oldViewName;
		this.containerWindow = containerWindow;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.navigator.ViewNavigator.ViewChangeEvent#getViewNavigator()
	 */
	@Override
	public ViewNavigator getViewNavigator() {
		return (ViewNavigator) getNavigator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.navigator.ViewNavigator.ViewNavigatorChangeEvent#getOldViewName()
	 */
	@Override
	public Optional<String> getOldViewName() {
		return Optional.ofNullable(oldViewName);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.navigator.ViewNavigator.ViewChangeEvent#getWindow()
	 */
	@Override
	public Optional<Window> getWindow() {
		return Optional.ofNullable(containerWindow);
	}

	/**
	 * Create a new {@link DefaultViewNavigatorChangeEvent} from a standard {@link ViewChangeEvent}.
	 * @param event Original {@link ViewChangeEvent}
	 * @param oldViewName Optional old view name
	 * @param navigator View navigator source of the event
	 * @param containerWindow Optional View Window
	 * @return A new {@link DefaultViewNavigatorChangeEvent}
	 */
	public static <N extends Navigator & ViewNavigator> DefaultViewNavigatorChangeEvent create(ViewChangeEvent event,
			String oldViewName, N navigator, Window containerWindow) {
		return new DefaultViewNavigatorChangeEvent(navigator, event.getOldView(), oldViewName, event.getNewView(),
				event.getViewName(), event.getParameters(), containerWindow);
	}

}
