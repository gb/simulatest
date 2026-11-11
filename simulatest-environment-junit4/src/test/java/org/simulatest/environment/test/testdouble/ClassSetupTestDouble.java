package org.simulatest.environment.test.testdouble;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.simulatest.environment.annotation.UseEnvironment;
import org.simulatest.environment.test.TestSetup;

// The class adds one row of its own before its tests. Every test should see it,
// next to the environment row, and not the row of the test that ran before.
@UseEnvironment(InsistenceTestEnvironment.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ClassSetupTestDouble {

	static { TestSetup.configure(); }

	@BeforeClass
	public static void addClassRow() throws SQLException {
		IsolationRows.insert(50, "class-data");
	}

	@Test
	public void aShouldSeeTheEnvironmentRowAndTheClassRow() throws SQLException {
		assertEquals(2, IsolationRows.count());
		IsolationRows.insert(100, "test-specific");
	}

	@Test
	public void bShouldSeeTheEnvironmentRowAndTheClassRow() throws SQLException {
		assertEquals(2, IsolationRows.count());
		IsolationRows.insert(100, "test-specific");
	}

}
