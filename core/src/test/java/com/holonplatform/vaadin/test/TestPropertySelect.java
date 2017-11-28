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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.property.PropertyValueConverter;
import com.holonplatform.datastore.jdbc.spring.EnableJdbcDatastore;
import com.holonplatform.jdbc.spring.EnableDataSource;
import com.holonplatform.vaadin.components.Components;
import com.holonplatform.vaadin.components.MultiSelect;
import com.holonplatform.vaadin.components.SingleSelect;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestPropertySelect.Config.class)
public class TestPropertySelect {

	@Configuration
	@PropertySource("test_datastore.properties")
	@EnableDataSource
	@EnableJdbcDatastore
	protected static class Config {

	}

	private static final DataTarget<?> TARGET = DataTarget.named("testdata");

	private static final PathProperty<String> CODE = PathProperty.create("code", String.class);
	private static final PathProperty<String> DESCRIPTION = PathProperty.create("description", String.class);
	private static final PathProperty<Integer> SEQUENCE = PathProperty.create("sequence", Integer.class);
	private static final PathProperty<Boolean> OBSOLETE = PathProperty.create("obsolete", Boolean.class)
			.converter(PropertyValueConverter.numericBoolean(Integer.class));

	private static final PropertySet<?> PROPERTIES = PropertySet.of(CODE, DESCRIPTION, SEQUENCE, OBSOLETE);

	@Autowired
	private Datastore datastore;

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
