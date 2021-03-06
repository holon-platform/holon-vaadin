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
package com.holonplatform.vaadin.spring.boot.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.holonplatform.vaadin.spring.boot.test.servlet.TestVaadinServlet;
import com.vaadin.server.VaadinServlet;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TestVaadinServletOverride {

	@Configuration
	@ComponentScan(basePackageClasses = TestVaadinServlet.class)
	@EnableAutoConfiguration
	static class Config {

	}

	@Autowired
	private VaadinServlet servlet;

	@Test
	public void testServlet() {
		assertNotNull(servlet);
		assertEquals(TestVaadinServlet.class, servlet.getClass());
	}

}
