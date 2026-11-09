package org.simulatest.environment.junit5.test.testdouble;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.simulatest.environment.annotation.UseEnvironment;
import org.simulatest.environment.junit5.test.testdouble.environment.ChildRowEnvironment;
import org.simulatest.environment.junit5.test.testdouble.environment.ParentRowEnvironment;

/**
 * Test double: the outer class uses the parent environment and a
 * {@code @Nested} class uses the child environment.
 */
@UseEnvironment(ParentRowEnvironment.class)
public class NestedOwnEnvironmentTest {

	@Test
	void outerShouldSeeOnlyTheParentRow() {
		assertEquals(1, EnvRows.count(), "outer test should see only the parent row");
	}

	@Nested
	@UseEnvironment(ChildRowEnvironment.class)
	class WithChildEnvironment {

		@Test
		void nestedShouldSeeParentAndChildRows() {
			assertEquals(2, EnvRows.count(), "nested test should see the parent row and the child row");
		}

	}

}
