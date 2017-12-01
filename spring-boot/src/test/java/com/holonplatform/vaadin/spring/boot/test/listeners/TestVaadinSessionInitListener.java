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
package com.holonplatform.vaadin.spring.boot.test.listeners;

import org.springframework.stereotype.Component;

import com.vaadin.server.ServiceException;
import com.vaadin.server.SessionInitEvent;
import com.vaadin.server.SessionInitListener;

@Component
@SuppressWarnings("serial")
public class TestVaadinSessionInitListener implements SessionInitListener {

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.server.SessionInitListener#sessionInit(com.vaadin.server.SessionInitEvent)
	 */
	@Override
	public void sessionInit(SessionInitEvent event) throws ServiceException {

	}

}
