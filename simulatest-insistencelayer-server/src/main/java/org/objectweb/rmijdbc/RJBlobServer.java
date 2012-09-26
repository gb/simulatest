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
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.server.Unreferenced;

/**
 * The representation (mapping) in the JavaTM programming language of an SQL
 * BLOB. An SQL BLOB is a built-in type that stores a Binary Large Object as a
 * column value in a row of a database table. The driver implements Blob using
 * an SQL locator(BLOB), which means that a Blob object contains a logical
 * pointer to the SQL BLOB data rather than the data itself. A Blob object is
 * valid for the duration of the transaction in which is was created.
 * 
 * Methods in the interfaces ResultSet, CallableStatement, and
 * PreparedStatement, such as getBlob and setBlob allow a programmer to access
 * the SQL BLOB. The Blob interface provides methods for getting the length of
 * an SQL BLOB (Binary Large Object) value, for materializing a BLOB value on
 * the client, and for determining the position of a pattern of bytes within a
 * BLOB value.
 */

public class RJBlobServer extends UnicastRemoteObject implements
		RJBlobInterface, Unreferenced {

	private static final long serialVersionUID = -9159295159342945431L;

	java.sql.Blob jdbcBlob_;

	public RJBlobServer(java.sql.Blob b) throws RemoteException {
		super(RMIRepository.rmiJdbcListenerPort,
				RMIRepository.rmiClientSocketFactory,
				RMIRepository.rmiServerSocketFactory);
		jdbcBlob_ = b;
	}

	public void unreferenced() {
		Runtime.getRuntime().gc();
	}

	public long length() throws RemoteException, SQLException {
		return jdbcBlob_.length();
	}

	public byte[] getBytes(long pos, int length) throws RemoteException,
			SQLException {
		return jdbcBlob_.getBytes(pos, length);
	}

	// TBD There's a hack there (InputStream not serializable)
	// public InputStream getBinaryStream() throws RemoteException, SQLException
	// {
	public byte[] getBinaryStream() throws RemoteException, SQLException {
		try {
			InputStream s = jdbcBlob_.getBinaryStream();
			return RJSerializer.toByteArray(s);
		} catch (IOException e) {
			throw new java.rmi.RemoteException(
					"RJBlobServer::getBinaryStream()", e);
		}
	}

	public long position(byte[] pattern, long start) throws RemoteException,
			SQLException {
		return jdbcBlob_.position(pattern, start);
	}

	public long position(Blob pattern, long start) throws RemoteException,
			SQLException {
		return jdbcBlob_.position(pattern, start);
	}

	// -------------------------- JDBC 3.0 -----------------------------------

	public int setBytes(long pos, byte[] bytes) throws RemoteException,
			SQLException {
		return jdbcBlob_.setBytes(pos, bytes);
	}

	public int setBytes(long pos, byte[] bytes, int offset, int len)
			throws RemoteException, SQLException {
		return jdbcBlob_.setBytes(pos, bytes, offset, len);
	}

	public java.io.OutputStream setBinaryStream(long pos)
			throws RemoteException, SQLException {
		return jdbcBlob_.setBinaryStream(pos);
	}

	public void truncate(long len) throws RemoteException, SQLException {
		jdbcBlob_.truncate(len);
	}

};
