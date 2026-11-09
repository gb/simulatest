package org.simulatest.di.spring.testdouble;

import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.simulatest.di.spring.SimulatestSpringConfig;
import org.simulatest.environment.annotation.UseEnvironment;
import org.simulatest.environment.junit.EnvironmentJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;

@RunWith(EnvironmentJUnitRunner.class)
@UseEnvironment(SeedEnvironment.class)
@SimulatestSpringConfig(SeedDataSourceConfig.class)
public class SeedSuite {

	@Autowired
	private DataSource dataSource;

	@Test
	public void seesOneSeedRow() throws SQLException {
		try (Connection connection = dataSource.getConnection()) {
			assertEquals(1, SeedTable.count(connection));
		}
	}

}
