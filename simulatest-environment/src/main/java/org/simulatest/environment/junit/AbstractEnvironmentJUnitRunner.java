package org.simulatest.environment.junit;

import java.util.Set;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.simulatest.environment.environment.EnvironmentDatabaseRunner;
import org.simulatest.environment.environment.EnvironmentDefinition;
import org.simulatest.environment.environment.EnvironmentFactory;
import org.simulatest.environment.environment.EnvironmentRaker;
import org.simulatest.environment.environment.EnvironmentTreeBuilder;
import org.simulatest.environment.environment.listener.EnvironmentRunnerNullable;
import org.simulatest.environment.tree.Tree;

public abstract class AbstractEnvironmentJUnitRunner extends Runner {
	
	private EnvironmentGrouperTests environmentGrouperTests;
	private Tree<EnvironmentDefinition> environmentTree;
	private EnvironmentRaker environmentRaker;
	private EnvironmentDescriptionTreeBuilder descriptionTreeBuilder;
	private EnvironmentDatabaseRunner environmentRunner;
	
	public AbstractEnvironmentJUnitRunner(Set<Class<?>> testClasses) throws InitializationError {
		this.environmentGrouperTests = new EnvironmentGrouperTests(testClasses);
		setup();
	}
	
	public AbstractEnvironmentJUnitRunner(Class<?> testClass) throws InitializationError {
		this.environmentGrouperTests = new EnvironmentGrouperTests(testClass);
		setup();
	}
	
	protected abstract EnvironmentFactory getEnvironmentFactory();
	
	protected abstract Runner instanceTest(Class<?> test) throws InitializationError;

	private void setup() throws InitializationError {
		initializeEnvironmentRaker();
		createEnvironmentTree();
		initializeDescriptionTreeBuilder();
		createTestRunners();
		populateDescriptionTreeBuilder();
	}

	private void initializeEnvironmentRaker() {
		environmentRaker = new EnvironmentRaker(environmentGrouperTests.getTestClasses());
	}

	private void createEnvironmentTree() {
		EnvironmentTreeBuilder treeBuilder = new EnvironmentTreeBuilder(environmentRaker.getEnvironments());
		environmentTree = treeBuilder.getTree();
	}
	
	private void initializeDescriptionTreeBuilder() {
		descriptionTreeBuilder = new EnvironmentDescriptionTreeBuilder(environmentTree);
	}
		
	private void createTestRunners() throws InitializationError {
		for (Class<?> testCase : environmentGrouperTests.getTestClasses()) 
			environmentGrouperTests.put(testCase, instanceTest(testCase));
	}
	
	private void populateDescriptionTreeBuilder() {
		for (EnvironmentDefinition environment : environmentRaker.getEnvironments()) 
			for (Class<?> testCase : environmentRaker.getTests(environment)) 
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
		environmentRunner = new EnvironmentDatabaseRunner(getEnvironmentFactory(), environmentTree);

		environmentRunner.addListener(new EnvironmentRunnerNullable() {
			@Override
			public void afterRun(EnvironmentDefinition environment) {
				runTestOfEnvironment(notifier, environment);
			}
		});

		environmentRunner.run();
	}
	
	private void runTestOfEnvironment(RunNotifier notifier, EnvironmentDefinition environment) {
		if (!environmentRaker.hasEnvironment(environment)) return;
		for (Class<?> testCase : environmentRaker.getTests(environment)) runTestCase(testCase, notifier);
	}
	
	private void runTestCase(Class<?> testCase, RunNotifier notifier) {
		environmentGrouperTests.get(testCase).run(notifier);
		environmentRunner.insistenceLayer().resetCurrentLevel();
	}

	protected final EnvironmentDatabaseRunner getEnvironmentRunner() {
		return environmentRunner;
	}
	
}