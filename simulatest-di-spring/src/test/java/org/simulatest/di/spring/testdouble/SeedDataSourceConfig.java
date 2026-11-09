package org.simulatest.di.spring.testdouble;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SeedDataSourceConfig {

	public static final String URL = "jdbc:h2:mem:spring_cross_session;DB_CLOSE_DELAY=-1";

	@Bean
	public DataSource dataSource() {
		JdbcDataSource dataSource = new JdbcDataSource();
		dataSource.setURL(URL);
		return dataSource;
	}

}
