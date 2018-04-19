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
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Locale;

import com.holonplatform.core.Validator;
import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyValueConverter;
import com.holonplatform.vaadin.components.Components;
import com.holonplatform.vaadin.components.Field;
import com.holonplatform.vaadin.components.Input;
import com.holonplatform.vaadin.components.MultiSelect;
import com.holonplatform.vaadin.components.SingleSelect;
import com.holonplatform.vaadin.components.ValidatableInput;
import com.holonplatform.vaadin.components.ValueHolder;
import com.holonplatform.vaadin.components.builders.DateInputBuilder.Resolution;
import com.vaadin.data.Converter;
import com.vaadin.data.HasValue;
import com.vaadin.data.Result;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("unused")
public class ExampleInput {

	enum MyEnum {

	}

	public void component() {
		// tag::component[]
		Input<String> stringInput = Components.input.string().build(); // <1>

		Component component = stringInput.getComponent(); // <2>
		VerticalLayout vl = Components.vl().add(component).build(); // <3>
		// end::component[]
	}

	public void builder1() {
		// tag::builder1[]
		Input<String> stringInput = Components.input.string().build(); // <1>
		stringInput = Components.input.string(true).build(); // <2>
		Input<Integer> integerInput = Components.input.number(Integer.class).build(); // <3>
		Input<Boolean> booleanInput = Components.input.boolean_().build(); // <4>
		Input<Date> dateInput = Components.input.date(Resolution.DAY).build(); // <5>
		Input<LocalDate> localDateInput = Components.input.localDate().build(); // <6>
		Input<LocalDateTime> localDateTimeInput = Components.input.localDateTime().build(); // <7>

		SingleSelect<MyEnum> enumSingleSelect = Components.input.enumSingle(MyEnum.class).build(); // <8>
		MultiSelect<MyEnum> enumMultiSelect = Components.input.enumMulti(MyEnum.class).build(); // <9>
		// end::builder1[]
	}

	public void builder2() {
		// tag::builder2[]
		Input<String> stringInput = Components.input.string() //
				// Component configuration
				.fullWidth().styleName("my-style").caption("The caption") // <1>
				// Input configuration
				.readOnly() // <2>
				.tabIndex(10) // <3>
				.withValue("Initial value") // <4>
				.withValueChangeListener(event -> { // <5>
					event.getValue();
					event.getOldValue();
					// ...
				}).withFocusListener(event -> { // <6>
					// focused
				})
				// Specific String Input configuration
				.inputPrompt("The prompt") // <7>
				.maxLength(100) // <8>
				.blankValuesAsNull(true) // <9>
				.textChangeEventMode(ValueChangeMode.BLUR) // <10>
				.build();
		// end::builder2[]
	}

	public void builder3() {
		// tag::builder3[]
		TextField field = new TextField();

		Input<String> stringInput = Input.from(field); // <1>
		// end::builder3[]
	}

	public void builder4() {
		// tag::builder4[]
		TextField field = new TextField();

		Input<Integer> integerInput = Input.from(field, new StringToIntegerConverter("conversion error")); // <1>
		// end::builder4[]
	}

	public void builder5() {
		// tag::builder5[]
		Field<LocalDate> field = Components.input.localDate() // <1>
				// configuration omitted
				.asField(); // <2>

		HasValue<LocalDate> hasValue = field; // <3>
		Component component = field;
		// end::builder5[]
	}

	public void valueChange() {
		// tag::valuechange[]
		Input<String> stringInput = Components.input.string() //
				.withValueChangeListener(event -> { // <1>
					String currentValue = event.getValue(); // <2>
					String previousValue = event.getOldValue(); // <3>
					boolean byUser = event.isUserOriginated(); // <4>
					ValueHolder<String> source = event.getSource(); // <5>
					// ...
				}).build();
		// end::valuechange[]
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
				Converter.from(value -> Result.ok((value == null) ? Boolean.FALSE : (value.intValue() > 0)),
						value -> (value == null) ? null : (value ? 1 : 0)));

		Boolean boolValue = booleanInput.getValue();

		final Property<Boolean> BOOL_PROPERTY = PathProperty.create("bool", Boolean.class);
		booleanInput = Input.from(integerInput, BOOL_PROPERTY, PropertyValueConverter.numericBoolean(Integer.class)); // <2>

		Input<Integer> longInput = Input.from(new TextField(), // <3>
				new StringToIntegerConverter("Conversion error"));
		// end::input7[]
	}

	private class TestData {

	}

}
