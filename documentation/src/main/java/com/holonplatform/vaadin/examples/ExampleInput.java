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

import java.time.LocalDate;
import java.util.Locale;

import com.holonplatform.core.Validator;
import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyValueConverter;
import com.holonplatform.vaadin.components.Components;
import com.holonplatform.vaadin.components.Field;
import com.holonplatform.vaadin.components.Input;
import com.holonplatform.vaadin.components.ValidatableInput;
import com.vaadin.data.Converter;
import com.vaadin.data.Result;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;

@SuppressWarnings("unused")
public class ExampleInput {

	public void input1() {
		// tag::input1[]
		Input<String> stringInput = Components.input.string().caption("String input").inputPrompt("Value here")
				.maxLength(100).styleName("mystyle").build(); // <1>

		stringInput.setValue("test"); // <2>
		String value = stringInput.getValue(); // <3>

		stringInput.addValueChangeListener(e -> System.out.println("Value: " + e.getValue())); // <4>
		// end::input1[]
	}

	public void input2() {
		// tag::input2[]
		Input<LocalDate> dateInput = Components.input.localDate().caption("Date input").lenient(true).build(); // <1>

		Component inputComponent = dateInput.getComponent(); // <2>
		// end::input2[]
	}

	public void input3() {
		// tag::input3[]
		Input<String> stringInput = Input.from(new TextField());
		// end::input3[]
	}

	public void input4() {
		// tag::input4[]
		Field<Locale> localeField = Components.input.singleSelect(Locale.class).items(Locale.US, Locale.CANADA)
				.caption("Select Locale").fullWidth().withValue(Locale.US).asField(); // <1>
		// end::input4[]
	}

	public void input5() {
		// tag::input5[]
		Input<String> stringInput = Components.input.string().build();
		ValidatableInput<String> validatableInput = ValidatableInput.from(stringInput); // <1>

		validatableInput.addValidator(Validator.email()); // <2>
		validatableInput.addValidator(Validator.max(100)); // <3>

		validatableInput.setValidationStatusHandler(e -> { // <4>
			if (e.isInvalid()) {
				Notification.show(e.getErrorMessage(), Type.ERROR_MESSAGE);
			}
		});

		validatableInput.validate(); // <5>

		validatableInput.setValidateOnValueChange(true); // <6>
		// end::input5[]
	}

	public void input6() {
		// tag::input6[]
		ValidatableInput<String> validatableInput = ValidatableInput.from(Components.input.string().build()); // <1>

		validatableInput = ValidatableInput.from(new TextField()); // <2>

		validatableInput = Components.input.string().validatable() // <3>
				.required("Value is required") // <4>
				.withValidator(Validator.max(100)).build(); // <5>
		// end::input6[]
	}

	public void input7() {
		// tag::input7[]
		Input<Integer> integerInput = Components.input.number(Integer.class).build();

		Input<Boolean> booleanInput = Input.from(integerInput, // <1>
				Converter.from(value -> Result.ok((value == null) ? null : (value ? 1 : 0)),
						value -> (value == null) ? Boolean.FALSE : (value.intValue() > 0)));

		Boolean boolValue = booleanInput.getValue();

		final Property<Boolean> BOOL_PROPERTY = PathProperty.create("bool", Boolean.class);
		booleanInput = Input.from(integerInput, BOOL_PROPERTY, PropertyValueConverter.numericBoolean(Integer.class)); // <2>

		Input<Long> longInput = Input.from(new TextField(), // <3>
				Converter.from(value -> Result.ok((value == null) ? null : value.toString()),
						value -> (value == null || value.trim().isEmpty()) ? null : Long.parseLong(value)));
		// end::input7[]
	}

	private class TestData {

	}

}
