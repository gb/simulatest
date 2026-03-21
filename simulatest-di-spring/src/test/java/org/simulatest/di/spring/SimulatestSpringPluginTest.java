package org.simulatest.di.spring;

import org.junit.jupiter.api.Test;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SimulatestSpringPluginTest {

	@Test
	void springPluginShouldAutowireAndRunEnvironments() {
		LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
				.selectors(DiscoverySelectors.selectClass(SimpleSpringJUnit5IT.class))
				.build();

		SummaryGeneratingListener summaryListener = new SummaryGeneratingListener();
		Launcher launcher = LauncherFactory.create();
		launcher.execute(request, summaryListener);

		TestExecutionSummary summary = summaryListener.getSummary();

		if (!summary.getFailures().isEmpty()) {
			StringBuilder details = new StringBuilder("Spring test failures:");
			summary.getFailures().forEach(f ->
				details.append("\n  ").append(f.getTestIdentifier().getDisplayName())
					.append(": ").append(f.getException()));
			assertEquals(0, summary.getTestsFailedCount(), details.toString());
		}
		assertEquals(2, summary.getTestsSucceededCount(), "Should run 2 test methods");
	}

}
