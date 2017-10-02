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
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.holonplatform.vaadin.spring.internal.security.SecuredViewAccessControlConfiguration;
import com.vaadin.navigator.View;

/**
 * Enables {@link View} authorization control using Spring Security <code>Secured</code> annotations on View class to
 * perform authorization control.
 * <p>
 * A Spring Security context must be configured and available in application context.
 * </p>
 *
 * @since 5.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(SecuredViewAccessControlConfiguration.class)
public @interface EnableSecuredView {

}
