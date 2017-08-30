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
 * Interface to configure an ordered {@link Layout}.
 * 
 * @param <B> Concrete configurator type
 *
 * @since 5.0.0
 */
public interface OrderedLayoutConfigurator<B extends OrderedLayoutConfigurator<B>> extends LayoutConfigurator<B> {

	/**
	 * This method is used to control how excess space in layout is distributed among components. Excess space may exist
	 * if layout is sized and contained non relatively sized components don't consume all available space.
	 * @param component the component in this layout which expand ratio is to be set
	 * @param expandRatio Expand ratio (greater or equal to 0)
	 * @return this
	 */
	B expand(Component component, float expandRatio);

	/**
	 * Adds the given component to layout with given <code>expandRatio</code>
	 * @param component The component to add
	 * @param expandRatio The expand ration to use with for added component
	 * @return this
	 */
	B addAndExpand(Component component, float expandRatio);

	/**
	 * Adds the given component to layout with applying full (<code>1</code>) expand ratio.
	 * @param component The component to add
	 * @return this
	 */
	default B addAndExpandFull(Component component) {
		return addAndExpand(component, 1f);
	}

	/**
	 * Adds the given component to layout with given <code>expandRatio</code> and <code>alignment</code>
	 * @param component The component to add
	 * @param alignment the Alignment to use for the component
	 * @param expandRatio The expand ration to use with for added component
	 * @return this
	 */
	B addAlignAndExpand(Component component, Alignment alignment, float expandRatio);

	/**
	 * Base ordered layout configurator
	 */
	public interface BaseOrderedLayoutConfigurator extends OrderedLayoutConfigurator<BaseOrderedLayoutConfigurator> {

	}

}
