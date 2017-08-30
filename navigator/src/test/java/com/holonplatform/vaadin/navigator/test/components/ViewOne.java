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
package com.holonplatform.vaadin.navigator.test.components;

import com.holonplatform.vaadin.navigator.annotations.OnShow;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.VerticalLayout;

public class ViewOne extends VerticalLayout implements View {

	private static final long serialVersionUID = 1L;

	private int enterCount = 0;

	public int getEnterCount() {
		return enterCount;
	}

	@OnShow
	@Override
	public void enter(ViewChangeEvent event) {
		enterCount++;
	}

}
