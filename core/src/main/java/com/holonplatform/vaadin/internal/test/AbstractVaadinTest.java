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
package com.holonplatform.vaadin.internal.test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Locale;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;

import com.vaadin.server.Constants;
import com.vaadin.server.DefaultDeploymentConfiguration;
import com.vaadin.server.DefaultUIProvider;
import com.vaadin.server.UICreateEvent;
import com.vaadin.server.UIProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.server.VaadinServletService;
import com.vaadin.server.VaadinSession;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.UI;
import com.vaadin.util.CurrentInstance;

/**
 * Abstract JUnit test class using Spring to enable a web environment to test Vaadin application elements.
 * 
 * @since 5.0.0
 */
public abstract class AbstractVaadinTest {

	public static final String TEST_SESSION_ID = "TestSessionID";

	public static final int TEST_UIID = 1;

	protected VaadinSession vaadinSession;

	protected UIProvider uiProvider;

	@Before
	public void setup() throws Exception {
		vaadinSession = createVaadinSession(getClientLocale());

		CurrentInstance.set(VaadinSession.class, vaadinSession);

		CurrentInstance.set(VaadinRequest.class, buildVaadinRequest());

		uiProvider = buildUiProvider();
	}

	/**
	 * Ger Locale tu use
	 * @return Client simulated Locale
	 */
	protected Locale getClientLocale() {
		return Locale.US;
	}

	/**
	 * Create a Vaadin UI using given UI class
	 * @param <T> UI type
	 * @param uiClass UI class to create
	 * @return UI instance
	 */
	@SuppressWarnings("unchecked")
	protected <T extends UI> T createUi(Class<T> uiClass) {
		T ui = (T) uiProvider.createInstance(buildUiCreateEvent(uiClass, null));
		CurrentInstance.set(UI.class, ui);
		return ui;
	}

	/**
	 * Create a Vaadin UI using given UI class
	 * @param <T> UI type
	 * @param uiClass UI class to create
	 * @param location Page location
	 * @return UI instance
	 */
	@SuppressWarnings("unchecked")
	protected <T extends UI> T createUi(Class<T> uiClass, String location) {
		UICreateEvent evt = buildUiCreateEvent(uiClass, location);
		T ui = (T) uiProvider.createInstance(evt);
		CurrentInstance.set(UI.class, ui);
		ui.doInit(evt.getRequest(), TEST_UIID, null);
		return ui;
	}

	/**
	 * Build the UICreateEvent to pass to UIProvider
	 * @param uiClass UI class
	 * @param location Optional Page location
	 * @return UICreateEvent
	 */
	protected UICreateEvent buildUiCreateEvent(Class<? extends UI> uiClass, String location) {
		VaadinServletRequest request = (location != null) ? buildVaadinRequest(location) : buildVaadinRequest();

		CurrentInstance.set(VaadinRequest.class, request);

		CurrentInstance.set(VaadinSession.class, vaadinSession);

		return new UICreateEvent(request, uiClass, TEST_UIID);
	}

	/**
	 * Build VaadinServletRequest
	 * @return VaadinServletRequest
	 */
	protected VaadinServletRequest buildVaadinRequest() {
		return new VaadinServletRequest(buildHttpServletRequest(), (VaadinServletService) vaadinSession.getService());
	}

	/**
	 * Build VaadinServletRequest using a location
	 * @param location Page location
	 * @return VaadinServletRequest
	 */
	@SuppressWarnings("serial")
	protected VaadinServletRequest buildVaadinRequest(final String location) {
		return new VaadinServletRequest(buildHttpServletRequest(), (VaadinServletService) vaadinSession.getService()) {

			/*
			 * (non-Javadoc)
			 * @see javax.servlet.ServletRequestWrapper#getParameter(java.lang.String)
			 */
			@Override
			public String getParameter(String name) {
				if ("v-loc".equals(name)) {
					return location;
				}
				return super.getParameter(name);
			}

		};
	}

	/**
	 * UIProvider to use
	 * @return UIProvider instance
	 */
	protected UIProvider buildUiProvider() {
		return new DefaultUIProvider();
	}

	/**
	 * Create a VaadinSession
	 * @param locale Client locale
	 * @return VaadinSession instance
	 * @throws Exception Failed to create session
	 */
	protected VaadinSession createVaadinSession(Locale locale) throws Exception {
		WrappedSession wrappedSession = mock(WrappedSession.class);
		VaadinServletService vaadinService = mock(VaadinServletService.class);
		when(vaadinService.getDeploymentConfiguration())
				.thenReturn(new DefaultDeploymentConfiguration(VaadinServletService.class, getDeploymentProperties()));

		VaadinSession session = mock(VaadinSession.class);
		when(session.getState()).thenReturn(VaadinSession.State.OPEN);
		when(session.getSession()).thenReturn(wrappedSession);
		when(session.getService()).thenReturn(vaadinService);
		when(session.getSession().getId()).thenReturn(TEST_SESSION_ID);
		when(session.hasLock()).thenReturn(true);
		when(session.getLocale()).thenReturn(locale != null ? locale : Locale.US);
		return session;
	}

	protected HttpServletRequest buildHttpServletRequest() {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getProtocol()).thenReturn("http");
		when(request.getScheme()).thenReturn("http");
		when(request.getRemoteAddr()).thenReturn("127.0.0.1");
		when(request.getRemoteHost()).thenReturn("localhost");
		when(request.getRemotePort()).thenReturn(80);
		when(request.getRequestURL()).thenReturn(new StringBuffer("http").append("://").append("localhost"));
		when(request.getRequestURI()).thenReturn("/");
		when(request.getLocalName()).thenReturn("localhost");
		when(request.getLocalPort()).thenReturn(80);
		when(request.isSecure()).thenReturn(false);
		when(request.getAttributeNames()).thenReturn(Collections.emptyEnumeration());
		when(request.getCharacterEncoding()).thenReturn("utf-8");
		when(request.getHeaderNames()).thenReturn(Collections.emptyEnumeration());
		return request;
	}

	/**
	 * Get Properties to provide to Vaadin DeploymentConfiguration
	 * @return DeploymentConfiguration properties
	 */
	protected Properties getDeploymentProperties() {
		Properties properties = new Properties();
		properties.put(Constants.SERVLET_PARAMETER_PRODUCTION_MODE, !isVaadinDebugMode());
		return properties;
	}

	/**
	 * Whether to use Vaadin debug mode
	 * @return <code>true</code> to use Vaadin debug mode
	 */
	protected boolean isVaadinDebugMode() {
		return false;
	}

}
