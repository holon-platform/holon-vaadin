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

import com.holonplatform.vaadin.navigator.SubViewContainer;
import com.vaadin.navigator.View;

/**
 * Declares a {@link View} as a sub-view of given View name.
 * <p>
 * The parent View must be a {@link SubViewContainer}.
 * </p>
 * 
 * @see SubViewContainer
 * 
 * @since 4.5.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Inherited
public @interface SubViewOf {

	/**
	 * Parent view name. The parent View must be a {@link SubViewContainer}.
	 * @return Parent view name
	 */
	String value();

}
