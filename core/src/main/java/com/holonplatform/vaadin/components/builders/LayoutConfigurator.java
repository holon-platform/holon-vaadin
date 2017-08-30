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

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.Layout;

/**
 * Interface to configure a {@link Layout}.
 * 
 * @param <B> Concrete configurator type
 *
 * @since 5.0.0
 */
public interface LayoutConfigurator<B extends LayoutConfigurator<B>> extends ClickableLayoutConfigurator<B> {

	/**
	 * Enable spacing between child components within the layout.
	 * @return this
	 */
	B spacing();

	/**
	 * Enable layout margins. Affects all four sides of the layout.
	 * @return this
	 */
	B margin();

	/**
	 * Enable layout top margin.
	 * @return this
	 */
	B marginTop();

	/**
	 * Enable layout bottom margin.
	 * @return this
	 */
	B marginBottom();

	/**
	 * Enable layout left margin.
	 * @return this
	 */
	B marginLeft();

	/**
	 * Enable layout right margin.
	 * @return this
	 */
	B marginRight();

	/**
	 * Sets the default alignment used for components added to this layout.
	 * @param alignment the Alignment to use
	 * @return this
	 */
	B defaultAlignment(Alignment alignment);

	/**
	 * Set alignment for one contained component in this layout.
	 * @param component the component to align within it's layout cell
	 * @param alignment the Alignment to use
	 * @return this
	 */
	B align(Component component, Alignment alignment);

	/**
	 * Adds the given component to layout with given <code>alignment</code>
	 * @param component The component to add
	 * @param alignment the Alignment to use for the component
	 * @return this
	 */
	B addAndAlign(Component component, Alignment alignment);

	/**
	 * Base layout configurator
	 */
	public interface BaseLayoutConfigurator extends LayoutConfigurator<BaseLayoutConfigurator> {

	}

}
