package org.simulatest.insistencelayer.integration;

import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.simulatest.insistencelayer.InsistenceLayer;
import org.simulatest.insistencelayer.InsistenceLayerFactory;
import org.simulatest.insistencelayer.util.TestDataSources;
import org.simulatest.insistencelayer.infra.sql.ConnectionWrapper;
import org.simulatest.insistencelayer.infra.sql.InsistenceLayerDataSource;

public class InsistenceLayerIntegrationTest {

	private InsistenceLayer insistenceLayer;
	private ConnectionWrapper wrapper;
	private Connection connection;
	private Statement statement;

	@Before
	public void setup() throws Exception {
		InsistenceLayerDataSource ds = new InsistenceLayerDataSource(TestDataSources.createH2("integrationtest"));
		wrapper = ds.getConnectionWrapper();
		connection = ds.getConnection();
		insistenceLayer = InsistenceLayerFactory.build(wrapper);
		statement = connection.createStatement();

		statement.executeUpdate("CREATE TABLE IF NOT EXISTS LOG (NAME VARCHAR(50))");
	}

	@After
	public void teardown() throws Exception {
		while (insistenceLayer.getCurrentLevel() > 0) {
			insistenceLayer.decreaseLevel();
		}
		statement.executeUpdate("DELETE FROM LOG");
		statement.close();
		connection.close();
	}

	@Test
	public void decreaseShouldUnwindEachLevelInReverseOrder() throws SQLException {
		insistenceLayer.increaseLevel();
		statement.executeUpdate("INSERT INTO LOG VALUES ('1')");
		assertEquals("after insert at level 1", 1, countFromTableLog());

		insistenceLayer.increaseLevel();
		statement.executeUpdate("DELETE FROM LOG");
		assertEquals("after delete at level 2", 0, countFromTableLog());

		insistenceLayer.increaseLevel();
		statement.executeUpdate("INSERT INTO LOG values ('1')");
		statement.executeUpdate("INSERT INTO LOG values ('2')");
		statement.executeUpdate("INSERT INTO LOG values ('3')");
		assertEquals("after three inserts at level 3", 3, countFromTableLog());

		insistenceLayer.increaseLevel();
		assertEquals("level 4 adds no data", 3, countFromTableLog());

		insistenceLayer.decreaseLevel();
		assertEquals("decrease from 4: no state change", 3, countFromTableLog());

		insistenceLayer.decreaseLevel();
		assertEquals("decrease from 3: rollback three inserts", 0, countFromTableLog());

		insistenceLayer.decreaseLevel();
		assertEquals("decrease from 2: restore the single row", 1, countFromTableLog());

		insistenceLayer.decreaseLevel();
		assertEquals("decrease from 1: back to empty baseline", 0, countFromTableLog());
	}

	@Test
	public void resetCurrentLevelTest() throws SQLException {
		insistenceLayer.increaseLevel();
		assertEquals(1, insistenceLayer.getCurrentLevel());
		statement.executeUpdate("INSERT INTO LOG VALUES ('1')");

		insistenceLayer.increaseLevel();
		assertEquals(2, insistenceLayer.getCurrentLevel());

		statement.executeUpdate("INSERT INTO LOG values ('1')");
		statement.executeUpdate("INSERT INTO LOG values ('2')");
		statement.executeUpdate("INSERT INTO LOG values ('3')");
		assertEquals(4, countFromTableLog());

		insistenceLayer.resetCurrentLevel();
		assertEquals(2, insistenceLayer.getCurrentLevel());

		assertEquals(1, countFromTableLog());
	}

	@Test
	public void decreaseLevelAndResetShouldUndoTheCurrentLevelAndTheOneBelow() throws SQLException {
		insistenceLayer.increaseLevel();
		statement.executeUpdate("INSERT INTO LOG VALUES ('level 1')");
		insistenceLayer.increaseLevel();
		statement.executeUpdate("INSERT INTO LOG VALUES ('level 2')");

		insistenceLayer.decreaseLevelAndReset();

		assertEquals(1, insistenceLayer.getCurrentLevel());
		assertEquals("the rows of both levels are undone", 0, countFromTableLog());
	}

	@Test
	public void aNewLevelShouldOpenAndCloseOnTheLevelLeftByDecreaseLevelAndReset() throws SQLException {
		leaveSecondLevelResettingTheFirst();
		statement.executeUpdate("INSERT INTO LOG VALUES ('level 1 again')");
		insistenceLayer.increaseLevel();
		statement.executeUpdate("INSERT INTO LOG VALUES ('level 2 again')");

		insistenceLayer.decreaseLevel();

		assertEquals("only the row of the new level is undone", 1, countFromTableLog());
	}

	@Test
	public void theLevelLeftByDecreaseLevelAndResetShouldStillResetToItsStart() throws SQLException {
		leaveSecondLevelResettingTheFirst();
		statement.executeUpdate("INSERT INTO LOG VALUES ('level 1 again')");

		insistenceLayer.resetCurrentLevel();

		assertEquals(0, countFromTableLog());
	}

	@Test
	public void decreaseLevelAndResetShouldUndoWhatTheLevelBelowCommitted() throws SQLException {
		commitARowInTheFirstLevelAndOpenTheSecond();

		insistenceLayer.decreaseLevelAndReset();

		assertEquals(0, countFromTableLog());
	}

	@Test
	public void rollbackAfterDecreaseLevelAndResetShouldGoToTheStartOfTheLevelNotToTheOldCommit() throws SQLException {
		commitARowInTheFirstLevelAndOpenTheSecond();
		insistenceLayer.decreaseLevelAndReset();
		statement.executeUpdate("INSERT INTO LOG VALUES ('after the reset')");

		connection.rollback();

		assertEquals(0, countFromTableLog());
	}

	@Test
	public void decreaseLevelAndResetAtTheFirstLevelShouldJustLeaveIt() throws SQLException {
		insistenceLayer.increaseLevel();
		statement.executeUpdate("INSERT INTO LOG VALUES ('level 1')");

		insistenceLayer.decreaseLevelAndReset();

		assertEquals(0, insistenceLayer.getCurrentLevel());
		assertEquals(0, countFromTableLog());
	}

	private void leaveSecondLevelResettingTheFirst() {
		insistenceLayer.increaseLevel();
		insistenceLayer.increaseLevel();
		insistenceLayer.decreaseLevelAndReset();
	}

	private void commitARowInTheFirstLevelAndOpenTheSecond() throws SQLException {
		insistenceLayer.increaseLevel();
		statement.executeUpdate("INSERT INTO LOG VALUES ('committed in level 1')");
		connection.commit();
		insistenceLayer.increaseLevel();
	}

	private int countFromTableLog() throws SQLException {
		ResultSet rs = statement.executeQuery("SELECT COUNT(*) from log");
		rs.next();
		return rs.getInt(1);
	}

}
