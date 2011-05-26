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
package de.codepitbull.visitor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Proxy to be used to get a visitor into Wicket without actually depending on
 * wicket.
 * 
 * {@link IVisitorProxy}
 * 
 * @author Jochen Mader
 * 
 */
public class VisitorProxyListener implements
		java.lang.reflect.InvocationHandler {

	private IVisitorProxy iVisitorProxy;

	public static void create(Object target, IVisitorProxy iVisitorProxy) {

		for (Method method : target.getClass().getMethods()) {
			if ("visitChildren".equals(method.getName())
					&& method.getParameterTypes().length == 1) {
				try {
					Class ivisitor = Class.forName(
							"org.apache.wicket.Component$IVisitor", true,
							target.getClass().getClassLoader());
					Object o = Proxy.newProxyInstance(
							ivisitor.getClassLoader(),
							new Class[] { ivisitor }, new VisitorProxyListener(
									iVisitorProxy));
					method.invoke(target, o);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public VisitorProxyListener(IVisitorProxy iVisitorProxy) {
		this.iVisitorProxy = iVisitorProxy;
	}

	public Object invoke(Object arg0, Method arg1, Object[] arg2)
			throws Throwable {
		iVisitorProxy.visit(arg2[0]);
		return null;
	}
}
