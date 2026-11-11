package org.simulatest.environment.junit5.test.testdouble;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.simulatest.environment.annotation.UseEnvironment;
import org.simulatest.environment.junit5.test.testdouble.environment.ParentRowEnvironment;

/**
 * Test double: the class adds one row of its own before its tests. Every
 * test should see it, next to the parent row, and not the row of the test
 * that ran before.
 */
@UseEnvironment(ParentRowEnvironment.class)
@TestMethodOrder(MethodOrderer.MethodName.class)
public class ClassSetupRowTest {

	@BeforeAll
	static void addClassRow() {
		EnvRows.insert(10, "class");
	}

	@Test
	void aShouldSeeTheParentRowAndTheClassRow() {
		assertEquals(2, EnvRows.count());
		EnvRows.insert(20, "test");
	}

	@Test
	void bShouldSeeTheParentRowAndTheClassRow() {
		assertEquals(2, EnvRows.count());
		EnvRows.insert(20, "test");
	}

}
