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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.property.Property;
import com.holonplatform.vaadin.components.ValidationStatusHandler.Status;
import com.holonplatform.vaadin.components.ValidationStatusHandler.ValidationStatusEvent;
import com.holonplatform.vaadin.components.ValueComponent;

/**
 * Default {@link ValidationStatusEvent} implementation.
 *
 * @param <V> Validation value type
 *
 * @since 5.0.0
 */
public class DefaultValidationStatusEvent<V> implements ValidationStatusEvent<V> {

	private static final long serialVersionUID = -2504760138806763843L;

	private final Status status;
	private final List<Localizable> errors;
	private final ValueComponent<V> source;
	private final Property<V> property;

	public DefaultValidationStatusEvent(Status status, List<Localizable> errors, ValueComponent<V> source,
			Property<V> property) {
		super();
		this.status = status;
		this.errors = errors;
		this.source = source;
		this.property = property;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValidationStatusHandler.ValidationStatusEvent#getStatus()
	 */
	@Override
	public Status getStatus() {
		return status;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValidationStatusHandler.ValidationStatusEvent#getErrors()
	 */
	@Override
	public List<Localizable> getErrors() {
		return (errors != null) ? errors : Collections.emptyList();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValidationStatusHandler.ValidationStatusEvent#getProperty()
	 */
	@Override
	public Optional<Property<V>> getProperty() {
		return Optional.ofNullable(property);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValidationStatusHandler.ValidationStatusEvent#getSource()
	 */
	@Override
	public Optional<ValueComponent<V>> getSource() {
		return Optional.ofNullable(source);
	}

}
