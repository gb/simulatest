
/**
 * RmiJdbc client/server JDBC Driver
 * (C) GIE Dyade (Groupe BULL / INRIA Research Center) 1997
 *
 * @version     1.0
 * @author      Pierre-Yves Gibello (pierreyves.gibello@experlog.com)
 */

package org.objectweb.rmijdbc;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;

/**
 * A ResultSetMetaData object can be used to find out about the types 
 * and properties of the columns in a ResultSet.
 */

public interface RJResultSetMetaDataInterface extends Remote {

  /**
   * What's the number of columns in the ResultSet?
   *
   * @return the number
   */
  int getColumnCount() throws RemoteException, SQLException;

  /**
   * Is the column automatically numbered, thus read-only?
   *
   * @param column the first column is 1, the second is 2, ...
   * @return true if so
   */
  boolean isAutoIncrement(int column) throws RemoteException, SQLException;

  /**
   * Does a column's case matter?
   *
   * @param column the first column is 1, the second is 2, ...
   * @return true if so
   */
  boolean isCaseSensitive(int column) throws RemoteException, SQLException;

  /**
   * Can the column be used in a where clause?
   *
   * @param column the first column is 1, the second is 2, ...
   * @return true if so
   */
  boolean isSearchable(int column) throws RemoteException, SQLException;

  /**
   * Is the column a cash value?
   *
   * @param column the first column is 1, the second is 2, ...
   * @return true if so
   */
  boolean isCurrency(int column) throws RemoteException, SQLException;

  /**
   * Can you put a NULL in this column?		
   *
   * @param column the first column is 1, the second is 2, ...
   * @return columnNoNulls, columnNullable or columnNullableUnknown
   */
  int isNullable(int column) throws RemoteException, SQLException;

  /**
   * Is the column a signed number?
   *
   * @param column the first column is 1, the second is 2, ...
   * @return true if so
   */
  boolean isSigned(int column) throws RemoteException, SQLException;

  /**
   * What's the column's normal max width in chars?
   *
   * @param column the first column is 1, the second is 2, ...
   * @return max width
   */
  int getColumnDisplaySize(int column) throws RemoteException, SQLException;

  /**
   * What's the suggested column title for use in printouts and
   * displays?
   *
   * @param column the first column is 1, the second is 2, ...
   * @return true if so 
   */
  String getColumnLabel(int column) throws RemoteException, SQLException;	

  /**
   * What's a column's name?
   *
   * @param column the first column is 1, the second is 2, ...
   * @return column name
   */
  String getColumnName(int column) throws RemoteException, SQLException;

  /**
   * What's a column's table's schema?
   *
   * @param column the first column is 1, the second is 2, ...
   * @return schema name or "" if not applicable
   */
  String getSchemaName(int column) throws RemoteException, SQLException;

  /**
   * What's a column's number of decimal digits?
   *
   * @param column the first column is 1, the second is 2, ...
   * @return precision
   */
  int getPrecision(int column) throws RemoteException, SQLException;

  /**
   * What's a column's number of digits to right of the decimal point?
   *
   * @param column the first column is 1, the second is 2, ...
   * @return scale
   */
  int getScale(int column) throws RemoteException, SQLException;	

  /**
   * What's a column's table name? 
   *
   * @return table name or "" if not applicable
   */
  String getTableName(int column) throws RemoteException, SQLException;

  /**
   * What's a column's table's catalog name?
   *
   * @param column the first column is 1, the second is 2, ...
   * @return column name or "" if not applicable.
   */
  String getCatalogName(int column) throws RemoteException, SQLException;

  /**
   * What's a column's SQL type?
   *
   * @param column the first column is 1, the second is 2, ...
   * @return SQL type
   * @see Types
   */
  int getColumnType(int column) throws RemoteException, SQLException;

  /**
   * What's a column's data source specific type name?
   *
   * @param column the first column is 1, the second is 2, ...
   * @return type name
   */
  String getColumnTypeName(int column) throws RemoteException, SQLException;

  /**
   * Is a column definitely not writable?
   *
   * @param column the first column is 1, the second is 2, ...
   * @return true if so
   */
  boolean isReadOnly(int column) throws RemoteException, SQLException;

  /**
   * Is it possible for a write on the column to succeed?
   *
   * @param column the first column is 1, the second is 2, ...
   * @return true if so
   */
  boolean isWritable(int column) throws RemoteException, SQLException;

  /**
   * Will a write on the column definitely succeed?	
   *
   * @param column the first column is 1, the second is 2, ...
   * @return true if so
   */
  boolean isDefinitelyWritable(int column) throws RemoteException, SQLException;

  //--------------------------JDBC 2.0-----------------------------------

/**
 * JDBC 2.0
 *
 * <p>Returns the fully-qualified name of the Java class whose instances 
 * are manufactured if the method <code>ResultSet.getObject</code>
 * is called to retrieve a value 
 * from the column.  <code>ResultSet.getObject</code> may return a subclass of the
 * class returned by this method.
 *
 * @return the fully-qualified name of the class in the Java programming
 *         language that would be used by the method 
 * <code>ResultSet.getObject</code> to retrieve the value in the specified
 * column. This is the class name used for custom mapping.
 * @exception SQLException if a database access error occurs
 */
String getColumnClassName(int column) throws RemoteException, SQLException;

};

