package org.simulatest.jeerunner.junit5.test;

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
import org.simulatest.insistencelayer.datasource.InsistenceLayerDataSource;
import org.simulatest.jeerunner.junit5.test.testdouble.SimpleJakartaJUnit5Test;

/**
 * Integration test verifying that the Jakarta CDI plugin correctly
 * initializes the CDI container and creates CDI-managed environments.
 */
class SimulatestJakartaPluginTest {

	@BeforeAll
	static void configureDataSource() {
		JdbcDataSource h2 = new JdbcDataSource();
		h2.setURL("jdbc:h2:mem:jeejunit5test;DB_CLOSE_DELAY=-1");
		h2.setUser("sa");
		InsistenceLayerDataSource.configure(h2);
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
			summary.getFailures().forEach(f ->
				System.err.println(f.getTestIdentifier().getDisplayName() + ": " + f.getException()));
		}

		assertEquals(0, summary.getTestsFailedCount(),
				"All Jakarta CDI tests should pass");
		assertEquals(2, summary.getTestsSucceededCount(),
				"Should run 2 test methods");
	}

}
