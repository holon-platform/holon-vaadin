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

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.components.Components;
import com.vaadin.shared.ui.dnd.DropEffect;
import com.vaadin.shared.ui.dnd.EffectAllowed;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("unused")
public class ExampleBuilders {

	public void i18n() {
		// tag::i18n[]
		Button btn = Components.button().caption("DefaultCaption", "button.caption.code") // <1>
				.build();

		Label lbl = Components.label()
				.content(Localizable.builder().message("DefaultMessage").messageCode("my.message.code").build()) // <2>
				.build();
		// end::i18n[]
	}

	public void dnd() {
		// tag::dnd[]
		Label lbl = Components.label().content("Draggable").dragSource(dragSource -> { // <1>
			dragSource.setEffectAllowed(EffectAllowed.MOVE); // <2>
			dragSource.setDataTransferText("hello receiver");
			dragSource.addDragStartListener(e -> Notification.show("Drag event started"));
		}).build();

		VerticalLayout droppableLayout = Components.vl().fullWidth().caption("Drop things inside me")
				.dropTarget((dropTarget, component) -> { // <3>
					dropTarget.setDropEffect(DropEffect.MOVE); // <4>
					dropTarget.addDropListener(dropEvent -> { // <5>
						dropEvent.getDragSourceComponent().ifPresent(dragged -> component.addComponent(dragged));
						Notification.show("DropEvent with data transfer text: " + dropEvent.getDataTransferText());
					});
				}).build();
		// end::dnd[]
	}

}
