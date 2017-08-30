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
package com.holonplatform.vaadin.components.builders;

import java.time.temporal.Temporal;

import com.holonplatform.vaadin.components.Input;

/**
 * Builder to create {@link Temporal} type {@link Input}s backed by a calendar widget.
 * 
 * @param <T> Temporal type
 * @param <B> Concrete builder type
 * 
 * @since 5.0.0
 */
public interface TemporalInputBuilder<T extends Temporal, B extends TemporalInputBuilder<T, B>>
		extends CalendarInputBuilder<T, B> {
	
	/**
	 * A {@link TemporalInputBuilder} for Fields not supporting time.
	 * 
	 * @since 5.0.0
	 */
	public interface TemporalWithoutTimeFieldBuilder<T extends Temporal>
			extends TemporalInputBuilder<T, TemporalWithoutTimeFieldBuilder<T>> {

	}

	/**
	 * A {@link TemporalInputBuilder} for Fields supporting time.
	 * 
	 * @since 5.0.0
	 */
	public interface TemporalWithTimeFieldBuilder<T extends Temporal>
			extends TemporalInputBuilder<T, TemporalWithTimeFieldBuilder<T>> {

		/**
		 * Sets to show and handle time seconds
		 * @return this
		 */
		TemporalWithTimeFieldBuilder<T> showSeconds();

	}

}
