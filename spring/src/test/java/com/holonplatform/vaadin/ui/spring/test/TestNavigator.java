/*
 * Copyright 2000-2016 Holon TDCN.
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
package com.holonplatform.vaadin.ui.spring.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import com.holonplatform.auth.AuthContext;
import com.holonplatform.auth.Realm;
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.core.internal.utils.TestUtils;
import com.holonplatform.spring.EnableBeanContext;
import com.holonplatform.vaadin.navigator.ViewNavigator;
import com.holonplatform.vaadin.navigator.ViewNavigator.ViewNavigationException;
import com.holonplatform.vaadin.spring.config.EnableViewContext;
import com.holonplatform.vaadin.spring.config.EnableViewNavigator;
import com.holonplatform.vaadin.spring.utils.AbstractVaadinSpringTest;
import com.holonplatform.vaadin.ui.spring.test.components.ContextTestData;
import com.holonplatform.vaadin.ui.spring.test.components.SpringTestUI;
import com.holonplatform.vaadin.ui.spring.test.components.ViewDisplayPanel;
import com.holonplatform.vaadin.ui.spring.test.components.ViewFive;
import com.holonplatform.vaadin.ui.spring.test.components.ViewFour;
import com.holonplatform.vaadin.ui.spring.test.components.ViewOne;
import com.holonplatform.vaadin.ui.spring.test.components.ViewSeven;
import com.holonplatform.vaadin.ui.spring.test.components.ViewSix;
import com.holonplatform.vaadin.ui.spring.test.components.ViewThree;
import com.holonplatform.vaadin.ui.spring.test.components.ViewTwo;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

@ContextConfiguration
@DirtiesContext
public class TestNavigator extends AbstractVaadinSpringTest {

	public final static String VIEW_HOME = "/home";

	public final static String VIEW_ONE = "/one";
	public final static String VIEW_TWO = "/two";
	public final static String VIEW_THREE = "/three";
	public final static String VIEW_FOUR = "/four";
	public final static String VIEW_FIVE = "/five";
	public final static String VIEW_SIX = "/six";
	public final static String VIEW_SEVEN = "/seven";

	@Configuration
	@EnableBeanContext
	@EnableViewContext
	@EnableViewNavigator
	@ComponentScan(basePackageClasses = ViewOne.class)
	static class Config extends AbstractVaadinSpringTest.Config {

		@Bean
		public LocalizationContext localizationContext() {
			return LocalizationContext.builder().build();
		}

		@Bean
		public AuthContext authContext() {
			return AuthContext.create(Realm.builder().build());
		}

		@Bean
		public ContextTestData contextTestData() {
			return new ContextTestData(1);
		}

	}

	private ViewNavigator navigator;
	private Panel viewer;

	private UI ui;

	@Override
	public void setup() throws Exception {
		super.setup();
		navigator = applicationContext.getBean(ViewNavigator.class);
		viewer = applicationContext.getBean(ViewDisplayPanel.class);

		ui = createUi(SpringTestUI.class, "http://localhost");
	}

	@SuppressWarnings("serial")
	@Test
	public void testNavigator() {

		final AtomicInteger counter = new AtomicInteger();

		assertNotNull(navigator);

		assertTrue(ViewNavigator.getCurrent().isPresent());

		assertEquals(VIEW_HOME, navigator.getCurrentViewName());

		navigator.addViewChangeListener(new ViewChangeListener() {

			@Override
			public boolean beforeViewChange(ViewChangeEvent event) {
				return true;
			}

			@Override
			public void afterViewChange(ViewChangeEvent event) {
				counter.incrementAndGet();
			}
		});

		navigator.navigateTo(VIEW_ONE, null);

		assertEquals(1, counter.get());

		assertEquals(VIEW_ONE, navigator.getCurrentViewName());

		View current = navigator.getCurrentView();
		assertNotNull(current);

		assertTrue(current instanceof ViewOne);

		assertEquals(1, ((ViewOne) current).getEnterCount());

		navigator.navigateTo(VIEW_TWO, null);

		assertEquals(2, counter.get());

		assertEquals(VIEW_TWO, navigator.getCurrentViewName());

		ViewTwo viewTwo = (ViewTwo) navigator.getCurrentView();
		assertNotNull(viewTwo);

		assertEquals(2, viewTwo.getEnterCount());

		assertNotNull(viewTwo.getPreviousView());
		assertTrue(viewTwo.getPreviousView() instanceof ViewOne);

		assertNotNull(viewTwo.getNavigator());

		boolean back = navigator.navigateBack();

		assertTrue(back);

		assertEquals(1, viewTwo.getLeaveCount());

		assertEquals(3, counter.get());

		assertEquals(VIEW_ONE, navigator.getCurrentViewName());

		navigator.navigateTo(VIEW_TWO, null);

		assertEquals(4, counter.get());

		current = navigator.getCurrentView();

		assertEquals(viewTwo, current);

		navigator.toView(VIEW_SIX).navigate();

		ViewSix viewSix = (ViewSix) navigator.getCurrentView();
		assertNotNull(viewSix);

		assertNotNull(viewSix.getLocalizationContext());
		assertNotNull(viewSix.getAuthContext());

	}

	@Test
	public void testViewParameters() {

		Map<String, Object> parameters = new HashMap<>();
		parameters.put("pString", "test");
		parameters.put("intpar", 3);
		parameters.put("boolpar", Boolean.TRUE);
		parameters.put("notexist", "xxx");

		navigator.navigateTo(VIEW_THREE, parameters);

		View current = navigator.getCurrentView();
		assertNotNull(current);

		assertTrue(current instanceof ViewThree);

		ViewThree vt = (ViewThree) current;

		assertEquals("test", vt.getpString());
		assertEquals(3, vt.getIntParam());
		assertTrue(vt.getBoolParam());

		parameters = new HashMap<>();
		parameters.put("pString", "test2");

		navigator.navigateTo(VIEW_THREE, parameters);

		current = navigator.getCurrentView();
		assertNotNull(current);

		assertTrue(current instanceof ViewThree);

		vt = (ViewThree) current;

		assertEquals("test2", vt.getpString());
		assertEquals(0, vt.getIntParam());
		assertFalse(vt.getBoolParam());

		navigator.toView(VIEW_THREE).withParameter("pString", "test3").withParameter("intpar", 7).navigate();

		current = navigator.getCurrentView();
		assertNotNull(current);

		assertTrue(current instanceof ViewThree);

		vt = (ViewThree) current;

		assertEquals("test3", vt.getpString());
		assertEquals(7, vt.getIntParam());
		assertFalse(vt.getBoolParam());

		assertEquals("test3", vt.getStringOnShow());
		assertEquals(7, vt.getIntOnEnter());
	}

	@Test
	public void testVolatileView() {

		navigator.toView(VIEW_ONE).navigate();
		assertEquals(VIEW_ONE, navigator.getCurrentViewName());

		navigator.toView(VIEW_FOUR).navigate();
		assertEquals(VIEW_FOUR, navigator.getCurrentViewName());

		assertNotNull(viewer.getContent());

		assertTrue(viewer.getContent() instanceof Label);
		assertEquals("FOUR", ((Label) viewer.getContent()).getValue());

		View current = navigator.getCurrentView();
		assertNotNull(current);

		assertTrue(current instanceof ViewFour);

		navigator.toView(VIEW_TWO).navigate();
		assertEquals(VIEW_TWO, navigator.getCurrentViewName());

		navigator.navigateBack();
		assertEquals(VIEW_ONE, navigator.getCurrentViewName());

	}

	@Test
	public void testNavigateInWindow() {

		Window wnd = navigator.navigateInWindow(VIEW_FOUR, null, null);

		assertEquals(VIEW_FOUR, navigator.getCurrentViewName());

		assertTrue(wnd.getContent() instanceof Label);
		assertEquals("FOUR", ((Label) wnd.getContent()).getValue());

		assertEquals(1, ui.getWindows().size());

		navigator.navigateTo(VIEW_FIVE, Collections.singletonMap("testPar", "test5"));

		assertEquals(2, ui.getWindows().size());

		wnd = null;
		Iterator<Window> i = ui.getWindows().iterator();
		while (i.hasNext()) {
			wnd = i.next();
		}
		assertNotNull(wnd);

		assertTrue(wnd.getContent() instanceof Label);
		assertEquals("test5", ((Label) wnd.getContent()).getValue());

		View current = navigator.getCurrentView();
		assertNotNull(current);

		assertTrue(current instanceof ViewFive);

		assertTrue(((ViewFive) current).isShowed());
		assertTrue(((ViewFive) current).isEntered());

		assertTrue(navigator.navigateBack());

		assertEquals(VIEW_FOUR, navigator.getCurrentViewName());

		navigator.navigateTo(VIEW_ONE, null);

		assertEquals(0, ui.getWindows().size());

	}

	@Test
	public void testBuilder() {

		navigator.navigateToDefault();

		assertEquals(VIEW_HOME, navigator.getCurrentViewName());

		navigator.toView(VIEW_SEVEN).withParameter("param2", "x").navigate();

		assertEquals(VIEW_SEVEN, navigator.getCurrentViewName());

		ViewSeven view = (ViewSeven) navigator.getCurrentView();
		assertNotNull(view);

		assertNotNull(view.getContextData());

		assertEquals("DFT", view.getParam());

		navigator.toView(VIEW_SEVEN).withParameter("param2", "x").withParameter("param", "V").navigate();

		assertEquals(VIEW_SEVEN, navigator.getCurrentViewName());

		view = (ViewSeven) navigator.getCurrentView();
		assertNotNull(view);

		assertEquals("V", view.getParam());

		TestUtils.expectedException(ViewNavigationException.class, () -> navigator.toView(VIEW_SEVEN).navigate());
	}

}
