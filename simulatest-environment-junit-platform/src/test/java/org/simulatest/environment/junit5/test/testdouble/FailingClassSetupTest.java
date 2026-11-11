package org.simulatest.environment.junit5.test.testdouble;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.simulatest.environment.annotation.UseEnvironment;
import org.simulatest.environment.junit5.test.testdouble.environment.ParentRowEnvironment;

/**
 * Test double: the class setup writes a row and then fails, so no test of
 * the class runs.
 */
@UseEnvironment(ParentRowEnvironment.class)
public class FailingClassSetupTest {

	@BeforeAll
	static void addClassRowAndFail() {
		EnvRows.insert(10, "class");
		throw new IllegalStateException("class setup failure");
	}

	// Has no assertion on purpose: the class setup above always throws, so this
	// method only gives Jupiter a test to discover.
	@Test
	@SuppressWarnings("java:S2699")
	void shouldNeverRun() {
		throw new UnsupportedOperationException("Should never be reached: the class setup fails first");
	}

}
