package org.simulatest.environment.junit5.descriptor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.platform.engine.support.descriptor.MethodSource;
import org.junit.platform.engine.support.hierarchical.Node;
import org.simulatest.environment.junit5.SimulatestExecutionContext;
import org.simulatest.environment.junit5.plugin.SimulatestEnginePlugin;
import org.simulatest.environment.junit5.plugin.TestInstantiationException;

/**
 * Describes a single test method. Executes the test and resets
 * the Insistence Layer savepoint after each method.
 */
public class MethodTestDescriptor extends AbstractTestDescriptor
		implements Node<SimulatestExecutionContext> {

	private final Class<?> testClass;
	private final Method testMethod;

	public MethodTestDescriptor(UniqueId uniqueId, Class<?> testClass, Method testMethod) {
		super(uniqueId, testMethod.getName(), MethodSource.from(testClass, testMethod));
		this.testClass = testClass;
		this.testMethod = testMethod;
	}

	public Class<?> getTestClass() {
		return testClass;
	}

	public Method getTestMethod() {
		return testMethod;
	}

	@Override
	public Type getType() {
		return Type.TEST;
	}

	@Override
	public SimulatestExecutionContext execute(SimulatestExecutionContext context,
			DynamicTestExecutor dynamicTestExecutor) throws Exception {
		Object instance = createTestInstance(context);
		for (SimulatestEnginePlugin plugin : context.plugins()) {
			plugin.postProcessTestInstance(instance);
		}

		testMethod.setAccessible(true);
		try {
			testMethod.invoke(instance);
		} catch (InvocationTargetException e) {
			throw (e.getCause() instanceof Exception ex) ? ex : e;
		}

		return context;
	}

	@Override
	public void after(SimulatestExecutionContext context) {
		if (context.insistenceLayer() != null) {
			context.insistenceLayer().resetCurrentLevel();
		}
	}

	private Object createTestInstance(SimulatestExecutionContext context) {
		for (SimulatestEnginePlugin plugin : context.plugins()) {
			Object instance = plugin.createTestInstance(testClass);
			if (instance != null) return instance;
		}
		try {
			return testClass.getDeclaredConstructor().newInstance();
		} catch (ReflectiveOperationException e) {
			throw new TestInstantiationException("Failed to instantiate test class: " + testClass.getName(), e);
		}
	}

}
