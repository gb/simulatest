package org.simulatest.environment.junit5.descriptor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;

/**
 * Everything Jupiter reported during the inner run of one test class, kept by
 * the unique id Jupiter gave each test and container.
 *
 * <p>It listens to the inner run and is read afterwards, when the Simulatest
 * engine reports the tests of the class.</p>
 *
 * <p><b>Thread-safety:</b> not thread-safe; filled and read on the thread that
 * runs the class.</p>
 */
final class JupiterOutcomes implements TestExecutionListener {

	private final Map<UniqueId, JupiterOutcome> outcomesById = new LinkedHashMap<>();

	@Override
	public void executionFinished(TestIdentifier identifier, TestExecutionResult result) {
		outcomesById.put(identifier.getUniqueIdObject(), JupiterOutcome.finished(identifier, result));
	}

	@Override
	public void executionSkipped(TestIdentifier identifier, String reason) {
		outcomesById.put(identifier.getUniqueIdObject(), JupiterOutcome.skipped(identifier, reason));
	}

	Optional<JupiterOutcome> of(UniqueId jupiterId) {
		return Optional.ofNullable(outcomesById.get(jupiterId));
	}

	/** The outcome of the nearest container around the given id that has one. */
	Optional<JupiterOutcome> ofNearestContainerAround(UniqueId jupiterId) {
		return containersAround(jupiterId).stream()
				.map(outcomesById::get)
				.filter(Objects::nonNull)
				.findFirst();
	}

	/** The tests Jupiter ran inside the given container, at any depth. */
	List<JupiterOutcome> testsInside(UniqueId container) {
		return outcomesById.values().stream()
				.filter(JupiterOutcome::isTest)
				.filter(outcome -> outcome.isInside(container))
				.toList();
	}

	/**
	 * The outcomes none of the given children reports: a container that did
	 * not succeed, like a class whose setup threw, and a test that is neither
	 * one of the children nor inside one of them.
	 */
	List<JupiterOutcome> notReportedBy(Set<UniqueId> childIds) {
		return outcomesById.values().stream()
				.filter(outcome -> !childIds.contains(outcome.jupiterId()))
				.filter(outcome -> isContainerThatDidNotSucceed(outcome) || isTestOutside(childIds, outcome))
				.toList();
	}

	private static boolean isContainerThatDidNotSucceed(JupiterOutcome outcome) {
		return !outcome.isTest() && outcome.didNotSucceed();
	}

	private static boolean isTestOutside(Set<UniqueId> containerIds, JupiterOutcome outcome) {
		return outcome.isTest() && containersAround(outcome.jupiterId()).stream().noneMatch(containerIds::contains);
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
