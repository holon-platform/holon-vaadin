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

import com.vaadin.shared.ui.ValueChangeMode;

/**
 * Allows to configure the {@link ValueChangeMode} and timeout for components which supports it.
 *
 * @param <B> Concrete configurator type
 *
 * @since 5.1.0
 */
public interface ValueChangeModeConfigurator<B extends ValueChangeModeConfigurator<B>> {

	/**
	 * Sets the mode how value change events are triggered.
	 * @param valueChangeMode the value change mode to set (not null)
	 * @return this
	 */
	B valueChangeMode(ValueChangeMode valueChangeMode);
	
	/**
	 * Sets how often value change events are triggered when the {@link ValueChangeMode} is set to either
	 * {@link ValueChangeMode#LAZY} or {@link ValueChangeMode#TIMEOUT}.
	 * @param valueChangeTimeout the timeout in milliseconds, (greater or equal to 0)
	 * @see #valueChangeMode(ValueChangeMode)
	 * @return this
	 */
	B valueChangeTimeout(int valueChangeTimeout);

}
