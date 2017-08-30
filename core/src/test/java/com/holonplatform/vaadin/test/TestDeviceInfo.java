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
package com.holonplatform.vaadin.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Locale;
import java.util.Optional;

import org.junit.Test;

import com.holonplatform.vaadin.device.DeviceInfo;
import com.holonplatform.vaadin.internal.test.AbstractVaadinTest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServletService;
import com.vaadin.server.VaadinSession;
import com.vaadin.server.WrappedSession;
import com.vaadin.util.CurrentInstance;

public class TestDeviceInfo extends AbstractVaadinTest {

	@Test
	public void testFromHeaders() {

		final String ua = "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:49.0) Gecko/20100101 Firefox/49.0";
		final String ac = "*/*";

		DeviceInfo di = DeviceInfo.create(ua, ac);

		assertNotNull(di);

		assertFalse(di.isSmartphone());
		assertFalse(di.isTablet());
		assertFalse(di.isMobile());

	}

	@Test
	public void testFromRequest() {

		final DeviceInfo di = DeviceInfo.create(VaadinService.getCurrentRequest());
		assertNotNull(di);

		VaadinSession session = mock(VaadinSession.class);
		when(session.getState()).thenReturn(VaadinSession.State.OPEN);
		when(session.getSession()).thenReturn(mock(WrappedSession.class));
		when(session.getService()).thenReturn(mock(VaadinServletService.class));
		when(session.getSession().getId()).thenReturn(TEST_SESSION_ID);
		when(session.hasLock()).thenReturn(true);
		when(session.getLocale()).thenReturn(Locale.US);
		when(session.getAttribute(DeviceInfo.SESSION_ATTRIBUTE_NAME)).thenReturn(di);
		CurrentInstance.set(VaadinSession.class, session);

		Optional<DeviceInfo> odi = DeviceInfo.get();

		assertTrue(odi.isPresent());

	}

}
