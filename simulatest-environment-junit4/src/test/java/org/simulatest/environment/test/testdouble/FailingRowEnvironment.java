package org.simulatest.environment.test.testdouble;

import java.sql.SQLException;

import org.simulatest.environment.Environment;
import org.simulatest.environment.infra.exception.EnvironmentExecutionException;

// Writes a row and then fails, like an environment that breaks halfway
// through its setup.
public class FailingRowEnvironment implements Environment {

	@Override
	public void run() {
		try {
			IsolationRows.insert(60, "half-built");
		} catch (SQLException e) {
			throw new EnvironmentExecutionException("Failed to insert the half-built row", e);
		}
		throw new IllegalStateException("environment failure");
	}

}
