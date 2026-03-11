package org.simulatest.insistencelayer.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.simulatest.insistencelayer.InsistenceLayerManager;
import org.simulatest.insistencelayer.InsistenceLayerManagerFactory;
import org.simulatest.insistencelayer.connection.ConnectionFactory;
import org.simulatest.insistencelayer.connection.ConnectionWrapper;

public class UserTransactionInsistenceLayerTest {

	private InsistenceLayerManager insistenceLayerManager;
	private ConnectionWrapper connection;
	private Statement statement;

	@Before
	public void setup() throws Exception {
		connection = ConnectionFactory.getConnection();
		insistenceLayerManager = InsistenceLayerManagerFactory.build(connection);
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
		// Transaction 1: insert + commit
		statement.executeUpdate("INSERT INTO LOG VALUES ('Entity-1')");
		connection.commit();

		assertEquals(Arrays.asList("Entity-1"), valuesFromTableLog());

		// Transaction 2: insert + rollback
		statement.executeUpdate("INSERT INTO LOG VALUES ('Entity-2')");
		connection.rollback();

		// Entity-1 survived, Entity-2 was rolled back
		assertEquals(Arrays.asList("Entity-1"), valuesFromTableLog());

		// Insistence layer level unchanged
		assertEquals(3, insistenceLayerManager.getCurrentLevel());
	}

	@Test
	public void rollbackWithoutPriorCommitShouldBeNoOp() throws SQLException {
		// No commit has been issued, so there is no rollback point
		statement.executeUpdate("INSERT INTO LOG VALUES ('Entity-1')");
		connection.rollback();

		// Data survives because there was no savepoint to roll back to
		assertEquals(Arrays.asList("Entity-1"), valuesFromTableLog());
		assertEquals(3, insistenceLayerManager.getCurrentLevel());
	}

	@Test
	public void decreaseLevelShouldUndoCommittedUserData() throws SQLException {
		statement.executeUpdate("INSERT INTO LOG VALUES ('at-level-3')");
		connection.commit();
		assertEquals(Arrays.asList("at-level-3"), valuesFromTableLog());

		// Framework decreases level — the insistence layer rollback undoes everything
		insistenceLayerManager.decreaseLevel();
		assertEquals(2, insistenceLayerManager.getCurrentLevel());
		assertTrue(valuesFromTableLog().isEmpty());

		// User transactions should still work at the new level
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

		// Framework resets the current level (e.g. between sibling tests)
		insistenceLayerManager.resetCurrentLevel();
		assertTrue(valuesFromTableLog().isEmpty());
		assertEquals(3, insistenceLayerManager.getCurrentLevel());

		// User transactions should still work after the reset
		statement.executeUpdate("INSERT INTO LOG VALUES ('after-reset')");
		connection.commit();
		assertEquals(Arrays.asList("after-reset"), valuesFromTableLog());

		statement.executeUpdate("INSERT INTO LOG VALUES ('should-vanish')");
		connection.rollback();
		assertEquals(Arrays.asList("after-reset"), valuesFromTableLog());
	}

	@Test
	public void userTransactionShouldWorkAfterSetLevelTo() throws SQLException {
		// setLevelTo uses dropCurrentLevel (releaseSavepoint) to skip levels
		insistenceLayerManager.setLevelTo(1);
		assertEquals(1, insistenceLayerManager.getCurrentLevel());

		// User transactions should still work after levels were dropped
		statement.executeUpdate("INSERT INTO LOG VALUES ('Entity-1')");
		connection.commit();
		assertEquals(Arrays.asList("Entity-1"), valuesFromTableLog());

		statement.executeUpdate("INSERT INTO LOG VALUES ('should-vanish')");
		connection.rollback();
		assertEquals(Arrays.asList("Entity-1"), valuesFromTableLog());
	}

	@Test
	public void uncommittedDataShouldSurviveLevelIncrease() throws SQLException {
		// User inserts data without committing
		statement.executeUpdate("INSERT INTO LOG VALUES ('uncommitted')");
		assertEquals(Arrays.asList("uncommitted"), valuesFromTableLog());

		// Framework increases level — uncommitted data should still be visible
		insistenceLayerManager.increaseLevel();
		assertEquals(4, insistenceLayerManager.getCurrentLevel());
		assertEquals(Arrays.asList("uncommitted"), valuesFromTableLog());

		// Decreasing back should undo what was done at level 4, but data before it survives
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
