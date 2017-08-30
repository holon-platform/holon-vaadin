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

import java.io.Serializable;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.components.ValueHolder;
import com.holonplatform.vaadin.components.ValueHolder.ValueChangeListener;
import com.vaadin.data.HasValue;
import com.vaadin.shared.Registration;

/**
 * Utility class for {@link ValueChangeListener} handling.
 * 
 * @since 5.0.0
 */
public final class ValueChangeListenerUtils implements Serializable {

	private static final long serialVersionUID = 8205409415837314274L;

	private ValueChangeListenerUtils() {
	}

	public static <V> Registration adapt(HasValue<V> field, ValueHolder<V> valueHolder,
			ValueChangeListener<V> listener) {
		ObjectUtils.argumentNotNull(listener, "ValueChangeListener must be not null");
		return field.addValueChangeListener(
				e -> listener.valueChange(new DefaultValueChangeEvent<>(valueHolder, e.getOldValue(), e.getValue())));
	}

}
