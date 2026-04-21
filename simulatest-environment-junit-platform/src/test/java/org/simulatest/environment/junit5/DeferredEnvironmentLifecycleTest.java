package org.simulatest.environment.junit5;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.simulatest.environment.Environment;
import org.simulatest.environment.EnvironmentDefinition;
import org.simulatest.environment.plugin.EnvironmentExecution;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DeferredEnvironmentLifecycleTest {

	@BeforeEach
	void startFresh() {
		DeferredEnvironmentCoordinator.reset();
	}

	@Test
	void onEnterDoesNothingSoEngineLeavesRunAndPushToTheExtension() {
		RecordingExecution execution = new RecordingExecution();
		EnvironmentDefinition definition = EnvironmentDefinition.create(Fake.class);

		new DeferredEnvironmentLifecycle().onEnter(definition, execution);

		assertTrue(execution.events.isEmpty(),
			"deferred entry must not run the env nor push a level; that work is done later "
			+ "by the plugin's Jupiter extension, after its DI container is up");
	}

	@Test
	void onExitPopsLevelAndForgetsCoordinatorRecord() {
		DeferredEnvironmentCoordinator.claimNotYetRun(Fake.class);
		RecordingExecution execution = new RecordingExecution();
		EnvironmentDefinition definition = EnvironmentDefinition.create(Fake.class);

		new DeferredEnvironmentLifecycle().onExit(definition, execution);

		assertEquals(List.of("pop"), execution.events);
		assertTrue(DeferredEnvironmentCoordinator.claimNotYetRun(Fake.class),
			"after exit, the coordinator must allow a fresh claim on the same env");
	}

	static class Fake implements Environment {
		@Override public void run() {}
	}

	static final class RecordingExecution implements EnvironmentExecution {
		final List<String> events = new ArrayList<>();

		@Override public void runEnvironment(EnvironmentDefinition d) { events.add("run:" + d.getName()); }
		@Override public void increaseInsistenceLevel() { events.add("push"); }
		@Override public void decreaseInsistenceLevel() { events.add("pop"); }
	}

}
