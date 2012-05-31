package org.simulatest.jee5runner.jee5;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.simulatest.environment.infra.EnvironmentInstantiationException;

public class JEE5Context {
	
	private static InitialContext initialContext;
	private static JEE5RunnerSupport jee5RunnerSupport = new JEE5RunnerSupport();
	
	public static Object lookup(Class<?> clazz) {
		return lookup(clazz, clazz.getSimpleName() + "/local");
	}
	
	private static Object lookup(Class<?> clazz, String lookupName) {
		try {
			return getInitialContext().lookup(lookupName);
		} catch (NamingException exception) {
			throw new EnvironmentInstantiationException(exception);
		}
	}
	
	private static InitialContext getInitialContext() {
		if (initialContext == null) setup();
		return initialContext;
	}

	private static void setup() {
		try {
			jee5RunnerSupport.startupEmbeddedContainer();
			initialContext = new InitialContext();
		} catch (NamingException exception) {
			throw new EnvironmentInstantiationException(exception);
		} catch (Exception exception) {
			throw new EnvironmentInstantiationException(exception);
		}
	}

}