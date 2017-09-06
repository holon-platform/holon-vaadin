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
package com.holonplatform.vaadin.examples;

import com.holonplatform.vaadin.navigator.ViewNavigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;

@SuppressWarnings({ "serial", "unused" })
public class ExampleNavigator {

	private static final View MY_ERROR_VIEW = new View() {

	};

	// tag::config[]
	class AppUI extends UI {

		@Override
		protected void init(VaadinRequest request) {
			ViewNavigator navigator = ViewNavigator.builder() // <1>
					.viewDisplay(this) // <2>
					.addProvider(getViewProvider()) // <3>
					.defaultViewName("home") // <4>
					.errorView(MY_ERROR_VIEW) // <5>
					.errorViewProvider(getErrorViewProvider()) // <6>
					.maxNavigationHistorySize(1000) // <7>
					.navigateToDefaultViewWhenViewNotAvailable(true) // <8>
					.withViewChangeListener(e -> { // <9>
						// ...
						return true;
					}).buildAndBind(this); // <10>
		}

	}
	// end::config[]

	private static final ViewProvider getViewProvider() {
		return null;
	}

	private static final ViewProvider getErrorViewProvider() {
		return null;
	}

}
