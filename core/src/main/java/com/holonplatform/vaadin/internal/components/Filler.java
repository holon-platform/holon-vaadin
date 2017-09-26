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
package com.holonplatform.vaadin.internal.components;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Label;

/**
 * A {@link Label} with undefined size and the HTML entity <code>&nbsp;</code> as content, which can be used with full
 * expand ratio as a space filler in layouts.
 * 
 * @since 5.0.0
 */
public class Filler extends Label {

	private static final long serialVersionUID = 9118937511147761287L;

	public Filler() {
		super("&nbsp;", ContentMode.HTML);
		setSizeUndefined();
	}

}
