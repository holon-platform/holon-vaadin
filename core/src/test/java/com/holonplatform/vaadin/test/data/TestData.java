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
package com.holonplatform.vaadin.test.data;

import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.property.PropertyValueConverter;

public final class TestData {

	public static final PathProperty<String> ID = PathProperty.create("code", String.class);
	public static final PathProperty<String> DESCRIPTION = PathProperty.create("description", String.class);
	public static final PathProperty<Integer> SEQUENCE = PathProperty.create("sequence", Integer.class);
	public static final PathProperty<Boolean> OBSOLETE = PathProperty.create("obsolete", boolean.class)
			.converter(PropertyValueConverter.numericBoolean(int.class));

	public static final PropertySet<?> PROPERTIES = PropertySet.of(ID, DESCRIPTION, SEQUENCE, OBSOLETE);

	private TestData() {
	}

}
