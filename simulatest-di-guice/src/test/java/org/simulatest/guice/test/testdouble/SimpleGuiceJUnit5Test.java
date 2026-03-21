package org.simulatest.guice.test.testdouble;

import java.util.ArrayList;
import java.util.List;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;
import org.simulatest.environment.Environment;
import org.simulatest.environment.annotation.EnvironmentParent;
import org.simulatest.environment.annotation.UseEnvironment;
import org.simulatest.guice.SimulatestGuiceConfig;

import com.google.inject.AbstractModule;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SimulatestGuiceConfig(SimpleGuiceJUnit5Test.TestModule.class)
@UseEnvironment(SimpleGuiceJUnit5Test.ChildEnv.class)
class SimpleGuiceJUnit5Test {

	public static class Greeter {
		public String greet() { return "Hello"; }
	}

	public static class MessageLog {
		private final List<String> messages = new ArrayList<>();
		public void add(String msg) { messages.add(msg); }
		public List<String> all() { return List.copyOf(messages); }
	}

	public static class TestModule extends AbstractModule {
		@Override
		protected void configure() {
			bind(MessageLog.class).asEagerSingleton();
		}
	}

	public static class ParentEnv implements Environment {
		@Inject Greeter greeter;
		@Inject MessageLog log;
		@Override public void run() { log.add(greeter.greet()); }
	}

	@EnvironmentParent(ParentEnv.class)
	public static class ChildEnv implements Environment {
		@Inject Greeter greeter;
		@Inject MessageLog log;
		@Override public void run() { log.add(greeter.greet() + " by child"); }
	}

	@Inject private Greeter greeter;
	@Inject private MessageLog log;

	@Test
	void guiceDIShouldWork() {
		assertEquals("Hello", greeter.greet());
	}

	@Test
	void environmentsShouldHaveRun() {
		assertEquals(List.of("Hello", "Hello by child"), log.all());
	}

}
