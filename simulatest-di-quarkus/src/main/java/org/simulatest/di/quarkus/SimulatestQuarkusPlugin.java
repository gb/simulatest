package org.simulatest.di.quarkus;

import java.util.Collection;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.simulatest.environment.junit5.DeferredEnvironmentCoordinator;
import org.simulatest.environment.junit5.DeferredEnvironmentLifecycle;
import org.simulatest.environment.plugin.EnvironmentLifecycle;
import org.simulatest.environment.plugin.SimulatestPlugin;
import org.simulatest.insistencelayer.InsistenceLayerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simulatest plugin for Quarkus + Panache test suites.
 *
 * <h2>Responsibilities</h2>
 *
 * <p>This plugin handles exactly the two things that must happen before
 * Simulatest's environment tree walks and before Quarkus boots:
 * <ol>
 *   <li>Discover a {@link QuarkusSimulatestConfigurer} on the classpath via
 *       {@link ServiceLoader}, wrap the {@link DataSource} it supplies with
 *       the Insistence Layer, and register it in
 *       {@link InsistenceLayerFactory}.</li>
 *   <li>Apply the user-supplied schema DDL to the wrapped data source, so
 *       tables exist before any environment tries to insert rows.</li>
 * </ol>
 *
 * <h2>What it deliberately does NOT do</h2>
 *
 * <p>This plugin does not bootstrap Arc or Quarkus. Quarkus starts on its
 * own schedule when a {@code @QuarkusTest}-annotated class enters its
 * {@code beforeAll} phase. By then the Insistence Layer is already
 * configured and the schema already exists, so Quarkus-managed Hibernate
 * can initialize cleanly.
 *
 * <p>Connections are routed through the Insistence Layer at the JDBC driver
 * layer — see {@link SimulatestInsistenceDriver}. Agroal operates on its
 * usual {@code DataSource}; every connection it hands out is already
 * Insistence-Layer-wrapped because the driver is what created it.
 *
 * <h2>What the user still has to configure</h2>
 *
 * <p>In {@code application.properties}, Hibernate cannot auto-detect a
 * dialect when {@code db-kind=other}, so set it explicitly:
 *
 * <pre>
 * # Required: Hibernate dialect must match the underlying database
 * quarkus.hibernate-orm.dialect=H2
 * quarkus.hibernate-orm.database.generation=none
 *
 * # Data source wiring
 * quarkus.datasource.db-kind=other
 * quarkus.datasource.jdbc.driver=org.simulatest.di.quarkus.SimulatestInsistenceDriver
 * quarkus.datasource.jdbc.url=jdbc:simulatest:jdbc:h2:mem:app;DB_CLOSE_DELAY=-1
 * quarkus.datasource.jdbc.min-size=1
 * quarkus.datasource.jdbc.max-size=1
 * quarkus.datasource.jdbc.initial-size=1
 *
 * # Classloading: every Simulatest jar carrying cross-classloader static
 * # state or identity-sensitive types (Environment, annotations) must be
 * # loaded by the parent classloader, so the engine and the Arc-loaded
 * # extension share one view of them.
 * quarkus.class-loading.parent-first-artifacts=\
 *     org.simulatest:simulatest-insistencelayer,\
 *     org.simulatest:simulatest-environment-core,\
 *     org.simulatest:simulatest-environment-junit-platform,\
 *     org.simulatest:simulatest-di-quarkus
 *
 * # Disable Dev Services so they don't spin up their own datasource
 * quarkus.devservices.enabled=false
 * </pre>
 *
 * <p>The user must also add {@code org.simulatest:simulatest-di-quarkus} to
 * the test classpath and provide a {@link QuarkusSimulatestConfigurer}
 * registered via {@code META-INF/services}. Exactly one configurer is
 * required; zero or more than one causes startup to fail with a diagnostic
 * error rather than surface confusing downstream failures.
 *
 * <h2>Thread-safety</h2>
 *
 * <p>Not thread-safe. Initialize and destroy from the owning test thread.
 * Inherits the Insistence Layer's single-thread constraint.
 */
public final class SimulatestQuarkusPlugin implements SimulatestPlugin {

	private static final Logger logger = LoggerFactory.getLogger(SimulatestQuarkusPlugin.class);

	@Override
	public void initialize(Collection<Class<?>> testClasses) {
		// A previous test session in the same JVM may have exited abruptly
		// and left coordinator state behind. Resetting on every session entry
		// guarantees a clean slate regardless of how the prior one ended.
		DeferredEnvironmentCoordinator.reset();

		if (InsistenceLayerFactory.isConfigured()) {
			logger.info("InsistenceLayer already configured; skipping bootstrap");
			return;
		}

		logClassloaderContext();
		apply(requireExactlyOneConfigurer(loadConfigurers()));
	}

	/**
	 * Validates discovery found exactly one configurer and returns it. Throws
	 * {@link IllegalStateException} with a diagnostic message on zero or more
	 * than one, turning a silent misconfiguration into a loud, actionable
	 * failure at plugin-init time rather than a confusing
	 * {@code requireDataSource()} error during the first environment.
	 *
	 * <p>Package-private for direct testing of the validation paths without
	 * having to simulate {@code ServiceLoader} behavior.
	 */
	static QuarkusSimulatestConfigurer requireExactlyOneConfigurer(
			List<QuarkusSimulatestConfigurer> configurers) {
		if (configurers.isEmpty()) {
			throw new IllegalStateException(
				"No " + QuarkusSimulatestConfigurer.class.getSimpleName() + " was found on the classpath. "
				+ "Register one via META-INF/services/" + QuarkusSimulatestConfigurer.class.getName() + ".");
		}
		if (configurers.size() > 1) {
			String names = configurers.stream()
					.map(c -> c.getClass().getName())
					.collect(Collectors.joining(", "));
			throw new IllegalStateException(
				"Multiple " + QuarkusSimulatestConfigurer.class.getSimpleName() + " implementations found: "
				+ names + ". Exactly one is required.");
		}
		return configurers.get(0);
	}

	private void apply(QuarkusSimulatestConfigurer configurer) {
		DataSource rawDataSource = configurer.dataSource();
		InsistenceLayerFactory.configure(rawDataSource);

		// Schema application runs BEFORE any environment, and therefore before
		// ConnectionWrapper.wrap() sets autoCommit=false. DDL at this point is
		// safe: no savepoint stack exists yet to invalidate.
		DataSource wrapped = InsistenceLayerFactory.requireDataSource();
		configurer.applySchema(wrapped);

		logger.info("Simulatest Quarkus plugin initialized via {}", configurer.getClass().getName());
	}

	// Emits the classloader identity of InsistenceLayerFactory at INFO. When a
	// Quarkus test run misbehaves with phantom isolation failures, this log
	// line lets a diagnostician verify at a glance that the parent-first
	// classloading configuration is in effect.
	private void logClassloaderContext() {
		ClassLoader factoryCl = InsistenceLayerFactory.class.getClassLoader();
		ClassLoader pluginCl  = getClass().getClassLoader();
		if (factoryCl == pluginCl) {
			logger.info("InsistenceLayerFactory loaded by plugin classloader {}", factoryCl);
		} else {
			logger.info(
				"InsistenceLayerFactory classloader ({}) differs from plugin classloader ({}). "
				+ "Confirm quarkus.class-loading.parent-first-artifacts includes simulatest-insistencelayer.",
				factoryCl, pluginCl);
		}
	}

	private static List<QuarkusSimulatestConfigurer> loadConfigurers() {
		return ServiceLoader.load(QuarkusSimulatestConfigurer.class).stream()
				.map(ServiceLoader.Provider::get)
				.toList();
	}

	/**
	 * Contributes the deferred lifecycle so the engine's tree walk becomes a
	 * no-op for env run and savepoint push; {@link QuarkusEnvironmentJupiterExtension}
	 * does that work after Arc has booted inside the inner Jupiter session.
	 */
	@Override
	public EnvironmentLifecycle environmentLifecycle() {
		return DeferredEnvironmentLifecycle.INSTANCE;
	}

	/**
	 * Clears suite-wide state so the next test run starts from a clean slate,
	 * which matters when multiple test sessions share one JVM and/or different
	 * {@code @TestProfile}s demand different schemas.
	 */
	@Override
	public void destroy() {
		InsistenceLayerFactory.clear();
		DeferredEnvironmentCoordinator.reset();
		QuarkusEnvironmentJupiterExtension.forgetLastKnownContainer();
	}

}
