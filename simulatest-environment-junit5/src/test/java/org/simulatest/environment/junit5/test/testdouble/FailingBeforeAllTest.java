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
	void shouldNeverRun() {
	}

}
