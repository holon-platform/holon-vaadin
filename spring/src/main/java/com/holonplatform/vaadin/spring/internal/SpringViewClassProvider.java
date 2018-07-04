/*
 * Copyright 2016-2018 Axioma srl.
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

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotatedElementUtils;

import com.holonplatform.core.internal.Logger;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.internal.VaadinLogger;
import com.holonplatform.vaadin.navigator.ViewClassProvider;
import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.internal.Conventions;
import com.vaadin.ui.UI;

/**
 * Spring {@link ViewClassProvider}.
 *
 * @since 5.1.3
 */
public class SpringViewClassProvider implements ViewClassProvider {

	private final static Logger LOGGER = VaadinLogger.create();

	private final Map<String, WeakReference<Class<? extends View>>> viewClasses = new ConcurrentHashMap<>();
	private final Map<Class<? extends UI>, Map<String, WeakReference<Class<? extends View>>>> uiViewClasses = new WeakHashMap<>();

	private final transient ApplicationContext applicationContext;

	/**
	 * Constructor.
	 * @param applicationContext ApplicationContext (not null)
	 */
	public SpringViewClassProvider(ApplicationContext applicationContext) {
		super();
		ObjectUtils.argumentNotNull(applicationContext, "ApplicationContext must be not null");
		this.applicationContext = applicationContext;
	}

	/**
	 * Init the provider, detecting all the View classes bound to the available View type beans.
	 */
	public void init() {
		LOGGER.info("SpringViewClassProvider: Detecting View classes");
		final String[] viewBeanNames = applicationContext.getBeanNamesForAnnotation(SpringView.class);
		for (String beanName : viewBeanNames) {
			try {
				final Class<?> type = applicationContext.getType(beanName);
				if (View.class.isAssignableFrom(type)) {

					@SuppressWarnings("unchecked")
					final Class<? extends View> viewType = (Class<? extends View>) type;

					final SpringView annotation = AnnotatedElementUtils.findMergedAnnotation(type, SpringView.class);

					if (annotation != null) {
						// View name
						final String viewName = getViewNameFromAnnotation(type, annotation);
						// UIs
						Class<? extends UI>[] uis = annotation.ui();
						if (uis.length == 0) {
							viewClasses.put(viewName, new WeakReference<>(viewType));
							LOGGER.debug(() -> "View class [" + viewType + "] detected for the view name [" + viewName
									+ "]");
						} else {
							for (Class<? extends UI> ui : uis) {
								uiViewClasses.computeIfAbsent(ui, key -> new HashMap<>()).put(viewName,
										new WeakReference<>(viewType));
								LOGGER.debug(() -> "View class [" + viewType + "] detected for the view name ["
										+ viewName + "] - bound to the UI class [" + ui + "]");
							}
						}
					}
				}
			} catch (@SuppressWarnings("unused") NoSuchBeanDefinitionException e) {
				// ignore
			}
		}
	}

	private String getViewNameFromAnnotation(Class<?> beanClass, SpringView annotation) {
		String viewName = Conventions.deriveMappingForView(beanClass, annotation);
		return applicationContext.getEnvironment().resolvePlaceholders(viewName);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.navigator.ViewClassProvider#getViewClass(java.lang.String)
	 */
	@Override
	public Optional<Class<? extends View>> getViewClass(String viewName) {
		if (viewName != null) {
			// check all UIs
			WeakReference<Class<? extends View>> viewClass = viewClasses.get(viewName);
			if (viewClass != null) {
				return Optional.ofNullable(viewClass.get());
			}
			// check specific UI
			final UI currentUI = UI.getCurrent();
			if (currentUI != null) {
				// check
				viewClass = uiViewClasses.getOrDefault(currentUI.getClass(), Collections.emptyMap()).get(viewName);
				if (viewClass != null) {
					return Optional.ofNullable(viewClass.get());
				}
			}
		}
		return Optional.empty();
	}

}
