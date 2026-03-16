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
import org.simulatest.environment.junit5.test.testdouble.AdvancedJupiterTest;
import org.simulatest.environment.junit5.test.testdouble.EnvironmentTracker;
import org.simulatest.environment.junit5.test.testdouble.FailingBeforeAllTest;
import org.simulatest.environment.junit5.test.testdouble.AnotherFirstLevelTest;
import org.simulatest.environment.junit5.test.testdouble.FirstLevelTest;
import org.simulatest.environment.junit5.test.testdouble.NestedEnvironmentsTest;
import org.simulatest.environment.junit5.test.testdouble.SecondLevelTest;
import org.simulatest.insistencelayer.InsistenceLayerFactory;

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
		InsistenceLayerFactory.configure(h2);
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
		TestExecutionSummary summary = runSimulatest(
				FirstLevelTest.class, AnotherFirstLevelTest.class, SecondLevelTest.class);

		assertNoFailures(summary);
		assertEquals(4, summary.getTestsSucceededCount(),
				"Should run 4 test methods total (2 FirstLevelTest + 1 AnotherFirstLevelTest + 1 SecondLevelTest)");
	}

	@Test
	void engineShouldReturnEmptyDescriptorForNonSimulatestClasses() {
		TestExecutionSummary summary = runSimulatest(SimulatestTestEngineTest.class);

		assertEquals(0, summary.getTestsStartedCount(),
				"No tests should be found for non-@UseEnvironment class");
	}

	@Test
	void postDiscoveryFilterShouldExcludeUseEnvironmentFromJupiter() {
		LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
				.selectors(
						DiscoverySelectors.selectClass(FirstLevelTest.class),
						DiscoverySelectors.selectClass(SecondLevelTest.class))
				.filters(new org.simulatest.environment.junit5.SimulatestPostDiscoveryFilter())
				.build();

		TestPlan testPlan = LauncherFactory.create().discover(request);

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
		LauncherDiscoveryRequest request = simulatestRequest(
				FirstLevelTest.class, AnotherFirstLevelTest.class, SecondLevelTest.class);

		TestPlan testPlan = LauncherFactory.create().discover(request);

		TestIdentifier engineRoot = testPlan.getRoots().stream()
				.filter(r -> r.getDisplayName().equals("Simulatest"))
				.findFirst().orElseThrow();

		printTree(testPlan, engineRoot, "");

		long firstLevelEnvCount = countDescendantsWithName(testPlan, engineRoot, "FirstLevelEnvironment");
		assertEquals(1, firstLevelEnvCount,
				"FirstLevelEnvironment should appear exactly once in the tree, " +
				"grouping both FirstLevelTest and AnotherFirstLevelTest");

		TestExecutionSummary summary = runSimulatest(
				FirstLevelTest.class, AnotherFirstLevelTest.class, SecondLevelTest.class);

		assertNoFailures(summary);
		assertEquals(4, summary.getTestsSucceededCount(),
				"Should run 4 methods (2 FirstLevelTest + 1 AnotherFirstLevelTest + 1 SecondLevelTest)");
	}

	@Test
	void shouldDelegateAllJupiterTestTypesToJupiter() {
		TestExecutionSummary summary = runSimulatest(AdvancedJupiterTest.class);

		assertNoFailures(summary);
		// 3 @ParameterizedTest + 3 @RepeatedTest + 2 @TestFactory + 1 @Nested = 9
		assertEquals(9, summary.getTestsSucceededCount(),
				"Should run all parameterized, repeated, dynamic, and nested tests");
	}

	@Test
	void engineShouldExecuteEnvironmentsInTreeOrder() {
		TestExecutionSummary summary = runSimulatest(SecondLevelTest.class);

		assertNoFailures(summary);
		assertEquals(1, summary.getTestsSucceededCount());

		assertTrue(EnvironmentTracker.getEvents().indexOf("FirstLevel")
				< EnvironmentTracker.getEvents().indexOf("SecondLevel"),
				"FirstLevel should run before SecondLevel");
	}

	@Test
	void selectingNestedClassDirectlyShouldResolveToEnclosingEnvironment() throws ClassNotFoundException {
		Class<?> innerClass = Class.forName(AdvancedJupiterTest.class.getName() + "$InnerTest");
		TestExecutionSummary summary = runSimulatest(innerClass);

		assertNoFailures(summary);
		assertTrue(summary.getTestsSucceededCount() >= 1,
				"Selecting a @Nested class directly should resolve to its @UseEnvironment enclosing class");
		assertTrue(EnvironmentTracker.getEvents().contains("FirstLevel"),
				"Environment should be set up when running a @Nested class directly");
	}

	@Test
	void nestedClassesWithOwnEnvironmentsShouldBeDiscoveredAndRunUnderCorrectEnvironment() {
		TestExecutionSummary summary = runSimulatest(NestedEnvironmentsTest.class);

		assertNoFailures(summary);
		assertEquals(3, summary.getTestsSucceededCount(),
				"Should discover and run all tests from @Nested classes with their own @UseEnvironment " +
				"(1 AtFirstLevel + 2 AtSecondLevel)");
		assertTrue(EnvironmentTracker.getEvents().contains("FirstLevel"),
				"FirstLevelEnvironment should have run for AtFirstLevel nested class");
		assertTrue(EnvironmentTracker.getEvents().contains("SecondLevel"),
				"SecondLevelEnvironment should have run for AtSecondLevel nested class");
	}

	@Test
	void nestedClassesShouldCoexistWithTopLevelClasses() {
		TestExecutionSummary summary = runSimulatest(
				FirstLevelTest.class, NestedEnvironmentsTest.class);

		assertNoFailures(summary);
		assertEquals(5, summary.getTestsSucceededCount(),
				"Should run 2 from FirstLevelTest + 3 from NestedEnvironmentsTest nested classes");
	}

	@Test
	void containerFailureShouldBeReportedAsFailed() {
		TestExecutionSummary summary = runSimulatest(FailingBeforeAllTest.class);

		assertTrue(summary.getTestsFailedCount() > 0 || summary.getContainersFailedCount() > 0,
				"A @BeforeAll failure should surface as a failure, not be silently swallowed");
	}

	@Test
	void classpathRootScanShouldDiscoverPackagePrivateClasses() {
		java.nio.file.Path testClasses = java.nio.file.Path.of("target/test-classes").toAbsolutePath();

		LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
				.selectors(DiscoverySelectors.selectClasspathRoots(java.util.Set.of(testClasses)))
				.filters(EngineFilter.includeEngines(SimulatestTestEngine.ENGINE_ID))
				.build();

		TestPlan testPlan = LauncherFactory.create().discover(request);

		TestIdentifier engineRoot = testPlan.getRoots().stream()
				.filter(r -> r.getDisplayName().equals("Simulatest"))
				.findFirst().orElseThrow();

		boolean foundPackagePrivate = testPlan.getDescendants(engineRoot).stream()
				.anyMatch(id -> id.getDisplayName().equals("PackagePrivateTest"));

		assertTrue(foundPackagePrivate,
				"ClasspathRootSelector scan should discover package-private @UseEnvironment test classes. " +
				"Found: " + testPlan.getDescendants(engineRoot).stream()
						.map(TestIdentifier::getDisplayName).toList());
	}

	// --- helpers ---

	private static LauncherDiscoveryRequest simulatestRequest(Class<?>... testClasses) {
		return LauncherDiscoveryRequestBuilder.request()
				.selectors(DiscoverySelectors.selectClasspathRoots(java.util.Set.of()))
				.selectors(java.util.Arrays.stream(testClasses)
						.map(DiscoverySelectors::selectClass)
						.toList())
				.filters(EngineFilter.includeEngines(SimulatestTestEngine.ENGINE_ID))
				.build();
	}

	private static TestExecutionSummary runSimulatest(Class<?>... testClasses) {
		SummaryGeneratingListener listener = new SummaryGeneratingListener();
		LauncherFactory.create().execute(simulatestRequest(testClasses), listener);
		return listener.getSummary();
	}

	private static void assertNoFailures(TestExecutionSummary summary) {
		if (!summary.getFailures().isEmpty()) {
			summary.getFailures().forEach(f ->
				System.err.println(f.getTestIdentifier().getDisplayName() + ": " + f.getException()));
		}
		assertEquals(0, summary.getTestsFailedCount(), "All tests should pass");
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

}
