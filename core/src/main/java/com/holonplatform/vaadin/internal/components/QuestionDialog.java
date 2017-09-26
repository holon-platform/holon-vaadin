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
package com.holonplatform.vaadin.internal.components;

import java.util.Optional;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.components.Components;
import com.holonplatform.vaadin.components.Dialog;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Question {@link Dialog} implementation.
 * 
 * @since 5.0.0
 */
public class QuestionDialog extends AbstractDialog {

	private static final long serialVersionUID = -2862006732887204896L;

	/**
	 * Default yes dialog button message
	 */
	public static final String DEFAULT_YES_BUTTON_MESSAGE = "Yes";
	/**
	 * Default yes dialog button message code
	 */
	public static final String DEFAULT_YES_BUTTON_MESSAGE_CODE = "holon.common.ui.message.dialog.button.yes";
	/**
	 * Default no dialog button message
	 */
	public static final String DEFAULT_NO_BUTTON_MESSAGE = "No";
	/**
	 * Default no dialog button message code
	 */
	public static final String DEFAULT_NO_BUTTON_MESSAGE_CODE = "holon.common.ui.message.dialog.button.no";

	/**
	 * Dialog yes button configurator
	 */
	private DialogButtonConfigurator yesButtonConfigurator;

	/**
	 * Dialog no button configurator
	 */
	private DialogButtonConfigurator noButtonConfigurator;

	public QuestionDialog() {
		super();
	}

	/**
	 * Get the dialog yes button configurator
	 * @return the dialog yes button configurator
	 */
	public Optional<DialogButtonConfigurator> getYesButtonConfigurator() {
		return Optional.ofNullable(yesButtonConfigurator);
	}

	/**
	 * Set the dialog yes button configurator
	 * @param yesButtonConfigurator the dialog yes button configurator to set
	 */
	public void setYesButtonConfigurator(DialogButtonConfigurator yesButtonConfigurator) {
		this.yesButtonConfigurator = yesButtonConfigurator;
	}

	/**
	 * Get the dialog no button configurator
	 * @return the dialog no button configurator
	 */
	public Optional<DialogButtonConfigurator> getNoButtonConfigurator() {
		return Optional.ofNullable(noButtonConfigurator);
	}

	/**
	 * Set the dialog no button configurator
	 * @param noButtonConfigurator the dialog no button configurator to set
	 */
	public void setNoButtonConfigurator(DialogButtonConfigurator noButtonConfigurator) {
		this.noButtonConfigurator = noButtonConfigurator;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.AbstractDialog#buildActions(com.vaadin.ui.HorizontalLayout)
	 */
	@Override
	protected void buildActions(HorizontalLayout actionsContainer) {
		actionsContainer.setSpacing(true);
		// yes
		final Button btnYes = Components.button().styleName(ValoTheme.BUTTON_PRIMARY)
				.caption(Localizable.builder().message(DEFAULT_YES_BUTTON_MESSAGE)
						.messageCode(DEFAULT_YES_BUTTON_MESSAGE_CODE).build())
				.onClick(e -> onDialogYesButtonClick(e.getButton())).build();
		getYesButtonConfigurator().ifPresent(c -> c.configureDialogButton(Components.configure(btnYes)));
		actionsContainer.addComponent(btnYes);
		actionsContainer.setComponentAlignment(btnYes, Alignment.MIDDLE_LEFT);
		if (getWidth() > -1) {
			btnYes.setWidth("100%");
		}
		// no
		final Button btnNo = Components.button()
				.caption(Localizable.builder().message(DEFAULT_NO_BUTTON_MESSAGE)
						.messageCode(DEFAULT_NO_BUTTON_MESSAGE_CODE).build())
				.onClick(e -> onDialogNoButtonClick(e.getButton())).build();
		getNoButtonConfigurator().ifPresent(c -> c.configureDialogButton(Components.configure(btnNo)));
		actionsContainer.addComponent(btnNo);
		actionsContainer.setComponentAlignment(btnNo, Alignment.MIDDLE_RIGHT);
		if (getWidth() > -1) {
			btnNo.setWidth("100%");
		}
	}

	/**
	 * Yes button click handler
	 * @param button Source button
	 */
	protected void onDialogYesButtonClick(Button button) {
		close(Boolean.TRUE);
	}

	/**
	 * No button click handler
	 * @param button Source button
	 */
	protected void onDialogNoButtonClick(Button button) {
		close(Boolean.FALSE);
	}

	// Builder

	/**
	 * Default {@link QuestionDialogBuilder} implementation.
	 */
	public static class DefaultBuilder extends AbstractBuilder<QuestionDialog, QuestionDialogBuilder>
			implements QuestionDialogBuilder {

		public DefaultBuilder() {
			super(new QuestionDialog());
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.Dialog.QuestionDialogBuilder#yesButtonConfigurator(com.holonplatform.
		 * vaadin.components.Dialog.DialogButtonConfigurator)
		 */
		@Override
		public QuestionDialogBuilder yesButtonConfigurator(DialogButtonConfigurator configurator) {
			getInstance().setYesButtonConfigurator(configurator);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.Dialog.QuestionDialogBuilder#noButtonConfigurator(com.holonplatform.
		 * vaadin.components.Dialog.DialogButtonConfigurator)
		 */
		@Override
		public QuestionDialogBuilder noButtonConfigurator(DialogButtonConfigurator configurator) {
			getInstance().setNoButtonConfigurator(configurator);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.Dialog.QuestionDialogBuilder#callback(com.holonplatform.vaadin.
		 * components.Dialog.QuestionCallback)
		 */
		@Override
		public QuestionDialogBuilder callback(QuestionCallback callback) {
			ObjectUtils.argumentNotNull(callback, "QuestionCallback must be not null");
			getInstance().addCloseListener(callback);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentBuilder#build(com.vaadin.ui.
		 * AbstractComponent)
		 */
		@Override
		protected Dialog build(QuestionDialog instance) {
			return instance;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentConfigurator#builder()
		 */
		@Override
		protected QuestionDialogBuilder builder() {
			return this;
		}

	}

}
