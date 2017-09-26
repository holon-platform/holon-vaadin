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
package com.holonplatform.vaadin.spring.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.holonplatform.vaadin.navigator.ViewNavigator;
import com.holonplatform.vaadin.navigator.annotations.ViewContext;
import com.holonplatform.vaadin.spring.AccessDeniedView;
import com.holonplatform.vaadin.spring.DefaultView;
import com.holonplatform.vaadin.spring.ErrorView;
import com.holonplatform.vaadin.spring.SpringViewNavigator;
import com.holonplatform.vaadin.spring.internal.ViewNavigatorEnabler;
import com.vaadin.navigator.View;
import com.vaadin.spring.VaadinConfiguration;
import com.vaadin.spring.access.ViewAccessControl;
import com.vaadin.spring.access.ViewInstanceAccessControl;
import com.vaadin.spring.annotation.EnableVaadin;

/**
 * Annotation to be used on Spring Configuration classes to setup Vaadin integration and registering a UI-scoped
 * {@link SpringViewNavigator} navigator.
 * 
 * <p>
 * This interface imports {@link VaadinConfiguration}, enabling Spring Vaadin integration. For this reason, replaces
 * {@link EnableVaadin} annotation behaviour, which is not required anymore on configuration classes.
 * </p>
 *
 * @since 5.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({ VaadinConfiguration.class, ViewNavigatorEnabler.class })
public @interface EnableViewNavigator {

	/**
	 * Enable context data injection in navigation Views using {@link ViewContext} annotation.
	 * <p>
	 * Default is <code>true</code>.
	 * </p>
	 * @return <code>true</code> to enable context data injection in navigation Views using {@link ViewContext}
	 *         annotation.
	 */
	boolean enableViewContextInjection() default true;

	/**
	 * Limit view navigation history tracking.
	 * @return Navigation history tracking max size. <code>-1</code> means no limit.
	 */
	int maxNavigationHistorySize() default -1;

	/**
	 * Set the default navigator view name. The default view it is used as target of
	 * {@link ViewNavigator#navigateToDefault()} method and as a fallback by {@link ViewNavigator#navigateBack()} method
	 * if no other View is available in history or as a default view when no view name is specified.
	 * <p>
	 * If a default view name is explicitly specified, any {@link DefaultView} annotated View is ignored.
	 * </p>
	 * @return Default navigator view name
	 */
	String defaultViewName() default "";

	/**
	 * Set whether to navigate to default view (if setted) when a view is not available from current navigation state.
	 * @return <code>true</code> to navigate to default view (if setted) when a view is not available from current
	 *         navigation state
	 */
	boolean navigateToDefaultViewWhenViewNotAvailable() default false;

	/**
	 * Set the View class to use as navigator error view.
	 * <p>
	 * If an error view class is explicitly specified, any {@link ErrorView} annotated View is ignored.
	 * </p>
	 * @return Navigator error view class
	 */
	Class<? extends View> errorView() default View.class;

	/**
	 * Set the View class to use as <em>access denied</em> view, i.e. the view to show when a {@link ViewAccessControl}
	 * or a {@link ViewInstanceAccessControl} denies access to a view.
	 * <p>
	 * If an access denied view class is explicitly specified, any {@link AccessDeniedView} annotated View is ignored.
	 * </p>
	 * @return Navigator error view class
	 */
	Class<? extends View> accessDeniedView() default View.class;

}
