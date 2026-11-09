package org.simulatest.environment.junit5.test.testdouble;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.simulatest.environment.annotation.UseEnvironment;
import org.simulatest.environment.junit5.test.testdouble.environment.ParentRowEnvironment;

/**
 * Test double: an annotated base class with a {@code @Nested} class that has
 * no {@code @UseEnvironment} of its own. Subclasses inherit the nested class
 * and run it under their own environment.
 */
@UseEnvironment(ParentRowEnvironment.class)
public abstract class RowBaseTest {

	protected abstract int expectedRows();

	@Nested
	class InheritedNested {

		@Test
		void shouldSeeTheRowsOfTheRunningClass() {
			assertEquals(expectedRows(), EnvRows.count());
		}

	}

}
