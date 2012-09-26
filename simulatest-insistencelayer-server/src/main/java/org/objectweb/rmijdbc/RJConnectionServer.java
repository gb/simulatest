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

import java.sql.*;

import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.server.Unreferenced;

/**InstantDB
 import javax.transaction.xa.*;
 import org.enhydra.instantdb.jdbc.ConnectionExtensions;
 InstantDB**/

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
public class RJConnectionServer extends UnicastRemoteObject implements RJConnectionInterface, Unreferenced {

	private static final long serialVersionUID = 6848526836582285691L;

	java.sql.Connection jdbcConnection_;

	public RJConnectionServer(java.sql.Connection c) throws RemoteException {
		super(RMIRepository.rmiJdbcListenerPort,
				RMIRepository.rmiClientSocketFactory,
				RMIRepository.rmiServerSocketFactory);
		jdbcConnection_ = c;
	}

	public void unreferenced() {
		try {
			if (jdbcConnection_ != null) {
				// Try to rollback transaction if any...
				try {
					if (!jdbcConnection_.getAutoCommit())
						jdbcConnection_.rollback();
				} catch (Exception e) {
					// ignore
				}
				jdbcConnection_.close();
				jdbcConnection_ = null;
			}
		} catch (Exception e) {
			jdbcConnection_ = null;
		}
		Runtime.getRuntime().gc();
	}

	protected void finalize() throws Throwable {
		if (jdbcConnection_ != null) {
			jdbcConnection_.close();
		}
		Runtime.getRuntime().gc();
	}

	/**
	 * SQL statements without parameters are normally executed using Statement
	 * objects. If the same SQL statement is executed many times, it is more
	 * efficient to use a PreparedStatement
	 * 
	 * @return a new Statement object
	 */
	public RJStatementInterface createStatement() throws RemoteException,
			SQLException {
		return new RJStatementServer(jdbcConnection_.createStatement());
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
	 * however, it does affect which method throws certain
	 * java.rmi.RemoteExceptions.
	 * 
	 * @param sql
	 *            a SQL statement that may contain one or more '?' IN parameter
	 *            placeholders
	 * 
	 * @return a new PreparedStatement object containing the pre-compiled
	 *         statement
	 */
	public RJPreparedStatementInterface prepareStatement(String sql)
			throws RemoteException, SQLException {
		return new RJPreparedStatementServer(
				jdbcConnection_.prepareStatement(sql));
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
	 * which method throws certain java.rmi.RemoteExceptions.
	 * 
	 * @param sql
	 *            a SQL statement that may contain one or more '?' parameter
	 *            placeholders. Typically this statement is a JDBC function call
	 *            escape string.
	 * 
	 * @return a new CallableStatement object containing the pre-compiled SQL
	 *         statement
	 */
	public RJCallableStatementInterface prepareCall(String sql)
			throws RemoteException, SQLException {
		return new RJCallableStatementServer(jdbcConnection_.prepareCall(sql));
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
	public String nativeSQL(String sql) throws RemoteException, SQLException {
		return jdbcConnection_.nativeSQL(sql);
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
	public void setAutoCommit(boolean autoCommit) throws RemoteException,
			SQLException {
		jdbcConnection_.setAutoCommit(autoCommit);
	}

	/**
	 * Get the current auto-commit state.
	 * 
	 * @return Current state of auto-commit mode.
	 * @see #setAutoCommit
	 */
	public boolean getAutoCommit() throws RemoteException, SQLException {
		return jdbcConnection_.getAutoCommit();
	}

	/**
	 * Commit makes all changes made since the previous commit/rollback
	 * permanent and releases any database locks currently held by the
	 * Connection. This method should only be used when auto commit has been
	 * disabled.
	 * 
	 * @see #setAutoCommit
	 */
	public void commit() throws RemoteException, SQLException {
		jdbcConnection_.commit();
	}

	/**
	 * Rollback drops all changes made since the previous commit/rollback and
	 * releases any database locks currently held by the Connection. This method
	 * should only be used when auto commit has been disabled.
	 * 
	 * @see #setAutoCommit
	 */
	public void rollback() throws RemoteException, SQLException {
		jdbcConnection_.rollback();
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
	public void close() throws RemoteException, SQLException {
		if (jdbcConnection_ != null)
			jdbcConnection_.close();
		Runtime.getRuntime().gc(); // Request for garbage collection
	}

	/**
	 * Tests to see if a Connection is closed.
	 * 
	 * @return true if the connection is closed; false if it's still open
	 */
	public boolean isClosed() throws RemoteException, SQLException {
		return jdbcConnection_.isClosed();
	}

	// ======================================================================
	// Advanced features:

	/**
	 * A Connection's database is able to provide information describing its
	 * tables, its supported SQL grammar, its stored procedures, the
	 * capabilities of this connection, etc. This information is made available
	 * through a DatabaseMetaData object.
	 * 
	 * @return a RJDatabaseMetaData object for this Connection
	 */
	public RJDatabaseMetaDataInterface getMetaData() throws RemoteException,
			SQLException {
		return new RJDatabaseMetaDataServer(jdbcConnection_.getMetaData());
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
	public void setReadOnly(boolean readOnly) throws RemoteException,
			SQLException {
		jdbcConnection_.setReadOnly(readOnly);
	}

	/**
	 * Tests to see if the connection is in read-only mode.
	 * 
	 * @return true if connection is read-only
	 */
	public boolean isReadOnly() throws RemoteException, SQLException {
		return jdbcConnection_.isReadOnly();
	}

	/**
	 * A sub-space of this Connection's database may be selected by setting a
	 * catalog name. If the driver does not support catalogs it will silently
	 * ignore this request.
	 */
	public void setCatalog(String catalog) throws RemoteException, SQLException {
		jdbcConnection_.setCatalog(catalog);
	}

	/**
	 * Return the Connection's current catalog name.
	 * 
	 * @return the current catalog name or null
	 */
	public String getCatalog() throws RemoteException, SQLException {
		return jdbcConnection_.getCatalog();
	}

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
	public void setTransactionIsolation(int level) throws RemoteException,
			SQLException {
		jdbcConnection_.setTransactionIsolation(level);
	}

	/**
	 * Get this Connection's current transaction isolation mode.
	 * 
	 * @return the current TRANSACTION_* mode value
	 */
	public int getTransactionIsolation() throws RemoteException, SQLException {
		return jdbcConnection_.getTransactionIsolation();
	}

	/**
	 * The first warning reported by calls on this Connection is returned.
	 * 
	 * <P>
	 * <B>Note:</B> Subsequent warnings will be chained to this SQLWarning.
	 * 
	 * @return the first SQLWarning or null
	 */
	public SQLWarning getWarnings() throws RemoteException, SQLException {
		return jdbcConnection_.getWarnings();
	}

	/**
	 * After this call, getWarnings returns null until a new warning is reported
	 * for this Connection.
	 */
	public void clearWarnings() throws RemoteException, SQLException {
		jdbcConnection_.clearWarnings();
	}

	// JDBC 2.0 methods
	// Added by Peter Hearty, Aug 2000, peter.hearty@lutris.com.

	public void setTypeMap(java.util.Map<String,Class<?>> map) throws RemoteException, SQLException {
		jdbcConnection_.setTypeMap(map);
	}

	public RJPreparedStatementInterface prepareStatement(String sql,
			int resultSetType, int resultSetConcurrency)
			throws RemoteException, SQLException {
		return new RJPreparedStatementServer(jdbcConnection_.prepareStatement(
				sql, resultSetType, resultSetConcurrency));
	}

	public RJCallableStatementInterface prepareCall(String sql,
			int resultSetType, int resultSetConcurrency)
			throws RemoteException, SQLException {
		return new RJCallableStatementServer(jdbcConnection_.prepareCall(sql,
				resultSetType, resultSetConcurrency));
	}

	public java.util.Map<String,Class<?>> getTypeMap() throws RemoteException, SQLException {
		return jdbcConnection_.getTypeMap();
	}

	public RJStatementInterface createStatement(int resultSetType,
			int resultSetConcurrency) throws RemoteException, SQLException {
		return new RJStatementServer(jdbcConnection_.createStatement(
				resultSetType, resultSetConcurrency));
	}

	/**
	 * InstantDB
	 * 
	 * // InstantDB specific extensions.
	 * 
	 * public Properties getProperties () throws java.rmi.RemoteException,
	 * SQLException { return
	 * ((ConnectionExtensions)jdbcConnection_).getProperties (); }
	 * 
	 * public Object getLastValueInserted ( String tableName, String columnName)
	 * throws java.rmi.RemoteException, SQLException { return
	 * ((ConnectionExtensions)jdbcConnection_).getLastValueInserted (tableName,
	 * columnName); }
	 * 
	 * public void startGlobalTransaction (Xid xid) throws
	 * java.rmi.RemoteException, XAException {
	 * ((ConnectionExtensions)jdbcConnection_).startGlobalTransaction (xid); }
	 * 
	 * public int prepare() throws java.rmi.RemoteException, XAException {
	 * return ((ConnectionExtensions)jdbcConnection_).prepare (); }
	 * 
	 * public String getDatabaseId() throws java.rmi.RemoteException,
	 * SQLException { return
	 * ((ConnectionExtensions)jdbcConnection_).getDatabaseId(); } InstantDB
	 **/

	// --------------------------JDBC 3.0-----------------------------

	public void setHoldability(int holdability)
			throws java.rmi.RemoteException, SQLException {
		jdbcConnection_.setHoldability(holdability);
	}

	public int getHoldability() throws java.rmi.RemoteException, SQLException {
		return jdbcConnection_.getHoldability();
	}

	public RJSavepointInterface setSavepoint() throws java.rmi.RemoteException,
			SQLException {
		return new RJSavepointServer(jdbcConnection_.setSavepoint());
	}

	public RJSavepointInterface setSavepoint(String name)
			throws java.rmi.RemoteException, SQLException {
		return new RJSavepointServer(jdbcConnection_.setSavepoint(name));
	}

	public void rollback(Savepoint savepoint) throws java.rmi.RemoteException,
			SQLException {
		jdbcConnection_.rollback(savepoint);
	}

	public void releaseSavepoint(Savepoint savepoint)
			throws java.rmi.RemoteException, SQLException {
		jdbcConnection_.releaseSavepoint(savepoint);
	}

	public RJStatementInterface createStatement(int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
			throws java.rmi.RemoteException, SQLException {
		return new RJStatementServer(jdbcConnection_.createStatement(
				resultSetType, resultSetConcurrency, resultSetHoldability));
	}

	public RJPreparedStatementInterface prepareStatement(String sql,
			int resultSetType, int resultSetConcurrency,
			int resultSetHoldability) throws java.rmi.RemoteException,
			SQLException {
		return new RJPreparedStatementServer(jdbcConnection_.prepareStatement(
				sql, resultSetType, resultSetConcurrency, resultSetHoldability));
	}

	public RJCallableStatementInterface prepareCall(String sql,
			int resultSetType, int resultSetConcurrency,
			int resultSetHoldability) throws java.rmi.RemoteException,
			SQLException {
		return new RJCallableStatementServer(jdbcConnection_.prepareCall(sql,
				resultSetType, resultSetConcurrency, resultSetHoldability));
	}

	public RJPreparedStatementInterface prepareStatement(String sql,
			int autoGeneratedKeys) throws java.rmi.RemoteException,
			SQLException {
		return new RJPreparedStatementServer(jdbcConnection_.prepareStatement(
				sql, autoGeneratedKeys));
	}

	public RJPreparedStatementInterface prepareStatement(String sql,
			int columnIndexes[]) throws java.rmi.RemoteException, SQLException {
		return new RJPreparedStatementServer(jdbcConnection_.prepareStatement(
				sql, columnIndexes));
	}

	public RJPreparedStatementInterface prepareStatement(String sql,
			String columnNames[]) throws java.rmi.RemoteException, SQLException {
		return new RJPreparedStatementServer(jdbcConnection_.prepareStatement(
				sql, columnNames));
	}

};
