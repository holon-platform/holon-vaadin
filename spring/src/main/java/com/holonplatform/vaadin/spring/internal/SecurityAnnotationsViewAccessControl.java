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
package com.holonplatform.vaadin.spring.internal;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.holonplatform.auth.AuthContext;
import com.holonplatform.core.Context;
import com.vaadin.navigator.View;
import com.vaadin.spring.access.ViewAccessControl;
import com.vaadin.ui.UI;

/**
 * A {@link ViewAccessControl} bean to check {@link View} authorization using <code>javax.annotation.security</code>
 * annotations on View class ({@link RolesAllowed}, {@link PermitAll}, {@link DenyAll}) and relying on
 * {@link AuthContext} to perform authorization control.
 * <p>
 * An {@link AuthContext} instance must be available as {@link Context} resource in order to perform access control.
 * </p>
 * 
 * @see AuthContext#getCurrent()
 *
 * @since 5.0.0
 */
public class SecurityAnnotationsViewAccessControl implements ViewAccessControl, ApplicationContextAware {

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

		if (applicationContext.findAnnotationOnBean(beanName, DenyAll.class) != null) {
			// DenyAll (no authentication required)
			return false;
		}
		if (applicationContext.findAnnotationOnBean(beanName, PermitAll.class) != null) {
			// PermitAll (no authentication required)
			return true;
		}

		// RolesAllowed - authentication required
		RolesAllowed ra = applicationContext.findAnnotationOnBean(beanName, RolesAllowed.class);
		if (ra != null) {

			// check authentication
			final AuthContext authContext = AuthContext.getCurrent()
					.orElseThrow(() -> new IllegalStateException("No AuthContext available as Context resource: "
							+ "failed to validate RolesAllowed security annotation on View bean name [" + beanName
							+ "]"));
			if (!authContext.getAuthentication().isPresent()) {
				// not authenticated
				return false;
			}

			// check permissions
			if (ra.value().length > 0) {
				// for empty roles names, no role is required, only authentication
				if (!authContext.isPermittedAny(ra.value())) {
					// no roles matches (with ANY semantic)
					return false;
				}
			}
		}

		return true;
	}

}
