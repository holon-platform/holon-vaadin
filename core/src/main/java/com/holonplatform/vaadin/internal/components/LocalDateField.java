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
package com.holonplatform.vaadin.internal.components;

import java.time.LocalDate;

import com.holonplatform.vaadin.components.Field;
import com.holonplatform.vaadin.components.Input;
import com.holonplatform.vaadin.components.builders.TemporalInputBuilder.TemporalWithoutTimeFieldBuilder;
import com.vaadin.ui.AbstractDateField;
import com.vaadin.ui.DateField;
import com.vaadin.ui.InlineDateField;

/**
 * Temporal Field implementation handling {@link LocalDate} value types.
 * 
 * <p>
 * This field is rendered using a {@link DateField} or an {@link InlineDateField} with DAY resolution.
 * </p>
 * 
 * @since 5.0.0
 */
public class LocalDateField extends AbstractTemporalField<LocalDate> {

	private static final long serialVersionUID = -360868004192038841L;

	/**
	 * Constructor
	 * @param inline <code>true</code> to render the field using and inline calendar input
	 */
	public LocalDateField(boolean inline) {
		this(inline, null);
	}

	/**
	 * Constructor
	 * @param inline <code>true</code> to render the field using and inline calendar input
	 * @param caption Field caption
	 */
	public LocalDateField(boolean inline, String caption) {
		super(LocalDate.class, inline);
		setCaption(caption);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.AbstractCustomField#buildInternalField(java.lang.Class)
	 */
	@Override
	protected AbstractDateField<LocalDate, ?> buildInternalField(Class<? extends LocalDate> type) {
		return isInline() ? new InlineDateField() : new DateField();
	}

	// Builder

	public static class Builder
			extends AbstractTemporalFieldBuilder<LocalDate, LocalDateField, TemporalWithoutTimeFieldBuilder<LocalDate>>
			implements TemporalWithoutTimeFieldBuilder<LocalDate> {

		/**
		 * Constructor
		 * @param inline True to render the field as an inline calendar
		 */
		public Builder(boolean inline) {
			super(new LocalDateField(inline));
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentBuilder#builder()
		 */
		@Override
		protected TemporalWithoutTimeFieldBuilder<LocalDate> builder() {
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.internal.components.builders.AbstractFieldBuilder#build(com.vaadin.ui.AbstractField)
		 */
		@Override
		protected Input<LocalDate> build(LocalDateField instance) {
			return instance;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.internal.components.builders.AbstractFieldBuilder#buildAsField(com.vaadin.ui.
		 * AbstractField)
		 */
		@Override
		protected Field<LocalDate> buildAsField(LocalDateField instance) {
			return instance;
		}

	}

}
