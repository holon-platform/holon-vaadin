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
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

import com.holonplatform.vaadin.components.Components;
import com.holonplatform.vaadin.components.MultiSelect;
import com.holonplatform.vaadin.components.SingleSelect;
import com.holonplatform.vaadin.test.data.TestBean;
import com.holonplatform.vaadin.test.data.TestEnum1;

public class TestSelectInput {

	@Test
	public void testInitialValue() {

		SingleSelect<TestEnum1> es = Components.input.enumSingle(TestEnum1.class).withValue(TestEnum1.B).build();
		assertFalse(es.isEmpty());
		assertNotNull(es.getValue());
		assertEquals(TestEnum1.B, es.getValue());

		MultiSelect<TestEnum1> ems = Components.input.enumMulti(TestEnum1.class).withValue(TestEnum1.A, TestEnum1.B)
				.build();
		assertFalse(ems.isEmpty());
		final Set<TestEnum1> values = ems.getValue();
		assertNotNull(values);
		assertTrue(values.contains(TestEnum1.A));
		assertTrue(values.contains(TestEnum1.B));

		SingleSelect<TestBean> ss = Components.input.singleSelect(TestBean.class).addItem(new TestBean("1", "a"))
				.addItem(new TestBean("2", "b")).withValue(new TestBean("2", "b")).build();
		assertFalse(ss.isEmpty());
		assertNotNull(ss.getValue());
		assertEquals(new TestBean("2", "b"), ss.getValue());

		MultiSelect<TestBean> ms = Components.input.multiSelect(TestBean.class).addItem(new TestBean("1", "a")).addItem(new TestBean("2", "b"))
				.addItem(new TestBean("3", "c")).withValue(new TestBean("2", "b"), new TestBean("3", "c")).build();
		assertFalse(ms.isEmpty());
		final Set<TestBean> mvalues = ms.getValue();
		assertNotNull(mvalues);
		assertTrue(mvalues.contains(new TestBean("2", "b")));
		assertTrue(mvalues.contains(new TestBean("3", "c")));

	}

}
