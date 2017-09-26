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

import org.springframework.beans.factory.FactoryBean;

import com.holonplatform.vaadin.spring.SpringViewNavigator;
import com.vaadin.navigator.View;

/**
 * Spring {@link FactoryBean} to create and configure {@link SpringViewNavigator} beans.
 * 
 * @since 5.0.0
 */
public class SpringViewNavigatorFactoryBean implements FactoryBean<DefaultSpringViewNavigator> {

	private int maxNavigationHistorySize;
	private String defaultViewName;
	private boolean navigateToDefaultViewWhenViewNotAvailable;
	private Class<? extends View> errorViewClass;
	private Class<? extends View> accessDeniedViewClass;

	public void setMaxNavigationHistorySize(int maxNavigationHistorySize) {
		this.maxNavigationHistorySize = maxNavigationHistorySize;
	}

	public void setDefaultViewName(String defaultViewName) {
		this.defaultViewName = defaultViewName;
	}

	public void setNavigateToDefaultViewWhenViewNotAvailable(boolean navigateToDefaultViewWhenViewNotAvailable) {
		this.navigateToDefaultViewWhenViewNotAvailable = navigateToDefaultViewWhenViewNotAvailable;
	}

	public void setErrorViewClass(Class<? extends View> errorViewClass) {
		this.errorViewClass = errorViewClass;
	}

	public void setAccessDeniedViewClass(Class<? extends View> accessDeniedViewClass) {
		this.accessDeniedViewClass = accessDeniedViewClass;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.beans.factory.FactoryBean#getObject()
	 */
	@Override
	public DefaultSpringViewNavigator getObject() throws Exception {
		DefaultSpringViewNavigator.SpringViewNavigatorBuilder builder = new DefaultSpringViewNavigator.SpringViewNavigatorBuilder();
		builder.navigateToDefaultViewWhenViewNotAvailable(navigateToDefaultViewWhenViewNotAvailable);
		if (maxNavigationHistorySize > -1) {
			builder.maxNavigationHistorySize(maxNavigationHistorySize);
		}
		if (defaultViewName != null) {
			builder.defaultViewName(defaultViewName);
		}
		if (errorViewClass != null) {
			builder.errorView(errorViewClass);
		}
		if (accessDeniedViewClass != null) {
			builder.accessDeniedView(accessDeniedViewClass);
		}
		return builder.buildInternal();
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.beans.factory.FactoryBean#getObjectType()
	 */
	@Override
	public Class<?> getObjectType() {
		return DefaultSpringViewNavigator.class;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.beans.factory.FactoryBean#isSingleton()
	 */
	@Override
	public boolean isSingleton() {
		return false;
	}

}
