package org.simulatest.environment.junit5.descriptor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.junit.platform.engine.DiscoverySelector;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.TestSource;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.platform.engine.support.descriptor.ClassSource;
import org.junit.platform.engine.support.hierarchical.Node;
import org.junit.platform.launcher.EngineFilter;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.core.LauncherConfig;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.simulatest.environment.junit5.SimulatestExecutionContext;

/**
 * Delegates test execution for a single class to Jupiter.
 *
 * <p>At execution time, this descriptor launches an internal Jupiter session for
 * {@code testClass}. Jupiter handles all discovery and execution — {@code @Test},
 * {@code @ParameterizedTest}, {@code @RepeatedTest}, {@code @TestFactory},
 * {@code @Nested}, lifecycle callbacks, extensions — everything. Results are
 * captured and replayed as dynamic {@link TestResultDescriptor} children so the
 * Simulatest engine reports them correctly to the IDE.</p>
 *
 * <p>The Insistence Layer's per-test {@code resetCurrentLevel()} is handled by
 * {@link org.simulatest.environment.junit5.extension.InsistenceAfterEachExtension},
 * which Jupiter auto-detects during the internal session.</p>
 */
public final class JupiterDelegatingClassDescriptor extends AbstractTestDescriptor
		implements Node<SimulatestExecutionContext> {

	private static final String JUPITER_ENGINE_ID = "junit-jupiter";
	private static final String AUTODETECTION_KEY = "junit.jupiter.extensions.autodetection.enabled";

	// Shared launcher for the inner Jupiter sessions. Auto-registration is disabled
	// to prevent SimulatestPostDiscoveryFilter from intercepting these internal runs
	// (which would cause infinite recursion) and to avoid duplicate listener output.
	private static final Launcher JUPITER_LAUNCHER;

	static {
		LauncherConfig config = LauncherConfig.builder()
				.enablePostDiscoveryFilterAutoRegistration(false)
				.enableTestExecutionListenerAutoRegistration(false)
				.build();
		JUPITER_LAUNCHER = LauncherFactory.create(config);
	}

	private final Class<?> testClass;

	public JupiterDelegatingClassDescriptor(UniqueId uniqueId, Class<?> testClass) {
		super(uniqueId, Objects.requireNonNull(testClass, "testClass must not be null").getSimpleName(),
				ClassSource.from(testClass));
		this.testClass = testClass;
	}

	@Override
	public Type getType() {
		return Type.CONTAINER;
	}

	@Override
	public boolean mayRegisterTests() {
		return true;
	}

	@Override
	public SimulatestExecutionContext execute(SimulatestExecutionContext context,
			DynamicTestExecutor dynamicTestExecutor) {
		SimulatestExecutionContext.withCurrent(context, () -> {
			List<CapturedResult> results = runJupiter();
			registerDynamicTests(results, dynamicTestExecutor);
		});
		return context;
	}

	private List<CapturedResult> runJupiter() {
		LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
				.selectors(selectTestClass())
				.filters(EngineFilter.includeEngines(JUPITER_ENGINE_ID))
				.configurationParameter(AUTODETECTION_KEY, "true")
				.build();

		ResultCapturingListener listener = new ResultCapturingListener();
		JUPITER_LAUNCHER.execute(request, listener);
		return listener.results;
	}

	// Jupiter requires the full enclosing-class chain for @Nested inner classes;
	// for top-level classes, a simple selectClass suffices.
	private DiscoverySelector selectTestClass() {
		Class<?> enclosing = testClass.getEnclosingClass();
		if (enclosing == null) {
			return DiscoverySelectors.selectClass(testClass);
		}
		List<Class<?>> enclosingChain = new ArrayList<>();
		for (Class<?> c = enclosing; c != null; c = c.getEnclosingClass()) {
			enclosingChain.add(c);
		}
		Collections.reverse(enclosingChain);
		return DiscoverySelectors.selectNestedClass(enclosingChain, testClass);
	}

	private void registerDynamicTests(List<CapturedResult> results, DynamicTestExecutor executor) {
		TestSource fallbackSource = ClassSource.from(testClass);
		int index = 0;
		for (CapturedResult captured : results) {
			TestSource source = captured.source != null ? captured.source : fallbackSource;
			UniqueId testId = getUniqueId().append("test", captured.displayName + "#" + (index++));
			TestResultDescriptor descriptor = captured.isSkipped()
					? TestResultDescriptor.skipped(testId, captured.displayName, source, captured.skipReason)
					: TestResultDescriptor.fromResult(testId, captured.displayName, source, captured.result);
			addChild(descriptor);
			executor.execute(descriptor);
		}
	}

	private static class ResultCapturingListener implements TestExecutionListener {

		final List<CapturedResult> results = new ArrayList<>();

		@Override
		public void executionFinished(TestIdentifier identifier, TestExecutionResult result) {
			if (identifier.isTest() || result.getStatus() != TestExecutionResult.Status.SUCCESSFUL) {
				results.add(new CapturedResult(
						identifier.getDisplayName(),
						identifier.getSource().orElse(null),
						result, null));
			}
		}

		@Override
		public void executionSkipped(TestIdentifier identifier, String reason) {
			if (identifier.isTest()) {
				results.add(new CapturedResult(
						identifier.getDisplayName(),
						identifier.getSource().orElse(null),
						null, reason));
			}
		}
	}

	private record CapturedResult(String displayName, TestSource source,
			TestExecutionResult result, String skipReason) {

		boolean isSkipped() {
			return skipReason != null;
		}
	}

}
