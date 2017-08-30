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
package com.holonplatform.vaadin.ui.spring.test.components;

import com.holonplatform.vaadin.navigator.annotations.ViewContext;
import com.holonplatform.vaadin.navigator.annotations.ViewParameter;
import com.holonplatform.vaadin.ui.spring.test.TestNavigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.VerticalLayout;

@SpringView(name = TestNavigator.VIEW_SEVEN)
public class ViewSeven extends VerticalLayout implements View {

	private static final long serialVersionUID = 1L;

	@ViewContext
	private ContextTestData data;

	@ViewParameter(defaultValue = "DFT")
	private String param;

	@ViewParameter(required = true)
	private String param2;

	public ContextTestData getContextData() {
		return data;
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public String getParam2() {
		return param2;
	}

	public void setParam2(String param2) {
		this.param2 = param2;
	}

	@Override
	public void enter(ViewChangeEvent event) {
	}

}
