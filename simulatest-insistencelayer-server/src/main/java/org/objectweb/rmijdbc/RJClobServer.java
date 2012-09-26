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
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.server.Unreferenced;

public class RJClobServer extends UnicastRemoteObject implements RJClobInterface, Unreferenced {

	private static final long serialVersionUID = 3412043380991229591L;

	java.sql.Clob jdbcClob_;

	public RJClobServer(java.sql.Clob b) throws RemoteException {
		super(RMIRepository.rmiJdbcListenerPort,
				RMIRepository.rmiClientSocketFactory,
				RMIRepository.rmiServerSocketFactory);
		jdbcClob_ = b;
	}

	public void unreferenced() {
		Runtime.getRuntime().gc();
	}

	// TBD There's a hack there (InputStream not serializable)
	// public Reader getCharacterStream() throws RemoteException, SQLException {
	public char[] getCharacterStream() throws RemoteException, SQLException {
		try {
			Reader r = jdbcClob_.getCharacterStream();
			return RJSerializer.toCharArray(r);
		} catch (IOException e) {
			throw new java.rmi.RemoteException(
					"RJClobServer::getCharacterStream()", e);
		}
	}

	public long length() throws RemoteException, SQLException {
		return jdbcClob_.length();
	}

	public String getSubString(long pos, int length) throws RemoteException,
			SQLException {
		return jdbcClob_.getSubString(pos, length);
	}

	// TBD There's a hack there (InputStream not serializable)
	// public InputStream getAsciiStream() throws RemoteException, SQLException
	// {
	public byte[] getAsciiStream() throws RemoteException, SQLException {
		try {
			InputStream s = jdbcClob_.getAsciiStream();
			return RJSerializer.toByteArray(s);
		} catch (IOException e) {
			throw new java.rmi.RemoteException(
					"RJClobServer::getBinaryStream()", e);
		}
	}

	public long position(String searchstr, long start) throws RemoteException,
			SQLException {
		return jdbcClob_.position(searchstr, start);
	}

	public long position(Clob pattern, long start) throws RemoteException,
			SQLException {
		return jdbcClob_.position(pattern, start);
	}

	// ---------------------------- jdbc 3.0 -----------------------------------
	public int setString(long pos, String str) throws RemoteException,
			SQLException {
		return jdbcClob_.setString(pos, str);
	}

	public int setString(long pos, String str, int offset, int len)
			throws RemoteException, SQLException {
		return jdbcClob_.setString(pos, str, offset, len);
	}

	public java.io.OutputStream setAsciiStream(long pos)
			throws RemoteException, SQLException {
		return jdbcClob_.setAsciiStream(pos);
	}

	public java.io.Writer setCharacterStream(long pos) throws RemoteException,
			SQLException {
		return jdbcClob_.setCharacterStream(pos);
	}

	public void truncate(long len) throws RemoteException, SQLException {
		jdbcClob_.truncate(len);
	}

};
