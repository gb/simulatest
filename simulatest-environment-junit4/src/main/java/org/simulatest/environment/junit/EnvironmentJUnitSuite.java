package org.simulatest.environment.junit;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import org.junit.runners.Suite.SuiteClasses;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;

public final class EnvironmentJUnitSuite extends EnvironmentJUnitRunner {

	public EnvironmentJUnitSuite(Class<?> suiteClass, RunnerBuilder builder) throws InitializationError {
		super(extractTestClasses(suiteClass));
	}

	private static Set<Class<?>> extractTestClasses(Class<?> suiteClass) {
		Objects.requireNonNull(suiteClass, "suiteClass must not be null");
		Set<Class<?>> allTests = new LinkedHashSet<>();
		Set<Class<?>> visited = new LinkedHashSet<>();
		collectTestsFromSuite(suiteClass, allTests, visited);
		return allTests;
	}

	private static void collectTestsFromSuite(Class<?> suite, Set<Class<?>> allTests, Set<Class<?>> visited) {
		if (!visited.add(suite)) return;
		SuiteClasses annotation = suite.getAnnotation(SuiteClasses.class);
		if (annotation == null) throw new IllegalArgumentException(
				"EnvironmentJUnitSuite demands a @SuiteClasses-annotated class; got " + suite.getName());
		for (Class<?> clazz : annotation.value()) {
			if (clazz.isAnnotationPresent(SuiteClasses.class)) collectTestsFromSuite(clazz, allTests, visited);
			else allTests.add(clazz);
		}
	}

}
