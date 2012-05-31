package org.simulatest.hibernate;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.connection.ConnectionProvider;
import org.simulatest.insistencelayer.connection.ConnectionFactory;

public class SimulatestConnectionProvider implements ConnectionProvider {

	@Override
	public void close() throws HibernateException {	}

	@Override
	public void closeConnection(Connection arg0) throws SQLException { }

	@Override
	public void configure(Properties arg0) throws HibernateException { }

	@Override
	public Connection getConnection() throws SQLException {
		return ConnectionFactory.getConnection();
	}

	@Override
	public boolean supportsAggressiveRelease() {
		return false;
	}

}
