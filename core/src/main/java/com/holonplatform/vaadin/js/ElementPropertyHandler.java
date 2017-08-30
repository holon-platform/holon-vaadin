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
package com.holonplatform.vaadin.js;

import com.vaadin.annotations.JavaScript;
import com.vaadin.server.AbstractJavaScriptExtension;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;

/**
 * JavaScript extension to set properties in html element of extended {@link Component}.
 *
 * @since 5.0.0
 */
@JavaScript("holon-vaadin-min.js")
public class ElementPropertyHandler extends AbstractJavaScriptExtension {

	private static final long serialVersionUID = 6647168569620773486L;

	/**
	 * Register this extension on given component
	 * @param component Component to extend
	 */
	public ElementPropertyHandler(AbstractComponent component) {
		extend(component);
	}

	/**
	 * Set a custom HTML property in the element bound to the extended Component
	 * @param name Property name
	 * @param value Property value
	 */
	public void setProperty(String name, Object value) {
		callFunction("setprp", name, value);
	}

	/**
	 * Remove an HTML property in the element bound to the extended Component
	 * @param name Property name to remove
	 */
	public void removeProperty(String name) {
		callFunction("rmvprp", name);
	}

	/**
	 * Set an event handler with given <code>eventName</code> in the element bound to the extended Component using given
	 * JavaScript content.
	 * @param eventName Event name to register
	 * @param js JavaScript to execute when event is fired
	 */
	public void setEventHandler(String eventName, String js) {
		callFunction("seteh", eventName, js);
	}

}
