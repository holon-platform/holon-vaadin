/*
 * Copyright 2000-2017 Holon TDCN.
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
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.vaadin.components.Components;
import com.holonplatform.vaadin.components.PropertyViewForm;
import com.holonplatform.vaadin.components.PropertyViewGroup;
import com.holonplatform.vaadin.components.ViewComponent;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.FormLayout;

@SuppressWarnings("unused")
public class ExampleViewComponent {

	public void view1() {
		// tag::view1[]
		ViewComponent<String> view = Components.view.component(String.class)
				.caption("TheCaption", "caption.message.code").icon(VaadinIcons.CAMERA).styleName("my-style").build(); // <1>

		view.setValue("TestValue"); // <2>
		String value = view.getValue(); // <3>
		// end::view1[]
	}

	public void view2() {
		// tag::view2[]
		final PathProperty<Long> ID = PathProperty.create("id", Long.class);
		final PathProperty<String> DESCRIPTION = PathProperty.create("description", String.class);

		final PropertySet<?> PROPERTIES = PropertySet.of(ID, DESCRIPTION);

		PropertyViewGroup viewGroup = Components.view.propertyGroup().properties(PROPERTIES).build(); // <1>

		PropertyViewForm viewForm = Components.view.formVertical().properties(PROPERTIES).build(); // <2>

		viewForm = Components.view.form(new FormLayout()).properties(PROPERTIES) //
				.composer((layout, source) -> { // <3>
					source.getValueComponents().forEach(c -> layout.addComponent(c.getComponent()));
				}).build();

		viewForm.setValue(PropertyBox.builder(PROPERTIES).set(ID, 1L).set(DESCRIPTION, "Test").build()); // <4>

		PropertyBox value = viewForm.getValue(); // <5>
		// end::view2[]
	}

}
