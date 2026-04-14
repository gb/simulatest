package org.simulatest.environment.junit5.descriptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.platform.engine.support.descriptor.EngineDescriptor;
import org.simulatest.environment.Environment;
import org.simulatest.environment.EnvironmentDefinition;
import org.simulatest.environment.EnvironmentFactory;
import org.simulatest.environment.annotation.EnvironmentParent;
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
	void afterShouldOnlyDecreaseWhenDescriptorHasNoParent() {
		TrackingInsistenceLayer insistenceLayer = new TrackingInsistenceLayer();
		DescriptorFixture fixture = DescriptorFixture.with(new TrackingEnvironment(), insistenceLayer);

		fixture.descriptor.after(fixture.context);

		assertEquals(List.of("decrease"), insistenceLayer.operations(),
				"A root environment should only unwind its own level");
	}

	@Test
	void afterShouldResetParentLevelWhenAnotherEnvironmentSiblingFollows() {
		TrackingInsistenceLayer insistenceLayer = new TrackingInsistenceLayer();
		DescriptorFixture fixture = DescriptorFixture.with(new TrackingEnvironment(), insistenceLayer);
		fixture.attachToParent();
		fixture.addEnvironmentSibling(ChildEnvironment.class);

		fixture.descriptor.after(fixture.context);

		assertEquals(List.of("decrease", "reset"), insistenceLayer.operations(),
				"A non-last environment should reset the parent level for the next sibling subtree");
	}

	@Test
	void afterShouldSkipResetWhenOnlyNonEnvironmentSiblingsFollow() {
		TrackingInsistenceLayer insistenceLayer = new TrackingInsistenceLayer();
		DescriptorFixture fixture = DescriptorFixture.with(new TrackingEnvironment(), insistenceLayer);
		fixture.attachToParent();
		fixture.addNonEnvironmentSibling("test-class");

		fixture.descriptor.after(fixture.context);

		assertEquals(List.of("decrease"), insistenceLayer.operations(),
				"Non-environment siblings should not trigger a parent-level reset");
	}

	@Test
	void afterShouldIgnoreEnvironmentSiblingsThatAppearBeforeCurrentDescriptor() {
		TrackingInsistenceLayer insistenceLayer = new TrackingInsistenceLayer();
		DescriptorFixture fixture = DescriptorFixture.with(new TrackingEnvironment(), insistenceLayer);
		fixture.addEnvironmentSibling(ChildEnvironment.class);
		fixture.attachToParent();

		fixture.descriptor.after(fixture.context);

		assertEquals(List.of("decrease"), insistenceLayer.operations(),
				"Only environment siblings after the current descriptor should trigger a parent-level reset");
	}

	@Test
	void afterShouldResetWhenEnvironmentSiblingAppearsAfterNonEnvironmentSibling() {
		TrackingInsistenceLayer insistenceLayer = new TrackingInsistenceLayer();
		DescriptorFixture fixture = DescriptorFixture.with(new TrackingEnvironment(), insistenceLayer);
		fixture.attachToParent();
		fixture.addNonEnvironmentSibling("test-class");
		fixture.addEnvironmentSibling(ChildEnvironment.class);

		fixture.descriptor.after(fixture.context);

		assertEquals(List.of("decrease", "reset"), insistenceLayer.operations(),
				"The descriptor should scan past non-environment siblings until it finds the next environment subtree");
	}

	private static final class DescriptorFixture {

		private static final UniqueId ENGINE_ID = UniqueId.forEngine("simulatest");

		private final EnvironmentTestDescriptor descriptor;
		private final SimulatestExecutionContext context;
		private final TrackingInsistenceLayer insistenceLayer;
		private final EngineDescriptor parent = new EngineDescriptor(ENGINE_ID, "Simulatest");

		private DescriptorFixture(EnvironmentTestDescriptor descriptor, SimulatestExecutionContext context,
				TrackingInsistenceLayer insistenceLayer) {
			this.descriptor = descriptor;
			this.context = context;
			this.insistenceLayer = insistenceLayer;
		}

		static DescriptorFixture with(Environment environment, TrackingInsistenceLayer insistenceLayer) {
			EnvironmentDefinition definition = EnvironmentDefinition.create(ParentEnvironment.class);
			EnvironmentTestDescriptor descriptor = new EnvironmentTestDescriptor(
					ENGINE_ID.append("environment", ParentEnvironment.class.getName()),
					definition);
			SimulatestExecutionContext context = contextFor(environment, definition, insistenceLayer);
			return new DescriptorFixture(descriptor, context, insistenceLayer);
		}

		void attachToParent() {
			parent.addChild(descriptor);
		}

		void addEnvironmentSibling(Class<? extends Environment> environmentClass) {
			parent.addChild(newEnvironmentDescriptor(environmentClass));
		}

		private EnvironmentTestDescriptor newEnvironmentDescriptor(Class<? extends Environment> environmentClass) {
			return new EnvironmentTestDescriptor(
					parent.getUniqueId().append("environment", environmentClass.getName()),
					EnvironmentDefinition.create(environmentClass));
		}

		void addNonEnvironmentSibling(String segment) {
			parent.addChild(new DummyDescriptor(parent.getUniqueId().append("class", segment), segment));
		}

		private static SimulatestExecutionContext contextFor(Environment environment,
				EnvironmentDefinition definition, InsistenceLayer insistenceLayer) {
			EnvironmentFactory factory = requestedDefinition -> {
				assertSame(definition, requestedDefinition,
						"The descriptor should request the environment instance for its own definition");
				return environment;
			};
			return new SimulatestExecutionContext(null, factory, insistenceLayer, List.of());
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

	private static final class DummyDescriptor extends AbstractTestDescriptor {

		private DummyDescriptor(UniqueId uniqueId, String displayName) {
			super(uniqueId, displayName);
		}

		@Override
		public Type getType() {
			return Type.CONTAINER;
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

	@EnvironmentParent(ParentEnvironment.class)
	private static class ChildEnvironment implements Environment {
		@Override
		public void run() { }
	}

}
