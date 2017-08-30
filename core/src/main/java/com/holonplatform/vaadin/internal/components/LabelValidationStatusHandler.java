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

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.components.ValidationStatusHandler;
import com.vaadin.ui.Label;

/**
 * A {@link ValidationStatusHandler} which uses a {@link Label} to notify validation errors.
 *
 * @since 5.0.0
 */
public class LabelValidationStatusHandler implements ValidationStatusHandler {

	private final Label label;
	private final boolean hideWhenValid;

	/**
	 * Constructor
	 * @param label Status label (not null)
	 * @param hideWhenValid <code>true</code> to hide the Label when the validation status is not invalid
	 */
	public LabelValidationStatusHandler(Label label, boolean hideWhenValid) {
		super();
		ObjectUtils.argumentNotNull(label, "Status label must be not null");
		this.label = label;
		this.hideWhenValid = hideWhenValid;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValidationStatusHandler#validationStatusChange(com.holonplatform.vaadin.
	 * components.ValidationStatusHandler.ValidationStatusEvent)
	 */
	@Override
	public void validationStatusChange(ValidationStatusEvent<?> statusChangeEvent) {
		final String error = statusChangeEvent.getErrorMessage();
		label.setValue((error != null) ? error : "");
		if (hideWhenValid) {
			// Only show the label when validation has failed
			label.setVisible(statusChangeEvent.isInvalid());
		}
	}

}
