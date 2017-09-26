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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

import com.holonplatform.vaadin.navigator.annotations.ViewContext;
import com.holonplatform.vaadin.navigator.annotations.ViewParameter;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.VerticalLayout;

public class ViewSeven extends VerticalLayout implements View {

	private static final long serialVersionUID = 1L;

	@ViewContext
	private ContextTestData data;

	@ViewParameter(defaultValue = "DFT")
	private String param;

	@ViewParameter(required = true)
	private String param2;

	@ViewParameter
	private Date paramDate;

	@ViewParameter
	private LocalDate paramLocalDate;
	@ViewParameter
	private LocalTime paramLocalTime;
	@ViewParameter
	private LocalDateTime paramLocalDateTime;

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

	public Date getParamDate() {
		return paramDate;
	}

	public void setParamDate(Date paramDate) {
		this.paramDate = paramDate;
	}

	public LocalDate getParamLocalDate() {
		return paramLocalDate;
	}

	public LocalTime getParamLocalTime() {
		return paramLocalTime;
	}

	public LocalDateTime getParamLocalDateTime() {
		return paramLocalDateTime;
	}

	@Override
	public void enter(ViewChangeEvent event) {
	}

}
