package de.codepitbull.objectsize.aspectj;

import java.util.ArrayList;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;

import de.codepitbull.objectsize.agent.ISizeOfStrategy;
import de.codepitbull.objectsize.agent.SizeInstrumentationAgent;
import de.codepitbull.objectsize.jmx.PageSizeResultOpenMBean;
import de.codepitbull.objectsize.strategy.DeepInstrumentationSizeOfStrategy;

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
