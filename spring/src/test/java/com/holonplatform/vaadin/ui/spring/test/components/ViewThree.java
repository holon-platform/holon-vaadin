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
package com.holonplatform.vaadin.ui.spring.test.components;

import javax.annotation.security.RolesAllowed;

import com.holonplatform.vaadin.navigator.annotations.OnShow;
import com.holonplatform.vaadin.navigator.annotations.ViewParameter;
import com.holonplatform.vaadin.ui.spring.test.TestNavigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.VerticalLayout;

@UIScope
@RolesAllowed("r2")
@SpringView(name = TestNavigator.VIEW_THREE)
public class ViewThree extends VerticalLayout implements View {

	private static final long serialVersionUID = 1L;

	@ViewParameter
	private String pString;

	@ViewParameter("intpar")
	private int pInt;

	@ViewParameter("boolpar")
	private boolean boolPar;

	private String stringOnShow;
	private int intOnEnter;

	public String getpString() {
		return pString;
	}

	public void setpString(String pString) {
		this.pString = pString;
	}

	public int getIntParam() {
		return pInt;
	}

	public boolean getBoolParam() {
		return boolPar;
	}

	public String getStringOnShow() {
		return stringOnShow;
	}

	public int getIntOnEnter() {
		return intOnEnter;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
	 */
	@Override
	public void enter(ViewChangeEvent event) {
		intOnEnter = pInt;
	}

	@OnShow
	public void showing() {
		stringOnShow = pString;
	}

}
