package org.simulatest.environment.junit5.test.testdouble;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.simulatest.environment.annotation.UseEnvironment;
import org.simulatest.environment.junit5.test.testdouble.environment.FirstLevelEnvironment;

@UseEnvironment(FirstLevelEnvironment.class)
public class FailingBeforeAllTest {

	@BeforeAll
	static void explode() {
		throw new RuntimeException("BeforeAll failure");
	}

	@Test
	@SuppressWarnings("java:S2699")
	void shouldNeverRun() {
		// This method is intentionally empty. The @BeforeAll above always throws,
		// so Jupiter never reaches this test. It exists only to give Jupiter a
		// discoverable test method — the real assertion is in
		// SimulatestTestEngineTest.containerFailureShouldBeReportedAsFailed().
		throw new UnsupportedOperationException("Should never be reached — @BeforeAll fails first");
	}

}
