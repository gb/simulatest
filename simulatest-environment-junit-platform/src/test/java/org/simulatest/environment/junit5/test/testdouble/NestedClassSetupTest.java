package org.simulatest.environment.junit5.test.testdouble;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;
import org.simulatest.environment.annotation.UseEnvironment;
import org.simulatest.environment.junit5.test.testdouble.environment.ParentRowEnvironment;

/**
 * Test double: the outer class adds a row and has no test of its own. The
 * first nested class adds one more row; the second adds none.
 */
@UseEnvironment(ParentRowEnvironment.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class NestedClassSetupTest {

	@BeforeAll
	static void addOuterRow() {
		EnvRows.insert(10, "outer");
	}

	@Nested
	@Order(1)
	@TestMethodOrder(MethodOrderer.MethodName.class)
	class WithItsOwnRow {

		@BeforeAll
		static void addNestedRow() {
			EnvRows.insert(11, "nested");
		}

		@Test
		void aShouldSeeTheParentOuterAndNestedRows() {
			assertEquals(3, EnvRows.count());
			EnvRows.insert(20, "test");
		}

		@Test
		void bShouldSeeTheParentOuterAndNestedRows() {
			assertEquals(3, EnvRows.count());
			EnvRows.insert(20, "test");
		}

	}

	@Nested
	@Order(2)
	class WithoutItsOwnRow {

		@Test
		void shouldSeeTheParentAndOuterRowsOnly() {
			assertEquals(2, EnvRows.count());
		}

	}

}
