/**
 * RmiJdbc client/server JDBC Driver
 * (C) ObjectWeb 1999-2003
 *
 * @version     3.0
 * @author      Pierre-Yves Gibello (pierreyves.gibello@experlog.com)
 */

package org.objectweb.rmijdbc;

import java.sql.*;
import java.rmi.RemoteException;

public class RJSavepoint implements java.sql.Savepoint, java.io.Serializable {

	private static final long serialVersionUID = 1111602839726444761L;

	RJSavepointInterface rmiSavepoint_;

	public RJSavepoint(RJSavepointInterface s) {
		rmiSavepoint_ = s;
	}

	public int getSavepointId() throws SQLException {
		try {
			return rmiSavepoint_.getSavepointId();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public String getSavepointName() throws SQLException {
		try {
			return rmiSavepoint_.getSavepointName();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

};
