
/**
 * RmiJdbc client/server JDBC Driver
 * (C) GIE Dyade (Groupe BULL / INRIA Research Center) 1997
 *
 * @version     1.0
 * @author      Pierre-Yves Gibello (pierreyves.gibello@experlog.com)
 */

package org.objectweb.rmijdbc;

import java.sql.*;
import java.rmi.RemoteException;

import java.util.Calendar;

// TBD: WARNING This file contains a hack for InputStream class...
// InputStream is not serializable, of course !
// The right way would be to encapsulate InputStream in a RMI remote object
// (hope I'll find time to do that)

/**
 * <P>A SQL statement is pre-compiled and stored in a
 * PreparedStatement object. This object can then be used to
 * efficiently execute this statement multiple times. 
 *
 * <P><B>Note:</B> The setXXX methods for setting IN parameter values
 * must specify types that are compatible with the defined SQL type of
 * the input parameter. For instance, if the IN parameter has SQL type
 * Integer then setInt should be used.
 *
 * <p>If arbitrary parameter type conversions are required then the
 * setObject method should be used with a target SQL type.
 *
 * @see Connection#prepareStatement
 * @see ResultSet 
 */

public interface RJPreparedStatementInterface extends RJStatementInterface {

    /**
     * A prepared SQL query is executed and its ResultSet is returned.
     *
     * @return a ResultSet that contains the data produced by the
     * query; never null
     */
  RJResultSetInterface executeQuery() throws RemoteException, SQLException;

    /**
     * Execute a SQL INSERT, UPDATE or DELETE statement. In addition,
     * SQL statements that return nothing such as SQL DDL statements
     * can be executed.
     *
     * @return either the row count for INSERT, UPDATE or DELETE; or 0
     * for SQL statements that return nothing
     */
  int executeUpdate() throws RemoteException, SQLException;

    /**
     * Set a parameter to SQL NULL.
     *
     * <P><B>Note:</B> You must specify the parameter's SQL type.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param sqlType SQL type code defined by java.sql.Types
     */
  void setNull(int parameterIndex, int sqlType) throws RemoteException, SQLException;

    /**
     * Set a parameter to a Java boolean value.  The driver converts this
     * to a SQL BIT value when it sends it to the database.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x the parameter value
     */
  void setBoolean(int parameterIndex, boolean x)
  throws RemoteException, SQLException;

    /**
     * Set a parameter to a Java byte value.  The driver converts this
     * to a SQL TINYINT value when it sends it to the database.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x the parameter value
     */
    void setByte(int parameterIndex, byte x) throws RemoteException, SQLException;

    /**
     * Set a parameter to a Java short value.  The driver converts this
     * to a SQL SMALLINT value when it sends it to the database.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x the parameter value
     */
    void setShort(int parameterIndex, short x) throws RemoteException, SQLException;

    /**
     * Set a parameter to a Java int value.  The driver converts this
     * to a SQL INTEGER value when it sends it to the database.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x the parameter value
     */
    void setInt(int parameterIndex, int x) throws RemoteException, SQLException;

    /**
     * Set a parameter to a Java long value.  The driver converts this
     * to a SQL BIGINT value when it sends it to the database.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x the parameter value
     */
    void setLong(int parameterIndex, long x) throws RemoteException, SQLException;

    /**
     * Set a parameter to a Java float value.  The driver converts this
     * to a SQL FLOAT value when it sends it to the database.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x the parameter value
     */
    void setFloat(int parameterIndex, float x) throws RemoteException, SQLException;

    /**
     * Set a parameter to a Java double value.  The driver converts this
     * to a SQL DOUBLE value when it sends it to the database.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x the parameter value
     */
  void setDouble(int parameterIndex, double x) throws RemoteException, SQLException;

    /**
     * Set a parameter to a java.lang.BigDecimal value.  The driver converts
     * this to a SQL NUMERIC value when it sends it to the database.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x the parameter value
     */
  void setBigDecimal(int parameterIndex, java.math.BigDecimal x)
  throws RemoteException, SQLException;

    /**
     * Set a parameter to a Java String value.  The driver converts this
     * to a SQL VARCHAR or LONGVARCHAR value (depending on the arguments
     * size relative to the driver's limits on VARCHARs) when it sends
     * it to the database.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x the parameter value
     */
  void setString(int parameterIndex, String x) throws RemoteException, SQLException;

    /**
     * Set a parameter to a Java array of bytes.  The driver converts
     * this to a SQL VARBINARY or LONGVARBINARY (depending on the
     * argument's size relative to the driver's limits on VARBINARYs)
     * when it sends it to the database.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x the parameter value 
     */
  void setBytes(int parameterIndex, byte x[]) throws RemoteException, SQLException;

    /**
     * Set a parameter to a java.sql.Date value.  The driver converts this
     * to a SQL DATE value when it sends it to the database.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x the parameter value
     */
  void setDate(int parameterIndex, java.sql.Date x)
  throws RemoteException, SQLException;

    /**
     * Set a parameter to a java.sql.Time value.  The driver converts this
     * to a SQL TIME value when it sends it to the database.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x the parameter value
     */
  void setTime(int parameterIndex, java.sql.Time x) 
  throws RemoteException, SQLException;

    /**
     * Set a parameter to a java.sql.Timestamp value.  The driver
     * converts this to a SQL TIMESTAMP value when it sends it to the
     * database.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x the parameter value 
     */
  void setTimestamp(int parameterIndex, java.sql.Timestamp x)
  throws RemoteException, SQLException;

    /**
     * When a very large ASCII value is input to a LONGVARCHAR
     * parameter, it may be more practical to send it via a
     * java.io.InputStream. JDBC will read the data from the stream
     * as needed, until it reaches end-of-file.  The JDBC driver will
     * do any necessary conversion from ASCII to the database char format.
     * 
     * <P><B>Note:</B> This stream object can either be a standard
     * Java stream object or your own subclass that implements the
     * standard interface.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x the java input stream which contains the ASCII parameter value
     * @param length the number of bytes in the stream 
     */
//TBD  This is a hack (InputStream not serializable)
//  void setAsciiStream(int parameterIndex, java.io.InputStream x,
  void setAsciiStream(int parameterIndex, byte[] x,
  int length) throws RemoteException, SQLException;

    /**
     * When a very large UNICODE value is input to a LONGVARCHAR
     * parameter, it may be more practical to send it via a
     * java.io.InputStream. JDBC will read the data from the stream
     * as needed, until it reaches end-of-file.  The JDBC driver will
     * do any necessary conversion from UNICODE to the database char format.
     * 
     * <P><B>Note:</B> This stream object can either be a standard
     * Java stream object or your own subclass that implements the
     * standard interface.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...  
     * @param x the java input stream which contains the
     * UNICODE parameter value 
     * @param length the number of bytes in the stream 
     */
//TBD  This is a hack (InputStream not serializable)
//  void setUnicodeStream(int parameterIndex, java.io.InputStream x,
  void setUnicodeStream(int parameterIndex, byte[] x,
  int length) throws RemoteException, SQLException;

    /**
     * When a very large binary value is input to a LONGVARBINARY
     * parameter, it may be more practical to send it via a
     * java.io.InputStream. JDBC will read the data from the stream
     * as needed, until it reaches end-of-file.
     * 
     * <P><B>Note:</B> This stream object can either be a standard
     * Java stream object or your own subclass that implements the
     * standard interface.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x the java input stream which contains the binary parameter value
     * @param length the number of bytes in the stream 
     */
//TBD  This is a hack (InputStream not serializable)
//  void setBinaryStream(int parameterIndex, java.io.InputStream x,
  void setBinaryStream(int parameterIndex, byte[] x,
  int length) throws RemoteException, SQLException;

    /**
     * <P>In general, parameter values remain in force for repeated use of a
     * Statement. Setting a parameter value automatically clears its
     * previous value.  However, in some cases it is useful to immediately
     * release the resources used by the current parameter values; this can
     * be done by calling clearParameters.
     */
  void clearParameters() throws RemoteException, SQLException;

  //----------------------------------------------------------------------
  // Advanced features:

  /**
   * <p>Set the value of a parameter using an object; use the
   * java.lang equivalent objects for integral values.
   *
   * <p>The given Java object will be converted to the targetSqlType
   * before being sent to the database.
   *
   * <p>Note that this method may be used to pass datatabase-
   * specific abstract data types. This is done by using a Driver-
   * specific Java type and using a targetSqlType of
   * java.sql.types.OTHER.
   *
   * @param parameterIndex The first parameter is 1, the second is 2, ...
   * @param x The object containing the input parameter value
   * @param targetSqlType The SQL type (as defined in java.sql.Types) to be 
   * sent to the database. The scale argument may further qualify this type.
   * @param scale For java.sql.Types.DECIMAL or java.sql.Types.NUMERIC types
   *          this is the number of digits after the decimal.  For all other
   *          types this value will be ignored,
   * @see Types 
   */
  void setObject(int parameterIndex, Object x, int targetSqlType,
  int scale) throws RemoteException, SQLException;

  /**
   * This method is like setObject above, but assumes a scale of zero.
   */
  void setObject(int parameterIndex, Object x, int targetSqlType)
  throws RemoteException, SQLException;

  /**
   * <p>Set the value of a parameter using an object; use the
   * java.lang equivalent objects for integral values.
   *
   * <p>The JDBC specification specifies a standard mapping from
   * Java Object types to SQL types.  The given argument java object
   * will be converted to the corresponding SQL type before being
   * sent to the database.
   *
   * <p>Note that this method may be used to pass datatabase
   * specific abstract data types, by using a Driver specific Java
   * type.
   *
   * @param parameterIndex The first parameter is 1, the second is 2, ...
   * @param x The object containing the input parameter value 
   */
  void setObject(int parameterIndex, Object x) throws RemoteException, SQLException;

  /**
   * Some prepared statements return multiple results; the execute
   * method handles these complex statements as well as the simpler
   * form of statements handled by executeQuery and executeUpdate.
   *
   * @see Statement#execute
   */
  boolean execute() throws RemoteException, SQLException;
    //--------------------------JDBC 2.0-----------------------------

    /**
     * JDBC 2.0
     *
     * Adds a set of parameters to the batch.
     * 
     * @exception SQLException if a database access error occurs
     * @see Statement#addBatch
     */
    void addBatch() throws java.rmi.RemoteException, SQLException;

    /**
     * JDBC 2.0
     *
	 * Sets the designated parameter to the given <code>Reader</code>
	 * object, which is the given number of characters long.
     * When a very large UNICODE value is input to a LONGVARCHAR
     * parameter, it may be more practical to send it via a
     * java.io.Reader. JDBC will read the data from the stream
     * as needed, until it reaches end-of-file.  The JDBC driver will
     * do any necessary conversion from UNICODE to the database char format.
     * 
     * <P><B>Note:</B> This stream object can either be a standard
     * Java stream object or your own subclass that implements the
     * standard interface.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x the java reader which contains the UNICODE data
     * @param length the number of characters in the stream 
     * @exception SQLException if a database access error occurs
     */
    void setCharacterStream(int parameterIndex,
       			  java.io.Reader reader,
			  int length) throws java.rmi.RemoteException, SQLException;
    // TBD The following hack allows to transfer the reader's content as a char
    // array... not optimal, but readers are nor serializable!
    void setCharacterStream(int parameterIndex,
       			  char serialized[],
			  int length) throws java.rmi.RemoteException, SQLException;

    /**
     * JDBC 2.0
     *
     * Sets a REF(&lt;structured-type&gt;) parameter.
     *
     * @param i the first parameter is 1, the second is 2, ...
     * @param x an object representing data of an SQL REF Type
     * @exception SQLException if a database access error occurs
     */
    void setRef (int i, Ref x) throws java.rmi.RemoteException, SQLException;

    /**
     * JDBC 2.0
     *
     * Sets a BLOB parameter.
     *
     * @param i the first parameter is 1, the second is 2, ...
     * @param x an object representing a BLOB
     * @exception SQLException if a database access error occurs
     */
    void setBlob (int i, Blob x) throws java.rmi.RemoteException, SQLException;

    /**
     * JDBC 2.0
     *
     * Sets a CLOB parameter.
     *
     * @param i the first parameter is 1, the second is 2, ...
     * @param x an object representing a CLOB
     * @exception SQLException if a database access error occurs
     */
    void setClob (int i, Clob x) throws java.rmi.RemoteException, SQLException;

    /**
     * JDBC 2.0
     *
     * Sets an Array parameter.
     *
     * @param i the first parameter is 1, the second is 2, ...
     * @param x an object representing an SQL array
     * @exception SQLException if a database access error occurs
     */
    void setArray (int i, Array x) throws java.rmi.RemoteException, SQLException;

    /**
     * JDBC 2.0
     *
     * Gets the number, types and properties of a ResultSet's columns.
     *
     * @return the description of a ResultSet's columns
     * @exception SQLException if a database access error occurs
     */
    RJResultSetMetaDataInterface getMetaData()
    throws java.rmi.RemoteException, SQLException;

    /**
     * JDBC 2.0
     *
     * Sets the designated parameter to a java.sql.Date value,
	 * using the given <code>Calendar</code> object.  The driver uses
	 * the <code>Calendar</code> object to construct an SQL DATE,
	 * which the driver then sends to the database.  With a
	 * a <code>Calendar</code> object, the driver can calculate the date
	 * taking into account a custom timezone and locale.  If no
	 * <code>Calendar</code> object is specified, the driver uses the default
	 * timezone and locale.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x the parameter value
	 * @param cal the <code>Calendar</code> object the driver will use
	 *            to construct the date
     * @exception SQLException if a database access error occurs
     */
    void setDate(int parameterIndex, java.sql.Date x, Calendar cal)
	    throws java.rmi.RemoteException, SQLException;

    /**
     * JDBC 2.0
     *
     * Sets the designated parameter to a java.sql.Time value,
	 * using the given <code>Calendar</code> object.  The driver uses
	 * the <code>Calendar</code> object to construct an SQL TIME,
	 * which the driver then sends to the database.  With a
	 * a <code>Calendar</code> object, the driver can calculate the time
	 * taking into account a custom timezone and locale.  If no
	 * <code>Calendar</code> object is specified, the driver uses the default
	 * timezone and locale.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x the parameter value
	 * @param cal the <code>Calendar</code> object the driver will use
	 *            to construct the time
     * @exception SQLException if a database access error occurs
     */
    void setTime(int parameterIndex, java.sql.Time x, Calendar cal) 
	    throws java.rmi.RemoteException, SQLException;

    /**
     * JDBC 2.0
     *
     * Sets the designated parameter to a java.sql.Timestamp value,
	 * using the given <code>Calendar</code> object.  The driver uses
	 * the <code>Calendar</code> object to construct an SQL TIMESTAMP,
	 * which the driver then sends to the database.  With a
	 * a <code>Calendar</code> object, the driver can calculate the timestamp
	 * taking into account a custom timezone and locale.  If no
	 * <code>Calendar</code> object is specified, the driver uses the default
	 * timezone and locale.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x the parameter value 
	 * @param cal the <code>Calendar</code> object the driver will use
	 *            to construct the timestamp
     * @exception SQLException if a database access error occurs
     */
    void setTimestamp(int parameterIndex, java.sql.Timestamp x, Calendar cal)
	    throws java.rmi.RemoteException, SQLException;

    /**
     * JDBC 2.0
     *
     * Sets the designated parameter to SQL NULL.  This version of setNull should
     * be used for user-named types and REF type parameters.  Examples
     * of user-named types include: STRUCT, DISTINCT, JAVA_OBJECT, and 
     * named array types.
     *
     * <P><B>Note:</B> To be portable, applications must give the
     * SQL type code and the fully-qualified SQL type name when specifying
     * a NULL user-defined or REF parameter.  In the case of a user-named type 
     * the name is the type name of the parameter itself.  For a REF 
     * parameter the name is the type name of the referenced type.  If 
     * a JDBC driver does not need the type code or type name information, 
     * it may ignore it.     
     *
     * Although it is intended for user-named and Ref parameters,
     * this method may be used to set a null parameter of any JDBC type.
     * If the parameter does not have a user-named or REF type, the given
     * typeName is ignored.
     *
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param sqlType a value from java.sql.Types
     * @param typeName the fully-qualified name of an SQL user-named type,
     *  ignored if the parameter is not a user-named type or REF 
     * @exception SQLException if a database access error occurs
     */
  void setNull (int paramIndex, int sqlType, String typeName) 
    throws java.rmi.RemoteException, SQLException;

    //------------------------- JDBC 3.0 -----------------------------------

    /**
     * Sets the designated parameter to the given <code>java.net.URL</code> value. 
     * The driver converts this to an SQL <code>DATALINK</code> value
     * when it sends it to the database.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x the <code>java.net.URL</code> object to be set
     * @exception SQLException if a database access error occurs
     * @since 1.4
     */ 
    void setURL(int parameterIndex, java.net.URL x)
    throws java.rmi.RemoteException, SQLException;

    /**
     * Retrieves the number, types and properties of this 
     * <code>PreparedStatement</code> object's parameters.
     *
     * @return a <code>ParameterMetaData</code> object that contains information
     *         about the number, types and properties of this 
     *         <code>PreparedStatement</code> object's parameters
     * @exception SQLException if a database access error occurs
     * @see ParameterMetaData
     * @since 1.4
     */
    RJParameterMetaDataInterface getParameterMetaData()
    throws java.rmi.RemoteException, SQLException;
  
};

