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
package com.holonplatform.vaadin.navigator.test.components;

import com.holonplatform.core.i18n.Caption;
import com.holonplatform.vaadin.navigator.ViewNavigator.ViewNavigatorChangeEvent;
import com.holonplatform.vaadin.navigator.annotations.OnShow;
import com.holonplatform.vaadin.navigator.annotations.VolatileView;
import com.vaadin.navigator.View;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;

@Caption("View4")
@VolatileView
public class ViewFour implements View {

	private static final long serialVersionUID = 1L;

	private View oldView;
	private String oldViewName;

	@Override
	public Component getViewComponent() {
		return new Label("FOUR");
	}

	public View getOldView() {
		return oldView;
	}

	public String getOldViewName() {
		return oldViewName;
	}

	@OnShow
	public void onShow(ViewNavigatorChangeEvent evt) {
		oldView = evt.getOldView();
		oldViewName = evt.getOldViewName().orElse(null);
	}

}
