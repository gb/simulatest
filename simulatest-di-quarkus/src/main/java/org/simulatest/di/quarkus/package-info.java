/**
 * Simulatest's integration with Quarkus's {@code @QuarkusTest}.
 *
 * <h2>Why this module exists separately from {@code simulatest-di-jee}</h2>
 *
 * <p>{@code simulatest-di-jee} boots Weld SE inside Simulatest's own test
 * lifecycle: Simulatest controls when the container starts, when beans are
 * resolved, and when the container shuts down. {@code @QuarkusTest} inverts
 * that. Quarkus owns its lifecycle through its own JUnit Jupiter extension,
 * boots Arc (its CDI runtime) on its own schedule, and may swap classloaders
 * whenever {@code @TestProfile} changes between test classes. Adapting to
 * those constraints is a different shape of work than embedding a CDI
 * container, so the integration lives in its own module rather than being
 * folded into {@code simulatest-di-jee}.
 *
 * <h2>Why this module ships a Jupiter extension and the others don't</h2>
 *
 * <p>{@link org.simulatest.environment.plugin.DependencyInjectionPlugin}
 * runs during JUnit's {@code beforeAll}, in the outer classloader, before
 * Quarkus has booted Arc. Environment instances need to be resolved as Arc
 * beans so their {@code @Inject} fields populate, which can only happen
 * after Arc is up. The plugin therefore contributes a deferred environment
 * lifecycle that makes the engine's tree-walk hooks no-ops; the
 * package-private
 * {@link org.simulatest.di.quarkus.PostArcEnvironmentRunner} then runs the
 * environment ancestry, pushes Insistence Layer levels, and detects Arc
 * restarts to unwind cleanly. Spring, Guice, and Jakarta CDI don't need
 * this because their containers boot synchronously inside the plugin's
 * {@code initialize}.
 *
 * <h2>Why a JDBC driver, and not a CDI decorator or a Hibernate
 * ConnectionProvider</h2>
 *
 * <p>The natural place to wrap a {@link javax.sql.DataSource} in a CDI
 * application is a {@code @Decorator} on the {@code DataSource} bean. The
 * natural place to wrap connections going to Hibernate is a custom
 * {@code ConnectionProvider}. A spike against Quarkus 3.x with Panache and
 * H2 demonstrated that <em>both</em> seams are bypassed: Quarkus's
 * {@code quarkus-agroal} extension registers {@code DataSource} as a
 * <em>synthetic</em> CDI bean (build-time bytecode), and synthetic beans
 * skip the decorator chain. {@code quarkus-hibernate-orm} hard-binds
 * Hibernate to its own internal {@code QuarkusConnectionProvider} at build
 * time and silently ignores
 * {@code quarkus.hibernate-orm.connection-provider} and
 * {@code hibernate.connection.provider_class}.
 *
 * <p>The only seam Quarkus actually honors is the JDBC driver layer.
 * {@link org.simulatest.insistencelayer.infra.sql.InsistenceLayerJdbcDriver}
 * (shipped by {@code simulatest-insistencelayer}) intercepts every
 * connection regardless of how Quarkus, Hibernate, or Agroal acquired it.
 *
 * <h2>The integration seam: {@code QuarkusTestResourceLifecycleManager}</h2>
 *
 * <p>Quarkus offers a first-class hook for code that must run before Arc
 * boots: {@code io.quarkus.test.common.QuarkusTestResourceLifecycleManager}.
 * {@link org.simulatest.di.quarkus.SimulatestQuarkusTestResource} reads the
 * user's {@code quarkus.datasource.jdbc.url}, optionally runs a
 * {@link org.simulatest.di.quarkus.SimulatestQuarkusBootstrap} discovered
 * via {@code ServiceLoader} to install the schema, and publishes JDBC URL
 * and driver overrides that route Agroal through the Insistence Layer. The
 * user's {@code application.properties} stays vanilla — no
 * {@code jdbc:insistencelayer:} prefix, no driver class name. The rewrite
 * happens here, scoped to the test session.
 *
 * <h2>What the user writes</h2>
 *
 * <pre>
 * &#064;QuarkusTest
 * &#064;QuarkusTestResource(SimulatestQuarkusTestResource.class)
 * &#064;UseEnvironment(MyEnv.class)
 * public class BookRepositoryTest { ... }
 * </pre>
 *
 * <p>Three annotations: two from Quarkus, one from Simulatest
 * ({@code @UseEnvironment}, the same one every other DI module uses). No
 * Simulatest-flavored Quarkus annotation; activation uses the vanilla
 * {@code @QuarkusTestResource} mechanism.
 *
 * <p>If the project needs to install schema explicitly (rather than letting
 * Hibernate's {@code drop-and-create} or Flyway/Liquibase do it), implement
 * {@link org.simulatest.di.quarkus.SimulatestQuarkusBootstrap} and register
 * it via
 * {@code META-INF/services/org.simulatest.di.quarkus.SimulatestQuarkusBootstrap}.
 * Otherwise nothing else is needed.
 */
package org.simulatest.di.quarkus;
