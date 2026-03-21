package org.simulatest.di.jee;

import java.util.ArrayList;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;
import org.simulatest.environment.Environment;
import org.simulatest.environment.annotation.EnvironmentParent;
import org.simulatest.environment.annotation.UseEnvironment;

import static org.junit.jupiter.api.Assertions.assertEquals;

@UseEnvironment(SimpleJakartaJUnit5Test.ChildEnv.class)
public class SimpleJakartaJUnit5Test {

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
	void cdiLookupShouldWork() {
		assertEquals("Hello", greeter.greet());
	}

	@Test
	void environmentsShouldHaveRun() {
		assertEquals(List.of("Hello", "Hello by child"), log.all());
	}

}
