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
package com.holonplatform.vaadin.spring.internal.security;

import org.springframework.context.annotation.Bean;

import com.vaadin.navigator.View;
import com.vaadin.spring.access.ViewAccessControl;

/**
 * Spring configuration to enable {@link View} access control using Spring Security <code>Secured</code> annotations on
 * View class.
 *
 * @since 5.0.0
 */
public class SecuredViewAccessControlConfiguration {

	@Bean
	public ViewAccessControl securedViewAccessControl() {
		return new SecuredViewAccessControl();
	}

}
