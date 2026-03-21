package org.simulatest.jeerunner.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.EngineFilter;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;
import org.simulatest.environment.junit5.SimulatestTestEngine;
import org.simulatest.insistencelayer.InsistenceLayerFactory;
import org.simulatest.jeerunner.test.testdouble.SimpleJakartaJUnit5Test;

class SimulatestJakartaPluginTest {

	@BeforeAll
	static void configureDataSource() {
		JdbcDataSource h2 = new JdbcDataSource();
		h2.setURL("jdbc:h2:mem:jeejunit5test;DB_CLOSE_DELAY=-1");
		h2.setUser("sa");
		InsistenceLayerFactory.configure(h2);
	}

	@Test
	void jakartaPluginShouldInitializeCdiAndRunEnvironments() {
		LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
				.selectors(DiscoverySelectors.selectClass(SimpleJakartaJUnit5Test.class))
				.filters(EngineFilter.includeEngines(SimulatestTestEngine.ENGINE_ID))
				.build();

		SummaryGeneratingListener summaryListener = new SummaryGeneratingListener();
		Launcher launcher = LauncherFactory.create();
		launcher.execute(request, summaryListener);

		TestExecutionSummary summary = summaryListener.getSummary();

		if (!summary.getFailures().isEmpty()) {
			StringBuilder details = new StringBuilder("Jakarta CDI test failures:");
			summary.getFailures().forEach(f ->
				details.append("\n  ").append(f.getTestIdentifier().getDisplayName())
					.append(": ").append(f.getException()));
			assertEquals(0, summary.getTestsFailedCount(), details.toString());
		}
		assertEquals(2, summary.getTestsSucceededCount(), "Should run 2 test methods");
	}

}
