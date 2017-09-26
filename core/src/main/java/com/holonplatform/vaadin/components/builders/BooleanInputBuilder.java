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

import com.holonplatform.vaadin.components.Input;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusListener;

/**
 * Builder to create {@link Boolean} type {@link Input} instances.
 * 
 * @since 5.0.0
 */
public interface BooleanInputBuilder extends InputBuilder<Boolean, Input<Boolean>, BooleanInputBuilder> {

	/**
	 * Sets whether to treat <code>null</code> values as {@link Boolean#FALSE} values
	 * @param nullValueAsFalse <code>true</code> to treat <code>null</code> values as {@link Boolean#FALSE} values
	 * @return this
	 */
	BooleanInputBuilder nullValueAsFalse(boolean nullValueAsFalse);

	/**
	 * Add a listener for focus gained events
	 * @param listener Listener to add
	 * @return this
	 */
	BooleanInputBuilder withFocusListener(FocusListener listener);

	/**
	 * Add a listener for focus lost events
	 * @param listener Listener to add
	 * @return this
	 */
	BooleanInputBuilder withBlurListener(BlurListener listener);

}
