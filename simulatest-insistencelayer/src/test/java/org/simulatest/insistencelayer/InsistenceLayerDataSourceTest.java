package org.simulatest.insistencelayer;

import static org.junit.Assert.*;

import java.sql.SQLException;

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
		InsistenceLayerDataSource ds = new InsistenceLayerDataSource(TestDataSources.createH2("datasourcetest"));
		assertNotNull(ds.getConnection());
	}

	@Test
	public void shouldReturnConnectionWrapperAfterConfigure() throws SQLException {
		InsistenceLayerDataSource ds = new InsistenceLayerDataSource(TestDataSources.createH2("datasourcetest"));
		assertNotNull(ds.getConnectionWrapper());
	}

}
