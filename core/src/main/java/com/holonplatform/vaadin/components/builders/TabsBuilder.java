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

import com.holonplatform.core.i18n.Localizable;
import com.vaadin.server.Resource;
import com.vaadin.ui.Component;
import com.vaadin.ui.Component.Focusable;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.CloseHandler;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;

/**
 * Builder to create tabbed component instances.
 * 
 * @param <C> Actual tabs component type
 *
 * @since 5.0.5
 */
public interface TabsBuilder<C extends TabSheet> extends ComponentBuilder<C, TabsBuilder<C>> {

	/**
	 * Configure a new Tab to be added to the tabs component.
	 * <p>
	 * Use {@link TabBuilder#add()} to add the new tab.
	 * </p>
	 * @param component the component to be added onto tab (not null)
	 * @return Tab builder
	 */
	TabBuilder<C> withTab(Component component);

	/**
	 * Convenience method to directly add a tab.
	 * @param component the component to be added onto tab (not null)
	 * @param caption Tab caption (not null)
	 * @return this
	 */
	default TabsBuilder<C> withTab(Component component, Localizable caption) {
		return withTab(component).caption(caption).add();
	}

	/**
	 * Convenience method to directly add a tab.
	 * @param component the component to be added onto tab (not null)
	 * @param caption Tab caption
	 * @return this
	 */
	default TabsBuilder<C> withTab(Component component, String caption) {
		return withTab(component).caption(caption).add();
	}

	/**
	 * Convenience method to directly add a tab.
	 * @param component the component to be added onto tab (not null)
	 * @param caption Tab caption (not null)
	 * @param icon Tab icon
	 * @return this
	 */
	default TabsBuilder<C> withTab(Component component, Localizable caption, Resource icon) {
		return withTab(component).caption(caption).icon(icon).add();
	}

	/**
	 * Convenience method to directly add a tab.
	 * @param component the component to be added onto tab (not null)
	 * @param caption Tab caption (not null)
	 * @param icon Tab icon
	 * @return this
	 */
	default TabsBuilder<C> withTab(Component component, String caption, Resource icon) {
		return withTab(component).caption(caption).icon(icon).add();
	}

	/**
	 * Add a {@link SelectedTabChangeListener}.
	 * @param listener Listener to add (not null)
	 * @return this
	 */
	TabsBuilder<C> withSelectedTabChangeListener(SelectedTabChangeListener listener);

	/**
	 * Set a custom tab close handler to be invoked when a user clicks on a tabs close button.
	 * @param closeHandler the close handler (not null)
	 * @return this
	 */
	TabsBuilder<C> closeHandler(CloseHandler closeHandler);

	/**
	 * Tab builder.
	 *
	 * @param <C> Actual tabs component type
	 */
	public interface TabBuilder<C extends TabSheet> {

		/**
		 * Set the tab caption using a {@link Localizable}.
		 * @param caption Tab caption (not null)
		 * @return this
		 */
		TabBuilder<C> caption(Localizable caption);

		/**
		 * Set the tab caption.
		 * @param caption Tab caption
		 * @return this
		 */
		default TabBuilder<C> caption(String caption) {
			return caption(Localizable.builder().message(caption).build());
		}

		/**
		 * Set the tab caption.
		 * @param defaultCaption Default caption
		 * @param messageCode Caption message code
		 * @param arguments Optional message arguments
		 * @return this
		 */
		default TabBuilder<C> caption(String defaultCaption, String messageCode, Object... arguments) {
			return caption(Localizable.builder().message(defaultCaption).messageCode(messageCode)
					.messageArguments(arguments).build());
		}

		/**
		 * Set the tab description using a {@link Localizable}.
		 * @param description Tab description (not null)
		 * @return this
		 */
		TabBuilder<C> description(Localizable description);

		/**
		 * Set the tab description.
		 * @param description Tab description
		 * @return this
		 */
		default TabBuilder<C> description(String description) {
			return description(Localizable.builder().message(description).build());
		}

		/**
		 * Set the tab description.
		 * @param defaultDescription Default description
		 * @param messageCode Description message code
		 * @param arguments Optional message arguments
		 * @return this
		 */
		default TabBuilder<C> description(String defaultDescription, String messageCode, Object... arguments) {
			return description(Localizable.builder().message(defaultDescription).messageCode(messageCode)
					.messageArguments(arguments).build());
		}

		/**
		 * Set the tab icon.
		 * @param icon Tab icon
		 * @return this
		 */
		TabBuilder<C> icon(Resource icon);

		/**
		 * Set tab visibility. Tabs are visible by default.
		 * @param visible Whether the tab is visible
		 * @return this
		 */
		TabBuilder<C> visible(boolean visible);

		/**
		 * Set whether the tab is enabled. Tabs are enabled by default.
		 * @param enabled Whether the tab is enabled
		 * @return this
		 */
		TabBuilder<C> enabled(boolean enabled);

		/**
		 * Sets the closability status for the tab. A closable tab can be closed by the user through the user interface.
		 * This also controls if a close button is shown to the user or not.
		 * @param closable <code>true</code> to set the tab as closable
		 * @return this
		 */
		TabBuilder<C> closable(boolean closable);

		/**
		 * Set the tab CSS style name.
		 * @param styleName The style name to set
		 * @return this
		 */
		TabBuilder<C> styleName(String styleName);

		/**
		 * Set the component that should automatically focused when the tab is selected.
		 * @param component the component to focus
		 * @return this
		 */
		TabBuilder<C> defaultFocusComponent(Focusable component);

		/**
		 * Add the new tab to the tabs component.
		 * @return Tabs component builder
		 */
		TabsBuilder<C> add();

	}

}
