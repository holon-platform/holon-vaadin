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
package com.holonplatform.vaadin.spring.utils;

import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;

import com.holonplatform.vaadin.internal.test.AbstractVaadinTest;
import com.vaadin.server.DefaultDeploymentConfiguration;
import com.vaadin.server.ServiceException;
import com.vaadin.server.UIProvider;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.server.VaadinServletService;
import com.vaadin.server.VaadinSession;
import com.vaadin.spring.annotation.EnableVaadin;
import com.vaadin.spring.internal.BeanStore;
import com.vaadin.spring.internal.BeanStoreRetrievalStrategy;
import com.vaadin.spring.internal.UIScopeImpl;
import com.vaadin.spring.server.SpringUIProvider;
import com.vaadin.spring.server.SpringVaadinServlet;
import com.vaadin.spring.server.SpringVaadinServletRequest;
import com.vaadin.spring.server.SpringVaadinServletService;

/**
 * Abstract JUnit test class using Spring to enable a web environment to test Vaadin application elements with Spring
 * support.
 * 
 * @since 5.0.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@WebAppConfiguration
@DirtiesContext
public abstract class AbstractVaadinSpringTest extends AbstractVaadinTest {

	@Configuration
	@EnableVaadin
	public static class Config {

		@Autowired
		private WebApplicationContext appContext;

		@Bean
		public VaadinSession vaadinSession() throws Exception {
			return new TestVaadinSpringSession(appContext);
		}

	}

	@Autowired
	protected WebApplicationContext applicationContext;

	@Autowired
	protected HttpServletRequest request;

	@Before
	@Override
	public void setup() throws Exception {
		UIScopeImpl.setBeanStoreRetrievalStrategy(new TestSingletonBeanStoreRetrievalStrategy());
		super.setup();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.core.utils.test.AbstractVaadinTest#createVaadinSession(java.util.Locale)
	 */
	@Override
	protected VaadinSession createVaadinSession(Locale locale) throws Exception {
		return applicationContext.getBean(VaadinSession.class);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.core.utils.test.AbstractVaadinTest#buildUiProvider()
	 */
	@Override
	protected UIProvider buildUiProvider() {
		return new SpringUIProvider(vaadinSession);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.core.utils.test.AbstractVaadinTest#buildVaadinRequest()
	 */
	@Override
	protected VaadinServletRequest buildVaadinRequest() {
		return new SpringVaadinServletRequest(request, (VaadinServletService) vaadinSession.getService(), false);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.utils.test.AbstractVaadinTest#buildVaadinRequest(java.lang.String)
	 */
	@SuppressWarnings("serial")
	@Override
	protected VaadinServletRequest buildVaadinRequest(String location) {
		return new SpringVaadinServletRequest(request, (VaadinServletService) vaadinSession.getService(), false) {

			/*
			 * (non-Javadoc)
			 * @see javax.servlet.ServletRequestWrapper#getParameter(java.lang.String)
			 */
			@Override
			public String getParameter(String name) {
				if ("v-loc".equals(name)) {
					return location;
				}
				return super.getParameter(name);
			}
		};
	}

	@SuppressWarnings("serial")
	public static class TestVaadinSpringSession extends VaadinSession {

		private final Lock lock = new ReentrantLock();

		public TestVaadinSpringSession(WebApplicationContext applicationContext) throws ServiceException {
			super(new TestSpringVaadinServletService(new SpringVaadinServlet(), applicationContext));
		}

		@Override
		public boolean hasLock() {
			return true;
		}

		/*
		 * (non-Javadoc)
		 * @see com.vaadin.server.VaadinSession#getLockInstance()
		 */
		@Override
		public Lock getLockInstance() {
			return lock;
		}

	}

	public static class TestSingletonBeanStoreRetrievalStrategy implements BeanStoreRetrievalStrategy {

		public static final String CONVERSATION_ID = "testConversation";

		private BeanStore beanStore = new BeanStore("testBeanStore");

		@Override
		public BeanStore getBeanStore() {
			return beanStore;
		}

		@Override
		public String getConversationId() {
			return CONVERSATION_ID;
		}

	}

	@SuppressWarnings("serial")
	public static class TestSpringVaadinServletService extends SpringVaadinServletService {

		private WebApplicationContext appContext;

		public TestSpringVaadinServletService(VaadinServlet servlet, WebApplicationContext applicationContext)
				throws ServiceException {
			super(servlet, new DefaultDeploymentConfiguration(TestSpringVaadinServletService.class, new Properties()),
					"");
			this.appContext = applicationContext;
			init();
		}

		@Override
		public WebApplicationContext getWebApplicationContext() {
			return appContext;
		}
	}

}
