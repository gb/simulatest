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
import java.util.Map;

/**
 * The mapping in the Java programming language for the SQL type ARRAY. By
 * default, an Array is a transaction duration reference to an SQL array. By
 * default, an Array is implemented using an SQL LOCATOR(array) internally.
 */

public class RJArray implements java.sql.Array, java.io.Serializable {

	private static final long serialVersionUID = -5140375250741324937L;

	private RJArrayInterface rmiArray_;

	public RJArray(RJArrayInterface a) {
		rmiArray_ = a;
	}

	/**
	 * Retrieves the contents of the SQL array designated by this Array object
	 * in the form of an array in the Java programming language.
	 */
	public Object getArray() throws SQLException {
		try {
			return rmiArray_.getArray();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Returns an array containing a slice of the SQL array, beginning with the
	 * specified index and containing up to count successive elements of the SQL
	 * array.
	 */
	public Object getArray(long index, int count) throws SQLException {
		try {
			return rmiArray_.getArray(index, count);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Returns the JDBC type of the elements in the array designated by this
	 * Array object.
	 */
	public int getBaseType() throws SQLException {
		try {
			return rmiArray_.getBaseType();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Returns the SQL type name of the elements in the array designated by this
	 * Array object.
	 */
	public String getBaseTypeName() throws SQLException {
		try {
			return rmiArray_.getBaseTypeName();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Returns a result set that contains the elements of the array designated
	 * by this Array object.
	 */
	public java.sql.ResultSet getResultSet() throws SQLException {
		try {
			return new RJResultSet(rmiArray_.getResultSet(), null);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Returns a result set holding the elements of the subarray that starts at
	 * index index and contains up to count successive elements.
	 */
	public java.sql.ResultSet getResultSet(long index, int count)
			throws SQLException {
		try {
			return new RJResultSet(rmiArray_.getResultSet(index, count), null);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	@Override
	public Object getArray(Map<String, Class<?>> map) throws SQLException {
		try {
			return rmiArray_.getArray(map);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	@Override
	public Object getArray(long index, int count, Map<String, Class<?>> map)
			throws SQLException {
		try {
			return rmiArray_.getArray(index, count, map);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	@Override
	public ResultSet getResultSet(Map<String, Class<?>> map)
			throws SQLException {
		try {
			return new RJResultSet(rmiArray_.getResultSet(map), null);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	@Override
	public ResultSet getResultSet(long index, int count,
			Map<String, Class<?>> map) throws SQLException {
		try {
			return new RJResultSet(rmiArray_.getResultSet(index, count, map),
					null);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	@Override
	public void free() throws SQLException {
		// TODO Auto-generated method stub
	}

};
