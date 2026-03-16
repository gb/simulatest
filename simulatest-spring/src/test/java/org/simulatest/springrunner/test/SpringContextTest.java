package org.simulatest.springrunner.test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.Test;
import org.simulatest.springrunner.spring.SimulatestSpringConfig;
import org.simulatest.springrunner.spring.SpringContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

class SpringContextTest {

	@Test
	void dataSourceShouldReturnBeanWhenPresent() {
		SpringContext context = new SpringContext();
		context.initialize(List.of(TestWithDataSource.class));

		assertNotNull(context.dataSource());

		context.destroy();
	}

	@Test
	void dataSourceShouldReturnNullWhenAbsent() {
		SpringContext context = new SpringContext();
		context.initialize(List.of(TestWithoutDataSource.class));

		assertNull(context.dataSource());

		context.destroy();
	}

	@SimulatestSpringConfig(DataSourceConfig.class)
	private static class TestWithDataSource {}

	@SimulatestSpringConfig(EmptyConfig.class)
	private static class TestWithoutDataSource {}

	@Configuration
	static class DataSourceConfig {
		@Bean
		DataSource dataSource() {
			JdbcDataSource ds = new JdbcDataSource();
			ds.setURL("jdbc:h2:mem:spring-context-test;DB_CLOSE_DELAY=-1");
			ds.setUser("sa");
			return ds;
		}
	}

	@Configuration
	static class EmptyConfig {}

}
