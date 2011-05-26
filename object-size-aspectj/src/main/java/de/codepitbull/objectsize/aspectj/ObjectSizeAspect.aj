/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.codepitbull.objectsize.aspectj;

import java.util.ArrayList;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;

import de.codepitbull.objectsize.agent.ISizeOfStrategy;
import de.codepitbull.objectsize.agent.SizeInstrumentationAgent;
import de.codepitbull.objectsize.jmx.PageSizeResultOpenMBean;
import de.codepitbull.objectsize.strategy.DeepInstrumentationSizeOfStrategy;
/**
 * Aspect for capturing calls to detachModels calls on Wicket Pages.
 * {@link org.apache.wicket.Page}
 * Each calculated size will be reported to the MBean.
 * {@link de.codepitbull.objectsize.jmx.PageSizeResultOpenMBean}
 * 
 * @author Jochen Mader
 *
 */
public aspect ObjectSizeAspect {

	private PageSizeResult pageSizeResult = new PageSizeResult();

	private PageSizeResultOpenMBean pageSizeResultOpenMBean;

	public ObjectSizeAspect() {
		if (System.getProperty("objectsize.strategy") != null)
			try {
				SizeInstrumentationAgent.setStrategy((ISizeOfStrategy) Class
						.forName(System.getProperty("objectsize.strategy"))
						.newInstance());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		else
			SizeInstrumentationAgent
					.setStrategy(new DeepInstrumentationSizeOfStrategy());

		//Code found on StackOverflow
		ArrayList<MBeanServer> mbservers = MBeanServerFactory
				.findMBeanServer(null);
		int nservers = mbservers.size();
		MBeanServer mbserver = null;
		if (nservers > 0) {
			//
			// TODO: A better way to get the currently active server ?
			// For some reason, every time the webapp is reloaded there is one
			// more instance of the MBeanServer
			mbserver = (MBeanServer) mbservers.get(nservers - 1);
		}
		if (mbserver == null) {
			mbserver = MBeanServerFactory.createMBeanServer();
		}
		//End Code found on StackOverflow
		
		try {
			ObjectName name = new ObjectName("de.codepitbull.objectsize:name=PageSizeResultOpenMBean");
			pageSizeResultOpenMBean = new PageSizeResultOpenMBean();
			mbserver.registerMBean(pageSizeResultOpenMBean, name);

		} catch (Exception e) {
			// crash early, crash hard
			throw new RuntimeException(e);
		}

	}

	public pointcut detachCall():
		 execution(void org.apache.wicket.Page.detachModels());

	after() returning: detachCall() {
		pageSizeResult.pageClass = thisJoinPoint.getTarget().getClass();
		pageSizeResult.sizeAfterDetach = SizeInstrumentationAgent
				.sizeOf(thisJoinPoint.getTarget());
		pageSizeResultOpenMBean
				.addPageSizeResult(pageSizeResult);
	}

	before() : detachCall() {
		pageSizeResult.pageClass = thisJoinPoint.getTarget().getClass();
		pageSizeResult.sizeBeforeDetach = SizeInstrumentationAgent
				.sizeOf(thisJoinPoint.getTarget());
	}

}
