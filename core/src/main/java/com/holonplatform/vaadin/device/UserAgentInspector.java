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

import java.io.Serializable;

import com.holonplatform.vaadin.internal.device.DefaultUserAgentInspector;
import com.vaadin.server.VaadinRequest;

/**
 * Interface to obtain client device and environment informations using browser provided <code>user-agent</code> and
 * <code>accept</code> request headers.
 * 
 * @since 5.0.0
 */
public interface UserAgentInspector extends Serializable {

	/**
	 * Return the User-Agent header
	 * @return User-agent header in lower case
	 */
	String getUserAgentHeader();

	/**
	 * Return the Accept header
	 * @return Accept header in lower case
	 */
	String getAcceptHeader();

	/**
	 * WebKit check
	 * @return whether the current browser is based on WebKit
	 */
	boolean isWebKit();

	/**
	 * iPhone check
	 * @return whether the device is an iPhone
	 */
	boolean isIPhone();

	/**
	 * iPad check
	 * @return whether the device is an iPad
	 */
	boolean isIPad();

	/**
	 * iPod check
	 * @return whether the device is an iPod touch
	 */
	boolean isIPod();

	/**
	 * iOS check
	 * @return whether the device is an iOS device
	 */
	boolean isIOs();

	/**
	 * Android check
	 * @return whether the device is an Android device
	 */
	boolean isAndroid();

	/**
	 * Android phone check
	 * @return whether the device is an Android phone
	 */
	boolean isAndroidPhone();

	/**
	 * Android tablet check
	 * @return whether the device is an Android tablet
	 */
	boolean isAndroidTablet();

	/**
	 * Windows phone check
	 * @return whether the device is a Windows phone
	 */
	boolean isWindowsPhone();

	/**
	 * Kindle check
	 * @return whether the device is a Kindle
	 */
	boolean isKindle();

	/**
	 * PlayStation check
	 * @return whether the device is a PlayStation
	 */
	boolean isPlaystation();

	/**
	 * Nintendo check
	 * @return whether the device is a Nintendo
	 */
	boolean isNintendo();

	/**
	 * Xbox check
	 * @return whether the device is a Xbox
	 */
	boolean isXbox();

	/**
	 * Game console check
	 * @return whether the device is a game console
	 */
	boolean isGameConsole();

	/**
	 * Smartphone check
	 * @return whether the device is a Smartphone
	 */
	boolean isSmartphone();

	/**
	 * Tablet check
	 * @return whether the device is a Tablet
	 */
	boolean isTablet();

	/**
	 * Mobile device check
	 * @return whether the device is a mobile device
	 */
	boolean isMobile();

	/**
	 * Build a UserAgentInspector instance using given header values
	 * @param userAgentHeader the User-Agent header
	 * @param httpAcceptHeader the Accept header
	 * @return The {@link UserAgentInspector}
	 */
	static UserAgentInspector create(String userAgentHeader, String httpAcceptHeader) {
		return new DefaultUserAgentInspector(userAgentHeader, httpAcceptHeader);
	}

	/**
	 * Build a UserAgentInspector instance using a {@link VaadinRequest}
	 * @param request Vaadin request
	 * @return The {@link UserAgentInspector}
	 */
	static UserAgentInspector create(VaadinRequest request) {
		return new DefaultUserAgentInspector(request);
	}

}
