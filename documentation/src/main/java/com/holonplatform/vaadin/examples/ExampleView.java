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

import com.holonplatform.vaadin.navigator.ViewNavigator;
import com.holonplatform.vaadin.navigator.ViewNavigator.ViewNavigatorChangeEvent;
import com.holonplatform.vaadin.navigator.annotations.OnLeave;
import com.holonplatform.vaadin.navigator.annotations.OnShow;
import com.holonplatform.vaadin.navigator.annotations.ViewParameter;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
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

}
