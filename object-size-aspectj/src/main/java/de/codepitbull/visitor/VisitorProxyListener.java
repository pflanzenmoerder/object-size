package de.codepitbull.visitor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import de.codepitbull.SizeInstrumentationAgent;

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
							new Class[] { ivisitor }, new VisitorProxyListener(iVisitorProxy));
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
