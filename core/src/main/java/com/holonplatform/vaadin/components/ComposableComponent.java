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
package com.holonplatform.vaadin.components;

import java.util.function.Consumer;

import com.holonplatform.vaadin.internal.components.DefaultComponentContainerComposer;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;

/**
 * A {@link Component} which supports a {@link Composer} delegate to compose a set of components into its content
 * component.
 * 
 * @since 5.0.0
 */
public interface ComposableComponent extends Component {

	/**
	 * Enumeration of composed Components width setup mode.
	 */
	public enum ComponentsWidthMode {

		/**
		 * The width of each component will not be altered
		 */
		NONE,

		/**
		 * The width of each component will adjusted according to parent layout width: if the parent layout is 100%
		 * wide, the components width will be setted to 100%
		 */
		AUTO,

		/**
		 * The width of each component will always be setted to 100%
		 */
		FULL;

	}

	/**
	 * Get the composed Components width setup mode.
	 * @return the composed Components width setup mode
	 */
	ComponentsWidthMode getComponentsWidthMode();

	/**
	 * Get the form content component
	 * @return Form content component
	 */
	Component getContent();

	/**
	 * Compose the components into the content component using the previously configured {@link Composer}.
	 * @throws IllegalStateException If the content component or the {@link Composer} are not configured (null)
	 */
	void compose();

	/**
	 * Delegate interface to compose a set of components from a component source into a {@link Component} type content.
	 * 
	 * @param <C> Content component type
	 * @param <S> Components source
	 */
	@FunctionalInterface
	public interface Composer<C extends Component, S extends PropertyComponentSource> {

		/**
		 * Compose the components provided by given <code>source</code> into given <code>content</code> component.
		 * @param content Content on which to compose the components (never null)
		 * @param source Components source (never null)
		 */
		void compose(C content, S source);

	}

	/**
	 * Base {@link ComposableComponent} builder.
	 *
	 * @param <S> Components source type
	 * @param <C> Content component type
	 * @param <B> Concrete builder type
	 */
	public interface Builder<S extends PropertyComponentSource, C extends Component, B extends Builder<S, C, B>> {

		/**
		 * Set a content initializer to setup the content component. This initiliazer is called every time the content
		 * composition is triggered.
		 * @param initializer Content initializer (not null)
		 * @return this
		 */
		B initializer(Consumer<C> initializer);

		/**
		 * Set the {@link Composer} to be used to compose the components into the content component.
		 * @param composer The composer to set (not null)
		 * @return this
		 */
		B composer(Composer<? super C, S> composer);

		/**
		 * Set whether to compose the components into the content component when the content is attached to a parent
		 * component, only if the component was not already composed using {@link ComposableComponent#compose()}.
		 * <p>
		 * Default is <code>true</code>.
		 * </p>
		 * @param composeOnAttach <code>true</code> to compose the components when the content is attached to a parent
		 *        component. If <code>false</code>, the {@link ComposableComponent#compose()} method must be invoked to
		 *        compose the components.
		 * @return this
		 */
		B composeOnAttach(boolean composeOnAttach);

		/**
		 * Set the composed Components width setup mode.
		 * @param componentsWidthMode composed Components width setup mode (not null)
		 * @return this
		 */
		B componentsWidthMode(ComponentsWidthMode componentsWidthMode);

	}

	/**
	 * Create a {@link Composer} which uses a {@link ComponentContainer} as composition layout and adds the property
	 * components to layout in the order they are returned from the {@link PropertyValueComponentSource}.
	 * @param <C> Actual ComponentContainer type
	 * @param <S> Actual components source
	 * @return A new {@link ComponentContainer} composer
	 */
	static <C extends ComponentContainer, S extends PropertyComponentSource> Composer<C, S> componentContainerComposer() {
		return new DefaultComponentContainerComposer<>();
	}

}
