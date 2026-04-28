package org.simulatest.di.quarkus;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.quarkus.test.common.QuarkusTestResource;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Marker that engages Simulatest's transactional isolation on a
 * {@code @QuarkusTest} class. Place it alongside {@code @QuarkusTest}:
 *
 * <pre>
 * &#064;QuarkusTest
 * &#064;SimulatestQuarkusTest
 * &#064;UseEnvironment(MyEnv.class)
 * public class BookRepositoryTest { ... }
 * </pre>
 *
 * <p>The annotation is meta-annotated with {@link QuarkusTestResource} (so
 * {@link SimulatestQuarkusTestResource} runs before Arc and rewrites JDBC
 * config) and with {@link ExtendWith} for {@link PostArcEnvironmentRunner}
 * (so per-test Insistence Layer levels are pushed and popped). Composing
 * the two activations into one annotation keeps the user-facing surface to
 * a single Simulatest-flavored marker.
 *
 * <p>Why this annotation exists: Quarkus does not enable JUnit Jupiter's
 * {@code junit.jupiter.extensions.autodetection.enabled} property, so the
 * post-Arc Jupiter extension cannot be picked up via {@code META-INF/services}
 * alone. The meta-annotated {@link ExtendWith} is the canonical activation
 * mechanism that always works.
 */
@Retention(RUNTIME)
@Target(TYPE)
@QuarkusTestResource(SimulatestQuarkusTestResource.class)
@ExtendWith(PostArcEnvironmentRunner.class)
public @interface SimulatestQuarkusTest {
}
