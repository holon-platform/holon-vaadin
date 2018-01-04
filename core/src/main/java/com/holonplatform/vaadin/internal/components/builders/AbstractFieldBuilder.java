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
package com.holonplatform.vaadin.internal.components.builders;

import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.holonplatform.vaadin.components.Field;
import com.holonplatform.vaadin.components.Input;
import com.holonplatform.vaadin.components.ValidatableInput;
import com.holonplatform.vaadin.components.ValueHolder;
import com.holonplatform.vaadin.components.ValueHolder.ValueChangeListener;
import com.holonplatform.vaadin.components.builders.InputBuilder;
import com.holonplatform.vaadin.components.builders.ValidatableInputBuilder;
import com.holonplatform.vaadin.internal.components.ValueChangeListenerUtils;
import com.vaadin.data.HasValue;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.dnd.DragSourceExtension;
import com.vaadin.ui.dnd.DropTargetExtension;

/**
 * Base {@link InputBuilder} class
 * 
 * @param <T> Field type
 * @param <C> Internal field type
 * @param <B> Concrete builder type
 * 
 * @since 5.0.0
 */
public abstract class AbstractFieldBuilder<T, C extends Input<T>, I extends AbstractField<T>, B extends InputBuilder<T, C, B>>
		extends AbstractLocalizableComponentConfigurator<I, B> implements InputBuilder<T, C, B> {

	protected DropTargetExtension<I> dropTargetExtension;
	protected BiConsumer<DropTargetExtension<? extends AbstractComponent>, C> dropTargetConfigurator;

	public AbstractFieldBuilder(I instance) {
		super(instance);
	}

	/**
	 * Build concrete instance in expected type
	 * @param instance Building instance
	 * @return Instance in expected type
	 */
	protected abstract C build(I instance);

	/**
	 * Build the concrete instance as a {@link HasValue} component.
	 * @param instance Building instance
	 * @return {@link HasValue} instance
	 */
	protected abstract Field<T> buildAsField(I instance);

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
	public B withValueChangeListener(ValueChangeListener<T> listener) {
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

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.InputBuilder#dragSource(java.util.function.Consumer)
	 */
	@Override
	public B dragSource(Consumer<DragSourceExtension<? extends AbstractComponent>> configurator) {
		final DragSourceExtension<I> extension = new DragSourceExtension<>(getInstance());
		if (configurator != null) {
			configurator.accept(extension);
		}
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.InputBuilder#dropTarget(java.util.function.BiConsumer)
	 */
	@Override
	public B dropTarget(BiConsumer<DropTargetExtension<? extends AbstractComponent>, C> configurator) {
		this.dropTargetExtension = new DropTargetExtension<>(getInstance());
		this.dropTargetConfigurator = configurator;
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ComponentBuilder#deferLocalization()
	 */
	@Override
	public B deferLocalization() {
		this.deferLocalization = true;
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ComponentBuilder#build()
	 */
	@Override
	public C build() {
		C component = build(setupLocalization(instance));
		// setup drop target extension
		if (dropTargetExtension != null && dropTargetConfigurator != null) {
			dropTargetConfigurator.accept(dropTargetExtension, component);
		}
		return component;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.InputBuilder#asField()
	 */
	@Override
	public Field<T> asField() {
		return buildAsField(setupLocalization(instance));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.InputBuilder#validatable()
	 */
	@Override
	public ValidatableInputBuilder<T, ValidatableInput<T>> validatable() {
		return new DefaultValidatableInputBuilder<>(build());
	}

}
