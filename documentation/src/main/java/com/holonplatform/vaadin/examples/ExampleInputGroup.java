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

import com.holonplatform.core.Validator;
import com.holonplatform.core.Validator.ValidationException;
import com.holonplatform.core.property.NumericProperty;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.property.StringProperty;
import com.holonplatform.core.property.VirtualProperty;
import com.holonplatform.vaadin.components.Components;
import com.holonplatform.vaadin.components.ComposableComponent.ComponentsWidthMode;
import com.holonplatform.vaadin.components.PropertyInputForm;
import com.holonplatform.vaadin.components.PropertyInputGroup;
import com.vaadin.data.converter.StringToLongConverter;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

@SuppressWarnings("unused")
public class ExampleInputGroup {

	public void group1a() {
		// tag::group1a[]
		final NumericProperty<Long> ID = NumericProperty.longType("id");
		final StringProperty DESCRIPTION = StringProperty.create("description");

		final PropertySet<?> PROPERTIES = PropertySet.of(ID, DESCRIPTION);

		PropertyInputGroup group = Components.input.propertyGroup().properties(PROPERTIES) // <1>
				.build();
		// end::group1a[]
	}

	public void group1b() {
		// tag::group1b[]
		PropertyInputGroup group = Components.input.propertyGroup().properties(PROPERTIES)
				.bind(ID, Components.input.number(Long.class).build()) // <1>
				.bind(DESCRIPTION, new TextField()) // <2>
				.build();
		// end::group1b[]
	}

	public void group1c() {
		// tag::group1c[]
		PropertyInputGroup group = Components.input.propertyGroup().properties(PROPERTIES)
				.bind(ID, Components.input.string().build(), new StringToLongConverter("Conversion error")) // <1>
				.bind(ID, new TextField(), new StringToLongConverter("Conversion error")) // <2>
				.build();
		// end::group1c[]
	}

	public void group2() {
		// tag::group2[]
		PropertyInputGroup group = Components.input.propertyGroup().properties(PROPERTIES).build();

		group.setValue(PropertyBox.builder(PROPERTIES).set(ID, 1L).set(DESCRIPTION, "TestDescription").build()); // <1>

		PropertyBox value = group.getValue(); // <2>

		group.addValueChangeListener(e -> { // <3>
			PropertyBox changedValue = e.getValue();
		});
		// end::group2[]
	}

	private final static NumericProperty<Long> ID = NumericProperty.longType("id");
	private final static StringProperty DESCRIPTION = StringProperty.create("description");

	private final static PropertySet<?> PROPERTIES = PropertySet.of(ID, DESCRIPTION);

	public void group3() {
		// tag::group3[]
		PropertyInputGroup group = Components.input.propertyGroup().properties(PROPERTIES) //
				.readOnly(ID) // <1>
				.build();
		// end::group3[]
	}

	public void group3b() {
		// tag::group3b[]
		PropertyInputGroup group = Components.input.propertyGroup().properties(PROPERTIES) //
				.hidden(ID) // <1>
				.build();
		// end::group3b[]
	}

	public void group3c() {
		// tag::group3c[]
		PropertyInputGroup group = Components.input.propertyGroup().properties(PROPERTIES) //
				.defaultValue(DESCRIPTION, property -> "Default") // <1>
				.build();
		// end::group3c[]
	}

	public void group4a() {
		// tag::group4a[]
		PropertyInputGroup group = Components.input.propertyGroup().properties(PROPERTIES) //
				.withValidator(DESCRIPTION, Validator.max(100)) // <1>
				.withValidator(ID, com.vaadin.data.Validator.from(id -> id != null, "Id must be not null")) // <2>
				// group validation
				.withValidator(Validator.create(propertyBox -> propertyBox.getValue(ID) > 0,
						"The ID value must be greater than 0")) // <3>
				.build();
		// end::group4a[]
	}

	public void group4b() {
		// tag::group4b[]
		PropertyInputGroup group = Components.input.propertyGroup().properties(PROPERTIES) //
				.required(ID) // <1>
				.required(ID, "The ID value is required") // <2>
				.build();
		// end::group4b[]
	}

	public void group5() {
		// tag::group5[]
		PropertyInputGroup group = createInputGroup();

		try {

			group.validate(); // <1>

			PropertyBox value = group.getValue(); // <2>

		} catch (ValidationException e) {
			// validation failed
		}

		PropertyBox value = group.getValue(false); // <3>

		group.getValueIfValid().ifPresent(propertyBox -> { // <4>
			// ...
		});
		// end::group5[]
	}

	public void group6a() {
		// tag::group6a[]
		PropertyInputGroup group = Components.input.propertyGroup().properties(PROPERTIES) //
				.ignorePropertyValidation() // <1>
				.build();
		// end::group6a[]
	}

	public void group6b() {
		// tag::group6b[]
		PropertyInputGroup group = Components.input.propertyGroup().properties(PROPERTIES) //
				.validateOnValueChange(false) // <1>
				.stopValidationAtFirstFailure(true) // <2>
				.stopOverallValidationAtFirstFailure(true) // <3>
				.build();
		// end::group6b[]
	}

	public void group6c() {
		// tag::group6c[]
		Label statusLabel = new Label();

		PropertyInputGroup group = Components.input.propertyGroup().properties(PROPERTIES) //
				.validationStatusHandler(validationEvent -> { // <1>
					// ...
				}).propertiesValidationStatusHandler(validationEvent -> { // <2>
					// ...
				}).validationStatusHandler(statusLabel) // <3>
				.build();
		// end::group6c[]
	}

	public void group7a() {
		// tag::group7a[]
		PropertyInputGroup group = Components.input.propertyGroup().properties(PROPERTIES) //
				.withValueChangeListener(e -> { // <1>
					PropertyBox value = e.getValue();
					// ...
				}).withValueChangeListener(DESCRIPTION, e -> { // <2>
					String description = e.getValue();
					// ...
				}) //
				.valueChangeMode(ValueChangeMode.BLUR) // <3>
				.valueChangeMode(DESCRIPTION, ValueChangeMode.LAZY) // <4>
				.valueChangeTimeout(DESCRIPTION, 500) // <5>
				.build();
		// end::group7a[]
	}

	public void group7b() {
		// tag::group7b[]
		PropertyInputGroup group = Components.input.propertyGroup().properties(PROPERTIES) //
				.withValueChangeListener(DESCRIPTION, (event, binder) -> { // <1>
					String description = event.getValue();
					Long id = binder.requireInput(ID).getValue();
					// ...
				}).build();
		// end::group7b[]
	}

	public void group8() {
		// tag::group8[]
		final VirtualProperty<String> VIRTUAL = VirtualProperty.create(String.class,
				propertyBox -> propertyBox.getValue(ID) + " -" + propertyBox.getValue(DESCRIPTION));

		final PropertySet<?> PROPERTIES = PropertySet.of(ID, DESCRIPTION, VIRTUAL);

		PropertyInputGroup group = Components.input.propertyGroup().properties(PROPERTIES) //
				.withValueChangeListener(DESCRIPTION, (event, binder) -> { // <1>
					binder.refresh(VIRTUAL);
				}) //
				.build();
		// end::group8[]
	}

	public void group9() {
		// tag::group9[]
		PropertyInputGroup group = Components.input.propertyGroup().properties(PROPERTIES) //
				.excludeReadOnlyProperties() // <1>
				.build();
		// end::group9[]
	}

	public void form1() {
		// tag::form1[]
		PropertyInputForm form = Components.input.form().properties(PROPERTIES).build(); // <1>
		form = Components.input.formVertical().properties(PROPERTIES).build(); // <2>
		form = Components.input.formHorizontal().properties(PROPERTIES).build(); // <3>
		form = Components.input.formGrid().properties(PROPERTIES).build(); // <4>
		// end::form1[]
	}

	public void form2() {
		// tag::form2[]
		PropertyInputForm form = Components.input.formGrid().properties(PROPERTIES) //
				.initializer(gridLayout -> { // <1>
					gridLayout.setSpacing(true);
					gridLayout.addStyleName("my-style");
				}).build();
		// end::form2[]
	}

	public void form3() {
		// tag::form3[]
		PropertyInputForm form = Components.input.form(new FormLayout()) // <1>
				.properties(PROPERTIES).composer((layout, source) -> { // <2>
					source.getValueComponents().forEach(c -> layout.addComponent(c.getComponent()));
				}).build();
		// end::form3[]
	}

	public void form4() {
		// tag::form4[]
		PropertyInputForm form = Components.input.form(new FormLayout()).properties(PROPERTIES) //
				.composer((layout, source) -> {
					source.getValueComponents().forEach(c -> layout.addComponent(c.getComponent()));
				}).composeOnAttach(false) // <1>
				.build();

		form.compose(); // <2>
		// end::form4[]
	}

	public void wmode() {
		// tag::wmode[]
		PropertyInputForm form = Components.input.form().properties(PROPERTIES)
				.componentsWidthMode(ComponentsWidthMode.FULL) // <1>
				.build();
		// end::wmode[]
	}

	public void ccfg() {
		// tag::ccfg[]
		PropertyInputForm form = Components.input.form().properties(PROPERTIES)
				.componentConfigurator(ID, cfg -> cfg.styleName("id-input").description("The ID")) // <1>
				.componentConfigurator(DESCRIPTION, cfg -> cfg.icon(VaadinIcons.CLIPBOARD_TEXT)) // <2>
				.build();
		// end::ccfg[]
	}

	private static PropertyInputGroup createInputGroup() {
		return null;
	}

}
