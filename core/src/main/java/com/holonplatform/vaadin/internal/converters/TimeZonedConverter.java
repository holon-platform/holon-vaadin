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
package com.holonplatform.vaadin.internal.converters;

import java.time.ZoneId;
import java.util.TimeZone;

import com.vaadin.data.Converter;

/**
 * A {@link Converter} with Time Zone support.
 * 
 * @param <PRESENTATION> Presentation type
 * @param <MODEL> Model type
 * 
 * @since 5.0.0
 */
public interface TimeZonedConverter<PRESENTATION, MODEL> extends Converter<PRESENTATION, MODEL> {

	/**
	 * Gets the Time Zone to use for conversion
	 * @return the conversion Time Zone
	 */
	ZoneId getTimeZone();

	/**
	 * Sets the Time Zone to use for conversion
	 * @param timeZone the Time Zone to set
	 */
	void setTimeZone(ZoneId timeZone);

	/**
	 * Sets the Time Zone to use for conversion using a {@link TimeZone} type
	 * @param timeZone the Time Zone to set
	 */
	default void setTimeZone(TimeZone timeZone) {
		setTimeZone((timeZone != null) ? timeZone.toZoneId() : null);
	}

}
