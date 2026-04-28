package org.simulatest.di.quarkus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.ServiceLoader;

import javax.sql.DataSource;

import org.eclipse.microprofile.config.ConfigProvider;
import org.simulatest.insistencelayer.InsistenceLayerFactory;
import org.simulatest.insistencelayer.infra.sql.InsistenceLayerJdbcDataSource;
import org.simulatest.insistencelayer.infra.sql.InsistenceLayerJdbcDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

/**
 * Quarkus test resource that prepares the Insistence Layer before Arc boots.
 *
 * <p>Activation is the vanilla Quarkus way: declare
 * {@code @QuarkusTestResource(SimulatestQuarkusTestResource.class)} on the
 * test class (or a base class). No Simulatest-flavored annotation involved.
 *
 * <p>What it does at {@code start()}:
 * <ol>
 *   <li>Reads the user's {@code quarkus.datasource.jdbc.url} from
 *       MicroProfile Config.</li>
 *   <li>Wraps it in an {@link InsistenceLayerJdbcDataSource} and registers
 *       it with {@link InsistenceLayerFactory}.</li>
 *   <li>If a {@link SimulatestQuarkusBootstrap} is registered via
 *       {@link ServiceLoader} (i.e. a {@code META-INF/services} entry on the
 *       test classpath), runs its {@code applySchema} against the wrapped
 *       {@link DataSource}. If none is registered, schema bootstrap is
 *       skipped — projects relying on
 *       {@code quarkus.hibernate-orm.database.generation} or Flyway/Liquibase
 *       need no bootstrap class.</li>
 *   <li>Returns config overrides that point Quarkus's Agroal pool at
 *       {@link InsistenceLayerJdbcDriver}.</li>
 * </ol>
 *
 * <p>The user's {@code application.properties} stays vanilla — no
 * {@code jdbc:insistencelayer:} prefix, no driver class name. The rewrite
 * happens here, scoped to the test session.
 */
public final class SimulatestQuarkusTestResource implements QuarkusTestResourceLifecycleManager {

	private static final Logger logger = LoggerFactory.getLogger(SimulatestQuarkusTestResource.class);

	private final Optional<SimulatestQuarkusBootstrap> bootstrap;

	public SimulatestQuarkusTestResource() {
		this(discoverBootstrap());
	}

	// Package-private constructor for tests; production code uses the no-arg
	// path which resolves the bootstrap via ServiceLoader.
	SimulatestQuarkusTestResource(Optional<SimulatestQuarkusBootstrap> bootstrap) {
		this.bootstrap = bootstrap;
	}

	@Override
	public Map<String, String> start() {
		String userUrl = ConfigProvider.getConfig()
			.getValue("quarkus.datasource.jdbc.url", String.class);
		Properties props = new Properties();
		ConfigProvider.getConfig().getOptionalValue("quarkus.datasource.username", String.class)
			.ifPresent(v -> props.setProperty("user", v));
		ConfigProvider.getConfig().getOptionalValue("quarkus.datasource.password", String.class)
			.ifPresent(v -> props.setProperty("password", v));

		InsistenceLayerFactory.configure(new InsistenceLayerJdbcDataSource(userUrl, props));

		bootstrap.ifPresent(b -> applySchemaOrUnwind(b, InsistenceLayerFactory.requireDataSource()));

		logger.info("Simulatest is wrapping {} via {}; the test session's DataSource routes through "
			+ "the Insistence Layer for transactional rollback per environment.",
			userUrl, InsistenceLayerJdbcDriver.class.getSimpleName());

		return Map.of(
			"quarkus.datasource.jdbc.url",    InsistenceLayerJdbcDriver.URL_PREFIX + userUrl,
			"quarkus.datasource.jdbc.driver", InsistenceLayerJdbcDriver.class.getName());
	}

	@Override
	public void stop() {
		InsistenceLayerFactory.clear();
	}

	private static void applySchemaOrUnwind(SimulatestQuarkusBootstrap b, DataSource wrapped) {
		try {
			b.applySchema(wrapped);
		} catch (RuntimeException e) {
			InsistenceLayerFactory.clear();
			throw e;
		}
	}

	// Loads the user's bootstrap via ServiceLoader. Zero is allowed (Hibernate's
	// drop-and-create or Flyway/Liquibase handle schema). More than one is a
	// configuration error: which schema would we apply?
	private static Optional<SimulatestQuarkusBootstrap> discoverBootstrap() {
		List<SimulatestQuarkusBootstrap> found = new ArrayList<>();
		for (SimulatestQuarkusBootstrap b : ServiceLoader.load(SimulatestQuarkusBootstrap.class)) {
			found.add(b);
		}
		if (found.size() > 1) {
			throw new IllegalStateException(
				"More than one SimulatestQuarkusBootstrap registered via ServiceLoader: "
				+ found.stream().map(b -> b.getClass().getName()).toList()
				+ ". Exactly zero or one is allowed per test classpath.");
		}
		return found.isEmpty() ? Optional.empty() : Optional.of(found.get(0));
	}

}
