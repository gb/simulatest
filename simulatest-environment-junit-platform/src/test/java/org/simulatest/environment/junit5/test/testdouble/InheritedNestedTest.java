package org.simulatest.environment.junit5.test.testdouble;

import org.simulatest.environment.annotation.UseEnvironment;
import org.simulatest.environment.junit5.test.testdouble.environment.ChildRowEnvironment;

/**
 * Test double: inherits the {@code @Nested} class of {@link RowBaseTest} and
 * runs it under the child environment, which adds a second row.
 */
@UseEnvironment(ChildRowEnvironment.class)
public class InheritedNestedTest extends RowBaseTest {

	@Override
	protected int expectedRows() {
		return 2;
	}

}
