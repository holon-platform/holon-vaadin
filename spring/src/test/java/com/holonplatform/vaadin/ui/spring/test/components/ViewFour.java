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

import javax.annotation.security.RolesAllowed;

import com.holonplatform.vaadin.navigator.ViewContentProvider;
import com.holonplatform.vaadin.navigator.annotations.VolatileView;
import com.holonplatform.vaadin.ui.spring.test.TestNavigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;

@VolatileView
@RolesAllowed({ "r1", "r2" })
@SpringView(name = TestNavigator.VIEW_FOUR)
public class ViewFour implements View, ViewContentProvider {

	private static final long serialVersionUID = 1L;

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
	 */
	@Override
	public void enter(ViewChangeEvent event) {
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.navigator.ViewContentProvider#getViewContent()
	 */
	@Override
	public Component getViewContent() {
		return new Label("FOUR");
	}

}
