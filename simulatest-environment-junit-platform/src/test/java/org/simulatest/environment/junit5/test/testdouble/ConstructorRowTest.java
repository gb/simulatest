package org.simulatest.environment.junit5.test.testdouble;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.simulatest.environment.annotation.UseEnvironment;
import org.simulatest.environment.junit5.test.testdouble.environment.ParentRowEnvironment;

/**
 * Test double: every test instance adds the same row when it is built. Each
 * test gets a new instance, so the row of one test must be gone before the
 * instance of the next test is built.
 */
@UseEnvironment(ParentRowEnvironment.class)
@TestMethodOrder(MethodOrderer.MethodName.class)
public class ConstructorRowTest {

	ConstructorRowTest() {
		EnvRows.insert(30, "instance");
	}

	@Test
	void aShouldSeeTheParentRowAndTheRowOfItsInstance() {
		assertEquals(2, EnvRows.count(), "first instance");
	}

	@Test
	void bShouldSeeTheParentRowAndTheRowOfItsInstance() {
		assertEquals(2, EnvRows.count(), "second instance");
	}

}
