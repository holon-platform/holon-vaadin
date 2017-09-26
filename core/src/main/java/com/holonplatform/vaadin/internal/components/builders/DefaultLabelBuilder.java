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
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.vaadin.components.builders.LabelBuilder;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Label;

/**
 * Default {@link LabelBuilder} implentation
 * 
 * @since 5.0.0
 */
public class DefaultLabelBuilder extends AbstractComponentBuilder<Label, Label, LabelBuilder> implements LabelBuilder {

	protected Localizable content;

	public DefaultLabelBuilder() {
		super(new Label());
		sizeUndefined();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentBuilder#builder()
	 */
	@Override
	protected DefaultLabelBuilder builder() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentBuilder#build(com.vaadin.ui.
	 * AbstractComponent)
	 */
	@Override
	protected Label build(Label instance) {
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentBuilder#localize(com.vaadin.ui.
	 * AbstractComponent)
	 */
	@Override
	protected void localize(Label instance) {
		super.localize(instance);
		if (content != null) {
			instance.setValue(LocalizationContext.translate(content, true));
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.LabelBuilder#content(java.lang.String)
	 */
	@Override
	public LabelBuilder content(String content) {
		getInstance().setValue(content);
		this.content = null;
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.LabelBuilder#content(java.lang.String, java.lang.String,
	 * java.lang.Object[])
	 */
	@Override
	public LabelBuilder content(String defaultContent, String messageCode, Object... arguments) {
		this.content = Localizable.builder().message(defaultContent).messageCode(messageCode)
				.messageArguments(arguments).build();
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.LabelBuilder#content(com.holonplatform.core.i18n.Localizable)
	 */
	@Override
	public LabelBuilder content(Localizable content) {
		this.content = content;
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.LabelBuilder#contentMode(com.vaadin.shared.ui.label.ContentMode)
	 */
	@Override
	public LabelBuilder contentMode(ContentMode contentMode) {
		getInstance().setContentMode(contentMode);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.LabelBuilder#html()
	 */
	@Override
	public LabelBuilder html() {
		getInstance().setContentMode(ContentMode.HTML);
		return builder();
	}

}
