/**
 * RmiJdbc client/server JDBC Driver
 * (C) GIE Dyade (Groupe BULL / INRIA Research Center) 1997
 * (C) ExperLog 1999-2000
 *
 * @version     1.0
 * @author      Pierre-Yves Gibello (pierreyves.gibello@experlog.com)
 */

package org.objectweb.rmijdbc;

import java.sql.*;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.Properties;

//import javax.transaction.xa.*;

/**
 * <P>
 * A Connection represents a session with a specific database. Within the
 * context of a Connection, SQL statements are executed and results are
 * returned.
 * 
 * <P>
 * A Connection's database is able to provide information describing its tables,
 * its supported SQL grammar, its stored procedures, the capabilities of this
 * connection, etc. This information is obtained with the getMetaData method.
 * 
 * <P>
 * <B>Note:</B> By default the Connection automatically commits changes after
 * executing each statement. If auto commit has been disabled, an explicit
 * commit must be done or database changes will not be saved.
 * 
 * @see DriverManager#getConnection
 * @see Statement
 * @see ResultSet
 * @see DatabaseMetaData
 */
public class RJConnection implements java.sql.Connection, java.io.Serializable {

	private static final long serialVersionUID = 1403286812713301225L;

	RJConnectionInterface rmiConnection_;

	protected RJConnection(RJConnectionInterface rmiconn) {
		rmiConnection_ = rmiconn;
	}

	public RJConnection(RJDriverInterface drv, String url, Properties info)
			throws Exception {

		// url and info are JDBC data
		rmiConnection_ = drv.connect(url, info);

		if (rmiConnection_ == null) {
			throw new SQLException(
					"Underlying driver couldn\'t establish the connection: connect() returned null, check the configuration");
		}
	}

	/**
	 * SQL statements without parameters are normally executed using Statement
	 * objects. If the same SQL statement is executed many times, it is more
	 * efficient to use a PreparedStatement
	 * 
	 * @return a new Statement object
	 */
	public java.sql.Statement createStatement() throws SQLException {
		try {
			return new RJStatement(rmiConnection_.createStatement(), this);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * A SQL statement with or without IN parameters can be pre-compiled and
	 * stored in a PreparedStatement object. This object can then be used to
	 * efficiently execute this statement multiple times.
	 * 
	 * <P>
	 * <B>Note:</B> This method is optimized for handling parametric SQL
	 * statements that benefit from precompilation. If the driver supports
	 * precompilation, prepareStatement will send the statement to the database
	 * for precompilation. Some drivers may not support precompilation. In this
	 * case, the statement may not be sent to the database until the
	 * PreparedStatement is executed. This has no direct affect on users;
	 * however, it does affect which method throws certain SQLExceptions.
	 * 
	 * @param sql
	 *            a SQL statement that may contain one or more '?' IN parameter
	 *            placeholders
	 * 
	 * @return a new PreparedStatement object containing the pre-compiled
	 *         statement
	 */
	public java.sql.PreparedStatement prepareStatement(String sql)
			throws SQLException {
		try {
			return new RJPreparedStatement(
					rmiConnection_.prepareStatement(sql), this);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * A SQL stored procedure call statement is handled by creating a
	 * CallableStatement for it. The CallableStatement provides methods for
	 * setting up its IN and OUT parameters, and methods for executing it.
	 * 
	 * <P>
	 * <B>Note:</B> This method is optimized for handling stored procedure call
	 * statements. Some drivers may send the call statement to the database when
	 * the prepareCall is done; others may wait until the CallableStatement is
	 * executed. This has no direct affect on users; however, it does affect
	 * which method throws certain SQLExceptions.
	 * 
	 * @param sql
	 *            a SQL statement that may contain one or more '?' parameter
	 *            placeholders. Typically this statement is a JDBC function call
	 *            escape string.
	 * 
	 * @return a new CallableStatement object containing the pre-compiled SQL
	 *         statement
	 */
	public java.sql.CallableStatement prepareCall(String sql)
			throws SQLException {
		try {
			return new RJCallableStatement(rmiConnection_.prepareCall(sql),
					this);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * A driver may convert the JDBC sql grammar into its system's native SQL
	 * grammar prior to sending it; nativeSQL returns the native form of the
	 * statement that the driver would have sent.
	 * 
	 * @param sql
	 *            a SQL statement that may contain one or more '?' parameter
	 *            placeholders
	 * 
	 * @return the native form of this statement
	 */
	public String nativeSQL(String sql) throws SQLException {
		try {
			return rmiConnection_.nativeSQL(sql);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * If a connection is in auto-commit mode, then all its SQL statements will
	 * be executed and committed as individual transactions. Otherwise, its SQL
	 * statements are grouped into transactions that are terminated by either
	 * commit() or rollback(). By default, new connections are in auto-commit
	 * mode.
	 * 
	 * The commit occurs when the statement completes or the next execute
	 * occurs, whichever comes first. In the case of statements returning a
	 * ResultSet, the statement completes when the last row of the ResultSet has
	 * been retrieved or the ResultSet has been closed. In advanced cases, a
	 * single statement may return multiple results as well as output parameter
	 * values. Here the commit occurs when all results and output param values
	 * have been retrieved.
	 * 
	 * @param autoCommit
	 *            true enables auto-commit; false disables auto-commit.
	 */
	public void setAutoCommit(boolean autoCommit) throws SQLException {
		try {
			rmiConnection_.setAutoCommit(autoCommit);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Get the current auto-commit state.
	 * 
	 * @return Current state of auto-commit mode.
	 * @see #setAutoCommit
	 */
	public boolean getAutoCommit() throws SQLException {
		try {
			return rmiConnection_.getAutoCommit();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Commit makes all changes made since the previous commit/rollback
	 * permanent and releases any database locks currently held by the
	 * Connection. This method should only be used when auto commit has been
	 * disabled.
	 * 
	 * @see #setAutoCommit
	 */
	public void commit() throws SQLException {
		try {
			rmiConnection_.commit();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Rollback drops all changes made since the previous commit/rollback and
	 * releases any database locks currently held by the Connection. This method
	 * should only be used when auto commit has been disabled.
	 * 
	 * @see #setAutoCommit
	 */
	public void rollback() throws SQLException {
		try {
			rmiConnection_.rollback();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * In some cases, it is desirable to immediately release a Connection's
	 * database and JDBC resources instead of waiting for them to be
	 * automatically released; the close method provides this immediate release.
	 * 
	 * <P>
	 * <B>Note:</B> A Connection is automatically closed when it is garbage
	 * collected. Certain fatal errors also result in a closed Connection.
	 */
	public void close() throws SQLException {
		try {
			rmiConnection_.close();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Tests to see if a Connection is closed.
	 * 
	 * @return true if the connection is closed; false if it's still open
	 */
	public boolean isClosed() throws SQLException {
		try {
			return rmiConnection_.isClosed();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	// ======================================================================
	// Advanced features:

	/**
	 * A Connection's database is able to provide information describing its
	 * tables, its supported SQL grammar, its stored procedures, the
	 * capabilities of this connection, etc. This information is made available
	 * through a DatabaseMetaData object.
	 * 
	 * @return a DatabaseMetaData object for this Connection
	 */
	public java.sql.DatabaseMetaData getMetaData() throws SQLException {
		try {
			return new RJDatabaseMetaData(rmiConnection_.getMetaData(), this);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * You can put a connection in read-only mode as a hint to enable database
	 * optimizations.
	 * 
	 * <P>
	 * <B>Note:</B> setReadOnly cannot be called while in the middle of a
	 * transaction.
	 * 
	 * @param readOnly
	 *            true enables read-only mode; false disables read-only mode.
	 */
	public void setReadOnly(boolean readOnly) throws SQLException {
		try {
			rmiConnection_.setReadOnly(readOnly);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Tests to see if the connection is in read-only mode.
	 * 
	 * @return true if connection is read-only
	 */
	public boolean isReadOnly() throws SQLException {
		try {
			return rmiConnection_.isReadOnly();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * A sub-space of this Connection's database may be selected by setting a
	 * catalog name. If the driver does not support catalogs it will silently
	 * ignore this request.
	 */
	public void setCatalog(String catalog) throws SQLException {
		try {
			rmiConnection_.setCatalog(catalog);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Return the Connection's current catalog name.
	 * 
	 * @return the current catalog name or null
	 */
	public String getCatalog() throws SQLException {
		try {
			return rmiConnection_.getCatalog();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Transactions are not supported.
	 */
	int TRANSACTION_NONE = 0;

	/**
	 * Dirty reads, non-repeatable reads and phantom reads can occur.
	 */
	int TRANSACTION_READ_UNCOMMITTED = 1;

	/**
	 * Dirty reads are prevented; non-repeatable reads and phantom reads can
	 * occur.
	 */
	int TRANSACTION_READ_COMMITTED = 2;

	/**
	 * Dirty reads and non-repeatable reads are prevented; phantom reads can
	 * occur.
	 */
	int TRANSACTION_REPEATABLE_READ = 4;

	/**
	 * Dirty reads, non-repeatable reads and phantom reads are prevented.
	 */
	int TRANSACTION_SERIALIZABLE = 8;

	/**
	 * You can call this method to try to change the transaction isolation level
	 * using one of the TRANSACTION_* values.
	 * 
	 * <P>
	 * <B>Note:</B> setTransactionIsolation cannot be called while in the middle
	 * of a transaction.
	 * 
	 * @param level
	 *            one of the TRANSACTION_* isolation values with the exception
	 *            of TRANSACTION_NONE; some databases may not support other
	 *            values
	 * 
	 * @see DatabaseMetaData#supportsTransactionIsolationLevel
	 */
	public void setTransactionIsolation(int level) throws SQLException {
		try {
			rmiConnection_.setTransactionIsolation(level);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Get this Connection's current transaction isolation mode.
	 * 
	 * @return the current TRANSACTION_* mode value
	 */
	public int getTransactionIsolation() throws SQLException {
		try {
			return rmiConnection_.getTransactionIsolation();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * The first warning reported by calls on this Connection is returned.
	 * 
	 * <P>
	 * <B>Note:</B> Subsequent warnings will be chained to this SQLWarning.
	 * 
	 * @return the first SQLWarning or null
	 */
	public SQLWarning getWarnings() throws SQLException {
		try {
			return rmiConnection_.getWarnings();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * After this call, getWarnings returns null until a new warning is reported
	 * for this Connection.
	 */
	public void clearWarnings() throws SQLException {
		try {
			rmiConnection_.clearWarnings();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	// JDBC 2.0 methods
	// Added by Mike Jennings (mjenning@islandnet.com)
	// sometime in the summer of 1999
	// Implementation added by Peter Hearty, Aug 2000, peter.hearty@lutris.com.

	public PreparedStatement prepareStatement(String sql, int resultSetType,
			int resultSetConcurrency) throws SQLException {
		try {
			return new RJPreparedStatement(rmiConnection_.prepareStatement(sql,
					resultSetType, resultSetConcurrency), this);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public CallableStatement prepareCall(String sql, int resultSetType,
			int resultSetConcurrency) throws SQLException {
		try {
			return new RJCallableStatement(rmiConnection_.prepareCall(sql,
					resultSetType, resultSetConcurrency), this);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public java.util.Map<String,Class<?>> getTypeMap() throws SQLException {
		try {
			return rmiConnection_.getTypeMap();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public Statement createStatement(int resultSetType, int resultSetConcurrency)
			throws SQLException {
		try {
			return new RJStatement(rmiConnection_.createStatement(
					resultSetType, resultSetConcurrency), this);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * TBD XA public void startGlobalTransaction (Xid xid) throws XAException {
	 * try { rmiConnection_.startGlobalTransaction (xid); }
	 * catch(RemoteException e) { throw new XAException(e.getMessage()); } }
	 **/

	// --------------------------JDBC 3.0-----------------------------

	public void setHoldability(int holdability) throws SQLException {
		try {
			rmiConnection_.setHoldability(holdability);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public int getHoldability() throws SQLException {
		try {
			return rmiConnection_.getHoldability();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public Savepoint setSavepoint() throws SQLException {
		try {
			return new RJSavepoint(rmiConnection_.setSavepoint());
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public Savepoint setSavepoint(String name) throws SQLException {
		try {
			return new RJSavepoint(rmiConnection_.setSavepoint(name));
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public void rollback(Savepoint savepoint) throws SQLException {
		try {
			rmiConnection_.rollback(savepoint);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		try {
			rmiConnection_.releaseSavepoint(savepoint);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public Statement createStatement(int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		try {
			return new RJStatement(rmiConnection_.createStatement(
					resultSetType, resultSetConcurrency, resultSetHoldability),
					this);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public PreparedStatement prepareStatement(String sql, int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		try {
			return new RJPreparedStatement(rmiConnection_.prepareStatement(sql,
					resultSetType, resultSetConcurrency, resultSetHoldability),
					this);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public CallableStatement prepareCall(String sql, int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		try {
			return new RJCallableStatement(rmiConnection_.prepareCall(sql,
					resultSetType, resultSetConcurrency, resultSetHoldability),
					this);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
			throws SQLException {
		try {
			return new RJPreparedStatement(rmiConnection_.prepareStatement(sql,
					autoGeneratedKeys), this);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public PreparedStatement prepareStatement(String sql, int columnIndexes[])
			throws SQLException {
		try {
			return new RJPreparedStatement(rmiConnection_.prepareStatement(sql,
					columnIndexes), this);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public PreparedStatement prepareStatement(String sql, String columnNames[])
			throws SQLException {
		try {
			return new RJPreparedStatement(rmiConnection_.prepareStatement(sql,
					columnNames), this);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public Clob createClob() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Blob createBlob() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NClob createNClob() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SQLXML createSQLXML() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isValid(int timeout) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setClientInfo(String name, String value)
			throws SQLClientInfoException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setClientInfo(Properties properties)
			throws SQLClientInfoException {
		// TODO Auto-generated method stub

	}

	@Override
	public String getClientInfo(String name) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Properties getClientInfo() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Array createArrayOf(String typeName, Object[] elements)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Struct createStruct(String typeName, Object[] attributes)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

};
