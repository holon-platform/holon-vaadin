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
package com.holonplatform.vaadin.device;

import java.util.Optional;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.internal.device.DefaultDeviceInfo;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;

/**
 * Provides informations about client device in which application is running
 * 
 * @see UserAgentInspector
 * 
 * @since 5.0.0
 */
public interface DeviceInfo extends UserAgentInspector {

	/**
	 * Name of the session attribute to cache DeviceInfo instance
	 */
	public static final String SESSION_ATTRIBUTE_NAME = DeviceInfo.class.getName();

	/**
	 * Get client screen width in pixels
	 * @return Client screen width, or <code>-1</code> if not available at method call time
	 */
	int getScreenWidth();

	/**
	 * Get client screen height in pixels
	 * @return Client screen height, or <code>-1</code> if not available at method call time
	 */
	int getScreenHeight();

	/**
	 * Get application viewport width in pixels
	 * @return Application viewport width, or <code>-1</code> if not available at method call time
	 */
	int getViewPortWidth();

	/**
	 * Get application viewport height in pixels
	 * @return Application viewport height, or <code>-1</code> if not available at method call time
	 */
	int getViewPortHeight();

	/**
	 * Build a DeviceInfo instance using given header values
	 * @param userAgentHeader the User-Agent header
	 * @param httpAcceptHeader the Accept header
	 * @return The {@link DeviceInfo}
	 */
	static DeviceInfo create(String userAgentHeader, String httpAcceptHeader) {
		return new DefaultDeviceInfo(UserAgentInspector.create(userAgentHeader, httpAcceptHeader));
	}

	/**
	 * Build a DeviceInfo instance using a {@link VaadinRequest}
	 * @param request Vaadin request (not null)
	 * @return The {@link DeviceInfo}
	 */
	static DeviceInfo create(VaadinRequest request) {
		ObjectUtils.argumentNotNull(request, "VaadinRequest must be not null");
		return new DefaultDeviceInfo(UserAgentInspector.create(request));
	}

	/**
	 * Get the current DeviceInfo instance, if available.
	 * <p>
	 * DeviceInfo is created using current {@link VaadinRequest}, if available. It is cached into {@link VaadinSession}
	 * for further requests.
	 * </p>
	 * @return Optional DeviceInfo, empty if it cannot be found in session and no current request is available to create
	 *         a new instance
	 */
	static Optional<DeviceInfo> get() {
		final VaadinSession session = VaadinSession.getCurrent();
		if (session != null) {
			return ensureInited(session);
		}
		return Optional.empty();
	}

	/**
	 * Get the current DeviceInfo instance, or throw an {@link IllegalStateException} if not available.
	 * @return The current DeviceInfo instance
	 * @see #get()
	 */
	static DeviceInfo require() {
		return get().orElseThrow(() -> new IllegalStateException("DeviceInfo is not available fro current session"));
	}

	/**
	 * Ensure that a {@link DeviceInfo} is available from given Vaadin <code>session</code>. For successful
	 * initialization, a {@link VaadinService#getCurrentRequest()} must be available.
	 * @param session Vaadin session (not null)
	 * @return The session scoped {@link DeviceInfo} instance, if a request is available
	 */
	static Optional<DeviceInfo> ensureInited(VaadinSession session) {
		ObjectUtils.argumentNotNull(session, "VaadinSession must be not null");
		DeviceInfo deviceInfo = (DeviceInfo) session.getAttribute(SESSION_ATTRIBUTE_NAME);
		if (deviceInfo == null) {
			final VaadinRequest request = VaadinService.getCurrentRequest();
			if (request != null) {
				synchronized (session) {
					session.setAttribute(SESSION_ATTRIBUTE_NAME, deviceInfo = create(request));
				}
			}
		}
		return Optional.ofNullable(deviceInfo);
	}

}
