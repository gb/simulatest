
/**
 * RmiJdbc client/server JDBC Driver
 * (C) GIE Dyade (Groupe BULL / INRIA Research Center) 1997
 *
 * @version     1.0
 * @author      Pierre-Yves Gibello (pierreyves.gibello@experlog.com)
 */

package org.objectweb.rmijdbc;

import java.sql.*;
import java.math.BigDecimal;
import java.util.Calendar;

/**
 * <P>CallableStatement is used to execute SQL stored procedures.
 *
 * <P>JDBC provides a stored procedure SQL escape that allows stored
 * procedures to be called in a standard way for all RDBMS's. This
 * escape syntax has one form that includes a result parameter and one
 * that does not. If used, the result parameter must be registered as
 * an OUT parameter. The other parameters may be used for input,
 * output or both. Parameters are refered to sequentially, by
 * number. The first parameter is 1.
 *
 * <P><CODE>
 * {?= call <procedure-name>[<arg1>,<arg2>, ...]}<BR>
 * {call <procedure-name>[<arg1>,<arg2>, ...]}
 * </CODE>
 *    
 * <P>IN parameter values are set using the set methods inherited from
 * PreparedStatement. The type of all OUT parameters must be
 * registered prior to executing the stored procedure; their values
 * are retrieved after execution via the get methods provided here.
 *
 * <P>A Callable statement may return a ResultSet or multiple
 * ResultSets. Multiple ResultSets are handled using operations
 * inherited from Statement.
 *
 * <P>For maximum portability, a call's ResultSets and update counts
 * should be processed prior to getting the values of output
 * parameters.
 *
 * @see Connection#prepareCall
 * @see ResultSet 
 */
public interface RJCallableStatementInterface
extends RJPreparedStatementInterface {

  /**
   * Before executing a stored procedure call, you must explicitly
   * call registerOutParameter to register the java.sql.Type of each
   * out parameter.
   *
   * <P><B>Note:</B> When reading the value of an out parameter, you
   * must use the getXXX method whose Java type XXX corresponds to the
   * parameter's registered SQL type.
   *
   * @param parameterIndex the first parameter is 1, the second is 2,...
   * @param sqlType SQL type code defined by java.sql.Types;
   * for parameters of type Numeric or Decimal use the version of
   * registerOutParameter that accepts a scale value
   * @see Type 
   */
  void registerOutParameter(int parameterIndex, int sqlType)
  throws java.rmi.RemoteException, SQLException;

  /**
   * Use this version of registerOutParameter for registering
   * Numeric or Decimal out parameters.
   *
   * <P><B>Note:</B> When reading the value of an out parameter, you
   * must use the getXXX method whose Java type XXX corresponds to the
   * parameter's registered SQL type.
   *
   * @param parameterIndex the first parameter is 1, the second is 2, ...
   * @param sqlType use either java.sql.Type.NUMERIC or java.sql.Type.DECIMAL
   * @param scale a value greater than or equal to zero representing the 
   *              desired number of digits to the right of the decimal point
   * @see Type 
   */
  void registerOutParameter(int parameterIndex, int sqlType, int scale)
  throws java.rmi.RemoteException, SQLException;

  /**
   * An OUT parameter may have the value of SQL NULL; wasNull reports 
   * whether the last value read has this special value.
   *
   * <P><B>Note:</B> You must first call getXXX on a parameter to
   * read its value and then call wasNull() to see if the value was
   * SQL NULL.
   *
   * @return true if the last parameter read was SQL NULL 
   */
  boolean wasNull() throws java.rmi.RemoteException, SQLException;

  /**
   * Get the value of a CHAR, VARCHAR, or LONGVARCHAR parameter as a Java String.
   *
   * @param parameterIndex the first parameter is 1, the second is 2, ...
   * @return the parameter value; if the value is SQL NULL, the result is null
   */
  String getString(int parameterIndex)
  throws java.rmi.RemoteException, SQLException;

  /**
   * Get the value of a BIT parameter as a Java boolean.
   *
   * @param parameterIndex the first parameter is 1, the second is 2, ...
   * @return the parameter value; if the value is SQL NULL, the result is false
   */
  boolean getBoolean(int parameterIndex)
  throws java.rmi.RemoteException, SQLException;

  /**
   * Get the value of a TINYINT parameter as a Java byte.
   *
   * @param parameterIndex the first parameter is 1, the second is 2, ...
   * @return the parameter value; if the value is SQL NULL, the result is 0
   */
  byte getByte(int parameterIndex)
  throws java.rmi.RemoteException, SQLException;

  /**
   * Get the value of a SMALLINT parameter as a Java short.
   *
   * @param parameterIndex the first parameter is 1, the second is 2, ...
   * @return the parameter value; if the value is SQL NULL, the result is 0
   */
  short getShort(int parameterIndex)
  throws java.rmi.RemoteException, SQLException;

  /**
   * Get the value of an INTEGER parameter as a Java int.
   *
   * @param parameterIndex the first parameter is 1, the second is 2, ...
   * @return the parameter value; if the value is SQL NULL, the result is 0
   */
  int getInt(int parameterIndex)
  throws java.rmi.RemoteException, SQLException;

  /**
   * Get the value of a BIGINT parameter as a Java long.
   *
   * @param parameterIndex the first parameter is 1, the second is 2, ...
   * @return the parameter value; if the value is SQL NULL, the result is 0
   */
  long getLong(int parameterIndex)
  throws java.rmi.RemoteException, SQLException;

  /**
   * Get the value of a FLOAT parameter as a Java float.
   *
   * @param parameterIndex the first parameter is 1, the second is 2, ...
   * @return the parameter value; if the value is SQL NULL, the result is 0
   */
  float getFloat(int parameterIndex)
  throws java.rmi.RemoteException, SQLException;

  /**
   * Get the value of a DOUBLE parameter as a Java double.
   *
   * @param parameterIndex the first parameter is 1, the second is 2, ...
   * @return the parameter value; if the value is SQL NULL, the result is 0
   */
  double getDouble(int parameterIndex)
  throws java.rmi.RemoteException, SQLException;

  /**
   * Get the value of a NUMERIC parameter as a java.math.BigDecimal object.
   *
   * @param parameterIndex the first parameter is 1, the second is 2, ...
   * @param scale a value greater than or equal to zero representing the 
   *              desired number of digits to the right of the decimal point
   * @return the parameter value; if the value is SQL NULL, the result is null
   */
  java.math.BigDecimal getBigDecimal(int parameterIndex, int scale)
  throws java.rmi.RemoteException, SQLException;

  /**
   * Get the value of a SQL BINARY or VARBINARY parameter as a Java byte[]
   *
   * @param parameterIndex the first parameter is 1, the second is 2, ...
   * @return the parameter value; if the value is SQL NULL, the result is null
   */
  byte[] getBytes(int parameterIndex)
  throws java.rmi.RemoteException, SQLException;

  /**
   * Get the value of a SQL DATE parameter as a java.sql.Date object
   *
   * @param parameterIndex the first parameter is 1, the second is 2, ...
   * @return the parameter value; if the value is SQL NULL, the result is null
   */
  java.sql.Date getDate(int parameterIndex)
  throws java.rmi.RemoteException, SQLException;

  /**
   * Get the value of a SQL TIME parameter as a java.sql.Time object.
   *
   * @param parameterIndex the first parameter is 1, the second is 2, ...
   * @return the parameter value; if the value is SQL NULL, the result is null
   */
  java.sql.Time getTime(int parameterIndex)
  throws java.rmi.RemoteException, SQLException;

  /**
   * Get the value of a SQL TIMESTAMP parameter as a java.sql.Timestamp object.
   *
   * @param parameterIndex the first parameter is 1, the second is 2, ...
   * @return the parameter value; if the value is SQL NULL, the result is null
   */
  java.sql.Timestamp getTimestamp(int parameterIndex)
  throws java.rmi.RemoteException, SQLException;

  //----------------------------------------------------------------------
  // Advanced features:


  /**
   * Get the value of a parameter as a Java object.
   *
   * <p>This method returns a Java object whose type coresponds to the SQL
   * type that was registered for this parameter using registerOutParameter.
   *
   * <p>Note that this method may be used to read
   * datatabase-specific, abstract data types. This is done by
   * specifying a targetSqlType of java.sql.types.OTHER, which
   * allows the driver to return a database-specific Java type.
   *
   * @param parameterIndex The first parameter is 1, the second is 2, ...
   * @return A java.lang.Object holding the OUT parameter value.
   * @see Types 
   */
  Object getObject(int parameterIndex)
  throws java.rmi.RemoteException, SQLException;

	//--------------------------JDBC 2.0-----------------------------

	/**
	 * JDBC 2.0
	 *
	 * Gets the value of a JDBC <code>NUMERIC</code> parameter as a 
	 * <code>java.math.BigDecimal</code> object with as many digits to the
	 * right of the decimal point as the value contains.
	 * @param parameterIndex the first parameter is 1, the second is 2,
	 * and so on
	 * @return the parameter value in full precision.  If the value is 
	 * SQL NULL, the result is <code>null</code>. 
	 * @exception SQLException if a database access error occurs
	 */
	BigDecimal getBigDecimal(int parameterIndex) throws java.rmi.RemoteException, SQLException;

	/**
	 * JDBC 2.0
	 *
	 * Returns an object representing the value of OUT parameter 
	 * <code>i</code> and uses <code>map</code> for the custom
	 * mapping of the parameter value.
	 * <p>
	 * This method returns a Java object whose type corresponds to the
	 * JDBC type that was registered for this parameter using the method
	 * <code>registerOutParameter</code>.  By registering the target
	 * JDBC type as <code>java.sql.Types.OTHER</code>, this method can
	 * be used to read database-specific abstract data types.  
	 * @param i the first parameter is 1, the second is 2, and so on
	 * @param map the mapping from SQL type names to Java classes
	 * @return a java.lang.Object holding the OUT parameter value.
	 * @exception SQLException if a database access error occurs
	 */
	 Object  getObject (int i, java.util.Map<String,Class<?>> map) throws java.rmi.RemoteException, SQLException;

	/**
	 * JDBC 2.0
	 *
	 * Gets the value of a JDBC <code>REF(&lt;structured-type&gt;)</code>
	 * parameter as a {@link Ref} object in the Java programming language.
	 * @param i the first parameter is 1, the second is 2, 
	 * and so on
	 * @return the parameter value as a <code>Ref</code> object in the
	 * Java programming language.  If the value was SQL NULL, the value
	 * <code>null</code> is returned.
	 * @exception SQLException if a database access error occurs
	 */
	 RJRefInterface getRef(int i)
         throws java.rmi.RemoteException, SQLException;

	/**
	 * JDBC 2.0
	 *
	 * Gets the value of a JDBC <code>BLOB</code> parameter as a
	 * {@link Blob} object in the Java programming language.
	 * @param i the first parameter is 1, the second is 2, and so on
	 * @return the parameter value as a <code>Blob</code> object in the
	 * Java programming language.  If the value was SQL NULL, the value
	 * <code>null</code> is returned.
	 * @exception SQLException if a database access error occurs
	 */
	 RJBlobInterface getBlob(int i)
         throws java.rmi.RemoteException, SQLException;

	/**
	 * JDBC 2.0
	 *
	 * Gets the value of a JDBC <code>CLOB</code> parameter as a
	 * <code>Clob</code> object in the Java programming language.
	 * @param i the first parameter is 1, the second is 2, and
	 * so on
	 * @return the parameter value as a <code>Clob</code> object in the
	 * Java programming language.  If the value was SQL NULL, the
	 * value <code>null</code> is returned.
	 * @exception SQLException if a database access error occurs
	 */
	 RJClobInterface getClob (int i)
         throws java.rmi.RemoteException, SQLException;

	/**
	 * JDBC 2.0
	 *
	 * Gets the value of a JDBC <code>ARRAY</code> parameter as an
	 * {@link Array} object in the Java programming language.
	 * @param i the first parameter is 1, the second is 2, and 
	 * so on
	 * @return the parameter value as an <code>Array</code> object in
	 * the Java programming language.  If the value was SQL NULL, the
	 * value <code>null</code> is returned.
	 * @exception SQLException if a database access error occurs
	 */
	 RJArrayInterface getArray (int i)
         throws java.rmi.RemoteException, SQLException;

	/**
	 * Gets the value of a JDBC <code>DATE</code> parameter as a 
	 * <code>java.sql.Date</code> object, using
	 * the given <code>Calendar</code> object
	 * to construct the date.
	 * With a <code>Calendar</code> object, the driver
	 * can calculate the date taking into account a custom timezone and locale.
	 * If no <code>Calendar</code> object is specified, the driver uses the
	 * default timezone and locale.
	 *
	 * @param parameterIndex the first parameter is 1, the second is 2, 
	 * and so on
	 * @param cal the <code>Calendar</code> object the driver will use
	 *            to construct the date
	 * @return the parameter value.  If the value is SQL NULL, the result is 
	 * <code>null</code>.
	 * @exception SQLException if a database access error occurs
	 */
	java.sql.Date getDate(int parameterIndex, Calendar cal) 
	throws java.rmi.RemoteException, SQLException;

	/**
	 * Gets the value of a JDBC <code>TIME</code> parameter as a 
	 * <code>java.sql.Time</code> object, using
	 * the given <code>Calendar</code> object
	 * to construct the time.
	 * With a <code>Calendar</code> object, the driver
	 * can calculate the time taking into account a custom timezone and locale.
	 * If no <code>Calendar</code> object is specified, the driver uses the
	 * default timezone and locale.
	 *
	 * @param parameterIndex the first parameter is 1, the second is 2,
	 * and so on
	 * @param cal the <code>Calendar</code> object the driver will use
	 *            to construct the time
	 * @return the parameter value; if the value is SQL NULL, the result is 
	 * <code>null</code>.
	 * @exception SQLException if a database access error occurs
	 */
	java.sql.Time getTime(int parameterIndex, Calendar cal) 
	throws java.rmi.RemoteException, SQLException;

	/**
	 * Gets the value of a JDBC <code>TIMESTAMP</code> parameter as a
	 * <code>java.sql.Timestamp</code> object, using
	 * the given <code>Calendar</code> object to construct
	 * the <code>Timestamp</code> object.
	 * With a <code>Calendar</code> object, the driver
	 * can calculate the timestamp taking into account a custom timezone and locale.
	 * If no <code>Calendar</code> object is specified, the driver uses the
	 * default timezone and locale.
	 *
	 *
	 * @param parameterIndex the first parameter is 1, the second is 2, 
	 * and so on
	 * @param cal the <code>Calendar</code> object the driver will use
	 *            to construct the timestamp
	 * @return the parameter value.  If the value is SQL NULL, the result is 
	 * <code>null</code>.
	 * @exception SQLException if a database access error occurs
	 */
	java.sql.Timestamp getTimestamp(int parameterIndex, Calendar cal) 
	throws java.rmi.RemoteException, SQLException;


    /**
     * JDBC 2.0
     *
     * Registers the designated output parameter.  This version of 
	 * the method <code>registerOutParameter</code>
     * should be used for a user-named or REF output parameter.  Examples
     * of user-named types include: STRUCT, DISTINCT, JAVA_OBJECT, and
     * named array types.
     *
     * Before executing a stored procedure call, you must explicitly
     * call <code>registerOutParameter</code> to register the type from
	 * <code>java.sql.Types</code> for each
     * OUT parameter.  For a user-named parameter the fully-qualified SQL
     * type name of the parameter should also be given, while a REF
     * parameter requires that the fully-qualified type name of the
     * referenced type be given.  A JDBC driver that does not need the
     * type code and type name information may ignore it.   To be portable,
     * however, applications should always provide these values for
     * user-named and REF parameters.
     *
     * Although it is intended for user-named and REF parameters,
     * this method may be used to register a parameter of any JDBC type.
     * If the parameter does not have a user-named or REF type, the
     * typeName parameter is ignored.
     *
     * <P><B>Note:</B> When reading the value of an out parameter, you
     * must use the <code>getXXX</code> method whose Java type XXX corresponds to the
     * parameter's registered SQL type.
     *
     * @param parameterIndex the first parameter is 1, the second is 2,...
     * @param sqlType a value from {@link java.sql.Types}
     * @param typeName the fully-qualified name of an SQL structured type
     * @exception SQLException if a database-access error occurs
     * @see Types
     */
    void registerOutParameter (int paramIndex, int sqlType, String typeName)
    throws java.rmi.RemoteException, SQLException;


  //--------------------------JDBC 3.0-----------------------------

    /**
     * Registers the OUT parameter named 
     * <code>parameterName</code> to the JDBC type 
     * <code>sqlType</code>.  All OUT parameters must be registered
     * before a stored procedure is executed.
     * <p>
     * The JDBC type specified by <code>sqlType</code> for an OUT
     * parameter determines the Java type that must be used
     * in the <code>get</code> method to read the value of that parameter.
     * <p>
     * If the JDBC type expected to be returned to this output parameter
     * is specific to this particular database, <code>sqlType</code>
     * should be <code>java.sql.Types.OTHER</code>.  The method 
     * {@link #getObject} retrieves the value.
     * @param parameterName the name of the parameter
     * @param sqlType the JDBC type code defined by <code>java.sql.Types</code>.
     * If the parameter is of JDBC type <code>NUMERIC</code>
     * or <code>DECIMAL</code>, the version of
     * <code>registerOutParameter</code> that accepts a scale value 
     * should be used.
     * @exception SQLException if a database access error occurs
     * @since 1.4
     * @see Types 
     */
    void registerOutParameter(String parameterName, int sqlType)
	throws java.rmi.RemoteException, SQLException;

    /**
     * Registers the parameter named 
     * <code>parameterName</code> to be of JDBC type
     * <code>sqlType</code>.  This method must be called
     * before a stored procedure is executed.
     * <p>
     * The JDBC type specified by <code>sqlType</code> for an OUT
     * parameter determines the Java type that must be used
     * in the <code>get</code> method to read the value of that parameter.
     * <p>
     * This version of <code>registerOutParameter</code> should be
     * used when the parameter is of JDBC type <code>NUMERIC</code>
     * or <code>DECIMAL</code>.
     * @param parameterName the name of the parameter
     * @param sqlType SQL type code defined by <code>java.sql.Types</code>.
     * @param scale the desired number of digits to the right of the
     * decimal point.  It must be greater than or equal to zero.
     * @exception SQLException if a database access error occurs
     * @since 1.4
     * @see Types 
     */
    void registerOutParameter(String parameterName, int sqlType, int scale)
	throws java.rmi.RemoteException, SQLException;

    /**
     * Registers the designated output parameter.  This version of 
     * the method <code>registerOutParameter</code>
     * should be used for a user-named or REF output parameter.  Examples
     * of user-named types include: STRUCT, DISTINCT, JAVA_OBJECT, and
     * named array types.
     *
     * Before executing a stored procedure call, you must explicitly
     * call <code>registerOutParameter</code> to register the type from
     * <code>java.sql.Types</code> for each
     * OUT parameter.  For a user-named parameter the fully-qualified SQL
     * type name of the parameter should also be given, while a REF
     * parameter requires that the fully-qualified type name of the
     * referenced type be given.  A JDBC driver that does not need the
     * type code and type name information may ignore it.   To be portable,
     * however, applications should always provide these values for
     * user-named and REF parameters.
     *
     * Although it is intended for user-named and REF parameters,
     * this method may be used to register a parameter of any JDBC type.
     * If the parameter does not have a user-named or REF type, the
     * typeName parameter is ignored.
     *
     * <P><B>Note:</B> When reading the value of an out parameter, you
     * must use the <code>getXXX</code> method whose Java type XXX corresponds to the
     * parameter's registered SQL type.
     *
     * @param parameterName the name of the parameter
     * @param sqlType a value from {@link java.sql.Types}
     * @param typeName the fully-qualified name of an SQL structured type
     * @exception SQLException if a database access error occurs
     * @see Types
     * @since 1.4
     */
    void registerOutParameter (String parameterName, int sqlType, String typeName)
	throws java.rmi.RemoteException, SQLException;

    /**
     * Retrieves the value of the designated JDBC <code>DATALINK</code> parameter as a
     * <code>java.net.URL</code> object.
     * 
     * @param parameterIndex the first parameter is 1, the second is 2,...
     * @return a <code>java.net.URL</code> object that represents the 
     *         JDBC <code>DATALINK</code> value used as the designated
     *         parameter
     * @exception SQLException if a database access error occurs,
     *            or if the URL being returned is
     *            not a valid URL on the Java platform
     * @see #setURL
     * @since 1.4
     */
    java.net.URL getURL(int parameterIndex) throws java.rmi.RemoteException, SQLException;

    /**
     * Sets the designated parameter to the given <code>java.net.URL</code> object.
     * The driver converts this to an SQL <code>DATALINK</code> value when
     * it sends it to the database.
     *
     * @param parameterName the name of the parameter
     * @param val the parameter value
     * @exception SQLException if a database access error occurs,
     *            or if a URL is malformed
     * @see #getURL
     * @since 1.4
     */
    void setURL(String parameterName, java.net.URL val) throws java.rmi.RemoteException, SQLException;
    
    /**
     * Sets the designated parameter to SQL <code>NULL</code>.
     *
     * <P><B>Note:</B> You must specify the parameter's SQL type.
     *
     * @param parameterName the name of the parameter
     * @param sqlType the SQL type code defined in <code>java.sql.Types</code>
     * @exception SQLException if a database access error occurs
     * @since 1.4
     */
    void setNull(String parameterName, int sqlType) throws java.rmi.RemoteException, SQLException;

    /**
     * Sets the designated parameter to the given Java <code>boolean</code> value.
     * The driver converts this
     * to an SQL <code>BIT</code> value when it sends it to the database.
     *
     * @param parameterName the name of the parameter
     * @param x the parameter value
     * @exception SQLException if a database access error occurs
     * @see #getBoolean
     * @since 1.4
     */
    void setBoolean(String parameterName, boolean x) throws java.rmi.RemoteException, SQLException;

    /**
     * Sets the designated parameter to the given Java <code>byte</code> value.  
     * The driver converts this
     * to an SQL <code>TINYINT</code> value when it sends it to the database.
     *
     * @param parameterName the name of the parameter
     * @param x the parameter value
     * @exception SQLException if a database access error occurs
     * @see #getByte
     * @since 1.4
     */
    void setByte(String parameterName, byte x) throws java.rmi.RemoteException, SQLException;

    /**
     * Sets the designated parameter to the given Java <code>short</code> value. 
     * The driver converts this
     * to an SQL <code>SMALLINT</code> value when it sends it to the database.
     *
     * @param parameterName the name of the parameter
     * @param x the parameter value
     * @exception SQLException if a database access error occurs
     * @see #getShort
     * @since 1.4
     */
    void setShort(String parameterName, short x) throws java.rmi.RemoteException, SQLException;

    /**
     * Sets the designated parameter to the given Java <code>int</code> value.  
     * The driver converts this
     * to an SQL <code>INTEGER</code> value when it sends it to the database.
     *
     * @param parameterName the name of the parameter
     * @param x the parameter value
     * @exception SQLException if a database access error occurs
     * @see #getInt
     * @since 1.4
     */
    void setInt(String parameterName, int x) throws java.rmi.RemoteException, SQLException;

    /**
     * Sets the designated parameter to the given Java <code>long</code> value. 
     * The driver converts this
     * to an SQL <code>BIGINT</code> value when it sends it to the database.
     *
     * @param parameterName the name of the parameter
     * @param x the parameter value
     * @exception SQLException if a database access error occurs
     * @see #getLong
     * @since 1.4
     */
    void setLong(String parameterName, long x) throws java.rmi.RemoteException, SQLException;

    /**
     * Sets the designated parameter to the given Java <code>float</code> value. 
     * The driver converts this
     * to an SQL <code>FLOAT</code> value when it sends it to the database.
     *
     * @param parameterName the name of the parameter
     * @param x the parameter value
     * @exception SQLException if a database access error occurs
     * @see #getFloat
     * @since 1.4
     */
    void setFloat(String parameterName, float x) throws java.rmi.RemoteException, SQLException;

    /**
     * Sets the designated parameter to the given Java <code>double</code> value.  
     * The driver converts this
     * to an SQL <code>DOUBLE</code> value when it sends it to the database.
     *
     * @param parameterName the name of the parameter
     * @param x the parameter value
     * @exception SQLException if a database access error occurs
     * @see #getDouble
     * @since 1.4
     */
    void setDouble(String parameterName, double x) throws java.rmi.RemoteException, SQLException;

    /**
     * Sets the designated parameter to the given
     * <code>java.math.BigDecimal</code> value.  
     * The driver converts this to an SQL <code>NUMERIC</code> value when
     * it sends it to the database.
     *
     * @param parameterName the name of the parameter
     * @param x the parameter value
     * @exception SQLException if a database access error occurs
     * @see #getBigDecimal
     * @since 1.4
     */
    void setBigDecimal(String parameterName, BigDecimal x) throws java.rmi.RemoteException, SQLException;

    /**
     * Sets the designated parameter to the given Java <code>String</code> value. 
     * The driver converts this
     * to an SQL <code>VARCHAR</code> or <code>LONGVARCHAR</code> value
     * (depending on the argument's
     * size relative to the driver's limits on <code>VARCHAR</code> values)
     * when it sends it to the database.
     *
     * @param parameterName the name of the parameter
     * @param x the parameter value
     * @exception SQLException if a database access error occurs
     * @see #getString
     * @since 1.4
     */
    void setString(String parameterName, String x) throws java.rmi.RemoteException, SQLException;

    /**
     * Sets the designated parameter to the given Java array of bytes.  
     * The driver converts this to an SQL <code>VARBINARY</code> or 
     * <code>LONGVARBINARY</code> (depending on the argument's size relative 
     * to the driver's limits on <code>VARBINARY</code> values) when it sends 
     * it to the database.
     *
     * @param parameterName the name of the parameter
     * @param x the parameter value 
     * @exception SQLException if a database access error occurs
     * @see #getBytes
     * @since 1.4
     */
    void setBytes(String parameterName, byte x[]) throws java.rmi.RemoteException, SQLException;

    /**
     * Sets the designated parameter to the given <code>java.sql.Date</code> value.  
     * The driver converts this
     * to an SQL <code>DATE</code> value when it sends it to the database.
     *
     * @param parameterName the name of the parameter
     * @param x the parameter value
     * @exception SQLException if a database access error occurs
     * @see #getDate
     * @since 1.4
     */
    void setDate(String parameterName, java.sql.Date x)
	throws java.rmi.RemoteException, SQLException;

    /**
     * Sets the designated parameter to the given <code>java.sql.Time</code> value.  
     * The driver converts this
     * to an SQL <code>TIME</code> value when it sends it to the database.
     *
     * @param parameterName the name of the parameter
     * @param x the parameter value
     * @exception SQLException if a database access error occurs
     * @see #getTime
     * @since 1.4
     */
    void setTime(String parameterName, java.sql.Time x) 
	throws java.rmi.RemoteException, SQLException;

    /**
     * Sets the designated parameter to the given <code>java.sql.Timestamp</code> value.  
     * The driver
     * converts this to an SQL <code>TIMESTAMP</code> value when it sends it to the
     * database.
     *
     * @param parameterName the name of the parameter
     * @param x the parameter value 
     * @exception SQLException if a database access error occurs
     * @see #getTimestamp
     * @since 1.4
     */
    void setTimestamp(String parameterName, java.sql.Timestamp x)
	throws java.rmi.RemoteException, SQLException;

    /**
     * Sets the designated parameter to the given input stream, which will have 
     * the specified number of bytes.
     * When a very large ASCII value is input to a <code>LONGVARCHAR</code>
     * parameter, it may be more practical to send it via a
     * <code>java.io.InputStream</code>. Data will be read from the stream
     * as needed until end-of-file is reached.  The JDBC driver will
     * do any necessary conversion from ASCII to the database char format.
     * 
     * <P><B>Note:</B> This stream object can either be a standard
     * Java stream object or your own subclass that implements the
     * standard interface.
     *
     * @param parameterName the name of the parameter
     * @param x the Java input stream that contains the ASCII parameter value
     * @param length the number of bytes in the stream 
     * @exception SQLException if a database access error occurs
     * @since 1.4
     */
    void setAsciiStream(String parameterName, java.io.InputStream x, int length)
	throws java.rmi.RemoteException, SQLException;

    /**
     * Sets the designated parameter to the given input stream, which will have 
     * the specified number of bytes.
     * When a very large binary value is input to a <code>LONGVARBINARY</code>
     * parameter, it may be more practical to send it via a
     * <code>java.io.InputStream</code> object. The data will be read from the stream
     * as needed until end-of-file is reached.
     * 
     * <P><B>Note:</B> This stream object can either be a standard
     * Java stream object or your own subclass that implements the
     * standard interface.
     *
     * @param parameterName the name of the parameter
     * @param x the java input stream which contains the binary parameter value
     * @param length the number of bytes in the stream 
     * @exception SQLException if a database access error occurs
     * @since 1.4
     */
    void setBinaryStream(String parameterName, java.io.InputStream x, 
			 int length) throws java.rmi.RemoteException, SQLException;

    /**
     * Sets the value of the designated parameter with the given object. The second
     * argument must be an object type; for integral values, the
     * <code>java.lang</code> equivalent objects should be used.
     *
     * <p>The given Java object will be converted to the given targetSqlType
     * before being sent to the database.
     *
     * If the object has a custom mapping (is of a class implementing the 
     * interface <code>SQLData</code>),
     * the JDBC driver should call the method <code>SQLData.writeSQL</code> to write it 
     * to the SQL data stream.
     * If, on the other hand, the object is of a class implementing
     * <code>Ref</code>, <code>Blob</code>, <code>Clob</code>, <code>Struct</code>, 
     * or <code>Array</code>, the driver should pass it to the database as a 
     * value of the corresponding SQL type.
     * <P>
     * Note that this method may be used to pass datatabase-
     * specific abstract data types. 
     *
     * @param parameterName the name of the parameter
     * @param x the object containing the input parameter value
     * @param targetSqlType the SQL type (as defined in java.sql.Types) to be 
     * sent to the database. The scale argument may further qualify this type.
     * @param scale for java.sql.Types.DECIMAL or java.sql.Types.NUMERIC types,
     *          this is the number of digits after the decimal point.  For all other
     *          types, this value will be ignored.
     * @exception SQLException if a database access error occurs
     * @see Types
     * @see #getObject
     * @since 1.4 
     */
    void setObject(String parameterName, Object x, int targetSqlType, int scale)
	throws java.rmi.RemoteException, SQLException;

    /**
     * Sets the value of the designated parameter with the given object.
     * This method is like the method <code>setObject</code>
     * above, except that it assumes a scale of zero.
     *
     * @param parameterName the name of the parameter
     * @param x the object containing the input parameter value
     * @param targetSqlType the SQL type (as defined in java.sql.Types) to be 
     *                      sent to the database
     * @exception SQLException if a database access error occurs
     * @see #getObject
     * @since 1.4
     */
    void setObject(String parameterName, Object x, int targetSqlType) 
	throws java.rmi.RemoteException, SQLException;

    /**
     * Sets the value of the designated parameter with the given object. 
     * The second parameter must be of type <code>Object</code>; therefore, the
     * <code>java.lang</code> equivalent objects should be used for built-in types.
     *
     * <p>The JDBC specification specifies a standard mapping from
     * Java <code>Object</code> types to SQL types.  The given argument 
     * will be converted to the corresponding SQL type before being
     * sent to the database.
     *
     * <p>Note that this method may be used to pass datatabase-
     * specific abstract data types, by using a driver-specific Java
     * type.
     *
     * If the object is of a class implementing the interface <code>SQLData</code>,
     * the JDBC driver should call the method <code>SQLData.writeSQL</code>
     * to write it to the SQL data stream.
     * If, on the other hand, the object is of a class implementing
     * <code>Ref</code>, <code>Blob</code>, <code>Clob</code>, <code>Struct</code>, 
     * or <code>Array</code>, the driver should pass it to the database as a 
     * value of the corresponding SQL type.
     * <P>
     * This method throws an exception if there is an ambiguity, for example, if the
     * object is of a class implementing more than one of the interfaces named above.
     *
     * @param parameterName the name of the parameter
     * @param x the object containing the input parameter value 
     * @exception SQLException if a database access error occurs or if the given
     *            <code>Object</code> parameter is ambiguous
     * @see #getObject
     * @since 1.4
     */
    void setObject(String parameterName, Object x) throws java.rmi.RemoteException, SQLException;
   

    /**
     * Sets the designated parameter to the given <code>Reader</code>
     * object, which is the given number of characters long.
     * When a very large UNICODE value is input to a <code>LONGVARCHAR</code>
     * parameter, it may be more practical to send it via a
     * <code>java.io.Reader</code> object. The data will be read from the stream
     * as needed until end-of-file is reached.  The JDBC driver will
     * do any necessary conversion from UNICODE to the database char format.
     * 
     * <P><B>Note:</B> This stream object can either be a standard
     * Java stream object or your own subclass that implements the
     * standard interface.
     *
     * @param parameterName the name of the parameter
     * @param reader the <code>java.io.Reader</code> object that
     *        contains the UNICODE data used as the designated parameter
     * @param length the number of characters in the stream 
     * @exception SQLException if a database access error occurs
     * @since 1.4
     */
    void setCharacterStream(String parameterName,
			    java.io.Reader reader,
			    int length) throws java.rmi.RemoteException, SQLException;

    /**
     * Sets the designated parameter to the given <code>java.sql.Date</code> value,
     * using the given <code>Calendar</code> object.  The driver uses
     * the <code>Calendar</code> object to construct an SQL <code>DATE</code> value,
     * which the driver then sends to the database.  With a
     * a <code>Calendar</code> object, the driver can calculate the date
     * taking into account a custom timezone.  If no
     * <code>Calendar</code> object is specified, the driver uses the default
     * timezone, which is that of the virtual machine running the application.
     *
     * @param parameterName the name of the parameter
     * @param x the parameter value
     * @param cal the <code>Calendar</code> object the driver will use
     *            to construct the date
     * @exception SQLException if a database access error occurs
     * @see #getDate
     * @since 1.4
     */
    void setDate(String parameterName, java.sql.Date x, Calendar cal)
	throws java.rmi.RemoteException, SQLException;

    /**
     * Sets the designated parameter to the given <code>java.sql.Time</code> value,
     * using the given <code>Calendar</code> object.  The driver uses
     * the <code>Calendar</code> object to construct an SQL <code>TIME</code> value,
     * which the driver then sends to the database.  With a
     * a <code>Calendar</code> object, the driver can calculate the time
     * taking into account a custom timezone.  If no
     * <code>Calendar</code> object is specified, the driver uses the default
     * timezone, which is that of the virtual machine running the application.
     *
     * @param parameterName the name of the parameter
     * @param x the parameter value
     * @param cal the <code>Calendar</code> object the driver will use
     *            to construct the time
     * @exception SQLException if a database access error occurs
     * @see #getTime
     * @since 1.4
     */
    void setTime(String parameterName, java.sql.Time x, Calendar cal) 
	throws java.rmi.RemoteException, SQLException;

    /**
     * Sets the designated parameter to the given <code>java.sql.Timestamp</code> value,
     * using the given <code>Calendar</code> object.  The driver uses
     * the <code>Calendar</code> object to construct an SQL <code>TIMESTAMP</code> value,
     * which the driver then sends to the database.  With a
     * a <code>Calendar</code> object, the driver can calculate the timestamp
     * taking into account a custom timezone.  If no
     * <code>Calendar</code> object is specified, the driver uses the default
     * timezone, which is that of the virtual machine running the application.
     *
     * @param parameterName the name of the parameter
     * @param x the parameter value 
     * @param cal the <code>Calendar</code> object the driver will use
     *            to construct the timestamp
     * @exception SQLException if a database access error occurs
     * @see #getTimestamp
     * @since 1.4
     */
    void setTimestamp(String parameterName, java.sql.Timestamp x, Calendar cal)
	throws java.rmi.RemoteException, SQLException;

    /**
     * Sets the designated parameter to SQL <code>NULL</code>.
     * This version of the method <code>setNull</code> should
     * be used for user-defined types and REF type parameters.  Examples
     * of user-defined types include: STRUCT, DISTINCT, JAVA_OBJECT, and 
     * named array types.
     *
     * <P><B>Note:</B> To be portable, applications must give the
     * SQL type code and the fully-qualified SQL type name when specifying
     * a NULL user-defined or REF parameter.  In the case of a user-defined type 
     * the name is the type name of the parameter itself.  For a REF 
     * parameter, the name is the type name of the referenced type.  If 
     * a JDBC driver does not need the type code or type name information, 
     * it may ignore it.     
     *
     * Although it is intended for user-defined and Ref parameters,
     * this method may be used to set a null parameter of any JDBC type.
     * If the parameter does not have a user-defined or REF type, the given
     * typeName is ignored.
     *
     *
     * @param paramName the name of the parameter
     * @param sqlType a value from <code>java.sql.Types</code>
     * @param typeName the fully-qualified name of an SQL user-defined type;
     *        ignored if the parameter is not a user-defined type or 
     *        SQL <code>REF</code> value
     * @exception SQLException if a database access error occurs
     * @since 1.4
     */
    void setNull (String parameterName, int sqlType, String typeName) 
	throws java.rmi.RemoteException, SQLException;

    /**
     * Retrieves the value of a JDBC <code>CHAR</code>, <code>VARCHAR</code>, 
     * or <code>LONGVARCHAR</code> parameter as a <code>String</code> in 
     * the Java programming language.
     * <p>
     * For the fixed-length type JDBC <code>CHAR</code>,
     * the <code>String</code> object
     * returned has exactly the same value the JDBC
     * <code>CHAR</code> value had in the
     * database, including any padding added by the database.
     * @param parameterName the name of the parameter
     * @return the parameter value. If the value is SQL <code>NULL</code>, the result 
     * is <code>null</code>.
     * @exception SQLException if a database access error occurs
     * @see #setString
     * @since 1.4
     */
    String getString(String parameterName) throws java.rmi.RemoteException, SQLException;

    /**
     * Retrieves the value of a JDBC <code>BIT</code> parameter as a
     * <code>boolean</code> in the Java programming language.
     * @param parameterName the name of the parameter
     * @return the parameter value.  If the value is SQL <code>NULL</code>, the result 
     * is <code>false</code>.
     * @exception SQLException if a database access error occurs
     * @see #setBoolean
     * @since 1.4
     */
    boolean getBoolean(String parameterName) throws java.rmi.RemoteException, SQLException;

    /**
     * Retrieves the value of a JDBC <code>TINYINT</code> parameter as a <code>byte</code> 
     * in the Java programming language.
     * @param parameterName the name of the parameter
     * @return the parameter value.  If the value is SQL <code>NULL</code>, the result 
     * is <code>0</code>.
     * @exception SQLException if a database access error occurs
     * @see #setByte
     * @since 1.4
     */
    byte getByte(String parameterName) throws java.rmi.RemoteException, SQLException;

    /**
     * Retrieves the value of a JDBC <code>SMALLINT</code> parameter as a <code>short</code>
     * in the Java programming language.
     * @param parameterName the name of the parameter
     * @return the parameter value.  If the value is SQL <code>NULL</code>, the result 
     * is <code>0</code>.
     * @exception SQLException if a database access error occurs
     * @see #setShort
     * @since 1.4
     */
    short getShort(String parameterName) throws java.rmi.RemoteException, SQLException;

    /**
     * Retrieves the value of a JDBC <code>INTEGER</code> parameter as an <code>int</code>
     * in the Java programming language.
     *
     * @param parameterName the name of the parameter
     * @return the parameter value.  If the value is SQL <code>NULL</code>, 
     *         the result is <code>0</code>.
     * @exception SQLException if a database access error occurs
     * @see #setInt
     * @since 1.4
     */
    int getInt(String parameterName) throws java.rmi.RemoteException, SQLException;

    /**
     * Retrieves the value of a JDBC <code>BIGINT</code> parameter as a <code>long</code>
     * in the Java programming language.
     *
     * @param parameterName the name of the parameter
     * @return the parameter value.  If the value is SQL <code>NULL</code>, 
     *         the result is <code>0</code>.
     * @exception SQLException if a database access error occurs
     * @see #setLong
     * @since 1.4
     */
    long getLong(String parameterName) throws java.rmi.RemoteException, SQLException;

    /**
     * Retrieves the value of a JDBC <code>FLOAT</code> parameter as a <code>float</code>
     * in the Java programming language.
     * @param parameterName the name of the parameter
     * @return the parameter value.  If the value is SQL <code>NULL</code>, 
     *         the result is <code>0</code>.
     * @exception SQLException if a database access error occurs
     * @see #setFloat
     * @since 1.4
     */
    float getFloat(String parameterName) throws java.rmi.RemoteException, SQLException;

    /**
     * Retrieves the value of a JDBC <code>DOUBLE</code> parameter as a <code>double</code>
     * in the Java programming language.
     * @param parameterName the name of the parameter
     * @return the parameter value.  If the value is SQL <code>NULL</code>, 
     *         the result is <code>0</code>.
     * @exception SQLException if a database access error occurs
     * @see #setDouble
     * @since 1.4
     */
    double getDouble(String parameterName) throws java.rmi.RemoteException, SQLException;

    /**
     * Retrieves the value of a JDBC <code>BINARY</code> or <code>VARBINARY</code> 
     * parameter as an array of <code>byte</code> values in the Java
     * programming language.
     * @param parameterName the name of the parameter
     * @return the parameter value.  If the value is SQL <code>NULL</code>, the result is 
     *  <code>null</code>.
     * @exception SQLException if a database access error occurs
     * @see #setBytes
     * @since 1.4
     */
    byte[] getBytes(String parameterName) throws java.rmi.RemoteException, SQLException;

    /**
     * Retrieves the value of a JDBC <code>DATE</code> parameter as a 
     * <code>java.sql.Date</code> object.
     * @param parameterName the name of the parameter
     * @return the parameter value.  If the value is SQL <code>NULL</code>, the result 
     * is <code>null</code>.
     * @exception SQLException if a database access error occurs
     * @see #setDate
     * @since 1.4
     */
    java.sql.Date getDate(String parameterName) throws java.rmi.RemoteException, SQLException;

    /**
     * Retrieves the value of a JDBC <code>TIME</code> parameter as a 
     * <code>java.sql.Time</code> object.
     * @param parameterName the name of the parameter
     * @return the parameter value.  If the value is SQL <code>NULL</code>, the result 
     * is <code>null</code>.
     * @exception SQLException if a database access error occurs
     * @see #setTime
     * @since 1.4
     */
    java.sql.Time getTime(String parameterName) throws java.rmi.RemoteException, SQLException;

    /**
     * Retrieves the value of a JDBC <code>TIMESTAMP</code> parameter as a 
     * <code>java.sql.Timestamp</code> object.
     * @param parameterName the name of the parameter
     * @return the parameter value.  If the value is SQL <code>NULL</code>, the result 
     * is <code>null</code>.
     * @exception SQLException if a database access error occurs
     * @see #setTimestamp
     * @since 1.4
     */
    java.sql.Timestamp getTimestamp(String parameterName) throws java.rmi.RemoteException, SQLException;

    /**
     * Retrieves the value of a parameter as an <code>Object</code> in the Java 
     * programming language. If the value is an SQL <code>NULL</code>, the 
     * driver returns a Java <code>null</code>.
     * <p>
     * This method returns a Java object whose type corresponds to the JDBC
     * type that was registered for this parameter using the method
     * <code>registerOutParameter</code>.  By registering the target JDBC
     * type as <code>java.sql.Types.OTHER</code>, this method can be used
     * to read database-specific abstract data types.
     * @param parameterName the name of the parameter
     * @return A <code>java.lang.Object</code> holding the OUT parameter value.
     * @exception SQLException if a database access error occurs
     * @see Types
     * @see #setObject
     * @since 1.4
     */
    Object getObject(String parameterName) throws java.rmi.RemoteException, SQLException;

    /**
     * Retrieves the value of a JDBC <code>NUMERIC</code> parameter as a 
     * <code>java.math.BigDecimal</code> object with as many digits to the
     * right of the decimal point as the value contains.
     * @param parameterName the name of the parameter
     * @return the parameter value in full precision.  If the value is 
     * SQL <code>NULL</code>, the result is <code>null</code>. 
     * @exception SQLException if a database access error occurs
     * @see #setBigDecimal
     * @since 1.4
     */
    BigDecimal getBigDecimal(String parameterName) throws java.rmi.RemoteException, SQLException;

    /**
     * Returns an object representing the value of OUT parameter 
     * <code>i</code> and uses <code>map</code> for the custom
     * mapping of the parameter value.
     * <p>
     * This method returns a Java object whose type corresponds to the
     * JDBC type that was registered for this parameter using the method
     * <code>registerOutParameter</code>.  By registering the target
     * JDBC type as <code>java.sql.Types.OTHER</code>, this method can
     * be used to read database-specific abstract data types.  
     * @param parameterName the name of the parameter
     * @param map the mapping from SQL type names to Java classes
     * @return a <code>java.lang.Object</code> holding the OUT parameter value
     * @exception SQLException if a database access error occurs
     * @see #setObject
     * @since 1.4
     */
    Object  getObject (String parameterName, java.util.Map<String,Class<?>> map) throws java.rmi.RemoteException, SQLException;

    /**
     * Retrieves the value of a JDBC <code>REF(&lt;structured-type&gt;)</code>
     * parameter as a {@link Ref} object in the Java programming language.
     *
     * @param parameterName the name of the parameter
     * @return the parameter value as a <code>Ref</code> object in the
     *         Java programming language.  If the value was SQL <code>NULL</code>, 
     *         the value <code>null</code> is returned.
     * @exception SQLException if a database access error occurs
     * @since 1.4
     */
    RJRefInterface getRef (String parameterName) throws java.rmi.RemoteException, SQLException;

    /**
     * Retrieves the value of a JDBC <code>BLOB</code> parameter as a
     * {@link Blob} object in the Java programming language.
     *
     * @param parameterName the name of the parameter
     * @return the parameter value as a <code>Blob</code> object in the
     *         Java programming language.  If the value was SQL <code>NULL</code>, 
     *         the value <code>null</code> is returned.
     * @exception SQLException if a database access error occurs
     * @since 1.4
     */
    RJBlobInterface getBlob (String parameterName) throws java.rmi.RemoteException, SQLException;

    /**
     * Retrieves the value of a JDBC <code>CLOB</code> parameter as a
     * <code>Clob</code> object in the Java programming language.
     * @param parameterName the name of the parameter
     * @return the parameter value as a <code>Clob</code> object in the
     *         Java programming language.  If the value was SQL <code>NULL</code>, 
     *         the value <code>null</code> is returned.
     * @exception SQLException if a database access error occurs
     * @since 1.4
     */
    RJClobInterface getClob (String parameterName) throws java.rmi.RemoteException, SQLException;

    /**
     * Retrieves the value of a JDBC <code>ARRAY</code> parameter as an
     * {@link Array} object in the Java programming language.
     *
     * @param parameterName the name of the parameter
     * @return the parameter value as an <code>Array</code> object in
     *         Java programming language.  If the value was SQL <code>NULL</code>, 
     *         the value <code>null</code> is returned.
     * @exception SQLException if a database access error occurs
     * @since 1.4
     */
    RJArrayInterface getArray (String parameterName) throws java.rmi.RemoteException, SQLException;

    /**
     * Retrieves the value of a JDBC <code>DATE</code> parameter as a 
     * <code>java.sql.Date</code> object, using
     * the given <code>Calendar</code> object
     * to construct the date.
     * With a <code>Calendar</code> object, the driver
     * can calculate the date taking into account a custom timezone and locale.
     * If no <code>Calendar</code> object is specified, the driver uses the
     * default timezone and locale.
     *
     * @param parameterName the name of the parameter
     * @param cal the <code>Calendar</code> object the driver will use
     *            to construct the date
     * @return the parameter value.  If the value is SQL <code>NULL</code>, 
     * the result is <code>null</code>.
     * @exception SQLException if a database access error occurs
     * @see #setDate
     * @since 1.4
     */
    java.sql.Date getDate(String parameterName, Calendar cal) 
	throws java.rmi.RemoteException, SQLException;

    /**
     * Retrieves the value of a JDBC <code>TIME</code> parameter as a 
     * <code>java.sql.Time</code> object, using
     * the given <code>Calendar</code> object
     * to construct the time.
     * With a <code>Calendar</code> object, the driver
     * can calculate the time taking into account a custom timezone and locale.
     * If no <code>Calendar</code> object is specified, the driver uses the
     * default timezone and locale.
     *
     * @param parameterName the name of the parameter
     * @param cal the <code>Calendar</code> object the driver will use
     *            to construct the time
     * @return the parameter value; if the value is SQL <code>NULL</code>, the result is 
     * <code>null</code>.
     * @exception SQLException if a database access error occurs
     * @see #setTime
     * @since 1.4
     */
    java.sql.Time getTime(String parameterName, Calendar cal) 
	throws java.rmi.RemoteException, SQLException;

    /**
     * Retrieves the value of a JDBC <code>TIMESTAMP</code> parameter as a
     * <code>java.sql.Timestamp</code> object, using
     * the given <code>Calendar</code> object to construct
     * the <code>Timestamp</code> object.
     * With a <code>Calendar</code> object, the driver
     * can calculate the timestamp taking into account a custom timezone and locale.
     * If no <code>Calendar</code> object is specified, the driver uses the
     * default timezone and locale.
     *
     *
     * @param parameterName the name of the parameter
     * @param cal the <code>Calendar</code> object the driver will use
     *            to construct the timestamp
     * @return the parameter value.  If the value is SQL <code>NULL</code>, the result is 
     * <code>null</code>.
     * @exception SQLException if a database access error occurs
     * @see #setTimestamp
     * @since 1.4
     */
    java.sql.Timestamp getTimestamp(String parameterName, Calendar cal) 
	throws java.rmi.RemoteException, SQLException;

    /**
     * Retrieves the value of a JDBC <code>DATALINK</code> parameter as a
     * <code>java.net.URL</code> object.
     *
     * @param parameterName the name of the parameter
     * @return the parameter value as a <code>java.net.URL</code> object in the
     * Java programming language.  If the value was SQL <code>NULL</code>, the
     * value <code>null</code> is returned.
     * @exception SQLException if a database access error occurs,
     *            or if there is a problem with the URL
     * @see #setURL
     * @since 1.4
     */
    java.net.URL getURL(String parameterName) throws java.rmi.RemoteException, SQLException;

};

