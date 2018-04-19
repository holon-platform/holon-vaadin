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

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.holonplatform.core.Validator;
import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.vaadin.components.BeanListing;
import com.holonplatform.vaadin.components.Components;
import com.holonplatform.vaadin.components.ItemListing.ColumnAlignment;
import com.holonplatform.vaadin.components.PropertyListing;
import com.holonplatform.vaadin.components.Selectable.SelectionMode;
import com.holonplatform.vaadin.data.ItemDataProvider;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.renderers.NumberRenderer;
import com.vaadin.ui.renderers.TextRenderer;

@SuppressWarnings("unused")
public class ExampleListing {

	private final static PathProperty<Long> ID = PathProperty.create("id", Long.class);
	private final static PathProperty<String> DESCRIPTION = PathProperty.create("description", String.class);
	private final static PropertySet<?> PROPERTIES = PropertySet.of(ID, DESCRIPTION);

	// tag::beanListing1[]
	private class TestData {

		private Long id;
		private String description;

		// getters and setters omitted

		// end::beanListing1[]

		public TestData(int id, String description) {
			super();
			this.id = Long.valueOf(id);
			this.description = description;
		}

	}

	public void beanListing2() {
		// tag::beanListing2[]
		BeanListing<TestData> listing = Components.listing.items(TestData.class) // <1>
				.build();
		// end::beanListing2[]
	}

	public void beanListing3() {
		// tag::beanListing3[]
		final List<TestData> data = Arrays.asList(new TestData(1, "One"), new TestData(2, "Two"),
				new TestData(3, "Three"));

		BeanListing<TestData> listing = Components.listing.items(TestData.class) //
				.dataSource(ItemDataProvider.create(cfg -> data.size(), // <1>
						(cfg, offset, limit) -> data.stream().skip(offset).limit(limit)))
				.build();
		// end::beanListing3[]
	}

	public void beanListing4() {
		// tag::beanListing4[]
		final Datastore datastore = getDatastore();

		BeanListing<TestData> listing = Components.listing.items(TestData.class) //
				.dataSource(datastore, DataTarget.named("test")) // <1>
				.build();
		// end::beanListing4[]
	}

	public void beanListing5() {
		// tag::beanListing5[]
		BeanListing<TestData> listing = Components.listing.items(TestData.class) //
				.dataSource(getDatastore(), DataTarget.named("test")).build();
		
		listing.refresh(); // <1>
		// end::beanListing5[]
	}

	public void beanListing() {
		BeanListing<TestData> listing = Components.listing.items(TestData.class) // <1>
				.header("id", "The ID") // <2>
				.header("description", "The description") // <3>
				.build();

		listing.refresh(); // <4>
	}

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

	public void listing4() {
		// tag::listing4[]
		PropertyListing listing = Components.listing.properties(PROPERTIES) //
				.header(ID, "Custom ID header") // <1>
				.columnHidingAllowed(true) // <2>
				.hidable(ID, false) // <3>
				.columnReorderingAllowed(true) // <4>
				.alignment(ID, ColumnAlignment.RIGHT) // <5>
				.hidden(DESCRIPTION, true) // <6>
				.resizable(ID, false) // <7>
				.width(ID, 100) // <8>
				.expandRatio(DESCRIPTION, 1) // <9>
				.minWidth(DESCRIPTION, 200) // <10>
				.maxWidth(DESCRIPTION, 300) // <11>
				.style(ID, (property, item) -> item.getValue(DESCRIPTION) != null ? "empty" : "not-empty") // <12>
				.withPropertyReorderListener((properties, userOriginated) -> { // <13>
					// ...
				}).withPropertyResizeListener((property, widthInPixel, userOriginated) -> { // <14>
					// ...
				}).withPropertyVisibilityListener((property, hidden, userOriginated) -> { // <15>
					// ...
				}).build();
		// end::listing4[]
	}

	public void listing5() {
		// tag::listing5[]
		PropertyListing listing = Components.listing.properties(PROPERTIES) //
				.render(ID, new NumberRenderer(Locale.US)) // <1>
				.render(ID, value -> String.valueOf(value), new TextRenderer()) // <2>
				.build();
		// end::listing5[]
	}

	public void listing6() {
		// tag::listing6[]
		PropertyListing listing = Components.listing.properties(PROPERTIES) //
				.heightByContents() // <1>
				.frozenColumns(1) // <2>
				.hideHeaders() // <3>
				.withRowStyle(item -> { // <4>
					return item.getValue(DESCRIPTION) != null ? "has-des" : "no-nes";
				}) //
				.itemDescriptionGenerator(item -> item.getValue(DESCRIPTION)) // <5>
				.detailsGenerator(item -> { // <6>
					VerticalLayout component = new VerticalLayout();
					// ...
					return component;
				}).withItemClickListener((item, property, event) -> { // <7>
					// ...
				}).header(header -> { // <8>
					header.getDefaultRow().setStyleName("my-header");
				}).footer(footer -> { // <9>
					footer.appendRow().setStyleName("my-footer");
				}).footerGenerator((source, footer) -> { // <10>
					footer.getRowAt(0).getCell(ID).setText("ID footer");
				}).withPostProcessor(grid -> { // <11>
					// ...
				}).build();
		// end::listing6[]
	}

	public void listing7() {
		// tag::listing7[]
		PropertyListing listing = Components.listing.properties(PROPERTIES) //
				.selectionMode(SelectionMode.SINGLE) // <1>
				.build();

		final PropertyBox ITEM = PropertyBox.builder(PROPERTIES).set(ID, 1L).build();

		PropertyBox selected = listing.getFirstSelectedItem().orElse(null); // <2>

		boolean isSelected = listing.isSelected(ITEM); // <3>

		listing.select(ITEM); // <4>

		listing.deselectAll(); // <5>
		// end::listing7[]
	}

	public void listing8() {
		// tag::listing8[]
		PropertyListing listing = Components.listing.properties(PROPERTIES) //
				.build();

		listing.setSelectionMode(SelectionMode.MULTI); // <1>

		final PropertyBox ITEM = PropertyBox.builder(PROPERTIES).set(ID, 1L).build();

		Set<PropertyBox> selected = listing.getSelectedItems(); // <2>

		boolean isSelected = listing.isSelected(ITEM); // <3>

		listing.select(ITEM); // <4>

		listing.deselectAll(); // <5>

		listing.selectAll(); // <6>
		// end::listing8[]
	}

	public void listing9() {
		// tag::listing9[]
		PropertyListing listing = Components.listing.properties(PROPERTIES) //
				.sortable(ID, true) // <1>
				.sortUsing(ID, DESCRIPTION) // <2>
				.sortGenerator(ID, (property, ascending) -> { // <3>
					return ascending ? ID.asc() : ID.desc();
				}) //
				.fixedSort(ID.asc()) // <4>
				.defaultSort(DESCRIPTION.asc()) // <5>
				.fixedFilter(ID.gt(0L)) // <6>
				.withQueryConfigurationProvider(() -> DESCRIPTION.isNotNull()) // <7>
				.build();
		// end::listing9[]
	}

	public void listing10() {
		// tag::listing10[]
		Datastore datastore = getDatastore();

		PropertyListing listing = Components.listing.properties(PROPERTIES) //
				.dataSource(datastore, DataTarget.named("test"), ID) // <1>
				.commitHandler((addedItems, modifiedItems, removedItems) -> { // <2>
					// ...
				}).build();

		final PropertyBox ITEM = PropertyBox.builder(PROPERTIES).set(ID, 777L).set(DESCRIPTION, "A description")
				.build();

		listing.addItem(ITEM); // <3>

		listing.refreshItem(ITEM); // <4>

		listing.removeItem(ITEM); // <5>
		// end::listing10[]
	}

	public void listing11() {
		// tag::listing11[]
		Datastore datastore = getDatastore();

		PropertyListing listing = Components.listing.properties(PROPERTIES) //
				.dataSource(datastore, DataTarget.named("test"), ID) //
				.buffered(true) // <1>
				.build();

		final PropertyBox ITEM = PropertyBox.builder(PROPERTIES).set(ID, 777L).set(DESCRIPTION, "A description")
				.build();

		listing.addItem(ITEM); // <2>
		listing.refreshItem(ITEM); // <3>
		listing.removeItem(ITEM); // <4>

		listing.commit(); // <5>
		listing.discard(); // <6>
		// end::listing11[]
	}

	public void listing12() {
		// tag::listing12[]
		PropertyListing listing = Components.listing.properties(PROPERTIES) //
				.editable(true) // <1>
				.editorBuffered(true) // <2>
				.editorSaveCaption("Save item") // <3>
				.editorCancelCaption("Discard") // <4>
				.editable(ID, false) // <5>
				.editor(DESCRIPTION, new TextField()) // <6>
				.withValidator(Validator.create(pb -> pb.getValue(DESCRIPTION) != null, "Description must be not null")) // <7>
				.withValidator(DESCRIPTION, Validator.max(100)) // <8>
				.required(ID) // <9>
				.build();
		// end::listing12[]
	}

	private static ItemDataProvider<PropertyBox> getDataProvider() {
		return null;
	}

	private static Datastore getDatastore() {
		return null;
	}

}
