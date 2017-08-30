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
package com.holonplatform.vaadin.navigator.internal;

import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;

/**
 * {@link AbstractViewDisplay} using a {@link ComponentContainer}.
 * <p>
 * All components of the container are removed before adding the new view to it.
 * </p>
 * 
 * @since 5.0.0
 */
public class ContainerViewDisplay extends AbstractViewDisplay {

	private static final long serialVersionUID = -4271275542028652280L;

	protected final ComponentContainer container;

	/**
	 * Constructor
	 * @param container ComponentContainer into which display the view contents
	 */
	public ContainerViewDisplay(ComponentContainer container) {
		super();
		this.container = container;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.ui.navigator.AbstractViewDisplay#showViewContent(com.vaadin.ui.Component)
	 */
	@Override
	protected void showViewContent(Component content) {
		container.removeAllComponents();
		if (content != null) {
			container.addComponent(content);
		}
	}

}
