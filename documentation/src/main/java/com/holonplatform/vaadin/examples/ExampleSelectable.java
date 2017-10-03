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

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.query.ConstantExpression;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.vaadin.components.Components;
import com.holonplatform.vaadin.components.MultiSelect;
import com.holonplatform.vaadin.components.SingleSelect;
import com.holonplatform.vaadin.components.builders.BaseSelectInputBuilder.RenderingMode;
import com.holonplatform.vaadin.data.ItemDataProvider;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Notification;

@SuppressWarnings("unused")
public class ExampleSelectable {

	public void selectable1() {
		// tag::selectable1[]
		SingleSelect<TestData> singleSelect = Components.input.singleSelect(TestData.class).caption("Single select")
				.build(); // <1>

		singleSelect.setValue(new TestData(1)); // <2>
		singleSelect.select(new TestData(1)); // <3>

		singleSelect.clear(); // <4>
		singleSelect.deselectAll(); // <5>

		boolean selected = singleSelect.isSelected(new TestData(1)); // <6>

		singleSelect.addSelectionListener(
				s -> s.getFirstSelectedItem().ifPresent(i -> Notification.show("Selected: " + i.getId()))); // <7>
		// end::selectable1[]
	}

	public void selectable2() {
		// tag::selectable2[]
		MultiSelect<TestData> multiSelect = Components.input.multiSelect(TestData.class).caption("Multi select")
				.build(); // <1>

		Set<TestData> values = new HashSet<>();
		values.add(new TestData(1));
		values.add(new TestData(2));

		multiSelect.setValue(values); // <2>
		multiSelect.select(new TestData(3)); // <3>

		multiSelect.deselect(new TestData(3)); // <4>

		multiSelect.clear(); // <5>
		multiSelect.deselectAll(); // <6>

		boolean selected = multiSelect.isSelected(new TestData(1)); // <7>

		multiSelect.addSelectionListener(s -> Notification.show(s.getAllSelectedItems().stream()
				.map(i -> String.valueOf(i.getId())).collect(Collectors.joining(";", "Selected: ", "")))); // <8>
		// end::selectable2[]
	}

	public void selectable3() {
		// tag::selectable3[]
		SingleSelect<TestData> singleSelect = Components.input.singleSelect(TestData.class, RenderingMode.OPTIONS)
				.build(); // <1>

		MultiSelect<TestData> multiSelect = Components.input.multiSelect(TestData.class, RenderingMode.SELECT).build(); // <2>
		// end::selectable3[]
	}

	public void selectable4() {
		// tag::selectable4[]
		SingleSelect<TestData> singleSelect = Components.input.singleSelect(TestData.class)
				.items(new TestData(1), new TestData(2)).build(); // <1>

		singleSelect = Components.input.singleSelect(TestData.class)
				.dataSource(ItemDataProvider.create(q -> 2, (q, o, l) -> Stream.of(new TestData(1), new TestData(2))))
				.build(); // <2>

		singleSelect = Components.input.singleSelect(TestData.class)
				.dataSource(DataProvider.ofItems(new TestData(1), new TestData(2))).build(); // <3>
		// end::selectable4[]
	}

	public void selectable5() {
		// tag::selectable5[]
		final TestData ONE = new TestData(1);
		final TestData TWO = new TestData(2);

		SingleSelect<TestData> singleSelect = Components.input.singleSelect(TestData.class).items(ONE, TWO)
				.itemCaption(ONE, "One") // <1>
				.itemCaption(ONE, "One", "caption-one-message-code") // <2>
				.itemIcon(ONE, VaadinIcons.STAR) // <3>
				.build();
		// end::selectable5[]
	}

	public void selectable6() {
		// tag::selectable6[]
		SingleSelect<TestData> singleSelect = Components.input.singleSelect(TestData.class)
				.items(new TestData(1), new TestData(2)) // set the items
				.itemCaptionGenerator(i -> i.getDescription()) // <1>
				.itemIconGenerator(i -> i.getId() == 1 ? VaadinIcons.STAR : VaadinIcons.STAR_O) // <2>
				.build();
		// end::selectable6[]
	}

	public void selectable7() {
		// tag::selectable7[]
		SingleSelect<TestData> singleSelect = Components.input.singleSelect(TestData.class)
				.items(new TestData(1), new TestData(2)) // set the items
				.filteringMode(
						(itemCaption, filterText) -> itemCaption.toLowerCase().startsWith(filterText.toLowerCase())) // <1>
				.build();
		// end::selectable7[]
	}

	public void selectable8() {
		// tag::selectable8[]
		final TestData ONE = new TestData(1);
		final TestData TWO = new TestData(2);

		SingleSelect<TestData> singleSelect = Components.input.singleSelect(TestData.class).dataSource(
				ItemDataProvider.create(q -> 2, (q, o, l) -> Stream.of(ONE, TWO)),
				filterText -> QueryFilter.startsWith(ConstantExpression.create("description"), filterText, true))
				.build();
		// end::selectable8[]
	}

	public void selectable9() {
		// tag::selectable9[]
		Datastore datastore = obtainDatastore();

		final PathProperty<Long> ID = PathProperty.create("id", Long.class);
		final PathProperty<String> DESCRIPTION = PathProperty.create("description", String.class);

		SingleSelect<Long> singleSelect = Components.input.singleSelect(ID) // <1>
				.dataSource(datastore, DataTarget.named("testData"), PropertySet.of(ID, DESCRIPTION)) // <2>
				.itemCaptionGenerator(propertyBox -> propertyBox.getValue(DESCRIPTION)) // <3>
				.build();

		singleSelect.setValue(Long.valueOf(1)); // <4>
		Long selectedId = singleSelect.getValue(); // <5>

		singleSelect.refresh(); // <6>
		// end::selectable9[]
	}

	public void selectable10() {
		// tag::selectable10[]
		Datastore datastore = obtainDatastore();

		final PathProperty<Long> ID = PathProperty.create("id", Long.class);
		final PathProperty<String> DESCRIPTION = PathProperty.create("description", String.class);

		SingleSelect<Long> singleSelect = Components.input.singleSelect(ID)
				.dataSource(datastore, DataTarget.named("testData"), PropertySet.of(ID, DESCRIPTION), // Datastore
						() -> ID.gt(0L)) // <1>
				.itemCaptionGenerator(propertyBox -> propertyBox.getValue(DESCRIPTION)).build();
		// end::selectable10[]
	}

	private static Datastore obtainDatastore() {
		return null;
	}

	private class TestData {

		private final long id;
		private String description;

		public TestData(long id) {
			super();
			this.id = id;
		}

		public long getId() {
			return id;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

	}

}
