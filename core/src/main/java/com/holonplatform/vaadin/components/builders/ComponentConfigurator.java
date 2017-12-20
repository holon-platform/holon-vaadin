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
import com.holonplatform.vaadin.internal.components.builders.DefaultComponentConfigurator;
import com.vaadin.event.ContextClickEvent.ContextClickListener;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ClientConnector.AttachListener;
import com.vaadin.server.ClientConnector.DetachListener;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.Resource;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;

/**
 * Interface to configure a {@link Component}.
 * 
 * @param <B> Concrete configurator type
 * 
 * @since 5.0.0
 */
public interface ComponentConfigurator<B extends ComponentConfigurator<B>> {

	/**
	 * Sets the width of the object. Negative number implies unspecified size.
	 * @param width The width of the component
	 * @param unit The unit used for the width
	 * @return this
	 */
	B width(float width, Unit unit);

	/**
	 * Sets the width of the component using String presentation.
	 * <p>
	 * String presentation is similar to what is used in Cascading Style Sheets. Size can be length or percentage of
	 * available size.
	 * </p>
	 * <p>
	 * The empty string ("") or null will unset the width and set the units to pixels.
	 * </p>
	 * @param width Width in CSS style string representation.
	 * @return this
	 */
	B width(String width);

	/**
	 * Sets the height of the object. Negative number implies unspecified size.
	 * @param height The height of the component
	 * @param unit The unit used for the height
	 * @return this
	 */
	B height(float height, Unit unit);

	/**
	 * Sets the height of the component using String presentation.
	 * <p>
	 * String presentation is similar to what is used in Cascading Style Sheets. Size can be length or percentage of
	 * available size.
	 * </p>
	 * <p>
	 * The empty string ("") or null will unset the height and set the units to pixels.
	 * </p>
	 * @param height Height in CSS style string representation.
	 * @return this
	 */
	B height(String height);

	/**
	 * Set the component width to 100%
	 * @return this
	 */
	B fullWidth();

	/**
	 * Set the component heigth to 100%
	 * @return this
	 */
	B fullHeight();

	/**
	 * Set the component width and heigth to 100%
	 * @return this
	 */
	B fullSize();

	/**
	 * Clears any defined width
	 * @return this
	 */
	B widthUndefined();

	/**
	 * Clears any defined height
	 * @return this
	 */
	B heightUndefined();

	/**
	 * Clears any size settings
	 * @return this
	 */
	B sizeUndefined();

	/**
	 * Adds one or more style names to this component. Multiple styles can be specified as a space-separated list of
	 * style names. The style name will be rendered as a HTML class name, which can be used in a CSS definition.
	 * <p>
	 * Each style name will occur in two versions: one as specified and one that is prefixed with the style name of the
	 * component.
	 * </p>
	 * @param styleName The new style to be added to the component
	 * @return this
	 */
	B styleName(String styleName);

	/**
	 * Sets one or more user-defined style names of the component, replacing any previous user-defined styles. Multiple
	 * styles can be specified as a space-separated list of style names. The style names must be valid CSS class names
	 * and should not conflict with any built-in style names in Vaadin or GWT.
	 * <p>
	 * Each style name will occur in two versions: one as specified and one that is prefixed with the style name of the
	 * component.
	 * </p>
	 * @param styleName The new style or styles of the component as a space-separated list
	 * @return this
	 */
	B replaceStyleName(String styleName);

	/**
	 * Changes the primary style name of the component.
	 * <p>
	 * The primary style name identifies the component when applying the CSS theme to the Component. By changing the
	 * style name all CSS rules targeted for that style name will no longer apply, and might result in the component not
	 * working as intended.
	 * </p>
	 * @param style The new primary style name
	 * @return this
	 */
	B primaryStyleName(String style);

	/**
	 * Disables the component. The user can not interact with disabled components, which are shown with a style that
	 * indicates the status, usually shaded in light gray color.
	 * @return this
	 */
	B disabled();

	/**
	 * Sets the components as not visible.
	 * @return this
	 */
	B notVisible();

	/**
	 * Set the component as not visible.
	 * <p>
	 * Invisible components are not drawn in the user interface. The effect is not merely a cosmetic CSS change - no
	 * information about an invisible component will be sent to the client. The effect is thus the same as removing the
	 * component from its parent.
	 * </p>
	 * @return this
	 */
	B hidden();

	/**
	 * Sets the caption rendered as HTML.
	 * <p>
	 * When the captions are rendered in the browser as HTML, the developer is responsible for ensuring no harmful HTML
	 * is used.
	 * <p>
	 * @return this
	 */
	B captionAsHtml();

	/**
	 * Sets the caption of the component.
	 * <p>
	 * A <i>caption</i> is an explanatory textual label accompanying a user interface component, usually shown above,
	 * left of, or inside the component.
	 * </p>
	 * @param caption The new caption for the component. If the caption is <code>null</code>, no caption is shown
	 * @return this
	 */
	default B caption(String caption) {
		return caption(Localizable.builder().message(caption).build());
	}

	/**
	 * Sets the caption of the component using a localizable <code>messageCode</code>.
	 * <p>
	 * For caption localization, a {@link LocalizationContext} must be available and localized as {@link Context}
	 * resource when component is built.
	 * </p>
	 * @param defaultCaption Default caption if no translation is available for given <code>messageCode</code> for
	 *        current Locale, or no {@link LocalizationContext} is available at all
	 * @param messageCode Caption translation message key
	 * @param arguments Optional translation arguments
	 * @return this
	 */
	default B caption(String defaultCaption, String messageCode, Object... arguments) {
		return caption(Localizable.builder().message(defaultCaption).messageCode(messageCode)
				.messageArguments(arguments).build());
	}

	/**
	 * Sets the caption of the component using a {@link Localizable} message.
	 * <p>
	 * For caption localization, a {@link LocalizationContext} must be available and localized as {@link Context}
	 * resource when component is built.
	 * </p>
	 * @param caption Caption {@link Localizable} message
	 * @return this
	 */
	B caption(Localizable caption);

	/**
	 * Sets the component's description.
	 * <p>
	 * The description is displayed as HTML in tooltips or directly in certain components so care should be taken to
	 * avoid creating the possibility for HTML injection and possibly XSS vulnerabilities.
	 * </p>
	 * @param description The description to set
	 * @return this
	 */
	default B description(String description) {
		return description(Localizable.builder().message(description).build());
	}

	/**
	 * Sets the component's description using a localizable <code>messageCode</code>.
	 * <p>
	 * For description localization, a {@link LocalizationContext} must be available and localized as {@link Context}
	 * resource when component is built.
	 * </p>
	 * @param defaultDescription Default description if no translation is available for given <code>messageCode</code>
	 *        for current Locale, or no {@link LocalizationContext} is available at all
	 * @param messageCode Description translation message key
	 * @param arguments Optional translation arguments
	 * @return this
	 */
	default B description(String defaultDescription, String messageCode, Object... arguments) {
		return description(Localizable.builder().message(defaultDescription).messageCode(messageCode)
				.messageArguments(arguments).build());
	}

	/**
	 * Sets the component's description using a {@link Localizable} message.
	 * <p>
	 * For description localization, a {@link LocalizationContext} must be available and localized as {@link Context}
	 * resource when component is built.
	 * </p>
	 * @param description Description {@link Localizable} message
	 * @return this
	 */
	B description(Localizable description);

	/**
	 * Sets the icon of the component.
	 * <p>
	 * An icon is an explanatory graphical label accompanying a user interface component, usually shown above, left of,
	 * or inside the component. Icon is closely related to caption and is usually displayed horizontally before or after
	 * it, depending on the component and the containing layout.
	 * </p>
	 * @param icon The icon of the component. If <code>null</code>, no icon is shown.
	 * @return this
	 */
	B icon(Resource icon);

	/**
	 * Adds an unique id for component that is used in the client-side for testing purposes. Keeping identifiers unique
	 * is the responsibility of the programmer.
	 * @param id An alphanumeric id
	 * @return this
	 */
	B id(String id);

	/**
	 * Sets the error handler for the component.
	 * <p>
	 * The error handler is dispatched whenever there is an error processing the data coming from the client for this
	 * component.
	 * </p>
	 * @param errorHandler The error handler for this component
	 * @return this
	 */
	B errorHandler(ErrorHandler errorHandler);

	/**
	 * Sets the data object, that can be used for any application specific data. The component does not use or modify
	 * this data.
	 * @param data The Application specific data.
	 * @return this
	 */
	B withData(Object data);

	/**
	 * Enables responsiveness for this component
	 * @return this
	 */
	B responsive();

	/**
	 * Add an {@link AttachListener} to the component, called after the component is attached to the application.
	 * @param listener Listener to add
	 * @return this
	 */
	B withAttachListener(AttachListener listener);

	/**
	 * Add an {@link DetachListener} to the component, called before the component is detached from the application.
	 * @param listener Listener to add
	 * @return this
	 */
	B withDetachListener(DetachListener listener);

	/**
	 * Add a {@link ShortcutListener} to this component
	 * @param shortcut ShortcutListener to add
	 * @return this
	 */
	B withShortcutListener(ShortcutListener shortcut);

	/**
	 * Add a {@link ContextClickListener} to this component, that gets notified when a context click happens.
	 * @param listener ContextClickListener to add
	 * @return this
	 */
	B withContextClickListener(ContextClickListener listener);

	/**
	 * Base component configurator.
	 */
	public interface BaseComponentConfigurator extends ComponentConfigurator<BaseComponentConfigurator> {

		/**
		 * Create a new {@link BaseComponentConfigurator} on given <code>component</code>.
		 * @param component Component to configure (not null)
		 * @return A new {@link BaseComponentConfigurator} to configure given component
		 */
		static BaseComponentConfigurator create(AbstractComponent component) {
			return new DefaultComponentConfigurator(component);
		}

	}

}
