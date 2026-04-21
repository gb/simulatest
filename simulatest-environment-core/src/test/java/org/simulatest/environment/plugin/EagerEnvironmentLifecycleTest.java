package org.simulatest.environment.plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.simulatest.environment.Environment;
import org.simulatest.environment.EnvironmentDefinition;

import static org.junit.Assert.assertEquals;

public class EagerEnvironmentLifecycleTest {

	@Test
	public void onEnterRunsEnvironmentThenPushesLevel() {
		RecordingExecution execution = new RecordingExecution();
		EnvironmentDefinition definition = EnvironmentDefinition.create(Fake.class);

		new EagerEnvironmentLifecycle().onEnter(definition, execution);

		// Eager lifecycle must run env BEFORE pushing the level so test savepoints
		// only roll back test mutations, not the env's seed writes.
		assertEquals(Arrays.asList("run:Fake", "push"), execution.events);
	}

	@Test
	public void onExitPopsLevel() {
		RecordingExecution execution = new RecordingExecution();
		EnvironmentDefinition definition = EnvironmentDefinition.create(Fake.class);

		new EagerEnvironmentLifecycle().onExit(definition, execution);

		assertEquals(Arrays.asList("pop"), execution.events);
	}

	public static class Fake implements Environment {
		@Override public void run() {}
	}

	static final class RecordingExecution implements EnvironmentExecution {
		final List<String> events = new ArrayList<>();

		@Override public void runEnvironment(EnvironmentDefinition d) { events.add("run:" + d.getName()); }
		@Override public void increaseInsistenceLevel() { events.add("push"); }
		@Override public void decreaseInsistenceLevel() { events.add("pop"); }
	}

}
