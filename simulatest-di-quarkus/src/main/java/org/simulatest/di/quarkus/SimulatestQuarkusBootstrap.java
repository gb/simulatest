package org.simulatest.di.quarkus;

import java.util.ServiceLoader;

import javax.sql.DataSource;

/**
 * Optional schema applier. Implementations install the database schema the
 * test suite expects to find on first run, before Quarkus boots and before
 * any Simulatest environment opens its first transaction.
 *
 * <p>Most projects don't need this — Hibernate's
 * {@code quarkus.hibernate-orm.database.generation=drop-and-create} or
 * Flyway/Liquibase Quarkus extensions handle schema on their own, and they
 * see the Insistence-Layer-wrapped DataSource transparently. Only ship a
 * {@code SimulatestQuarkusBootstrap} when the project genuinely needs to
 * run DDL itself before any test method executes.
 *
 * <p>Discovered via {@link ServiceLoader}: register your implementation in
 * {@code META-INF/services/org.simulatest.di.quarkus.SimulatestQuarkusBootstrap}.
 * Exactly zero or one bootstrap may be registered per test classpath; more
 * than one fails fast at session start with a diagnostic error.
 */
public interface SimulatestQuarkusBootstrap {

	/**
	 * Applies the schema to the (already Insistence-Layer-wrapped)
	 * {@link DataSource}. Runs once per Quarkus session, before any
	 * environment opens a connection. DDL is safe at this point because no
	 * savepoint has been pushed yet.
	 */
	void applySchema(DataSource wrapped);

}
