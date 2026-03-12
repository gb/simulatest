package org.simulatest.environment.junit5.descriptor;

import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.platform.engine.support.descriptor.ClassSource;
import org.junit.platform.engine.support.hierarchical.Node;
import org.simulatest.environment.junit5.SimulatestExecutionContext;

/**
 * Describes a test class within an environment.
 * Pass-through container for method-level descriptors.
 */
public class ClassTestDescriptor extends AbstractTestDescriptor
		implements Node<SimulatestExecutionContext> {

	private final Class<?> testClass;

	public ClassTestDescriptor(UniqueId uniqueId, Class<?> testClass) {
		super(uniqueId, testClass.getSimpleName(), ClassSource.from(testClass));
		this.testClass = testClass;
	}

	public Class<?> getTestClass() {
		return testClass;
	}

	@Override
	public Type getType() {
		return Type.CONTAINER;
	}

}
