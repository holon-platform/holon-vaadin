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
package com.holonplatform.vaadin.components;

import com.vaadin.ui.Component;

/**
 * Represents and object which can be represented by a UI {@link Component}, which can be obtained using the
 * {@link #getComponent()} method.
 *
 * @since 5.0.5
 */
public interface HasComponent {

	/**
	 * Get the UI {@link Component} which represents this object.
	 * @return the UI component
	 */
	Component getComponent();

}
