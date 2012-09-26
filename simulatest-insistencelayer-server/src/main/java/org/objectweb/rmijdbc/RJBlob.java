/**
 * RmiJdbc client/server JDBC Driver
 * (C) ExperLog 1999-2000
 *
 * @version     1.0
 * @author      Pierre-Yves Gibello (pierreyves.gibello@experlog.com)
 */

package org.objectweb.rmijdbc;

import java.sql.*;
import java.io.InputStream;
import java.rmi.RemoteException;

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

public class RJBlob implements java.sql.Blob, java.io.Serializable {

	private static final long serialVersionUID = -7097904509586405062L;
	
	RJBlobInterface rmiBlob_;

	public RJBlob(RJBlobInterface b) {
		rmiBlob_ = b;
	}

	public long length() throws SQLException {
		try {
			return rmiBlob_.length();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public byte[] getBytes(long pos, int length) throws SQLException {
		try {
			return rmiBlob_.getBytes(pos, length);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	// TBD There's a hack there (InputStream not serializable)
	public InputStream getBinaryStream() throws SQLException {
		try {
			byte[] val = rmiBlob_.getBinaryStream();
			return RJSerializer.toInputStream(val);
		} catch (Exception e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public long position(byte[] pattern, long start) throws SQLException {
		try {
			return rmiBlob_.position(pattern, start);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public long position(Blob pattern, long start) throws SQLException {
		try {
			// Serialize the blob by calling its getBytes() method, then call
			// the
			// other position() method - the one that receives a byte array.
			return rmiBlob_.position(
					pattern.getBytes(0, (int) pattern.length()), start);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	// -------------------------- JDBC 3.0 -----------------------------------

	public int setBytes(long pos, byte[] bytes) throws SQLException {
		try {
			return rmiBlob_.setBytes(pos, bytes);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public int setBytes(long pos, byte[] bytes, int offset, int len)
			throws SQLException {
		try {
			return rmiBlob_.setBytes(pos, bytes, offset, len);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public java.io.OutputStream setBinaryStream(long pos) throws SQLException {
		try {
			return rmiBlob_.setBinaryStream(pos);
		} catch (Exception e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public void truncate(long len) throws SQLException {
		try {
			rmiBlob_.truncate(len);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	@Override
	public void free() throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public InputStream getBinaryStream(long pos, long length)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

};
