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
package com.holonplatform.vaadin.components.builders;

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.ui.Layout;

/**
 * Interface to configure a {@link Layout} supporting {@link LayoutClickListener}s.
 * 
 * @param <B> Concrete builder type
 *
 * @since 5.0.0
 */
public interface ClickableLayoutConfigurator<B extends ClickableLayoutConfigurator<B>>
		extends ComponentContainerConfigurator<B> {

	/**
	 * Adds a click listener to the layout. The listener is called whenever the user clicks inside the layout. An event
	 * is also triggered when the click targets a component inside a nested layout or Panel, provided the targeted
	 * component does not prevent the click event from propagating. A caption is not considered part of a component. The
	 * child component that was clicked is included in the {@link LayoutClickEvent}.
	 * @param listener The listener to add
	 * @return this
	 */
	B withLayoutClickListener(LayoutClickListener listener);

	/**
	 * Base {@link ClickableLayoutConfigurator}.
	 */
	public interface BaseClickableLayoutConfigurator
			extends ClickableLayoutConfigurator<BaseClickableLayoutConfigurator> {

	}

}
