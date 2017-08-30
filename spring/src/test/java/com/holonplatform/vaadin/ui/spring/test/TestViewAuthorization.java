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
package com.holonplatform.vaadin.ui.spring.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Optional;

import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import com.holonplatform.auth.Account;
import com.holonplatform.auth.AuthContext;
import com.holonplatform.auth.AuthenticationToken;
import com.holonplatform.auth.Credentials;
import com.holonplatform.auth.Realm;
import com.holonplatform.auth.Account.AccountProvider;
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.core.internal.utils.TestUtils;
import com.holonplatform.spring.EnableBeanContext;
import com.holonplatform.vaadin.navigator.ViewNavigator;
import com.holonplatform.vaadin.navigator.ViewNavigator.ViewNavigationException;
import com.holonplatform.vaadin.spring.config.EnableViewAuthorization;
import com.holonplatform.vaadin.spring.config.EnableViewNavigator;
import com.holonplatform.vaadin.spring.utils.AbstractVaadinSpringTest;
import com.holonplatform.vaadin.ui.spring.test.components.SpringTestUI;
import com.holonplatform.vaadin.ui.spring.test.components.ViewOne;
import com.vaadin.navigator.View;

@ContextConfiguration
@DirtiesContext
public class TestViewAuthorization extends AbstractVaadinSpringTest {

	@Configuration
	@EnableBeanContext
	@EnableViewAuthorization
	@EnableViewNavigator
	@ComponentScan(basePackageClasses = ViewOne.class)
	static class Config extends AbstractVaadinSpringTest.Config {

		@Bean
		public AuthContext authContext() {
			AccountProvider ap = id -> {
				Account act = null;
				if ("a1".equals(id)) {
					act = Account.builder(id).credentials(Credentials.builder().secret("a1").build()).permission("r1")
							.build();
				} else if ("a2".equals(id)) {
					act = Account.builder(id).credentials(Credentials.builder().secret("a2").build()).permission("r2")
							.build();
				}
				return Optional.ofNullable(act);
			};

			return AuthContext
					.create(Realm.builder().authenticator(Account.authenticator(ap)).withDefaultAuthorizer().build());
		}

		@Bean
		public LocalizationContext localizationContext() {
			return LocalizationContext.builder().build();
		}

	}

	private ViewNavigator navigator;

	@Override
	public void setup() throws Exception {
		super.setup();
		navigator = applicationContext.getBean(ViewNavigator.class);
	}

	@Test
	public void testAuthorization() {

		createUi(SpringTestUI.class, "http://localhost");

		assertNotNull(navigator);

		navigator.navigateTo(TestNavigator.VIEW_ONE, null);

		assertEquals(TestNavigator.VIEW_ONE, navigator.getCurrentViewName());

		View current = navigator.getCurrentView();
		assertNotNull(current);

		TestUtils.expectedException(ViewNavigationException.class,
				() -> navigator.navigateTo(TestNavigator.VIEW_TWO, null));

		AuthContext authContext = AuthContext.getCurrent().orElse(null);
		assertNotNull(authContext);

		authContext.authenticate(AuthenticationToken.accountCredentials("a1", "a1"));

		navigator.navigateTo(TestNavigator.VIEW_TWO, null);
		current = navigator.getCurrentView();
		assertNotNull(current);

		TestUtils.expectedException(ViewNavigationException.class,
				() -> navigator.navigateTo(TestNavigator.VIEW_THREE, null));

		navigator.navigateTo(TestNavigator.VIEW_FOUR, null);
		current = navigator.getCurrentView();
		assertNotNull(current);

		TestUtils.expectedException(ViewNavigationException.class,
				() -> navigator.navigateTo(TestNavigator.VIEW_SIX, null));

		authContext.unauthenticate();
	}

}
