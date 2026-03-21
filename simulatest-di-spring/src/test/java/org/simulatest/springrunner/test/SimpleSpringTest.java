package org.simulatest.springrunner.test;

import java.util.ArrayList;
import java.util.List;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.simulatest.environment.Environment;
import org.simulatest.environment.annotation.EnvironmentParent;
import org.simulatest.environment.annotation.UseEnvironment;
import org.simulatest.insistencelayer.InsistenceLayerFactory;
import org.simulatest.environment.junit.EnvironmentJUnitRunner;
import org.simulatest.springrunner.spring.SimulatestSpringConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.junit.Assert.assertEquals;

@RunWith(EnvironmentJUnitRunner.class)
@SimulatestSpringConfig(SimpleSpringTest.Config.class)
@UseEnvironment(SimpleSpringTest.ChildEnv.class)
public class SimpleSpringTest {

	static {
		JdbcDataSource h2 = new JdbcDataSource();
		h2.setURL("jdbc:h2:mem:springtest;DB_CLOSE_DELAY=-1");
		h2.setUser("sa");
		InsistenceLayerFactory.configure(h2);
	}

	@Configuration
	static class Config {
		@Bean Greeter greeter() { return new Greeter(); }
		@Bean MessageLog messageLog() { return new MessageLog(); }
	}

	public static class Greeter {
		public String greet() { return "Hello"; }
	}

	public static class MessageLog {
		private final List<String> messages = new ArrayList<>();
		public void add(String msg) { messages.add(msg); }
		public List<String> all() { return List.copyOf(messages); }
	}

	public static class ParentEnv implements Environment {
		@Autowired Greeter greeter;
		@Autowired MessageLog log;
		@Override public void run() { log.add(greeter.greet()); }
	}

	@EnvironmentParent(ParentEnv.class)
	public static class ChildEnv implements Environment {
		@Autowired Greeter greeter;
		@Autowired MessageLog log;
		@Override public void run() { log.add(greeter.greet() + " by child"); }
	}

	@Autowired private Greeter greeter;
	@Autowired private MessageLog log;

	@Test
	public void simpleSpringDITest() {
		assertEquals("Hello", greeter.greet());
	}

	@Test
	public void environmentsTest() {
		assertEquals(List.of("Hello", "Hello by child"), log.all());
	}

}
