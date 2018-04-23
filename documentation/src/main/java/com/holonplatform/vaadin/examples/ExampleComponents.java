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

import com.holonplatform.vaadin.components.Components;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("unused")
public class ExampleComponents {

	class MyData {
	}

	public void builder1() {
		// tag::builder1[]
		Label lbl = Components.label() // <1>
				.fullWidth() // <2>
				.height(50, Unit.PIXELS) // <3>
				.styleName("my-style").styleName(ValoTheme.LABEL_BOLD) // <4>
				.icon(VaadinIcons.CHECK) // <5>
				.caption("The caption") // <6>
				.captionAsHtml() // <7>
				.description("The description") // <8>
				.withData(new MyData()) // <9>
				.hidden() // <10>
				.disabled() // <11>
				.responsive() // <12>
				.withAttachListener(event -> { // <13>
					// ...
				}).withDetachListener(event -> { // <14>
					// ...
				}).withContextClickListener(event -> { // <15>
					event.isDoubleClick();
					// ...
				}).errorHandler(event -> { // <16>

				})
				// Label specific configuration
				.content("Label content") // <17>
				.html() // <18>
				.build();
		// end::builder1[]
	}

	private static final Component COMPONENT_1 = new Label();
	private static final Component COMPONENT_2 = new Label();
	private static final Component COMPONENT_3 = new Label();
	private static final Component COMPONENT_4 = new Label();
	private static final Component COMPONENT_5 = new Label();
	private static final Component COMPONENT_6 = new Label();
	private static final Component COMPONENT_7 = new Label();

	public void builder2() {
		// tag::builder2[]
		VerticalLayout verticalLayout = Components.vl() // <1>
				.margin() // <2>
				.spacing() // <3>
				.add(COMPONENT_1).add(COMPONENT_2, COMPONENT_3) // <4>
				.addAndAlign(COMPONENT_4, Alignment.TOP_CENTER) // <5>
				.addAndExpand(COMPONENT_5, 0.5f) // <6>
				.addAndExpandFull(COMPONENT_6) // <7>
				.addAlignAndExpand(COMPONENT_7, Alignment.MIDDLE_CENTER, 1f) // <8>
				.deferLocalization().build();
		// end::builder2[]
	}

	public void builder3() {
		// tag::builder3[]
		Label label = new Label();

		Components.configure(label).fullWidth().content("my content").contentMode(ContentMode.PREFORMATTED); // <1>

		VerticalLayout verticalLayout = new VerticalLayout();

		Components.configure(verticalLayout).spacing().addAndAlign(COMPONENT_1, Alignment.TOP_CENTER) // <2>
				.addAndExpandFull(COMPONENT_2);
		// end::builder3[]
	}

	public void builder4() {
		// tag::builder4[]
		Label lbl = Components.label() //
				.caption("Default caption", "caption.message.code") //
				.deferLocalization() // <1>
				.build();
		// end::builder4[]
	}

}
