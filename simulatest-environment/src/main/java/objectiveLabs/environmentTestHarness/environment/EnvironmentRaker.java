package objectiveLabs.environmentTestHarness.environment;

import static objectiveLabs.environmentTestHarness.environment.EnvironmentDefinition.create;
import static objectiveLabs.environmentTestHarness.infra.AnnotationUtils.extractEnvironment;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EnvironmentRaker {
	
	private Map<EnvironmentDefinition, List<Class<?>>> testsByEnvionrment; 
	
	public EnvironmentRaker(Collection<Class<?>> tests) {
		testsByEnvionrment = new HashMap<EnvironmentDefinition, List<Class<?>>>();
		addTestsIntoEnvironmentsNodes(tests);
	}
	
	private void addTestsIntoEnvironmentsNodes(Collection<Class<?>> tests) {
		for (Class<?> testCase : tests) addTestIntoEnvironmentNode(testCase);
	}

	private void addTestIntoEnvironmentNode(Class<?> test) {
		EnvironmentDefinition environment = create(extractEnvironment(test));
		initializeEnvironmentIfNecessary(environment);
		testsByEnvionrment.get(environment).add(test);
	}

	private void initializeEnvironmentIfNecessary(EnvironmentDefinition environment) {
		if (testsByEnvionrment.get(environment) != null) return;
		testsByEnvionrment.put(environment, new LinkedList<Class<?>>());
	}
	
	public int size() {
		return testsByEnvionrment.size();
	}
	
	public List<Class<?>> getTests(EnvironmentDefinition environment) {
		return Collections.unmodifiableList(testsByEnvionrment.get(environment));
	}
	
	public Set<EnvironmentDefinition> getEnvironments() {
		return Collections.unmodifiableSet(testsByEnvionrment.keySet());
	}
	
	public boolean hasEnvironment(EnvironmentDefinition environment) {
		return testsByEnvionrment.containsKey(environment);
	}
	
}