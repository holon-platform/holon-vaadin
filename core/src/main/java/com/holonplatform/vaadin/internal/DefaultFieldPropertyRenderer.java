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
package com.holonplatform.vaadin.internal;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.Temporal;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.Locale;

import javax.annotation.Priority;

import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.internal.utils.TypeUtils;
import com.holonplatform.core.presentation.StringValuePresenter;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyRenderer;
import com.holonplatform.core.temporal.TemporalType;
import com.holonplatform.vaadin.components.Components.input;
import com.holonplatform.vaadin.components.Field;
import com.holonplatform.vaadin.components.builders.DateInputBuilder.Resolution;
import com.holonplatform.vaadin.components.builders.TemporalInputBuilder;

/**
 * Default {@link PropertyRenderer} to create {@link Field} type {@link Property} representations.
 * 
 * @param <T> Property type
 *
 * @since 5.0.0
 */
@SuppressWarnings("rawtypes")
@Priority(Integer.MAX_VALUE)
public class DefaultFieldPropertyRenderer<T> implements PropertyRenderer<Field, T> {

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.property.PropertyRenderer#getRenderType()
	 */
	@Override
	public Class<? extends Field> getRenderType() {
		return Field.class;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.property.PropertyRenderer#render(com.holonplatform.core.property.Property)
	 */
	@Override
	public Field render(Property<? extends T> property) {

		ObjectUtils.argumentNotNull(property, "Property must be not null");

		Class<?> propertyType = property.getType();

		// Try to render property according to a supported property type
		if (TypeUtils.isString(propertyType)) {
			// String
			return renderString(property);
		}
		if (TypeUtils.isBoolean(propertyType)) {
			// Boolean
			return renderBoolean(property);
		}
		if (TypeUtils.isEnum(propertyType)) {
			// Enum
			return renderEnum(property);
		}
		if (TypeUtils.isTemporal(propertyType)) {
			// Temporal
			return renderTemporal(property);
		}
		if (TypeUtils.isDate(propertyType)) {
			// Date
			return renderDate(property);
		}
		if (TypeUtils.isNumber(propertyType)) {
			// Number
			return renderNumber(property);
		}

		return null;
	}

	/**
	 * Renders a String value type Field
	 * @param property Property to render
	 * @return Field instance
	 */
	protected Field<T> renderString(Property<? extends T> property) {
		return postProcessField(input.string().emptyValuesAsNull(true).asField(), property);
	}

	/**
	 * Renders a Boolean value type Field
	 * @param property Property to render
	 * @return Field instance
	 */
	protected Field<T> renderBoolean(Property<? extends T> property) {
		return postProcessField(input.boolean_().nullValueAsFalse(true).asField(), property);
	}

	/**
	 * Renders a Enum value type Field
	 * @param property Property to render
	 * @return Field instance
	 */
	@SuppressWarnings("unchecked")
	protected Field<T> renderEnum(Property<? extends T> property) {
		Class<Enum> enumType = (Class<Enum>) property.getType();
		return postProcessField(input.singleSelect(enumType).items(enumType.getEnumConstants()).asField(), property);
	}

	/**
	 * Renders a Temporal value type Field
	 * @param property Property to render
	 * @return Field instance
	 */
	@SuppressWarnings("unchecked")
	protected Field<T> renderTemporal(Property<? extends T> property) {

		TemporalInputBuilder builder = null;

		if (LocalDate.class.isAssignableFrom(property.getType())) {
			builder = input.localDate(false);
		} else if (LocalDateTime.class.isAssignableFrom(property.getType())) {
			builder = input.localDateTime(false);
		} else {
			throw new UnsupportedTemporalTypeException(
					"Temporal type " + property.getType().getName() + " is not supported by default field renderer");
		}

		final TemporalInputBuilder<Temporal, ?> b = builder;

		// set locale from LocalizationContext, if any
		LocalizationContext.getCurrent().filter(l -> l.isLocalized()).flatMap((c) -> c.getLocale())
				.ifPresent((l) -> b.locale(l));

		return postProcessField(b.asField(), property);
	}

	/**
	 * Renders a Date value type Field
	 * @param property Property to render
	 * @return Field instance
	 */
	protected Field<T> renderDate(Property<? extends T> property) {
		final TemporalType type = property.getConfiguration().getTemporalType().orElse(TemporalType.DATE);
		return postProcessField(
				input.date((type == TemporalType.DATE_TIME) ? Resolution.MINUTE : Resolution.DAY, false).asField(),
				property);
	}

	/**
	 * Renders a numeric value type Field
	 * @param property Property to render
	 * @return Field instance
	 */
	@SuppressWarnings("unchecked")
	protected Field<T> renderNumber(Property<? extends T> property) {
		// Number format

		Class<? extends Number> type = (Class<? extends Number>) property.getType();

		int decimals = property.getConfiguration().getParameter(StringValuePresenter.DECIMAL_POSITIONS).orElse(-1);
		boolean disableGrouping = property.getConfiguration().getParameter(StringValuePresenter.DISABLE_GROUPING)
				.orElse(Boolean.FALSE);

		Locale locale = LocalizationContext.getCurrent().filter(l -> l.isLocalized()).flatMap(l -> l.getLocale())
				.orElse(Locale.getDefault());

		NumberFormat numberFormat = LocalizationContext.getCurrent().filter(l -> l.isLocalized())
				.map((l) -> l.getNumberFormat(type, decimals, disableGrouping))
				.orElse(TypeUtils.isDecimalNumber(property.getType()) ? NumberFormat.getNumberInstance(locale)
						: NumberFormat.getIntegerInstance(locale));

		if (decimals > -1) {
			numberFormat.setMinimumFractionDigits(decimals);
			numberFormat.setMaximumFractionDigits(decimals);
		}
		if (disableGrouping) {
			numberFormat.setGroupingUsed(false);
		}

		return postProcessField(input.number(type).numberFormat(numberFormat).asField(), property);
	}

	/**
	 * Post process created {@link Field} for additional setup
	 * @param <F> Field type
	 * @param field Field to process
	 * @param property Property
	 * @return processed field
	 */
	@SuppressWarnings("unchecked")
	protected <F> Field<F> postProcessField(Field field, Property<? extends T> property) {
		// caption
		field.setCaption(LocalizationContext.translate(property, true));
		return field;
	}

}
