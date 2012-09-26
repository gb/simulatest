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
import java.util.Map;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.server.Unreferenced;

/**
 * The mapping in the Java programming language for the SQL type ARRAY. By
 * default, an Array is a transaction duration reference to an SQL array. By
 * default, an Array is implemented using an SQL LOCATOR(array) internally.
 */

public class RJArrayServer extends UnicastRemoteObject implements
		RJArrayInterface, Unreferenced {

	private static final long serialVersionUID = -8767803729427304605L;

	java.sql.Array jdbcArray_;

	public RJArrayServer(java.sql.Array jdbcArray) throws RemoteException {
		super(RMIRepository.rmiJdbcListenerPort,
				RMIRepository.rmiClientSocketFactory,
				RMIRepository.rmiServerSocketFactory);
		jdbcArray_ = jdbcArray;
	}

	public void unreferenced() {
		Runtime.getRuntime().gc();
	}

	/**
	 * Retrieves the contents of the SQL array designated by this Array object
	 * in the form of an array in the Java programming language.
	 */
	public Object getArray() throws RemoteException, SQLException {
		return jdbcArray_.getArray();
	}

	/**
	 * Returns an array containing a slice of the SQL array, beginning with the
	 * specified index and containing up to count successive elements of the SQL
	 * array.
	 */
	public Object getArray(long index, int count) throws RemoteException,
			SQLException {
		return jdbcArray_.getArray(index, count);
	}

	/**
	 * Returns an array containing a slice of the SQL array object designated by
	 * this object, beginning with the specified index and containing up to
	 * count successive elements of the SQL array.
	 */
	public Object getArray(long index, int count, Map<String, Class<?>> map)
			throws RemoteException, SQLException {
		return jdbcArray_.getArray(index, count, map);
	}

	/**
	 * Retrieves the contents of the SQL array designated by this Array object,
	 * using the specified map for type map customizations.
	 */
	public Object getArray(Map<String, Class<?>> map) throws RemoteException,
			SQLException {
		return jdbcArray_.getArray(map);
	}

	/**
	 * Returns the JDBC type of the elements in the array designated by this
	 * Array object.
	 */
	public int getBaseType() throws RemoteException, SQLException {
		return jdbcArray_.getBaseType();
	}

	/**
	 * Returns the SQL type name of the elements in the array designated by this
	 * Array object.
	 */
	public String getBaseTypeName() throws RemoteException, SQLException {
		return jdbcArray_.getBaseTypeName();
	}

	/**
	 * Returns a result set that contains the elements of the array designated
	 * by this Array object.
	 */
	public RJResultSetInterface getResultSet() throws RemoteException,
			SQLException {
		return new RJResultSetServer(jdbcArray_.getResultSet());
	}

	/**
	 * Returns a result set holding the elements of the subarray that starts at
	 * index index and contains up to count successive elements.
	 */
	public RJResultSetInterface getResultSet(long index, int count)
			throws RemoteException, SQLException {
		return new RJResultSetServer(jdbcArray_.getResultSet(index, count));
	}

	/**
	 * Returns a result set holding the elements of the subarray that starts at
	 * index index and contains up to count successive elements.
	 */
	public RJResultSetInterface getResultSet(long index, int count,
			Map<String, Class<?>> map) throws RemoteException, SQLException {
		return new RJResultSetServer(jdbcArray_.getResultSet(index, count, map));
	}

	/**
	 * Returns a result set that contains the elements of the array designated
	 * by this Array object and uses the given map to map the array elements.
	 */
	public RJResultSetInterface getResultSet(Map<String, Class<?>> map)
			throws RemoteException, SQLException {
		return new RJResultSetServer(jdbcArray_.getResultSet(map));
	}

};
