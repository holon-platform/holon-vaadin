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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyValueConverter;
import com.holonplatform.vaadin.components.Components;
import com.holonplatform.vaadin.components.Input;
import com.holonplatform.vaadin.components.SingleSelect;
import com.holonplatform.vaadin.internal.components.StringField;
import com.holonplatform.vaadin.test.data.TestBean;
import com.holonplatform.vaadin.test.data.TestEnum1;
import com.vaadin.data.Converter;
import com.vaadin.data.Result;
import com.vaadin.data.ValueContext;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.data.converter.StringToLongConverter;
import com.vaadin.ui.TextField;

public class TestInput {

	@Test
	public void testEmptyInput() {

		StringField sf = new StringField();
		assertTrue(sf.isEmpty());

		Input<String> si = Components.input.string().emptyValuesAsNull(false).blankValuesAsNull(false).build();
		assertTrue(si.isEmpty());
		assertEquals("", si.getValue());

		si = Components.input.string().emptyValuesAsNull(true).blankValuesAsNull(false).build();
		assertTrue(si.isEmpty());
		assertNull(si.getValue());

		si.setValue(" ");
		assertFalse(si.isEmpty());
		assertNotNull(si.getValue());

		si = Components.input.string().emptyValuesAsNull(true).blankValuesAsNull(true).build();
		assertTrue(si.isEmpty());
		assertNull(si.getValue());

		si.setValue(" ");
		assertTrue(si.isEmpty());
		assertNull(si.getValue());

		si.setValue("a");
		assertFalse(si.isEmpty());
		assertNotNull(si.getValue());
		assertEquals("a", si.getValue());

		// area

		si = Components.input.string(true).emptyValuesAsNull(false).blankValuesAsNull(false).build();
		assertTrue(si.isEmpty());
		assertEquals("", si.getValue());

		si = Components.input.string(true).emptyValuesAsNull(true).blankValuesAsNull(false).build();
		assertTrue(si.isEmpty());
		assertNull(si.getValue());

		si.setValue(" ");
		assertFalse(si.isEmpty());
		assertNotNull(si.getValue());

		si = Components.input.string(true).emptyValuesAsNull(true).blankValuesAsNull(true).build();
		assertTrue(si.isEmpty());
		assertNull(si.getValue());

		si.setValue(" ");
		assertTrue(si.isEmpty());
		assertNull(si.getValue());

		// number

		Input<Integer> ii = Components.input.number(Integer.class).build();
		assertTrue(ii.isEmpty());
		assertNull(ii.getValue());

		// enum

		SingleSelect<TestEnum1> es = Components.input.enumSingle(TestEnum1.class).build();
		assertTrue(es.isEmpty());
		assertNull(es.getValue());

		es.setValue(TestEnum1.A);
		assertFalse(es.isEmpty());
		assertNotNull(es.getValue());
		assertEquals(TestEnum1.A, es.getValue());

		// select

		SingleSelect<TestBean> ss = Components.input.singleSelect(TestBean.class).addItem(new TestBean("1", "a"))
				.addItem(new TestBean("2", "b")).build();
		assertTrue(ss.isEmpty());
		assertNull(ss.getValue());

		ss.setValue(new TestBean("1", "a"));
		assertFalse(ss.isEmpty());
		assertNotNull(ss.getValue());
		assertEquals(new TestBean("1", "a"), ss.getValue());

		ss = Components.input.singleSelect(TestBean.class).addItem(new TestBean("1", "a"))
				.addItem(new TestBean("2", "b")).emptySelectionCaption("EMPTY").build();
		assertTrue(ss.isEmpty());
		assertNull(ss.getValue());

		ss = Components.input.singleSelect(TestBean.class).addItem(new TestBean("1", "a"))
				.addItem(new TestBean("2", "b")).emptySelectionAllowed(false).build();
		assertTrue(ss.isEmpty());
		assertNull(ss.getValue());

	}

	@SuppressWarnings("serial")
	@Test
	public void testInputConverter() {

		Input<Integer> ii = Components.input.number(Integer.class).build();

		Input<Boolean> bi = Input.from(ii, new Converter<Integer, Boolean>() {

			@Override
			public Result<Boolean> convertToModel(Integer value, ValueContext context) {
				return Result.ok((value == null) ? Boolean.FALSE : (value.intValue() > 0));
			}

			@Override
			public Integer convertToPresentation(Boolean value, ValueContext context) {
				return (value == null) ? null : (value ? 1 : 0);
			}
		});

		bi.addValueChangeListener(e -> {
		});

		assertFalse(bi.getValue());
		bi.setValue(true);
		assertTrue(bi.getValue());
		bi.setValue(false);
		assertFalse(bi.getValue());

		final Property<Boolean> PRP = PathProperty.create("prp", Boolean.class);

		bi = Input.from(ii, PRP, PropertyValueConverter.numericBoolean(Integer.class));

		assertFalse(bi.getValue());
		bi.setValue(true);
		assertTrue(bi.getValue());
		bi.setValue(false);
		assertFalse(bi.getValue());

		Input<Long> li = Input.from(new TextField(), new StringToLongConverter("error"));

		assertNull(li.getValue());
		li.setValue(1L);
		assertEquals(Long.valueOf(1), li.getValue());

	}
	
	@Test
	public void testFieldConverter() {
		TextField tf = new TextField();
		
		Input<Integer> i = Input.from(tf, new StringToIntegerConverter("error"));
		
		i.setValue(1);
		assertEquals("1", tf.getValue());
		
		tf.setValue("2");
		assertEquals(Integer.valueOf(2), i.getValue());
		
	}

}
