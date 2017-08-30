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
package com.holonplatform.vaadin.navigator.internal;

import com.holonplatform.vaadin.navigator.annotations.ViewContext;
import com.holonplatform.vaadin.navigator.internal.ViewConfiguration.ViewConfigurationException;
import com.holonplatform.vaadin.navigator.internal.ViewConfiguration.ViewConfigurationProvider;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewProvider;

/**
 * {@link ViewProvider} adapter which wraps a concrete ViewProvider and allow to perform some view instance processing
 * before returning it to navigator using a {@link ViewProcessorProvider}.
 * 
 * @since 5.0.0
 */
public class DefaultViewProviderAdapter implements ViewProviderAdapter {

	private static final long serialVersionUID = -7664494014173003621L;

	protected final ViewConfigurationProvider viewConfigurationProvider;
	protected final ViewProvider viewProvider;
	protected final ViewProcessorProvider viewProcessorProvider;

	/**
	 * Constructor
	 * @param viewConfigurationProvider ViewConfigurationProvider
	 * @param viewProvider Concrete {@link ViewProvider}
	 */
	public DefaultViewProviderAdapter(ViewConfigurationProvider viewConfigurationProvider, ViewProvider viewProvider) {
		super();
		this.viewConfigurationProvider = viewConfigurationProvider;
		this.viewProvider = viewProvider;
		if (viewProvider instanceof ViewProcessorProvider) {
			this.viewProcessorProvider = (ViewProcessorProvider) viewProvider;
		} else {
			this.viewProcessorProvider = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.ui.navigator.ViewProviderAdapter#getWrappedProvider()
	 */
	@Override
	public ViewProvider getWrappedProvider() {
		return viewProvider;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.navigator.ViewProvider#getViewName(java.lang.String)
	 */
	@Override
	public String getViewName(String viewAndParameters) {
		return viewProvider.getViewName(viewAndParameters);
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.navigator.ViewProvider#getView(java.lang.String)
	 */
	@Override
	public View getView(String viewName) {
		return processViewInstance(viewProvider.getView(viewName));
	}

	/**
	 * Process given View instance for additional operations, such as {@link ViewContext} data injection
	 * @param view View instance
	 * @return Processed View
	 * @throws ViewConfigurationException Error processing View instance
	 */
	protected View processViewInstance(View view) throws ViewConfigurationException {
		if (viewProcessorProvider != null) {
			return viewProcessorProvider.processViewInstance(viewConfigurationProvider, view);
		} else {
			return view;
		}
	}

}
