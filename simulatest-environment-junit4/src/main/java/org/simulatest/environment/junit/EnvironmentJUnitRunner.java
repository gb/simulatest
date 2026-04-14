package org.simulatest.environment.junit;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
import org.simulatest.environment.tree.EnvironmentTreeBuilder;
import org.simulatest.environment.plugin.SimulatestPlugin;
import org.simulatest.environment.SimulatestSession;
import org.simulatest.environment.listener.EnvironmentRunnerListener;
import org.simulatest.environment.infra.exception.EnvironmentInstantiationException;
import org.simulatest.environment.tree.Tree;
import org.simulatest.insistencelayer.InsistenceLayer;

/**
 * JUnit 4 {@link Runner} that walks a test's environment tree and delegates
 * per-test execution to {@link SimulatestJUnit4ClassRunner}.
 */
public class EnvironmentJUnitRunner extends Runner implements Filterable {

	private final Map<Class<?>, Runner> runnersByTest = new LinkedHashMap<>();
	private final Set<Class<?>> testClasses = new LinkedHashSet<>();
	private final List<SimulatestPlugin> plugins;
	private EnvironmentInfrastructure infrastructure;
	private EnvironmentRunner environmentRunner;

	public EnvironmentJUnitRunner(Set<Class<?>> testClasses) throws InitializationError {
		this.testClasses.addAll(Objects.requireNonNull(testClasses, "testClasses must not be null"));
		this.plugins = SimulatestSession.loadPlugins();
		createTestRunners();
		this.infrastructure = buildInfrastructure();
	}

	public EnvironmentJUnitRunner(Class<?> testClass) throws InitializationError {
		this(Set.of(Objects.requireNonNull(testClass, "testClass must not be null")));
	}

	private EnvironmentInfrastructure buildInfrastructure() {
		EnvironmentExtractor extractor = EnvironmentExtractor.extract(Collections.unmodifiableCollection(testClasses));
		Tree<EnvironmentDefinition> tree = new EnvironmentTreeBuilder(extractor.getEnvironments()).getTree();
		EnvironmentDescriptionTreeBuilder descriptions = new EnvironmentDescriptionTreeBuilder(tree);
		for (EnvironmentDefinition environment : extractor.getEnvironments()) {
			for (Class<?> testCase : extractor.getTests(environment)) {
				descriptions.addTestDescription(environment, requireRunner(testCase).getDescription());
			}
		}
		return new EnvironmentInfrastructure(extractor, tree, descriptions);
	}

	private void createTestRunners() throws InitializationError {
		for (Class<?> testCase : testClasses)
			runnersByTest.put(testCase, new SimulatestJUnit4ClassRunner(this, testCase, plugins));
	}

	private Runner requireRunner(Class<?> testCase) {
		Runner runner = runnersByTest.get(testCase);
		if (runner == null) throw new IllegalStateException("No runner found for test class: " + testCase.getName());
		return runner;
	}

	@Override
	public Description getDescription() {
		return infrastructure.descriptions().getDescription();
	}

	@Override
	public void run(final RunNotifier notifier) {
		initializeTestClasses();

		try (SimulatestSession session = SimulatestSession.open(plugins, Collections.unmodifiableCollection(testClasses))) {
			environmentRunner = new EnvironmentRunner(session.factory(), infrastructure.tree(),
					session.insistenceLayer().orElse(null));

			environmentRunner.addListener(new EnvironmentRunnerListener() {
				@Override
				public void afterRun(EnvironmentDefinition environment) {
					runTestOfEnvironment(notifier, environment);
				}
			});

			session.run(environmentRunner::run);
		}
	}

	private void runTestOfEnvironment(RunNotifier notifier, EnvironmentDefinition environment) {
		if (!infrastructure.extractor().hasEnvironment(environment)) return;

		for (Class<?> testCase : infrastructure.extractor().getTests(environment))
			requireRunner(testCase).run(notifier);
	}

	void resetInsistenceLevel() {
		if (environmentRunner == null) return;
		environmentRunner.insistenceLayer().ifPresent(InsistenceLayer::resetCurrentLevel);
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

		infrastructure = buildInfrastructure();
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

	private record EnvironmentInfrastructure(
			EnvironmentExtractor extractor,
			Tree<EnvironmentDefinition> tree,
			EnvironmentDescriptionTreeBuilder descriptions) { }

}
