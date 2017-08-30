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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import com.holonplatform.core.Path;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.core.property.Property;
import com.holonplatform.vaadin.components.ComposableComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Panel;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Base {@link ComposableComponent} form.
 * 
 * @param <C> Content component type
 * @param <S> Components source type
 * 
 * @since 5.0.0
 */
public abstract class AbstractComposableForm<C extends Component, S> extends Panel implements ComposableComponent {

	private static final long serialVersionUID = 6196476129131362753L;

	/**
	 * Form content initializer
	 */
	private Consumer<C> initializer;

	/**
	 * Composer
	 */
	private Composer<? super C, S> composer;

	/**
	 * Compose on attach behaviour
	 */
	private boolean composeOnAttach = true;

	/**
	 * Composition state
	 */
	private boolean composed = false;

	/**
	 * Custom property captions
	 */
	private Map<Property<?>, Localizable> propertyCaptions = new HashMap<>(8);

	/**
	 * Hidden property captions
	 */
	private Collection<Property<?>> hiddenPropertyCaptions = new HashSet<>(8);

	/**
	 * Constructor
	 */
	public AbstractComposableForm() {
		this(null);
	}

	/**
	 * Constructor with form content
	 * @param content Form composition content
	 */
	public AbstractComposableForm(C content) {
		super();
		if (content != null) {
			setContent(content);
		}
		addStyleName(ValoTheme.PANEL_BORDERLESS);
		// default style name
		addStyleName("h-form");
	}

	protected abstract S getComponentSource();

	/**
	 * Get the form content initializer
	 * @return the form content initializer
	 */
	public Optional<Consumer<C>> getInitializer() {
		return Optional.ofNullable(initializer);
	}

	/**
	 * Set the form content initializer
	 * @param initializer the initializer to set
	 */
	public void setInitializer(Consumer<C> initializer) {
		this.initializer = initializer;
	}

	/**
	 * Get the composer
	 * @return the composer
	 */
	public Composer<? super C, S> getComposer() {
		return composer;
	}

	/**
	 * Set the composer
	 * @param composer the composer to set
	 */
	public void setComposer(Composer<? super C, S> composer) {
		this.composer = composer;
	}

	/**
	 * Gets whether the form must be composed on {@link #attach()}, if not already composed invoking {@link #compose()}.
	 * @return <code>true</code> if the form must be composed on {@link #attach()}
	 */
	public boolean isComposeOnAttach() {
		return composeOnAttach;
	}

	/**
	 * Sets whether the form must be composed on {@link #attach()}, if not already composed invoking {@link #compose()}.
	 * @param composeOnAttach <code>true</code> to compose the form on {@link #attach()}
	 */
	public void setComposeOnAttach(boolean composeOnAttach) {
		this.composeOnAttach = composeOnAttach;
	}

	/**
	 * Set the caption for the component bound to given property
	 * @param property Property
	 * @param caption Localizable caption
	 */
	protected void setPropertyCaption(Property<?> property, Localizable caption) {
		if (property != null && caption != null) {
			propertyCaptions.put(property, caption);
		}
	}

	/**
	 * Set the caption for the component bound to given property as hidden
	 * @param property Property
	 */
	protected void hidePropertyCaption(Property<?> property) {
		if (property != null) {
			hiddenPropertyCaptions.add(property);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ComposableComponent#compose()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void compose() {
		if (getContent() == null) {
			throw new IllegalStateException("Missing form content");
		}
		if (getComposer() == null) {
			throw new IllegalStateException("Missing form composer");
		}

		C content;
		try {
			content = (C) getContent();
		} catch (Exception e) {
			throw new IllegalStateException("Form content is not of expected type", e);
		}

		// init form content
		getInitializer().ifPresent(i -> i.accept(content));

		// compose
		getComposer().compose(content, getComponentSource());

		this.composed = true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.ui.AbstractComponent#attach()
	 */
	@Override
	public void attach() {
		super.attach();

		// check compose on attach
		if (!composed) {
			compose();
		}
	}

	/**
	 * Configure given component using given property.
	 * @param property Property to which the component refers
	 * @param component Component to configure
	 */
	protected void configureComponent(Property<?> property, Component component) {

		if (hiddenPropertyCaptions.contains(property)) {
			component.setCaption(null);
		} else {
			if (propertyCaptions.containsKey(property)) {
				component.setCaption(LocalizationContext.translate(propertyCaptions.get(property), true));
			} else {
				if (component.getCaption() == null) {
					if (Path.class.isAssignableFrom(property.getClass())) {
						component.setCaption(((Path<?>) property).getName());
					} else {
						component.setCaption(property.toString());
					}
				}
			}
		}
	}

}
