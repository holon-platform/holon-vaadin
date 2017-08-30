/*
 * Copyright 2000-2017 Holon TDCN.
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
package com.holonplatform.vaadin.internal.components;

import com.holonplatform.vaadin.components.ComposableComponent.Composer;
import com.holonplatform.vaadin.components.PropertyValueComponentSource;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;

/**
 * Default {@link Composer} using a {@link ComponentContainer} as composition layout and adding the componens to the
 * layout in the order they are returned from the a {@link PropertyValueComponentSource}.
 * <p>
 * Provides a construction parameter to automatically set all composed components to full width.
 * </p>
 * 
 * @since 5.0.0
 */
public class DefaultComponentContainerComposer implements Composer<ComponentContainer, PropertyValueComponentSource> {

	private final boolean fullWidthComponents;

	/**
	 * Constructor
	 * @param fullWidthComponents <code>true</code> to set the width for all composed components as <code>100%</code>.
	 */
	public DefaultComponentContainerComposer(boolean fullWidthComponents) {
		super();
		this.fullWidthComponents = fullWidthComponents;
	}

	/**
	 * Gets whether to set the width for all composed components as <code>100%</code>.
	 * @return <code>true</code> if the components width must be setted to <code>100%</code>
	 */
	protected boolean isFullWidthComponents() {
		return fullWidthComponents;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ComposableComponent.Composer#compose(com.vaadin.ui.Component,
	 * java.lang.Object)
	 */
	@Override
	public void compose(ComponentContainer content, PropertyValueComponentSource source) {
		// remove all components
		content.removeAllComponents();
		// add components
		source.getValueComponents().forEach(valueComponent -> {
			final Component component = valueComponent.getComponent();
			if (component == null) {
				throw new RuntimeException(
						"The value component [" + valueComponent + "] returned a null component from getComponent()");
			}
			if (isFullWidthComponents()) {
				component.setWidth("100%");
			}
			content.addComponent(component);
		});
	}

}
