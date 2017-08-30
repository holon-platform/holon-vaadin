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
package com.holonplatform.vaadin.navigator.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.holonplatform.vaadin.navigator.internal.ViewConfiguration.ViewWindowConfiguration;
import com.vaadin.navigator.View;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Window;

/**
 * Force a {@link View} annotated with this annotation to be opened in a {@link Window}.
 * 
 * @since 5.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Inherited
public @interface WindowView {

	/**
	 * Whether to show a close button in the view Window header
	 * @return <code>true</code> to show a close button in the view Window header
	 */
	boolean closable() default true;

	/**
	 * Whether to allow the view Window to be resized
	 * @return <code>true</code> to allow the view Window to be resized
	 */
	boolean resizable() default true;

	/**
	 * View Window width using String representation. See {@link Sizeable#setWidth(String)} for details.
	 * @return View Window width. Default is {@link ViewWindowConfiguration#DEFAULT_WINDOW_WIDTH}
	 */
	String windowWidth() default ViewWindowConfiguration.DEFAULT_WINDOW_WIDTH;

	/**
	 * View Window height using String representation. See {@link Sizeable#setHeight(String)} for details.
	 * @return View Window height. Default is {@link ViewWindowConfiguration#DEFAULT_WINDOW_HEIGHT}
	 */
	String windowHeigth() default ViewWindowConfiguration.DEFAULT_WINDOW_HEIGHT;

}
