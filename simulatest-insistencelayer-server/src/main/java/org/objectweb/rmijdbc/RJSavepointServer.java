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
import java.rmi.server.UnicastRemoteObject;
import java.rmi.server.Unreferenced;

public class RJSavepointServer extends UnicastRemoteObject implements
		RJSavepointInterface, Unreferenced {

	private static final long serialVersionUID = 8822694132424815784L;

	Savepoint jdbcSavepoint_;

	public RJSavepointServer(Savepoint s) throws RemoteException {
		super(RMIRepository.rmiJdbcListenerPort,
				RMIRepository.rmiClientSocketFactory,
				RMIRepository.rmiServerSocketFactory);
		jdbcSavepoint_ = s;
	}

	public void unreferenced() {
		Runtime.getRuntime().gc();
	}

	public int getSavepointId() throws RemoteException, SQLException {
		return jdbcSavepoint_.getSavepointId();
	}

	public String getSavepointName() throws RemoteException, SQLException {
		return jdbcSavepoint_.getSavepointName();
	}

};
