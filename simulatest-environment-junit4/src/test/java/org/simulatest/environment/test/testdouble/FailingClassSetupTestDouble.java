package org.simulatest.environment.test.testdouble;

import java.sql.SQLException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.simulatest.environment.annotation.UseEnvironment;
import org.simulatest.environment.test.TestSetup;

// The class setup writes a row and then fails, so no test of the class runs.
//
// The name does not end in Test on purpose: Surefire would then run the class
// on its own, without its environment.
@SuppressWarnings("java:S3577")
@UseEnvironment(InsistenceTestEnvironment.class)
public class FailingClassSetupTestDouble {

	static { TestSetup.configure(); }

	@BeforeClass
	public static void addClassRowAndFail() throws SQLException {
		IsolationRows.insert(50, "class-data");
		throw new IllegalStateException("class setup failure");
	}

	// Has no assertion on purpose: the class setup above always throws, so this
	// method only gives JUnit a test to find.
	@Test
	@SuppressWarnings("java:S2699")
	public void shouldNeverRun() {
		throw new UnsupportedOperationException("Should never be reached: the class setup fails first");
	}

}
