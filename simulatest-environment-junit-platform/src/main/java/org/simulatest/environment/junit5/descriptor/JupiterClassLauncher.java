package org.simulatest.environment.junit5.descriptor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.platform.engine.DiscoverySelector;
import org.junit.platform.engine.FilterResult;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.EngineFilter;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.PostDiscoveryFilter;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;
import org.junit.platform.launcher.core.LauncherConfig;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.simulatest.environment.junit5.OwnEnvironmentClassFilter;

/**
 * Asks Jupiter to discover and to run the tests of one test class, in a
 * launcher of its own inside the Simulatest engine.
 *
 * <p>Jupiter handles everything about the tests: {@code @Test},
 * {@code @ParameterizedTest}, {@code @RepeatedTest}, {@code @TestFactory},
 * {@code @Nested}, lifecycle callbacks and extensions.</p>
 */
final class JupiterClassLauncher {

	private static final String JUPITER_ENGINE_ID = "junit-jupiter";
	private static final String AUTODETECTION_KEY = "junit.jupiter.extensions.autodetection.enabled";
	private static final String AUTODETECTION_VALUE_ENABLED = "true";

	// Shared by the inner runs. Nothing is auto-registered: the filter of this
	// engine would take the inner runs away from Jupiter, and the listeners of
	// the user already hear the outer run; here they would hear every class again.
	private static final Launcher LAUNCHER = LauncherFactory.create(LauncherConfig.builder()
			.enablePostDiscoveryFilterAutoRegistration(false)
			.enableTestExecutionListenerAutoRegistration(false)
			.enableLauncherSessionListenerAutoRegistration(false)
			.enableLauncherDiscoveryListenerAutoRegistration(false)
			.build());

	private final Class<?> testClass;
	private final List<DiscoverySelector> parts;

	/**
	 * @param parts the selectors that narrow the class to part of it; empty
	 *              for the whole class
	 */
	JupiterClassLauncher(Class<?> testClass, List<DiscoverySelector> parts) {
		this.testClass = testClass;
		this.parts = List.copyOf(parts);
	}

	/**
	 * Returns the leaves of what Jupiter finds: each test, and each container
	 * that makes its tests while it runs (a parameterized or repeated test, a
	 * test factory).
	 */
	List<TestIdentifier> discover() {
		TestPlan plan = LAUNCHER.discover(request(everythingSelected()));
		return plan.getRoots().stream()
				.flatMap(root -> plan.getDescendants(root).stream())
				.filter(identifier -> plan.getChildren(identifier).isEmpty())
				.toList();
	}

	/** Runs the given leaves, out of the ones {@link #discover()} returned. */
	void run(Set<UniqueId> leaves, TestExecutionListener listener) {
		LAUNCHER.execute(request(only(leaves)), listener);
	}

	private LauncherDiscoveryRequest request(PostDiscoveryFilter leavesToRun) {
		return LauncherDiscoveryRequestBuilder.request()
				.selectors(parts.isEmpty() ? List.of(wholeClass()) : parts)
				.filters(EngineFilter.includeEngines(JUPITER_ENGINE_ID), new OwnEnvironmentClassFilter(testClass),
						leavesToRun)
				.configurationParameter(AUTODETECTION_KEY, AUTODETECTION_VALUE_ENABLED)
				.build();
	}

	private static PostDiscoveryFilter everythingSelected() {
		return descriptor -> FilterResult.included("Everything selected is discovered");
	}

	// The class is selected the same way as at discovery, so a selection of one
	// invocation stays one invocation. A container around a leaf has to stay
	// for the leaf to stay.
	private static PostDiscoveryFilter only(Set<UniqueId> leaves) {
		return descriptor -> FilterResult.includedIf(
				leaves.stream().anyMatch(leaf -> leaf.hasPrefix(descriptor.getUniqueId())));
	}

	// Jupiter needs the full chain of enclosing classes to select a @Nested class.
	private DiscoverySelector wholeClass() {
		List<Class<?>> enclosingClasses = enclosingClassesOutermostFirst();
		return enclosingClasses.isEmpty()
				? DiscoverySelectors.selectClass(testClass)
				: DiscoverySelectors.selectNestedClass(enclosingClasses, testClass);
	}

	private List<Class<?>> enclosingClassesOutermostFirst() {
		List<Class<?>> enclosingClasses = new ArrayList<>();
		Class<?> enclosing = testClass.getEnclosingClass();
		while (enclosing != null) {
			enclosingClasses.add(enclosing);
			enclosing = enclosing.getEnclosingClass();
		}
		Collections.reverse(enclosingClasses);
		return enclosingClasses;
	}

}
