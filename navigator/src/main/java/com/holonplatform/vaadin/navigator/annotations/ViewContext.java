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
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

import com.holonplatform.core.Context;

/**
 * Annotation used to inject a {@link Context} resource into an annotated {@link Field}.
 * <p>
 * Required Context resource type is derived from Field's type.
 * </p>
 *
 * @since 5.0.0
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ViewContext {

	/**
	 * Optional context resource key to inject. If not configured, the resource key will be derived from the target
	 * injection property type class name.
	 * @return Context resource key
	 */
	String value() default "";

	/**
	 * Whether the resource injection is required. If required and a consistent context resource is not available for
	 * injection, a configuration exception is thrown.
	 * @return <code>true</code> if the resource injection is required
	 */
	boolean required() default true;

}
