package org.simulatest.environment.environment;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.runners.Suite.SuiteClasses;


public class SuiteTestExtractor {

	private final Set<Class<?>> allTests = new HashSet<>();

	public SuiteTestExtractor(Class<?> suiteClass) {
		digesterSuite(suiteClass);
	}

	public Set<Class<?>> getAllTests() {
		return Collections.unmodifiableSet(allTests);
	}

	private void digesterSuite(Class<?> suite) {
		SuiteClasses annotation = suite.getAnnotation(SuiteClasses.class);
		if (annotation == null) throw new IllegalArgumentException("SuiteTestExtractor demands a @SuiteClasses-annotated class");
		for (Class<?> clazz : annotation.value()) addTest(clazz);
	}

	private void addTest(Class<?> clazz) {
		SuiteClasses annotation = clazz.getAnnotation(SuiteClasses.class);
		if (annotation != null) digesterSuite(clazz);
		else allTests.add(clazz);
	}

}