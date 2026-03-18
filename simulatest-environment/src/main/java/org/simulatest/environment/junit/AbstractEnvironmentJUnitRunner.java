package org.simulatest.environment.junit;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.Filterable;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.simulatest.environment.EnvironmentDefinition;
import org.simulatest.environment.EnvironmentExtractor;
import org.simulatest.environment.EnvironmentRunner;
import org.simulatest.environment.EnvironmentTreeBuilder;
import org.simulatest.environment.plugin.SimulatestPlugin;
import org.simulatest.environment.SimulatestSession;
import org.simulatest.environment.listener.EnvironmentRunnerListener;
import org.simulatest.environment.infra.exception.EnvironmentInstantiationException;
import org.simulatest.environment.tree.Tree;

public abstract class AbstractEnvironmentJUnitRunner extends Runner implements Filterable {

	private final Map<Class<?>, Runner> runnersByTest = new HashMap<>();
	private final Set<Class<?>> testClasses = new HashSet<>();
	private final List<SimulatestPlugin> plugins;
	private Tree<EnvironmentDefinition> environmentTree;
	private EnvironmentExtractor environmentExtractor;
	private EnvironmentDescriptionTreeBuilder descriptionTreeBuilder;
	private EnvironmentRunner environmentRunner;

	public AbstractEnvironmentJUnitRunner(Set<Class<?>> testClasses) throws InitializationError {
		this.testClasses.addAll(testClasses);
		this.plugins = SimulatestSession.loadPlugins();
		setup();
	}

	public AbstractEnvironmentJUnitRunner(Class<?> testClass) throws InitializationError {
		this(Set.of(testClass));
	}

	private void setup() throws InitializationError {
		initializeEnvironmentExtractor();
		createEnvironmentTree();
		initializeDescriptionTreeBuilder();
		createTestRunners();
		populateDescriptionTreeBuilder();
	}

	private void initializeEnvironmentExtractor() {
		environmentExtractor = EnvironmentExtractor.extract(Collections.unmodifiableCollection(testClasses));
	}

	private void createEnvironmentTree() {
		EnvironmentTreeBuilder treeBuilder = new EnvironmentTreeBuilder(environmentExtractor.getEnvironments());
		environmentTree = treeBuilder.getTree();
	}

	private void initializeDescriptionTreeBuilder() {
		descriptionTreeBuilder = new EnvironmentDescriptionTreeBuilder(environmentTree);
	}

	private void createTestRunners() throws InitializationError {
		for (Class<?> testCase : testClasses)
			runnersByTest.put(testCase, createTestRunner(testCase));
	}

	protected Runner createTestRunner(Class<?> test) throws InitializationError {
		return new SimulatestJUnit4ClassRunner(this, test, plugins);
	}

	private Runner requireRunner(Class<?> testCase) {
		Runner runner = runnersByTest.get(testCase);
		if (runner == null) throw new IllegalStateException("No runner found for test class: " + testCase.getName());
		return runner;
	}

	private void populateDescriptionTreeBuilder() {
		for (EnvironmentDefinition environment : environmentExtractor.getEnvironments()) {
			for (Class<?> testCase : environmentExtractor.getTests(environment)) {
				descriptionTreeBuilder.addTestDescription(environment, requireRunner(testCase).getDescription());
			}
		}
	}

	@Override
	public Description getDescription() {
		return descriptionTreeBuilder.getDescription();
	}

	@Override
	public void run(final RunNotifier notifier) {
		initializeTestClasses();

		try (SimulatestSession session = SimulatestSession.open(plugins, Collections.unmodifiableCollection(testClasses))) {
			environmentRunner = new EnvironmentRunner(session.factory(), environmentTree, session.insistenceLayer());

			environmentRunner.addListener(new EnvironmentRunnerListener() {
				@Override
				public void afterRun(EnvironmentDefinition environment) {
					runTestOfEnvironment(notifier, environment);
				}
			});

			environmentRunner.run();
		}
	}

	private void runTestOfEnvironment(RunNotifier notifier, EnvironmentDefinition environment) {
		if (!environmentExtractor.hasEnvironment(environment)) return;

		for (Class<?> testCase : environmentExtractor.getTests(environment))
			requireRunner(testCase).run(notifier);
	}

	public void resetInsistenceLevel() {
		environmentRunner.insistenceLayer().resetCurrentLevel();
	}

	@Override
	public void filter(Filter filter) throws NoTestsRemainException {
		for (Class<?> testCase : List.copyOf(testClasses)) {
			try {
				((Filterable) requireRunner(testCase)).filter(filter);
			} catch (NoTestsRemainException e) {
				testClasses.remove(testCase);
				runnersByTest.remove(testCase);
			}
		}

		if (testClasses.isEmpty())
			throw new NoTestsRemainException();

		initializeEnvironmentExtractor();
		createEnvironmentTree();
		initializeDescriptionTreeBuilder();
		populateDescriptionTreeBuilder();
	}

	private void initializeTestClasses() {
		for (Class<?> testClass : testClasses) {
			try {
				Class.forName(testClass.getName(), true, testClass.getClassLoader());
			} catch (ClassNotFoundException e) {
				throw new EnvironmentInstantiationException(
						"Failed to initialize test class: " + testClass.getName(), e);
			}
		}
	}

}
