/*
 * Copyright 2000-2016 Holon TDCN.
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
package com.holonplatform.vaadin.internal.components.builders;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.vaadin.components.Input;
import com.holonplatform.vaadin.components.builders.StringInputBuilder;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.TextArea;

/**
 * Builder to create {@link String} type fields.
 * 
 * @param <I> Internal field type
 * 
 * @since 5.0.0
 */
public abstract class AbstractStringFieldBuilder<I extends AbstractTextField>
		extends AbstractFieldBuilder<String, Input<String>, I, StringInputBuilder> implements StringInputBuilder {

	protected Localizable inputPrompt;

	public AbstractStringFieldBuilder(I instance) {
		super(instance);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.TextInputFieldBuilder#maxLength(int)
	 */
	@Override
	public StringInputBuilder maxLength(int maxLength) {
		getInstance().setMaxLength(maxLength);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.TextInputFieldBuilder#inputPrompt(java.lang.String)
	 */
	@Override
	public StringInputBuilder inputPrompt(String inputPrompt) {
		getInstance().setPlaceholder(inputPrompt);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.TextInputFieldBuilder#inputPrompt(java.lang.String,
	 * java.lang.String, java.lang.Object[])
	 */
	@Override
	public StringInputBuilder inputPrompt(String defaultInputPrompt, String messageCode, Object... arguments) {
		this.inputPrompt = Localizable.builder().message(defaultInputPrompt).messageCode(messageCode)
				.messageArguments(arguments).build();
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.TextInputFieldBuilder#inputPrompt(com.holonplatform.core.i18n.
	 * Localizable)
	 */
	@Override
	public StringInputBuilder inputPrompt(Localizable inputPrompt) {
		this.inputPrompt = inputPrompt;
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.TextInputFieldBuilder#textChangeEventMode(com.vaadin.ui.
	 * AbstractTextField.TextChangeEventMode)
	 */
	@Override
	public StringInputBuilder textChangeEventMode(ValueChangeMode inputEventMode) {
		getInstance().setValueChangeMode(inputEventMode);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.TextInputFieldBuilder#textChangeTimeout(int)
	 */
	@Override
	public StringInputBuilder textChangeTimeout(int timeout) {
		getInstance().setValueChangeTimeout(timeout);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.TextInputFieldBuilder#withFocusListener(com.vaadin.event.
	 * FieldEvents.FocusListener)
	 */
	@Override
	public StringInputBuilder withFocusListener(FocusListener listener) {
		getInstance().addFocusListener(listener);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.TextInputFieldBuilder#withBlurListener(com.vaadin.event.FieldEvents
	 * .BlurListener)
	 */
	@Override
	public StringInputBuilder withBlurListener(BlurListener listener) {
		getInstance().addBlurListener(listener);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.TextFieldBuilder#rows(int)
	 */
	@Override
	public StringInputBuilder rows(int rows) {
		if (getInstance() instanceof TextArea) {
			((TextArea) getInstance()).setRows(rows);
		}
		return builder();
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.internal.components.builders.AbstractFieldBuilder#localize(com.vaadin.ui.AbstractField)
	 */
	@Override
	protected void localize(I instance) {
		super.localize(instance);

		if (inputPrompt != null) {
			instance.setPlaceholder(LocalizationContext.translate(inputPrompt, true));
		}
	}

}
