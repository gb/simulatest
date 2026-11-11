package org.simulatest.environment.junit5.descriptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.junit.platform.engine.EngineExecutionListener;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.TestExecutionResult.Status;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.reporting.ReportEntry;
import org.junit.platform.engine.support.hierarchical.ThrowableCollector;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;
import org.opentest4j.TestAbortedException;

/**
 * Passes on what Jupiter reports during the inner run of one test class, at
 * the moment Jupiter reports it, as events of the Simulatest engine.
 *
 * <p>So a test is reported as started when it starts and as finished when it
 * finishes: a result does not wait for the end of its class, and the time
 * between the two events is the time the test took.</p>
 *
 * <p>Each event of Jupiter is about a test or container Jupiter knows by a
 * unique id. The reporter finds the descriptor of the Simulatest engine for
 * that id:</p>
 * <ul>
 *   <li>a test discovered before the run has one already;</li>
 *   <li>what Jupiter makes while it runs (one invocation of a parameterized
 *       test, a dynamic test or container) gets one when Jupiter registers
 *       it;</li>
 *   <li>a container around the discovered tests (the class, a nested class)
 *       has none. What Jupiter says about it explains the tests inside it
 *       that never ran, and fails the class when the container failed.</li>
 * </ul>
 *
 * <p><b>Thread-safety:</b> not thread-safe; Jupiter calls it on the thread
 * that runs the class.</p>
 */
final class JupiterRunReporter implements TestExecutionListener {

	private static final String NEVER_RUN = "Jupiter did not run it";

	private final TestDescriptor testClass;
	private final EngineExecutionListener listener;
	private final Map<UniqueId, TestDescriptor> descriptorsByJupiterId = new HashMap<>();
	private final Set<JupiterTestDescriptor> discoveredAndNotReportedYet;
	private final Map<UniqueId, String> whyNothingInsideRan = new HashMap<>();
	private final ThrowableCollector failuresOfContainers = new ThrowableCollector(TestAbortedException.class::isInstance);

	JupiterRunReporter(TestDescriptor testClass, List<JupiterTestDescriptor> discoveredTests,
			EngineExecutionListener listener) {
		this.testClass = testClass;
		this.listener = listener;
		this.discoveredAndNotReportedYet = new LinkedHashSet<>(discoveredTests);
		for (JupiterTestDescriptor test : discoveredTests) descriptorsByJupiterId.put(test.jupiterId(), test);
	}

	// What Jupiter makes while it runs goes inside the descriptor that made it,
	// or inside the class when none of the descriptors is around it.
	@Override
	public void dynamicTestRegistered(TestIdentifier madeWhileRunning) {
		UniqueId jupiterId = madeWhileRunning.getUniqueIdObject();
		TestDescriptor parent = nearestAround(jupiterId, descriptorsByJupiterId).orElse(testClass);

		TestDescriptor descriptor = new JupiterTestDescriptor(parent, madeWhileRunning);
		parent.addChild(descriptor);
		descriptorsByJupiterId.put(jupiterId, descriptor);
		listener.dynamicTestRegistered(descriptor);
	}

	@Override
	public void executionStarted(TestIdentifier identifier) {
		descriptorOf(identifier).ifPresent(descriptor -> {
			discoveredAndNotReportedYet.remove(descriptor);
			listener.executionStarted(descriptor);
		});
	}

	@Override
	public void executionSkipped(TestIdentifier identifier, String reason) {
		descriptorOf(identifier).ifPresentOrElse(
				descriptor -> reportSkipped(descriptor, reason),
				() -> whyNothingInsideRan.put(identifier.getUniqueIdObject(), reason));
	}

	@Override
	public void executionFinished(TestIdentifier identifier, TestExecutionResult result) {
		descriptorOf(identifier).ifPresentOrElse(
				descriptor -> listener.executionFinished(descriptor, result),
				() -> containerFinished(identifier, result));
	}

	@Override
	public void reportingEntryPublished(TestIdentifier identifier, ReportEntry entry) {
		listener.reportingEntryPublished(descriptorOf(identifier).orElse(testClass), entry);
	}

	// Jupiter says nothing about a test inside a container that is disabled,
	// failed or was aborted. Such a test is still in the plan of the launcher, so
	// it is reported as skipped, with what the container around it had to tell.
	@Override
	public void testPlanExecutionFinished(TestPlan innerPlan) {
		for (JupiterTestDescriptor neverRun : List.copyOf(discoveredAndNotReportedYet)) {
			reportSkipped(neverRun, whyItNeverRan(neverRun));
		}
	}

	/**
	 * Throws what a container around the tests failed with: a class whose
	 * setup or teardown threw, for example. No test reports that, so the class
	 * has to. Does nothing when no container failed.
	 */
	void rethrowFailuresOfContainers() {
		failuresOfContainers.assertEmpty();
	}

	private void reportSkipped(TestDescriptor descriptor, String reason) {
		discoveredAndNotReportedYet.remove(descriptor);
		listener.executionSkipped(descriptor, reason);
	}

	private void containerFinished(TestIdentifier container, TestExecutionResult result) {
		if (result.getStatus() == Status.SUCCESSFUL) return;

		whyNothingInsideRan.put(container.getUniqueIdObject(), whatHappenedTo(container, result));
		if (failsTheClass(container, result)) result.getThrowable().ifPresent(this::collect);
	}

	// A nested class that was aborted only explains why its own tests did not
	// run. The class is aborted when its own container is.
	private boolean failsTheClass(TestIdentifier container, TestExecutionResult result) {
		return result.getStatus() == Status.FAILED || container.getSource().equals(testClass.getSource());
	}

	// The collector keeps the first failure and adds the others to it as
	// suppressed. A failure takes the place of an abort that came before it.
	private void collect(Throwable failure) {
		failuresOfContainers.execute(() -> {
			throw failure;
		});
	}

	private static String whatHappenedTo(TestIdentifier container, TestExecutionResult result) {
		String what = result.getStatus() == Status.ABORTED ? " was aborted" : " failed";
		return container.getDisplayName() + what;
	}

	private String whyItNeverRan(JupiterTestDescriptor discovered) {
		return nearestAround(discovered.jupiterId(), whyNothingInsideRan).orElse(NEVER_RUN);
	}

	private Optional<TestDescriptor> descriptorOf(TestIdentifier identifier) {
		return Optional.ofNullable(descriptorsByJupiterId.get(identifier.getUniqueIdObject()));
	}

	private static <T> Optional<T> nearestAround(UniqueId jupiterId, Map<UniqueId, T> byJupiterId) {
		return containersAround(jupiterId).stream()
				.map(byJupiterId::get)
				.filter(Objects::nonNull)
				.findFirst();
	}

	// Nearest first, up to the engine. The engine is the one segment an id cannot
	// lose, so the walk stops there.
	private static List<UniqueId> containersAround(UniqueId jupiterId) {
		List<UniqueId> containers = new ArrayList<>();
		UniqueId container = jupiterId;
		while (container.getSegments().size() > 1) {
			container = container.removeLastSegment();
			containers.add(container);
		}
		return containers;
	}

}
