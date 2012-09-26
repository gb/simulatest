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

import java.io.*;

// TBD: WARNING This file contains a hack for InputStream class...
// InputStream is not serializable, of course !
// The right way would be to encapsulate InputStream in a RMI remote object
// (hope I'll find time to do that)

/**
 * <P>
 * A SQL statement is pre-compiled and stored in a PreparedStatement object.
 * This object can then be used to efficiently execute this statement multiple
 * times.
 * 
 * <P>
 * <B>Note:</B> The setXXX methods for setting IN parameter values must specify
 * types that are compatible with the defined SQL type of the input parameter.
 * For instance, if the IN parameter has SQL type Integer then setInt should be
 * used.
 * 
 * <p>
 * If arbitrary parameter type conversions are required then the setObject
 * method should be used with a target SQL type.
 * 
 * @see Connection#prepareStatement
 * @see ResultSet
 */

public class RJPreparedStatement extends RJStatement implements PreparedStatement, Serializable {

	private static final long serialVersionUID = 8985778546527312724L;

	RJPreparedStatementInterface rmiPrepStmt_;

	public RJPreparedStatement(RJPreparedStatementInterface p, Connection c) {
		super(p, c);
		rmiPrepStmt_ = p;
	}

	/**
	 * A prepared SQL query is executed and its ResultSet is returned.
	 * 
	 * @return a ResultSet that contains the data produced by the query; never
	 *         null
	 */
	public java.sql.ResultSet executeQuery() throws SQLException {
		try {
			return new RJResultSet(rmiPrepStmt_.executeQuery(), this);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Execute a SQL INSERT, UPDATE or DELETE statement. In addition, SQL
	 * statements that return nothing such as SQL DDL statements can be
	 * executed.
	 * 
	 * @return either the row count for INSERT, UPDATE or DELETE; or 0 for SQL
	 *         statements that return nothing
	 */
	public int executeUpdate() throws SQLException {
		try {
			return rmiPrepStmt_.executeUpdate();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Set a parameter to SQL NULL.
	 * 
	 * <P>
	 * <B>Note:</B> You must specify the parameter's SQL type.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param sqlType
	 *            SQL type code defined by java.sql.Types
	 */
	public void setNull(int parameterIndex, int sqlType) throws SQLException {
		try {
			rmiPrepStmt_.setNull(parameterIndex, sqlType);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Set a parameter to a Java boolean value. The driver converts this to a
	 * SQL BIT value when it sends it to the database.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            the parameter value
	 */
	public void setBoolean(int parameterIndex, boolean x) throws SQLException {
		try {
			rmiPrepStmt_.setBoolean(parameterIndex, x);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Set a parameter to a Java byte value. The driver converts this to a SQL
	 * TINYINT value when it sends it to the database.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            the parameter value
	 */
	public void setByte(int parameterIndex, byte x) throws SQLException {
		try {
			rmiPrepStmt_.setByte(parameterIndex, x);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Set a parameter to a Java short value. The driver converts this to a SQL
	 * SMALLINT value when it sends it to the database.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            the parameter value
	 */
	public void setShort(int parameterIndex, short x) throws SQLException {
		try {
			rmiPrepStmt_.setShort(parameterIndex, x);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Set a parameter to a Java int value. The driver converts this to a SQL
	 * INTEGER value when it sends it to the database.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            the parameter value
	 */
	public void setInt(int parameterIndex, int x) throws SQLException {
		try {
			rmiPrepStmt_.setInt(parameterIndex, x);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Set a parameter to a Java long value. The driver converts this to a SQL
	 * BIGINT value when it sends it to the database.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            the parameter value
	 */
	public void setLong(int parameterIndex, long x) throws SQLException {
		try {
			rmiPrepStmt_.setLong(parameterIndex, x);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Set a parameter to a Java float value. The driver converts this to a SQL
	 * FLOAT value when it sends it to the database.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            the parameter value
	 */
	public void setFloat(int parameterIndex, float x) throws SQLException {
		try {
			rmiPrepStmt_.setFloat(parameterIndex, x);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Set a parameter to a Java double value. The driver converts this to a SQL
	 * DOUBLE value when it sends it to the database.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            the parameter value
	 */
	public void setDouble(int parameterIndex, double x) throws SQLException {
		try {
			rmiPrepStmt_.setDouble(parameterIndex, x);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Set a parameter to a java.lang.BigDecimal value. The driver converts this
	 * to a SQL NUMERIC value when it sends it to the database.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            the parameter value
	 */
	public void setBigDecimal(int parameterIndex, java.math.BigDecimal x)
			throws SQLException {
		try {
			rmiPrepStmt_.setBigDecimal(parameterIndex, x);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Set a parameter to a Java String value. The driver converts this to a SQL
	 * VARCHAR or LONGVARCHAR value (depending on the arguments size relative to
	 * the driver's limits on VARCHARs) when it sends it to the database.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            the parameter value
	 */
	public void setString(int parameterIndex, String x) throws SQLException {
		try {
			rmiPrepStmt_.setString(parameterIndex, x);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Set a parameter to a Java array of bytes. The driver converts this to a
	 * SQL VARBINARY or LONGVARBINARY (depending on the argument's size relative
	 * to the driver's limits on VARBINARYs) when it sends it to the database.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            the parameter value
	 */
	public void setBytes(int parameterIndex, byte x[]) throws SQLException {
		try {
			rmiPrepStmt_.setBytes(parameterIndex, x);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Set a parameter to a java.sql.Date value. The driver converts this to a
	 * SQL DATE value when it sends it to the database.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            the parameter value
	 */
	public void setDate(int parameterIndex, java.sql.Date x)
			throws SQLException {
		try {
			rmiPrepStmt_.setDate(parameterIndex, x);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Set a parameter to a java.sql.Time value. The driver converts this to a
	 * SQL TIME value when it sends it to the database.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            the parameter value
	 */
	public void setTime(int parameterIndex, java.sql.Time x)
			throws SQLException {
		try {
			rmiPrepStmt_.setTime(parameterIndex, x);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Set a parameter to a java.sql.Timestamp value. The driver converts this
	 * to a SQL TIMESTAMP value when it sends it to the database.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            the parameter value
	 */
	public void setTimestamp(int parameterIndex, java.sql.Timestamp x)
			throws SQLException {
		try {
			rmiPrepStmt_.setTimestamp(parameterIndex, x);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * When a very large ASCII value is input to a LONGVARCHAR parameter, it may
	 * be more practical to send it via a java.io.InputStream. JDBC will read
	 * the data from the stream as needed, until it reaches end-of-file. The
	 * JDBC driver will do any necessary conversion from ASCII to the database
	 * char format.
	 * 
	 * <P>
	 * <B>Note:</B> This stream object can either be a standard Java stream
	 * object or your own subclass that implements the standard interface.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            the java input stream which contains the ASCII parameter value
	 * @param length
	 *            the number of bytes in the stream
	 */
	// TBD InputStream is not serializable: I transmit all the data in a byte[]

	public void setAsciiStream(int parameterIndex, java.io.InputStream x,
			int length) throws SQLException {
		try {
			// rmiPrepStmt_.setAsciiStream(parameterIndex, x, length);

			/**
			 * // I read the whole InputStream into a StringBuffer, then send
			 * the // StringBuffer's bytes to the server !! Awful, isn't it :(
			 * // The right way is to "RMIze" InputStream, or what ??
			 * StringBuffer buf = new StringBuffer(); BufferedReader r = new
			 * BufferedReader(new InputStreamReader(x)); char cbuf[] = new
			 * char[256]; int nb; while((nb = r.read(cbuf, 0, 255)) >= 0) {
			 * if(nb > 0) buf.append(cbuf, 0, nb); } r.close();
			 * 
			 * rmiPrepStmt_.setAsciiStream(parameterIndex,
			 * buf.toString().getBytes(), length);
			 **/

			BufferedInputStream s = new BufferedInputStream(x);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte buf[] = new byte[256];
			int br;
			while ((br = s.read(buf)) >= 0) {
				if (br > 0)
					bos.write(buf, 0, br);
			}
			s.close();
			rmiPrepStmt_.setAsciiStream(parameterIndex, bos.toByteArray(),
					length);

		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		} catch (IOException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * When a very large UNICODE value is input to a LONGVARCHAR parameter, it
	 * may be more practical to send it via a java.io.InputStream. JDBC will
	 * read the data from the stream as needed, until it reaches end-of-file.
	 * The JDBC driver will do any necessary conversion from UNICODE to the
	 * database char format.
	 * 
	 * <P>
	 * <B>Note:</B> This stream object can either be a standard Java stream
	 * object or your own subclass that implements the standard interface.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            the java input stream which contains the UNICODE parameter
	 *            value
	 * @param length
	 *            the number of bytes in the stream
	 */
	// TBD InputStream is not serializable: I transmit all the data in a byte[]

	public void setUnicodeStream(int parameterIndex, java.io.InputStream x,
			int length) throws SQLException {
		try {
			// rmiPrepStmt_.setUnicodeStream(parameterIndex, x, length);

			/**
			 * // I read the whole InputStream into a StringBuffer, then send
			 * the // StringBuffer's bytes to the server !! Awful, isn't it :(
			 * // The right way is to "RMIze" InputStream, or what ??
			 * StringBuffer buf = new StringBuffer(); BufferedReader r = new
			 * BufferedReader(new InputStreamReader(x)); char cbuf[] = new
			 * char[256]; int nb; while((nb = r.read(cbuf, 0, 255)) >= 0) {
			 * if(nb > 0) buf.append(cbuf, 0, nb); } r.close();
			 * 
			 * rmiPrepStmt_.setUnicodeStream(parameterIndex,
			 * buf.toString().getBytes(), length);
			 **/

			BufferedInputStream s = new BufferedInputStream(x);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte buf[] = new byte[256];
			int br;
			while ((br = s.read(buf)) >= 0) {
				if (br > 0)
					bos.write(buf, 0, br);
			}
			s.close();
			rmiPrepStmt_.setUnicodeStream(parameterIndex, bos.toByteArray(),
					length);

		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		} catch (IOException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * When a very large binary value is input to a LONGVARBINARY parameter, it
	 * may be more practical to send it via a java.io.InputStream. JDBC will
	 * read the data from the stream as needed, until it reaches end-of-file.
	 * 
	 * <P>
	 * <B>Note:</B> This stream object can either be a standard Java stream
	 * object or your own subclass that implements the standard interface.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            the java input stream which contains the binary parameter
	 *            value
	 * @param length
	 *            the number of bytes in the stream
	 */
	// TBD InputStream is not serializable: I transmit all the data in a byte[]

	public void setBinaryStream(int parameterIndex, java.io.InputStream x,
			int length) throws SQLException {
		try {
			// rmiPrepStmt_.setBinaryStream(parameterIndex, x, length);
			/*
			 * // I read the whole InputStream into a StringBuffer, then send
			 * the // StringBuffer's bytes to the server !! Awful, isn't it :(
			 * // The right way is to "RMIze" InputStream, or what ??
			 * StringBuffer buf = new StringBuffer(); BufferedReader r = new
			 * BufferedReader(new InputStreamReader(x)); char cbuf[] = new
			 * char[256]; int nb; while((nb = r.read(cbuf, 0, 255)) >= 0) {
			 * if(nb > 0) buf.append(cbuf, 0, nb); } r.close();
			 * 
			 * rmiPrepStmt_.setBinaryStream(parameterIndex,
			 * buf.toString().getBytes(), length);
			 */

			BufferedInputStream s = new BufferedInputStream(x);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte buf[] = new byte[256];
			int br;
			while ((br = s.read(buf)) >= 0) {
				if (br > 0)
					bos.write(buf, 0, br);
			}
			s.close();
			rmiPrepStmt_.setBinaryStream(parameterIndex, bos.toByteArray(),
					length);

			/**
			 * TBD Proposed by P.Hearty rmiPrepStmt_.setBinaryStream(
			 * parameterIndex, RJResultSetServer.getBytesFromInputStream(x),
			 * length);
			 **/
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		} catch (IOException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * <P>
	 * In general, parameter values remain in force for repeated use of a
	 * Statement. Setting a parameter value automatically clears its previous
	 * value. However, in some cases it is useful to immediately release the
	 * resources used by the current parameter values; this can be done by
	 * calling clearParameters.
	 */
	public void clearParameters() throws SQLException {
		try {
			rmiPrepStmt_.clearParameters();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	// ----------------------------------------------------------------------
	// Advanced features:

	/**
	 * <p>
	 * Set the value of a parameter using an object; use the java.lang
	 * equivalent objects for integral values.
	 * 
	 * <p>
	 * The given Java object will be converted to the targetSqlType before being
	 * sent to the database.
	 * 
	 * <p>
	 * Note that this method may be used to pass datatabase- specific abstract
	 * data types. This is done by using a Driver- specific Java type and using
	 * a targetSqlType of java.sql.types.OTHER.
	 * 
	 * @param parameterIndex
	 *            The first parameter is 1, the second is 2, ...
	 * @param x
	 *            The object containing the input parameter value
	 * @param targetSqlType
	 *            The SQL type (as defined in java.sql.Types) to be sent to the
	 *            database. The scale argument may further qualify this type.
	 * @param scale
	 *            For java.sql.Types.DECIMAL or java.sql.Types.NUMERIC types
	 *            this is the number of digits after the decimal. For all other
	 *            types this value will be ignored,
	 * @see Types
	 */
	public void setObject(int parameterIndex, Object x, int targetSqlType,
			int scale) throws SQLException {
		try {
			rmiPrepStmt_.setObject(parameterIndex, x, targetSqlType, scale);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * This method is like setObject above, but assumes a scale of zero.
	 */
	public void setObject(int parameterIndex, Object x, int targetSqlType)
			throws SQLException {
		try {
			rmiPrepStmt_.setObject(parameterIndex, x, targetSqlType);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * <p>
	 * Set the value of a parameter using an object; use the java.lang
	 * equivalent objects for integral values.
	 * 
	 * <p>
	 * The JDBC specification specifies a standard mapping from Java Object
	 * types to SQL types. The given argument java object will be converted to
	 * the corresponding SQL type before being sent to the database.
	 * 
	 * <p>
	 * Note that this method may be used to pass datatabase specific abstract
	 * data types, by using a Driver specific Java type.
	 * 
	 * @param parameterIndex
	 *            The first parameter is 1, the second is 2, ...
	 * @param x
	 *            The object containing the input parameter value
	 */
	public void setObject(int parameterIndex, Object x) throws SQLException {
		try {
			rmiPrepStmt_.setObject(parameterIndex, x);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Some prepared statements return multiple results; the execute method
	 * handles these complex statements as well as the simpler form of
	 * statements handled by executeQuery and executeUpdate.
	 * 
	 * @see Statement#execute
	 */
	public boolean execute() throws SQLException {
		try {
			return rmiPrepStmt_.execute();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	// JDBC 2.0 methods added by Mike Jennings
	// sometime in the summer of 1999
	// Implementation added by Peter Hearty, Aug 2000,
	// (peter.hearty@lutris.com).

	public void setTimestamp(int parameterIndex, Timestamp x,
			java.util.Calendar cal) throws SQLException {
		try {
			rmiPrepStmt_.setTimestamp(parameterIndex, x, cal);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public void setTime(int parameterIndex, Time x, java.util.Calendar cal)
			throws SQLException {
		try {
			rmiPrepStmt_.setTime(parameterIndex, x, cal);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public void setRef(int i, Ref x) throws SQLException {
		try {
			rmiPrepStmt_.setRef(i, x);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public void setNull(int paramIndex, int sqlType, String typeName)
			throws SQLException {
		try {
			rmiPrepStmt_.setNull(paramIndex, sqlType, typeName);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public void setDate(int parameterIndex, Date x, java.util.Calendar cal)
			throws SQLException {
		try {
			rmiPrepStmt_.setDate(parameterIndex, x, cal);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public void setClob(int i, Clob x) throws SQLException {
		try {
			rmiPrepStmt_.setClob(i, x);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public void setCharacterStream(int parameterIndex, java.io.Reader reader,
			int length) throws SQLException {
		try {
			// rmiPrepStmt_.setCharacterStream(parameterIndex,reader,length);

			rmiPrepStmt_.setCharacterStream(parameterIndex,
					RJSerializer.toCharArray(reader), length);

		} catch (Exception e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public void setBlob(int i, Blob x) throws SQLException {
		try {
			rmiPrepStmt_.setBlob(i, x);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public void setArray(int i, Array x) throws SQLException {
		try {
			rmiPrepStmt_.setArray(i, x);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public ResultSetMetaData getMetaData() throws SQLException {
		try {
			return new RJResultSetMetaData(rmiPrepStmt_.getMetaData());
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public void addBatch() throws SQLException {
		try {
			rmiPrepStmt_.addBatch();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	// ------------------------- JDBC 3.0 -----------------------------------
	public void setURL(int parameterIndex, java.net.URL x) throws SQLException {
		try {
			rmiPrepStmt_.setURL(parameterIndex, x);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public ParameterMetaData getParameterMetaData() throws SQLException {
		try {
			return new RJParameterMetaData(rmiPrepStmt_.getParameterMetaData());
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	@Override
	public void setRowId(int parameterIndex, RowId x) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setNString(int parameterIndex, String value)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setNCharacterStream(int parameterIndex, Reader value,
			long length) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setNClob(int parameterIndex, NClob value) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setClob(int parameterIndex, Reader reader, long length)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setBlob(int parameterIndex, InputStream inputStream, long length)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setNClob(int parameterIndex, Reader reader, long length)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSQLXML(int parameterIndex, SQLXML xmlObject)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setAsciiStream(int parameterIndex, InputStream x, long length)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setBinaryStream(int parameterIndex, InputStream x, long length)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setCharacterStream(int parameterIndex, Reader reader,
			long length) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setAsciiStream(int parameterIndex, InputStream x)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setBinaryStream(int parameterIndex, InputStream x)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setCharacterStream(int parameterIndex, Reader reader)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setNCharacterStream(int parameterIndex, Reader value)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setClob(int parameterIndex, Reader reader) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setBlob(int parameterIndex, InputStream inputStream)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setNClob(int parameterIndex, Reader reader) throws SQLException {
		// TODO Auto-generated method stub

	}

};
