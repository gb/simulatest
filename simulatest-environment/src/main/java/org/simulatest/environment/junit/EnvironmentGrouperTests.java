package org.simulatest.environment.junit;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.runner.Runner;

public class EnvironmentGrouperTests {
	
	private final Map<Class<?>, Runner> runnersByTest = new HashMap<>();
	private final Set<Class<?>> testClasses = new HashSet<>();

	public EnvironmentGrouperTests(Set<Class<?>> testClasses) {
		this.testClasses.addAll(testClasses);
	}

	public Collection<Class<?>> getTestClasses() {
		return Collections.unmodifiableCollection(testClasses);
	}
	
	public void put(Class<?> testCase, Runner instancedTest) {
		runnersByTest.put(testCase, instancedTest);
	}
	
	public Runner get(Class<?> testCase) {
		return runnersByTest.get(testCase);
	}

	public void remove(Class<?> testCase) {
		runnersByTest.remove(testCase);
		testClasses.remove(testCase);
	}

}
