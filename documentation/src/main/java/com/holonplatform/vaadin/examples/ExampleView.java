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

import java.time.LocalDate;
import java.util.Optional;

import com.holonplatform.vaadin.device.DeviceInfo;
import com.holonplatform.vaadin.navigator.ViewNavigator;
import com.holonplatform.vaadin.navigator.ViewNavigator.ViewNavigatorChangeEvent;
import com.holonplatform.vaadin.navigator.ViewWindowConfigurator;
import com.holonplatform.vaadin.navigator.annotations.OnLeave;
import com.holonplatform.vaadin.navigator.annotations.OnShow;
import com.holonplatform.vaadin.navigator.annotations.ViewParameter;
import com.holonplatform.vaadin.navigator.annotations.ViewWindowConfiguration;
import com.holonplatform.vaadin.navigator.annotations.VolatileView;
import com.holonplatform.vaadin.navigator.annotations.WindowView;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings({ "serial", "unused" })
public class ExampleView {

	// tag::viewparams[]
	class ViewExample implements View {

		@ViewParameter("myparam") // <1>
		private String stringParam;

		@ViewParameter(defaultValue = "1") // <2>
		private Integer intParam;

		@ViewParameter(required = true) // <3>
		private LocalDate requiredParam;

	}
	// end::viewparams[]

	// tag::showleave[]
	class ViewExample2 implements View {

		@ViewParameter
		private String myparam;

		@OnShow
		public void onShow() { // <1>
			// ...
		}

		@OnShow(onRefresh = true) // <2>
		public void onShowAtRefreshToo() {
			// ...
		}

		@OnShow
		public void onShow2(ViewChangeEvent event) { // <3>
			String name = event.getViewName(); // <4>
			View oldView = event.getOldView(); // <5>
			// ...
		}

		@OnShow
		public void onShow3(ViewNavigatorChangeEvent event) { // <6>
			ViewNavigator navigator = event.getViewNavigator(); // <7>
			Optional<Window> viewWindow = event.getWindow(); // <8>
			// ...
		}

		@OnLeave
		public void onLeave() { // <9>
			// ...
		}

		@OnLeave
		public void onLeave2(ViewNavigatorChangeEvent event) { // <10>
			View nextView = event.getNewView(); // <11>
			// ...
		}

	}
	// end::showleave[]

	// tag::viewcontent[]
	class ViewExampleContent extends VerticalLayout implements View { // <1>

		public ViewExampleContent() {
			super();
			addComponent(new Label("View content"));
		}

	}

	class ViewExampleContentProvider implements View {

		@Override
		public Component getViewComponent() { // <2>
			boolean mobile = DeviceInfo.get().map(info -> info.isMobile()).orElse(false);
			return mobile ? buildMobileViewContent() : buildDefaultViewContent();
		}

	}
	// end::viewcontent[]

	// tag::volatile[]
	@VolatileView
	class VolatileViewExample implements View {

	}
	// end::volatile[]

	public void navigateInWindow() {
		// tag::window1[]
		ViewNavigator.require().navigateInWindow("myView"); // <1>
		// end::window1[]
	}

	// tag::window2[]
	@WindowView // <1>
	class MyWindowView1 implements View {

	}

	void openTheView() {
		ViewNavigator.require().navigateTo("myView"); // <2>
	}
	// end::window2[]

	public void navigateInWindow3() {
		// tag::window3[]
		ViewNavigator.require().navigateInWindow("myView", configurator -> {
			configurator.fullWidth().caption("Title").closable(false).resizable(false); // <1>
		});
		// end::window3[]
	}

	// tag::window4[]
	@WindowView(windowWidth = "50%", windowHeigth = "50%", closable = false, resizable = true) // <1>
	class MyWindowView2 implements View {

	}
	// end::window4[]

	// tag::window5[]
	@WindowView
	class MyWindowView3 implements View {

		@ViewWindowConfiguration // <1>
		public void configure(ViewWindowConfigurator configurator) {
			configurator.fullWidth().caption("Title").closable(false).resizable(false);
		}

	}
	// end::window5[]

	private static final Component buildDefaultViewContent() {
		return null;
	}

	private static final Component buildMobileViewContent() {
		return null;
	}

}
