package de.codepitbull;

import java.lang.instrument.Instrumentation;

public interface ISizeOfStrategy {
	Long sizeOf(Object object, Instrumentation instrumentation);
}
