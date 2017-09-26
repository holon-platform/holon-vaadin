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
package com.holonplatform.vaadin;

import com.holonplatform.http.HttpRequest;
import com.holonplatform.vaadin.internal.DefaultVaadinHttpRequest;
import com.vaadin.server.VaadinRequest;

/**
 * {@link HttpRequest} using Vaadin {@link VaadinRequest}.
 * 
 * @since 5.0.0
 */
public interface VaadinHttpRequest extends HttpRequest {

	/**
	 * Returns the portion of the request URI that indicates the context of the request.
	 * @return a String specifying the portion of the request URI that indicates the context of the request
	 */
	public String getContextPath();

	/**
	 * Create a {@link VaadinHttpRequest} using given {@link VaadinRequest}.
	 * @param request Vaadin request (not null)
	 * @return A {@link VaadinHttpRequest} backed by given request
	 */
	static VaadinHttpRequest create(VaadinRequest request) {
		return new DefaultVaadinHttpRequest(request);
	}

}
