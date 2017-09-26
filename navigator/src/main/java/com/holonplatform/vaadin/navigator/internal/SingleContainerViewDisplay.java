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
package com.holonplatform.vaadin.navigator.internal;

import com.vaadin.ui.Component;
import com.vaadin.ui.SingleComponentContainer;

/**
 * {@link AbstractViewDisplay} using a {@link SingleComponentContainer}.
 * <p>
 * SingleComponentContainer content is replaced with the active View content.
 * </p>
 * 
 * @since 5.0.0
 */
public class SingleContainerViewDisplay extends AbstractViewDisplay {

	private static final long serialVersionUID = -4771055128239996651L;

	private final SingleComponentContainer container;

	/**
	 * Constructor
	 * @param container SingleComponentContainer which content has to be replaced by the view content
	 */
	public SingleContainerViewDisplay(SingleComponentContainer container) {
		super();
		this.container = container;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.ui.navigator.AbstractViewDisplay#showViewContent(com.vaadin.ui.Component)
	 */
	@Override
	protected void showViewContent(Component content) {
		container.setContent(content);
	}

}
