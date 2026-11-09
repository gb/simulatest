package org.simulatest.insistencelayer.integration;

import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.simulatest.insistencelayer.InsistenceLayer;
import org.simulatest.insistencelayer.InsistenceLayerFactory;
import org.simulatest.insistencelayer.infra.sql.InsistenceLayerDataSource;
import org.simulatest.insistencelayer.util.TestDataSources;

/**
 * Changing the isolation level through the proxy while a level is open must
 * not commit the level's data. H2 commits the open transaction on
 * {@code setTransactionIsolation}, and Spring calls it for every
 * {@code @Transactional(isolation = ...)} that differs from the default.
 */
public class TransactionIsolationInsistenceLayerTest {

	private DataSource realDataSource;
	private InsistenceLayer insistenceLayer;
	private Connection connection;
	private Statement statement;

	@Before
	public void setup() throws Exception {
		realDataSource = TestDataSources.createH2("isolationleveltest");
		InsistenceLayerDataSource ds = new InsistenceLayerDataSource(realDataSource);
		connection = ds.getConnection();
		insistenceLayer = InsistenceLayerFactory.build(ds.getConnectionWrapper());
		statement = connection.createStatement();

		statement.executeUpdate("CREATE TABLE IF NOT EXISTS LOG (NAME VARCHAR(50))");

		insistenceLayer.increaseLevel();
		statement.executeUpdate("INSERT INTO LOG VALUES ('environment-row')");
	}

	@After
	public void teardown() throws Exception {
		insistenceLayer.decreaseAllLevels();
		statement.executeUpdate("DELETE FROM LOG");
		statement.close();
	}

	@Test
	public void setTransactionIsolationInsideALevelShouldNotCommitTheLevelData() throws SQLException {
		connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

		assertEquals(Collections.emptyList(), valuesSeenByAnotherConnection());
	}

	@Test
	public void decreaseLevelShouldUndoTheLevelDataAfterAnIsolationChange() throws SQLException {
		connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

		insistenceLayer.decreaseLevel();

		assertEquals(Collections.emptyList(), valuesSeenByTheProxy());
	}

	@Test
	public void resetCurrentLevelShouldUndoTheTestDataAfterAnIsolationChange() throws SQLException {
		insistenceLayer.increaseLevel();
		statement.executeUpdate("INSERT INTO LOG VALUES ('test-row')");
		connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

		insistenceLayer.resetCurrentLevel();

		assertEquals(Arrays.asList("environment-row"), valuesSeenByTheProxy());
	}

	@Test
	public void anIsolationChangeAroundATransactionShouldLeaveTheLevelsIntact() throws SQLException {
		insistenceLayer.increaseLevel();

		// What Spring's DataSourceTransactionManager does around one @Transactional block.
		int previous = connection.getTransactionIsolation();
		connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
		statement.executeUpdate("INSERT INTO LOG VALUES ('test-row')");
		connection.commit();
		connection.setTransactionIsolation(previous);

		insistenceLayer.resetCurrentLevel();
		assertEquals(Arrays.asList("environment-row"), valuesSeenByTheProxy());

		insistenceLayer.decreaseAllLevels();
		assertEquals(Collections.emptyList(), valuesSeenByAnotherConnection());
	}

	private List<String> valuesSeenByTheProxy() throws SQLException {
		return valuesFromTableLog(statement);
	}

	// A second connection to the same in-memory database sees only committed rows.
	private List<String> valuesSeenByAnotherConnection() throws SQLException {
		try (Connection other = realDataSource.getConnection();
			 Statement otherStatement = other.createStatement()) {
			return valuesFromTableLog(otherStatement);
		}
	}

	private static List<String> valuesFromTableLog(Statement statement) throws SQLException {
		List<String> values = new ArrayList<>();
		try (ResultSet rs = statement.executeQuery("SELECT NAME FROM LOG ORDER BY NAME")) {
			while (rs.next()) values.add(rs.getString("NAME"));
		}
		return values;
	}

}
