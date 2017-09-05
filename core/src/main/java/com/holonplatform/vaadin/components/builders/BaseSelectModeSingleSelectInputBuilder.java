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
package com.holonplatform.vaadin.components.builders;

import com.holonplatform.core.Context;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.i18n.LocalizationContext;
import com.vaadin.server.SerializablePredicate;
import com.vaadin.ui.ComboBox.CaptionFilter;

/**
 * A {@link SingleSelectInputBuilder} for {@link RenderingMode#NATIVE_SELECT} and {@link RenderingMode#SELECT}.
 * 
 * @param <T> Value type
 * @param <B> Actual builder type
 * 
 * @since 5.0.0
 */
public interface BaseSelectModeSingleSelectInputBuilder<T, B extends BaseSelectModeSingleSelectInputBuilder<T, B>>
		extends SingleSelectInputBuilder<T, B> {

	/**
	 * Sets whether the user is allowed to select nothing. When true, a special empty item is shown to the user.
	 * @param emptySelectionAllowed true to allow not selecting anything, false to require selection
	 * @return this
	 */
	B emptySelectionAllowed(boolean emptySelectionAllowed);

	/**
	 * Sets the empty selection caption.
	 * @param caption the empty selection caption to set, <code>null</code> for none
	 * @return this
	 */
	default B emptySelectionCaption(String caption) {
		return emptySelectionCaption(Localizable.builder().message(caption).build());
	}

	/**
	 * Sets the empty selection caption using a localizable <code>messageCode</code>.
	 * <p>
	 * For caption localization, a {@link LocalizationContext} must be available and localized as {@link Context}
	 * resource when component is built or when component is displayed if {@link #deferLocalization()} is
	 * <code>true</code>.
	 * </p>
	 * @param defaultCaption Default message if no translation is available for given <code>messageCode</code> for
	 *        current Locale.
	 * @param messageCode the empty selection caption translation message key
	 * @param arguments Optional translation arguments
	 * @return this
	 */
	default B emptySelectionCaption(String defaultCaption, String messageCode, Object... arguments) {
		return emptySelectionCaption(Localizable.builder().message(defaultCaption).messageCode(messageCode)
				.messageArguments(arguments).build());
	}

	/**
	 * Sets the empty selection caption.
	 * <p>
	 * For caption localization, a {@link LocalizationContext} must be available and localized as {@link Context}
	 * resource when component is built or when component is displayed if {@link #deferLocalization()} is
	 * <code>true</code>.
	 * </p>
	 * @param caption Localizable empty selection caption
	 * @return this
	 */
	B emptySelectionCaption(Localizable caption);

	/**
	 * A {@link SingleSelectInputBuilder} for {@link RenderingMode#NATIVE_SELECT}.
	 * 
	 * @param <T> Value type
	 */
	public interface NativeModeSingleSelectInputBuilder<T>
			extends BaseSelectModeSingleSelectInputBuilder<T, NativeModeSingleSelectInputBuilder<T>> {

	}

	/**
	 * A {@link SingleSelectInputBuilder} for {@link RenderingMode#OPTIONS}.
	 * 
	 * @param <T> Value type
	 */
	public interface OptionsModeSingleSelectInputBuilder<T>
			extends SingleSelectInputBuilder<T, OptionsModeSingleSelectInputBuilder<T>> {

		/**
		 * Sets whether html is allowed in the item captions.
		 * @param htmlContentAllowed true if the captions are used as html, false if used as plain text
		 * @return this
		 */
		OptionsModeSingleSelectInputBuilder<T> htmlContentAllowed(boolean htmlContentAllowed);

		/**
		 * Sets the item enabled predicate. The predicate is applied to each item to determine whether the item should
		 * be enabled or disabled.
		 * @param itemEnabledProvider the item enabled provider to set (not null)
		 * @return this
		 */
		OptionsModeSingleSelectInputBuilder<T> itemEnabledProvider(SerializablePredicate<T> itemEnabledProvider);

	}

	/**
	 * A {@link SingleSelectInputBuilder} for {@link RenderingMode#SELECT}.
	 * 
	 * @param <T> Value type
	 */
	public interface SelectModeSingleSelectInputBuilder<T>
			extends BaseSelectModeSingleSelectInputBuilder<T, SelectModeSingleSelectInputBuilder<T>> {

		/**
		 * Sets the input prompt - a textual prompt that is displayed when the field would otherwise be empty, to prompt
		 * the user for input.
		 * <p>
		 * The input prompt is available only in {@link RenderingMode#SELECT} mode.
		 * </p>
		 * @param inputPrompt the input prompt to set, <code>null</code> for none
		 * @return this
		 */
		default SelectModeSingleSelectInputBuilder<T> inputPrompt(String inputPrompt) {
			return inputPrompt(Localizable.builder().message(inputPrompt).build());
		}

		/**
		 * Sets the input prompt - a textual prompt that is displayed when the field would otherwise be empty, to prompt
		 * the user for input - using a localizable <code>messageCode</code>.
		 * <p>
		 * For input prompt localization, a {@link LocalizationContext} must be available and localized as
		 * {@link Context} resource when component is built or when component is displayed if
		 * {@link #deferLocalization()} is <code>true</code>.
		 * </p>
		 * <p>
		 * The input prompt is available only in {@link RenderingMode#SELECT} mode.
		 * </p>
		 * @param defaultInputPrompt Default message if no translation is available for given <code>messageCode</code>
		 *        for current Locale.
		 * @param messageCode Input prompt translation message key
		 * @param arguments Optional translation arguments
		 * @return this
		 */
		default SelectModeSingleSelectInputBuilder<T> inputPrompt(String defaultInputPrompt, String messageCode,
				Object... arguments) {
			return inputPrompt(Localizable.builder().message(defaultInputPrompt).messageCode(messageCode)
					.messageArguments(arguments).build());
		}

		/**
		 * Sets the input prompt - a textual prompt that is displayed when the field would otherwise be empty, to prompt
		 * the user for input - using a {@link Localizable} message.
		 * <p>
		 * For input prompt localization, a {@link LocalizationContext} must be available and localized as
		 * {@link Context} resource when component is built or when component is displayed if
		 * {@link #deferLocalization()} is <code>true</code>.
		 * </p>
		 * <p>
		 * The input prompt is available only in {@link RenderingMode#SELECT} mode.
		 * </p>
		 * @param inputPrompt Localizable input prompt
		 * @return this
		 */
		SelectModeSingleSelectInputBuilder<T> inputPrompt(Localizable inputPrompt);

		/**
		 * Disables the possibility to input text into the field, so the field area of the component is just used to
		 * show what is selected. If the concrete select component does not support user input, this method has no
		 * effect.
		 * @return this
		 */
		SelectModeSingleSelectInputBuilder<T> disableTextInput();

		/**
		 * Sets whether to scroll the selected item visible (directly open the page on which it is) when opening the
		 * combo box popup or not. Only applies to select components with a suggestions popup. This requires finding the
		 * index of the item, which can be expensive in many large lazy loading containers.
		 * @param scrollToSelectedItem true to find the page with the selected item when opening the selection popup
		 * @return this
		 */
		SelectModeSingleSelectInputBuilder<T> scrollToSelectedItem(boolean scrollToSelectedItem);

		/**
		 * Sets the suggestion pop-up's width as a CSS string. By using relative units (e.g. "50%") it's possible to set
		 * the popup's width relative to the selection component itself.
		 * <p>
		 * Only applies to select field with backing components supporting a suggestion popup.
		 * </p>
		 * @param width the suggestion pop-up width
		 * @return this
		 */
		SelectModeSingleSelectInputBuilder<T> suggestionPopupWidth(String width);

		/**
		 * Sets the option filtering strategy.
		 * <p>
		 * Only applies to select field with backing components supporting a suggestion popup.
		 * </p>
		 * @param captionFilter the caption filtering strategy to use
		 * @return this
		 */
		SelectModeSingleSelectInputBuilder<T> filteringMode(CaptionFilter captionFilter);

	}

}
