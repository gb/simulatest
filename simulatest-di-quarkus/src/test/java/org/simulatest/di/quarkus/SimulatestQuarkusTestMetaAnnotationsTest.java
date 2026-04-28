package org.simulatest.di.quarkus;

import io.quarkus.test.common.QuarkusTestResource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Locks down {@link SimulatestQuarkusTest}'s meta-annotations. Refactors that
 * accidentally drop one would silently break user setups (the test resource
 * stops registering, or per-test rollback stops firing). This test fails
 * loudly instead.
 */
class SimulatestQuarkusTestMetaAnnotationsTest {

	@Test
	void quarkusTestResourceTargetsTheRightLifecycleManager() {
		QuarkusTestResource meta = SimulatestQuarkusTest.class.getAnnotation(QuarkusTestResource.class);
		assertNotNull(meta, "@SimulatestQuarkusTest must carry @QuarkusTestResource so the test resource auto-registers");
		assertEquals(SimulatestQuarkusTestResource.class, meta.value());
	}

	@Test
	void extendWithTargetsThePostArcEnvironmentRunner() {
		ExtendWith meta = SimulatestQuarkusTest.class.getAnnotation(ExtendWith.class);
		assertNotNull(meta, "@SimulatestQuarkusTest must carry @ExtendWith so per-test push/pop fires");
		assertEquals(1, meta.value().length);
		assertEquals(PostArcEnvironmentRunner.class, meta.value()[0]);
	}
}
