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

import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.vaadin.components.BeanListing;
import com.holonplatform.vaadin.components.Components;
import com.holonplatform.vaadin.components.PropertyListing;
import com.holonplatform.vaadin.data.ItemDataProvider;

@SuppressWarnings("unused")
public class ExampleListing {

	private final static PathProperty<Long> ID = PathProperty.create("id", Long.class);
	private final static PathProperty<String> DESCRIPTION = PathProperty.create("description", String.class);
	private final static PropertySet<?> PROPERTIES = PropertySet.of(ID, DESCRIPTION);

	// tag::beanListing[]
	private class TestData {

		private Long id;
		private String description;

		// getters and setters omitted

	}

	public void beanListing() {
		BeanListing<TestData> listing = Components.listing.items(TestData.class) // <1>
				.header("id", "The ID") // <2>
				.header("description", "The description") // <3>
				.build();

		listing.refresh(); // <4>
	}
	// end::beanListing[]

	public void propertyListing() {
		// tag::propertyListing[]
		final PathProperty<Long> ID = PathProperty.create("id", Long.class);
		final PathProperty<String> DESCRIPTION = PathProperty.create("description", String.class);

		final PropertySet<?> PROPERTIES = PropertySet.of(ID, DESCRIPTION);

		PropertyListing listing = Components.listing.properties(PROPERTIES).build(); // <1>
		// end::propertyListing[]
	}

	public void listing1() {
		// tag::listing1[]
		ItemDataProvider<PropertyBox> dataProvider = getDataProvider();
		PropertyListing listing = Components.listing.properties(PROPERTIES) //
				.dataSource(dataProvider) // <1>
				.build();
		// end::listing1[]
	}

	public void listing2() {
		// tag::listing2[]
		ItemDataProvider<PropertyBox> dataProvider = getDataProvider();

		PropertyListing listing = Components.listing.properties(PROPERTIES) //
				.dataSource(dataProvider, item -> item.getValue(ID)) // <1>
				.build();

		listing = Components.listing.properties(PROPERTIES) //
				.dataSource(dataProvider, ID) // <2>
				.build();
		// end::listing2[]
	}

	public void listing3() {
		// tag::listing3[]
		Datastore datastore = getDatastore();

		PropertyListing listing = Components.listing.properties(PROPERTIES) //
				.dataSource(datastore, DataTarget.named("test"), ID) // <1>
				.build();
		// end::listing3[]
	}

	private static ItemDataProvider<PropertyBox> getDataProvider() {
		return null;
	}

	private static Datastore getDatastore() {
		return null;
	}

}
