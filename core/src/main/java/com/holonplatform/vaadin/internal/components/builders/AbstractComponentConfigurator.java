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
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.components.builders.ComponentConfigurator;
import com.vaadin.event.ContextClickEvent.ContextClickListener;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ClientConnector.AttachListener;
import com.vaadin.server.ClientConnector.DetachListener;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.Resource;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.AbstractComponent;

/**
 * Base {@link ComponentConfigurator}.
 * 
 * @param <I> Internal component type
 * @param <B> Concrete configurator type
 *
 * @since 5.0.0
 */
public abstract class AbstractComponentConfigurator<I extends AbstractComponent, B extends ComponentConfigurator<B>>
		implements ComponentConfigurator<B> {

	/**
	 * Instance to configure
	 */
	protected final I instance;

	/**
	 * Constructor
	 * @param instance Instance to configure (not null)
	 */
	public AbstractComponentConfigurator(I instance) {
		super();
		ObjectUtils.argumentNotNull(instance, "Instance muts be not null");
		this.instance = instance;
	}

	/**
	 * Actual builder
	 * @return Builder
	 */
	protected abstract B builder();

	/**
	 * @return the instance to build
	 */
	protected I getInstance() {
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ComponentBuilder#width(float, com.vaadin.server.Sizeable.Unit)
	 */
	@Override
	public B width(float width, Unit unit) {
		getInstance().setWidth(width, unit);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ComponentBuilder#width(java.lang.String)
	 */
	@Override
	public B width(String width) {
		getInstance().setWidth(width);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ComponentBuilder#height(float, com.vaadin.server.Sizeable.Unit)
	 */
	@Override
	public B height(float height, Unit unit) {
		getInstance().setHeight(height, unit);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ComponentBuilder#height(java.lang.String)
	 */
	@Override
	public B height(String height) {
		getInstance().setHeight(height);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ComponentBuilder#fullWidth()
	 */
	@Override
	public B fullWidth() {
		getInstance().setWidth(100, Unit.PERCENTAGE);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ComponentBuilder#fullHeigth()
	 */
	@Override
	public B fullHeight() {
		getInstance().setHeight(100, Unit.PERCENTAGE);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ComponentBuilder#fullSize()
	 */
	@Override
	public B fullSize() {
		getInstance().setSizeFull();
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ComponentBuilder#widthUndefined()
	 */
	@Override
	public B widthUndefined() {
		getInstance().setWidthUndefined();
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ComponentBuilder#heightUndefined()
	 */
	@Override
	public B heightUndefined() {
		getInstance().setHeightUndefined();
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ComponentBuilder#sizeUndefined()
	 */
	@Override
	public B sizeUndefined() {
		getInstance().setSizeUndefined();
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ComponentBuilder#styleName(java.lang.String)
	 */
	@Override
	public B styleName(String styleName) {
		getInstance().addStyleName(styleName);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ComponentBuilder#replaceStyleName(java.lang.String)
	 */
	@Override
	public B replaceStyleName(String styleName) {
		getInstance().setStyleName(styleName);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ComponentBuilder#primaryStyleName(java.lang.String)
	 */
	@Override
	public B primaryStyleName(String style) {
		getInstance().setPrimaryStyleName(style);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ComponentBuilder#disabled()
	 */
	@Override
	public B disabled() {
		getInstance().setEnabled(false);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ComponentConfigurator#notVisible()
	 */
	@Override
	public B notVisible() {
		getInstance().setVisible(false);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ComponentBuilder#hidden()
	 */
	@Override
	public B hidden() {
		getInstance().setVisible(false);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ComponentBuilder#captionAsHtml()
	 */
	@Override
	public B captionAsHtml() {
		getInstance().setCaptionAsHtml(true);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.ComponentBuilder#caption(com.holonplatform.core.i18n.Localizable)
	 */
	@Override
	public B caption(Localizable caption) {
		getInstance().setCaption(localizeMessage(caption));
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ComponentBuilder#description(com.holonplatform.core.i18n.
	 * Localizable)
	 */
	@Override
	public B description(Localizable description) {
		getInstance().setDescription(localizeMessage(description));
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ComponentBuilder#icon(com.vaadin.server.Resource)
	 */
	@Override
	public B icon(Resource icon) {
		getInstance().setIcon(icon);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ComponentBuilder#id(java.lang.String)
	 */
	@Override
	public B id(String id) {
		getInstance().setId(id);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ComponentBuilder#errorHandler(com.vaadin.server.ErrorHandler)
	 */
	@Override
	public B errorHandler(ErrorHandler errorHandler) {
		getInstance().setErrorHandler(errorHandler);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ComponentBuilder#withData(java.lang.Object)
	 */
	@Override
	public B withData(Object data) {
		getInstance().setData(data);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ComponentBuilder#responsive()
	 */
	@Override
	public B responsive() {
		getInstance().setResponsive(true);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ComponentBuilder#withAttachListener(com.vaadin.server.
	 * ClientConnector.AttachListener)
	 */
	@Override
	public B withAttachListener(AttachListener listener) {
		getInstance().addAttachListener(listener);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ComponentBuilder#withDetachListener(com.vaadin.server.
	 * ClientConnector.DetachListener)
	 */
	@Override
	public B withDetachListener(DetachListener listener) {
		getInstance().addDetachListener(listener);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ComponentBuilder#withShortcutListener(com.vaadin.event.
	 * ShortcutListener)
	 */
	@Override
	public B withShortcutListener(ShortcutListener shortcut) {
		getInstance().addShortcutListener(shortcut);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ComponentBuilder#withContextClickListener(com.vaadin.event.
	 * ContextClickEvent.ContextClickListener)
	 */
	@Override
	public B withContextClickListener(ContextClickListener listener) {
		getInstance().addContextClickListener(listener);
		return builder();
	}

	/**
	 * Localize given <code>message</code> using current {@link LocalizationContext}, if available.
	 * @param message Message to localize
	 * @return Localized message
	 */
	protected String localizeMessage(Localizable message) {
		return (message != null) ? LocalizationContext.translate(message, true) : null;
	}

}
