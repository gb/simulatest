package org.simulatest.di.spring;

import org.junit.jupiter.api.Test;
import org.simulatest.environment.Environment;
import org.simulatest.environment.annotation.EnvironmentParent;
import org.simulatest.environment.annotation.UseEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@UseEnvironment(SimpleSpringJUnit5IT.ChildEnv.class)
class SimpleSpringJUnit5IT {

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
	void springDIShouldWork() {
		assertEquals("Hello", greeter.greet());
	}

	@Test
	void environmentsShouldHaveRun() {
		assertEquals(List.of("Hello", "Hello by child"), log.all());
	}

}
