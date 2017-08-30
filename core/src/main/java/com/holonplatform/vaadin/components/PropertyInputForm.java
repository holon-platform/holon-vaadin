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
package com.holonplatform.vaadin.components;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.vaadin.components.builders.ComponentBuilder;
import com.holonplatform.vaadin.internal.components.DefaultPropertyInputForm;
import com.vaadin.ui.Component;

/**
 * A {@link PropertyInputGroup} component to display the property {@link Input}s on a layout, using the
 * {@link ComposableComponent} composition strategy.
 * <p>
 * A {@link Composer} is used to draw the form UI.
 * </p>
 * 
 * @since 5.0.0
 */
public interface PropertyInputForm extends ComposableComponent, ValueComponent<PropertyBox>, PropertyInputGroup {

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyInputGroup#getValue()
	 */
	@Override
	default PropertyBox getValue() {
		return PropertyInputGroup.super.getValue();
	}

	/**
	 * Get a builder to create a {@link PropertyInputForm}.
	 * @param <C> Form content component type
	 * @param content Form content, where {@link Input}s will be composed by the form {@link Composer} (not null)
	 * @return {@link PropertyInputForm} builder
	 */
	static <C extends Component> PropertyInputFormBuilder<C> builder(C content) {
		ObjectUtils.argumentNotNull(content, "Form content must be not null");
		return new DefaultPropertyInputForm.DefaultBuilder<>(content);
	}

	/**
	 * {@link PropertyInputForm} builder.
	 * @param <C> Form content component type
	 */
	public interface PropertyInputFormBuilder<C extends Component>
			extends ComposableComponent.Builder<PropertyValueComponentSource, C, PropertyInputFormBuilder<C>>,
			PropertyInputGroup.Builder<PropertyInputForm, PropertyInputFormBuilder<C>>,
			ComponentBuilder<PropertyInputForm, PropertyInputFormBuilder<C>> {

		/**
		 * Set the caption for the input component bound to given property. By default, the caption is obtained from
		 * {@link Property} itself (which is {@link Localizable}).
		 * @param property Property for which to set the input caption (not null)
		 * @param caption Localizable input caption
		 * @return this
		 */
		PropertyInputFormBuilder<C> propertyCaption(Property<?> property, Localizable caption);

		/**
		 * Set the caption for the input component bound to given property. By default, the caption is obtained from
		 * {@link Property} itself (which is {@link Localizable}).
		 * @param property Property for which to set the input caption (not null)
		 * @param caption Input component caption
		 * @return this
		 */
		PropertyInputFormBuilder<C> propertyCaption(Property<?> property, String caption);

		/**
		 * Set the caption for the input component bound to given property. By default, the caption is obtained from
		 * {@link Property} itself (which is {@link Localizable}).
		 * @param property Property for which to set the input caption (not null)
		 * @param defaultCaption Default caption if no translation is available for given <code>messageCode</code> for
		 *        current Locale, or no {@link LocalizationContext} is available at all
		 * @param messageCode Caption translation message key
		 * @param arguments Optional translation arguments
		 * @return this
		 */
		PropertyInputFormBuilder<C> propertyCaption(Property<?> property, String defaultCaption, String messageCode,
				Object... arguments);

		/**
		 * Set the caption for the input component bound to given property as hidden.
		 * @param property Property for which to hide the input caption (not null)
		 * @return this
		 */
		PropertyInputFormBuilder<C> hidePropertyCaption(Property<?> property);

	}

}
