/*
 * Copyright 2000-2017 Holon TDCN.
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
package com.holonplatform.vaadin.internal.components;

import java.io.Serializable;
import java.time.ZoneId;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;
import java.util.Date;
import java.util.TimeZone;

import com.holonplatform.vaadin.components.builders.DateInputBuilder.Resolution;
import com.holonplatform.vaadin.internal.converters.TimeZonedConverter;
import com.vaadin.ui.AbstractDateField;

public abstract class AbstractCalendarField<T extends Temporal & TemporalAdjuster & Serializable & Comparable<? super T>>
		extends AbstractCustomField<Date, T, AbstractDateField<T, ?>> {

	private static final long serialVersionUID = 4306592900956596843L;

	private final Resolution resolution;
	private final boolean inline;

	public AbstractCalendarField(Resolution resolution, boolean inline) {
		super(Date.class, false);
		this.resolution = (resolution != null) ? resolution : Resolution.DAY;
		this.inline = inline;

		addStyleName("h-datefield", false);

		init();
	}

	protected abstract TimeZonedConverter<Date, T> getConverter();

	protected Resolution getResolution() {
		return resolution;
	}

	protected boolean isInline() {
		return inline;
	}

	public void setTimeZone(ZoneId timeZone) {
		getConverter().setTimeZone(timeZone);
	}

	public void setTimeZone(TimeZone timeZone) {
		getConverter().setTimeZone(timeZone);
	}

	public void setShowISOWeekNumbers(boolean showWeekNumbers) {
		getInternalField().setShowISOWeekNumbers(showWeekNumbers);
	}

	public void setParseErrorMessage(String parsingErrorMessage) {
		getInternalField().setParseErrorMessage(parsingErrorMessage);
	}

	public void setDateOutOfRangeMessage(String dateOutOfRangeMessage) {
		getInternalField().setDateOutOfRangeMessage(dateOutOfRangeMessage);
	}

	public void setRangeStart(Date startDate) {
		getInternalField().setRangeStart(toInternalValue(startDate));
	}

	public void setRangeEnd(Date endDate) {
		getInternalField().setRangeEnd(toInternalValue(endDate));
	}

	public void setDateFormat(String dateFormat) {
		getInternalField().setDateFormat(dateFormat);
	}

	public void setLenient(boolean lenient) {
		getInternalField().setLenient(lenient);
	}

}
