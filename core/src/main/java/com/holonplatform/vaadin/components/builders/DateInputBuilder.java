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

import java.util.Date;

import com.holonplatform.vaadin.components.Input;

/**
 * A {@link CalendarInputBuilder} for {@link Date} type {@link Input}s.
 * 
 * @since 5.0.0
 */
public interface DateInputBuilder extends CalendarInputBuilder<Date, DateInputBuilder> {

	public enum Resolution {

		SECOND(true), MINUTE(true), HOUR(true), DAY(false), MONTH(false), YEAR(false);

		private final boolean time;

		private Resolution(boolean time) {
			this.time = time;
		}

		public boolean isTime() {
			return time;
		}

	}

}
