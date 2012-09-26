/**
 * RmiJdbc client/server JDBC Driver
 * (C) GIE Dyade (Groupe BULL / INRIA Research Center) 1997
 *
 * @version     1.0
 * @author      Pierre-Yves Gibello (pierreyves.gibello@experlog.com)
 */

package org.objectweb.rmijdbc;

import java.math.BigDecimal;
import java.rmi.*;
import java.rmi.server.Unreferenced;
import java.sql.*;
import java.util.Calendar;

/**
 * <P>
 * CallableStatement is used to execute SQL stored procedures.
 * 
 * <P>
 * JDBC provides a stored procedure SQL escape that allows stored procedures to
 * be called in a standard way for all RDBMS's. This escape syntax has one form
 * that includes a result parameter and one that does not. If used, the result
 * parameter must be registered as an OUT parameter. The other parameters may be
 * used for input, output or both. Parameters are refered to sequentially, by
 * number. The first parameter is 1.
 * 
 * <P>
 * <CODE>
 * {?= call <procedure-name>[<arg1>,<arg2>, ...]}<BR>
 * {call <procedure-name>[<arg1>,<arg2>, ...]}
 * </CODE>
 * 
 * <P>
 * IN parameter values are set using the set methods inherited from
 * PreparedStatement. The type of all OUT parameters must be registered prior to
 * executing the stored procedure; their values are retrieved after execution
 * via the get methods provided here.
 * 
 * <P>
 * A Callable statement may return a ResultSet or multiple ResultSets. Multiple
 * ResultSets are handled using operations inherited from Statement.
 * 
 * <P>
 * For maximum portability, a call's ResultSets and update counts should be
 * processed prior to getting the values of output parameters.
 * 
 * @see Connection#prepareCall
 * @see ResultSet
 */
public class RJCallableStatementServer extends RJPreparedStatementServer
		implements RJCallableStatementInterface, Unreferenced {

	private static final long serialVersionUID = -6614176649842805792L;

	java.sql.CallableStatement jdbcCallableStmt_;

	public RJCallableStatementServer(java.sql.CallableStatement c) throws java.rmi.RemoteException {
		super(c);
		jdbcCallableStmt_ = c;
	}

	public void unreferenced() {
		Runtime.getRuntime().gc();
	}

	/**
	 * Before executing a stored procedure call, you must explicitly call
	 * registerOutParameter to register the java.sql.Type of each out parameter.
	 * 
	 * <P>
	 * <B>Note:</B> When reading the value of an out parameter, you must use the
	 * getXXX method whose Java type XXX corresponds to the parameter's
	 * registered SQL type.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2,...
	 * @param sqlType
	 *            SQL type code defined by java.sql.Types; for parameters of
	 *            type Numeric or Decimal use the version of
	 *            registerOutParameter that accepts a scale value
	 * @see Type
	 */
	public void registerOutParameter(int parameterIndex, int sqlType)
			throws RemoteException, SQLException {
		jdbcCallableStmt_.registerOutParameter(parameterIndex, sqlType);
	}

	/**
	 * Use this version of registerOutParameter for registering Numeric or
	 * Decimal out parameters.
	 * 
	 * <P>
	 * <B>Note:</B> When reading the value of an out parameter, you must use the
	 * getXXX method whose Java type XXX corresponds to the parameter's
	 * registered SQL type.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param sqlType
	 *            use either java.sql.Type.NUMERIC or java.sql.Type.DECIMAL
	 * @param scale
	 *            a value greater than or equal to zero representing the desired
	 *            number of digits to the right of the decimal point
	 * @see Type
	 */
	public void registerOutParameter(int parameterIndex, int sqlType, int scale)
			throws RemoteException, SQLException {
		jdbcCallableStmt_.registerOutParameter(parameterIndex, sqlType, scale);
	}

	/**
	 * An OUT parameter may have the value of SQL NULL; wasNull reports whether
	 * the last value read has this special value.
	 * 
	 * <P>
	 * <B>Note:</B> You must first call getXXX on a parameter to read its value
	 * and then call wasNull() to see if the value was SQL NULL.
	 * 
	 * @return true if the last parameter read was SQL NULL
	 */
	public boolean wasNull() throws RemoteException, SQLException {
		return jdbcCallableStmt_.wasNull();
	}

	/**
	 * Get the value of a CHAR, VARCHAR, or LONGVARCHAR parameter as a Java
	 * String.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @return the parameter value; if the value is SQL NULL, the result is null
	 */
	public String getString(int parameterIndex) throws RemoteException,
			SQLException {
		return jdbcCallableStmt_.getString(parameterIndex);
	}

	/**
	 * Get the value of a BIT parameter as a Java boolean.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @return the parameter value; if the value is SQL NULL, the result is
	 *         false
	 */
	public boolean getBoolean(int parameterIndex) throws RemoteException,
			SQLException {
		return jdbcCallableStmt_.getBoolean(parameterIndex);
	}

	/**
	 * Get the value of a TINYINT parameter as a Java byte.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @return the parameter value; if the value is SQL NULL, the result is 0
	 */
	public byte getByte(int parameterIndex) throws RemoteException,
			SQLException {
		return jdbcCallableStmt_.getByte(parameterIndex);
	}

	/**
	 * Get the value of a SMALLINT parameter as a Java short.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @return the parameter value; if the value is SQL NULL, the result is 0
	 */
	public short getShort(int parameterIndex) throws RemoteException,
			SQLException {
		return jdbcCallableStmt_.getShort(parameterIndex);
	}

	/**
	 * Get the value of an INTEGER parameter as a Java int.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @return the parameter value; if the value is SQL NULL, the result is 0
	 */
	public int getInt(int parameterIndex) throws RemoteException, SQLException {
		return jdbcCallableStmt_.getInt(parameterIndex);
	}

	/**
	 * Get the value of a BIGINT parameter as a Java long.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @return the parameter value; if the value is SQL NULL, the result is 0
	 */
	public long getLong(int parameterIndex) throws RemoteException,
			SQLException {
		return jdbcCallableStmt_.getLong(parameterIndex);
	}

	/**
	 * Get the value of a FLOAT parameter as a Java float.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @return the parameter value; if the value is SQL NULL, the result is 0
	 */
	public float getFloat(int parameterIndex) throws RemoteException,
			SQLException {
		return jdbcCallableStmt_.getFloat(parameterIndex);
	}

	/**
	 * Get the value of a DOUBLE parameter as a Java double.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @return the parameter value; if the value is SQL NULL, the result is 0
	 */
	public double getDouble(int parameterIndex) throws RemoteException,
			SQLException {
		return jdbcCallableStmt_.getDouble(parameterIndex);
	}

	/**
	 * Get the value of a NUMERIC parameter as a java.math.BigDecimal object.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param scale
	 *            a value greater than or equal to zero representing the desired
	 *            number of digits to the right of the decimal point
	 * @return the parameter value; if the value is SQL NULL, the result is null
	 */
	@SuppressWarnings("deprecation")
	public BigDecimal getBigDecimal(int parameterIndex, int scale) throws RemoteException, SQLException {
		return jdbcCallableStmt_.getBigDecimal(parameterIndex, scale);
	}

	/**
	 * Get the value of a SQL BINARY or VARBINARY parameter as a Java byte[]
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @return the parameter value; if the value is SQL NULL, the result is null
	 */
	public byte[] getBytes(int parameterIndex) throws RemoteException, SQLException {
		return jdbcCallableStmt_.getBytes(parameterIndex);
	}

	/**
	 * Get the value of a SQL DATE parameter as a java.sql.Date object
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @return the parameter value; if the value is SQL NULL, the result is null
	 */
	public java.sql.Date getDate(int parameterIndex) throws RemoteException,
			SQLException {
		return jdbcCallableStmt_.getDate(parameterIndex);
	}

	/**
	 * Get the value of a SQL TIME parameter as a java.sql.Time object.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @return the parameter value; if the value is SQL NULL, the result is null
	 */
	public java.sql.Time getTime(int parameterIndex) throws RemoteException,
			SQLException {
		return jdbcCallableStmt_.getTime(parameterIndex);
	}

	/**
	 * Get the value of a SQL TIMESTAMP parameter as a java.sql.Timestamp
	 * object.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @return the parameter value; if the value is SQL NULL, the result is null
	 */
	public java.sql.Timestamp getTimestamp(int parameterIndex)
			throws RemoteException, SQLException {
		return jdbcCallableStmt_.getTimestamp(parameterIndex);
	}

	// ----------------------------------------------------------------------
	// Advanced features:

	/**
	 * Get the value of a parameter as a Java object.
	 * 
	 * <p>
	 * This method returns a Java object whose type coresponds to the SQL type
	 * that was registered for this parameter using registerOutParameter.
	 * 
	 * <p>
	 * Note that this method may be used to read datatabase-specific, abstract
	 * data types. This is done by specifying a targetSqlType of
	 * java.sql.types.OTHER, which allows the driver to return a
	 * database-specific Java type.
	 * 
	 * @param parameterIndex
	 *            The first parameter is 1, the second is 2, ...
	 * @return A java.lang.Object holding the OUT parameter value.
	 * @see Types
	 */
	public Object getObject(int parameterIndex) throws RemoteException,
			SQLException {
		return jdbcCallableStmt_.getObject(parameterIndex);
	}

	// 1.2 Added by Peter Hearty (peter.hearty@lutris.com) Aug 2000.
	public void registerOutParameter(int paramIndex, int sqlType,
			String typeName) throws RemoteException, SQLException {
		jdbcCallableStmt_.registerOutParameter(paramIndex, sqlType, typeName);
	}

	public Timestamp getTimestamp(int parameterIndex, Calendar cal)
			throws RemoteException, SQLException {
		return jdbcCallableStmt_.getTimestamp(parameterIndex, cal);
	}

	public Time getTime(int parameterIndex, Calendar cal)
			throws RemoteException, SQLException {
		return jdbcCallableStmt_.getTime(parameterIndex, cal);
	}

	public RJRefInterface getRef(int i) throws RemoteException, SQLException {
		return new RJRefServer(jdbcCallableStmt_.getRef(i));
	}

	public Object getObject(int i, java.util.Map<String,Class<?>> map) throws RemoteException, SQLException {
		return jdbcCallableStmt_.getObject(i, map);
	}

	public Date getDate(int parameterIndex, Calendar cal)
			throws RemoteException, SQLException {
		return jdbcCallableStmt_.getDate(parameterIndex, cal);
	}

	public RJClobInterface getClob(int i) throws RemoteException, SQLException {
		return new RJClobServer(jdbcCallableStmt_.getClob(i));
	}

	public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal)
			throws RemoteException, SQLException {
		jdbcCallableStmt_.setTimestamp(parameterIndex, x, cal);
	}

	public RJBlobInterface getBlob(int i) throws RemoteException, SQLException {
		return new RJBlobServer(jdbcCallableStmt_.getBlob(i));
	}

	public BigDecimal getBigDecimal(int parameterIndex) throws RemoteException,
			SQLException {
		return jdbcCallableStmt_.getBigDecimal(parameterIndex);
	}

	public RJArrayInterface getArray(int i) throws RemoteException,
			SQLException {
		return new RJArrayServer(jdbcCallableStmt_.getArray(i));
	}

	// end of java 1.2 stuff

	// --------------------------JDBC 3.0-----------------------------

	public void registerOutParameter(String parameterName, int sqlType)
			throws RemoteException, SQLException {
		jdbcCallableStmt_.registerOutParameter(parameterName, sqlType);
	}

	public void registerOutParameter(String parameterName, int sqlType,
			int scale) throws RemoteException, SQLException {
		jdbcCallableStmt_.registerOutParameter(parameterName, sqlType, scale);
	}

	public void registerOutParameter(String parameterName, int sqlType,
			String typeName) throws RemoteException, SQLException {
		jdbcCallableStmt_
				.registerOutParameter(parameterName, sqlType, typeName);
	}

	public java.net.URL getURL(int parameterIndex) throws RemoteException,
			SQLException {
		return jdbcCallableStmt_.getURL(parameterIndex);
	}

	public void setURL(String parameterName, java.net.URL val)
			throws RemoteException, SQLException {
		jdbcCallableStmt_.setURL(parameterName, val);
	}

	public void setNull(String parameterName, int sqlType)
			throws RemoteException, SQLException {
		jdbcCallableStmt_.setNull(parameterName, sqlType);
	}

	public void setBoolean(String parameterName, boolean x)
			throws RemoteException, SQLException {
		jdbcCallableStmt_.setBoolean(parameterName, x);
	}

	public void setByte(String parameterName, byte x) throws RemoteException,
			SQLException {
		jdbcCallableStmt_.setByte(parameterName, x);
	}

	public void setShort(String parameterName, short x) throws RemoteException,
			SQLException {
		jdbcCallableStmt_.setShort(parameterName, x);
	}

	public void setInt(String parameterName, int x) throws RemoteException,
			SQLException {
		jdbcCallableStmt_.setInt(parameterName, x);
	}

	public void setLong(String parameterName, long x) throws RemoteException,
			SQLException {
		jdbcCallableStmt_.setLong(parameterName, x);
	}

	public void setFloat(String parameterName, float x) throws RemoteException,
			SQLException {
		jdbcCallableStmt_.setFloat(parameterName, x);
	}

	public void setDouble(String parameterName, double x)
			throws RemoteException, SQLException {
		jdbcCallableStmt_.setDouble(parameterName, x);
	}

	public void setBigDecimal(String parameterName, BigDecimal x)
			throws RemoteException, SQLException {
		jdbcCallableStmt_.setBigDecimal(parameterName, x);
	}

	public void setString(String parameterName, String x)
			throws RemoteException, SQLException {
		jdbcCallableStmt_.setString(parameterName, x);
	}

	public void setBytes(String parameterName, byte x[])
			throws RemoteException, SQLException {
		jdbcCallableStmt_.setBytes(parameterName, x);
	}

	public void setDate(String parameterName, java.sql.Date x)
			throws RemoteException, SQLException {
		jdbcCallableStmt_.setDate(parameterName, x);
	}

	public void setTime(String parameterName, java.sql.Time x)
			throws RemoteException, SQLException {
		jdbcCallableStmt_.setTime(parameterName, x);
	}

	public void setTimestamp(String parameterName, java.sql.Timestamp x)
			throws RemoteException, SQLException {
		jdbcCallableStmt_.setTimestamp(parameterName, x);
	}

	public void setAsciiStream(String parameterName, java.io.InputStream x,
			int length) throws RemoteException, SQLException {
		jdbcCallableStmt_.setAsciiStream(parameterName, x, length);
	}

	public void setBinaryStream(String parameterName, java.io.InputStream x,
			int length) throws RemoteException, SQLException {
		jdbcCallableStmt_.setBinaryStream(parameterName, x, length);
	}

	public void setObject(String parameterName, Object x, int targetSqlType,
			int scale) throws RemoteException, SQLException {
		jdbcCallableStmt_.setObject(parameterName, x, targetSqlType, scale);
	}

	public void setObject(String parameterName, Object x, int targetSqlType)
			throws RemoteException, SQLException {
		jdbcCallableStmt_.setObject(parameterName, x, targetSqlType);
	}

	public void setObject(String parameterName, Object x)
			throws RemoteException, SQLException {
		jdbcCallableStmt_.setObject(parameterName, x);
	}

	public void setCharacterStream(String parameterName, java.io.Reader reader,
			int length) throws RemoteException, SQLException {
		jdbcCallableStmt_.setCharacterStream(parameterName, reader, length);
	}

	public void setDate(String parameterName, java.sql.Date x, Calendar cal)
			throws RemoteException, SQLException {
		jdbcCallableStmt_.setDate(parameterName, x, cal);
	}

	public void setTime(String parameterName, java.sql.Time x, Calendar cal)
			throws RemoteException, SQLException {
		jdbcCallableStmt_.setTime(parameterName, x, cal);
	}

	public void setTimestamp(String parameterName, java.sql.Timestamp x,
			Calendar cal) throws RemoteException, SQLException {
		jdbcCallableStmt_.setTimestamp(parameterName, x, cal);
	}

	public void setNull(String parameterName, int sqlType, String typeName)
			throws RemoteException, SQLException {
		/** TBD jdbcCallableStmt_.setNull(parameterName, sqlType, typeName); **/
		jdbcCallableStmt_.setNull(parameterName, sqlType, typeName);
	}

	public String getString(String parameterName) throws RemoteException,
			SQLException {
		return jdbcCallableStmt_.getString(parameterName);
	}

	public boolean getBoolean(String parameterName) throws RemoteException,
			SQLException {
		return jdbcCallableStmt_.getBoolean(parameterName);
	}

	public byte getByte(String parameterName) throws RemoteException,
			SQLException {
		return jdbcCallableStmt_.getByte(parameterName);
	}

	public short getShort(String parameterName) throws RemoteException,
			SQLException {
		return jdbcCallableStmt_.getShort(parameterName);
	}

	public int getInt(String parameterName) throws RemoteException,
			SQLException {
		return jdbcCallableStmt_.getInt(parameterName);
	}

	public long getLong(String parameterName) throws RemoteException,
			SQLException {
		return jdbcCallableStmt_.getLong(parameterName);
	}

	public float getFloat(String parameterName) throws RemoteException,
			SQLException {
		return jdbcCallableStmt_.getFloat(parameterName);
	}

	public double getDouble(String parameterName) throws RemoteException,
			SQLException {
		return jdbcCallableStmt_.getDouble(parameterName);
	}

	public byte[] getBytes(String parameterName) throws RemoteException,
			SQLException {
		return jdbcCallableStmt_.getBytes(parameterName);
	}

	public java.sql.Date getDate(String parameterName) throws RemoteException,
			SQLException {
		return jdbcCallableStmt_.getDate(parameterName);
	}

	public java.sql.Time getTime(String parameterName) throws RemoteException,
			SQLException {
		return jdbcCallableStmt_.getTime(parameterName);
	}

	public java.sql.Timestamp getTimestamp(String parameterName)
			throws RemoteException, SQLException {
		return jdbcCallableStmt_.getTimestamp(parameterName);
	}

	public Object getObject(String parameterName) throws RemoteException,
			SQLException {
		return jdbcCallableStmt_.getObject(parameterName);
	}

	public BigDecimal getBigDecimal(String parameterName)
			throws RemoteException, SQLException {
		return jdbcCallableStmt_.getBigDecimal(parameterName);
	}

	public Object getObject(String parameterName, java.util.Map<String,Class<?>> map) throws RemoteException, SQLException {
		return jdbcCallableStmt_.getObject(parameterName, map);
	}

	public RJRefInterface getRef(String parameterName) throws RemoteException,
			SQLException {
		return new RJRefServer(jdbcCallableStmt_.getRef(parameterName));
	}

	public RJBlobInterface getBlob(String parameterName)
			throws RemoteException, SQLException {
		return new RJBlobServer(jdbcCallableStmt_.getBlob(parameterName));
	}

	public RJClobInterface getClob(String parameterName)
			throws RemoteException, SQLException {
		return new RJClobServer(jdbcCallableStmt_.getClob(parameterName));
	}

	public RJArrayInterface getArray(String parameterName)
			throws RemoteException, SQLException {
		return new RJArrayServer(jdbcCallableStmt_.getArray(parameterName));
	}

	public java.sql.Date getDate(String parameterName, Calendar cal)
			throws RemoteException, SQLException {
		return jdbcCallableStmt_.getDate(parameterName, cal);
	}

	public java.sql.Time getTime(String parameterName, Calendar cal)
			throws RemoteException, SQLException {
		return jdbcCallableStmt_.getTime(parameterName, cal);
	}

	public java.sql.Timestamp getTimestamp(String parameterName, Calendar cal)
			throws RemoteException, SQLException {
		return jdbcCallableStmt_.getTimestamp(parameterName, cal);
	}

	public java.net.URL getURL(String parameterName) throws RemoteException,
			SQLException {
		return jdbcCallableStmt_.getURL(parameterName);
	}

};
