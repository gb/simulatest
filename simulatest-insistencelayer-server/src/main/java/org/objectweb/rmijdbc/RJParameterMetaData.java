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

public class RJParameterMetaData implements java.sql.ParameterMetaData, java.io.Serializable {

	private static final long serialVersionUID = -5246592995965891733L;

	RJParameterMetaDataInterface rmiParameterMetaData_;

	public RJParameterMetaData(RJParameterMetaDataInterface s) {
		rmiParameterMetaData_ = s;
	}

	public String getParameterClassName(int param) throws SQLException {
		try {
			return rmiParameterMetaData_.getParameterClassName(param);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public int getParameterCount() throws SQLException {
		try {
			return rmiParameterMetaData_.getParameterCount();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public int getParameterMode(int param) throws SQLException {
		try {
			return rmiParameterMetaData_.getParameterMode(param);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public int getParameterType(int param) throws SQLException {
		try {
			return rmiParameterMetaData_.getParameterType(param);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public String getParameterTypeName(int param) throws SQLException {
		try {
			return rmiParameterMetaData_.getParameterTypeName(param);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public int getPrecision(int param) throws SQLException {
		try {
			return rmiParameterMetaData_.getPrecision(param);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public int getScale(int param) throws SQLException {
		try {
			return rmiParameterMetaData_.getScale(param);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public int isNullable(int param) throws SQLException {
		try {
			return rmiParameterMetaData_.isNullable(param);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public boolean isSigned(int param) throws SQLException {
		try {
			return rmiParameterMetaData_.isSigned(param);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

};
