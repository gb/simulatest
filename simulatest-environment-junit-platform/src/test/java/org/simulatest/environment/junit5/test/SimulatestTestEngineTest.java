package org.simulatest.environment.junit5.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.engine.DiscoverySelector;
import org.junit.platform.engine.FilterResult;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.engine.support.descriptor.MethodSource;
import org.junit.platform.launcher.EngineFilter;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.PostDiscoveryFilter;
import org.junit.platform.launcher.TagFilter;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;
import org.simulatest.environment.junit5.SimulatestTestEngine;
import org.simulatest.environment.junit5.descriptor.EnvironmentTestDescriptor;
import org.simulatest.environment.junit5.test.testdouble.AdvancedJupiterTest;
import org.simulatest.environment.junit5.test.testdouble.EnvRows;
import org.simulatest.environment.junit5.test.testdouble.InheritedNestedTest;
import org.simulatest.environment.junit5.test.testdouble.NestedOwnEnvironmentTest;
import org.simulatest.environment.junit5.test.testdouble.InsistenceIsolationTest;
import org.simulatest.environment.junit5.test.testdouble.EnvironmentTracker;
import org.simulatest.environment.junit5.test.testdouble.ClassSetupRowTest;
import org.simulatest.environment.junit5.test.testdouble.ConstructorRowTest;
import org.simulatest.environment.junit5.test.testdouble.FailingBeforeAllTest;
import org.simulatest.environment.junit5.test.testdouble.FailingClassSetupTest;
import org.simulatest.environment.junit5.test.testdouble.NestedClassSetupTest;
import org.simulatest.environment.junit5.test.testdouble.FailingEnvironmentTest;
import org.simulatest.environment.junit5.test.testdouble.AnotherFirstLevelTest;
import org.simulatest.environment.junit5.test.testdouble.FirstLevelTest;
import org.simulatest.environment.junit5.test.testdouble.NestedEnvironmentsTest;
import org.simulatest.environment.junit5.test.testdouble.RowBaseTest;
import org.simulatest.environment.junit5.test.testdouble.SecondLevelTest;
import org.simulatest.environment.junit5.test.testdouble.TaggedTest;
import org.simulatest.environment.junit5.test.testdouble.environment.FirstLevelEnvironment;
import org.simulatest.environment.junit5.test.testdouble.environment.InsistenceTestEnvironment;
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
		// DDL on the raw connection: H2 commits on CREATE TABLE, which would destroy open savepoints.
		EnvRows.createTable(h2);
		InsistenceTestEnvironment.createTable(h2);
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

		TestIdentifier jupiterRoot = testPlan.getRoots().stream()
				.filter(r -> r.getDisplayName().contains("Jupiter"))
				.findFirst()
				.orElseThrow(() -> new AssertionError(
						"Expected a Jupiter engine root; filter had no target. Roots: "
								+ testPlan.getRoots().stream().map(TestIdentifier::getDisplayName).toList()));

		assertTrue(testPlan.getChildren(jupiterRoot).isEmpty(),
				"PostDiscoveryFilter should prune @UseEnvironment classes from Jupiter. " +
				"Found: " + testPlan.getChildren(jupiterRoot));
	}

	@Test
	void classesWithSameEnvironmentShouldBeGroupedUnderOneNode() {
		LauncherDiscoveryRequest request = simulatestRequest(
				FirstLevelTest.class, AnotherFirstLevelTest.class, SecondLevelTest.class);

		TestPlan testPlan = LauncherFactory.create().discover(request);

		TestIdentifier engineRoot = testPlan.getRoots().stream()
				.filter(r -> r.getDisplayName().equals("Simulatest"))
				.findFirst().orElseThrow();

		long firstLevelEnvCount = countDescendantsWithName(testPlan, engineRoot, "FirstLevelEnvironment");
		assertEquals(1, firstLevelEnvCount,
				"FirstLevelEnvironment should appear exactly once in the tree, " +
				"grouping both FirstLevelTest and AnotherFirstLevelTest");
	}

	@Test
	void environmentUniqueIdShouldUseFullyQualifiedClassName() {
		LauncherDiscoveryRequest request = simulatestRequest(FirstLevelTest.class);
		TestPlan testPlan = LauncherFactory.create().discover(request);

		TestIdentifier engineRoot = testPlan.getRoots().stream()
				.filter(r -> r.getDisplayName().equals("Simulatest"))
				.findFirst()
				.orElseThrow();

		TestIdentifier envNode = testPlan.getDescendants(engineRoot).stream()
				.filter(id -> id.getDisplayName().equals("FirstLevelEnvironment"))
				.findFirst()
				.orElseThrow();

		assertTrue(envNode.getUniqueIdObject().toString().contains(FirstLevelEnvironment.class.getName()),
				"Environment UniqueId segment should use fully qualified class name to avoid collisions");
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
	void nestedClassWithOwnEnvironmentShouldRunOnceUnderItsOwnEnvironment() {
		TestExecutionSummary summary = runSimulatest(NestedOwnEnvironmentTest.class);

		assertNoFailures(summary);
		assertEquals(2, summary.getTestsSucceededCount(),
				"1 outer test + 1 nested test; the nested test must not also run under the outer environment");
	}

	@Test
	void selectingNestedClassWithOwnEnvironmentDirectlyShouldRunItOnce() throws ClassNotFoundException {
		Class<?> nestedClass = Class.forName(NestedOwnEnvironmentTest.class.getName() + "$WithChildEnvironment");
		TestExecutionSummary summary = runSimulatest(nestedClass);

		assertNoFailures(summary);
		assertEquals(1, summary.getTestsSucceededCount(), "Only the nested test should run");
	}

	@Test
	void inheritedNestedClassWithoutOwnEnvironmentShouldRunUnderTheSubclassEnvironment() {
		TestExecutionSummary summary = runSimulatest(InheritedNestedTest.class);

		assertNoFailures(summary);
		assertEquals(1, summary.getTestsSucceededCount(),
				"The nested class inherited from the base class should run once, under the subclass environment");
	}

	@Test
	void containerFailureShouldBeReportedAsFailed() {
		TestExecutionSummary summary = runSimulatest(FailingBeforeAllTest.class);

		assertTrue(summary.getTestsFailedCount() > 0 || summary.getContainersFailedCount() > 0,
				"A @BeforeAll failure should surface as a failure, not be silently swallowed");
	}

	@Test
	void failingEnvironmentShouldNotDisturbItsSiblingsOrLeaveDataBehind() {
		TestExecutionSummary summary = runSimulatest(FailingEnvironmentTest.class, NestedOwnEnvironmentTest.class);

		assertEquals(List.of("FailingRowEnvironment"), namesOfFailed(summary),
				"Only the failing environment should be reported as failed");
		assertEquals(2, summary.getTestsSucceededCount(),
				"The tests of the parent and sibling environments should still pass, seeing the parent row");
		assertEveryLevelIsClosedAndNoRowIsLeft();
	}

	@Test
	void classSetupDataShouldBeSeenByEveryTestOfTheClass() {
		TestExecutionSummary summary = runSimulatest(ClassSetupRowTest.class);

		assertNoFailures(summary);
		assertEquals(2, summary.getTestsSucceededCount(), "Both tests should see the row the class added");
		assertEveryLevelIsClosedAndNoRowIsLeft();
	}

	@Test
	void dataWrittenWhenATestInstanceIsBuiltShouldBeUndoneWithItsTest() {
		TestExecutionSummary summary = runSimulatest(ConstructorRowTest.class);

		assertNoFailures(summary);
		assertEquals(2, summary.getTestsSucceededCount(),
				"The instance of the second test adds the same row, so the row of the first must be gone");
		assertEveryLevelIsClosedAndNoRowIsLeft();
	}

	@Test
	void classSetupDataShouldNotReachTheNextClass() {
		TestExecutionSummary summary = runSimulatest(ClassSetupRowTest.class, NestedOwnEnvironmentTest.class);

		assertNoFailures(summary);
		assertEquals(4, summary.getTestsSucceededCount(),
				"2 from ClassSetupRowTest + 2 from NestedOwnEnvironmentTest, "
				+ "which runs next and should see only the rows of its environments");
		assertEveryLevelIsClosedAndNoRowIsLeft();
	}

	@Test
	void dataOfAFailedClassSetupShouldNotReachTheNextClass() {
		TestExecutionSummary summary = runSimulatest(FailingClassSetupTest.class, NestedOwnEnvironmentTest.class);

		assertEquals(List.of("FailingClassSetupTest"), namesOfFailed(summary),
				"Only the failing class should be reported as failed");
		assertEquals(2, summary.getTestsSucceededCount(),
				"The class that runs next should see only the rows of its environments");
		assertEveryLevelIsClosedAndNoRowIsLeft();
	}

	@Test
	void nestedClassesShouldSeeTheSetupDataOfTheirOuterClassAndNotOfEachOther() {
		TestExecutionSummary summary = runSimulatest(NestedClassSetupTest.class);

		assertNoFailures(summary);
		assertEquals(3, summary.getTestsSucceededCount(), "2 from WithItsOwnRow + 1 from WithoutItsOwnRow");
		assertEveryLevelIsClosedAndNoRowIsLeft();
	}

	@Test
	void classpathRootScanShouldDiscoverPackagePrivateClasses() throws URISyntaxException {
		Path testClasses = Path.of(
				FirstLevelTest.class.getProtectionDomain().getCodeSource().getLocation().toURI());

		LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
				.selectors(DiscoverySelectors.selectClasspathRoots(Set.of(testClasses)))
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

	@Test
	void exactlyOneEnvironmentDescriptorPerParentShouldBeFlaggedAsLastSibling() {
		SimulatestTestEngine engine = new SimulatestTestEngine();
		LauncherDiscoveryRequest request = simulatestRequest(
				FirstLevelTest.class, SecondLevelTest.class, InsistenceIsolationTest.class);

		TestDescriptor root = engine.discover(request, UniqueId.forEngine(SimulatestTestEngine.ENGINE_ID));

		EnvironmentTestDescriptor bigBangEnv = singleEnvironmentChild(root,
				"The engine root should hold a single BigBangEnvironment descriptor");
		assertTrue(bigBangEnv.isLastEnvironmentSibling(),
				"A sole env child should be flagged as the last env sibling");

		List<EnvironmentTestDescriptor> bigBangChildren = environmentChildrenOf(bigBangEnv);
		assertEquals(2, bigBangChildren.size(),
				"BigBang should contain two env children: FirstLevelEnvironment and InsistenceTestEnvironment");
		EnvironmentTestDescriptor lastInOrder = bigBangChildren.get(bigBangChildren.size() - 1);
		EnvironmentTestDescriptor firstInOrder = bigBangChildren.get(0);
		assertTrue(lastInOrder.isLastEnvironmentSibling(),
				"The env sibling appearing last in insertion order should be flagged as the last env sibling");
		assertFalse(firstInOrder.isLastEnvironmentSibling(),
				"The env sibling appearing earlier should not be flagged");

		EnvironmentTestDescriptor firstLevelEnv = bigBangChildren.stream()
				.filter(d -> d.getDefinition().getName().equals("FirstLevelEnvironment"))
				.findFirst().orElseThrow();
		EnvironmentTestDescriptor secondLevelEnv = singleEnvironmentChild(firstLevelEnv,
				"FirstLevelEnvironment should contain exactly one env child (SecondLevelEnvironment)");
		assertTrue(secondLevelEnv.isLastEnvironmentSibling(),
				"A sole env child should be flagged as the last env sibling");
	}

	private static List<EnvironmentTestDescriptor> environmentChildrenOf(TestDescriptor parent) {
		return parent.getChildren().stream()
				.filter(EnvironmentTestDescriptor.class::isInstance)
				.map(EnvironmentTestDescriptor.class::cast)
				.toList();
	}

	private static EnvironmentTestDescriptor singleEnvironmentChild(TestDescriptor parent, String message) {
		List<EnvironmentTestDescriptor> children = environmentChildrenOf(parent);
		assertEquals(1, children.size(), message);
		return children.get(0);
	}

	@Test
	void insistenceLevelShouldResetBetweenTests() {
		TestExecutionSummary summary = runSimulatest(InsistenceIsolationTest.class);

		assertNoFailures(summary);
		assertEquals(2, summary.getTestsSucceededCount(),
				"Both isolation tests should pass, proving per-test insistence reset works");
	}

	// --- selecting less than a class, the way an IDE asks for one test ---

	@Test
	void selectingOneMethodShouldRunOnlyThatMethod() {
		List<String> ran = testsRunBy(
				DiscoverySelectors.selectMethod(FirstLevelTest.class, "shouldHaveFirstLevelEnvironment"));

		assertEquals(List.of("shouldHaveFirstLevelEnvironment()"), ran);
	}

	@Test
	void selectingTwoMethodsOfOneClassShouldRunBoth() {
		List<String> ran = testsRunBy(
				DiscoverySelectors.selectMethod(FirstLevelTest.class, "shouldHaveFirstLevelEnvironment"),
				DiscoverySelectors.selectMethod(FirstLevelTest.class, "shouldNotHaveSecondLevel"));

		assertEquals(List.of("shouldHaveFirstLevelEnvironment()", "shouldNotHaveSecondLevel()"), ran);
	}

	@Test
	void selectingAClassAndOneOfItsMethodsShouldRunTheWholeClassOnce() {
		List<String> ran = testsRunBy(
				DiscoverySelectors.selectMethod(FirstLevelTest.class, "shouldHaveFirstLevelEnvironment"),
				DiscoverySelectors.selectClass(FirstLevelTest.class));

		assertEquals(List.of("shouldHaveFirstLevelEnvironment()", "shouldNotHaveSecondLevel()"), ran);
	}

	@Test
	void selectingAParameterizedMethodShouldRunEveryInvocation() {
		List<String> ran = testsRunBy(parameterizedMethod());

		assertEquals(3, ran.size(), "One test per value. Ran: " + ran);
	}

	@Test
	void selectingOneIterationShouldRunOnlyThatInvocation() {
		List<String> ran = testsRunBy(DiscoverySelectors.selectIteration(parameterizedMethod(), 1));

		assertEquals(1, ran.size(), "Ran: " + ran);
		assertTrue(ran.get(0).contains("beta"), "The second value should be the one that ran. Ran: " + ran);
	}

	@Test
	void selectingANestedClassShouldRunOnlyItsTestsUnderTheEnclosingEnvironment() throws ClassNotFoundException {
		Class<?> innerClass = Class.forName(AdvancedJupiterTest.class.getName() + "$InnerTest");

		List<String> ran = testsRunBy(
				DiscoverySelectors.selectNestedClass(List.of(AdvancedJupiterTest.class), innerClass));

		assertEquals(List.of("nestedTestShouldSeeEnvironment()"), ran);
	}

	@Test
	void selectingAMethodOfANestedClassShouldRunOnlyThatMethod() throws ClassNotFoundException {
		Class<?> innerClass = Class.forName(AdvancedJupiterTest.class.getName() + "$InnerTest");

		List<String> ran = testsRunBy(DiscoverySelectors.selectNestedMethod(
				List.of(AdvancedJupiterTest.class), innerClass, "nestedTestShouldSeeEnvironment"));

		assertEquals(List.of("nestedTestShouldSeeEnvironment()"), ran);
	}

	@Test
	void selectingANestedClassWithItsOwnEnvironmentShouldRunItUnderThatEnvironment() throws ClassNotFoundException {
		Class<?> nestedClass = Class.forName(NestedOwnEnvironmentTest.class.getName() + "$WithChildEnvironment");

		List<String> ran = testsRunBy(
				DiscoverySelectors.selectNestedClass(List.of(NestedOwnEnvironmentTest.class), nestedClass));

		assertEquals(List.of("nestedShouldSeeParentAndChildRows()"), ran);
	}

	@Test
	void selectingAMethodOfANestedClassWithItsOwnEnvironmentShouldRunItUnderThatEnvironment()
			throws ClassNotFoundException {
		Class<?> nestedClass = Class.forName(NestedOwnEnvironmentTest.class.getName() + "$WithChildEnvironment");

		List<String> ran = testsRunBy(DiscoverySelectors.selectNestedMethod(
				List.of(NestedOwnEnvironmentTest.class), nestedClass, "nestedShouldSeeParentAndChildRows"));

		assertEquals(List.of("nestedShouldSeeParentAndChildRows()"), ran);
	}

	@Test
	void selectingAnInheritedNestedClassShouldRunItUnderTheSubclassEnvironment() throws ClassNotFoundException {
		Class<?> inheritedNested = Class.forName(RowBaseTest.class.getName() + "$InheritedNested");

		List<String> ran = testsRunBy(
				DiscoverySelectors.selectNestedClass(List.of(InheritedNestedTest.class), inheritedNested));

		assertEquals(List.of("shouldSeeTheRowsOfTheRunningClass()"), ran);
	}

	@Test
	void selectingTheUniqueIdOfATestShouldRunOnlyThatTestAgain() {
		String uniqueId = uniqueIdsOfTestsRunBy(DiscoverySelectors.selectClass(FirstLevelTest.class)).get(1);

		List<String> ran = testsRunBy(DiscoverySelectors.selectUniqueId(uniqueId));

		assertEquals(List.of("shouldNotHaveSecondLevel()"), ran);
	}

	@Test
	void selectingTheUniqueIdOfATestClassShouldRunTheWholeClassAgain() {
		String testId = uniqueIdsOfTestsRunBy(DiscoverySelectors.selectClass(FirstLevelTest.class)).get(0);
		String classId = UniqueId.parse(testId).removeLastSegment().toString();

		List<String> ran = testsRunBy(DiscoverySelectors.selectUniqueId(classId));

		assertEquals(List.of("shouldHaveFirstLevelEnvironment()", "shouldNotHaveSecondLevel()"), ran);
	}

	@Test
	void theUniqueIdOfATestShouldNotChangeWhenFewerTestsRun() {
		List<String> wholeClass = uniqueIdsOfTestsRunBy(DiscoverySelectors.selectClass(FirstLevelTest.class));
		List<String> oneMethod = uniqueIdsOfTestsRunBy(
				DiscoverySelectors.selectMethod(FirstLevelTest.class, "shouldNotHaveSecondLevel"));

		assertEquals(List.of(wholeClass.get(1)), oneMethod);
	}

	// --- the filters of the launcher, the way a build tool narrows a run ---

	@Test
	void includingATagShouldRunOnlyTheTestsWithThatTag() {
		List<String> ran = testsRunBy(TagFilter.includeTags("fast"), DiscoverySelectors.selectClass(TaggedTest.class));

		assertEquals(List.of("fast()"), ran);
	}

	@Test
	void excludingATagShouldNotRunTheTestsWithThatTag() {
		List<String> ran = testsRunBy(TagFilter.excludeTags("slow"), DiscoverySelectors.selectClass(TaggedTest.class));

		assertEquals(List.of("fast()"), ran);
	}

	@Test
	void aClassWithNoTestLeftShouldNotRunItsEnvironment() {
		testsRunBy(TagFilter.includeTags("no-test-has-this-tag"), DiscoverySelectors.selectClass(TaggedTest.class));

		assertFalse(EnvironmentTracker.getEvents().contains("FirstLevel"),
				"No test is left to run, so the environment has nothing to be built for");
	}

	@Test
	void aFilterShouldNotWidenASelectionOfOneInvocation() {
		List<String> ran = testsRunBy(
				TagFilter.excludeTags("no-test-has-this-tag"),
				DiscoverySelectors.selectIteration(parameterizedMethod(), 1),
				DiscoverySelectors.selectMethod(AdvancedJupiterTest.class, "repeatedTestShouldSeeEnvironment"));

		assertEquals(4, ran.size(), "1 invocation of the parameterized test + 3 repetitions. Ran: " + ran);
	}

	@Test
	void aFilterThatTakesASiblingOutShouldNotWidenASelectionOfOneInvocation() {
		List<String> ran = testsRunBy(
				onlyTheMethodNamed("parameterizedTestShouldSeeEnvironment"),
				DiscoverySelectors.selectIteration(parameterizedMethod(), 1),
				DiscoverySelectors.selectMethod(AdvancedJupiterTest.class, "repeatedTestShouldSeeEnvironment"));

		assertEquals(1, ran.size(), "Only the invocation asked for. Ran: " + ran);
	}

	@Test
	void aFilterOnTheMethodShouldRunOnlyThatMethod() {
		List<String> ran = testsRunBy(
				onlyTheMethodNamed("shouldNotHaveSecondLevel"), DiscoverySelectors.selectClass(FirstLevelTest.class));

		assertEquals(List.of("shouldNotHaveSecondLevel()"), ran);
	}

	// --- what the launcher knows about the tests of a class ---

	@Test
	void theTestsOfAClassShouldBeKnownBeforeTheRun() {
		TestPlan plan = LauncherFactory.create().discover(requestFor(DiscoverySelectors.selectClass(FirstLevelTest.class)));

		List<String> tests = plan.getRoots().stream()
				.flatMap(root -> plan.getDescendants(root).stream())
				.filter(TestIdentifier::isTest)
				.map(TestIdentifier::getDisplayName)
				.toList();
		assertEquals(List.of("shouldHaveFirstLevelEnvironment()", "shouldNotHaveSecondLevel()"), tests);
	}

	@Test
	void aDisabledTestShouldBeReportedAsSkipped() {
		TestExecutionSummary summary = runSimulatest(TaggedTest.class);

		assertEquals(1, summary.getTestsSkippedCount());
	}

	@Test
	void aClassJupiterCannotDiscoverShouldBeReportedAsFailedAndNotStopTheOthers() {
		LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
				.selectors(
						DiscoverySelectors.selectMethod(FirstLevelTest.class, "aMethodTheClassDoesNotHave"),
						DiscoverySelectors.selectClass(AnotherFirstLevelTest.class))
				.filters(EngineFilter.includeEngines(SimulatestTestEngine.ENGINE_ID))
				.build();
		SummaryGeneratingListener listener = new SummaryGeneratingListener();

		LauncherFactory.create().execute(request, listener);

		assertEquals(List.of("FirstLevelTest"), namesOfFailed(listener.getSummary()));
		assertEquals(1, listener.getSummary().getTestsSucceededCount(),
				"The test of AnotherFirstLevelTest should still run");
	}

	@Test
	void theTestsOfAClassWhoseSetupFailsShouldBeReportedAsSkipped() {
		TestExecutionSummary summary = runSimulatest(FailingBeforeAllTest.class);

		assertEquals(1, summary.getTestsSkippedCount(), "The test never ran, and should not be missing from the report");
	}

	@Test
	void theInvocationsOfAParameterizedTestShouldBeReportedInsideTheirMethod() {
		String invocation = uniqueIdsOfTestsRunBy(parameterizedMethod()).get(0);

		String method = UniqueId.parse(invocation).removeLastSegment().getLastSegment().getValue();
		assertTrue(method.contains("parameterizedTestShouldSeeEnvironment"),
				"The segment before the invocation should be its method, was: " + method);
	}

	@Test
	void selectingTheUniqueIdOfOneInvocationShouldRunOnlyThatInvocationAgain() {
		String secondInvocation = uniqueIdsOfTestsRunBy(parameterizedMethod()).get(1);

		List<String> ran = testsRunBy(DiscoverySelectors.selectUniqueId(secondInvocation));

		assertEquals(1, ran.size(), "Ran: " + ran);
		assertTrue(ran.get(0).contains("beta"), "The second value should be the one that ran. Ran: " + ran);
	}

	// --- helpers ---

	// Takes out every test method but one, like the filter Surefire adds for
	// -Dtest=SomeClass#someMethod.
	private static PostDiscoveryFilter onlyTheMethodNamed(String methodName) {
		return descriptor -> FilterResult.includedIf(!isAnotherMethod(descriptor, methodName));
	}

	private static boolean isAnotherMethod(TestDescriptor descriptor, String methodName) {
		return descriptor.getSource()
				.filter(MethodSource.class::isInstance)
				.map(MethodSource.class::cast)
				.filter(source -> !source.getMethodName().equals(methodName))
				.isPresent();
	}

	private static DiscoverySelector parameterizedMethod() {
		return DiscoverySelectors.selectMethod(
				AdvancedJupiterTest.class, "parameterizedTestShouldSeeEnvironment", "java.lang.String");
	}

	private static List<String> testsRunBy(DiscoverySelector... selectors) {
		return finishedTestsOf(requestFor(selectors)).stream().map(TestIdentifier::getDisplayName).toList();
	}

	private static List<String> testsRunBy(PostDiscoveryFilter filter, DiscoverySelector... selectors) {
		LauncherDiscoveryRequest filtered = LauncherDiscoveryRequestBuilder.request()
				.selectors(selectors)
				.filters(filter)
				.build();
		return finishedTestsOf(filtered).stream().map(TestIdentifier::getDisplayName).toList();
	}

	private static List<String> uniqueIdsOfTestsRunBy(DiscoverySelector... selectors) {
		return finishedTestsOf(requestFor(selectors)).stream().map(TestIdentifier::getUniqueId).toList();
	}

	private static LauncherDiscoveryRequest requestFor(DiscoverySelector... selectors) {
		return LauncherDiscoveryRequestBuilder.request().selectors(selectors).build();
	}

	// Runs with every engine on the classpath and the filters registered by default,
	// like an IDE does, so a test picked up by no engine or by two shows up here.
	private static List<TestIdentifier> finishedTestsOf(LauncherDiscoveryRequest request) {
		List<TestIdentifier> finished = new ArrayList<>();
		SummaryGeneratingListener summary = new SummaryGeneratingListener();

		LauncherFactory.create().execute(request, summary, new TestExecutionListener() {
			@Override
			public void executionFinished(TestIdentifier identifier, TestExecutionResult result) {
				if (identifier.isTest()) finished.add(identifier);
			}
		});

		assertNoFailures(summary.getSummary());
		return finished;
	}

	private static LauncherDiscoveryRequest simulatestRequest(Class<?>... testClasses) {
		return LauncherDiscoveryRequestBuilder.request()
				.selectors(DiscoverySelectors.selectClasspathRoots(Set.of()))
				.selectors(Arrays.stream(testClasses)
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

	private static List<String> namesOfFailed(TestExecutionSummary summary) {
		return summary.getFailures().stream()
				.map(failure -> failure.getTestIdentifier().getDisplayName())
				.toList();
	}

	private static void assertEveryLevelIsClosedAndNoRowIsLeft() {
		assertEquals(0, InsistenceLayerFactory.resolve().orElseThrow().getCurrentLevel(),
				"Every level should be closed after the run");
		assertEquals(0, EnvRows.count(), "No row should stay in the database after the run");
	}

	private static void assertNoFailures(TestExecutionSummary summary) {
		if (!summary.getFailures().isEmpty()) {
			StringBuilder details = new StringBuilder("Test failures:");
			summary.getFailures().forEach(f ->
				details.append("\n  ").append(f.getTestIdentifier().getDisplayName())
					.append(": ").append(f.getException()));
			assertEquals(0, summary.getTestsFailedCount(), details.toString());
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
