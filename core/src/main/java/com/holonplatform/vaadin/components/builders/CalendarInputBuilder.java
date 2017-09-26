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
package com.holonplatform.vaadin.components.builders;

import java.util.Calendar;
import java.util.TimeZone;

import com.holonplatform.core.Context;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.vaadin.components.Input;

/**
 * An builder to create {@link Input}s rendered as a calendar (inline or in a popup activated by a button) in UI.
 * 
 * @param <T> Value type
 * @param <B> Concrete builder type
 * 
 * @since 5.0.0
 */
public interface CalendarInputBuilder<T, B extends CalendarInputBuilder<T, B>> extends InputBuilder<T, Input<T>, B> {

	/**
	 * Sets date format to use for internal component date display.
	 * @param dateFormat the dateFormat to set
	 * @return this
	 */
	B dateFormat(String dateFormat);

	/**
	 * Sets the default error message used if the DateField cannot parse the text input by user to a Date field.
	 * @param parsingErrorMessage Parse error message
	 * @return this
	 */
	B parseErrorMessage(String parsingErrorMessage);

	/**
	 * Sets the default error message used if the DateField cannot parse the text input by user to a Date field, using a
	 * localizable <code>messageCode</code>.
	 * <p>
	 * For message localization, a {@link LocalizationContext} must be available and localized as {@link Context}
	 * resource when component is built.
	 * </p>
	 * @param defaultParseErrorMessage Default error message if no translation is available for given
	 *        <code>messageCode</code> for current Locale.
	 * @param messageCode Parse error message translation message key
	 * @param arguments Optional translation arguments
	 * @return this
	 */
	B parseErrorMessage(String defaultParseErrorMessage, String messageCode, Object... arguments);

	/**
	 * Sets the default error message used if the DateField cannot parse the text input by user to a Date field, using a
	 * {@link Localizable} message.
	 * <p>
	 * For message localization, a {@link LocalizationContext} must be available and localized as {@link Context}
	 * resource when component is built.
	 * </p>
	 * @param parseErrorMessage Localizable error message
	 * @return this
	 */
	B parseErrorMessage(Localizable parseErrorMessage);

	/**
	 * Specifies whether or not date/time interpretation in component is to be lenient.
	 * @see Calendar#setLenient(boolean)
	 * @param lenient true if the lenient mode is to be turned on; false if it is to be turned off.
	 * @return this
	 */
	B lenient(boolean lenient);

	/**
	 * Sets the visibility of ISO 8601 week numbers in the date selector. ISO 8601 defines that a week always starts
	 * with a Monday so the week numbers are only shown if this is the case.
	 * @param showWeekNumbers true if week numbers should be shown, false otherwise.
	 * @return this
	 */
	B showISOWeekNumbers(boolean showWeekNumbers);

	/**
	 * Sets the time zone used by this date field. The time zone is used to convert the absolute time in a Date object
	 * to a logical time displayed in the selector and to convert the select time back to a Date object. If no time zone
	 * has been set, the current default time zone is used.
	 * @param timeZone the time zone to use for time calculations.
	 * @return this
	 */
	B timeZone(TimeZone timeZone);

	/**
	 * Sets the start range for this component. If the value is set before this date/time, the component will not
	 * validate. If <code>start</code> is set to <code>null</code>, any value before end range will be accepted by the
	 * range.
	 * @param start the allowed range's start date/time (inclusive)
	 * @return this
	 */
	B rangeStart(T start);

	/**
	 * Sets the end range for this component. If the value is set after this date/time, the component will not validate.
	 * If <code>end</code> is set to <code>null</code>, any value after start range will be accepted by the range.
	 * @param end the allowed range's end date (inclusive)
	 * @return this
	 */
	B rangeEnd(T end);

	/**
	 * Sets the error message to notify if the range validation fails.
	 * @param dateOutOfRangeMessage Localizable message which is shown when value (the date) is set outside allowed
	 *        range
	 * @return this
	 */
	B dateOutOfRangeMessage(String dateOutOfRangeMessage);

	/**
	 * Sets the error message to notify if the range validation fails, using a localizable <code>messageCode</code>.
	 * <p>
	 * For caption localization, a {@link LocalizationContext} must be available and localized as {@link Context}
	 * resource when component is built.
	 * </p>
	 * @param defaultDateOutOfRangeMessage Default error message if no translation is available for given
	 *        <code>messageCode</code> for current Locale.
	 * @param messageCode Date out of range message translation message key
	 * @param arguments Optional translation arguments
	 * @return this
	 */
	B dateOutOfRangeMessage(String defaultDateOutOfRangeMessage, String messageCode, Object... arguments);

	/**
	 * Sets the error message to notify if the range validation fails, using a {@link Localizable} message.
	 * <p>
	 * For caption localization, a {@link LocalizationContext} must be available and localized as {@link Context}
	 * resource when component is built.
	 * </p>
	 * @param dateOutOfRangeMessage Localizable error message
	 * @return this
	 */
	B dateOutOfRangeMessage(Localizable dateOutOfRangeMessage);

}
