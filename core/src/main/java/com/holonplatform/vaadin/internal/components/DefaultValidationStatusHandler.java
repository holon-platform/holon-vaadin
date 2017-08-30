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

import com.holonplatform.vaadin.components.ValidationStatusHandler;
import com.holonplatform.vaadin.components.ValueComponent;
import com.vaadin.server.UserError;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;

/**
 * Default {@link ValidationStatusHandler} implementation using
 * {@link AbstractComponent#setComponentError(com.vaadin.server.ErrorMessage)} to notify the validation status.
 * 
 * @since 5.0.0
 */
public class DefaultValidationStatusHandler implements ValidationStatusHandler {

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValidationStatusHandler#validationStatusChange(com.holonplatform.vaadin.
	 * components.ValidationStatusHandler.ValidationStatusEvent)
	 */
	@Override
	public void validationStatusChange(final ValidationStatusEvent<?> statusChangeEvent) {
		statusChangeEvent.getSource().ifPresent(c -> {
			setComponentError(c, statusChangeEvent.isInvalid() ? statusChangeEvent.getErrorMessage() : null);
		});
	}

	private static void setComponentError(ValueComponent<?> valueComponent, String error) {
		final Component c = valueComponent.getComponent();
		if (c != null && c instanceof AbstractComponent) {
			((AbstractComponent) c).setComponentError((error == null) ? null : new UserError(error));
		}
	}

}
