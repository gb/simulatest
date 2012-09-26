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

public interface RJArrayInterface extends java.rmi.Remote {
	/**
	 * Retrieves the contents of the SQL array designated by this Array object
	 * in the form of an array in the Java programming language.
	 */
	Object getArray() throws RemoteException, SQLException;

	/**
	 * Returns an array containing a slice of the SQL array, beginning with the
	 * specified index and containing up to count successive elements of the SQL
	 * array.
	 */
	Object getArray(long index, int count) throws RemoteException, SQLException;

	/**
	 * Returns an array containing a slice of the SQL array object designated by
	 * this object, beginning with the specified index and containing up to
	 * count successive elements of the SQL array.
	 */
	Object getArray(long index, int count, Map<String,Class<?>> map) throws RemoteException,
			SQLException;

	/**
	 * Retrieves the contents of the SQL array designated by this Array object,
	 * using the specified map for type map customizations.
	 */
	Object getArray(Map<String,Class<?>> map) throws RemoteException, SQLException;

	/**
	 * Returns the JDBC type of the elements in the array designated by this
	 * Array object.
	 */
	int getBaseType() throws RemoteException, SQLException;

	/**
	 * Returns the SQL type name of the elements in the array designated by this
	 * Array object.
	 */
	String getBaseTypeName() throws RemoteException, SQLException;

	/**
	 * Returns a result set that contains the elements of the array designated
	 * by this Array object.
	 */
	RJResultSetInterface getResultSet() throws RemoteException, SQLException;

	/**
	 * Returns a result set holding the elements of the subarray that starts at
	 * index index and contains up to count successive elements.
	 */
	RJResultSetInterface getResultSet(long index, int count)
			throws RemoteException, SQLException;

	/**
	 * Returns a result set holding the elements of the subarray that starts at
	 * index index and contains up to count successive elements.
	 */
	RJResultSetInterface getResultSet(long index, int count, Map<String,Class<?>> map)
			throws RemoteException, SQLException;

	/**
	 * Returns a result set that contains the elements of the array designated
	 * by this Array object and uses the given map to map the array elements.
	 */
	RJResultSetInterface getResultSet(Map<String,Class<?>> map) throws RemoteException,
			SQLException;

};
