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

import com.holonplatform.vaadin.navigator.ViewNavigator;
import com.vaadin.ui.Window;

/**
 * Default {@link Window} used by {@link ViewNavigator} to display View instances in a window.
 * 
 * <p>
 * This Window {@link #equals(Object)} behaviour compares {@link #getNavigationState()} Strings to resolve equality with
 * another Window.
 * </p>
 * 
 * @since 5.0.0
 */
public class DefaultViewWindow extends Window {

	private static final long serialVersionUID = 5416691652019761826L;

	private final String navigationState;

	/**
	 * Constructor
	 * @param caption Window caption
	 * @param navigationState Navigation state
	 */
	public DefaultViewWindow(String caption, String navigationState) {
		super(caption);
		this.navigationState = navigationState;
	}

	/**
	 * Navigation state (view name and parameters) bound to this Window
	 * @return Navigation state
	 */
	public String getNavigationState() {
		return navigationState;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((navigationState == null) ? 0 : navigationState.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		DefaultViewWindow other = (DefaultViewWindow) obj;
		if (navigationState == null) {
			if (other.navigationState != null)
				return false;
		} else if (!navigationState.equals(other.navigationState))
			return false;
		return true;
	}

}
