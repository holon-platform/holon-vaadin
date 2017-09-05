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

import com.holonplatform.vaadin.components.Components;
import com.holonplatform.vaadin.components.Dialog;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Notification;

public class ExampleDialog {

	public void dialog1() {
		// tag::dialog1[]
		Dialog dialog = Components.dialog() // <1>
				.draggable(false) // <2>
				.closable(true) // <3>
				.resizable(true) // <4>
				.modal(true) // <5>
				.message("Dialog message", "dialog.message.code") // <6>
				.okButtonConfigurator(cfg -> cfg.caption("Done").icon(VaadinIcons.CHECK_CIRCLE_O)) // <7>
				.withCloseListener((window, action) -> { // <8>
					// ...
				}).build();

		dialog.open(); // <9>

		dialog.close(); // <10>
		// end::dialog1[]
	}

	public void dialog2() {
		// tag::dialog2[]
		Components.questionDialog() // <1>
				.message("Can I do it for you?") // <2>
				.yesButtonConfigurator(cfg -> cfg.caption("Ok, let's do it")) // <3>
				.noButtonConfigurator(cfg -> cfg.caption("No, thanks")) // <4>
				.callback(answeredYes -> { // <5>
					Notification.show("Ok selected: " + answeredYes);
				}).build().open(); // <6>
		// end::dialog2[]
	}

}
