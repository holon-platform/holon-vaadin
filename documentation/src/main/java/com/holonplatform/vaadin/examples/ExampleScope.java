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

import java.util.Locale;

import com.holonplatform.core.i18n.LocalizationContext;
import com.vaadin.server.VaadinSession;

public class ExampleScope {

	public void sessionscope() {
		// tag::sessionscope[]
		VaadinSession.getCurrent().setAttribute(LocalizationContext.CONTEXT_KEY,
				LocalizationContext.builder().withInitialLocale(Locale.US).build()); // <1>

		LocalizationContext.getCurrent().ifPresent(localizationContext -> { // <2>
			// LocalizationContext obtained from current Vaadin session
		});
		// end::sessionscope[]
	}

}
