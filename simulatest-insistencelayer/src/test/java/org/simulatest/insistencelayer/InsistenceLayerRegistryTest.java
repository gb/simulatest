package org.simulatest.insistencelayer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.Test;
import org.simulatest.insistencelayer.infra.exception.InsistenceLayerException;

public class InsistenceLayerRegistryTest {

	@Test
	public void clearShouldCloseTheConnectionTheDataSourceHolds() throws SQLException {
		Connection connection = mock(Connection.class);
		InsistenceLayerRegistry registry = configuredWith(connection);

		registry.clear();

		verify(connection).close();
	}

	@Test
	public void clearShouldForgetTheDataSourceEvenWhenClosingItsConnectionFails() throws SQLException {
		Connection connection = mock(Connection.class);
		doThrow(new SQLException("close failed")).when(connection).close();
		InsistenceLayerRegistry registry = configuredWith(connection);

		assertThrows(InsistenceLayerException.class, registry::clear);

		assertFalse(registry.isConfigured());
		assertTrue(registry.dataSource().isEmpty());
	}

	private static InsistenceLayerRegistry configuredWith(Connection connection) throws SQLException {
		DataSource dataSource = mock(DataSource.class);
		when(dataSource.getConnection()).thenReturn(connection);
		InsistenceLayerRegistry registry = new InsistenceLayerRegistry();
		registry.configure(dataSource);
		return registry;
	}

}
