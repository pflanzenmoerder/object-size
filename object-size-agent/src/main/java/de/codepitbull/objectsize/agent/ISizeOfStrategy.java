package de.codepitbull.objectsize.agent;

import java.lang.instrument.Instrumentation;

public interface ISizeOfStrategy {
	Long sizeOf(Object object, Instrumentation instrumentation);
}
