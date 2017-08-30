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
package com.holonplatform.vaadin.internal.components.builders;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.vaadin.components.builders.ComponentConfigurator;
import com.vaadin.ui.AbstractComponent;

/**
 * Abstract {@link ComponentConfigurator} with localization support.
 * 
 * @since 5.0.0
 */
public abstract class AbstractLocalizableComponentConfigurator<I extends AbstractComponent, B extends ComponentConfigurator<B>>
		extends AbstractComponentConfigurator<I, B> {

	/**
	 * Deferred messages localization
	 */
	protected boolean deferLocalization = false;

	protected Localizable caption;
	protected Localizable description;

	/**
	 * Constructor
	 * @param instance The instance to configure
	 */
	public AbstractLocalizableComponentConfigurator(I instance) {
		super(instance);
	}

	/**
	 * Performs messages localization
	 * @param instance Building instance
	 */
	protected void localize(I instance) {
		if (caption != null) {
			instance.setCaption(LocalizationContext.translate(caption, true));
		}
		if (description != null) {
			instance.setDescription(LocalizationContext.translate(description, true));
		}
	}

	/**
	 * Setup instance localization, checking deferred localization
	 * @param instance Building instance
	 * @return Setted up instance
	 */
	protected I setupLocalization(I instance) {
		if (deferLocalization) {
			instance.addAttachListener((e) -> localize(instance));
		} else {
			localize(instance);
		}
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.ComponentBuilder#caption(com.holonplatform.core.i18n.Localizable)
	 */
	@Override
	public B caption(Localizable caption) {
		this.caption = caption;
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ComponentBuilder#description(com.holonplatform.core.i18n.
	 * Localizable)
	 */
	@Override
	public B description(Localizable description) {
		this.description = description;
		return builder();
	}

}
