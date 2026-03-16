package org.simulatest.guice.test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.Test;
import org.simulatest.guice.GuiceContext;
import org.simulatest.guice.SimulatestGuiceConfig;

import com.google.inject.AbstractModule;

class GuiceContextTest {

	@Test
	void dataSourceShouldReturnBindingWhenPresent() {
		GuiceContext context = new GuiceContext();
		context.initialize(List.of(TestWithDataSource.class));

		assertNotNull(context.dataSource());

		context.destroy();
	}

	@Test
	void dataSourceShouldReturnNullWhenAbsent() {
		GuiceContext context = new GuiceContext();
		context.initialize(List.of(TestWithoutDataSource.class));

		assertNull(context.dataSource());

		context.destroy();
	}

	@SimulatestGuiceConfig(DataSourceModule.class)
	private static class TestWithDataSource {}

	@SimulatestGuiceConfig(EmptyModule.class)
	private static class TestWithoutDataSource {}

	public static class DataSourceModule extends AbstractModule {
		@Override
		protected void configure() {
			JdbcDataSource ds = new JdbcDataSource();
			ds.setURL("jdbc:h2:mem:guice-context-test;DB_CLOSE_DELAY=-1");
			ds.setUser("sa");
			bind(DataSource.class).toInstance(ds);
		}
	}

	public static class EmptyModule extends AbstractModule {
		@Override
		protected void configure() { /* no bindings: used to test the absent-DataSource path */ }
	}

}
