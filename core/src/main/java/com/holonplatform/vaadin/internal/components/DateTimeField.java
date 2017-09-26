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
package com.holonplatform.vaadin.internal.components;

import java.time.LocalDateTime;
import java.util.Date;

import com.holonplatform.vaadin.components.Field;
import com.holonplatform.vaadin.components.Input;
import com.holonplatform.vaadin.components.builders.DateInputBuilder;
import com.holonplatform.vaadin.components.builders.DateInputBuilder.Resolution;
import com.holonplatform.vaadin.internal.components.builders.AbstractDateFieldBuilder;
import com.holonplatform.vaadin.internal.converters.DateToLocalDateTimeConverter;
import com.holonplatform.vaadin.internal.converters.TimeZonedConverter;
import com.vaadin.data.ValueContext;
import com.vaadin.shared.ui.datefield.DateTimeResolution;
import com.vaadin.ui.AbstractDateField;
import com.vaadin.ui.AbstractLocalDateTimeField;
import com.vaadin.ui.InlineDateTimeField;

/**
 * A {@link Date} type {@link Input} field.
 * 
 * @since 5.0.0
 */
public class DateTimeField extends AbstractCalendarField<LocalDateTime> {

	private static final long serialVersionUID = -4038430896229576972L;

	private final TimeZonedConverter<Date, LocalDateTime> converter;

	public DateTimeField(Resolution resolution, boolean inline) {
		super(resolution, inline);
		converter = new DateToLocalDateTimeConverter();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.AbstractCustomField#buildInternalField(java.lang.Class)
	 */
	@Override
	protected AbstractDateField<LocalDateTime, ?> buildInternalField(Class<? extends Date> type) {
		AbstractLocalDateTimeField field = isInline() ? new InlineDateTimeField() : new com.vaadin.ui.DateTimeField();
		if (getResolution() == Resolution.HOUR) {
			field.setResolution(DateTimeResolution.HOUR);
		} else if (getResolution() == Resolution.SECOND) {
			field.setResolution(DateTimeResolution.SECOND);
		} else {
			field.setResolution(DateTimeResolution.MINUTE);
		}
		return field;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.AbstractCalendarField#getConverter()
	 */
	@Override
	protected TimeZonedConverter<Date, LocalDateTime> getConverter() {
		return converter;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.AbstractCustomField#fromInternalValue(java.lang.Object)
	 */
	@Override
	protected Date fromInternalValue(LocalDateTime value) {
		return getConverter().convertToPresentation(value, new ValueContext(findLocale()));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.AbstractCustomField#toInternalValue(java.lang.Object)
	 */
	@Override
	protected LocalDateTime toInternalValue(Date value) {
		return getConverter().convertToModel(value, new ValueContext(findLocale()))
				.getOrThrow(msg -> new IllegalArgumentException(msg));
	}

	/**
	 * Builder to create {@link DateTimeField} instances
	 */
	public static class Builder extends AbstractDateFieldBuilder<DateTimeField> {

		public Builder(Resolution resolution, boolean inline) {
			super(new DateTimeField(resolution, inline));
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentBuilder#builder()
		 */
		@Override
		protected DateInputBuilder builder() {
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.internal.components.builders.AbstractFieldBuilder#build(com.vaadin.ui.AbstractField)
		 */
		@Override
		protected Input<Date> build(DateTimeField instance) {
			return instance;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.internal.components.builders.AbstractFieldBuilder#buildAsField(com.vaadin.ui.
		 * AbstractField)
		 */
		@Override
		protected Field<Date> buildAsField(DateTimeField instance) {
			return instance;
		}

	}

}
