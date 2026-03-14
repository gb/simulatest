package org.simulatest.insistencelayer;

import static org.junit.Assert.*;

import java.sql.SQLException;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.Test;
import org.simulatest.insistencelayer.datasource.InsistenceLayerDataSource;

public class InsistenceLayerDataSourceTest {

	@Test
	public void shouldNotBePossibleCreateAnInstanceWithANullDataSource() {
		try {
			new InsistenceLayerDataSource(null);
			fail("was possible create an instance of InsistenceLayerDataSource with a null DataSource!");
		} catch (RuntimeException e) {
			assertEquals("DataSource is null", e.getMessage());
		}
	}

	@Test
	public void shouldGetANonNullableConnection() throws SQLException {
		JdbcDataSource h2 = new JdbcDataSource();
		h2.setURL("jdbc:h2:mem:datasourcetest;DB_CLOSE_DELAY=-1");
		h2.setUser("sa");

		InsistenceLayerDataSource ds = new InsistenceLayerDataSource(h2);
		assertNotNull(ds.getConnection());
	}

	@Test
	public void shouldReturnConnectionWrapperAfterConfigure() throws SQLException {
		JdbcDataSource h2 = new JdbcDataSource();
		h2.setURL("jdbc:h2:mem:datasourcetest;DB_CLOSE_DELAY=-1");
		h2.setUser("sa");

		InsistenceLayerDataSource ds = new InsistenceLayerDataSource(h2);
		assertNotNull(ds.getConnectionWrapper());
	}

}
