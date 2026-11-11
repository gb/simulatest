package org.simulatest.environment.test.testdouble;

import org.simulatest.environment.Environment;
import org.simulatest.environment.EnvironmentDefinition;
import org.simulatest.environment.EnvironmentFactory;
import org.simulatest.environment.EnvironmentReflectionFactory;

/** Test Stub factory that forces a specific environment class to throw from {@code run()}. */
public final class ThrowingEnvironmentFactory implements EnvironmentFactory {

	private final Class<? extends Environment> throwingClass;
	private final EnvironmentFactory delegate = new EnvironmentReflectionFactory();

	public ThrowingEnvironmentFactory(Class<? extends Environment> throwingClass) {
		this.throwingClass = throwingClass;
	}

	@Override
	public Environment create(EnvironmentDefinition definition) {
		if (throwingClass.equals(definition.getEnvironmentClass())) {
			return () -> { throw new RuntimeException("simulated environment failure"); };
		}
		return delegate.create(definition);
	}

}
