package org.simulatest.environment.bootstrap;

import javax.sql.DataSource;

/**
 * Tells Simulatest how to reach your database and how to create its schema.
 *
 * <h2>What this is for</h2>
 *
 * Simulatest needs two things before it can run a single test:
 * <ol>
 *   <li>A {@link DataSource} pointing at a real database, so it can wrap
 *       it in the Insistence Layer and route every JDBC call through a
 *       savepoint stack.</li>
 *   <li>A schema (tables, indexes, constraints) already created in that
 *       database, so the first {@code INSERT} doesn't fail.</li>
 * </ol>
 *
 * Implement this interface and register it with Java's standard
 * {@link java.util.ServiceLoader}: add a line in
 * {@code src/test/resources/META-INF/services/org.simulatest.environment.bootstrap.SimulatestDatabaseSetup}
 * with the fully-qualified class name. That's the entire bootstrap
 * contract: no {@code SimulatestPlugin} to extend, no
 * {@code InsistenceLayerFactory.configure(...)} call to make.
 *
 * <h2>When each method is called</h2>
 *
 * Once per test suite, at suite start, in declaration order:
 * {@link #dataSource()} first, then {@link #setupSchema(DataSource)}.
 * Neither is called again for the lifetime of the suite. The per-method
 * Javadoc below covers what to expect at each call.
 *
 * <h2>What you need to implement</h2>
 *
 * Depends on whether you use a dependency-injection container.
 *
 * <h3>Plain JDBC (no DI)</h3>
 *
 * Implement <strong>both</strong> methods. {@link #dataSource()} builds and
 * returns a {@code DataSource}; {@link #setupSchema(DataSource)} creates the
 * schema.
 *
 * <pre>{@code
 * public class AppDatabaseSetup implements SimulatestDatabaseSetup {
 *
 *     @Override
 *     public DataSource dataSource() {
 *         JdbcDataSource ds = new JdbcDataSource();
 *         ds.setURL("jdbc:h2:mem:app;DB_CLOSE_DELAY=-1");
 *         ds.setUser("sa");
 *         return ds;
 *     }
 *
 *     @Override
 *     public void setupSchema(DataSource ds) {
 *         Flyway.configure().dataSource(ds).load().migrate();
 *     }
 * }
 * }</pre>
 *
 * <h3>With Spring, Guice, or CDI</h3>
 *
 * Your DI container already builds a {@code DataSource}. The matching
 * Simulatest DI plugin (e.g. {@code simulatest-di-spring}) discovers it and
 * configures the Insistence Layer for you. So <strong>omit
 * {@link #dataSource()}</strong>: the default {@code null} signals "use the
 * one DI provides." Only implement {@link #setupSchema(DataSource)}:
 *
 * <pre>{@code
 * public class AppDatabaseSetup implements SimulatestDatabaseSetup {
 *
 *     @Override
 *     public void setupSchema(DataSource ds) {
 *         Flyway.configure().dataSource(ds).load().migrate();
 *     }
 * }
 * }</pre>
 *
 * If your DI container <em>also</em> handles schema (JPA's
 * {@code hibernate.hbm2ddl.auto=create}, a Liquibase Spring bean with
 * {@code initMethod}, etc.), you don't need this class at all. Simulatest
 * sees zero registered implementations, sees the layer configured by DI,
 * and proceeds.
 *
 * <h2>Why {@link #setupSchema(DataSource)} exists at all</h2>
 *
 * It's the one thing the framework can't fake away. JDBC's rule:
 * <strong>DDL statements ({@code CREATE TABLE}, {@code ALTER}, {@code DROP})
 * auto-commit on every database that matters</strong>: Postgres, Oracle,
 * MySQL, SQL Server. They escape any savepoint, and worse, they break the
 * savepoint stack on the underlying connection.
 *
 * <p>Simulatest's whole premise is "every test runs inside a savepoint that
 * gets rolled back." If schema creation runs inside that savepoint, the
 * auto-commit kills it. So schema must run <em>before</em> the outer
 * savepoint is pushed. This method is the one well-defined moment in the
 * suite lifecycle where that's guaranteed: after the {@code DataSource}
 * has been wrapped, before the first savepoint exists.</p>
 *
 * <p>You can no-op this method only if something else (your DI container,
 * Testcontainers' {@code withInitScript}, a build-system step) has already
 * created the schema before the suite reaches this point.</p>
 *
 * <h2>Discovery rules</h2>
 *
 * Exactly zero or one implementation is allowed on the classpath:
 * <ul>
 *   <li><strong>Zero, with a DI plugin that provides a DataSource:</strong>
 *       proceed. DI owns both DataSource and schema.</li>
 *   <li><strong>Zero, with no DataSource provided by anything:</strong>
 *       the plugin no-ops; calls to
 *       {@link org.simulatest.insistencelayer.InsistenceLayerFactory#requireDataSource()}
 *       later will fail with a clear error.</li>
 *   <li><strong>One:</strong> the normal case. Use it.</li>
 *   <li><strong>Two or more:</strong> fail at suite start with the names of
 *       all implementations found, and tell the user to keep one.</li>
 * </ul>
 *
 * @see org.simulatest.insistencelayer.InsistenceLayerFactory
 */
public interface SimulatestDatabaseSetup {

	/**
	 * Returns the {@code DataSource} Simulatest should wrap with the
	 * Insistence Layer.
	 *
	 * <p><strong>Implement this when:</strong> you don't use a DI container,
	 * or your DI container doesn't expose a {@code DataSource} bean.</p>
	 *
	 * <p><strong>Skip this when:</strong> you use Spring, Guice, or CDI
	 * and bind a {@code DataSource} in the container. The matching
	 * Simulatest DI plugin will discover it. Returning {@code null} from
	 * here (the default) tells Simulatest "look elsewhere for the
	 * DataSource."</p>
	 *
	 * <p><strong>When it's called:</strong> once, at suite start, before any
	 * test class is loaded. Whatever you return is wrapped immediately and
	 * never asked for again.</p>
	 *
	 * <p><strong>What to return:</strong> a normal {@code DataSource}. Don't
	 * wrap it yourself. Don't pool it (one connection is plenty for a test
	 * suite, and pools fight the single-connection model the Insistence
	 * Layer relies on). A {@code DriverManagerDataSource}, the H2
	 * {@code JdbcDataSource}, or the Postgres {@code PGSimpleDataSource}
	 * are all fine.</p>
	 *
	 * @return the DataSource to wrap, or {@code null} to defer to whatever
	 *         a DI plugin provides
	 */
	default DataSource dataSource() {
		return null;
	}

	/**
	 * Creates the schema (tables, indexes, constraints, seed reference data
	 * that must survive between tests) in the given {@code DataSource}.
	 *
	 * <p><strong>Implement this when:</strong> your schema isn't created
	 * automatically by something else before tests run.</p>
	 *
	 * <p><strong>Skip this when:</strong> your schema is already in place
	 * by the time Simulatest starts: JPA's {@code hbm2ddl.auto=create},
	 * a Liquibase/Flyway bean wired into your DI context, Testcontainers'
	 * {@code withInitScript(...)}, a CI step that runs migrations before
	 * the test phase, etc.</p>
	 *
	 * <p><strong>When it's called:</strong> once, at suite start,
	 * <em>after</em> {@link #dataSource()} has been wrapped by the
	 * Insistence Layer and <em>before</em> the first savepoint is pushed.
	 * This is the only safe moment for DDL: see the interface-level docs
	 * for why.</p>
	 *
	 * <p><strong>What to do inside:</strong> anything that creates schema.
	 * Simulatest is intentionally unopinionated: call Flyway, call
	 * Liquibase, run a {@code .sql} file with {@code RunScript}, build a
	 * JPA {@code EntityManagerFactory} with {@code create} mode, or just
	 * issue {@code CREATE TABLE} statements yourself.</p>
	 *
	 * <p><strong>What <em>not</em> to do:</strong> don't insert per-test
	 * data here: that belongs in an {@code Environment}. This method is
	 * for structure (tables, constraints), not state.</p>
	 *
	 * @param dataSource the Insistence-Layer-wrapped DataSource. Use this
	 *                   one, not the raw one you returned from
	 *                   {@link #dataSource()}, so any seed data you insert
	 *                   here participates correctly in the savepoint stack.
	 */
	default void setupSchema(DataSource dataSource) {
	}

}
