package org.simulatest.environment.junit5.test.testdouble;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.simulatest.environment.annotation.UseEnvironment;
import org.simulatest.environment.junit5.test.testdouble.environment.FirstLevelEnvironment;

@UseEnvironment(FirstLevelEnvironment.class)
class PackagePrivateTest {

	@Test
	void shouldBeDiscoveredDespitePackagePrivateVisibility() {
		Assertions.assertTrue(EnvironmentTracker.getEvents().contains("FirstLevel"));
	}

}
