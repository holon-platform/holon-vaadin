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
import com.vaadin.ui.SingleComponentContainer;
import com.vaadin.ui.HasComponents.ComponentAttachListener;
import com.vaadin.ui.HasComponents.ComponentDetachListener;

/**
 * Builder for {@link SingleComponentContainer} components type creation.
 * 
 * @param <C> Component container type
 * @param <B> Concrete builder type
 * 
 * @since 5.0.0
 */
public interface SingleComponentContainerBuilder<C extends SingleComponentContainer, B extends SingleComponentContainerBuilder<C, B>>
		extends ComponentBuilder<C, B> {

	/**
	 * Sets the content of this container. The content should always be set, either as a constructor parameter or by
	 * calling this method.
	 * @param content The component to use as content
	 * @return this
	 */
	B content(Component content);

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
