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

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.holonplatform.vaadin.navigator.annotations.ViewParameter;
import com.holonplatform.vaadin.navigator.internal.ViewConfiguration.ViewParameterDefinition;

/**
 * Default {@link ViewParameterDefinition} implementation
 * 
 * @since 4.0.0
 */
public class DefaultViewParameterDefinition implements ViewParameterDefinition {

	private static final long serialVersionUID = -8286322000190818550L;

	private final String name;
	private final Class<?> type;

	private boolean required = false;
	private Object defaultValue;

	private Field field;
	private Method readMethod;
	private Method writeMethod;

	/**
	 * Constructor
	 * @param name Parameter name
	 * @param type Parameter value type
	 */
	public DefaultViewParameterDefinition(String name, Class<?> type) {
		super();
		this.name = name;
		this.type = type;
	}

	/**
	 * Set whether the parameter is required
	 * @param required <code>true</code> if parameter is required, <code>false</code> otherwise
	 */
	public void setRequired(boolean required) {
		this.required = required;
	}

	/**
	 * Set parameter default value. According to parameter value type, a suitable String representation must be provided
	 * for default value:
	 * <ul>
	 * <li>For numeric values, <code>.</code> (dot) charachter must be used as decimal separator</li>
	 * <li>For boolean values, only words <code>true</code> and <code>false</code> are admitted</li>
	 * <li>For Enum values, String representation of enum ordinal index must be provided</li>
	 * <li>For Date values, default value must be expressed using date format pattern
	 * {@link ViewParameter#DEFAULT_DATE_PATTERN}</li>
	 * </ul>
	 * @param defaultValue Parameter default value
	 */
	public void setDefaultValue(Object defaultValue) {
		this.defaultValue = defaultValue;
	}

	/**
	 * Set the view class field bound to this parameter definition
	 * @param field View class field bound to this parameter definition
	 */
	public void setField(Field field) {
		this.field = field;
	}

	/**
	 * Set the view parameter field read method
	 * @param readMethod Field read method
	 */
	public void setReadMethod(Method readMethod) {
		this.readMethod = readMethod;
	}

	/**
	 * Set the view parameter field write method
	 * @param writeMethod Field write method
	 */
	public void setWriteMethod(Method writeMethod) {
		this.writeMethod = writeMethod;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.ui.navigator.ViewParameterDefinition#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.ui.navigator.ViewParameterDefinition#getType()
	 */
	@Override
	public Class<?> getType() {
		return type;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.ui.navigator.ViewParameterDefinition#isRequired()
	 */
	@Override
	public boolean isRequired() {
		return required;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.ui.navigator.ViewParameterDefinition#getDefaultValue()
	 */
	@Override
	public Object getDefaultValue() {
		return defaultValue;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.ui.navigator.ViewParameterDefinition#getField()
	 */
	@Override
	public Field getField() {
		return field;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.ui.navigator.ViewParameterDefinition#getReadMethod()
	 */
	@Override
	public Method getReadMethod() {
		return readMethod;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.ui.navigator.ViewParameterDefinition#getWriteMethod()
	 */
	@Override
	public Method getWriteMethod() {
		return writeMethod;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DefaultViewParameterDefinition [name=" + name + ", type=" + type + ", required=" + required
				+ ", defaultValue=" + defaultValue + "]";
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DefaultViewParameterDefinition other = (DefaultViewParameterDefinition) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
