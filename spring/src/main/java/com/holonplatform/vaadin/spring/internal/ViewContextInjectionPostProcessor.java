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
package com.holonplatform.vaadin.spring.internal;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import com.holonplatform.vaadin.navigator.ViewNavigator;
import com.holonplatform.vaadin.navigator.annotations.ViewContext;
import com.holonplatform.vaadin.navigator.internal.ViewNavigationUtils;
import com.holonplatform.vaadin.navigator.internal.ViewConfiguration.ViewConfigurationProvider;
import com.vaadin.navigator.View;

/**
 * {@link BeanPostProcessor} to inject {@link ViewContext} fields into a {@link View} instance.
 *
 * @since 5.0.0
 */
public class ViewContextInjectionPostProcessor implements BeanPostProcessor {

	/*
	 * (non-Javadoc)
	 * @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessBeforeInitialization(java.lang.Object,
	 * java.lang.String)
	 */
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof View) {
			ViewNavigator navigator = ViewNavigator.require();
			if (navigator instanceof ViewConfigurationProvider) {
				return ViewNavigationUtils.injectContext((ViewConfigurationProvider) navigator, (View) bean);
			}
		}
		return bean;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessAfterInitialization(java.lang.Object,
	 * java.lang.String)
	 */
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

}
