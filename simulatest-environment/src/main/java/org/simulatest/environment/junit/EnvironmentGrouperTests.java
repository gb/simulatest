package org.simulatest.environment.junit;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.runner.Runner;

public class EnvironmentGrouperTests {
	
	private Map<Class<?>, Runner> runnersByTest = new HashMap<Class<?>, Runner>();
	private Set<Class<?>> testClasses = new HashSet<Class<?>>();
	
	public EnvironmentGrouperTests(Class<?> testClass) {
		testClasses.add(testClass);
	}
	
	public EnvironmentGrouperTests(Set<Class<?>> testClasses) {
		for (Class<?> testClass : testClasses) testClasses.add(testClass);
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

}