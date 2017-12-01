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
package com.holonplatform.vaadin.spring.boot.internal;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;

import com.vaadin.server.SessionDestroyListener;
import com.vaadin.server.SessionInitListener;
import com.vaadin.server.VaadinServletService;
import com.vaadin.spring.server.SpringVaadinServlet;

/**
 * {@link SpringVaadinServlet} extension to allow automatic {@link SessionInitListener} and
 * {@link SessionDestroyListener} beans registration at servlet initialization.
 * 
 * @since 5.0.4
 */
public class HolonVaadinServlet extends SpringVaadinServlet {

	private static final long serialVersionUID = -8960783303822199941L;

	/**
	 * {@link SessionInitListener}s to be registered at servlet initialization
	 */
	private final List<SessionInitListener> sessionInitListeners = new LinkedList<>();

	/**
	 * {@link SessionDestroyListener}s to be registered at servlet initialization
	 */
	private final List<SessionDestroyListener> sessionDestroyListeners = new LinkedList<>();

	/**
	 * Add a {@link SessionInitListener} to be registered at servlet initialization.
	 * @param listener The {@link SessionInitListener} to add
	 */
	public void addSessionInitListener(SessionInitListener listener) {
		if (listener != null) {
			sessionInitListeners.add(listener);
		}
	}

	/**
	 * Add a {@link SessionDestroyListener} to be registered at servlet initialization.
	 * @param listener The {@link SessionDestroyListener} to add
	 */
	public void addSessionDestroyListener(SessionDestroyListener listener) {
		if (listener != null) {
			sessionDestroyListeners.add(listener);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.spring.server.SpringVaadinServlet#servletInitialized()
	 */
	@Override
	protected void servletInitialized() throws ServletException {
		super.servletInitialized();

		// register session init/destroy listeners
		final VaadinServletService service = getService();
		sessionInitListeners.forEach(listener -> service.addSessionInitListener(listener));
		sessionDestroyListeners.forEach(listener -> service.addSessionDestroyListener(listener));
	}

}
