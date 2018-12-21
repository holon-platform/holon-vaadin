/*
 * Copyright 2000-2017 Holon TDCN.
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
package com.holonplatform.vaadin.examples;

import java.util.Optional;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.holonplatform.auth.Account;
import com.holonplatform.auth.Account.AccountProvider;
import com.holonplatform.auth.AuthContext;
import com.holonplatform.auth.Credentials;
import com.holonplatform.auth.Realm;
import com.holonplatform.spring.EnableBeanContext;
import com.holonplatform.vaadin.components.Components;
import com.holonplatform.vaadin.spring.AccessDeniedView;
import com.holonplatform.vaadin.spring.config.EnableViewAuthorization;
import com.holonplatform.vaadin.spring.config.EnableViewNavigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.spring.annotation.VaadinSessionScope;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public class ExampleSpringAuthz {

	// tag::authz[]
	@Configuration
	@ComponentScan(basePackageClasses = ViewOne.class)
	@EnableViewNavigator
	@EnableBeanContext // <1>
	@EnableViewAuthorization // <2>
	class SpringConfig {

		@Bean // <3>
		@VaadinSessionScope
		public AuthContext authContext() {
			AccountProvider ap = id -> {
				// Only a user with username 'username1' is available
				if ("username1".equals(id)) {
					// setup the user password and assign the role 'role1'
					return Optional.of(Account.builder(id).credentials(Credentials.builder().secret("s3cr3t").build())
							.withPermission("role1").build());
				}
				return Optional.empty();
			};
			return AuthContext.create(Realm.builder()
					// authenticator using the AccountProvider
					.withAuthenticator(Account.authenticator(ap))
					// default authorizer
					.withDefaultAuthorizer().build());
		}

	}

	@SpringView(name = "view1")
	@PermitAll // <4>
	class ViewOne extends VerticalLayout implements View {

	}

	@SpringView(name = "view2")
	@RolesAllowed("role1") // <5>
	class ViewTwo extends VerticalLayout implements View {

	}

	@AccessDeniedView // <6>
	@UIScope
	@SpringView(name = "forbidden")
	class AccessDenied extends VerticalLayout implements View {

		private static final long serialVersionUID = 1L;

		private final Label message;

		public AccessDenied() {
			super();
			Components.configure(this).margin()
					.add(message = Components.label().styleName(ValoTheme.LABEL_FAILURE).build());
		}

		@Override
		public void enter(ViewChangeEvent event) {
			message.setValue("Access denied [" + event.getViewName() + "]");
		}

	}
	// end::authz[]

}
