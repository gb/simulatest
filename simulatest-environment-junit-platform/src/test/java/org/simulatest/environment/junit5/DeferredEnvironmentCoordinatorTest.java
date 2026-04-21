package org.simulatest.environment.junit5;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.simulatest.environment.Environment;
import org.simulatest.environment.annotation.EnvironmentParent;
import org.simulatest.environment.annotation.UseEnvironment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
	static class UsesLeaf { }

}
