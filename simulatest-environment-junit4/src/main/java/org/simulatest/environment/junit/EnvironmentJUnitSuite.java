package org.simulatest.environment.junit;

import java.util.HashSet;
import java.util.Set;

import org.junit.runners.Suite.SuiteClasses;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;

public class EnvironmentJUnitSuite extends EnvironmentJUnitRunner {

	public EnvironmentJUnitSuite(Class<?> suiteClass, RunnerBuilder builder) throws InitializationError {
		super(extractTestClasses(suiteClass));
	}

	private static Set<Class<?>> extractTestClasses(Class<?> suiteClass) {
		Set<Class<?>> allTests = new HashSet<>();
		collectTestsFromSuite(suiteClass, allTests);
		return allTests;
	}

	private static void collectTestsFromSuite(Class<?> suite, Set<Class<?>> allTests) {
		SuiteClasses annotation = suite.getAnnotation(SuiteClasses.class);
		if (annotation == null) throw new IllegalArgumentException("EnvironmentJUnitSuite demands a @SuiteClasses-annotated class");
		for (Class<?> clazz : annotation.value()) {
			if (clazz.isAnnotationPresent(SuiteClasses.class)) collectTestsFromSuite(clazz, allTests);
			else allTests.add(clazz);
		}
	}

}
