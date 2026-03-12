package org.simulatest.environment.environment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Test;

public class EnvironmentScannerTest {

	@Test
	public void shouldFindEnvironmentsInPackage() {
		EnvironmentScanner scanner = new EnvironmentScanner("org.simulatest.environment.test.testdouble.environment");
		Collection<Class<?>> environments = scanner.getEnvironmentList();

		assertEquals(2, environments.size());
	}

	@Test
	public void shouldReturnEmptyForPackageWithNoEnvironments() {
		EnvironmentScanner scanner = new EnvironmentScanner("org.simulatest.nonexistent");
		assertTrue(scanner.getEnvironmentList().isEmpty());
	}

	@Test
	public void shouldFindEnvironmentsWithDefaultConstructor() {
		EnvironmentScanner scanner = new EnvironmentScanner();
		Collection<Class<?>> environments = scanner.getEnvironmentList();

		assertTrue("should find at least BigBangEnvironment", environments.contains(BigBangEnvironment.class));
	}

}
