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
package com.holonplatform.vaadin.internal;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.http.Cookie;
import com.holonplatform.http.HttpMethod;
import com.holonplatform.http.HttpRequest;
import com.holonplatform.http.internal.AbstractHttpRequest;
import com.holonplatform.vaadin.VaadinHttpRequest;
import com.vaadin.server.VaadinRequest;

/**
 * {@link HttpRequest} using Vaadin {@link VaadinRequest}.
 * 
 * @since 5.0.0
 */
public class DefaultVaadinHttpRequest extends AbstractHttpRequest implements VaadinHttpRequest {

	/**
	 * Concrete Vaadin request
	 */
	protected final VaadinRequest request;

	/**
	 * Constructor
	 * @param request Vaadin request (not null)
	 */
	public DefaultVaadinHttpRequest(VaadinRequest request) {
		super();
		ObjectUtils.argumentNotNull(request, "VaadinRequest must be not null");
		this.request = request;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.VaadinHttpRequest#getContextPath()
	 */
	@Override
	public String getContextPath() {
		return request.getContextPath();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.http.HttpRequest#getMethod()
	 */
	@Override
	public HttpMethod getMethod() {
		return HttpMethod.from(request.getMethod());
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.http.HttpRequest#getRequestPath()
	 */
	@Override
	public String getRequestPath() {
		return request.getPathInfo();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.http.HttpRequest#getRequestHost()
	 */
	@Override
	public String getRequestHost() {
		return request.getRemoteHost();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.http.HttpRequest#getRequestParameter(java.lang.String)
	 */
	@Override
	public Optional<String> getRequestParameter(String name) {
		ObjectUtils.argumentNotNull(name, "Parameter name must be not null");
		return getQueryParameterValue(request.getParameterMap(), name);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.http.HttpRequest#getMultiValueRequestParameter(java.lang.String)
	 */
	@Override
	public Optional<List<String>> getMultiValueRequestParameter(String name) {
		ObjectUtils.argumentNotNull(name, "Parameter name must be not null");
		return getQueryParameterMultiValue(request.getParameterMap(), name);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.http.HttpRequest#getRequestParameters()
	 */
	@Override
	public Map<String, List<String>> getRequestParameters() {
		return getQueryParametersMap(request.getParameterMap());
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.http.HttpRequest#getRequestCookie(java.lang.String)
	 */
	@Override
	public Optional<Cookie> getRequestCookie(String name) {
		ObjectUtils.argumentNotNull(name, "Cookie name must be not null");
		javax.servlet.http.Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (javax.servlet.http.Cookie cookie : cookies) {
				if (name.equals(cookie.getName())) {
					return Optional.of(Cookie.builder().name(cookie.getName()).value(cookie.getValue())
							.version(cookie.getVersion()).path(cookie.getPath()).domain(cookie.getDomain()).build());
				}
			}
		}
		return Optional.empty();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.http.HttpRequest#getBody()
	 */
	@Override
	public InputStream getBody() throws IOException, UnsupportedOperationException {
		return request.getInputStream();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.messaging.MessageHeaders#getHeaders()
	 */
	@Override
	public Map<String, List<String>> getHeaders() {
		Map<String, List<String>> headers = new HashMap<>();
		Enumeration<String> names = request.getHeaderNames();
		if (names != null) {
			while (names.hasMoreElements()) {
				String name = names.nextElement();
				String value = request.getHeader(name);
				if (value == null || value.trim().equals("")) {
					headers.put(name, Collections.emptyList());
				} else {
					if (value.indexOf(',') > -1) {
						headers.put(name, Arrays.asList(value.split(",")));
					} else {
						headers.put(name, Collections.singletonList(value));
					}
				}
			}
		}
		return headers;
	}

	/**
	 * Get a query parameter value
	 * @param queryParameters Query parameters map
	 * @param name Parameter name
	 * @return If parameter is present and has a single value, that value is returned. If parameter has multiple values,
	 *         a String with all values separated by a comma is returned
	 */
	private static Optional<String> getQueryParameterValue(Map<String, String[]> queryParameters, String name) {
		if (name != null) {
			if (queryParameters != null && queryParameters.containsKey(name)) {
				String[] values = queryParameters.get(name);
				if (values != null && values.length > 0) {
					if (values.length == 1) {
						return Optional.ofNullable(values[0]);
					} else {
						StringBuilder sb = new StringBuilder();
						for (String value : values) {
							if (sb.length() > 0) {
								sb.append(',');
							}
							sb.append(value);
						}
						return Optional.of(sb.toString());
					}
				}
			}
		}
		return Optional.empty();
	}

	/**
	 * Get a query parameter multi value
	 * @param queryParameters Query parameters map
	 * @param name Parameter name
	 * @return Parameter values
	 */
	private static Optional<List<String>> getQueryParameterMultiValue(Map<String, String[]> queryParameters,
			String name) {
		if (name != null) {
			if (queryParameters != null && queryParameters.containsKey(name)) {
				String[] values = queryParameters.get(name);
				if (values != null && values.length > 0) {
					return Optional.ofNullable(Arrays.asList(values));
				}
			}
		}
		return Optional.empty();
	}

	/**
	 * Get a query parameter values map
	 * @param queryParameters Query parameters map
	 * @return Parameters name-values map
	 */
	private static Map<String, List<String>> getQueryParametersMap(Map<String, String[]> queryParameters) {
		if (queryParameters != null && !queryParameters.isEmpty()) {
			Map<String, List<String>> map = new HashMap<>(queryParameters.size());
			queryParameters.forEach((n, v) -> map.put(n, (v == null) ? Collections.emptyList() : Arrays.asList(v)));
			return map;
		}
		return Collections.emptyMap();
	}

}
