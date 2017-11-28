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
package com.holonplatform.vaadin.components;

import com.holonplatform.core.Context;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.vaadin.components.builders.ButtonConfigurator.BaseButtonConfigurator;
import com.holonplatform.vaadin.components.builders.ComponentBuilder;
import com.holonplatform.vaadin.internal.components.DefaultDialog;
import com.holonplatform.vaadin.internal.components.QuestionDialog;
import com.vaadin.shared.Registration;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;

/**
 * Dialog window representation.
 *
 * @since 5.0.0
 */
public interface Dialog extends Component {

	/**
	 * Returns whether the dialog is modal.
	 * @return <code>true</code> if the dialog is modal, <code>false</code> otherwise
	 */
	boolean isModal();

	/**
	 * Sets whether the dialog is modal. When a modal dialog is open, components outside that dialog window cannot be
	 * accessed.
	 * @param modal <code>true</code> to set the dialog as modal, <code>false</code> to unset
	 */
	void setModal(boolean modal);

	/**
	 * If there are currently several window dialogs visible, calling this method makes this dialog window topmost.
	 * <p>
	 * This method can only be called if this dialog is connected to a UI. Else an illegal state exception is thrown.
	 * Also if there are modal windows and this dialog window is not modal, and illegal state exception is thrown.
	 * </p>
	 */
	void bringToFront();

	/**
	 * Open the dialog window using current {@link UI}.
	 */
	default void open() {
		open(UI.getCurrent());
	}

	/**
	 * Open the dialog window using given {@link UI}.
	 * @param ui UI to which to attach the dialog window
	 */
	void open(UI ui);

	/**
	 * Closes the dialog window
	 */
	void close();

	/**
	 * Adds a {@link CloseListener} for dialog closing events.
	 * @param listener The listener to add (not null)
	 * @return the listener {@link Registration}
	 */
	Registration addCloseListener(CloseListener listener);

	/**
	 * Listener for dialog closing event
	 */
	@FunctionalInterface
	public interface CloseListener {

		/**
		 * Invoked when the {@link Dialog} is closed
		 * @param dialog the dialog which was closed
		 * @param actionId Optional id of the action which caused the dialog closure
		 */
		void dialogClosed(Dialog dialog, Object actionId);

	}

	/**
	 * {@link CloseListener} extension which acts as callback for a question dialog to react to the user's response.
	 */
	@FunctionalInterface
	public interface QuestionCallback extends CloseListener {

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.Dialog.CloseListener#dialogClosed(com.holonplatform.vaadin.components.
		 * Dialog, java.lang.Object)
		 */
		@Override
		default void dialogClosed(Dialog dialog, Object actionId) {
			questionResponse(actionId != null && (boolean) actionId);
		}

		/**
		 * Invoked when the question dialog is closed as a result of a user action.
		 * @param answeredYes <code>true</code> if the user selected the <em>yes</em> action, <code>false</code>
		 *        otherwise
		 */
		void questionResponse(boolean answeredYes);

	}

	// Builders

	/**
	 * Gets a builder to create and open a {@link Dialog} window.
	 * <p>
	 * The dialog will present by default a single <em>ok</em> button.
	 * </p>
	 * @return {@link Dialog} builder
	 */
	public static DialogBuilder builder() {
		return new DefaultDialog.DefaultBuilder();
	}

	/**
	 * Gets a builder to create and open a question {@link Dialog} window.
	 * <p>
	 * The dialog will present by default a <em>yes</em> and a <em>no</em> button. Use
	 * {@link QuestionDialogBuilder#callback(com.holonplatform.vaadin.components.Dialog.QuestionCallback)} to handle the
	 * user selected answer.
	 * </p>
	 * @return Question {@link Dialog} builder
	 */
	public static QuestionDialogBuilder question() {
		return new QuestionDialog.DefaultBuilder();
	}

	/**
	 * Interface to configure {@link Dialog} buttons at build time.
	 */
	@FunctionalInterface
	public interface DialogButtonConfigurator {

		/**
		 * Configure the dialog button
		 * @param configurator Configurator to configure the dialog button
		 */
		void configureDialogButton(BaseButtonConfigurator configurator);

	}

	/**
	 * Question {@link Dialog} builder.
	 */
	public interface QuestionDialogBuilder extends Builder<QuestionDialogBuilder> {

		/**
		 * Set a {@link DialogButtonConfigurator} to configure the <em>yes</em> answer dialog button.
		 * @param configurator Dialog button configurator
		 * @return this
		 */
		QuestionDialogBuilder yesButtonConfigurator(DialogButtonConfigurator configurator);

		/**
		 * Set a {@link DialogButtonConfigurator} to configure the <em>no</em> answer dialog button.
		 * @param configurator Dialog button configurator
		 * @return this
		 */
		QuestionDialogBuilder noButtonConfigurator(DialogButtonConfigurator configurator);

		/**
		 * Set the callback to use when user select a <em>yes</em> or <em>no</em> answer from the question dialog.
		 * @param callback Question dialog callback
		 * @return this
		 */
		QuestionDialogBuilder callback(QuestionCallback callback);

	}

	/**
	 * Default {@link Dialog} builder.
	 */
	public interface DialogBuilder extends Builder<DialogBuilder> {

		/**
		 * Set a {@link DialogButtonConfigurator} to configure the <em>ok</em> dialog button.
		 * @param configurator Dialog button configurator
		 * @return this
		 */
		DialogBuilder okButtonConfigurator(DialogButtonConfigurator configurator);

	}

	/**
	 * Base dialog builder
	 * @param <B> Concrete builder type
	 */
	public interface Builder<B extends Builder<B>> extends ComponentBuilder<Dialog, B> {

		/**
		 * Sets the dialog message.
		 * @param message The dialog message
		 * @return this
		 */
		default B message(String message) {
			return message(Localizable.builder().message(message).build());
		}

		/**
		 * Sets the localizable dialog message.
		 * <p>
		 * For dialog message localization, a {@link LocalizationContext} must be available and localized as
		 * {@link Context} resource when dialog is displayed.
		 * </p>
		 * @param defaultMessage Default dialog message if no translation is available for given
		 *        <code>messageCode</code> for current Locale, or no {@link LocalizationContext} is available at all
		 * @param messageCode Dialog message translation message key
		 * @param arguments Optional Dialog message translation arguments
		 * @return this
		 */
		default B message(String defaultMessage, String messageCode, Object... arguments) {
			return message(Localizable.builder().message(defaultMessage).messageCode(messageCode)
					.messageArguments(arguments).build());
		}

		/**
		 * Sets the {@link Localizable} dialog message.
		 * <p>
		 * For message localization, a {@link LocalizationContext} must be available and localized as {@link Context}
		 * resource when dialog is displayed.
		 * </p>
		 * @param message {@link Localizable} dialog message
		 * @return this
		 */
		B message(Localizable message);

		/**
		 * Set the dialog message style name
		 * @param messageStyleName the dialog message style name to set
		 * @return this
		 */
		B messageStyleName(String messageStyleName);

		/**
		 * Set the dialog message description style name
		 * @param messageDescriptionStyleName the dialog message description style name to set
		 * @return this
		 */
		B messageDescriptionStyleName(String messageDescriptionStyleName);

		/**
		 * Set whether the dialog is modal. Default is <code>true</code>.
		 * @param modal whether the dialog is modal
		 * @return this
		 */
		B modal(boolean modal);

		/**
		 * Set whether the dialog is closable (i.e. a close button is show in the dialog caption). Default is
		 * <code>false</code>.
		 * @param closable whether the dialog is closable
		 * @return this
		 */
		B closable(boolean closable);

		/**
		 * Set whether the dialog is resizable. Default is <code>false</code>.
		 * @param resizable whether the dialog is resizable
		 * @return this
		 */
		B resizable(boolean resizable);

		/**
		 * Set whether the dialog is draggable. Default is <code>false</code>.
		 * @param draggable whether the dialog is draggable
		 * @return this
		 */
		B draggable(boolean draggable);

		/**
		 * Set whether the dialog messages allow HTML content.
		 * @param htmlContentAllowed <code>true</code> to allow HTML content for dialog messages
		 * @return this
		 */
		B htmlContentAllowed(boolean htmlContentAllowed);

		/**
		 * Add a {@link CloseListener} to be notified when dialog is closed
		 * @param closeListener The close listener to add (not null)
		 * @return this
		 */
		B withCloseListener(CloseListener closeListener);

		/**
		 * Sets the content of the dialog. If not <code>null</code>, the default dialog content will be replaced by the
		 * given content.
		 * @param content The component to use as dialog content
		 * @return this
		 */
		B content(Component content);

		/**
		 * Sets the position of the dialog window on the screen.
		 * @param x The x coordinate for the window
		 * @param y The y coordinate for the window
		 * @return this
		 */
		B position(int x, int y);

		/**
		 * Adds a close shortcut - pressing this key while holding down all (if any) modifiers specified while the
		 * dialog window is in focus will close the window.
		 * @param keyCode the keycode for invoking the shortcut
		 * @param modifiers the (optional) modifiers for invoking the shortcut. Can be set to null to be explicit about
		 *        not having modifiers.
		 * @return this
		 */
		B withCloseShortcut(int keyCode, int... modifiers);

		/**
		 * Build and open the dialog window using given {@link UI}.
		 * @param ui UI to which to attach the dialog window
		 * @return the {@link Dialog} instance
		 */
		Dialog open(UI ui);

		/**
		 * Build and open the dialog window using given current {@link UI}.
		 * @return the {@link Dialog} instance
		 */
		default Dialog open() {
			return open(UI.getCurrent());
		}

	}

}
