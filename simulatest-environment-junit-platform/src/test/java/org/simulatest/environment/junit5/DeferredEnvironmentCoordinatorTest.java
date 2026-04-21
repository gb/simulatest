package org.simulatest.environment.junit5;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.simulatest.environment.Environment;
import org.simulatest.environment.annotation.EnvironmentParent;
import org.simulatest.environment.annotation.UseEnvironment;

import org.simulatest.environment.junit5.testdouble.CyclicEnvironmentFixtures;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DeferredEnvironmentCoordinatorTest {

	@BeforeEach
	void startFresh() {
		DeferredEnvironmentCoordinator.reset();
	}

	// =========================================================================
	// Ancestry walking via @UseEnvironment + @EnvironmentParent
	// =========================================================================

	@Test
	void ancestryIsEmptyWhenClassHasNoUseEnvironment() {
		assertTrue(DeferredEnvironmentCoordinator.ancestryOf(NoAnnotation.class).isEmpty());
	}

	@Test
	void ancestryForLeafWithoutParentIsTheLeafAlone() {
		List<Class<? extends Environment>> ancestry =
			DeferredEnvironmentCoordinator.ancestryOf(UsesRoot.class);

		assertEquals(List.of(Root.class), ancestry);
	}

	@Test
	void ancestryIsOrderedRootFirst() {
		List<Class<? extends Environment>> ancestry =
			DeferredEnvironmentCoordinator.ancestryOf(UsesLeaf.class);

		assertEquals(List.of(Root.class, Middle.class, Leaf.class), ancestry,
			"root-first ordering lets callers push savepoints outermost-to-innermost");
	}

	@Test
	void cyclicParentChainThrowsRatherThanLooping() {
		IllegalStateException error = assertThrows(IllegalStateException.class,
			() -> DeferredEnvironmentCoordinator.walkParentChain(CyclicEnvironmentFixtures.NodeA.class));

		assertTrue(error.getMessage().contains("Cyclic @EnvironmentParent"),
			"error should name the cycle condition: " + error.getMessage());
	}

	@Test
	void nestedInnerClassInheritsOuterUseEnvironment() {
		List<Class<? extends Environment>> ancestry =
			DeferredEnvironmentCoordinator.ancestryOf(UsesLeaf.NestedInner.class);

		assertEquals(List.of(Root.class, Middle.class, Leaf.class), ancestry,
			"@Nested inner classes must pick up the outer class's environment");
	}

	// =========================================================================
	// Claim / forget / reset semantics
	// =========================================================================

	@Test
	void firstClaimWinsSubsequentClaimsLose() {
		assertTrue(DeferredEnvironmentCoordinator.claimNotYetRun(Root.class));
		assertFalse(DeferredEnvironmentCoordinator.claimNotYetRun(Root.class));
		assertFalse(DeferredEnvironmentCoordinator.claimNotYetRun(Root.class));
	}

	@Test
	void forgetAllowsReclaim() {
		DeferredEnvironmentCoordinator.claimNotYetRun(Root.class);
		DeferredEnvironmentCoordinator.forget(Root.class);

		assertTrue(DeferredEnvironmentCoordinator.claimNotYetRun(Root.class),
			"after exit, a re-entered subtree must be able to claim again");
	}

	@Test
	void resetClearsEverything() {
		DeferredEnvironmentCoordinator.claimNotYetRun(Root.class);
		DeferredEnvironmentCoordinator.claimNotYetRun(Middle.class);
		DeferredEnvironmentCoordinator.claimNotYetRun(Leaf.class);

		DeferredEnvironmentCoordinator.reset();

		assertTrue(DeferredEnvironmentCoordinator.claimNotYetRun(Root.class));
		assertTrue(DeferredEnvironmentCoordinator.claimNotYetRun(Middle.class));
		assertTrue(DeferredEnvironmentCoordinator.claimNotYetRun(Leaf.class));
	}

	@Test
	void wasPushedIsFalseUntilRecorded() {
		DeferredEnvironmentCoordinator.claimNotYetRun(Root.class);

		assertFalse(DeferredEnvironmentCoordinator.wasPushed(Root.class),
			"a bare claim without a recorded push must not signal wasPushed");
	}

	@Test
	void wasPushedFlipsToTrueAfterRecord() {
		DeferredEnvironmentCoordinator.claimNotYetRun(Root.class);
		DeferredEnvironmentCoordinator.recordPush(Root.class);

		assertTrue(DeferredEnvironmentCoordinator.wasPushed(Root.class));
	}

	@Test
	void forgetClearsBothClaimAndPush() {
		DeferredEnvironmentCoordinator.claimNotYetRun(Root.class);
		DeferredEnvironmentCoordinator.recordPush(Root.class);

		DeferredEnvironmentCoordinator.forget(Root.class);

		assertFalse(DeferredEnvironmentCoordinator.wasPushed(Root.class));
		assertTrue(DeferredEnvironmentCoordinator.claimNotYetRun(Root.class));
	}

	// =========================================================================
	// Test fixtures
	// =========================================================================

	static class Root implements Environment {
		@Override public void run() {}
	}

	@EnvironmentParent(Root.class)
	static class Middle implements Environment {
		@Override public void run() {}
	}

	@EnvironmentParent(Middle.class)
	static class Leaf implements Environment {
		@Override public void run() {}
	}

	static class NoAnnotation { }

	@UseEnvironment(Root.class)
	static class UsesRoot { }

	@UseEnvironment(Leaf.class)
	static class UsesLeaf {
		static class NestedInner { }
	}

}
