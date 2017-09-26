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

import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.MouseEvents.ClickListener;
import com.vaadin.ui.Panel;

/**
 * Builder to create {@link Panel} instances.
 *
 * @since 5.0.0
 */
public interface PanelBuilder extends SingleComponentContainerBuilder<Panel, PanelBuilder> {

	/**
	 * Adds <code>borderless</code> and <code>light</code> style names to panel to render the panel without using
	 * borders or any other style. In order to this method to work, current theme must correctly interpret these style
	 * names for panel rendering.
	 * <p>
	 * Vaadin standard Reindeer and Valo themes are able to interpret above style name in the right way.
	 * </p>
	 * @return this
	 */
	PanelBuilder borderless();

	/**
	 * Sets the <i>tabulator index</i> of the component. The tab index property is used to specify the order in which
	 * the fields are focused when the user presses the Tab key. Components with a defined tab index are focused
	 * sequentially first, and then the components with no tab index.
	 * <p>
	 * If the tab index is not set (is set to zero), the default tab order is used. The order is somewhat
	 * browser-dependent, but generally follows the HTML structure of the page.
	 * </p>
	 * <p>
	 * A negative value means that the component is completely removed from the tabulation order and can not be reached
	 * by pressing the Tab key at all.
	 * </p>
	 * @param tabIndex The tab order of this component. Indexes usually start from 1. Zero means that default tab order
	 *        should be used. A negative value means that the field should not be included in the tabbing sequence.
	 * @return this
	 */
	PanelBuilder tabIndex(int tabIndex);

	/**
	 * Add an {@link Action} to the panel.
	 * @param <T> Action type
	 * @param action Action to add
	 * @return this
	 */
	<T extends Action & com.vaadin.event.Action.Listener> PanelBuilder withAction(T action);

	/**
	 * Adds an Action {@link Handler} to the panel
	 * @param actionHandler Action handler to add
	 * @return this
	 */
	PanelBuilder withActionHandler(Handler actionHandler);

	/**
	 * Add a click listener to the Panel. The listener is called whenever the user clicks inside the Panel. Also when
	 * the click targets a component inside the Panel, provided the targeted component does not prevent the click event
	 * from propagating.
	 * @param listener Listener to add
	 * @return this
	 */
	PanelBuilder withClickListener(ClickListener listener);

}
