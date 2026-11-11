package org.simulatest.environment.junit5.test.testdouble;

import org.junit.jupiter.api.Test;
import org.simulatest.environment.annotation.UseEnvironment;
import org.simulatest.environment.junit5.test.testdouble.environment.FailingRowEnvironment;

/**
 * Test double: its environment always fails, so its test never runs.
 */
@UseEnvironment(FailingRowEnvironment.class)
public class FailingEnvironmentTest {

	@Test
	void shouldNeverRun() {
		throw new UnsupportedOperationException("Should never be reached: the environment fails first");
	}

}
