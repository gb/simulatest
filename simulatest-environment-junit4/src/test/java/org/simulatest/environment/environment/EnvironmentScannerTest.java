package org.simulatest.environment.environment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Test;
import org.simulatest.environment.BigBangEnvironment;
import org.simulatest.environment.EnvironmentScanner;

public class EnvironmentScannerTest {

	@Test
	public void shouldFindEnvironmentsInPackage() {
		EnvironmentScanner scanner = new EnvironmentScanner("org.simulatest.environment.test.testdouble.environment");
		Collection<Class<?>> environments = scanner.getEnvironments();

		assertEquals(2, environments.size());
	}

	@Test
	public void shouldReturnEmptyForPackageWithNoEnvironments() {
		EnvironmentScanner scanner = new EnvironmentScanner("org.simulatest.nonexistent");
		assertTrue(scanner.getEnvironments().isEmpty());
	}

	@Test
	public void shouldFindEnvironmentsWithDefaultConstructor() {
		EnvironmentScanner scanner = new EnvironmentScanner();
		Collection<Class<?>> environments = scanner.getEnvironments();

		assertTrue("should find at least BigBangEnvironment", environments.contains(BigBangEnvironment.class));
	}

}
