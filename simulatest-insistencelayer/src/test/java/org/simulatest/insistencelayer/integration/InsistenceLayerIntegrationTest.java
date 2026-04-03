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
	}

	@Test
	public void integrationTest() throws SQLException {
		insistenceLayer.increaseLevel();
		statement.executeUpdate("INSERT INTO LOG VALUES ('1')");
		assertEquals(1, countFromTableLog());

		insistenceLayer.increaseLevel();
		statement.executeUpdate("DELETE FROM LOG");
		assertEquals(0, countFromTableLog());

		insistenceLayer.increaseLevel();
		statement.executeUpdate("INSERT INTO LOG values ('1')");
		statement.executeUpdate("INSERT INTO LOG values ('2')");
		statement.executeUpdate("INSERT INTO LOG values ('3')");
		assertEquals(3, countFromTableLog());

		insistenceLayer.increaseLevel();
		assertEquals(3, countFromTableLog());

		insistenceLayer.decreaseLevel();
		assertEquals(3, countFromTableLog());

		insistenceLayer.decreaseLevel();
		assertEquals(0, countFromTableLog());

		insistenceLayer.decreaseLevel();
		assertEquals(1, countFromTableLog());

		insistenceLayer.decreaseLevel();
		assertEquals(0, countFromTableLog());
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

	private int countFromTableLog() throws SQLException {
		ResultSet rs = statement.executeQuery("SELECT COUNT(*) from log");
		rs.next();
		return rs.getInt(1);
	}

}
