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
package com.holonplatform.vaadin.spring.boot;

import java.util.List;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import com.holonplatform.core.internal.Logger;
import com.holonplatform.vaadin.internal.VaadinLogger;
import com.holonplatform.vaadin.spring.boot.internal.HolonVaadinServlet;
import com.vaadin.server.SessionDestroyListener;
import com.vaadin.server.SessionInitListener;
import com.vaadin.server.VaadinServlet;
import com.vaadin.spring.boot.internal.VaadinServletConfiguration;
import com.vaadin.spring.server.SpringVaadinServlet;

/**
 * Spring boot auto configuration class to use the {@link HolonVaadinServlet} as Vaadin servlet, allowing 
 * automatic {@link SessionInitListener} and {@link SessionDestroyListener} beans registration.
 *
 * @since 5.0.4
 */
@Configuration
@ConditionalOnWebApplication
@ConditionalOnClass(SpringVaadinServlet.class)
@AutoConfigureBefore(value = { /* VaadinAutoConfiguration.class, */ VaadinServletConfiguration.class })
public class HolonVaadinServletAutoConfiguration {

	/**
	 * Logger
	 */
	private static final Logger LOGGER = VaadinLogger.create();

	private final List<SessionInitListener> sessionInitListeners;
	private final List<SessionDestroyListener> sessionDestroyListeners;

	public HolonVaadinServletAutoConfiguration(ObjectProvider<List<SessionInitListener>> sessionInitListeners,
			ObjectProvider<List<SessionDestroyListener>> sessionDestroyListeners) {
		this.sessionInitListeners = sessionInitListeners.getIfAvailable();
		this.sessionDestroyListeners = sessionDestroyListeners.getIfAvailable();
	}

	@Bean
	@ConditionalOnMissingBean
	public VaadinServlet vaadinServlet() {
		HolonVaadinServlet servlet = new HolonVaadinServlet();
		configureServlet(servlet);

		LOGGER.debug(() -> "HolonVaadinServlet configured");

		return servlet;
	}

	/**
	 * Configure the {@link HolonVaadinServlet} instance registering {@link SessionInitListener} and
	 * {@link SessionDestroyListener} beans, if available.
	 * @param servlet the servlet
	 */
	private void configureServlet(HolonVaadinServlet servlet) {
		// SessionInitListeners
		if (this.sessionInitListeners != null) {
			AnnotationAwareOrderComparator.sort(this.sessionInitListeners);
			for (SessionInitListener listener : this.sessionInitListeners) {
				servlet.addSessionInitListener(listener);
				LOGGER.info("Registered SessionInitListener [" + listener.getClass().getName() + "]");
			}
		}
		// SessionDestroyListeners
		if (this.sessionDestroyListeners != null) {
			AnnotationAwareOrderComparator.sort(this.sessionDestroyListeners);
			for (SessionDestroyListener listener : this.sessionDestroyListeners) {
				servlet.addSessionDestroyListener(listener);
				LOGGER.info("Registered SessionDestroyListener [" + listener.getClass().getName() + "]");
			}
		}
	}

}
