package org.simulatest.insistencelayer.connection;

import java.sql.Connection;
import java.sql.SQLException;

import org.simulatest.insistencelayer.datasource.InsistenceLayerDataSource;

public class ConnectionFactory {
	
	private static InsistenceLayerDataSource dataSource = new InsistenceLayerDataSource();
	
	public static Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}

}