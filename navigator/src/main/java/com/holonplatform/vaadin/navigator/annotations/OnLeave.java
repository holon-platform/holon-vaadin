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
package com.holonplatform.vaadin.navigator.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.holonplatform.vaadin.navigator.ViewNavigator.ViewNavigatorChangeEvent;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;

/**
 * {@link View} class accessible (public) methods annotated with this annotation will be called by view navigator when
 * the view is about to be deactivated (i.e. a navigation to another view was triggered).
 * 
 * <p>
 * Annotated methods may provide an optional parameter of {@link ViewNavigatorChangeEvent} or default
 * {@link ViewChangeEvent} type to obtain informations about view navigation.
 * </p>
 * 
 * If more than one OnLeave annotated method in present in view actual class or in it's class hierarchy, all these
 * methods will be called and the following behaviour will be adopted:
 * <ul>
 * <li>Methods will be called following class hierarchy, starting from the top (the first superclass after Object)</li>
 * <li>For methods of the same class, no calling order is guaranteed</li>
 * </ul>
 * 
 * @since 4.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
@Documented
public @interface OnLeave {

}
