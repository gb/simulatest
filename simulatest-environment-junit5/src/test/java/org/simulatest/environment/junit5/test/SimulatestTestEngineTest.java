package org.simulatest.environment.junit5.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.EngineFilter;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;
import org.simulatest.environment.junit5.SimulatestTestEngine;
import org.simulatest.environment.junit5.test.testdouble.EnvironmentTracker;
import org.simulatest.environment.junit5.test.testdouble.AnotherFirstLevelTest;
import org.simulatest.environment.junit5.test.testdouble.FirstLevelTest;
import org.simulatest.environment.junit5.test.testdouble.SecondLevelTest;
import org.simulatest.insistencelayer.datasource.InsistenceLayerDataSource;

/**
 * Integration test for the Simulatest JUnit 5 TestEngine.
 * Uses the JUnit Platform Launcher API to programmatically run the engine.
 */
class SimulatestTestEngineTest {

	@BeforeAll
	static void configureDataSource() {
		JdbcDataSource h2 = new JdbcDataSource();
		h2.setURL("jdbc:h2:mem:junit5test;DB_CLOSE_DELAY=-1");
		h2.setUser("sa");
		InsistenceLayerDataSource.configure(h2);
	}

	@AfterAll
	static void cleanUp() {
		EnvironmentTracker.clear();
	}

	@BeforeEach
	void clearTracker() {
		EnvironmentTracker.clear();
	}

	@Test
	void engineShouldDiscoverAndRunTestsInEnvironmentOrder() {
		LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
				.selectors(
						DiscoverySelectors.selectClass(FirstLevelTest.class),
						DiscoverySelectors.selectClass(AnotherFirstLevelTest.class),
						DiscoverySelectors.selectClass(SecondLevelTest.class))
				.filters(EngineFilter.includeEngines(SimulatestTestEngine.ENGINE_ID))
				.build();

		SummaryGeneratingListener summaryListener = new SummaryGeneratingListener();
		Launcher launcher = LauncherFactory.create();
		launcher.execute(request, summaryListener);

		TestExecutionSummary summary = summaryListener.getSummary();

		if (!summary.getFailures().isEmpty()) {
			summary.getFailures().forEach(f ->
				System.err.println(f.getTestIdentifier().getDisplayName() + ": " + f.getException()));
		}

		assertEquals(0, summary.getTestsFailedCount(),
				"All tests should pass");
		assertEquals(4, summary.getTestsSucceededCount(),
				"Should run 4 test methods total (2 FirstLevelTest + 1 AnotherFirstLevelTest + 1 SecondLevelTest)");
	}

	@Test
	void engineShouldReturnEmptyDescriptorForNonSimulatestClasses() {
		LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
				.selectors(DiscoverySelectors.selectClass(SimulatestTestEngineTest.class))
				.filters(EngineFilter.includeEngines(SimulatestTestEngine.ENGINE_ID))
				.build();

		SummaryGeneratingListener summaryListener = new SummaryGeneratingListener();
		Launcher launcher = LauncherFactory.create();
		launcher.execute(request, summaryListener);

		TestExecutionSummary summary = summaryListener.getSummary();
		assertEquals(0, summary.getTestsStartedCount(),
				"No tests should be found for non-@UseEnvironment class");
	}

	@Test
	void postDiscoveryFilterShouldExcludeUseEnvironmentFromJupiter() {
		// Register filter explicitly in the request
		LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
				.selectors(
						DiscoverySelectors.selectClass(FirstLevelTest.class),
						DiscoverySelectors.selectClass(SecondLevelTest.class))
				.filters(new org.simulatest.environment.junit5.SimulatestPostDiscoveryFilter())
				.build();

		Launcher launcher = LauncherFactory.create();
		TestPlan testPlan = launcher.discover(request);

		for (TestIdentifier root : testPlan.getRoots()) {
			if (root.getDisplayName().contains("Jupiter")) {
				assertTrue(testPlan.getChildren(root).isEmpty(),
						"PostDiscoveryFilter should prune @UseEnvironment classes from Jupiter. " +
						"Found: " + testPlan.getChildren(root));
			}
		}
	}

	@Test
	void classesWithSameEnvironmentShouldBeGroupedUnderOneNode() {
		LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
				.selectors(
						DiscoverySelectors.selectClass(FirstLevelTest.class),
						DiscoverySelectors.selectClass(AnotherFirstLevelTest.class),
						DiscoverySelectors.selectClass(SecondLevelTest.class))
				.filters(EngineFilter.includeEngines(SimulatestTestEngine.ENGINE_ID))
				.build();

		Launcher launcher = LauncherFactory.create();
		TestPlan testPlan = launcher.discover(request);

		// Find the Simulatest engine root
		TestIdentifier engineRoot = testPlan.getRoots().stream()
				.filter(r -> r.getDisplayName().equals("Simulatest"))
				.findFirst().orElseThrow();

		// Print tree for diagnosis
		printTree(testPlan, engineRoot, "");

		// Count how many children have "FirstLevelEnvironment" in their display name
		long firstLevelEnvCount = countDescendantsWithName(testPlan, engineRoot, "FirstLevelEnvironment");
		assertEquals(1, firstLevelEnvCount,
				"FirstLevelEnvironment should appear exactly once in the tree, " +
				"grouping both FirstLevelTest and AnotherFirstLevelTest");

		// Verify both test classes are under FirstLevelEnvironment
		SummaryGeneratingListener summaryListener = new SummaryGeneratingListener();
		LauncherFactory.create().execute(request, summaryListener);

		TestExecutionSummary summary = summaryListener.getSummary();
		assertEquals(0, summary.getTestsFailedCount(), "All tests should pass");
		assertEquals(4, summary.getTestsSucceededCount(),
				"Should run 4 methods (2 FirstLevelTest + 1 AnotherFirstLevelTest + 1 SecondLevelTest)");
	}

	private void printTree(TestPlan testPlan, TestIdentifier node, String indent) {
		System.out.println(indent + node.getDisplayName() + " [" + node.getUniqueId() + "]");
		for (TestIdentifier child : testPlan.getChildren(node)) {
			printTree(testPlan, child, indent + "  ");
		}
	}

	private long countDescendantsWithName(TestPlan testPlan, TestIdentifier node, String name) {
		long count = 0;
		for (TestIdentifier child : testPlan.getChildren(node)) {
			if (child.getDisplayName().equals(name)) count++;
			count += countDescendantsWithName(testPlan, child, name);
		}
		return count;
	}

	@Test
	void engineShouldExecuteEnvironmentsInTreeOrder() {
		LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
				.selectors(DiscoverySelectors.selectClass(SecondLevelTest.class))
				.filters(EngineFilter.includeEngines(SimulatestTestEngine.ENGINE_ID))
				.build();

		SummaryGeneratingListener summaryListener = new SummaryGeneratingListener();
		Launcher launcher = LauncherFactory.create();
		launcher.execute(request, summaryListener);

		TestExecutionSummary summary = summaryListener.getSummary();
		assertEquals(0, summary.getTestsFailedCount());
		assertEquals(1, summary.getTestsSucceededCount());

		// Verify environment execution order: parent before child
		assertTrue(EnvironmentTracker.getEvents().indexOf("FirstLevel")
				< EnvironmentTracker.getEvents().indexOf("SecondLevel"),
				"FirstLevel should run before SecondLevel");
	}

}
