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

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.holonplatform.auth.annotations.Authenticate;
import com.holonplatform.core.internal.utils.AnnotationUtils;
import com.holonplatform.vaadin.navigator.SubViewContainer;
import com.holonplatform.vaadin.navigator.ViewContentProvider;
import com.holonplatform.vaadin.navigator.ViewWindowConfigurator;
import com.holonplatform.vaadin.navigator.annotations.OnLeave;
import com.holonplatform.vaadin.navigator.annotations.OnShow;
import com.holonplatform.vaadin.navigator.annotations.ViewContext;
import com.holonplatform.vaadin.navigator.annotations.WindowView;

/**
 * Default {@link ViewConfiguration} implementation.
 * 
 * @since 5.0.0
 */
public class DefaultViewConfiguration implements ViewConfiguration {

	private static final long serialVersionUID = -288143111172867404L;

	private boolean _volatile = false;
	private boolean forceInWindow = false;
	private boolean viewContentProvider;
	private boolean subViewContainer = false;
	private String parentViewName;
	private String caption;
	private String captionMessageCode;
	private Collection<ViewParameterDefinition> parameters;
	private List<Method> onShowMethods;
	private List<Method> onLeaveMethods;
	private WindowView windowConfiguration;
	private List<Method> fireOnRefreshMethods;
	private Collection<ViewContextField> contextInjectionFields;
	private Authenticate authentication;

	/**
	 * Constructor
	 */
	public DefaultViewConfiguration() {
		super();
	}

	/**
	 * Set this view as volatile (not to be tracked in navigation history)
	 * @param _volatile <code>true</code> to set this view as volatile
	 */
	public void setVolatile(boolean _volatile) {
		this._volatile = _volatile;
	}

	/**
	 * Set this view to be displayed in a Window
	 * @param forceInWindow <code>true</code> to set this view to be displayed in a Window
	 */
	public void setForceInWindow(boolean forceInWindow) {
		this.forceInWindow = forceInWindow;
	}

	/**
	 * Set whether the view class extends {@link ViewContentProvider} to delegate View contents supplying
	 * @param viewContentProvider <code>true</code> if the view class is a ViewContentProvider
	 */
	public void setViewContentProvider(boolean viewContentProvider) {
		this.viewContentProvider = viewContentProvider;
	}

	/**
	 * Set parent view name for sub views
	 * @param parentViewName Parent view name
	 */
	public void setParentViewName(String parentViewName) {
		this.parentViewName = parentViewName;
	}

	/**
	 * Declares view class is a {@link SubViewContainer}
	 * @param subViewContainer <code>true</code> if view class is a {@link SubViewContainer}
	 */
	public void setSubViewContainer(boolean subViewContainer) {
		this.subViewContainer = subViewContainer;
	}

	/**
	 * Set view caption
	 * @param caption View caption
	 */
	public void setCaption(String caption) {
		this.caption = caption;
	}

	/**
	 * Set view caption translation message code
	 * @param captionMessageCode View caption translation message code
	 */
	public void setCaptionMessageCode(String captionMessageCode) {
		this.captionMessageCode = captionMessageCode;
	}

	/**
	 * Set view parameters definitions
	 * @param parameters the parameters definitions to set
	 */
	public void setParameters(Collection<ViewParameterDefinition> parameters) {
		this.parameters = parameters;
	}

	/**
	 * Set view {@link OnShow} methods
	 * @param onShowMethods View OnShow methods
	 */
	public void setOnShowMethods(List<Method> onShowMethods) {
		this.onShowMethods = onShowMethods;
	}

	/**
	 * Set view {@link OnLeave} methods
	 * @param onLeaveMethods View OnLeave methods
	 */
	public void setOnLeaveMethods(List<Method> onLeaveMethods) {
		this.onLeaveMethods = onLeaveMethods;
	}

	/**
	 * Set given {@link OnShow} method as fire on refresh
	 * @param onShowMethod Method to set
	 */
	public void setFireOnRefreshMethod(Method onShowMethod) {
		if (fireOnRefreshMethods == null) {
			fireOnRefreshMethods = new LinkedList<>();
		}
		fireOnRefreshMethods.add(onShowMethod);
	}

	/**
	 * Set view Window configuration
	 * @param windowConfiguration View Window configuration
	 */
	public void setWindowConfiguration(WindowView windowConfiguration) {
		this.windowConfiguration = windowConfiguration;
	}

	/**
	 * Set view class fields which declare {@link ViewContext} data injection
	 * @param contextInjectionFields Context injection fields
	 */
	public void setContextInjectionFields(Collection<ViewContextField> contextInjectionFields) {
		this.contextInjectionFields = contextInjectionFields;
	}

	/**
	 * Set view {@link Authenticate} annotation.
	 * @param authentication the Authenticate annotation to set
	 */
	public void setAuthentication(Authenticate authentication) {
		this.authentication = authentication;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.ui.navigator.ViewConfiguration#isVolatile()
	 */
	@Override
	public boolean isVolatile() {
		return _volatile;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.ui.navigator.ViewConfiguration#isForceInWindow()
	 */
	@Override
	public boolean isForceInWindow() {
		return forceInWindow;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.ui.navigator.ViewConfiguration#isViewContentProvider()
	 */
	@Override
	public boolean isViewContentProvider() {
		return viewContentProvider;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.ui.navigator.ViewConfiguration#isSubView()
	 */
	@Override
	public boolean isSubView() {
		return getParentViewName() != null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.ui.navigator.ViewConfiguration#isSubViewContainer()
	 */
	@Override
	public boolean isSubViewContainer() {
		return subViewContainer;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.ui.navigator.ViewConfiguration#getParentViewName()
	 */
	@Override
	public String getParentViewName() {
		return parentViewName;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.ui.navigator.ViewConfiguration#getCaption()
	 */
	@Override
	public String getCaption() {
		return caption;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.ui.navigator.ViewConfiguration#getCaptionMessageCode()
	 */
	@Override
	public String getCaptionMessageCode() {
		return captionMessageCode;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.ui.navigator.ViewConfiguration#getParameters()
	 */
	@Override
	public Collection<ViewParameterDefinition> getParameters() {
		if (parameters != null) {
			return parameters;
		}
		return Collections.emptySet();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.ui.navigator.ViewConfiguration#getOnShowMethods()
	 */
	@Override
	public List<Method> getOnShowMethods() {
		if (onShowMethods != null) {
			return onShowMethods;
		}
		return Collections.emptyList();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.ui.navigator.ViewConfiguration#getOnLeaveMethods()
	 */
	@Override
	public List<Method> getOnLeaveMethods() {
		if (onLeaveMethods != null) {
			return onLeaveMethods;
		}
		return Collections.emptyList();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.ui.navigator.ViewConfiguration#isFireOnRefresh(java.lang.reflect.Method)
	 */
	@Override
	public boolean isFireOnRefresh(Method onShowMethod) {
		return onShowMethod != null && fireOnRefreshMethods != null && fireOnRefreshMethods.contains(onShowMethod);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.ui.navigator.ViewConfiguration#getContextInjectionFields()
	 */
	@Override
	public Collection<ViewContextField> getContextInjectionFields() {
		if (contextInjectionFields != null) {
			return contextInjectionFields;
		}
		return Collections.emptySet();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.navigator.internal.ViewConfiguration#getAuthentication()
	 */
	@Override
	public Optional<Authenticate> getAuthentication() {
		return Optional.ofNullable(authentication);
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.function.Consumer#accept(java.lang.Object)
	 */
	@Override
	public void accept(ViewWindowConfigurator t) {
		if (windowConfiguration != null) {
			t.closable(windowConfiguration.closable());
			t.resizable(windowConfiguration.resizable());
			if (!AnnotationUtils.isEmpty(windowConfiguration.windowWidth())) {
				t.width(windowConfiguration.windowWidth());
			}
			if (!AnnotationUtils.isEmpty(windowConfiguration.windowHeigth())) {
				t.height(windowConfiguration.windowHeigth());
			}
		}
	}

}
