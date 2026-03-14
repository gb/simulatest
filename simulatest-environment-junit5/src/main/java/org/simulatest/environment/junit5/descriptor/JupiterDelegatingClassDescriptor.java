package org.simulatest.environment.junit5.descriptor;

import java.util.ArrayList;
import java.util.List;

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
public class JupiterDelegatingClassDescriptor extends AbstractTestDescriptor
		implements Node<SimulatestExecutionContext> {

	private static final String JUPITER_ENGINE_ID = "junit-jupiter";
	private static final String AUTODETECTION_KEY = "junit.jupiter.extensions.autodetection.enabled";

	private static volatile Launcher jupiterLauncher;

	private static Launcher jupiterLauncher() {
		if (jupiterLauncher == null) {
			synchronized (JupiterDelegatingClassDescriptor.class) {
				if (jupiterLauncher == null) {
					LauncherConfig config = LauncherConfig.builder()
							.enablePostDiscoveryFilterAutoRegistration(false)
							.enableTestExecutionListenerAutoRegistration(false)
							.build();
					jupiterLauncher = LauncherFactory.create(config);
				}
			}
		}
		return jupiterLauncher;
	}

	private final Class<?> testClass;

	public JupiterDelegatingClassDescriptor(UniqueId uniqueId, Class<?> testClass) {
		super(uniqueId, testClass.getSimpleName(), ClassSource.from(testClass));
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
		SimulatestExecutionContext.setCurrent(context);
		try {
			List<CapturedResult> results = runJupiter();
			registerDynamicTests(results, dynamicTestExecutor);
		} finally {
			SimulatestExecutionContext.clearCurrent();
		}
		return context;
	}

	private List<CapturedResult> runJupiter() {
		LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
				.selectors(DiscoverySelectors.selectClass(testClass))
				.filters(EngineFilter.includeEngines(JUPITER_ENGINE_ID))
				.configurationParameter(AUTODETECTION_KEY, "true")
				.build();

		ResultCapturingListener listener = new ResultCapturingListener();
		jupiterLauncher().execute(request, listener);
		return listener.results;
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
