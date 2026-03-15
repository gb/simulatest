package org.simulatest.environment.junit;

import java.util.List;
import java.util.Set;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.Filterable;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.simulatest.environment.environment.EnvironmentDatabaseRunner;
import org.simulatest.environment.environment.EnvironmentDefinition;
import org.simulatest.environment.environment.EnvironmentExtractor;
import org.simulatest.environment.environment.EnvironmentTreeBuilder;
import org.simulatest.environment.environment.SimulatestPlugin;
import org.simulatest.environment.environment.SimulatestPlugins;
import org.simulatest.environment.environment.listener.EnvironmentRunnerListener;
import org.simulatest.environment.infra.exception.EnvironmentInstantiationException;
import org.simulatest.environment.tree.Tree;

public abstract class AbstractEnvironmentJUnitRunner extends Runner implements Filterable {

	private final EnvironmentGrouperTests environmentGrouperTests;
	private final List<SimulatestPlugin> plugins;
	private Tree<EnvironmentDefinition> environmentTree;
	private EnvironmentExtractor environmentExtractor;
	private EnvironmentDescriptionTreeBuilder descriptionTreeBuilder;
	private EnvironmentDatabaseRunner environmentRunner;

	public AbstractEnvironmentJUnitRunner(Set<Class<?>> testClasses) throws InitializationError {
		this.environmentGrouperTests = new EnvironmentGrouperTests(testClasses);
		this.plugins = SimulatestPlugins.loadAll();
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
		environmentExtractor = new EnvironmentExtractor(environmentGrouperTests.getTestClasses());
	}

	private void createEnvironmentTree() {
		EnvironmentTreeBuilder treeBuilder = new EnvironmentTreeBuilder(environmentExtractor.getEnvironments());
		environmentTree = treeBuilder.getTree();
	}

	private void initializeDescriptionTreeBuilder() {
		descriptionTreeBuilder = new EnvironmentDescriptionTreeBuilder(environmentTree);
	}

	private void createTestRunners() throws InitializationError {
		for (Class<?> testCase : environmentGrouperTests.getTestClasses())
			environmentGrouperTests.put(testCase, instanceTest(testCase));
	}

	protected Runner instanceTest(Class<?> test) throws InitializationError {
		return new SimulatestJUnit4ClassRunner(this, test, plugins);
	}

	private void populateDescriptionTreeBuilder() {
		for (EnvironmentDefinition environment : environmentExtractor.getEnvironments())
			for (Class<?> testCase : environmentExtractor.getTests(environment))
				addTestDescription(environment, testCase);
	}

	private void addTestDescription(EnvironmentDefinition environment, Class<?> testCase) {
		descriptionTreeBuilder.addTestDescription(environment, getDescription(testCase));
	}

	private Description getDescription(Class<?> testCase) {
		return environmentGrouperTests.get(testCase).getDescription();
	}

	@Override
	public Description getDescription() {
		return descriptionTreeBuilder.getDescription();
	}

	@Override
	public void run(final RunNotifier notifier) {
		initializeTestClasses();
		SimulatestPlugins.initializeAll(plugins, environmentGrouperTests.getTestClasses());

		try {
			environmentRunner = new EnvironmentDatabaseRunner(SimulatestPlugins.resolveFactory(plugins), environmentTree);

			environmentRunner.addListener(new EnvironmentRunnerListener() {
				@Override
				public void afterRun(EnvironmentDefinition environment) {
					runTestOfEnvironment(notifier, environment);
				}
			});

			environmentRunner.run();
		} finally {
			SimulatestPlugins.destroyAll(plugins);
		}
	}

	private void runTestOfEnvironment(RunNotifier notifier, EnvironmentDefinition environment) {
		if (!environmentExtractor.hasEnvironment(environment)) return;
		for (Class<?> testCase : environmentExtractor.getTests(environment)) runTestCase(testCase, notifier);
	}

	private void runTestCase(Class<?> testCase, RunNotifier notifier) {
		environmentGrouperTests.get(testCase).run(notifier);
	}

	public EnvironmentDatabaseRunner getEnvironmentRunner() {
		return environmentRunner;
	}

	public void resetInsistenceLevel() {
		environmentRunner.insistenceLayer().resetCurrentLevel();
	}

	@Override
	public void filter(Filter filter) throws NoTestsRemainException {
		for (Class<?> testCase : environmentGrouperTests.getTestClasses()) {
			Runner runner = environmentGrouperTests.get(testCase);
			if (runner instanceof Filterable filterable) {
				filterable.filter(filter);
			}
		}
	}

	private void initializeTestClasses() {
		for (Class<?> testClass : environmentGrouperTests.getTestClasses()) {
			try {
				Class.forName(testClass.getName(), true, testClass.getClassLoader());
			} catch (ClassNotFoundException e) {
				throw new EnvironmentInstantiationException(
						"Failed to initialize test class: " + testClass.getName(), e);
			}
		}
	}

}
