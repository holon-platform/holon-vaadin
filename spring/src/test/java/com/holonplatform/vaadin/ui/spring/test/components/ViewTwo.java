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

import com.holonplatform.vaadin.navigator.ViewNavigator;
import com.holonplatform.vaadin.navigator.annotations.OnLeave;
import com.holonplatform.vaadin.navigator.annotations.OnShow;
import com.holonplatform.vaadin.navigator.annotations.ViewContext;
import com.holonplatform.vaadin.ui.spring.test.TestNavigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.VerticalLayout;

@UIScope
@RolesAllowed("r1")
@SpringView(name = TestNavigator.VIEW_TWO)
public class ViewTwo extends VerticalLayout implements View {

	private static final long serialVersionUID = 1L;

	@ViewContext
	private ViewNavigator navigator;

	private int enterCount = 0;
	private int leaveCount = 0;

	private View previousView;

	public int getEnterCount() {
		return enterCount;
	}

	public int getLeaveCount() {
		return leaveCount;
	}

	public View getPreviousView() {
		return previousView;
	}

	public ViewNavigator getNavigator() {
		return navigator;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
	 */
	@Override
	public void enter(ViewChangeEvent event) {
		enterCount++;
	}

	@OnShow
	public void onShow(ViewChangeEvent evt) {
		enterCount++;

		previousView = evt.getOldView();
	}

	@OnLeave
	public void onLeave() {
		leaveCount++;
	}

}
