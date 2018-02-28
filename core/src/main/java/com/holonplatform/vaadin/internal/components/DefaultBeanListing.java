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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.holonplatform.core.beans.BeanPropertySet;
import com.holonplatform.core.internal.Logger;
import com.holonplatform.core.property.PathProperty;
import com.holonplatform.vaadin.components.BeanListing;
import com.holonplatform.vaadin.components.Field;
import com.holonplatform.vaadin.internal.VaadinLogger;
import com.vaadin.data.HasValue;
import com.vaadin.data.ValueProvider;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FontIcon;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.renderers.ImageRenderer;
import com.vaadin.ui.renderers.Renderer;

/**
 * Default {@link BeanListing} implementation.
 * 
 * @param <T> Bean type
 *
 * @since 5.0.0
 */
public class DefaultBeanListing<T> extends DefaultItemListing<T, String> implements BeanListing<T> {

	private static final long serialVersionUID = 2163943323958351369L;

	private static final Logger LOGGER = VaadinLogger.create();

	protected final BeanPropertySet<T> beanPropertySet;

	/**
	 * Cosntructor.
	 * @param beanType Bean class (not null)
	 */
	public DefaultBeanListing(Class<T> beanType) {
		super(new Grid<>(beanType));
		this.beanPropertySet = BeanPropertySet.create(beanType);
		// init captions
		getGrid().getColumns().forEach(c -> {
			String id = c.getId();
			getBeanProperty(id).ifPresent(p -> getPropertyColumn(id).setCaption(p));
		});
	}

	/**
	 * Get the bean property with given <code>propertyId</code>.
	 * @param propertyId Property id
	 * @return The bean property with given id, or an empty Optional if not found
	 */
	protected Optional<PathProperty<Object>> getBeanProperty(String propertyId) {
		if (propertyId == null) {
			return Optional.empty();
		}
		return beanPropertySet.getProperty(propertyId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.DefaultItemListing#getColumnId(java.lang.Object)
	 */
	@Override
	public String getColumnId(String property) {
		return property;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.DefaultItemListing#getColumnProperty(java.lang.String)
	 */
	@Override
	protected String getColumnProperty(String columnId) {
		return columnId;
	}

	@Override
	protected Optional<ValueProvider<?, ?>> getDefaultPropertyPresenter(String property) {
		Optional<PathProperty<Object>> beanProperty = getBeanProperty(property);
		if (beanProperty.isPresent()) {
			if (FontIcon.class.isAssignableFrom(beanProperty.get().getType())) {
				return Optional.of(v -> ((FontIcon) v).getHtml());
			}
			return Optional.of(v -> beanProperty.get().present(v));
		}
		return super.getDefaultPropertyPresenter(property);
	}

	@Override
	protected Optional<Renderer<?>> getDefaultPropertyRenderer(String property) {
		Optional<PathProperty<Object>> beanProperty = getBeanProperty(property);
		if (beanProperty.isPresent()) {
			if (Component.class.isAssignableFrom(beanProperty.get().getType())) {
				return Optional.of(new ComponentRenderer());
			}
			if (FontIcon.class.isAssignableFrom(beanProperty.get().getType())) {
				return Optional.of(new HtmlRenderer(""));
			}
			if (ExternalResource.class.isAssignableFrom(beanProperty.get().getType())
					|| ThemeResource.class.isAssignableFrom(beanProperty.get().getType())) {
				return Optional.of(new ImageRenderer<>());
			}
		}
		return super.getDefaultPropertyRenderer(property);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.DefaultItemListing#getDefaultPropertyEditor(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected <E extends HasValue<?> & Component> Optional<E> getDefaultPropertyEditor(String property) {
		E field = null;
		try {
			field = (E) getBeanProperty(property).flatMap(p -> p.renderIfAvailable(Field.class)).orElse(null);
		} catch (Exception e) {
			if (isEditable()) {
				LOGGER.warn("No default property editor available for property [" + property + "]", e);
			}
		}
		if (field != null) {
			return Optional.of(field);
		}
		return super.getDefaultPropertyEditor(property);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.internal.components.DefaultItemListing#getDefaultPropertyValidators(java.lang.Object)
	 */
	@Override
	protected Collection<com.holonplatform.core.Validator<?>> getDefaultPropertyValidators(String property) {
		Collection<com.holonplatform.core.Validator<Object>> validators = getBeanProperty(property)
				.map(p -> p.getValidators()).orElse(Collections.emptySet());
		if (validators.isEmpty()) {
			return Collections.emptySet();
		}
		List<com.holonplatform.core.Validator<?>> vs = new ArrayList<>(validators.size());
		validators.forEach(v -> vs.add(v));
		return vs;
	}

}
