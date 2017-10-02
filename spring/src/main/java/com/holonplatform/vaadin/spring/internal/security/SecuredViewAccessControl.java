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

import java.io.Serializable;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.spring.access.ViewAccessControl;
import com.vaadin.ui.UI;

/**
 * A {@link ViewAccessControl} to enable Spring Security support using the {@link Secured} annotation.
 *
 * @since 5.0.0
 */
public class SecuredViewAccessControl implements ViewAccessControl, ApplicationContextAware, Serializable {

	private static final long serialVersionUID = 8760650298896814700L;

	private ApplicationContext applicationContext;

	/*
	 * (non-Javadoc)
	 * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.
	 * ApplicationContext)
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.spring.access.ViewAccessControl#isAccessGranted(com.vaadin.ui.UI, java.lang.String)
	 */
	@Override
	public boolean isAccessGranted(UI ui, String beanName) {
		final Secured secured = applicationContext.findAnnotationOnBean(beanName, Secured.class);
		if (secured == null) {
			return true;
		}
		return isAccessGranted(secured.value());
	}

	/**
	 * Checks if the current user is granted any explicitly provided security attributes.
	 * @param securityConfigAttributes list of security configuration attributes (e.g. ROLE_USER, ROLE_ADMIN).
	 * @return <code>true</code> if the access is granted or the view is not secured, <code>false</code> otherwise
	 */
	private static boolean isAccessGranted(String[] securityConfigAttributes) {
		SecurityContext context = SecurityContextHolder.getContext();
		Authentication authentication = context.getAuthentication();
		if (authentication == null) {
			return false;
		}
		Set<String> authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
				.collect(Collectors.toSet());
		return Stream.of(securityConfigAttributes).anyMatch(authorities::contains);
	}

}
