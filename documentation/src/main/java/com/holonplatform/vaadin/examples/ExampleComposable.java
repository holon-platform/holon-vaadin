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
package com.holonplatform.vaadin.examples;

import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.vaadin.components.Components;
import com.holonplatform.vaadin.components.ComposableComponent.ComponentsWidthMode;
import com.holonplatform.vaadin.components.PropertyInputForm;
import com.vaadin.icons.VaadinIcons;

@SuppressWarnings("unused")
public class ExampleComposable {

	public void wmode() {
		// tag::wmode[]
		final PathProperty<Long> ID = PathProperty.create("id", Long.class);
		final PathProperty<String> DESCRIPTION = PathProperty.create("description", String.class);
		final PropertySet<?> PROPERTIES = PropertySet.of(ID, DESCRIPTION);

		PropertyInputForm form = Components.input.form().properties(PROPERTIES)
				.componentsWidthMode(ComponentsWidthMode.FULL) // <1>
				.build();
		// end::wmode[]
	}

	public void ccfg() {
		// tag::ccfg[]
		final PathProperty<Long> ID = PathProperty.create("id", Long.class);
		final PathProperty<String> DESCRIPTION = PathProperty.create("description", String.class);
		final PropertySet<?> PROPERTIES = PropertySet.of(ID, DESCRIPTION);

		PropertyInputForm form = Components.input.form().properties(PROPERTIES)
				.componentConfigurator(ID, cfg -> cfg.styleName("id-input").description("The ID")) // <1>
				.componentConfigurator(DESCRIPTION, cfg -> cfg.icon(VaadinIcons.CLIPBOARD_TEXT)) // <2>
				.build();
		// end::ccfg[]
	}

}
