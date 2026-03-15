package org.simulatest.environment.environment;

import static org.simulatest.environment.environment.EnvironmentDefinition.create;
import static org.simulatest.environment.infra.AnnotationUtils.extractEnvironment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EnvironmentExtractor {

	private final Map<EnvironmentDefinition, List<Class<?>>> testsByEnvironment;

	public EnvironmentExtractor(Collection<Class<?>> tests) {
		testsByEnvironment = new HashMap<>();
		for (Class<?> testCase : tests) addTestIntoEnvironmentNode(testCase);
	}

	private void addTestIntoEnvironmentNode(Class<?> test) {
		EnvironmentDefinition environment = create(extractEnvironment(test));
		testsByEnvironment.computeIfAbsent(environment, key -> new ArrayList<>()).add(test);
	}

	public int size() {
		return testsByEnvironment.size();
	}

	public List<Class<?>> getTests(EnvironmentDefinition environment) {
		return Collections.unmodifiableList(testsByEnvironment.getOrDefault(environment, List.of()));
	}

	public Set<EnvironmentDefinition> getEnvironments() {
		return Collections.unmodifiableSet(testsByEnvironment.keySet());
	}

	public boolean hasEnvironment(EnvironmentDefinition environment) {
		return testsByEnvironment.containsKey(environment);
	}
	
}