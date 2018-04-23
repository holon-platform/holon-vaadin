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
package com.holonplatform.vaadin.internal.components.builders;

import com.holonplatform.core.property.Property;
import com.holonplatform.vaadin.components.PropertyListing;
import com.holonplatform.vaadin.components.builders.PropertyListingBuilder.GridPropertyListingBuilder;
import com.holonplatform.vaadin.internal.components.DefaultPropertyListing;

/**
 * Default {@link GridPropertyListingBuilder} implementation.
 *
 * @since 5.0.0
 */
public class DefaultGridPropertyListingBuilder
		extends AbstractGridPropertyListingBuilder<PropertyListing, DefaultPropertyListing, GridPropertyListingBuilder>
		implements GridPropertyListingBuilder {

	/**
	 * Constructor.
	 * @param <P> Property type
	 * @param properties Listing property set
	 */
	public <P extends Property<?>> DefaultGridPropertyListingBuilder(Iterable<P> properties) {
		super(new DefaultPropertyListing(properties), properties);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentBuilder#build(com.vaadin.ui.
	 * AbstractComponent)
	 */
	@Override
	protected PropertyListing build(DefaultPropertyListing instance) {
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentConfigurator#builder()
	 */
	@Override
	protected GridPropertyListingBuilder builder() {
		return this;
	}

}
