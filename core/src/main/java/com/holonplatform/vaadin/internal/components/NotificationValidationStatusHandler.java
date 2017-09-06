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

import java.util.stream.Collectors;

import com.holonplatform.vaadin.components.ValidationStatusHandler;
import com.vaadin.server.Page;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

/**
 * A {@link ValidationStatusHandler} which uses an error {@link Notification} to notify validation errors.
 *
 * @since 5.0.0
 */
public class NotificationValidationStatusHandler implements ValidationStatusHandler {

	private final Notification notification;
	private final boolean showAllErrors;

	/**
	 * Constructor
	 * @param notification The notification instance to use, <code>null</code> for default
	 * @param showAllErrors <code>true</code> to display all validation errors, <code>false</code> to show only the
	 *        first
	 */
	public NotificationValidationStatusHandler(Notification notification, boolean showAllErrors) {
		super();
		this.notification = notification;
		this.showAllErrors = showAllErrors;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValidationStatusHandler#validationStatusChange(com.holonplatform.vaadin.
	 * components.ValidationStatusHandler.ValidationStatusEvent)
	 */
	@Override
	public void validationStatusChange(ValidationStatusEvent<?> statusChangeEvent) {
		if (statusChangeEvent.isInvalid()) {

			String error = showAllErrors
					? statusChangeEvent.getErrorMessages().stream().collect(Collectors.joining("<br/>"))
					: statusChangeEvent.getErrorMessage();

			if (error == null || error.trim().equals("")) {
				error = "Validation error";
			}

			if (notification != null) {
				notification.setCaption(error);
				notification.show(Page.getCurrent());
			} else {
				Notification.show(error, Type.ERROR_MESSAGE);
			}

		}
	}

}
