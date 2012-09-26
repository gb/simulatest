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

public class RJParameterMetaDataServer extends UnicastRemoteObject implements RJParameterMetaDataInterface, Unreferenced {

	private static final long serialVersionUID = -8097027292768216751L;

	ParameterMetaData jdbcParameterMetaData_;

	public RJParameterMetaDataServer(ParameterMetaData s)
			throws RemoteException {
		super(RMIRepository.rmiJdbcListenerPort,
				RMIRepository.rmiClientSocketFactory,
				RMIRepository.rmiServerSocketFactory);
		jdbcParameterMetaData_ = s;
	}

	public void unreferenced() {
		Runtime.getRuntime().gc();
	}

	public String getParameterClassName(int param) throws RemoteException,
			SQLException {
		return jdbcParameterMetaData_.getParameterClassName(param);
	}

	public int getParameterCount() throws RemoteException, SQLException {
		return jdbcParameterMetaData_.getParameterCount();
	}

	public int getParameterMode(int param) throws RemoteException, SQLException {
		return jdbcParameterMetaData_.getParameterMode(param);
	}

	public int getParameterType(int param) throws RemoteException, SQLException {
		return jdbcParameterMetaData_.getParameterType(param);
	}

	public String getParameterTypeName(int param) throws RemoteException,
			SQLException {
		return jdbcParameterMetaData_.getParameterTypeName(param);
	}

	public int getPrecision(int param) throws RemoteException, SQLException {
		return jdbcParameterMetaData_.getPrecision(param);
	}

	public int getScale(int param) throws RemoteException, SQLException {
		return jdbcParameterMetaData_.getScale(param);
	}

	public int isNullable(int param) throws RemoteException, SQLException {
		return jdbcParameterMetaData_.isNullable(param);
	}

	public boolean isSigned(int param) throws RemoteException, SQLException {
		return jdbcParameterMetaData_.isSigned(param);
	}

};
