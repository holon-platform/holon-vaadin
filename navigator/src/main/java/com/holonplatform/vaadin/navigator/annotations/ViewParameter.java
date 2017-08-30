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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

import com.vaadin.navigator.View;

/**
 * Annotation to mark a {@link View} class field as view parameter holder, i.e. the field in which to inject a specific
 * view navigation parameter.
 * 
 * <p>
 * Parameter name can be specified using {@link #value()}. If not setted (empty String), the parameter name is assumed
 * to be the annotated field name.
 * </p>
 * 
 * Supported parameter value types are:
 * <ul>
 * <li>{@link String}</li>
 * <li>{@link Number}s</li>
 * <li>{@link Boolean}</li>
 * <li>{@link Enum} (ordinal value must be used for enum values serialization)</li>
 * <li>{@link Date} using date format pattern {@link #DEFAULT_DATE_PATTERN}</li>
 * <li>{@link LocalDate} using date format pattern ISO local date (yyyy-MM-dd)</li>
 * <li>{@link LocalTime} using date format pattern ISO local time (HH:mm:ss)</li>
 * <li>{@link LocalDateTime} using date format pattern ISO local date/time ('yyyy-MM-ddTHH:mm:ss')</li>
 * </ul>
 * 
 * @since 4.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
@Inherited
public @interface ViewParameter {

	/**
	 * Characters not admitted in parameter name
	 */
	public static final char[] ILLEGAL_PARAMETER_NAME_CHARACTERS = new char[] { '/', '&', '=' };

	/**
	 * Default Date parameter value serialization pattern
	 */
	public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";

	/**
	 * Default parameters URI encode charset
	 */
	public static final String DEFAULT_PARAMETER_ENCODING = "UTF-8";

	/**
	 * Parameter name. If not setted (empty String), the parameter name is assumed to be the annotated field name.
	 * @return Parameter name
	 */
	String value() default "";

	/**
	 * Declares this parameter as required. If parameter is not provided and no {@link #defaultValue()} is defined, an
	 * error will be thrown.
	 * @return <code>true</code> if parameter is required, <code>false</code> (default) otherwise
	 */
	boolean required() default false;

	/**
	 * Default parameter value if no value is provided.
	 * <p>
	 * According to parameter value type, a suitable String representation must be provided for default value:
	 * <ul>
	 * <li>For numeric values, <code>.</code> (dot) charachter must be used as decimal separator</li>
	 * <li>For boolean values, only words <code>true</code> and <code>false</code> are admitted</li>
	 * <li>For Enum values, String representation of enum ordinal index must be provided</li>
	 * <li>For Date values, default value must be expressed using date format pattern {@link #DEFAULT_DATE_PATTERN}</li>
	 * </ul>
	 * @return Default parameter value, or an empty String for none
	 */
	String defaultValue() default "";

}
