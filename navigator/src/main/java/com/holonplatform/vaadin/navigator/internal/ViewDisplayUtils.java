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

import java.io.Serializable;

import com.holonplatform.vaadin.navigator.ViewContentProvider;
import com.holonplatform.vaadin.navigator.internal.ViewConfiguration.ViewConfigurationException;
import com.vaadin.navigator.View;
import com.vaadin.ui.Component;

/**
 * Utility class for {@link View} display operations
 * 
 * @since 5.0.0
 */
public final class ViewDisplayUtils implements Serializable {

	private static final long serialVersionUID = -4834098223817784423L;

	/*
	 * Empty private constructor: this class is intended only to provide constants ad utility methods.
	 */
	private ViewDisplayUtils() {
	}

	/**
	 * Get given <code>view</code> content: if View is a {@link ViewContentProvider}, than
	 * {@link ViewContentProvider#getViewContent()} is returned, else if view is a {@link Component}, view instance
	 * itself is returned. Otherwise, a {@link IllegalArgumentException} is thrown.
	 * @param view View for which retrieve the content
	 * @return View content as {@link Component}
	 * @throws IllegalArgumentException if view instance is not a {@link ViewContentProvider} nor a {@link Component}
	 */
	public static Component getViewContent(View view) throws IllegalArgumentException {
		if (view != null) {
			if (view instanceof ViewContentProvider) {
				try {
					// view delegates content providing to ViewContentProvider
					return ((ViewContentProvider) view).getViewContent();
				} catch (Exception e) {
					throw new ViewConfigurationException(
							"Failed to obtain View content of View [" + view.getClass().getName() + "]", e);
				}
			} else if (view instanceof Component) {
				// view is a Component itself
				return (Component) view;
			} else {
				throw new IllegalArgumentException(
						"Invalid View " + view.getClass().getName() + ": View instance must be a "
								+ Component.class.getName() + " or a " + ViewContentProvider.class.getName());
			}
		}
		return null;
	}

}
