/**
 * RmiJdbc client/server JDBC Driver
 * (C) ExperLog 1999-2000
 *
 * @version     1.0
 * @author      Pierre-Yves Gibello (pierreyves.gibello@experlog.com)
 *              Additional SSL Support
 *              Douglas Hammond(djhammond@sympatico.ca)
 */

package org.objectweb.rmijdbc;

import java.sql.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.server.Unreferenced;

/**
 * A reference to an SQL structured type value in the database. A Ref can be
 * saved to persistent storage. A Ref is dereferenced by passing it as a
 * parameter to an SQL statement and executing the statement.
 */

public class RJRefServer extends UnicastRemoteObject implements RJRefInterface, Unreferenced {

	private static final long serialVersionUID = 4670883362727163286L;
	java.sql.Ref jdbcRef_;

	public RJRefServer(java.sql.Ref r) throws RemoteException {
		super(RMIRepository.rmiJdbcListenerPort,
				RMIRepository.rmiClientSocketFactory,
				RMIRepository.rmiServerSocketFactory);
		jdbcRef_ = r;
	}

	public void unreferenced() {
		Runtime.getRuntime().gc();
	}

	public String getBaseTypeName() throws RemoteException, SQLException {
		return jdbcRef_.getBaseTypeName();
	}

	// -----------------------JDBC 3.0 ------------------------
	public Object getObject(java.util.Map<String,Class<?>> map) throws RemoteException,
			SQLException {
		return jdbcRef_.getObject(map);
	}

	public Object getObject() throws RemoteException, SQLException {
		return jdbcRef_.getObject();
	}

	public void setObject(Object value) throws RemoteException, SQLException {
		jdbcRef_.setObject(value);
	}
};
