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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.holonplatform.auth.AuthContext;
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.vaadin.navigator.SubViewContainer;
import com.holonplatform.vaadin.navigator.ViewNavigator;
import com.holonplatform.vaadin.navigator.ViewNavigator.ViewNavigationException;
import com.holonplatform.vaadin.navigator.annotations.SubViewOf;
import com.holonplatform.vaadin.navigator.annotations.ViewContext;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

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

	public void config2() {
		// tag::config2[]
		UI ui = getUI();
		ViewNavigator.builder() //
				.viewDisplay(ui) //
				.withView("view1", View1.class) // <1>
				.withView("view2", View2.class) // <2>
				.defaultViewName("view1") // <3>
				.buildAndBind(ui);
		// end::config2[]
	}

	public void nav1() {
		// tag::nav1[]
		ViewNavigator navigator = getViewNavigator();

		navigator.navigateTo("myView"); // <1>

		Map<String, Object> parameters = new HashMap<>();
		parameters.put("parameter1", "test");
		parameters.put("parameter2", 34.5);

		navigator.navigateTo("myView", parameters); // <2>
		// end::nav1[]
	}

	public void nav2() {
		// tag::nav2[]
		ViewNavigator navigator = getViewNavigator();

		navigator.navigateInWindow("myView"); // <1>

		Map<String, Object> parameters = new HashMap<>();
		parameters.put("parameter1", "test");
		parameters.put("parameter2", 34.5);

		navigator.navigateInWindow("myView", windowConfig -> {
			windowConfig.fullWidth();
			windowConfig.styleName("my-window-style");
		}, parameters); // <2>
		// end::nav2[]
	}

	public void nav3() {
		// tag::nav3[]
		ViewNavigator navigator = getViewNavigator();

		navigator.toView("myView").withParameter("parameter1", "test").withParameter("parameter2", 34.5).navigate(); // <1>

		navigator.toView("myView").navigateInWindow(); // <2>

		navigator.toView("myView").navigateInWindow(windowConfig -> {
			windowConfig.fullWidth();
			windowConfig.styleName("my-window-style");
		}); // <3>
		// end::nav3[]
	}

	// tag::ctx[]
	class ViewContextExample implements View {

		@ViewContext
		private LocalizationContext localizationContext;

		@ViewContext
		private AuthContext authContext;

	}
	// end::ctx[]

	public void obtain() {
		// tag::obtain[]
		Optional<ViewNavigator> navigator = ViewNavigator.getCurrent(); // <1>

		ViewNavigator viewNavigator = ViewNavigator.require(); // <2>
		// end::obtain[]
	}

	// tag::sub[]
	class SubViewContainerExample extends TabSheet implements SubViewContainer { // <1>

		public SubViewContainerExample() {
			super();
			setSizeFull();
		}

		@Override
		public boolean display(View view, String viewName, Map<String, String> parameters)
				throws ViewNavigationException {
			addTab(ViewNavigator.getViewContent(view), viewName, VaadinIcons.PIN);
			return true;
		}

		@Override
		public View getCurrentView() {
			return (View) getSelectedTab();
		}

	}

	@SubViewOf("mycontainer")
	public class SubViewExample extends VerticalLayout implements View { // <2>

		public SubViewExample() {
			super();
			setMargin(true);
			addComponent(new Label("The sub view 1"));
		}

	}
	// end::sub[]

	private static final ViewNavigator getViewNavigator() {
		return null;
	}

	private static final ViewProvider getViewProvider() {
		return null;
	}

	private static final ViewProvider getErrorViewProvider() {
		return null;
	}

	private static final UI getUI() {
		return null;
	}

	private class View1 implements View {

	}

	private class View2 implements View {

	}

}
