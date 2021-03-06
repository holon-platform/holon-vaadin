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
package com.holonplatform.vaadin.ui.spring.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import com.holonplatform.auth.Account;
import com.holonplatform.auth.Account.AccountProvider;
import com.holonplatform.auth.AuthContext;
import com.holonplatform.auth.AuthenticationToken;
import com.holonplatform.auth.Credentials;
import com.holonplatform.auth.Realm;
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.spring.EnableBeanContext;
import com.holonplatform.vaadin.navigator.ViewNavigator;
import com.holonplatform.vaadin.spring.config.EnableViewAuthorization;
import com.holonplatform.vaadin.spring.config.EnableViewNavigator;
import com.holonplatform.vaadin.spring.utils.AbstractVaadinSpringTest;
import com.holonplatform.vaadin.ui.spring.test.components.SpringTestUI;
import com.holonplatform.vaadin.ui.spring.test.components.ViewDisplayPanel;
import com.holonplatform.vaadin.ui.spring.test.components.ViewOne;
import com.holonplatform.vaadin.ui.spring.test.errorview.ViewError;
import com.vaadin.navigator.View;
import com.vaadin.ui.Panel;

@ContextConfiguration
@DirtiesContext
public class TestViewAuthorizationWithError extends AbstractVaadinSpringTest {

	@Configuration
	@EnableBeanContext
	@EnableViewAuthorization
	@EnableViewNavigator
	@ComponentScan(basePackageClasses = { ViewOne.class, ViewError.class })
	static class Config extends AbstractVaadinSpringTest.Config {

		@Bean
		public AuthContext authContext() {
			AccountProvider ap = id -> {
				Account act = null;
				if ("a1".equals(id)) {
					act = Account.builder(id).credentials(Credentials.builder().secret("a1").build())
							.withPermission("r1").build();
				} else if ("a2".equals(id)) {
					act = Account.builder(id).credentials(Credentials.builder().secret("a2").build())
							.withPermission("r2").build();
				}
				return Optional.ofNullable(act);
			};

			return AuthContext.create(
					Realm.builder().withAuthenticator(Account.authenticator(ap)).withDefaultAuthorizer().build());
		}

		@Bean
		public LocalizationContext localizationContext() {
			return LocalizationContext.builder().build();
		}

	}

	private ViewNavigator navigator;
	private Panel viewer;

	@BeforeAll
	public static void setupLogger() {
		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();
	}

	@BeforeEach
	@Override
	public void setup() throws Exception {
		super.setup();
		navigator = applicationContext.getBean(ViewNavigator.class);
		viewer = applicationContext.getBean(ViewDisplayPanel.class);
	}

	@Test
	public void testAuthorization() {

		createUi(SpringTestUI.class, "http://localhost");

		assertNotNull(navigator);

		navigator.navigateTo(TestNavigator.VIEW_ONE, null);

		assertEquals(TestNavigator.VIEW_ONE, navigator.getCurrentViewName());

		View current = navigator.getCurrentView();
		assertNotNull(current);

		navigator.navigateTo(TestNavigator.VIEW_TWO, null);

		assertEquals(ViewError.class, viewer.getContent().getClass());
		assertEquals("error", ((ViewError) viewer.getContent()).getValue());

		AuthContext authContext = AuthContext.getCurrent().orElse(null);
		assertNotNull(authContext);

		authContext.authenticate(AuthenticationToken.accountCredentials("a2", "a2"));

		navigator.navigateTo(TestNavigator.VIEW_THREE, null);
		current = navigator.getCurrentView();
		assertNotNull(current);

		navigator.navigateTo(TestNavigator.VIEW_TWO, null);
		assertEquals(ViewError.class, viewer.getContent().getClass());

		navigator.navigateTo(TestNavigator.VIEW_FOUR, null);
		current = navigator.getCurrentView();
		assertNotNull(current);

		navigator.navigateTo(TestNavigator.VIEW_SIX, null);
		assertEquals(ViewError.class, viewer.getContent().getClass());

		authContext.unauthenticate();
	}

}
