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
package com.holonplatform.vaadin.internal.components.builders;

import java.util.Date;
import java.util.TimeZone;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.vaadin.components.Input;
import com.holonplatform.vaadin.components.builders.CalendarInputBuilder;
import com.holonplatform.vaadin.components.builders.DateInputBuilder;
import com.holonplatform.vaadin.internal.components.AbstractCalendarField;

/**
 * Base {@link CalendarInputBuilder} implementation.
 * 
 * @param <I> Concrete date field type
 * 
 * @since 5.0.0
 */
public abstract class AbstractDateFieldBuilder<I extends AbstractCalendarField<?>>
		extends AbstractFieldBuilder<Date, Input<Date>, I, DateInputBuilder> implements DateInputBuilder {

	protected Localizable parseErrorMessage;
	protected Localizable outOfRangeMessage;

	public AbstractDateFieldBuilder(I instance) {
		super(instance);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.internal.components.builders.AbstractFieldBuilder#localize(com.vaadin.ui.AbstractField)
	 */
	@Override
	protected void localize(I instance) {
		super.localize(instance);

		if (parseErrorMessage != null) {
			instance.setParseErrorMessage(LocalizationContext.translate(parseErrorMessage, true));
		}
		if (outOfRangeMessage != null) {
			instance.setDateOutOfRangeMessage(LocalizationContext.translate(outOfRangeMessage, true));
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.CalendarFieldBuilder#dateFormat(java.lang.String)
	 */
	@Override
	public DateInputBuilder dateFormat(String dateFormat) {
		getInstance().setDateFormat(dateFormat);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.CalendarFieldBuilder#parseErrorMessage(java.lang.String)
	 */
	@Override
	public DateInputBuilder parseErrorMessage(String parsingErrorMessage) {
		getInstance().setParseErrorMessage(parsingErrorMessage);
		this.parseErrorMessage = null;
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.CalendarFieldBuilder#parseErrorMessage(java.lang.String,
	 * java.lang.String, java.lang.Object[])
	 */
	@Override
	public DateInputBuilder parseErrorMessage(String defaultParseErrorMessage, String messageCode,
			Object... arguments) {
		this.parseErrorMessage = Localizable.builder().message(defaultParseErrorMessage).messageCode(messageCode)
				.messageArguments(arguments).build();
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.CalendarFieldBuilder#parseErrorMessage(com.holonplatform.core.i18n
	 * .Localizable)
	 */
	@Override
	public DateInputBuilder parseErrorMessage(Localizable parseErrorMessage) {
		this.parseErrorMessage = parseErrorMessage;
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.CalendarFieldBuilder#lenient(boolean)
	 */
	@Override
	public DateInputBuilder lenient(boolean lenient) {
		getInstance().setLenient(lenient);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.CalendarFieldBuilder#showISOWeekNumbers(boolean)
	 */
	@Override
	public DateInputBuilder showISOWeekNumbers(boolean showWeekNumbers) {
		getInstance().setShowISOWeekNumbers(showWeekNumbers);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.CalendarFieldBuilder#timeZone(java.util.TimeZone)
	 */
	@Override
	public DateInputBuilder timeZone(TimeZone timeZone) {
		getInstance().setTimeZone(timeZone);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.CalendarFieldBuilder#rangeStart(java.lang.Object)
	 */
	@Override
	public DateInputBuilder rangeStart(Date start) {
		getInstance().setRangeStart(start);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.CalendarFieldBuilder#rangeEnd(java.lang.Object)
	 */
	@Override
	public DateInputBuilder rangeEnd(Date end) {
		getInstance().setRangeEnd(end);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.CalendarFieldBuilder#dateOutOfRangeMessage(java.lang.String)
	 */
	@Override
	public DateInputBuilder dateOutOfRangeMessage(String dateOutOfRangeMessage) {
		getInstance().setDateOutOfRangeMessage(dateOutOfRangeMessage);
		this.outOfRangeMessage = null;
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.CalendarFieldBuilder#dateOutOfRangeMessage(java.lang.String,
	 * java.lang.String, java.lang.Object[])
	 */
	@Override
	public DateInputBuilder dateOutOfRangeMessage(String defaultDateOutOfRangeMessage, String messageCode,
			Object... arguments) {
		this.outOfRangeMessage = Localizable.builder().message(defaultDateOutOfRangeMessage).messageCode(messageCode)
				.messageArguments(arguments).build();
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.CalendarFieldBuilder#dateOutOfRangeMessage(com.holonplatform.core.
	 * i18n.Localizable)
	 */
	@Override
	public DateInputBuilder dateOutOfRangeMessage(Localizable dateOutOfRangeMessage) {
		this.outOfRangeMessage = dateOutOfRangeMessage;
		return builder();
	}

}
