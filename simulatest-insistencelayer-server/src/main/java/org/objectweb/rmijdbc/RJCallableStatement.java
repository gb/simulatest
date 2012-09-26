/**
 * RmiJdbc client/server JDBC Driver
 * (C) GIE Dyade (Groupe BULL / INRIA Research Center) 1997
 *
 * @version     1.0
 * @author      Pierre-Yves Gibello (pierreyves.gibello@experlog.com)
 */

package org.objectweb.rmijdbc;

import java.sql.*;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Map;
import java.rmi.RemoteException;

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
public class RJCallableStatement extends RJPreparedStatement implements
		java.sql.CallableStatement, java.io.Serializable {

	private static final long serialVersionUID = -6726079658911383929L;
	
	RJCallableStatementInterface rmiCallableStmt_;

	public RJCallableStatement(RJCallableStatementInterface c, Connection conn) {
		super(c, conn);
		rmiCallableStmt_ = c;
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
			throws SQLException {
		try {
			rmiCallableStmt_.registerOutParameter(parameterIndex, sqlType);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
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
			throws SQLException {
		try {
			rmiCallableStmt_.registerOutParameter(parameterIndex, sqlType,
					scale);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
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
	public boolean wasNull() throws SQLException {
		try {
			return rmiCallableStmt_.wasNull();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Get the value of a CHAR, VARCHAR, or LONGVARCHAR parameter as a Java
	 * String.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @return the parameter value; if the value is SQL NULL, the result is null
	 */
	public String getString(int parameterIndex) throws SQLException {
		try {
			return rmiCallableStmt_.getString(parameterIndex);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Get the value of a BIT parameter as a Java boolean.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @return the parameter value; if the value is SQL NULL, the result is
	 *         false
	 */
	public boolean getBoolean(int parameterIndex) throws SQLException {
		try {
			return rmiCallableStmt_.getBoolean(parameterIndex);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Get the value of a TINYINT parameter as a Java byte.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @return the parameter value; if the value is SQL NULL, the result is 0
	 */
	public byte getByte(int parameterIndex) throws SQLException {
		try {
			return rmiCallableStmt_.getByte(parameterIndex);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Get the value of a SMALLINT parameter as a Java short.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @return the parameter value; if the value is SQL NULL, the result is 0
	 */
	public short getShort(int parameterIndex) throws SQLException {
		try {
			return rmiCallableStmt_.getShort(parameterIndex);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Get the value of an INTEGER parameter as a Java int.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @return the parameter value; if the value is SQL NULL, the result is 0
	 */
	public int getInt(int parameterIndex) throws SQLException {
		try {
			return rmiCallableStmt_.getInt(parameterIndex);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Get the value of a BIGINT parameter as a Java long.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @return the parameter value; if the value is SQL NULL, the result is 0
	 */
	public long getLong(int parameterIndex) throws SQLException {
		try {
			return rmiCallableStmt_.getLong(parameterIndex);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Get the value of a FLOAT parameter as a Java float.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @return the parameter value; if the value is SQL NULL, the result is 0
	 */
	public float getFloat(int parameterIndex) throws SQLException {
		try {
			return rmiCallableStmt_.getFloat(parameterIndex);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Get the value of a DOUBLE parameter as a Java double.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @return the parameter value; if the value is SQL NULL, the result is 0
	 */
	public double getDouble(int parameterIndex) throws SQLException {
		try {
			return rmiCallableStmt_.getDouble(parameterIndex);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
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
	public java.math.BigDecimal getBigDecimal(int parameterIndex, int scale)
			throws SQLException {
		try {
			return rmiCallableStmt_.getBigDecimal(parameterIndex, scale);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Get the value of a SQL BINARY or VARBINARY parameter as a Java byte[]
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @return the parameter value; if the value is SQL NULL, the result is null
	 */
	public byte[] getBytes(int parameterIndex) throws SQLException {
		try {
			return rmiCallableStmt_.getBytes(parameterIndex);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Get the value of a SQL DATE parameter as a java.sql.Date object
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @return the parameter value; if the value is SQL NULL, the result is null
	 */
	public java.sql.Date getDate(int parameterIndex) throws SQLException {
		try {
			return rmiCallableStmt_.getDate(parameterIndex);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Get the value of a SQL TIME parameter as a java.sql.Time object.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @return the parameter value; if the value is SQL NULL, the result is null
	 */
	public java.sql.Time getTime(int parameterIndex) throws SQLException {
		try {
			return rmiCallableStmt_.getTime(parameterIndex);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
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
			throws SQLException {
		try {
			return rmiCallableStmt_.getTimestamp(parameterIndex);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
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
	public Object getObject(int parameterIndex) throws SQLException {
		try {
			return rmiCallableStmt_.getObject(parameterIndex);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	// JDBC 2.0 methods
	// Added by Mike Jennings (mjenning@islandnet.com)
	// sometime in the summer of 1999

	// Implementation added Aug 2000 by Peter Hearty (peter.hearty@lutris.com).

	public void registerOutParameter(int paramIndex, int sqlType,
			String typeName) throws SQLException {
		try {
			rmiCallableStmt_
					.registerOutParameter(paramIndex, sqlType, typeName);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public Timestamp getTimestamp(int parameterIndex, java.util.Calendar cal)
			throws SQLException {
		try {
			return rmiCallableStmt_.getTimestamp(parameterIndex, cal);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public Time getTime(int parameterIndex, java.util.Calendar cal)
			throws SQLException {
		try {
			return rmiCallableStmt_.getTime(parameterIndex, cal);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public Ref getRef(int i) throws SQLException {
		try {
			return new RJRef(rmiCallableStmt_.getRef(i));
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public Date getDate(int parameterIndex, java.util.Calendar cal)
			throws SQLException {
		try {
			return rmiCallableStmt_.getDate(parameterIndex, cal);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public Clob getClob(int i) throws SQLException {
		try {
			return new RJClob(rmiCallableStmt_.getClob(i));
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public Blob getBlob(int i) throws SQLException {
		try {
			return new RJBlob(rmiCallableStmt_.getBlob(i));
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public java.math.BigDecimal getBigDecimal(int parameterIndex)
			throws SQLException {
		try {
			return rmiCallableStmt_.getBigDecimal(parameterIndex);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public Array getArray(int i) throws SQLException {
		try {
			return new RJArray(rmiCallableStmt_.getArray(i));
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	// end of java 1.2 stuff

	// --------------------------JDBC 3.0-----------------------------

	public void registerOutParameter(String parameterName, int sqlType)
			throws SQLException {
		try {
			rmiCallableStmt_.registerOutParameter(parameterName, sqlType);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public void registerOutParameter(String parameterName, int sqlType,
			int scale) throws SQLException {
		try {
			rmiCallableStmt_
					.registerOutParameter(parameterName, sqlType, scale);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public void registerOutParameter(String parameterName, int sqlType,
			String typeName) throws SQLException {
		try {
			rmiCallableStmt_.registerOutParameter(parameterName, sqlType,
					typeName);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public java.net.URL getURL(int parameterIndex) throws SQLException {
		try {
			return rmiCallableStmt_.getURL(parameterIndex);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public void setURL(String parameterName, java.net.URL val)
			throws SQLException {
		try {
			rmiCallableStmt_.setURL(parameterName, val);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public void setNull(String parameterName, int sqlType) throws SQLException {
		try {
			rmiCallableStmt_.setNull(parameterName, sqlType);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public void setBoolean(String parameterName, boolean x) throws SQLException {
		try {
			rmiCallableStmt_.setBoolean(parameterName, x);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public void setByte(String parameterName, byte x) throws SQLException {
		try {
			rmiCallableStmt_.setByte(parameterName, x);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public void setShort(String parameterName, short x) throws SQLException {
		try {
			rmiCallableStmt_.setShort(parameterName, x);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public void setInt(String parameterName, int x) throws SQLException {
		try {
			rmiCallableStmt_.setInt(parameterName, x);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public void setLong(String parameterName, long x) throws SQLException {
		try {
			rmiCallableStmt_.setLong(parameterName, x);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public void setFloat(String parameterName, float x) throws SQLException {
		try {
			rmiCallableStmt_.setFloat(parameterName, x);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public void setDouble(String parameterName, double x) throws SQLException {
		try {
			rmiCallableStmt_.setDouble(parameterName, x);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public void setBigDecimal(String parameterName, BigDecimal x)
			throws SQLException {
		try {
			rmiCallableStmt_.setBigDecimal(parameterName, x);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public void setString(String parameterName, String x) throws SQLException {
		try {
			rmiCallableStmt_.setString(parameterName, x);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public void setBytes(String parameterName, byte x[]) throws SQLException {
		try {
			rmiCallableStmt_.setBytes(parameterName, x);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public void setDate(String parameterName, java.sql.Date x)
			throws SQLException {
		try {
			rmiCallableStmt_.setDate(parameterName, x);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public void setTime(String parameterName, java.sql.Time x)
			throws SQLException {
		try {
			rmiCallableStmt_.setTime(parameterName, x);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public void setTimestamp(String parameterName, java.sql.Timestamp x)
			throws SQLException {
		try {
			rmiCallableStmt_.setTimestamp(parameterName, x);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public void setAsciiStream(String parameterName, java.io.InputStream x,
			int length) throws SQLException {
		try {
			rmiCallableStmt_.setAsciiStream(parameterName, x, length);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public void setBinaryStream(String parameterName, java.io.InputStream x,
			int length) throws SQLException {
		try {
			rmiCallableStmt_.setBinaryStream(parameterName, x, length);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public void setObject(String parameterName, Object x, int targetSqlType,
			int scale) throws SQLException {
		try {
			rmiCallableStmt_.setObject(parameterName, x, targetSqlType, scale);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public void setObject(String parameterName, Object x, int targetSqlType)
			throws SQLException {
		try {
			rmiCallableStmt_.setObject(parameterName, x, targetSqlType);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public void setObject(String parameterName, Object x) throws SQLException {
		try {
			rmiCallableStmt_.setObject(parameterName, x);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public void setCharacterStream(String parameterName, java.io.Reader reader,
			int length) throws SQLException {
		try {
			rmiCallableStmt_.setCharacterStream(parameterName, reader, length);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public void setDate(String parameterName, java.sql.Date x, Calendar cal)
			throws SQLException {
		try {
			rmiCallableStmt_.setDate(parameterName, x, cal);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public void setTime(String parameterName, java.sql.Time x, Calendar cal)
			throws SQLException {
		try {
			rmiCallableStmt_.setTime(parameterName, x, cal);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public void setTimestamp(String parameterName, java.sql.Timestamp x,
			Calendar cal) throws SQLException {
		try {
			rmiCallableStmt_.setTimestamp(parameterName, x, cal);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public void setNull(String parameterName, int sqlType, String typeName)
			throws SQLException {
		try {
			rmiCallableStmt_.setNull(parameterName, sqlType, typeName);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public String getString(String parameterName) throws SQLException {
		try {
			return rmiCallableStmt_.getString(parameterName);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public boolean getBoolean(String parameterName) throws SQLException {
		try {
			return rmiCallableStmt_.getBoolean(parameterName);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public byte getByte(String parameterName) throws SQLException {
		try {
			return rmiCallableStmt_.getByte(parameterName);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public short getShort(String parameterName) throws SQLException {
		try {
			return rmiCallableStmt_.getShort(parameterName);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public int getInt(String parameterName) throws SQLException {
		try {
			return rmiCallableStmt_.getInt(parameterName);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public long getLong(String parameterName) throws SQLException {
		try {
			return rmiCallableStmt_.getLong(parameterName);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public float getFloat(String parameterName) throws SQLException {
		try {
			return rmiCallableStmt_.getFloat(parameterName);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public double getDouble(String parameterName) throws SQLException {
		try {
			return rmiCallableStmt_.getDouble(parameterName);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public byte[] getBytes(String parameterName) throws SQLException {
		try {
			return rmiCallableStmt_.getBytes(parameterName);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public java.sql.Date getDate(String parameterName) throws SQLException {
		try {
			return rmiCallableStmt_.getDate(parameterName);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public java.sql.Time getTime(String parameterName) throws SQLException {
		try {
			return rmiCallableStmt_.getTime(parameterName);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public java.sql.Timestamp getTimestamp(String parameterName)
			throws SQLException {
		try {
			return rmiCallableStmt_.getTimestamp(parameterName);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public Object getObject(String parameterName) throws SQLException {
		try {
			return rmiCallableStmt_.getObject(parameterName);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public BigDecimal getBigDecimal(String parameterName) throws SQLException {
		try {
			return rmiCallableStmt_.getBigDecimal(parameterName);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public Ref getRef(String parameterName) throws SQLException {
		try {
			return new RJRef(rmiCallableStmt_.getRef(parameterName));
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public Blob getBlob(String parameterName) throws SQLException {
		try {
			return new RJBlob(rmiCallableStmt_.getBlob(parameterName));
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public Clob getClob(String parameterName) throws SQLException {
		try {
			return new RJClob(rmiCallableStmt_.getClob(parameterName));
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public Array getArray(String parameterName) throws SQLException {
		try {
			return new RJArray(rmiCallableStmt_.getArray(parameterName));
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public java.sql.Date getDate(String parameterName, Calendar cal)
			throws SQLException {
		try {
			return rmiCallableStmt_.getDate(parameterName, cal);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public java.sql.Time getTime(String parameterName, Calendar cal)
			throws SQLException {
		try {
			return rmiCallableStmt_.getTime(parameterName, cal);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public java.sql.Timestamp getTimestamp(String parameterName, Calendar cal)
			throws SQLException {
		try {
			return rmiCallableStmt_.getTimestamp(parameterName, cal);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public java.net.URL getURL(String parameterName) throws SQLException {
		try {
			return rmiCallableStmt_.getURL(parameterName);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	@Override
	public Object getObject(int parameterIndex, Map<String, Class<?>> map)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getObject(String parameterName, Map<String, Class<?>> map)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RowId getRowId(int parameterIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RowId getRowId(String parameterName) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setRowId(String parameterName, RowId x) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setNString(String parameterName, String value)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setNCharacterStream(String parameterName, Reader value,
			long length) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setNClob(String parameterName, NClob value) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setClob(String parameterName, Reader reader, long length)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setBlob(String parameterName, InputStream inputStream,
			long length) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setNClob(String parameterName, Reader reader, long length)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public NClob getNClob(int parameterIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NClob getNClob(String parameterName) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSQLXML(String parameterName, SQLXML xmlObject)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public SQLXML getSQLXML(int parameterIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SQLXML getSQLXML(String parameterName) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNString(int parameterIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNString(String parameterName) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Reader getNCharacterStream(int parameterIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Reader getNCharacterStream(String parameterName) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Reader getCharacterStream(int parameterIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Reader getCharacterStream(String parameterName) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setBlob(String parameterName, Blob x) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setClob(String parameterName, Clob x) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setAsciiStream(String parameterName, InputStream x, long length)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setBinaryStream(String parameterName, InputStream x, long length)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCharacterStream(String parameterName, Reader reader,
			long length) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setAsciiStream(String parameterName, InputStream x)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setBinaryStream(String parameterName, InputStream x)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCharacterStream(String parameterName, Reader reader)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setNCharacterStream(String parameterName, Reader value)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setClob(String parameterName, Reader reader)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setBlob(String parameterName, InputStream inputStream)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setNClob(String parameterName, Reader reader)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

};
