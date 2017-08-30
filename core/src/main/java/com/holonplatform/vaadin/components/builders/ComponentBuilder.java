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
package com.holonplatform.vaadin.components.builders;

import com.vaadin.ui.Component;

/**
 * Base builder to create {@link Component}s.
 * 
 * @param <C> Concrete component type
 * @param <B> Concrete builder type
 * 
 * @since 5.0.0
 */
public interface ComponentBuilder<C extends Component, B extends ComponentBuilder<C, B>>
		extends ComponentConfigurator<B> {

	/**
	 * Instructs the builder to resolve any message localization (for example component caption and description) only
	 * after the component is attached to parent layout. By default, localization is performed immediately during
	 * component building.
	 * @return this
	 */
	B deferLocalization();

	/**
	 * Build and returns the component
	 * @return Component instance
	 */
	C build();

}
