package org.simulatest.environment.junit5.test.testdouble;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.simulatest.environment.annotation.UseEnvironment;
import org.simulatest.environment.junit5.test.testdouble.environment.FirstLevelEnvironment;

/**
 * Test double: one test with each tag, and one that is disabled.
 */
@UseEnvironment(FirstLevelEnvironment.class)
public class TaggedTest {

	@Test
	@Tag("fast")
	void fast() {
		EnvironmentTracker.trackEvent("fast");
	}

	@Test
	@Tag("slow")
	void slow() {
		EnvironmentTracker.trackEvent("slow");
	}

	@Test
	@Tag("slow")
	@Disabled("not ready")
	void disabled() {
		EnvironmentTracker.trackEvent("disabled");
	}

}
