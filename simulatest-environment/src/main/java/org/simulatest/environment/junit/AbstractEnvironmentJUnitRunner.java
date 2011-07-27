package org.simulatest.environment.junit;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.simulatest.environment.environment.EnvironmentDefinition;
import org.simulatest.environment.environment.EnvironmentFactory;
import org.simulatest.environment.environment.EnvironmentRaker;
import org.simulatest.environment.environment.EnvironmentReflectionFactory;
import org.simulatest.environment.environment.EnvironmentRunner;
import org.simulatest.environment.environment.EnvironmentRunnerListener;
import org.simulatest.environment.environment.EnvironmentTreeBuilder;
import org.simulatest.environment.environment.listener.EnvironmentRunnerListenerInsistence;
import org.simulatest.environment.tree.Tree;
import org.simulatest.insistencelayer.InsistenceLayerManager;


public abstract class AbstractEnvironmentJUnitRunner extends Runner {
	
	private Map<Class<?>, Runner> runnersByTest;
	private Set<Class<?>> testClasses;
	private Tree<EnvironmentDefinition> environmentTree;
	private EnvironmentRaker environmentRaker;
	private EnvironmentDescriptionTreeBuilder descriptionTreeBuilder;
	
	public AbstractEnvironmentJUnitRunner(Set<Class<?>> testClasses) throws InitializationError {
		this.testClasses = testClasses;
		
		setup();
	}
	
	public AbstractEnvironmentJUnitRunner(Class<?> testClass) throws InitializationError {
		this.testClasses = new HashSet<Class<?>>(1);
		this.testClasses.add(testClass);
		
		setup();
	}

	private void setup() throws InitializationError {
		initializeEnvironmentRaker();
		createEnvironmentTree();
		initializeDescriptionTreeBuilder();
		createTestRunners();
		populateDescriptionTreeBuilder();
	}

	private void initializeEnvironmentRaker() {
		environmentRaker = new EnvironmentRaker(testClasses);
	}

	private void createEnvironmentTree() {
		EnvironmentTreeBuilder environmentTreeBuilder = new EnvironmentTreeBuilder();
		environmentTreeBuilder.addAll(environmentRaker.getEnvironments());
		
		environmentTree = environmentTreeBuilder.getTree();
	}
	
	private void initializeDescriptionTreeBuilder() {
		descriptionTreeBuilder = new EnvironmentDescriptionTreeBuilder(environmentTree);
	}
		
	private void createTestRunners() throws InitializationError {
		runnersByTest = new HashMap<Class<?>, Runner>(testClasses.size());
		for (Class<?> testCase : testClasses) runnersByTest.put(testCase, instanceTest(testCase));
	}
	
	protected Runner instanceTest(Class<?> test) throws InitializationError {
		return new BlockJUnit4ClassRunner(test);
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
		return runnersByTest.get(testCase).getDescription();
	}

	@Override
	public Description getDescription() {
		return descriptionTreeBuilder.getDescription();
	}

	@Override
	public void run(final RunNotifier notifier) {
		EnvironmentRunner environmentRunner = new EnvironmentRunner(getEnvironmentFactory(), environmentTree);

		environmentRunner.addListener(new EnvironmentRunnerListenerInsistence(getInsistenceLayerManager()));

		environmentRunner.addListener(new EnvironmentRunnerListener() {
			public void beforeRun(EnvironmentDefinition definition) {}
			public void afterChildrenRun(EnvironmentDefinition definition) {}

			@Override
			public void afterRun(EnvironmentDefinition environment) {
				runTestOfEnvironment(notifier, environment);
			}
		});

		environmentRunner.run();
	}
	
	protected EnvironmentFactory getEnvironmentFactory() {
		return new EnvironmentReflectionFactory();
	}

	private void runTestOfEnvironment(RunNotifier notifier, EnvironmentDefinition environment) {
		if (!environmentRaker.hasEnvironment(environment)) return;
		for (Class<?> testCase : environmentRaker.getTests(environment)) runTestCase(testCase, notifier);
	}
	
	private void runTestCase(Class<?> testCase, RunNotifier notifier) {
		runnersByTest.get(testCase).run(notifier);
		getInsistenceLayerManager().resetCurrentLevel();
	}

	private InsistenceLayerManager getInsistenceLayerManager() {
		try {
			//TODO obter o insistenceLayerManager da conexão e não estaticamente
			return  InsistenceLayerManager.getInstance();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

}