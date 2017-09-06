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
package com.holonplatform.vaadin.internal.components.builders;

import java.util.Locale;

import com.holonplatform.vaadin.components.Input;
import com.holonplatform.vaadin.components.ValueHolder;
import com.holonplatform.vaadin.components.builders.InputConfigurator;
import com.holonplatform.vaadin.internal.components.ValueChangeListenerUtils;
import com.vaadin.ui.AbstractField;

/**
 * Base {@link InputConfigurator} implementation.
 * 
 * @param <T> Field type
 * @param <C> Internal field type
 * @param <B> Concrete configurator type
 * 
 * @since 5.0.0
 */
public abstract class AbstractFieldConfigurator<T, C extends AbstractField<T>, B extends InputConfigurator<T, B>>
		extends AbstractComponentConfigurator<C, B> implements InputConfigurator<T, B> {

	/**
	 * Constructor
	 * @param instance Instance to configure (not null)
	 */
	public AbstractFieldConfigurator(C instance) {
		super(instance);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.InputConfigurator#readOnly()
	 */
	@Override
	public B readOnly() {
		getInstance().setReadOnly(true);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.FieldBuilder#tabIndex(int)
	 */
	@Override
	public B tabIndex(int tabIndex) {
		getInstance().setTabIndex(tabIndex);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.FieldBuilder#withValue(java.lang.Object)
	 */
	@Override
	public B withValue(T value) {
		getInstance().setValue(value);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.InputConfigurator#withValueChangeListener(com.holonplatform.vaadin.
	 * components.ValueHolder.ValueChangeListener)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public B withValueChangeListener(com.holonplatform.vaadin.components.ValueHolder.ValueChangeListener<T> listener) {
		if (ValueHolder.class.isAssignableFrom(getInstance().getClass())) {
			((ValueHolder<T>) getInstance()).addValueChangeListener(listener);
		} else {
			ValueChangeListenerUtils.adapt(getInstance(), Input.from(getInstance()), listener);
		}
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.FieldBuilder#locale(java.util.Locale)
	 */
	@Override
	public B locale(Locale locale) {
		getInstance().setLocale(locale);
		return builder();
	}

}
