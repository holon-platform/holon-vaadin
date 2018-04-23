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

import com.holonplatform.core.property.NumericProperty;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.property.StringProperty;
import com.holonplatform.vaadin.components.Components;
import com.holonplatform.vaadin.components.ComposableComponent.ComponentsWidthMode;
import com.holonplatform.vaadin.components.PropertyViewForm;
import com.holonplatform.vaadin.components.PropertyViewGroup;
import com.holonplatform.vaadin.components.ViewComponent;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.FormLayout;

@SuppressWarnings("unused")
public class ExampleViewComponent {

	public void view1() {
		// tag::view1[]
		ViewComponent<String> view = Components.view.component(String.class) // <1>
				.caption("TheCaption", "caption.message.code") // <2>
				.icon(VaadinIcons.CAMERA) //
				.styleName("my-style") //
				.build();

		view.setValue("TestValue"); // <3>

		String value = view.getValue(); // <4>
		// end::view1[]
	}

	public void view2() {
		// tag::view2[]
		final NumericProperty<Long> ID = NumericProperty.longType("id");
		final StringProperty DESCRIPTION = StringProperty.create("description");

		final PropertySet<?> PROPERTIES = PropertySet.of(ID, DESCRIPTION);

		PropertyViewGroup viewGroup = Components.view.propertyGroup().properties(PROPERTIES).build(); // <1>

		PropertyViewForm viewForm = Components.view.formVertical().properties(PROPERTIES).build(); // <2>

		viewForm.setValue(PropertyBox.builder(PROPERTIES).set(ID, 1L).set(DESCRIPTION, "Test").build()); // <3>
		PropertyBox value = viewForm.getValue(); // <4>
		// end::view2[]
	}

	private static final NumericProperty<Long> ID = NumericProperty.longType("id");
	private static final StringProperty DESCRIPTION = StringProperty.create("description");

	private static final PropertySet<?> PROPERTIES = PropertySet.of(ID, DESCRIPTION);

	public void view3() {
		// tag::view3[]
		PropertyViewForm viewForm = Components.view.form(new FormLayout()).properties(PROPERTIES) //
				.initializer(layout -> layout.setMargin(true)) // <1>
				.composer((layout, source) -> { // <2>
					source.getValueComponents().forEach(c -> layout.addComponent(c.getComponent()));
				}) //
				.componentConfigurator(DESCRIPTION, cfg -> cfg.styleName("my-style")) // <3>
				.componentsWidthMode(ComponentsWidthMode.FULL) // <4>
				.build();
		// end::view3[]
	}

	public void view4() {
		// tag::view4[]
		PropertyViewForm viewForm = Components.view.form(new FormLayout()).properties(PROPERTIES) //
				.propertyCaption(DESCRIPTION, "My caption") // <1>
				.hidePropertyCaption(ID) // <2>
				.build();
		// end::view4[]
	}

}
