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
package com.holonplatform.vaadin.examples;

import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.property.PropertyRendererRegistry;
import com.holonplatform.vaadin.components.Components;
import com.holonplatform.vaadin.components.Field;
import com.holonplatform.vaadin.components.Input;
import com.holonplatform.vaadin.components.Input.InputPropertyRenderer;
import com.holonplatform.vaadin.components.ViewComponent;

@SuppressWarnings("unused")
public class ExampleRenderers {

	@SuppressWarnings("unchecked")
	public void renderers1() {
		// tag::renderers1[]
		final PathProperty<String> TEXT = PathProperty.create("text", String.class);
		final PathProperty<Long> LONG = PathProperty.create("long", Long.class);

		Input<String> input = TEXT.render(Input.class); // <1>

		Field<Long> field = LONG.render(Field.class); // <2>

		ViewComponent<String> view = TEXT.render(ViewComponent.class); // <3>
		// end::renderers1[]
	}

	@SuppressWarnings("unchecked")
	public void renderers2() {
		// tag::renderers2[]
		final PathProperty<String> TEXT = PathProperty.create("text", String.class);

		InputPropertyRenderer<String> textAreaInputRenderer = p -> Components.input.string(true).build(); // <1>

		PropertyRendererRegistry.get().register(p -> p == TEXT, textAreaInputRenderer); // <2>

		Input<String> input = TEXT.render(Input.class); // <3>
		// end::renderers2[]
	}

}
