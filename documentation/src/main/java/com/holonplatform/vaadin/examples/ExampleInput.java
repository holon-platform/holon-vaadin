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
import com.holonplatform.core.Validator.ValidationException;
import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyValueConverter;
import com.holonplatform.vaadin.components.Components;
import com.holonplatform.vaadin.components.Field;
import com.holonplatform.vaadin.components.Input;
import com.holonplatform.vaadin.components.MultiSelect;
import com.holonplatform.vaadin.components.SingleSelect;
import com.holonplatform.vaadin.components.ValidatableInput;
import com.holonplatform.vaadin.components.ValidationStatusHandler;
import com.holonplatform.vaadin.components.ValidationStatusHandler.Status;
import com.holonplatform.vaadin.components.ValueHolder;
import com.holonplatform.vaadin.components.builders.DateInputBuilder.Resolution;
import com.vaadin.data.Converter;
import com.vaadin.data.HasValue;
import com.vaadin.data.Result;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.data.converter.StringToLongConverter;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
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

	public void valueChangeMode() {
		// tag::valuechangemode[]
		Input<String> stringInput = Components.input.string().withValueChangeListener(event -> {
			// ...
		}).valueChangeMode(ValueChangeMode.LAZY) // <1>
				.valueChangeTimeout(1000) // <2>
				.build();

		boolean supported = stringInput.isValueChangeModeSupported(); // <3>
		if (supported) {
			stringInput.setValueChangeMode(ValueChangeMode.BLUR); // <4>
		}
		// end::valuechangemode[]
	}

	public void validatableBuilders() {
		// tag::validatable1[]
		Input<String> stringInput = Components.input.string().build();

		ValidatableInput<String> validatableInput = ValidatableInput.from(stringInput); // <1>

		validatableInput = ValidatableInput.from(new TextField()); // <2>
		// end::validatable1[]

		// tag::validatable2[]
		validatableInput = Components.input.string() //
				.caption("The caption") //
				.validatable() // <1>
				.required("Value is required") // <2>
				.withValidator(Validator.max(100)) // <3>
				.build();
		// end::validatable2[]
	}

	public void validatable() {
		// tag::validatable3[]
		ValidatableInput<String> validatableInput = Components.input.string().validatable().build(); // <1>

		validatableInput.addValidator(Validator.max(100)); // <2>
		validatableInput.addValidator(Validator.email("Must be a valid e-mail address", "invalid.email.message.code")); // <3>
		validatableInput
				.addValidator(Validator.create(value -> value.length() >= 3, "Must be at least 3 characters long")); // <4>
		// end::validatable3[]

		// tag::validatable4[]
		try {
			validatableInput.validate(); // <1>
		} catch (ValidationException e) {
			// do something at validation failure
		}

		boolean valid = validatableInput.isValid(); // <2>
		// end::validatable4[]
	}

	public void validateOnValueChange() {
		// tag::validatable5[]
		ValidatableInput<String> validatableInput = Components.input.string().validatable() //
				.validateOnValueChange(true) // <1>
				.build();

		validatableInput.setValidateOnValueChange(false); // <2>
		// end::validatable5[]
	}

	public void validationHandler() {
		// tag::validatable6[]
		ValidatableInput<String> validatableInput = Components.input.string().validatable() //
				.validationStatusHandler(event -> { // <1>
					Status status = event.getStatus();
					// ....
				}).build();

		validatableInput.setValidationStatusHandler(event -> { // <2>
			event.getError();
			// ...
		});
		// end::validatable6[]

		// tag::validatable7[]
		Label statusLabel = new Label();
		ValidationStatusHandler vsh = ValidationStatusHandler.label(statusLabel); // <1>

		ValidationStatusHandler usingNotifications = ValidationStatusHandler.notification(); // <2>
		// end::validatable7[]
	}

	public void input7() {
		// tag::input7[]
		Input<String> stringInput = Components.input.string().build();

		Input<Integer> integerInput = Input.from(stringInput, new StringToIntegerConverter("Conversion error")); // <2>

		Input<Boolean> booleanInput = Input.from(integerInput, // <2>
				Converter.from(value -> Result.ok((value == null) ? Boolean.FALSE : (value.intValue() > 0)),
						value -> (value == null) ? null : (value ? 1 : 0)));

		Input<Long> longInput = Input.from(new TextField(), new StringToLongConverter("Conversion error")); // <3>
		// end::input7[]
	}

	public void input8() {
		// tag::input8[]
		Input<Integer> integerInput = Components.input.number(Integer.class).build();

		final Property<Boolean> BOOL_PROPERTY = PathProperty.create("bool", Boolean.class); // <1>

		Input<Boolean> booleanInput = Input.from(integerInput, BOOL_PROPERTY,
				PropertyValueConverter.numericBoolean(Integer.class)); // <2>
		// end::input8[]
	}

	public void input10() {
		// tag::input10[]
		Field<Locale> localeField = Components.input.singleSelect(Locale.class).items(Locale.US, Locale.CANADA)
				.caption("Select Locale").fullWidth().withValue(Locale.US).asField(); // <1>
		// end::input10[]
	}

	private class TestData {

	}

}
