package org.simulatest.di.spring.testdouble;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.simulatest.environment.Environment;
import org.springframework.beans.factory.annotation.Autowired;

public class SeedEnvironment implements Environment {

	@Autowired
	private DataSource dataSource;

	@Override
	public void run() {
		try (Connection connection = dataSource.getConnection()) {
			SeedTable.insertRow(connection);
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
	}

}
