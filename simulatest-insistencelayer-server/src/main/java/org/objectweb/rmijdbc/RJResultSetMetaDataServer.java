/**
 * RmiJdbc client/server JDBC Driver
 * (C) GIE Dyade (Groupe BULL / INRIA Research Center) 1997
 *
 * @version     1.0
 * @author      Pierre-Yves Gibello (pierreyves.gibello@experlog.com)
 *              Additional SSL Support
 *              Douglas Hammond(djhammond@sympatico.ca)
 */

package org.objectweb.rmijdbc;

import java.rmi.*;
import java.sql.SQLException;
import java.rmi.server.Unreferenced;

/**
 * A ResultSetMetaData object can be used to find out about the types and
 * properties of the columns in a ResultSet.
 */

public class RJResultSetMetaDataServer extends
		java.rmi.server.UnicastRemoteObject implements
		RJResultSetMetaDataInterface, Unreferenced {

	private static final long serialVersionUID = -4184420457589132652L;

	java.sql.ResultSetMetaData metadata_;

	public RJResultSetMetaDataServer(java.sql.ResultSetMetaData md)
			throws RemoteException {
		super(RMIRepository.rmiJdbcListenerPort,
				RMIRepository.rmiClientSocketFactory,
				RMIRepository.rmiServerSocketFactory);
		metadata_ = md;
	}

	public void unreferenced() {
		Runtime.getRuntime().gc();
	}

	/**
	 * What's the number of columns in the ResultSet?
	 * 
	 * @return the number
	 */
	public int getColumnCount() throws RemoteException, SQLException {
		return metadata_.getColumnCount();
	}

	/**
	 * Is the column automatically numbered, thus read-only?
	 * 
	 * @param column
	 *            the first column is 1, the second is 2, ...
	 * @return true if so
	 */
	public boolean isAutoIncrement(int column) throws RemoteException,
			SQLException {
		return metadata_.isAutoIncrement(column);
	}

	/**
	 * Does a column's case matter?
	 * 
	 * @param column
	 *            the first column is 1, the second is 2, ...
	 * @return true if so
	 */
	public boolean isCaseSensitive(int column) throws RemoteException,
			SQLException {
		return metadata_.isCaseSensitive(column);
	}

	/**
	 * Can the column be used in a where clause?
	 * 
	 * @param column
	 *            the first column is 1, the second is 2, ...
	 * @return true if so
	 */
	public boolean isSearchable(int column) throws RemoteException,
			SQLException {
		return metadata_.isSearchable(column);
	}

	/**
	 * Is the column a cash value?
	 * 
	 * @param column
	 *            the first column is 1, the second is 2, ...
	 * @return true if so
	 */
	public boolean isCurrency(int column) throws RemoteException, SQLException {
		return metadata_.isCurrency(column);
	}

	/**
	 * Can you put a NULL in this column?
	 * 
	 * @param column
	 *            the first column is 1, the second is 2, ...
	 * @return columnNoNulls, columnNullable or columnNullableUnknown
	 */
	public int isNullable(int column) throws RemoteException, SQLException {
		return metadata_.isNullable(column);
	}

	/**
	 * Is the column a signed number?
	 * 
	 * @param column
	 *            the first column is 1, the second is 2, ...
	 * @return true if so
	 */
	public boolean isSigned(int column) throws RemoteException, SQLException {
		return metadata_.isSigned(column);
	}

	/**
	 * What's the column's normal max width in chars?
	 * 
	 * @param column
	 *            the first column is 1, the second is 2, ...
	 * @return max width
	 */
	public int getColumnDisplaySize(int column) throws RemoteException,
			SQLException {
		return metadata_.getColumnDisplaySize(column);
	}

	/**
	 * What's the suggested column title for use in printouts and displays?
	 * 
	 * @param column
	 *            the first column is 1, the second is 2, ...
	 * @return true if so
	 */
	public String getColumnLabel(int column) throws RemoteException,
			SQLException {
		return metadata_.getColumnLabel(column);
	}

	/**
	 * What's a column's name?
	 * 
	 * @param column
	 *            the first column is 1, the second is 2, ...
	 * @return column name
	 */
	public String getColumnName(int column) throws RemoteException,
			SQLException {
		return metadata_.getColumnName(column);
	}

	/**
	 * What's a column's table's schema?
	 * 
	 * @param column
	 *            the first column is 1, the second is 2, ...
	 * @return schema name or "" if not applicable
	 */
	public String getSchemaName(int column) throws RemoteException,
			SQLException {
		return metadata_.getSchemaName(column);
	}

	/**
	 * What's a column's number of decimal digits?
	 * 
	 * @param column
	 *            the first column is 1, the second is 2, ...
	 * @return precision
	 */
	public int getPrecision(int column) throws RemoteException, SQLException {
		return metadata_.getPrecision(column);
	}

	/**
	 * What's a column's number of digits to right of the decimal point?
	 * 
	 * @param column
	 *            the first column is 1, the second is 2, ...
	 * @return scale
	 */
	public int getScale(int column) throws RemoteException, SQLException {
		return metadata_.getScale(column);
	}

	/**
	 * What's a column's table name?
	 * 
	 * @return table name or "" if not applicable
	 */
	public String getTableName(int column) throws RemoteException, SQLException {
		return metadata_.getTableName(column);
	}

	/**
	 * What's a column's table's catalog name?
	 * 
	 * @param column
	 *            the first column is 1, the second is 2, ...
	 * @return column name or "" if not applicable.
	 */
	public String getCatalogName(int column) throws RemoteException,
			SQLException {
		return metadata_.getCatalogName(column);
	}

	/**
	 * What's a column's SQL type?
	 * 
	 * @param column
	 *            the first column is 1, the second is 2, ...
	 * @return SQL type
	 * @see Types
	 */
	public int getColumnType(int column) throws RemoteException, SQLException {
		return metadata_.getColumnType(column);
	}

	/**
	 * What's a column's data source specific type name?
	 * 
	 * @param column
	 *            the first column is 1, the second is 2, ...
	 * @return type name
	 */
	public String getColumnTypeName(int column) throws RemoteException,
			SQLException {
		return metadata_.getColumnTypeName(column);
	}

	/**
	 * Is a column definitely not writable?
	 * 
	 * @param column
	 *            the first column is 1, the second is 2, ...
	 * @return true if so
	 */
	public boolean isReadOnly(int column) throws RemoteException, SQLException {
		return metadata_.isReadOnly(column);
	}

	/**
	 * Is it possible for a write on the column to succeed?
	 * 
	 * @param column
	 *            the first column is 1, the second is 2, ...
	 * @return true if so
	 */
	public boolean isWritable(int column) throws RemoteException, SQLException {
		return metadata_.isWritable(column);
	}

	/**
	 * Will a write on the column definitely succeed?
	 * 
	 * @param column
	 *            the first column is 1, the second is 2, ...
	 * @return true if so
	 */
	public boolean isDefinitelyWritable(int column) throws RemoteException,
			SQLException {
		return metadata_.isDefinitelyWritable(column);
	}

	// JDBC 2.0 methods

	public String getColumnClassName(int column) throws RemoteException,
			SQLException {
		return metadata_.getColumnClassName(column);
	}

};
