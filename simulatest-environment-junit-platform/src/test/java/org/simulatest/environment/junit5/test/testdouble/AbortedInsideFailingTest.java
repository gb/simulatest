package org.simulatest.environment.junit5.test.testdouble;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.simulatest.environment.annotation.UseEnvironment;
import org.simulatest.environment.junit5.test.testdouble.environment.FirstLevelEnvironment;

/**
 * Test double: a nested class is aborted before its tests, and then the
 * teardown of the outer class fails. The abort comes first; the failure is
 * the one that must not be lost.
 */
@UseEnvironment(FirstLevelEnvironment.class)
public class AbortedInsideFailingTest {

	@AfterAll
	static void failTheTeardown() {
		throw new IllegalStateException("teardown failure");
	}

	@Test
	void outerTest() {
		EnvironmentTracker.trackEvent("outerTest");
	}

	@Nested
	class Aborted {

		@BeforeAll
		static void abort() {
			Assumptions.abort("not today");
		}

		@Test
		void neverRuns() {
			EnvironmentTracker.trackEvent("neverRuns");
		}

	}

}
