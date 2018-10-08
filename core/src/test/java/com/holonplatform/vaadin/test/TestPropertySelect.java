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
package com.holonplatform.vaadin.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.property.PropertyValueConverter;
import com.holonplatform.datastore.jdbc.JdbcDatastore;
import com.holonplatform.jdbc.DataSourceBuilder;
import com.holonplatform.vaadin.components.Components;
import com.holonplatform.vaadin.components.MultiSelect;
import com.holonplatform.vaadin.components.SingleSelect;

public class TestPropertySelect {

	private static Datastore datastore;

	@BeforeAll
	public static void initDatastore() {

		final DataSource dataSource = DataSourceBuilder.builder()
				.url("jdbc:h2:mem:vaadin1;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE").username("sa")
				.withInitScriptResource("test-db.sql").build();

		datastore = JdbcDatastore.builder().dataSource(dataSource).traceEnabled(true).build();
	}

	private static final DataTarget<?> TARGET = DataTarget.named("testdata");

	private static final PathProperty<String> CODE = PathProperty.create("code", String.class);
	private static final PathProperty<String> DESCRIPTION = PathProperty.create("description", String.class);
	private static final PathProperty<Integer> SEQUENCE = PathProperty.create("sequence", Integer.class);
	private static final PathProperty<Boolean> OBSOLETE = PathProperty.create("obsolete", Boolean.class)
			.converter(PropertyValueConverter.numericBoolean(Integer.class));

	private static final PropertySet<?> PROPERTIES = PropertySet.of(CODE, DESCRIPTION, SEQUENCE, OBSOLETE);

	@Test
	public void testSetup() {
		long count = datastore.query().target(TARGET).count();
		assertTrue(count > 0);

		List<PropertyBox> values = datastore.query().target(TARGET).list(PROPERTIES);
		assertNotNull(values);
		assertTrue(values.size() > 0);
	}

	@Test
	public void testDefaultConfig() {
		SingleSelect<String> slt = Components.input.singleSelect(CODE).dataSource(datastore, TARGET, PROPERTIES)
				.build();
		assertNull(slt.getValue());

		MultiSelect<String> mslt = Components.input.multiSelect(CODE).dataSource(datastore, TARGET, PROPERTIES).build();
		assertTrue(mslt.getValue().isEmpty());
	}

	@Test
	public void testSetValue() {

		SingleSelect<String> slt = Components.input.singleSelect(CODE).dataSource(datastore, TARGET, PROPERTIES)
				.itemConverter(value -> (value == null) ? null
						: datastore.query().target(TARGET).filter(CODE.eq(value)).findOne(PROPERTIES).orElse(null))
				.build();

		slt.select("c3");

		assertNotNull(slt.getValue());
		assertEquals("c3", slt.getValue());

		MultiSelect<String> mslt = Components.input.multiSelect(CODE).dataSource(datastore, TARGET, PROPERTIES)
				.itemConverter(value -> (value == null) ? null
						: datastore.query().target(TARGET).filter(CODE.eq(value)).findOne(PROPERTIES).orElse(null))
				.build();

		mslt.select("c3", "c5");

		assertNotNull(mslt.getValue());
		assertTrue(mslt.getValue().contains("c3"));
		assertTrue(mslt.getValue().contains("c5"));

	}

	@Test
	public void testDatastoreItemConverter() {
		SingleSelect<String> slt = Components.input.singleSelect(CODE).dataSource(datastore, TARGET, PROPERTIES)
				.build();
		slt.select("c3");

		assertNotNull(slt.getValue());
		assertEquals("c3", slt.getValue());

		MultiSelect<String> mslt = Components.input.multiSelect(CODE).dataSource(datastore, TARGET, PROPERTIES).build();

		mslt.select("c3", "c5");

		assertNotNull(mslt.getValue());
		assertTrue(mslt.getValue().contains("c3"));
		assertTrue(mslt.getValue().contains("c5"));
	}

}
