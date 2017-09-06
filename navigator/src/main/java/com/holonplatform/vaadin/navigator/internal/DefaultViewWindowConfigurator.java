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

import com.holonplatform.vaadin.internal.components.builders.AbstractComponentConfigurator;
import com.holonplatform.vaadin.navigator.ViewWindowConfigurator;
import com.vaadin.ui.Window;

/**
 * Default {@link ViewWindowConfigurator} implementation.
 * 
 * @since 5.0.0
 */
public class DefaultViewWindowConfigurator extends AbstractComponentConfigurator<Window, ViewWindowConfigurator>
		implements ViewWindowConfigurator {

	private static final long serialVersionUID = -2308438834734648256L;

	public DefaultViewWindowConfigurator(Window window) {
		super(window);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.navigator.ViewWindowConfiguration#closable(boolean)
	 */
	@Override
	public ViewWindowConfigurator closable(boolean closable) {
		getInstance().setClosable(closable);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.navigator.ViewWindowConfiguration#resizable(boolean)
	 */
	@Override
	public ViewWindowConfigurator resizable(boolean resizable) {
		getInstance().setResizable(resizable);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.navigator.ViewWindowConfiguration#draggable(boolean)
	 */
	@Override
	public ViewWindowConfigurator draggable(boolean draggable) {
		getInstance().setDraggable(draggable);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.navigator.ViewWindowConfiguration#position(int, int)
	 */
	@Override
	public ViewWindowConfigurator position(int x, int y) {
		getInstance().setPosition(x, y);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.navigator.ViewWindowConfiguration#withCloseShortcut(int, int[])
	 */
	@Override
	public ViewWindowConfigurator withCloseShortcut(int keyCode, int... modifiers) {
		getInstance().addCloseShortcut(keyCode, modifiers);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentConfigurator#builder()
	 */
	@Override
	protected DefaultViewWindowConfigurator builder() {
		return this;
	}

}
