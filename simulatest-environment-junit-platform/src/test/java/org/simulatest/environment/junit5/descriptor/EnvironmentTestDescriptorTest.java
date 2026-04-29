package org.simulatest.environment.junit5.descriptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.platform.engine.UniqueId;
import org.simulatest.environment.Environment;
import org.simulatest.environment.EnvironmentDefinition;
import org.simulatest.environment.EnvironmentFactory;
import org.simulatest.environment.infra.exception.EnvironmentExecutionException;
import org.simulatest.environment.junit5.SimulatestExecutionContext;
import org.simulatest.insistencelayer.InsistenceLayer;

class EnvironmentTestDescriptorTest {

	@Test
	void beforeShouldRunEnvironmentAndIncreaseLevelWhenInsistenceLayerExists() {
		TrackingEnvironment environment = new TrackingEnvironment();
		TrackingInsistenceLayer insistenceLayer = new TrackingInsistenceLayer();
		DescriptorFixture fixture = DescriptorFixture.with(environment, insistenceLayer);

		SimulatestExecutionContext returnedContext = fixture.descriptor.before(fixture.context);

		assertSame(fixture.context, returnedContext);
		assertEquals(1, environment.runCount, "The environment should run exactly once before children execute");
		assertEquals(List.of("increase"), insistenceLayer.operations(),
				"The environment boundary should open a new insistence level after setup");
	}

	@Test
	void beforeShouldWrapEnvironmentFailures() {
		RuntimeException failure = new IllegalStateException("boom");
		DescriptorFixture fixture = DescriptorFixture.with(new ThrowingEnvironment(failure), new TrackingInsistenceLayer());

		EnvironmentExecutionException exception = assertThrows(
				EnvironmentExecutionException.class,
				() -> fixture.descriptor.before(fixture.context));

		assertEquals("Failed during run for environment 'ParentEnvironment'", exception.getMessage());
		assertSame(failure, exception.getCause());
		assertEquals(List.of(), fixture.insistenceLayer.operations(),
				"The insistence level should not open when environment setup fails");
	}

	@Test
	void afterShouldOnlyDecreaseWhenDescriptorIsLastEnvironmentSibling() {
		TrackingInsistenceLayer insistenceLayer = new TrackingInsistenceLayer();
		DescriptorFixture fixture = DescriptorFixture.with(new TrackingEnvironment(), insistenceLayer);

		fixture.descriptor.after(fixture.context);

		assertEquals(List.of("decrease"), insistenceLayer.operations(),
				"The last environment sibling should only unwind its own level; the parent will roll past its savepoint");
	}

	@Test
	void afterShouldResetParentLevelWhenAnotherEnvironmentSiblingFollows() {
		TrackingInsistenceLayer insistenceLayer = new TrackingInsistenceLayer();
		DescriptorFixture fixture = DescriptorFixture.notLastSibling(new TrackingEnvironment(), insistenceLayer);

		fixture.descriptor.after(fixture.context);

		assertEquals(List.of("decrease", "reset"), insistenceLayer.operations(),
				"A non-last environment should reset the parent level for the next sibling subtree");
	}

	private static final class DescriptorFixture {

		private static final UniqueId ENGINE_ID = UniqueId.forEngine("simulatest");

		private final EnvironmentTestDescriptor descriptor;
		private final SimulatestExecutionContext context;
		private final TrackingInsistenceLayer insistenceLayer;

		private DescriptorFixture(EnvironmentTestDescriptor descriptor, SimulatestExecutionContext context,
				TrackingInsistenceLayer insistenceLayer) {
			this.descriptor = descriptor;
			this.context = context;
			this.insistenceLayer = insistenceLayer;
		}

		static DescriptorFixture with(Environment environment, TrackingInsistenceLayer insistenceLayer) {
			return build(environment, insistenceLayer, true);
		}

		static DescriptorFixture notLastSibling(Environment environment, TrackingInsistenceLayer insistenceLayer) {
			return build(environment, insistenceLayer, false);
		}

		private static DescriptorFixture build(Environment environment, TrackingInsistenceLayer insistenceLayer,
				boolean lastEnvironmentSibling) {
			EnvironmentDefinition definition = EnvironmentDefinition.create(ParentEnvironment.class);
			EnvironmentTestDescriptor descriptor = new EnvironmentTestDescriptor(
					ENGINE_ID.append("environment", ParentEnvironment.class.getName()),
					definition,
					lastEnvironmentSibling);
			SimulatestExecutionContext context = contextFor(environment, definition, insistenceLayer);
			return new DescriptorFixture(descriptor, context, insistenceLayer);
		}

		private static SimulatestExecutionContext contextFor(Environment environment,
				EnvironmentDefinition definition, InsistenceLayer insistenceLayer) {
			EnvironmentFactory factory = requestedDefinition -> {
				assertSame(definition, requestedDefinition,
						"The descriptor should request the environment instance for its own definition");
				return environment;
			};
			return new SimulatestExecutionContext(null, factory, insistenceLayer);
		}
	}

	private static final class TrackingInsistenceLayer implements InsistenceLayer {

		private final List<String> operations = new ArrayList<>();

		List<String> operations() {
			return List.copyOf(operations);
		}

		@Override
		public int getCurrentLevel() {
			return 0;
		}

		@Override
		public void increaseLevel() {
			operations.add("increase");
		}

		@Override
		public void decreaseLevel() {
			operations.add("decrease");
		}

		@Override
		public void resetCurrentLevel() {
			operations.add("reset");
		}

		@Override
		public void setLevelTo(int level) {
			operations.add("set:" + level);
		}
	}

	private static class TrackingEnvironment implements Environment {

		private int runCount;

		@Override
		public void run() {
			runCount++;
		}
	}

	private static final class ThrowingEnvironment implements Environment {

		private final RuntimeException failure;

		private ThrowingEnvironment(RuntimeException failure) {
			this.failure = failure;
		}

		@Override
		public void run() {
			throw failure;
		}
	}

	private static class ParentEnvironment implements Environment {
		@Override
		public void run() { }
	}

}
