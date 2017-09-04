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
import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.vaadin.components.Components;
import com.holonplatform.vaadin.components.PropertyInputForm;
import com.holonplatform.vaadin.components.PropertyInputGroup;
import com.vaadin.ui.FormLayout;

@SuppressWarnings("unused")
public class ExampleInputGroup {

	// private static final PropertySet<?> PROPERTY_SET = PropertySet.of();

	public void group1() {
		// tag::group1[]
		final PathProperty<Long> ID = PathProperty.create("id", Long.class);
		final PathProperty<String> DESCRIPTION = PathProperty.create("description", String.class);

		final PropertySet<?> PROPERTIES = PropertySet.of(ID, DESCRIPTION);

		PropertyInputGroup group = Components.input.propertyGroup().properties(PROPERTIES) // <1>
				.bind(ID, Components.input.number(Long.class).build()) // <2>
				.bind(DESCRIPTION, Components.input.string().maxLength(100).build()) // <3>
				.build();

		group.setValue(PropertyBox.builder(PROPERTIES).set(ID, 1L).set(DESCRIPTION, "TestDescription").build()); // <4>

		PropertyBox value = group.getValue(); // <5>

		group.addValueChangeListener(e -> { // <6>
			PropertyBox changedValue = e.getValue();
		});
		// end::group1[]
	}

	public void group2() {
		// tag::group2[]
		final PathProperty<Long> ID = PathProperty.create("id", Long.class);
		final PathProperty<String> DESCRIPTION = PathProperty.create("description", String.class);

		final PropertySet<?> PROPERTIES = PropertySet.of(ID, DESCRIPTION);

		PropertyInputGroup group = Components.input.propertyGroup().properties(PROPERTIES).build(); // <1>
		// end::group2[]
	}

	public void group3() {
		// tag::group3[]
		final PathProperty<Long> ID = PathProperty.create("id", Long.class);
		final PathProperty<String> DESCRIPTION = PathProperty.create("description", String.class);

		final PropertySet<?> PROPERTIES = PropertySet.of(ID, DESCRIPTION);

		PropertyInputGroup group = Components.input.propertyGroup().properties(PROPERTIES) //
				.readOnly(ID) // <1>
				.build();
		// end::group3[]
	}

	public void group4() {
		// tag::group4[]
		final PathProperty<Long> ID = PathProperty.create("id", Long.class);
		final PathProperty<String> DESCRIPTION = PathProperty.create("description", String.class);

		final PropertySet<?> PROPERTIES = PropertySet.of(ID, DESCRIPTION);

		PropertyInputGroup group = Components.input.propertyGroup().properties(PROPERTIES) //
				.withValidator(DESCRIPTION, Validator.max(100)) // <1>
				.required(ID) // <2>
				.required(ID, "The ID value is required") // <3>
				.withValidator(Validator.create(propertyBox -> propertyBox.getValue(ID) > 0,
						"The ID value must be greater than 0")) // <4>
				.build();
		// end::group4[]
	}

	public void group5() {
		// tag::group5[]
		PropertyInputGroup group = createInputGroup();

		group.validate(); // <1>

		PropertyBox value = group.getValue(); // <2>

		value = group.getValue(false); // <3>

		value = group.getValueIfValid().orElse(null); // <4>
		// end::group5[]
	}

	public void group6() {
		// tag::group6[]
		final PathProperty<Long> ID = PathProperty.create("id", Long.class);
		final PathProperty<String> DESCRIPTION = PathProperty.create("description", String.class);

		final PropertySet<?> PROPERTIES = PropertySet.of(ID, DESCRIPTION);

		PropertyInputGroup group = Components.input.propertyGroup().properties(PROPERTIES) //
				.stopValidationAtFirstFailure(true) // <1>
				.stopOverallValidationAtFirstFailure(true) // <2>
				.validationStatusHandler(validationEvent -> { // <3>
					// ...
				}).propertiesValidationStatusHandler(validationEvent -> { // <4>
					// ...
				}).build();
		// end::group6[]
	}

	public void form1() {
		// tag::form1[]
		final PathProperty<Long> ID = PathProperty.create("id", Long.class);
		final PathProperty<String> DESCRIPTION = PathProperty.create("description", String.class);

		final PropertySet<?> PROPERTIES = PropertySet.of(ID, DESCRIPTION);

		PropertyInputForm form = Components.input.form(new FormLayout()).properties(PROPERTIES).required(ID).build(); // <1>

		form = Components.input.form(new FormLayout()).properties(PROPERTIES).required(ID)
				.composer((layout, source) -> { // <2>
					source.getValueComponents().forEach(c -> layout.addComponent(c.getComponent()));
				}).build();

		form.setValue(PropertyBox.builder(PROPERTIES).set(ID, 1L).set(DESCRIPTION, "Test").build()); // <3>

		PropertyBox value = form.getValue(); // <4>
		// end::form1[]
	}

	public void form2() {
		// tag::form2[]
		PropertyInputForm form = Components.input.form().properties(PROPERTY_SET).build(); // <1>
		form = Components.input.formVertical().properties(PROPERTY_SET).build(); // <2>
		form = Components.input.formHorizontal().properties(PROPERTY_SET).build(); // <3>
		form = Components.input.formGrid().properties(PROPERTY_SET)
				.initializer(gridLayout -> gridLayout.setSpacing(true)).build(); // <4>
		// end::form2[]
	}

	@SuppressWarnings("static-method")
	private PropertyInputGroup createInputGroup() {
		return null;
	}

	private final static PropertySet<?> PROPERTY_SET = PropertySet.of();

}
