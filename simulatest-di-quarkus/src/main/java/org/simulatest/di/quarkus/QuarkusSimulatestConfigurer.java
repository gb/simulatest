package org.simulatest.di.quarkus;

import javax.sql.DataSource;

/**
 * User-provided hook that tells {@link SimulatestQuarkusPlugin} what
 * {@link DataSource} to wrap and how to install the database schema.
 *
 * <p>Register an implementation via
 * {@code META-INF/services/org.simulatest.di.quarkus.QuarkusSimulatestConfigurer}.
 * One per test classpath is the expected case.
 *
 * <p>Typical implementation seeds an H2 in-memory database and runs CREATE
 * TABLE statements. The example below needs the following imports:
 * {@code javax.sql.DataSource}, {@code java.sql.Connection},
 * {@code java.sql.Statement}, {@code java.sql.SQLException},
 * {@code org.h2.jdbcx.JdbcDataSource}.
 *
 * <pre>
 * public final class MyConfigurer implements QuarkusSimulatestConfigurer {
 *
 *     &#064;Override public DataSource dataSource() {
 *         JdbcDataSource h2 = new JdbcDataSource();
 *         h2.setURL("jdbc:h2:mem:quarkus-test;DB_CLOSE_DELAY=-1");
 *         return h2;
 *     }
 *
 *     &#064;Override public void applySchema(DataSource wrapped) {
 *         try (Connection c = wrapped.getConnection();
 *              Statement s = c.createStatement()) {
 *             s.execute("CREATE TABLE book (id BIGINT PRIMARY KEY, title VARCHAR(200))");
 *         } catch (SQLException e) {
 *             throw new IllegalStateException("schema failed", e);
 *         }
 *     }
 * }
 * </pre>
 */
public interface QuarkusSimulatestConfigurer {

	/**
	 * The raw {@link DataSource} to hand to the Insistence Layer. Must point at
	 * the same database Quarkus-managed Hibernate will use.
	 */
	DataSource dataSource();

	/**
	 * Applies the schema to the (already Insistence-Layer-wrapped)
	 * {@link DataSource}. Runs BEFORE any environment; DDL is safe here because
	 * the wrapper hasn't yet pushed any savepoints.
	 */
	void applySchema(DataSource wrapped);

}
