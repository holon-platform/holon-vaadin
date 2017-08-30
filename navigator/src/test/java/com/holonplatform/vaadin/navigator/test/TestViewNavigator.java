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
package com.holonplatform.vaadin.navigator.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.holonplatform.auth.AuthContext;
import com.holonplatform.auth.Realm;
import com.holonplatform.core.Context;
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.core.internal.utils.TestUtils;
import com.holonplatform.vaadin.internal.test.AbstractVaadinTest;
import com.holonplatform.vaadin.navigator.ViewNavigator;
import com.holonplatform.vaadin.navigator.ViewNavigator.ViewNavigationException;
import com.holonplatform.vaadin.navigator.internal.DefaultViewConfigurationCache;
import com.holonplatform.vaadin.navigator.internal.DefaultViewProvider;
import com.holonplatform.vaadin.navigator.internal.ViewDisplayUtils;
import com.holonplatform.vaadin.navigator.internal.ViewNavigationUtils;
import com.holonplatform.vaadin.navigator.test.components.ContextTestData;
import com.holonplatform.vaadin.navigator.test.components.NavigatorTestUI;
import com.holonplatform.vaadin.navigator.test.components.ViewFive;
import com.holonplatform.vaadin.navigator.test.components.ViewFour;
import com.holonplatform.vaadin.navigator.test.components.ViewOne;
import com.holonplatform.vaadin.navigator.test.components.ViewSeven;
import com.holonplatform.vaadin.navigator.test.components.ViewSix;
import com.holonplatform.vaadin.navigator.test.components.ViewThree;
import com.holonplatform.vaadin.navigator.test.components.ViewTwo;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class TestViewNavigator extends AbstractVaadinTest {

	private final static String VIEW_ONE = "/one";
	private final static String VIEW_TWO = "/two";
	private final static String VIEW_THREE = "/three";
	private final static String VIEW_FOUR = "/four";
	private final static String VIEW_FIVE = "/five";
	private final static String VIEW_SIX = "/six";
	private final static String VIEW_SEVEN = "/seven";

	private Panel viewer;

	public TestViewNavigator() {
		super();
		viewer = new Panel();
	}

	@Test
	public void testUtils() {

		TestUtils.checkUtilityClass(ViewDisplayUtils.class);
		TestUtils.checkUtilityClass(ViewNavigationUtils.class);

		TestUtils.checkEnum(DefaultViewConfigurationCache.class);

	}

	@SuppressWarnings("serial")
	@Test
	public void testNavigator() {

		final AtomicInteger counter = new AtomicInteger();

		Context.get().threadScope()
				.map(s -> s.put(LocalizationContext.CONTEXT_KEY, LocalizationContext.builder().build()));
		Context.get().threadScope()
				.map(s -> s.put(AuthContext.CONTEXT_KEY, AuthContext.create(Realm.builder().build())));

		NavigatorTestUI ui = createUi(NavigatorTestUI.class, "http://localhost");

		DefaultViewProvider provider = new DefaultViewProvider();
		provider.registerView(VIEW_ONE, ViewOne.class);
		provider.registerView(VIEW_TWO, ViewTwo.class);
		provider.registerView(VIEW_THREE, ViewThree.class);
		provider.registerView(VIEW_SIX, ViewSix.class);

		ViewNavigator navigator = ViewNavigator.builder().viewDisplay(viewer).addProvider(provider).buildAndBind(ui);

		assertNotNull(navigator);

		assertNotNull(ViewNavigator.require());

		assertNull(navigator.getCurrentViewName());
		assertNull(navigator.getCurrentView());

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

		TestUtils.expectedException(ViewNavigationException.class, () -> navigator.toView(VIEW_FIVE).navigate());

		Context.get().threadScope().map(s -> s.remove(LocalizationContext.CONTEXT_KEY));
		Context.get().threadScope().map(s -> s.remove(AuthContext.CONTEXT_KEY));
	}

	@Test
	public void testViewParameters() {

		NavigatorTestUI ui = createUi(NavigatorTestUI.class, "http://localhost");

		DefaultViewProvider provider = new DefaultViewProvider();
		provider.registerView(VIEW_ONE, ViewOne.class);
		provider.registerView(VIEW_TWO, ViewTwo.class);
		provider.registerView(VIEW_THREE, ViewThree.class);

		ViewNavigator navigator = ViewNavigator.builder().viewDisplay(viewer).addProvider(provider).buildAndBind(ui);

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

		NavigatorTestUI ui = createUi(NavigatorTestUI.class, "http://localhost");

		DefaultViewProvider provider = new DefaultViewProvider();
		provider.registerView(VIEW_ONE, ViewOne.class);
		provider.registerView(VIEW_TWO, ViewTwo.class);
		provider.registerView(VIEW_THREE, ViewThree.class);
		provider.registerView(VIEW_FOUR, ViewFour.class);

		ViewNavigator navigator = ViewNavigator.builder().viewDisplay(viewer).addProvider(provider).buildAndBind(ui);

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

		NavigatorTestUI ui = createUi(NavigatorTestUI.class, "http://localhost");

		DefaultViewProvider provider = new DefaultViewProvider();
		provider.registerView(VIEW_ONE, ViewOne.class);
		provider.registerView(VIEW_TWO, ViewTwo.class);
		provider.registerView(VIEW_THREE, ViewThree.class);
		provider.registerView(VIEW_FOUR, ViewFour.class);
		provider.registerView(VIEW_FIVE, ViewFive.class);

		ViewNavigator navigator = ViewNavigator.builder().viewDisplay(viewer).addProvider(provider).buildAndBind(ui);

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

		NavigatorTestUI ui = createUi(NavigatorTestUI.class, "http://localhost");

		final VerticalLayout vd = new VerticalLayout();

		DefaultViewProvider provider = new DefaultViewProvider();
		provider.registerView(VIEW_ONE, ViewOne.class);
		provider.registerView(VIEW_SEVEN, ViewSeven.class);

		Context.get().scope(Context.CLASSLOADER_SCOPE_NAME)
				.map(s -> s.put(ContextTestData.class.getName(), new ContextTestData(1)));

		ViewNavigator navigator = ViewNavigator.builder().viewDisplay(vd).addProvider(provider)
				.maxNavigationHistorySize(10).defaultViewName(VIEW_ONE).buildAndBind(ui);

		navigator.navigateToDefault();

		assertEquals(VIEW_ONE, navigator.getCurrentViewName());

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

		Calendar c = Calendar.getInstance(Locale.ITALIAN);
		c.set(Calendar.DAY_OF_MONTH, 9);
		c.set(Calendar.MONTH, 2);
		c.set(Calendar.YEAR, 1979);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);

		navigator.toView(VIEW_SEVEN).withParameter("param2", "x").withParameter("paramDate", c.getTime()).navigate();

		assertEquals(VIEW_SEVEN, navigator.getCurrentViewName());

		view = (ViewSeven) navigator.getCurrentView();
		assertNotNull(view);

		assertEquals(c.getTime(), view.getParamDate());

		LocalDate date = LocalDate.of(1979, Month.MARCH, 9);
		LocalTime time = LocalTime.of(18, 30, 15);
		LocalDateTime dt = LocalDateTime.of(1979, Month.MARCH, 9, 18, 30, 15);

		navigator.toView(VIEW_SEVEN).withParameter("param2", "x").withParameter("paramLocalDate", date)
				.withParameter("paramLocalTime", time).withParameter("paramLocalDateTime", dt).navigate();

		assertEquals(VIEW_SEVEN, navigator.getCurrentViewName());

		view = (ViewSeven) navigator.getCurrentView();
		assertNotNull(view);

		assertEquals(date, view.getParamLocalDate());
		assertEquals(time, view.getParamLocalTime());
		assertEquals(dt, view.getParamLocalDateTime());
	}

}
