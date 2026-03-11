package org.simulatest.insistencelayer.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.simulatest.insistencelayer.InsistenceLayerManager;
import org.simulatest.insistencelayer.InsistenceLayerManagerFactory;
import org.simulatest.insistencelayer.connection.ConnectionWrapper;
import org.simulatest.insistencelayer.datasource.InsistenceLayerDataSource;

public class UserTransactionInsistenceLayerTest {

	private InsistenceLayerManager insistenceLayerManager;
	private ConnectionWrapper wrapper;
	private Connection connection;
	private Statement statement;

	@Before
	public void setup() throws Exception {
		JdbcDataSource h2 = new JdbcDataSource();
		h2.setURL("jdbc:h2:~/.h2/test");
		h2.setUser("sa");

		InsistenceLayerDataSource ds = new InsistenceLayerDataSource(h2);
		wrapper = ds.getConnectionWrapper();
		connection = ds.getConnection();
		insistenceLayerManager = InsistenceLayerManagerFactory.build(wrapper);
		statement = connection.createStatement();

		statement.executeUpdate("CREATE TABLE IF NOT EXISTS LOG (NAME VARCHAR(50))");

		insistenceLayerManager.increaseLevel();
		insistenceLayerManager.increaseLevel();
		insistenceLayerManager.increaseLevel();
		assertEquals(3, insistenceLayerManager.getCurrentLevel());
	}

	@After
	public void teardown() throws Exception {
		while (insistenceLayerManager.getCurrentLevel() > 0) {
			insistenceLayerManager.decreaseLevel();
		}
		statement.executeUpdate("DELETE FROM LOG");
	}

	@Test
	public void commitAndRollbackShouldWorkInsideInsistenceLayer() throws SQLException {
		statement.executeUpdate("INSERT INTO LOG VALUES ('Entity-1')");
		connection.commit();

		assertEquals(Arrays.asList("Entity-1"), valuesFromTableLog());

		statement.executeUpdate("INSERT INTO LOG VALUES ('Entity-2')");
		connection.rollback();

		assertEquals(Arrays.asList("Entity-1"), valuesFromTableLog());
		assertEquals(3, insistenceLayerManager.getCurrentLevel());
	}

	@Test
	public void rollbackWithoutPriorCommitShouldBeNoOp() throws SQLException {
		statement.executeUpdate("INSERT INTO LOG VALUES ('Entity-1')");
		connection.rollback();

		assertEquals(Arrays.asList("Entity-1"), valuesFromTableLog());
		assertEquals(3, insistenceLayerManager.getCurrentLevel());
	}

	@Test
	public void decreaseLevelShouldUndoCommittedUserData() throws SQLException {
		statement.executeUpdate("INSERT INTO LOG VALUES ('at-level-3')");
		connection.commit();
		assertEquals(Arrays.asList("at-level-3"), valuesFromTableLog());

		insistenceLayerManager.decreaseLevel();
		assertEquals(2, insistenceLayerManager.getCurrentLevel());
		assertTrue(valuesFromTableLog().isEmpty());

		statement.executeUpdate("INSERT INTO LOG VALUES ('at-level-2')");
		connection.commit();
		assertEquals(Arrays.asList("at-level-2"), valuesFromTableLog());

		statement.executeUpdate("INSERT INTO LOG VALUES ('should-vanish')");
		connection.rollback();
		assertEquals(Arrays.asList("at-level-2"), valuesFromTableLog());
	}

	@Test
	public void resetCurrentLevelShouldUndoCommittedUserData() throws SQLException {
		statement.executeUpdate("INSERT INTO LOG VALUES ('before-reset')");
		connection.commit();
		assertEquals(Arrays.asList("before-reset"), valuesFromTableLog());

		insistenceLayerManager.resetCurrentLevel();
		assertTrue(valuesFromTableLog().isEmpty());
		assertEquals(3, insistenceLayerManager.getCurrentLevel());

		statement.executeUpdate("INSERT INTO LOG VALUES ('after-reset')");
		connection.commit();
		assertEquals(Arrays.asList("after-reset"), valuesFromTableLog());

		statement.executeUpdate("INSERT INTO LOG VALUES ('should-vanish')");
		connection.rollback();
		assertEquals(Arrays.asList("after-reset"), valuesFromTableLog());
	}

	@Test
	public void userTransactionShouldWorkAfterSetLevelTo() throws SQLException {
		insistenceLayerManager.setLevelTo(1);
		assertEquals(1, insistenceLayerManager.getCurrentLevel());

		statement.executeUpdate("INSERT INTO LOG VALUES ('Entity-1')");
		connection.commit();
		assertEquals(Arrays.asList("Entity-1"), valuesFromTableLog());

		statement.executeUpdate("INSERT INTO LOG VALUES ('should-vanish')");
		connection.rollback();
		assertEquals(Arrays.asList("Entity-1"), valuesFromTableLog());
	}

	@Test
	public void uncommittedDataShouldSurviveLevelIncrease() throws SQLException {
		statement.executeUpdate("INSERT INTO LOG VALUES ('uncommitted')");
		assertEquals(Arrays.asList("uncommitted"), valuesFromTableLog());

		insistenceLayerManager.increaseLevel();
		assertEquals(4, insistenceLayerManager.getCurrentLevel());
		assertEquals(Arrays.asList("uncommitted"), valuesFromTableLog());

		insistenceLayerManager.decreaseLevel();
		assertEquals(3, insistenceLayerManager.getCurrentLevel());
		assertEquals(Arrays.asList("uncommitted"), valuesFromTableLog());
	}

	private List<String> valuesFromTableLog() throws SQLException {
		ResultSet rs = statement.executeQuery("SELECT NAME FROM LOG ORDER BY NAME");
		List<String> values = new ArrayList<String>();
		while (rs.next()) {
			values.add(rs.getString(1));
		}
		return values;
	}

}
