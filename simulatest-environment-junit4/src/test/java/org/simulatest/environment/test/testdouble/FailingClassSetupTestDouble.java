package org.simulatest.environment.test.testdouble;

import java.sql.SQLException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.simulatest.environment.annotation.UseEnvironment;
import org.simulatest.environment.test.TestSetup;

// The class setup writes a row and then fails, so no test of the class runs.
@UseEnvironment(InsistenceTestEnvironment.class)
public class FailingClassSetupTestDouble {

	static { TestSetup.configure(); }

	@BeforeClass
	public static void addClassRowAndFail() throws SQLException {
		IsolationRows.insert(50, "class-data");
		throw new IllegalStateException("class setup failure");
	}

	@Test
	public void shouldNeverRun() {
		throw new UnsupportedOperationException("Should never be reached: the class setup fails first");
	}

}
