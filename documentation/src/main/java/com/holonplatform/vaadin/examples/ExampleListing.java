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
import java.util.stream.Stream;

import com.holonplatform.core.Validator;
import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.property.BooleanProperty;
import com.holonplatform.core.property.NumericProperty;
import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.property.StringProperty;
import com.holonplatform.core.property.VirtualProperty;
import com.holonplatform.core.query.QueryConfigurationProvider;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.core.query.QuerySort;
import com.holonplatform.vaadin.components.BeanListing;
import com.holonplatform.vaadin.components.Components;
import com.holonplatform.vaadin.components.ItemListing.ColumnAlignment;
import com.holonplatform.vaadin.components.PropertyListing;
import com.holonplatform.vaadin.components.Selectable.SelectionMode;
import com.holonplatform.vaadin.data.ItemDataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.renderers.NumberRenderer;
import com.vaadin.ui.renderers.TextRenderer;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("unused")
public class ExampleListing {

	private final static NumericProperty<Long> ID = NumericProperty.longType("id");
	private final static PathProperty<String> DESCRIPTION = StringProperty.create("description");
	private final static PropertySet<Property<?>> PROPERTIES = PropertySet.of(ID, DESCRIPTION);

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

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
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
		final NumericProperty<Long> ID = NumericProperty.longType("id");
		final StringProperty DESCRIPTION = StringProperty.create("description");

		final PropertySet<?> PROPERTIES = PropertySet.of(ID, DESCRIPTION);

		PropertyListing listing = Components.listing.properties(PROPERTIES).build(); // <1>
		// end::propertyListing[]
	}

	public void propertyListing2() {
		// tag::propertyListing2[]
		final NumericProperty<Long> ID = NumericProperty.longType("id");
		final StringProperty DESCRIPTION = StringProperty.create("description");
		final BooleanProperty ACTIVE = BooleanProperty.create("active");

		final PropertySet<?> PROPERTIES = PropertySet.of(ID, DESCRIPTION, ACTIVE);

		PropertyListing listing = Components.listing.properties(PROPERTIES) // <1>
				.build(DESCRIPTION, ID); // <2>
		// end::propertyListing2[]
	}

	public void propertyListing3() {
		// tag::propertyListing3[]
		final NumericProperty<Long> ID = NumericProperty.longType("id");
		final StringProperty DESCRIPTION = StringProperty.create("description");
		final BooleanProperty ACTIVE = BooleanProperty.create("active");

		final PropertySet<?> PROPERTIES = PropertySet.of(ID, DESCRIPTION, ACTIVE);

		PropertyListing listing = Components.listing.properties(PROPERTIES) //
				.displayAsFirst(ACTIVE) // <1>
				.displayBefore(ID, DESCRIPTION) // <2>
				.displayAfter(DESCRIPTION, ACTIVE) // <3>
				.build();
		// end::propertyListing3[]
	}

	public void propertyListing4() {
		// tag::propertyListing4[]
		final NumericProperty<Long> ID = NumericProperty.longType("id");
		final StringProperty TEXT = StringProperty.create("txt");

		final VirtualProperty<String> DESCRIPTION = VirtualProperty.create(String.class,
				item -> "ID: " + item.getValue(ID)); // <1>

		PropertyListing listing = Components.listing.properties(ID, TEXT) //
				.build(ID, TEXT, DESCRIPTION); // <2>
		// end::propertyListing4[]
	}

	public void propertyListing5() {
		// tag::propertyListing5[]
		final NumericProperty<Long> ID = NumericProperty.longType("id");
		final VirtualProperty<String> DESCRIPTION = VirtualProperty.create(String.class,
				item -> "ID: " + item.getValue(ID)); // <1>

		final PropertySet<?> PROPERTIES = PropertySet.of(ID, DESCRIPTION);

		PropertyListing listing = Components.listing.properties(PROPERTIES).build(); // <2>
		// end::propertyListing5[]
	}

	public void propertyListing6() {
		// tag::propertyListing6[]
		PropertyListing listing = Components.listing.properties(PROPERTIES) //
				.withVirtualProperty(String.class, item -> "ID: " + item.getValue(ID)).add() // <1>
				.withVirtualProperty(item -> "ID: " + item.getValue(ID)).add() // <2>
				.build();
		// end::propertyListing6[]
	}

	public void propertyListing7() {
		// tag::propertyListing7[]
		PropertyListing listing = Components.listing.properties(PROPERTIES) //
				.withVirtualProperty(String.class, item -> "ID: " + item.getValue(ID)) // <1>
				.header("The header") // <2>
				.headerHTML("The <strong>header</strong>") // <3>
				.alignment(ColumnAlignment.CENTER) // <4>
				.width(100) // <5>
				.minWidth(50) // <6>
				.maxWidth(200) // <7>
				.expandRatio(1) // <8>
				.resizable(true) // <9>
				.hidable(true) // <10>
				.hidden(false) // <11>
				.hidingToggleCaption("Show/hide") // <12>
				.style("my-style") // <13>
				.style((property, item) -> "stylename") // <14>
				.render(new HtmlRenderer()) // <15>
				.sortUsing(ID) // <16>
				.sortGenerator((property, asc) -> QuerySort.of(ID, asc)) // <17>
				.add() // <18>
				.build();
		// end::propertyListing7[]
	}

	public void propertyListing8() {
		// tag::propertyListing8[]
		PropertyListing listing = Components.listing.properties(PROPERTIES) //
				.withVirtualProperty(String.class, item -> "ID: " + item.getValue(ID)) // <1>
				.displayAsFirst() // <2>
				.displayAsLast() // <3>
				.displayBefore(DESCRIPTION) // <4>
				.displayAfter(ID) // <5>
				.add().build();
		// end::propertyListing8[]
	}

	public void propertyListing9() {
		// tag::propertyListing9[]
		PropertyListing listing = Components.listing.properties(PROPERTIES) //
				.withVirtualProperty(String.class, "_myid", item -> "ID: " + item.getValue(ID)).displayAfter(ID).add() // <1>
				.withVirtualProperty(String.class, item -> "ID2: " + item.getValue(ID)).displayBeforeColumnId("_myid") // <2>
				.add().build();
		// end::propertyListing9[]
	}

	public void identifiers() {
		// tag::identifiers[]
		final NumericProperty<Long> ID = NumericProperty.longType("id");
		final StringProperty DESCRIPTION = StringProperty.create("description");

		final PropertySet<?> PROPERTIES = PropertySet.builderOf(ID, DESCRIPTION).withIdentifier(ID).build(); // <1>

		PropertyListing listing = Components.listing.properties(PROPERTIES).build();
		// end::identifiers[]
	}

	public void listing1() {
		// tag::listing1[]
		ItemDataProvider<PropertyBox> dataProvider = ItemDataProvider.create(cfg -> 0, // <1>
				(cfg, offset, limit) -> Stream.empty()); // <2>

		PropertyListing listing = Components.listing.properties(PROPERTIES) //
				.dataSource(dataProvider) // <3>
				.build();
		// end::listing1[]
	}

	public void listing1b() {
		// tag::listing1b[]
		Datastore datastore = getDatastore();

		PropertyListing listing = Components.listing.properties(PROPERTIES) //
				.dataSource(datastore, DataTarget.named("test")) // <1>
				.build();
		// end::listing1b[]
	}

	public void listing2() {
		// tag::listing2[]
		ItemDataProvider<PropertyBox> dataProvider = getDataProvider();

		PropertyListing listing = Components.listing.properties(PROPERTIES) //
				.dataSource(dataProvider, item -> item.getValue(ID)) // <1>
				.build();

		listing = Components.listing.properties(PROPERTIES) //
				.dataSource(getDatastore(), DataTarget.named("test"), ID) // <2>
				.build();
		// end::listing2[]
	}

	public void listing3() {
		// tag::listing3[]
		PropertyListing listing = Components.listing.properties(PROPERTIES) //
				.dataSource(getDatastore(), DataTarget.named("test")) //
				.withQueryConfigurationProvider(new QueryConfigurationProvider() { // <1>

					@Override
					public QueryFilter getQueryFilter() {
						return ID.gt(0L);
					}

					@Override
					public QuerySort getQuerySort() {
						return DESCRIPTION.asc();
					}

				}).build();
		// end::listing3[]
	}

	public void listing4() {
		// tag::listing4[]
		PropertyListing listing = Components.listing.properties(PROPERTIES) //
				.header(ID, "Custom ID header") // <1>
				.header(ID, "Default header", "id-header-message-code") // <2>
				.headerHTML(ID, "HTML <strong>header</strong>") // <3>
				.columnHidingAllowed(true) // <4>
				.hidable(ID, false) // <5>
				.columnReorderingAllowed(true) // <6>
				.alignment(ID, ColumnAlignment.RIGHT) // <7>
				.hidden(DESCRIPTION, true) // <8>
				.resizable(ID, false) // <9>
				.width(ID, 100) // <10>
				.expandRatio(DESCRIPTION, 1) // <11>
				.minWidth(DESCRIPTION, 200) // <12>
				.maxWidth(DESCRIPTION, 300) // <13>
				.minimumWidthFromContent(DESCRIPTION, true) // <14>
				.style(ID, (property, item) -> item.getValue(DESCRIPTION) != null ? "empty" : "not-empty") // <15>
				.withPropertyReorderListener((properties, userOriginated) -> { // <16>
					// ...
				}).withPropertyResizeListener((property, widthInPixel, userOriginated) -> { // <17>
					// ...
				}).withPropertyVisibilityListener((property, hidden, userOriginated) -> { // <18>
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
				}).withItemClickListener((item, property, index, event) -> { // <7>
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

	public void listingVirtual() {
		// tag::virtualproperty1[]
		final VirtualProperty<Component> EDIT = VirtualProperty.create(Component.class).message("Edit") // <1>
				.valueProvider( // <2>
						row -> Components.button().styleName(ValoTheme.BUTTON_ICON_ONLY).icon(VaadinIcons.EDIT)
								.onClick(e -> {
									Long rowId = row.getValue(ID); // <3>
									// perform edit action ...
								}).build());

		PropertyListing listing = Components.listing.properties(PROPERTIES) // <4>
				.build(EDIT, ID, DESCRIPTION); // <5>

		listing = Components.listing.properties(PROPERTIES) //
				.build(PropertySet.builder().add(PROPERTIES).add(EDIT).build()); // <6>
		// end::virtualproperty1[]
	}

	public void beanListingVirtual() {
		// tag::virtualcolumn1[]
		BeanListing<TestData> listing = Components.listing.items(TestData.class)
				.withVirtualColumn("delete", Button.class, bean -> { // <1>
					return Components.button().icon(VaadinIcons.TRASH).onClick(e -> {
						Long rowId = bean.getId(); // <2>
					}).build();
				}).build("delete", "id", "description"); // <3>
		// end::virtualcolumn1[]
	}

	private static ItemDataProvider<PropertyBox> getDataProvider() {
		return null;
	}

	private static Datastore getDatastore() {
		return null;
	}

}
