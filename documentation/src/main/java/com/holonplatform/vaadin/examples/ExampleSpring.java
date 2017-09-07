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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.holonplatform.spring.EnableBeanContext;
import com.holonplatform.vaadin.navigator.ViewNavigator;
import com.holonplatform.vaadin.spring.DefaultView;
import com.holonplatform.vaadin.spring.config.EnableViewNavigator;
import com.vaadin.navigator.View;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.SpringViewDisplay;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class ExampleSpring {

	// tag::spring1[]
	@Configuration // <1>
	@ComponentScan(basePackageClasses = ViewOne.class) // <2>
	@EnableViewNavigator // <3>
	@EnableBeanContext // <4>
	class SpringConfig {

	}

	@SpringView(name = "view1") // <5>
	@DefaultView // <6>
	class ViewOne extends VerticalLayout implements View {

	}

	@SpringView(name = "view2") // <7>
	@UIScope // <8>
	class ViewTwo extends VerticalLayout implements View {

	}

	@SpringUI // <9>
	@SpringViewDisplay // <10>
	class AppUI extends UI {

		@Autowired
		ViewNavigator navigator; // <11>

		@Override
		protected void init(VaadinRequest request) {
			// ...
		}

	}
	// end::spring1[]

}
