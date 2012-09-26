/**
 * RmiJdbc client/server JDBC Driver
 * (C) ExperLog 1999-2000
 *
 * @version     1.0
 * @author      Pierre-Yves Gibello (pierreyves.gibello@experlog.com)
 */

package org.objectweb.rmijdbc;

import java.sql.*;
import java.rmi.RemoteException;

/**
 * A reference to an SQL structured type value in the database. A Ref can be
 * saved to persistent storage. A Ref is dereferenced by passing it as a
 * parameter to an SQL statement and executing the statement.
 */

public class RJRef implements java.sql.Ref, java.io.Serializable {

	private static final long serialVersionUID = -6995641239473023862L;
	
	RJRefInterface rmiRef_;

	public RJRef(RJRefInterface r) {
		rmiRef_ = r;
	}

	public String getBaseTypeName() throws SQLException {
		try {
			return rmiRef_.getBaseTypeName();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	// -----------------------JDBC 3.0 ------------------------
	public Object getObject(java.util.Map<String,Class<?>> map) throws SQLException {
		try {
			return rmiRef_.getObject(map);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public Object getObject() throws SQLException {
		try {
			return rmiRef_.getObject();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public void setObject(Object value) throws SQLException {
		try {
			rmiRef_.setObject(value);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

};
