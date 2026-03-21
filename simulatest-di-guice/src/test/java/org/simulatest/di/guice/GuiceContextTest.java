package org.simulatest.di.guice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.Test;
import org.simulatest.di.guice.GuiceContext;
import org.simulatest.di.guice.GuiceModuleProvider;
import org.simulatest.di.guice.SimulatestGuiceConfig;

import com.google.inject.AbstractModule;
import com.google.inject.Module;

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

	@Test
	void moduleProviderShouldSupplyParameterizedModules() {
		GuiceContext context = new GuiceContext();
		context.initialize(List.of(TestWithProvider.class));

		assertNotNull(context.getInstance(Greeting.class));
		assertEquals("hello", context.getInstance(Greeting.class).value());

		context.destroy();
	}

	@Test
	void valueAndProvidersShouldCombine() {
		GuiceContext context = new GuiceContext();
		context.initialize(List.of(TestWithValueAndProvider.class));

		assertNotNull(context.dataSource());
		assertEquals("hello", context.getInstance(Greeting.class).value());

		context.destroy();
	}

	@SimulatestGuiceConfig(providers = GreetingModuleProvider.class)
	private static class TestWithProvider {}

	@SimulatestGuiceConfig(value = DataSourceModule.class, providers = GreetingModuleProvider.class)
	private static class TestWithValueAndProvider {}

	public record Greeting(String value) {}

	public static class ParameterizedModule extends AbstractModule {
		private final String greeting;

		public ParameterizedModule(String greeting) {
			this.greeting = greeting;
		}

		@Override
		protected void configure() {
			bind(Greeting.class).toInstance(new Greeting(greeting));
		}
	}

	public static class GreetingModuleProvider implements GuiceModuleProvider {
		@Override
		public Module[] modules() {
			return new Module[] { new ParameterizedModule("hello") };
		}
	}

}
