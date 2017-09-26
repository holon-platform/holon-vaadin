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

import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.HasComponents.ComponentAttachListener;
import com.vaadin.ui.HasComponents.ComponentDetachListener;

/**
 * Interface to configure a {@link ComponentContainer}.
 * 
 * @param <B> Concrete configurator type
 *
 * @since 5.0.0
 */
public interface ComponentContainerConfigurator<B extends ComponentContainerConfigurator<B>>
		extends ComponentConfigurator<B> {

	/**
	 * Adds the components in the given order to the component container.
	 * @param components Components to add
	 * @return this
	 */
	B add(Component... components);

	/**
	 * Add a listener to be notified when a component is attached to container
	 * @param listener Listener to add
	 * @return this
	 */
	B withComponentAttachListener(ComponentAttachListener listener);

	/**
	 * Add a listener to be notified when a component is detached from container
	 * @param listener Listener to add
	 * @return this
	 */
	B withComponentDetachListener(ComponentDetachListener listener);

}
