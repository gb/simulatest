package org.simulatest.environment.bootstrap;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import javax.sql.DataSource;

import org.simulatest.environment.infra.ServiceLoaders;
import org.simulatest.environment.plugin.SimulatestPlugin;
import org.simulatest.insistencelayer.InsistenceLayerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Built-in {@link SimulatestPlugin} that bootstraps the database for a
 * Simulatest test suite by discovering a {@link SimulatestDatabaseSetup}
 * via {@link java.util.ServiceLoader}.
 *
 * <p>Runs <em>after</em> any DI plugin (Spring/Guice/CDI/Quarkus) so that
 * a {@code DataSource} configured by DI takes precedence over one supplied
 * by {@link SimulatestDatabaseSetup#dataSource()}. The schema hook
 * ({@link SimulatestDatabaseSetup#setupSchema(DataSource)}) always runs
 * against the configured DataSource regardless of who configured it,
 * giving the user one unambiguous place to put DDL.</p>
 *
 * <p>Discovery rules: zero or one {@link SimulatestDatabaseSetup} on the
 * classpath. Two or more is an authorship error and fails fast naming all
 * implementations found.</p>
 */
public final class DatabaseBootstrapPlugin implements SimulatestPlugin {

	private static final Logger logger = LoggerFactory.getLogger(DatabaseBootstrapPlugin.class);

	private final Supplier<Optional<SimulatestDatabaseSetup>> setupSupplier;

	public DatabaseBootstrapPlugin() {
		this(() -> ServiceLoaders.loadAtMostOne(SimulatestDatabaseSetup.class));
	}

	// Package-private so tests can supply their own setup without registering it via ServiceLoader.
	DatabaseBootstrapPlugin(Supplier<Optional<SimulatestDatabaseSetup>> setupSupplier) {
		this.setupSupplier = Objects.requireNonNull(setupSupplier, "setupSupplier");
	}

	@Override
	public void initialize(Collection<Class<?>> testClasses) {
		Optional<SimulatestDatabaseSetup> loaded = setupSupplier.get();
		if (loaded.isEmpty()) return;

		SimulatestDatabaseSetup setup = loaded.get();
		if (InsistenceLayerFactory.isConfigured()) {
			logger.info("InsistenceLayer already configured by an earlier plugin; skipping dataSource()");
			runSchemaPreservingExistingLayer(setup);
		} else {
			configureDataSource(setup);
			runSchemaAndClearLayerOnFailure(setup);
		}
	}

	private static void configureDataSource(SimulatestDatabaseSetup setup) {
		DataSource dataSource = setup.dataSource();
		if (dataSource == null) {
			throw new IllegalStateException(
					"SimulatestDatabaseSetup " + setup.getClass().getName()
							+ " returned null from dataSource() and no DI plugin provided one. "
							+ "Either implement dataSource() or add a DI plugin with a DataSource bean.");
		}
		logger.info("Configuring InsistenceLayer from {}", setup.getClass().getName());
		InsistenceLayerFactory.configure(dataSource);
	}

	private static void runSchemaAndClearLayerOnFailure(SimulatestDatabaseSetup setup) {
		try {
			invokeSetupSchema(setup);
		} catch (RuntimeException e) {
			InsistenceLayerFactory.clear();
			throw e;
		}
	}

	private static void runSchemaPreservingExistingLayer(SimulatestDatabaseSetup setup) {
		invokeSetupSchema(setup);
	}

	private static void invokeSetupSchema(SimulatestDatabaseSetup setup) {
		DataSource wrapped = InsistenceLayerFactory.requireDataSource();
		try {
			setup.setupSchema(wrapped);
		} catch (RuntimeException e) {
			throw new IllegalStateException(
					"Schema setup failed in " + setup.getClass().getName() + ".setupSchema(DataSource)", e);
		}
	}

}
