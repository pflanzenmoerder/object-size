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
package de.codepitbull.objectsize.agent;

import java.lang.instrument.Instrumentation;

/**
 * Agent for getting hold of the Instrumentation and providing it to the
 * selected size strategy.
 * 
 * @author Jochen Mader
 * 
 */
public class SizeInstrumentationAgent {
	private static ISizeOfStrategy sizeOfStrategy;

	private static Instrumentation instrumentation;

	public static void agentmain(String agentArgs, Instrumentation instr) {
		instrumentation = instr;
	}

	public static void premain(String agentArgs, Instrumentation instr) {
		instrumentation = instr;
	}

	/**
	 * Register the size strategy.
	 * 
	 * @param strategy
	 */
	public static void setStrategy(ISizeOfStrategy strategy) {
		sizeOfStrategy = strategy;
	}

	/**
	 * Calculate the size of the object using the registered strategy.
	 * 
	 * @param object
	 * @return
	 */
	public static Long sizeOf(Object object) {
		if (sizeOfStrategy == null)
			throw new RuntimeException("Strategy has not been set!");
		return sizeOfStrategy.sizeOf(object, instrumentation);
	}

}
