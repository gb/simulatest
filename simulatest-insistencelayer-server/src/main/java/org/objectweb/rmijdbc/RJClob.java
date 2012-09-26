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
import java.io.Reader;
import java.rmi.RemoteException;

public class RJClob implements java.sql.Clob, java.io.Serializable {

	private static final long serialVersionUID = 7221327488965425393L;
	
	RJClobInterface rmiClob_;

	public RJClob(RJClobInterface b) {
		rmiClob_ = b;
	}

	// TBD There's a hack there (Reader not serializable)
	public Reader getCharacterStream() throws SQLException {
		try {
			char[] val = rmiClob_.getCharacterStream();
			return RJSerializer.toReader(val);
		} catch (Exception e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public long length() throws SQLException {
		try {
			return rmiClob_.length();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public String getSubString(long pos, int length) throws SQLException {
		try {
			return rmiClob_.getSubString(pos, length);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	// TBD There's a hack there (InputStream not serializable)
	public InputStream getAsciiStream() throws SQLException {
		try {
			byte[] val = rmiClob_.getAsciiStream();
			return RJSerializer.toInputStream(val);
		} catch (Exception e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public long position(String searchstr, long start) throws SQLException {
		try {
			return rmiClob_.position(searchstr, start);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public long position(Clob searchstr, long start) throws SQLException {
		try {
			// Serialize the blob by calling its getBytes() method, then call
			// the
			// other position() method - the one that receives a byte array.
			return rmiClob_.position(
					searchstr.getSubString(0, (int) searchstr.length()), start);
		} catch (Exception e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	// ---------------------------- jdbc 3.0 -----------------------------------
	public int setString(long pos, String str) throws SQLException {
		try {
			return rmiClob_.setString(pos, str);
		} catch (Exception e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public int setString(long pos, String str, int offset, int len)
			throws SQLException {
		try {
			return rmiClob_.setString(pos, str, offset, len);
		} catch (Exception e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public java.io.OutputStream setAsciiStream(long pos) throws SQLException {
		try {
			return rmiClob_.setAsciiStream(pos);
		} catch (Exception e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public java.io.Writer setCharacterStream(long pos) throws SQLException {
		try {
			return rmiClob_.setCharacterStream(pos);
		} catch (Exception e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public void truncate(long len) throws SQLException {
		try {
			rmiClob_.truncate(len);
		} catch (Exception e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	@Override
	public void free() throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public Reader getCharacterStream(long pos, long length) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

};
