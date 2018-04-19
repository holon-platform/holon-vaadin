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
package com.holonplatform.vaadin.components.builders;

import com.holonplatform.core.Context;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.i18n.LocalizationContext;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Label;

/**
 * Interface to configure a {@link Label}.
 * 
 * @param <B> Concrete configurator type
 * 
 * @since 5.1.0
 */
public interface LabelConfigurator<B extends LabelConfigurator<B>> extends ComponentConfigurator<B> {

	/**
	 * Sets the label content
	 * @param content The content to set
	 * @return this
	 */
	B content(String content);

	/**
	 * Sets the label content using a localizable <code>messageCode</code>.
	 * <p>
	 * For content localization, a {@link LocalizationContext} must be available and localized as {@link Context}
	 * resource when component is built or when component is displayed if {@link #deferLocalization()} is
	 * <code>true</code>.
	 * </p>
	 * @param defaultContent Default content if no translation is available for given <code>messageCode</code> for
	 *        current Locale.
	 * @param messageCode Content translation message key
	 * @param arguments Optional translation arguments
	 * @return this
	 */
	B content(String defaultContent, String messageCode, Object... arguments);

	/**
	 * Sets the label content using a {@link Localizable} message.
	 * <p>
	 * For content localization, a {@link LocalizationContext} must be available and localized as {@link Context}
	 * resource when component is built or when component is displayed if {@link #deferLocalization()} is
	 * <code>true</code>.
	 * </p>
	 * @param content Localizable content message
	 * @return this
	 */
	B content(Localizable content);

	/**
	 * Sets the content mode of the Label.
	 * @param contentMode The content mode to set
	 * @return this
	 */
	B contentMode(ContentMode contentMode);

	/**
	 * A shortcut to set the content mode to {@link ContentMode#HTML}
	 * @return this
	 */
	B html();

	/**
	 * Base Label configurator.
	 */
	public interface BaseLabelConfigurator extends LabelConfigurator<BaseLabelConfigurator> {

	}

}
