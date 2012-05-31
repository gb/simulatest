package org.simulatest.insistencelayer.connection;

import java.sql.SQLException;

import org.simulatest.insistencelayer.datasource.InsistenceLayerDataSource;

public class ConnectionFactory {
	
	public static ConnectionWrapper getConnection() throws SQLException {
		return (ConnectionWrapper) new InsistenceLayerDataSource().getConnection();
	}

}