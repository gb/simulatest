package org.simulatest.di.jee;

import java.util.ArrayList;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.simulatest.environment.Environment;
import org.simulatest.environment.annotation.EnvironmentParent;
import org.simulatest.environment.annotation.UseEnvironment;
import org.simulatest.insistencelayer.InsistenceLayerFactory;
import org.simulatest.environment.junit.EnvironmentJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(EnvironmentJUnitRunner.class)
@UseEnvironment(SimpleJakartaTest.ChildEnv.class)
public class SimpleJakartaTest {

	static {
		JdbcDataSource h2 = new JdbcDataSource();
		h2.setURL("jdbc:h2:mem:jeetest;DB_CLOSE_DELAY=-1");
		h2.setUser("sa");
		InsistenceLayerFactory.configure(h2);
	}

	@ApplicationScoped
	public static class Greeter {
		public String greet() { return "Hello"; }
	}

	@ApplicationScoped
	public static class MessageLog {
		private final List<String> messages = new ArrayList<>();
		public void add(String msg) { messages.add(msg); }
		public List<String> all() { return List.copyOf(messages); }
	}

	@Dependent
	public static class ParentEnv implements Environment {
		@Inject Greeter greeter;
		@Inject MessageLog log;
		@Override public void run() { log.add(greeter.greet()); }
	}

	@Dependent
	@EnvironmentParent(ParentEnv.class)
	public static class ChildEnv implements Environment {
		@Inject Greeter greeter;
		@Inject MessageLog log;
		@Override public void run() { log.add(greeter.greet() + " by child"); }
	}

	@Inject private Greeter greeter;
	@Inject private MessageLog log;

	@Test
	public void testCdiLookup() {
		assertEquals("Hello", greeter.greet());
	}

	@Test
	public void testEnvironments() {
		assertEquals(List.of("Hello", "Hello by child"), log.all());
	}

}
