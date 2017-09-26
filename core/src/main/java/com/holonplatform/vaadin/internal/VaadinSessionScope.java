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
package com.holonplatform.vaadin.internal;

import java.util.Optional;

import com.holonplatform.core.ContextScope;
import com.holonplatform.core.exceptions.TypeMismatchException;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.internal.utils.TypeUtils;
import com.vaadin.server.VaadinSession;

/**
 * A {@link ContextScope} bound to current {@link VaadinSession}, looking up context resources using Vaadin Session
 * attributes.
 * 
 * <p>
 * THis scope uses {@link VaadinSession#getCurrent()} to obtain the current Vaadin session, so this scope is active only
 * when invoked within a Vaadin server request thread.
 * </p>
 * 
 * @since 5.0.0
 */
public class VaadinSessionScope implements ContextScope {

	/**
	 * Context name
	 */
	public final static String NAME = "vaadin-session";

	/**
	 * Context order
	 */
	public final static int ORDER = Integer.MIN_VALUE + 1500;

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.context.ContextScope#getName()
	 */
	@Override
	public String getName() {
		return NAME;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.context.ContextScope#getOrder()
	 */
	@Override
	public int getOrder() {
		return ORDER;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.context.ContextScope#get(java.lang.String, java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> Optional<T> get(String resourceKey, Class<T> resourceType) throws TypeMismatchException {
		ObjectUtils.argumentNotNull(resourceKey, "Resource key must be not null");
		ObjectUtils.argumentNotNull(resourceType, "Resource type must be not null");

		final VaadinSession session = VaadinSession.getCurrent();

		if (session != null) {

			Object value = session.getAttribute(resourceKey);
			if (value != null) {

				// check type
				if (!TypeUtils.isAssignable(value.getClass(), resourceType)) {
					throw new TypeMismatchException("<" + NAME + "> Actual resource type [" + value.getClass().getName()
							+ "] and required resource type [" + resourceType.getName() + "] mismatch");
				}

				return Optional.of((T) value);

			}
		}

		return Optional.empty();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.context.ContextScope#put(java.lang.String, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> Optional<T> put(String resourceKey, T value) throws UnsupportedOperationException {
		ObjectUtils.argumentNotNull(resourceKey, "Resource key must be not null");

		final VaadinSession session = VaadinSession.getCurrent();
		if (session == null) {
			throw new IllegalStateException("Current VaadinSession not available");
		}

		Object exist = session.getAttribute(resourceKey);

		session.setAttribute(resourceKey, value);

		try {
			T previous = (T) exist;
			return Optional.ofNullable(previous);
		} catch (@SuppressWarnings("unused") Exception e) {
			// ignore
			return Optional.empty();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.context.ContextScope#putIfAbsent(java.lang.String, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> Optional<T> putIfAbsent(String resourceKey, T value) throws UnsupportedOperationException {
		ObjectUtils.argumentNotNull(resourceKey, "Resource key must be not null");

		final VaadinSession session = VaadinSession.getCurrent();
		if (session == null) {
			throw new IllegalStateException("Current VaadinSession not available");
		}

		if (value != null) {
			synchronized (session) {
				Object exist = session.getAttribute(resourceKey);
				if (exist == null) {
					session.setAttribute(resourceKey, value);
				} else {
					return Optional.of((T) exist);
				}
			}
		}

		return Optional.empty();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.context.ContextScope#remove(java.lang.String)
	 */
	@Override
	public boolean remove(String resourceKey) throws UnsupportedOperationException {
		ObjectUtils.argumentNotNull(resourceKey, "Resource key must be not null");

		final VaadinSession session = VaadinSession.getCurrent();
		if (session == null) {
			throw new IllegalStateException("Current VaadinSession not available");
		}

		Object exist = session.getAttribute(resourceKey);
		session.setAttribute(resourceKey, null);

		return exist != null;
	}

}
