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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;

import com.vaadin.data.Converter;
import com.vaadin.data.Result;
import com.vaadin.data.ValueContext;

/**
 * Abstract {@link Converter} to convert {@link LocalDateTime}s into {@link Date}s and back.
 * 
 * @since 5.0.0
 */
public class DateToLocalDateTimeConverter implements TimeZonedConverter<Date, LocalDateTime> {

	private static final long serialVersionUID = -5571033980372020964L;

	/*
	 * Time zone to use for conversion
	 */
	private ZoneId timeZone;

	/**
	 * Construct a converter using default Time Zone
	 * @see ZoneId#systemDefault()
	 */
	public DateToLocalDateTimeConverter() {
		this(ZoneOffset.UTC);
	}

	/**
	 * Construct a converter using given Time Zone
	 * @param timeZone Time zone to use for conversion
	 */
	public DateToLocalDateTimeConverter(ZoneId timeZone) {
		super();
		this.timeZone = (timeZone != null) ? timeZone : ZoneOffset.UTC;
	}

	/**
	 * Gets the Time zone to use for conversion
	 * @return the timeZone Time zone to use for conversion
	 */
	@Override
	public ZoneId getTimeZone() {
		return timeZone;
	}

	/**
	 * Sets the Time zone to use for conversion
	 * @param timeZone the Time zone to set
	 */
	@Override
	public void setTimeZone(ZoneId timeZone) {
		this.timeZone = timeZone;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Converter#convertToModel(java.lang.Object, com.vaadin.data.ValueContext)
	 */
	@Override
	public Result<LocalDateTime> convertToModel(Date value, ValueContext context) {
		if (value != null) {
			return Result.ok(LocalDateTime.ofInstant(value.toInstant(), getTimeZone()));
		}
		return Result.ok(null);
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Converter#convertToPresentation(java.lang.Object, com.vaadin.data.ValueContext)
	 */
	@Override
	public Date convertToPresentation(LocalDateTime value, ValueContext context) {
		if (value != null) {
			return Date.from(value.atZone(getTimeZone()).toInstant());
		}
		return null;
	}

}
