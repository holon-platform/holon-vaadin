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
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.vaadin.components.Components;
import com.holonplatform.vaadin.components.PropertyListing;

@SuppressWarnings("unused")
public class ExampleListingMigration2 {

	// tag::listing1[]
	private final static PathProperty<Long> ID = PathProperty.create("id", Long.class);
	private final static PathProperty<String> DESCRIPTION = PathProperty.create("description", String.class);

	private final static PropertySet<?> PROPERTIES = PropertySet.builderOf(ID, DESCRIPTION).withIdentifier(ID).build(); // <1>
	// end::listing1[]

	public void listing() {
		// tag::listing2[]
		Datastore datastore = getDatastore();

		PropertyListing listing = Components.listing.properties(PROPERTIES) // <1>
				.dataSource(datastore, DataTarget.named("test")) // <2>
				.build();
		// end::listing2[]
	}

	private static Datastore getDatastore() {
		return null;
	}

}
