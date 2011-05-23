package de.codepitbull;

import de.codepitbull.strategy.DeepInstrumentationSizeOfStrategy;

public aspect ObjectSizeAspect {

	public ObjectSizeAspect() {
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
		System.out.println(position+" "+SizeInstrumentationAgent.sizeOf(target) + " "
				+ target.getClass() + target);
	}

}
