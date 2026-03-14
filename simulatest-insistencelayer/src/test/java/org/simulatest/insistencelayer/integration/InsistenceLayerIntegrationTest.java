package org.simulatest.insistencelayer.integration;

import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.simulatest.insistencelayer.InsistenceLayerManager;
import org.simulatest.insistencelayer.InsistenceLayerManagerFactory;
import org.simulatest.insistencelayer.connection.ConnectionWrapper;
import org.simulatest.insistencelayer.datasource.InsistenceLayerDataSource;

public class InsistenceLayerIntegrationTest {

	private InsistenceLayerManager insistenceLayerManager;
	private ConnectionWrapper wrapper;
	private Connection connection;
	private Statement statement;

	@Before
	public void setup() throws Exception {
		JdbcDataSource h2 = new JdbcDataSource();
		h2.setURL("jdbc:h2:mem:integrationtest;DB_CLOSE_DELAY=-1");
		h2.setUser("sa");

		InsistenceLayerDataSource ds = new InsistenceLayerDataSource(h2);
		wrapper = ds.getConnectionWrapper();
		connection = ds.getConnection();
		insistenceLayerManager = InsistenceLayerManagerFactory.build(wrapper);
		statement = connection.createStatement();

		statement.executeUpdate("CREATE TABLE IF NOT EXISTS LOG (NAME VARCHAR(50))");
	}

	@After
	public void teardown() throws Exception {
		while (insistenceLayerManager.getCurrentLevel() > 0) {
			insistenceLayerManager.decreaseLevel();
		}
		statement.executeUpdate("DELETE FROM LOG");
	}

	@Test
	public void integrationTest() throws SQLException {
		insistenceLayerManager.increaseLevel();
		statement.executeUpdate("INSERT INTO LOG VALUES ('1')");
		assertEquals(1, countFromTableLog());

		insistenceLayerManager.increaseLevel();
		statement.executeUpdate("DELETE FROM LOG");
		assertEquals(0, countFromTableLog());

		insistenceLayerManager.increaseLevel();
		statement.executeUpdate("INSERT INTO LOG values ('1')");
		statement.executeUpdate("INSERT INTO LOG values ('2')");
		statement.executeUpdate("INSERT INTO LOG values ('3')");
		assertEquals(3, countFromTableLog());

		insistenceLayerManager.increaseLevel();
		assertEquals(3, countFromTableLog());

		insistenceLayerManager.decreaseLevel();
		assertEquals(3, countFromTableLog());

		insistenceLayerManager.decreaseLevel();
		assertEquals(0, countFromTableLog());

		insistenceLayerManager.decreaseLevel();
		assertEquals(1, countFromTableLog());

		insistenceLayerManager.decreaseLevel();
		assertEquals(0, countFromTableLog());
	}

	@Test
	public void resetCurrentLevelTest() throws SQLException {
		insistenceLayerManager.increaseLevel();
		assertEquals(1, insistenceLayerManager.getCurrentLevel());
		statement.executeUpdate("INSERT INTO LOG VALUES ('1')");

		insistenceLayerManager.increaseLevel();
		assertEquals(2, insistenceLayerManager.getCurrentLevel());

		statement.executeUpdate("INSERT INTO LOG values ('1')");
		statement.executeUpdate("INSERT INTO LOG values ('2')");
		statement.executeUpdate("INSERT INTO LOG values ('3')");
		assertEquals(4, countFromTableLog());

		insistenceLayerManager.resetCurrentLevel();
		assertEquals(2, insistenceLayerManager.getCurrentLevel());

		assertEquals(1, countFromTableLog());
	}

	private int countFromTableLog() throws SQLException {
		ResultSet rs = statement.executeQuery("SELECT COUNT(*) from log");
		rs.next();
		return rs.getInt(1);
	}

}
