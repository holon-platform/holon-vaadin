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

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.holonplatform.vaadin.navigator.annotations.ViewContext;
import com.vaadin.navigator.View;

/**
 * Spring configuration to provide post-processor to enable {@link ViewContext} injection
 *
 * @since 5.0.0
 */
@Configuration
public class ViewContextInjectionConfiguration {

	/**
	 * Register a {@link BeanPostProcessor} to enable {@link ViewContext} injection in navigator {@link View} annotated
	 * fields.
	 * @return BeanPostProcessor
	 */
	@Bean
	public static BeanPostProcessor viewContextInjectionPostProcessor() {
		return new ViewContextInjectionPostProcessor();
	}

}
