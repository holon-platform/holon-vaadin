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

import java.util.Map;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import com.holonplatform.vaadin.spring.SpringViewNavigator;
import com.holonplatform.vaadin.spring.config.EnableViewNavigator;
import com.vaadin.navigator.View;
import com.vaadin.spring.internal.SpringViewDisplayPostProcessor;
import com.vaadin.spring.internal.UIScopeImpl;

/**
 * Enables {@link SpringViewNavigator} as an UI-scoped bean.
 * <p>
 * The reccomended way to use this enabler is through {@link EnableViewNavigator} annotation on Spring configuration
 * classes.
 * </p>
 * 
 * @since 5.0.0
 * 
 * @see EnableViewNavigator
 */
public class ViewNavigatorEnabler implements ImportBeanDefinitionRegistrar {

	/*
	 * (non-Javadoc)
	 * @see
	 * org.springframework.context.annotation.ImportBeanDefinitionRegistrar#registerBeanDefinitions(org.springframework.
	 * core.type.AnnotationMetadata, org.springframework.beans.factory.support.BeanDefinitionRegistry)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {

		// Check is not a call from sub-classes
		if (annotationMetadata.getAnnotationAttributes(EnableViewNavigator.class.getName()) == null) {
			return;
		}

		final Map<String, Object> attributes = annotationMetadata
				.getAnnotationAttributes(EnableViewNavigator.class.getName());

		// Context data injection
		if (attributes.containsKey("enableViewContextInjection")
				&& ((boolean) attributes.get("enableViewContextInjection"))) {
			GenericBeanDefinition definition = new GenericBeanDefinition();
			definition.setBeanClass(ViewContextInjectionPostProcessor.class);
			definition.setAutowireCandidate(false);
			registry.registerBeanDefinition("viewContextInjectionPostProcessor", definition);
		}

		// Navigator

		int maxNavigationHistorySize = -1;
		String defaultViewName = null;
		boolean navigateToDefaultViewWhenViewNotAvailable = false;
		Class<? extends View> errorViewClass = null;
		Class<? extends View> accessDeniedViewClass = null;

		if (attributes.containsKey("maxNavigationHistorySize")) {
			maxNavigationHistorySize = (int) attributes.get("maxNavigationHistorySize");
		}
		if (attributes.containsKey("defaultViewName")) {
			defaultViewName = (String) attributes.get("defaultViewName");
			if (defaultViewName != null && defaultViewName.trim().equals("")) {
				defaultViewName = null;
			}
		}
		if (attributes.containsKey("navigateToDefaultViewWhenViewNotAvailable")) {
			navigateToDefaultViewWhenViewNotAvailable = (boolean) attributes
					.get("navigateToDefaultViewWhenViewNotAvailable");
		}
		if (attributes.containsKey("errorView")) {
			errorViewClass = (Class<? extends View>) attributes.get("errorView");
			if (errorViewClass != null && errorViewClass == View.class) {
				errorViewClass = null;
			}
		}
		if (attributes.containsKey("accessDeniedView")) {
			accessDeniedViewClass = (Class<? extends View>) attributes.get("accessDeniedView");
			if (accessDeniedViewClass != null && accessDeniedViewClass == View.class) {
				accessDeniedViewClass = null;
			}
		}

		GenericBeanDefinition definition = new GenericBeanDefinition();
		definition.setBeanClass(SpringViewNavigatorFactoryBean.class);
		definition.setAutowireCandidate(false);
		MutablePropertyValues pvs = new MutablePropertyValues();
		pvs.add("maxNavigationHistorySize", maxNavigationHistorySize);
		pvs.add("defaultViewName", defaultViewName);
		pvs.add("navigateToDefaultViewWhenViewNotAvailable", navigateToDefaultViewWhenViewNotAvailable);
		pvs.add("errorViewClass", errorViewClass);
		pvs.add("accessDeniedViewClass", accessDeniedViewClass);
		definition.setPropertyValues(pvs);
		registry.registerBeanDefinition("viewNavigatorFactory", definition);

		definition = new GenericBeanDefinition();
		definition.setBeanClass(DefaultSpringViewNavigator.class);
		definition.setAutowireCandidate(true);
		definition.setScope(UIScopeImpl.VAADIN_UI_SCOPE_NAME);
		definition.setFactoryBeanName("viewNavigatorFactory");
		registry.registerBeanDefinition("viewNavigator", definition);

		definition = new GenericBeanDefinition();
		definition.setBeanClass(SpringViewDisplayPostProcessor.class);
		definition.setAutowireCandidate(false);
		registry.registerBeanDefinition("springViewDisplayPostProcessor", definition);

	}

}
