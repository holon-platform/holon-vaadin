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
package com.holonplatform.vaadin.internal.components.builders;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.components.builders.LabelConfigurator;
import com.holonplatform.vaadin.components.builders.LabelConfigurator.BaseLabelConfigurator;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Label;

/**
 * Default {@link LabelConfigurator} implementation.
 *
 * @since 5.1.0
 */
public class DefaultLabelConfigurator extends AbstractComponentConfigurator<Label, BaseLabelConfigurator>
		implements BaseLabelConfigurator {

	/**
	 * Constructor
	 * @param instance Instance to configure (not null)
	 */
	public DefaultLabelConfigurator(Label instance) {
		super(instance);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentConfigurator#builder()
	 */
	@Override
	protected BaseLabelConfigurator builder() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.LabelConfigurator#content(java.lang.String)
	 */
	@Override
	public BaseLabelConfigurator content(String content) {
		getInstance().setValue(content);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.LabelConfigurator#content(java.lang.String, java.lang.String,
	 * java.lang.Object[])
	 */
	@Override
	public BaseLabelConfigurator content(String defaultContent, String messageCode, Object... arguments) {
		return content(Localizable.builder().message(defaultContent).messageCode(messageCode)
				.messageArguments(arguments).build());
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.LabelConfigurator#content(com.holonplatform.core.i18n.Localizable)
	 */
	@Override
	public BaseLabelConfigurator content(Localizable content) {
		getInstance().setValue(localizeMessage(content));
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.LabelConfigurator#contentMode(com.vaadin.shared.ui.ContentMode)
	 */
	@Override
	public BaseLabelConfigurator contentMode(ContentMode contentMode) {
		getInstance().setContentMode(contentMode);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.LabelConfigurator#html()
	 */
	@Override
	public BaseLabelConfigurator html() {
		getInstance().setContentMode(ContentMode.HTML);
		return this;
	}

}
