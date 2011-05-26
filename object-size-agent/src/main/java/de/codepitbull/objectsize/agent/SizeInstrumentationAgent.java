package de.codepitbull.objectsize.agent;

import java.lang.instrument.Instrumentation;

public class SizeInstrumentationAgent {
	private static ISizeOfStrategy sizeOfStrategy;

	private static Instrumentation instrumentation;

	public static void agentmain(String agentArgs, Instrumentation instr) {
		instrumentation = instr;
	}

	public static void premain(String agentArgs, Instrumentation instr) {
		instrumentation = instr;
	}

	public static void setStrategy(ISizeOfStrategy strategy) {
		sizeOfStrategy = strategy;
	}

	public static Long sizeOf(Object object) {
		if (sizeOfStrategy == null)
			throw new RuntimeException("Strategy has not been set!");
		return sizeOfStrategy.sizeOf(object, instrumentation);
	}

}
