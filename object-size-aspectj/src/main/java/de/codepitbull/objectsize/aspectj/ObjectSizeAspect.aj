package de.codepitbull.objectsize.aspectj;

import de.codepitbull.objectsize.agent.ISizeOfStrategy;
import de.codepitbull.objectsize.agent.SizeInstrumentationAgent;
import de.codepitbull.objectsize.strategy.DeepInstrumentationSizeOfStrategy;

public aspect ObjectSizeAspect {

	public ObjectSizeAspect() {
		if (System.getProperty("objectsize.strategy") != null)
			try {
				SizeInstrumentationAgent
						.setStrategy((ISizeOfStrategy)Class.forName(System.getProperty("objectsize.strategy")).newInstance());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		else
			SizeInstrumentationAgent
					.setStrategy(new DeepInstrumentationSizeOfStrategy());
	}

	public pointcut detachCall():
		 execution(void org.apache.wicket.Page.detachModels());

	after() returning: detachCall() {
		process(thisJoinPoint.getTarget(), "after detachModel");
	}

	before() : detachCall() {
		process(thisJoinPoint.getTarget(), "before detachModel");
	}

	public void process(Object target, String position) {
		System.out.println(position + " "
				+ SizeInstrumentationAgent.sizeOf(target) + " "
				+ target.getClass() + target);
	}

}
