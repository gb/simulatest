package org.simulatest.environment;

import static org.simulatest.environment.EnvironmentDefinition.create;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.simulatest.environment.annotation.UseEnvironment;

public final class EnvironmentExtractor {

	private final Map<EnvironmentDefinition, List<Class<?>>> testsByEnvironment;

	public static EnvironmentExtractor extract(Collection<Class<?>> tests) {
		return new EnvironmentExtractor(tests);
	}

	private EnvironmentExtractor(Collection<Class<?>> tests) {
		Objects.requireNonNull(tests, "tests must not be null");
		testsByEnvironment = new HashMap<>();
		for (Class<?> testCase : tests) addTestIntoEnvironmentNode(testCase);
	}

	private void addTestIntoEnvironmentNode(Class<?> test) {
		UseEnvironment annotation = test.getAnnotation(UseEnvironment.class);
		Class<? extends Environment> envClass = annotation == null ? BigBangEnvironment.class : annotation.value();
		testsByEnvironment.computeIfAbsent(create(envClass), key -> new ArrayList<>()).add(test);
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