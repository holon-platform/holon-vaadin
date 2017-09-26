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
import com.holonplatform.vaadin.components.Components;
import com.holonplatform.vaadin.components.Dialog;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Default {@link Dialog} implementation with a single <em>ok</em> button.
 *
 * @since 5.0.0
 */
public class DefaultDialog extends AbstractDialog {

	private static final long serialVersionUID = -2319092919438728731L;

	/**
	 * Default dialog button message
	 */
	public static final String DEFAULT_BUTTON_MESSAGE = "OK";
	/**
	 * Default dialog button message code
	 */
	public static final String DEFAULT_BUTTON_MESSAGE_CODE = "holon.common.ui.message.dialog.button.ok";

	/**
	 * Dialog ok button configurator
	 */
	private DialogButtonConfigurator okButtonConfigurator;

	public DefaultDialog() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.AbstractDialog#buildActions(com.vaadin.ui.HorizontalLayout)
	 */
	@Override
	protected void buildActions(HorizontalLayout actionsContainer) {
		Button btn = Components.button().styleName(ValoTheme.BUTTON_PRIMARY)
				.fullWidth().caption(Localizable.builder().message(DEFAULT_BUTTON_MESSAGE)
						.messageCode(DEFAULT_BUTTON_MESSAGE_CODE).build())
				.onClick(e -> onDialogButtonClick(e.getButton())).build();
		// configurator
		getOkButtonConfigurator().ifPresent(c -> c.configureDialogButton(Components.configure(btn)));
		actionsContainer.addComponent(btn);
	}

	/**
	 * Get the dialog ok button configurator
	 * @return the dialog ok button configurator
	 */
	public Optional<DialogButtonConfigurator> getOkButtonConfigurator() {
		return Optional.ofNullable(okButtonConfigurator);
	}

	/**
	 * Set the dialog ok button configurator
	 * @param okButtonConfigurator the dialog ok button configurator to set
	 */
	public void setOkButtonConfigurator(DialogButtonConfigurator okButtonConfigurator) {
		this.okButtonConfigurator = okButtonConfigurator;
	}

	/**
	 * Closes the dialog when button is clicked
	 * @param button Source button
	 */
	protected void onDialogButtonClick(Button button) {
		close(null);
	}

	// Builder

	/**
	 * Default {@link DialogBuilder} implementation.
	 */
	public static class DefaultBuilder extends AbstractBuilder<DefaultDialog, DialogBuilder> implements DialogBuilder {

		public DefaultBuilder() {
			super(new DefaultDialog());
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.Dialog.DialogBuilder#okButtonConfigurator(com.holonplatform.vaadin.
		 * components.Dialog.DialogButtonConfigurator)
		 */
		@Override
		public DialogBuilder okButtonConfigurator(DialogButtonConfigurator configurator) {
			getInstance().setOkButtonConfigurator(configurator);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentBuilder#build(com.vaadin.ui.
		 * AbstractComponent)
		 */
		@Override
		protected Dialog build(DefaultDialog instance) {
			return instance;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentConfigurator#builder()
		 */
		@Override
		protected DefaultBuilder builder() {
			return this;
		}

	}

}
