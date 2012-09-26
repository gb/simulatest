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
public interface RJRefInterface extends java.rmi.Remote {

	String getBaseTypeName() throws RemoteException, SQLException;

	Object getObject(java.util.Map<String, Class<?>> map)
			throws RemoteException, SQLException;

	Object getObject() throws RemoteException, SQLException;

	void setObject(Object value) throws RemoteException, SQLException;

};
