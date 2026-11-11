package org.simulatest.environment.junit5.test.testdouble;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.simulatest.environment.annotation.UseEnvironment;
import org.simulatest.environment.junit5.test.testdouble.environment.FirstLevelEnvironment;

/**
 * Test double: each test writes a line in the tracker while it runs, so a
 * listener that writes in the same tracker shows when the test was reported.
 */
@UseEnvironment(FirstLevelEnvironment.class)
@TestMethodOrder(MethodOrderer.MethodName.class)
public class LoggedTest {

	@Test
	void aFirst() {
		EnvironmentTracker.trackEvent("body of aFirst");
	}

	@Test
	void bSecond() {
		EnvironmentTracker.trackEvent("body of bSecond");
	}

}
