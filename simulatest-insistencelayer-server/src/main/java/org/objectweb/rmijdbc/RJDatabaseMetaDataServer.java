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

/**
 * This class provides information about the database as a whole.
 * 
 * <P>
 * Many of the methods here return lists of information in ResultSets. You can
 * use the normal ResultSet methods such as getString and getInt to retrieve the
 * data from these ResultSets. If a given form of metadata is not available,
 * these methods should throw a java.rmi.RemoteException.
 * 
 * <P>
 * Some of these methods take arguments that are String patterns. These
 * arguments all have names such as fooPattern. Within a pattern String, "%"
 * means match any substring of 0 or more characters, and "_" means match any
 * one character. Only metadata entries matching the search pattern are
 * returned. If a search pattern argument is set to a null ref, it means that
 * argument's criteria should be dropped from the search.
 * 
 * <P>
 * A java.rmi.RemoteException will be thrown if a driver does not support a meta
 * data method. In the case of methods that return a ResultSet, either a
 * ResultSet (which may be empty) is returned or a java.rmi.RemoteException is
 * thrown.
 */
public class RJDatabaseMetaDataServer extends UnicastRemoteObject implements RJDatabaseMetaDataInterface, Unreferenced {

	private static final long serialVersionUID = -3885238295078139965L;
	java.sql.DatabaseMetaData jdbcMetadata_;

	public RJDatabaseMetaDataServer(java.sql.DatabaseMetaData d)
			throws RemoteException, SQLException {
		super(RMIRepository.rmiJdbcListenerPort,
				RMIRepository.rmiClientSocketFactory,
				RMIRepository.rmiServerSocketFactory);
		jdbcMetadata_ = d;
	}

	public void unreferenced() {
		Runtime.getRuntime().gc();
	}

	// ----------------------------------------------------------------------
	// First, a variety of minor information about the target database.

	/**
	 * Can all the procedures returned by getProcedures be called by the current
	 * user?
	 * 
	 * @return true if so
	 */
	public boolean allProceduresAreCallable() throws RemoteException,
			SQLException {
		return jdbcMetadata_.allProceduresAreCallable();
	}

	/**
	 * Can all the tables returned by getTable be SELECTed by the current user?
	 * 
	 * @return true if so
	 */
	public boolean allTablesAreSelectable() throws RemoteException,
			SQLException {
		return jdbcMetadata_.allTablesAreSelectable();
	}

	/**
	 * What's the url for this database?
	 * 
	 * @return the url or null if it can't be generated
	 */
	public String getURL() throws RemoteException, SQLException {
		return jdbcMetadata_.getURL();
	}

	/**
	 * What's our user name as known to the database?
	 * 
	 * @return our database user name
	 */
	public String getUserName() throws RemoteException, SQLException {
		return jdbcMetadata_.getUserName();
	}

	/**
	 * Is the database in read-only mode?
	 * 
	 * @return true if so
	 */
	public boolean isReadOnly() throws RemoteException, SQLException {
		return jdbcMetadata_.isReadOnly();
	}

	/**
	 * Are NULL values sorted high?
	 * 
	 * @return true if so
	 */
	public boolean nullsAreSortedHigh() throws RemoteException, SQLException {
		return jdbcMetadata_.nullsAreSortedHigh();
	}

	/**
	 * Are NULL values sorted low?
	 * 
	 * @return true if so
	 */
	public boolean nullsAreSortedLow() throws RemoteException, SQLException {
		return jdbcMetadata_.nullsAreSortedLow();
	}

	/**
	 * Are NULL values sorted at the start regardless of sort order?
	 * 
	 * @return true if so
	 */
	public boolean nullsAreSortedAtStart() throws RemoteException, SQLException {
		return jdbcMetadata_.nullsAreSortedAtStart();
	}

	/**
	 * Are NULL values sorted at the end regardless of sort order?
	 * 
	 * @return true if so
	 */
	public boolean nullsAreSortedAtEnd() throws RemoteException, SQLException {
		return jdbcMetadata_.nullsAreSortedAtEnd();
	}

	/**
	 * What's the name of this database product?
	 * 
	 * @return database product name
	 */
	public String getDatabaseProductName() throws RemoteException, SQLException {
		return jdbcMetadata_.getDatabaseProductName();
	}

	/**
	 * What's the version of this database product?
	 * 
	 * @return database version
	 */
	public String getDatabaseProductVersion() throws RemoteException,
			SQLException {
		return jdbcMetadata_.getDatabaseProductVersion();
	}

	/**
	 * What's the name of this JDBC driver?
	 * 
	 * @return JDBC driver name
	 */
	public String getDriverName() throws RemoteException, SQLException {
		return "RmiJdbc!" + jdbcMetadata_.getDriverName();
	}

	/**
	 * What's the version of this JDBC driver?
	 * 
	 * @return JDBC driver version
	 */
	public String getDriverVersion() throws RemoteException, SQLException {
		return jdbcMetadata_.getDriverVersion();
	}

	/**
	 * What's this JDBC driver's major version number?
	 * 
	 * @return JDBC driver major version
	 */
	public int getDriverMajorVersion() {
		return jdbcMetadata_.getDriverMajorVersion();
	}

	/**
	 * What's this JDBC driver's minor version number?
	 * 
	 * @return JDBC driver minor version number
	 */
	public int getDriverMinorVersion() {
		return jdbcMetadata_.getDriverMinorVersion();
	}

	/**
	 * Does the database store tables in a local file?
	 * 
	 * @return true if so
	 */
	public boolean usesLocalFiles() throws RemoteException, SQLException {
		return jdbcMetadata_.usesLocalFiles();
	}

	/**
	 * Does the database use a file for each table?
	 * 
	 * @return true if the database uses a local file for each table
	 */
	public boolean usesLocalFilePerTable() throws RemoteException, SQLException {
		return jdbcMetadata_.usesLocalFilePerTable();
	}

	/**
	 * Does the database treat mixed case unquoted SQL identifiers as case
	 * sensitive and as a result store them in mixed case?
	 * 
	 * A JDBC-Compliant driver will always return false.
	 * 
	 * @return true if so
	 */
	public boolean supportsMixedCaseIdentifiers() throws RemoteException,
			SQLException {
		return jdbcMetadata_.supportsMixedCaseIdentifiers();
	}

	/**
	 * Does the database treat mixed case unquoted SQL identifiers as case
	 * insensitive and store them in upper case?
	 * 
	 * @return true if so
	 */
	public boolean storesUpperCaseIdentifiers() throws RemoteException,
			SQLException {
		return jdbcMetadata_.storesUpperCaseIdentifiers();
	}

	/**
	 * Does the database treat mixed case unquoted SQL identifiers as case
	 * insensitive and store them in lower case?
	 * 
	 * @return true if so
	 */
	public boolean storesLowerCaseIdentifiers() throws RemoteException,
			SQLException {
		return jdbcMetadata_.storesLowerCaseIdentifiers();
	}

	/**
	 * Does the database treat mixed case unquoted SQL identifiers as case
	 * insensitive and store them in mixed case?
	 * 
	 * @return true if so
	 */
	public boolean storesMixedCaseIdentifiers() throws RemoteException,
			SQLException {
		return jdbcMetadata_.storesMixedCaseIdentifiers();
	}

	/**
	 * Does the database treat mixed case quoted SQL identifiers as case
	 * sensitive and as a result store them in mixed case?
	 * 
	 * A JDBC-Compliant driver will always return false.
	 * 
	 * @return true if so
	 */
	public boolean supportsMixedCaseQuotedIdentifiers() throws RemoteException,
			SQLException {
		return jdbcMetadata_.supportsMixedCaseQuotedIdentifiers();
	}

	/**
	 * Does the database treat mixed case quoted SQL identifiers as case
	 * insensitive and store them in upper case?
	 * 
	 * @return true if so
	 */
	public boolean storesUpperCaseQuotedIdentifiers() throws RemoteException,
			SQLException {
		return jdbcMetadata_.storesUpperCaseQuotedIdentifiers();
	}

	/**
	 * Does the database treat mixed case quoted SQL identifiers as case
	 * insensitive and store them in lower case?
	 * 
	 * @return true if so
	 */
	public boolean storesLowerCaseQuotedIdentifiers() throws RemoteException,
			SQLException {
		return jdbcMetadata_.storesLowerCaseQuotedIdentifiers();
	}

	/**
	 * Does the database treat mixed case quoted SQL identifiers as case
	 * insensitive and store them in mixed case?
	 * 
	 * @return true if so
	 */
	public boolean storesMixedCaseQuotedIdentifiers() throws RemoteException,
			SQLException {
		return jdbcMetadata_.storesMixedCaseQuotedIdentifiers();
	}

	/**
	 * What's the string used to quote SQL identifiers? This returns a space " "
	 * if identifier quoting isn't supported.
	 * 
	 * A JDBC-Compliant driver always uses a double quote character.
	 * 
	 * @return the quoting string
	 */
	public String getIdentifierQuoteString() throws RemoteException,
			SQLException {
		return jdbcMetadata_.getIdentifierQuoteString();
	}

	/**
	 * Get a comma separated list of all a database's SQL keywords that are NOT
	 * also SQL92 keywords.
	 * 
	 * @return the list
	 */
	public String getSQLKeywords() throws RemoteException, SQLException {
		return jdbcMetadata_.getSQLKeywords();
	}

	/**
	 * Get a comma separated list of math functions.
	 * 
	 * @return the list
	 */
	public String getNumericFunctions() throws RemoteException, SQLException {
		return jdbcMetadata_.getNumericFunctions();
	}

	/**
	 * Get a comma separated list of string functions.
	 * 
	 * @return the list
	 */
	public String getStringFunctions() throws RemoteException, SQLException {
		return jdbcMetadata_.getStringFunctions();
	}

	/**
	 * Get a comma separated list of system functions.
	 * 
	 * @return the list
	 */
	public String getSystemFunctions() throws RemoteException, SQLException {
		return jdbcMetadata_.getSystemFunctions();
	}

	/**
	 * Get a comma separated list of time and date functions.
	 * 
	 * @return the list
	 */
	public String getTimeDateFunctions() throws RemoteException, SQLException {
		return jdbcMetadata_.getTimeDateFunctions();
	}

	/**
	 * This is the string that can be used to escape '_' or '%' in the string
	 * pattern style catalog search parameters.
	 * 
	 * <P>
	 * The '_' character represents any single character.
	 * <P>
	 * The '%' character represents any sequence of zero or more characters.
	 * 
	 * @return the string used to escape wildcard characters
	 */
	public String getSearchStringEscape() throws RemoteException, SQLException {
		return jdbcMetadata_.getSearchStringEscape();
	}

	/**
	 * Get all the "extra" characters that can be used in unquoted identifier
	 * names (those beyond a-z, A-Z, 0-9 and _).
	 * 
	 * @return the string containing the extra characters
	 */
	public String getExtraNameCharacters() throws RemoteException, SQLException {
		return jdbcMetadata_.getExtraNameCharacters();
	}

	// --------------------------------------------------------------------
	// Functions describing which features are supported.

	/**
	 * Is "ALTER TABLE" with add column supported?
	 * 
	 * @return true if so
	 */
	public boolean supportsAlterTableWithAddColumn() throws RemoteException,
			SQLException {
		return jdbcMetadata_.supportsAlterTableWithAddColumn();
	}

	/**
	 * Is "ALTER TABLE" with drop column supported?
	 * 
	 * @return true if so
	 */
	public boolean supportsAlterTableWithDropColumn() throws RemoteException,
			SQLException {
		return jdbcMetadata_.supportsAlterTableWithDropColumn();
	}

	/**
	 * Is column aliasing supported?
	 * 
	 * <P>
	 * If so, the SQL AS clause can be used to provide names for computed
	 * columns or to provide alias names for columns as required.
	 * 
	 * A JDBC-Compliant driver always returns true.
	 * 
	 * @return true if so
	 */
	public boolean supportsColumnAliasing() throws RemoteException,
			SQLException {
		return jdbcMetadata_.supportsColumnAliasing();
	}

	/**
	 * Are concatenations between NULL and non-NULL values NULL?
	 * 
	 * A JDBC-Compliant driver always returns true.
	 * 
	 * @return true if so
	 */
	public boolean nullPlusNonNullIsNull() throws RemoteException, SQLException {
		return jdbcMetadata_.nullPlusNonNullIsNull();
	}

	/**
	 * Is the CONVERT function between SQL types supported?
	 * 
	 * @return true if so
	 */
	public boolean supportsConvert() throws RemoteException, SQLException {
		return jdbcMetadata_.supportsConvert();
	}

	/**
	 * Is CONVERT between the given SQL types supported?
	 * 
	 * @param fromType
	 *            the type to convert from
	 * @param toType
	 *            the type to convert to
	 * @return true if so
	 * @see Types
	 */
	public boolean supportsConvert(int fromType, int toType)
			throws RemoteException, SQLException {
		return jdbcMetadata_.supportsConvert();
	}

	/**
	 * Are table correlation names supported?
	 * 
	 * A JDBC-Compliant driver always returns true.
	 * 
	 * @return true if so
	 */
	public boolean supportsTableCorrelationNames() throws RemoteException,
			SQLException {
		return jdbcMetadata_.supportsTableCorrelationNames();
	}

	/**
	 * If table correlation names are supported, are they restricted to be
	 * different from the names of the tables?
	 * 
	 * @return true if so
	 */
	public boolean supportsDifferentTableCorrelationNames()
			throws RemoteException, SQLException {
		return jdbcMetadata_.supportsDifferentTableCorrelationNames();
	}

	/**
	 * Are expressions in "ORDER BY" lists supported?
	 * 
	 * @return true if so
	 */
	public boolean supportsExpressionsInOrderBy() throws RemoteException,
			SQLException {
		return jdbcMetadata_.supportsExpressionsInOrderBy();
	}

	/**
	 * Can an "ORDER BY" clause use columns not in the SELECT?
	 * 
	 * @return true if so
	 */
	public boolean supportsOrderByUnrelated() throws RemoteException,
			SQLException {
		return jdbcMetadata_.supportsOrderByUnrelated();
	}

	/**
	 * Is some form of "GROUP BY" clause supported?
	 * 
	 * @return true if so
	 */
	public boolean supportsGroupBy() throws RemoteException, SQLException {
		return jdbcMetadata_.supportsGroupBy();
	}

	/**
	 * Can a "GROUP BY" clause use columns not in the SELECT?
	 * 
	 * @return true if so
	 */
	public boolean supportsGroupByUnrelated() throws RemoteException,
			SQLException {
		return jdbcMetadata_.supportsGroupByUnrelated();
	}

	/**
	 * Can a "GROUP BY" clause add columns not in the SELECT provided it
	 * specifies all the columns in the SELECT?
	 * 
	 * @return true if so
	 */
	public boolean supportsGroupByBeyondSelect() throws RemoteException,
			SQLException {
		return jdbcMetadata_.supportsGroupByBeyondSelect();
	}

	/**
	 * Is the escape character in "LIKE" clauses supported?
	 * 
	 * A JDBC-Compliant driver always returns true.
	 * 
	 * @return true if so
	 */
	public boolean supportsLikeEscapeClause() throws RemoteException,
			SQLException {
		return jdbcMetadata_.supportsLikeEscapeClause();
	}

	/**
	 * Are multiple ResultSets from a single execute supported?
	 * 
	 * @return true if so
	 */
	public boolean supportsMultipleResultSets() throws RemoteException,
			SQLException {
		return jdbcMetadata_.supportsMultipleResultSets();
	}

	/**
	 * Can we have multiple transactions open at once (on different
	 * connections)?
	 * 
	 * @return true if so
	 */
	public boolean supportsMultipleTransactions() throws RemoteException,
			SQLException {
		return jdbcMetadata_.supportsMultipleTransactions();
	}

	/**
	 * Can columns be defined as non-nullable?
	 * 
	 * A JDBC-Compliant driver always returns true.
	 * 
	 * @return true if so
	 */
	public boolean supportsNonNullableColumns() throws RemoteException,
			SQLException {
		return jdbcMetadata_.supportsNonNullableColumns();
	}

	/**
	 * Is the ODBC Minimum SQL grammar supported?
	 * 
	 * All JDBC-Compliant drivers must return true.
	 * 
	 * @return true if so
	 */
	public boolean supportsMinimumSQLGrammar() throws RemoteException,
			SQLException {
		return jdbcMetadata_.supportsMinimumSQLGrammar();
	}

	/**
	 * Is the ODBC Core SQL grammar supported?
	 * 
	 * @return true if so
	 */
	public boolean supportsCoreSQLGrammar() throws RemoteException,
			SQLException {
		return jdbcMetadata_.supportsCoreSQLGrammar();
	}

	/**
	 * Is the ODBC Extended SQL grammar supported?
	 * 
	 * @return true if so
	 */
	public boolean supportsExtendedSQLGrammar() throws RemoteException,
			SQLException {
		return jdbcMetadata_.supportsExtendedSQLGrammar();
	}

	/**
	 * Is the ANSI92 entry level SQL grammar supported?
	 * 
	 * All JDBC-Compliant drivers must return true.
	 * 
	 * @return true if so
	 */
	public boolean supportsANSI92EntryLevelSQL() throws RemoteException,
			SQLException {
		return jdbcMetadata_.supportsANSI92EntryLevelSQL();
	}

	/**
	 * Is the ANSI92 intermediate SQL grammar supported?
	 * 
	 * @return true if so
	 */
	public boolean supportsANSI92IntermediateSQL() throws RemoteException,
			SQLException {
		return jdbcMetadata_.supportsANSI92IntermediateSQL();
	}

	/**
	 * Is the ANSI92 full SQL grammar supported?
	 * 
	 * @return true if so
	 */
	public boolean supportsANSI92FullSQL() throws RemoteException, SQLException {
		return jdbcMetadata_.supportsANSI92FullSQL();
	}

	/**
	 * Is the SQL Integrity Enhancement Facility supported?
	 * 
	 * @return true if so
	 */
	public boolean supportsIntegrityEnhancementFacility()
			throws RemoteException, SQLException {
		return jdbcMetadata_.supportsIntegrityEnhancementFacility();
	}

	/**
	 * Is some form of outer join supported?
	 * 
	 * @return true if so
	 */
	public boolean supportsOuterJoins() throws RemoteException, SQLException {
		return jdbcMetadata_.supportsOuterJoins();
	}

	/**
	 * Are full nested outer joins supported?
	 * 
	 * @return true if so
	 */
	public boolean supportsFullOuterJoins() throws RemoteException,
			SQLException {
		return jdbcMetadata_.supportsFullOuterJoins();
	}

	/**
	 * Is there limited support for outer joins? (This will be true if
	 * supportFullOuterJoins is true.)
	 * 
	 * @return true if so
	 */
	public boolean supportsLimitedOuterJoins() throws RemoteException,
			SQLException {
		return jdbcMetadata_.supportsLimitedOuterJoins();
	}

	/**
	 * What's the database vendor's preferred term for "schema"?
	 * 
	 * @return the vendor term
	 */
	public String getSchemaTerm() throws RemoteException, SQLException {
		return jdbcMetadata_.getSchemaTerm();
	}

	/**
	 * What's the database vendor's preferred term for "procedure"?
	 * 
	 * @return the vendor term
	 */
	public String getProcedureTerm() throws RemoteException, SQLException {
		return jdbcMetadata_.getProcedureTerm();
	}

	/**
	 * What's the database vendor's preferred term for "catalog"?
	 * 
	 * @return the vendor term
	 */
	public String getCatalogTerm() throws RemoteException, SQLException {
		return jdbcMetadata_.getCatalogTerm();
	}

	/**
	 * Does a catalog appear at the start of a qualified table name? (Otherwise
	 * it appears at the end)
	 * 
	 * @return true if it appears at the start
	 */
	public boolean isCatalogAtStart() throws RemoteException, SQLException {
		return jdbcMetadata_.isCatalogAtStart();
	}

	/**
	 * What's the separator between catalog and table name?
	 * 
	 * @return the separator string
	 */
	public String getCatalogSeparator() throws RemoteException, SQLException {
		return jdbcMetadata_.getCatalogSeparator();
	}

	/**
	 * Can a schema name be used in a data manipulation statement?
	 * 
	 * @return true if so
	 */
	public boolean supportsSchemasInDataManipulation() throws RemoteException,
			SQLException {
		return jdbcMetadata_.supportsSchemasInDataManipulation();
	}

	/**
	 * Can a schema name be used in a procedure call statement?
	 * 
	 * @return true if so
	 */
	public boolean supportsSchemasInProcedureCalls() throws RemoteException,
			SQLException {
		return jdbcMetadata_.supportsSchemasInProcedureCalls();
	}

	/**
	 * Can a schema name be used in a table definition statement?
	 * 
	 * @return true if so
	 */
	public boolean supportsSchemasInTableDefinitions() throws RemoteException,
			SQLException {
		return jdbcMetadata_.supportsSchemasInTableDefinitions();
	}

	/**
	 * Can a schema name be used in an index definition statement?
	 * 
	 * @return true if so
	 */
	public boolean supportsSchemasInIndexDefinitions() throws RemoteException,
			SQLException {
		return jdbcMetadata_.supportsSchemasInIndexDefinitions();
	}

	/**
	 * Can a schema name be used in a privilege definition statement?
	 * 
	 * @return true if so
	 */
	public boolean supportsSchemasInPrivilegeDefinitions()
			throws RemoteException, SQLException {
		return jdbcMetadata_.supportsSchemasInPrivilegeDefinitions();
	}

	/**
	 * Can a catalog name be used in a data manipulation statement?
	 * 
	 * @return true if so
	 */
	public boolean supportsCatalogsInDataManipulation() throws RemoteException,
			SQLException {
		return jdbcMetadata_.supportsCatalogsInDataManipulation();
	}

	/**
	 * Can a catalog name be used in a procedure call statement?
	 * 
	 * @return true if so
	 */
	public boolean supportsCatalogsInProcedureCalls() throws RemoteException,
			SQLException {
		return jdbcMetadata_.supportsCatalogsInProcedureCalls();
	}

	/**
	 * Can a catalog name be used in a table definition statement?
	 * 
	 * @return true if so
	 */
	public boolean supportsCatalogsInTableDefinitions() throws RemoteException,
			SQLException {
		return jdbcMetadata_.supportsCatalogsInTableDefinitions();
	}

	/**
	 * Can a catalog name be used in an index definition statement?
	 * 
	 * @return true if so
	 */
	public boolean supportsCatalogsInIndexDefinitions() throws RemoteException,
			SQLException {
		return jdbcMetadata_.supportsCatalogsInIndexDefinitions();
	}

	/**
	 * Can a catalog name be used in a privilege definition statement?
	 * 
	 * @return true if so
	 */
	public boolean supportsCatalogsInPrivilegeDefinitions()
			throws RemoteException, SQLException {
		return jdbcMetadata_.supportsCatalogsInPrivilegeDefinitions();
	}

	/**
	 * Is positioned DELETE supported?
	 * 
	 * @return true if so
	 */
	public boolean supportsPositionedDelete() throws RemoteException,
			SQLException {
		return jdbcMetadata_.supportsPositionedDelete();
	}

	/**
	 * Is positioned UPDATE supported?
	 * 
	 * @return true if so
	 */
	public boolean supportsPositionedUpdate() throws RemoteException,
			SQLException {
		return jdbcMetadata_.supportsPositionedUpdate();
	}

	/**
	 * Is SELECT for UPDATE supported?
	 * 
	 * @return true if so
	 */
	public boolean supportsSelectForUpdate() throws RemoteException,
			SQLException {
		return jdbcMetadata_.supportsSelectForUpdate();
	}

	/**
	 * Are stored procedure calls using the stored procedure escape syntax
	 * supported?
	 * 
	 * @return true if so
	 */
	public boolean supportsStoredProcedures() throws RemoteException,
			SQLException {
		return jdbcMetadata_.supportsStoredProcedures();
	}

	/**
	 * Are subqueries in comparison expressions supported?
	 * 
	 * A JDBC-Compliant driver always returns true.
	 * 
	 * @return true if so
	 */
	public boolean supportsSubqueriesInComparisons() throws RemoteException,
			SQLException {
		return jdbcMetadata_.supportsSubqueriesInComparisons();
	}

	/**
	 * Are subqueries in 'exists' expressions supported?
	 * 
	 * A JDBC-Compliant driver always returns true.
	 * 
	 * @return true if so
	 */
	public boolean supportsSubqueriesInExists() throws RemoteException,
			SQLException {
		return jdbcMetadata_.supportsSubqueriesInExists();
	}

	/**
	 * Are subqueries in 'in' statements supported?
	 * 
	 * A JDBC-Compliant driver always returns true.
	 * 
	 * @return true if so
	 */
	public boolean supportsSubqueriesInIns() throws RemoteException,
			SQLException {
		return jdbcMetadata_.supportsSubqueriesInIns();
	}

	/**
	 * Are subqueries in quantified expressions supported?
	 * 
	 * A JDBC-Compliant driver always returns true.
	 * 
	 * @return true if so
	 */
	public boolean supportsSubqueriesInQuantifieds() throws RemoteException,
			SQLException {
		return jdbcMetadata_.supportsSubqueriesInQuantifieds();
	}

	/**
	 * Are correlated subqueries supported?
	 * 
	 * A JDBC-Compliant driver always returns true.
	 * 
	 * @return true if so
	 */
	public boolean supportsCorrelatedSubqueries() throws RemoteException,
			SQLException {
		return jdbcMetadata_.supportsCorrelatedSubqueries();
	}

	/**
	 * Is SQL UNION supported?
	 * 
	 * @return true if so
	 */
	public boolean supportsUnion() throws RemoteException, SQLException {
		return jdbcMetadata_.supportsUnion();
	}

	/**
	 * Is SQL UNION ALL supported?
	 * 
	 * @return true if so
	 */
	public boolean supportsUnionAll() throws RemoteException, SQLException {
		return jdbcMetadata_.supportsUnionAll();
	}

	/**
	 * Can cursors remain open across commits?
	 * 
	 * @return true if cursors always remain open; false if they might not
	 *         remain open
	 */
	public boolean supportsOpenCursorsAcrossCommit() throws RemoteException,
			SQLException {
		return jdbcMetadata_.supportsOpenCursorsAcrossCommit();
	}

	/**
	 * Can cursors remain open across rollbacks?
	 * 
	 * @return true if cursors always remain open; false if they might not
	 *         remain open
	 */
	public boolean supportsOpenCursorsAcrossRollback() throws RemoteException,
			SQLException {
		return jdbcMetadata_.supportsOpenCursorsAcrossRollback();
	}

	/**
	 * Can statements remain open across commits?
	 * 
	 * @return true if statements always remain open; false if they might not
	 *         remain open
	 */
	public boolean supportsOpenStatementsAcrossCommit() throws RemoteException,
			SQLException {
		return jdbcMetadata_.supportsOpenStatementsAcrossCommit();
	}

	/**
	 * Can statements remain open across rollbacks?
	 * 
	 * @return true if statements always remain open; false if they might not
	 *         remain open
	 */
	public boolean supportsOpenStatementsAcrossRollback()
			throws RemoteException, SQLException {
		return jdbcMetadata_.supportsOpenStatementsAcrossRollback();
	}

	// ----------------------------------------------------------------------
	// The following group of methods exposes various limitations
	// based on the target database with the current driver.
	// Unless otherwise specified, a result of zero means there is no
	// limit, or the limit is not known.

	/**
	 * How many hex characters can you have in an inline binary literal?
	 * 
	 * @return max literal length
	 */
	public int getMaxBinaryLiteralLength() throws RemoteException, SQLException {
		return jdbcMetadata_.getMaxBinaryLiteralLength();
	}

	/**
	 * What's the max length for a character literal?
	 * 
	 * @return max literal length
	 */
	public int getMaxCharLiteralLength() throws RemoteException, SQLException {
		return jdbcMetadata_.getMaxCharLiteralLength();
	}

	/**
	 * What's the limit on column name length?
	 * 
	 * @return max literal length
	 */
	public int getMaxColumnNameLength() throws RemoteException, SQLException {
		return jdbcMetadata_.getMaxColumnNameLength();
	}

	/**
	 * What's the maximum number of columns in a "GROUP BY" clause?
	 * 
	 * @return max number of columns
	 */
	public int getMaxColumnsInGroupBy() throws RemoteException, SQLException {
		return jdbcMetadata_.getMaxColumnsInGroupBy();
	}

	/**
	 * What's the maximum number of columns allowed in an index?
	 * 
	 * @return max columns
	 */
	public int getMaxColumnsInIndex() throws RemoteException, SQLException {
		return jdbcMetadata_.getMaxColumnsInIndex();
	}

	/**
	 * What's the maximum number of columns in an "ORDER BY" clause?
	 * 
	 * @return max columns
	 */
	public int getMaxColumnsInOrderBy() throws RemoteException, SQLException {
		return jdbcMetadata_.getMaxColumnsInOrderBy();
	}

	/**
	 * What's the maximum number of columns in a "SELECT" list?
	 * 
	 * @return max columns
	 */
	public int getMaxColumnsInSelect() throws RemoteException, SQLException {
		return jdbcMetadata_.getMaxColumnsInSelect();
	}

	/**
	 * What's the maximum number of columns in a table?
	 * 
	 * @return max columns
	 */
	public int getMaxColumnsInTable() throws RemoteException, SQLException {
		return jdbcMetadata_.getMaxColumnsInTable();
	}

	/**
	 * How many active connections can we have at a time to this database?
	 * 
	 * @return max connections
	 */
	public int getMaxConnections() throws RemoteException, SQLException {
		return jdbcMetadata_.getMaxConnections();
	}

	/**
	 * What's the maximum cursor name length?
	 * 
	 * @return max cursor name length in bytes
	 */
	public int getMaxCursorNameLength() throws RemoteException, SQLException {
		return jdbcMetadata_.getMaxCursorNameLength();
	}

	/**
	 * What's the maximum length of an index (in bytes)?
	 * 
	 * @return max index length in bytes
	 */
	public int getMaxIndexLength() throws RemoteException, SQLException {
		return jdbcMetadata_.getMaxIndexLength();
	}

	/**
	 * What's the maximum length allowed for a schema name?
	 * 
	 * @return max name length in bytes
	 */
	public int getMaxSchemaNameLength() throws RemoteException, SQLException {
		return jdbcMetadata_.getMaxSchemaNameLength();
	}

	/**
	 * What's the maximum length of a procedure name?
	 * 
	 * @return max name length in bytes
	 */
	public int getMaxProcedureNameLength() throws RemoteException, SQLException {
		return jdbcMetadata_.getMaxProcedureNameLength();
	}

	/**
	 * What's the maximum length of a catalog name?
	 * 
	 * @return max name length in bytes
	 */
	public int getMaxCatalogNameLength() throws RemoteException, SQLException {
		return jdbcMetadata_.getMaxCatalogNameLength();
	}

	/**
	 * What's the maximum length of a single row?
	 * 
	 * @return max row size in bytes
	 */
	public int getMaxRowSize() throws RemoteException, SQLException {
		return jdbcMetadata_.getMaxRowSize();
	}

	/**
	 * Did getMaxRowSize() include LONGVARCHAR and LONGVARBINARY blobs?
	 * 
	 * @return true if so
	 */
	public boolean doesMaxRowSizeIncludeBlobs() throws RemoteException,
			SQLException {
		return jdbcMetadata_.doesMaxRowSizeIncludeBlobs();
	}

	/**
	 * What's the maximum length of a SQL statement?
	 * 
	 * @return max length in bytes
	 */
	public int getMaxStatementLength() throws RemoteException, SQLException {
		return jdbcMetadata_.getMaxStatementLength();
	}

	/**
	 * How many active statements can we have open at one time to this database?
	 * 
	 * @return the maximum
	 */
	public int getMaxStatements() throws RemoteException, SQLException {
		return jdbcMetadata_.getMaxStatements();
	}

	/**
	 * What's the maximum length of a table name?
	 * 
	 * @return max name length in bytes
	 */
	public int getMaxTableNameLength() throws RemoteException, SQLException {
		return jdbcMetadata_.getMaxTableNameLength();
	}

	/**
	 * What's the maximum number of tables in a SELECT?
	 * 
	 * @return the maximum
	 */
	public int getMaxTablesInSelect() throws RemoteException, SQLException {
		return jdbcMetadata_.getMaxTablesInSelect();
	}

	/**
	 * What's the maximum length of a user name?
	 * 
	 * @return max name length in bytes
	 */
	public int getMaxUserNameLength() throws RemoteException, SQLException {
		return jdbcMetadata_.getMaxUserNameLength();
	}

	// ----------------------------------------------------------------------

	/**
	 * What's the database's default transaction isolation level? The values are
	 * defined in java.sql.Connection.
	 * 
	 * @return the default isolation level
	 * @see Connection
	 */
	public int getDefaultTransactionIsolation() throws RemoteException,
			SQLException {
		return jdbcMetadata_.getDefaultTransactionIsolation();
	}

	/**
	 * Are transactions supported? If not, commit is a noop and the isolation
	 * level is TRANSACTION_NONE.
	 * 
	 * @return true if transactions are supported
	 */
	public boolean supportsTransactions() throws RemoteException, SQLException {
		return jdbcMetadata_.supportsTransactions();
	}

	/**
	 * Does the database support the given transaction isolation level?
	 * 
	 * @param level
	 *            the values are defined in java.sql.Connection
	 * @return true if so
	 * @see Connection
	 */
	public boolean supportsTransactionIsolationLevel(int level)
			throws RemoteException, SQLException {
		return jdbcMetadata_.supportsTransactionIsolationLevel(level);
	}

	/**
	 * Are both data definition and data manipulation statements within a
	 * transaction supported?
	 * 
	 * @return true if so
	 */
	public boolean supportsDataDefinitionAndDataManipulationTransactions()
			throws RemoteException, SQLException {
		return jdbcMetadata_
				.supportsDataDefinitionAndDataManipulationTransactions();
	}

	/**
	 * Are only data manipulation statements within a transaction supported?
	 * 
	 * @return true if so
	 */
	public boolean supportsDataManipulationTransactionsOnly()
			throws RemoteException, SQLException {
		return jdbcMetadata_.supportsDataManipulationTransactionsOnly();
	}

	/**
	 * Does a data definition statement within a transaction force the
	 * transaction to commit?
	 * 
	 * @return true if so
	 */
	public boolean dataDefinitionCausesTransactionCommit()
			throws RemoteException, SQLException {
		return jdbcMetadata_.dataDefinitionCausesTransactionCommit();
	}

	/**
	 * Is a data definition statement within a transaction ignored?
	 * 
	 * @return true if so
	 */
	public boolean dataDefinitionIgnoredInTransactions()
			throws RemoteException, SQLException {
		return jdbcMetadata_.dataDefinitionIgnoredInTransactions();
	}

	/**
	 * Get a description of stored procedures available in a catalog.
	 * 
	 * <P>
	 * Only procedure descriptions matching the schema and procedure name
	 * criteria are returned. They are ordered by PROCEDURE_SCHEM, and
	 * PROCEDURE_NAME.
	 * 
	 * <P>
	 * Each procedure description has the the following columns:
	 * <OL>
	 * <LI><B>PROCEDURE_CAT</B> String => procedure catalog (may be null)
	 * <LI><B>PROCEDURE_SCHEM</B> String => procedure schema (may be null)
	 * <LI><B>PROCEDURE_NAME</B> String => procedure name
	 * <LI>reserved for future use
	 * <LI>reserved for future use
	 * <LI>reserved for future use
	 * <LI><B>REMARKS</B> String => explanatory comment on the procedure
	 * <LI><B>PROCEDURE_TYPE</B> short => kind of procedure:
	 * <UL>
	 * <LI>procedureResultUnknown - May return a result
	 * <LI>procedureNoResult - Does not return a result
	 * <LI>procedureReturnsResult - Returns a result
	 * </UL>
	 * </OL>
	 * 
	 * @param catalog
	 *            a catalog name; "" retrieves those without a catalog; null
	 *            means drop catalog name from the selection criteria
	 * @param schemaPattern
	 *            a schema name pattern; "" retrieves those without a schema
	 * @param procedureNamePattern
	 *            a procedure name pattern
	 * @return ResultSet - each row is a procedure description
	 * @see #getSearchStringEscape
	 */
	public RJResultSetInterface getProcedures(String catalog,
			String schemaPattern, String procedureNamePattern)
			throws RemoteException, SQLException {
		return new RJResultSetServer(jdbcMetadata_.getProcedures(catalog,
				schemaPattern, procedureNamePattern));
	}

	/**
	 * Get a description of a catalog's stored procedure parameters and result
	 * columns.
	 * 
	 * <P>
	 * Only descriptions matching the schema, procedure and parameter name
	 * criteria are returned. They are ordered by PROCEDURE_SCHEM and
	 * PROCEDURE_NAME. Within this, the return value, if any, is first. Next are
	 * the parameter descriptions in call order. The column descriptions follow
	 * in column number order.
	 * 
	 * <P>
	 * Each row in the ResultSet is a parameter description or column
	 * description with the following fields:
	 * <OL>
	 * <LI><B>PROCEDURE_CAT</B> String => procedure catalog (may be null)
	 * <LI><B>PROCEDURE_SCHEM</B> String => procedure schema (may be null)
	 * <LI><B>PROCEDURE_NAME</B> String => procedure name
	 * <LI><B>COLUMN_NAME</B> String => column/parameter name
	 * <LI><B>COLUMN_TYPE</B> Short => kind of column/parameter:
	 * <UL>
	 * <LI>procedureColumnUnknown - nobody knows
	 * <LI>procedureColumnIn - IN parameter
	 * <LI>procedureColumnInOut - INOUT parameter
	 * <LI>procedureColumnOut - OUT parameter
	 * <LI>procedureColumnReturn - procedure return value
	 * <LI>procedureColumnResult - result column in ResultSet
	 * </UL>
	 * <LI><B>DATA_TYPE</B> short => SQL type from java.sql.Types
	 * <LI><B>TYPE_NAME</B> String => SQL type name
	 * <LI><B>PRECISION</B> int => precision
	 * <LI><B>LENGTH</B> int => length in bytes of data
	 * <LI><B>SCALE</B> short => scale
	 * <LI><B>RADIX</B> short => radix
	 * <LI><B>NULLABLE</B> short => can it contain NULL?
	 * <UL>
	 * <LI>procedureNoNulls - does not allow NULL values
	 * <LI>procedureNullable - allows NULL values
	 * <LI>procedureNullableUnknown - nullability unknown
	 * </UL>
	 * <LI><B>REMARKS</B> String => comment describing parameter/column
	 * </OL>
	 * 
	 * <P>
	 * <B>Note:</B> Some databases may not return the column descriptions for a
	 * procedure. Additional columns beyond REMARKS can be defined by the
	 * database.
	 * 
	 * @param catalog
	 *            a catalog name; "" retrieves those without a catalog; null
	 *            means drop catalog name from the selection criteria
	 * @param schemaPattern
	 *            a schema name pattern; "" retrieves those without a schema
	 * @param procedureNamePattern
	 *            a procedure name pattern
	 * @param columnNamePattern
	 *            a column name pattern
	 * @return ResultSet - each row is a stored procedure parameter or column
	 *         description
	 * @see #getSearchStringEscape
	 */
	public RJResultSetInterface getProcedureColumns(String catalog,
			String schemaPattern, String procedureNamePattern,
			String columnNamePattern) throws RemoteException, SQLException {
		return new RJResultSetServer(jdbcMetadata_.getProcedureColumns(catalog,
				schemaPattern, procedureNamePattern, columnNamePattern));
	}

	/**
	 * Get a description of tables available in a catalog.
	 * 
	 * <P>
	 * Only table descriptions matching the catalog, schema, table name and type
	 * criteria are returned. They are ordered by TABLE_TYPE, TABLE_SCHEM and
	 * TABLE_NAME.
	 * 
	 * <P>
	 * Each table description has the following columns:
	 * <OL>
	 * <LI><B>TABLE_CAT</B> String => table catalog (may be null)
	 * <LI><B>TABLE_SCHEM</B> String => table schema (may be null)
	 * <LI><B>TABLE_NAME</B> String => table name
	 * <LI><B>TABLE_TYPE</B> String => table type. Typical types are "TABLE",
	 * "VIEW", "SYSTEM TABLE", "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS",
	 * "SYNONYM".
	 * <LI><B>REMARKS</B> String => explanatory comment on the table
	 * </OL>
	 * 
	 * <P>
	 * <B>Note:</B> Some databases may not return information for all tables.
	 * 
	 * @param catalog
	 *            a catalog name; "" retrieves those without a catalog; null
	 *            means drop catalog name from the selection criteria
	 * @param schemaPattern
	 *            a schema name pattern; "" retrieves those without a schema
	 * @param tableNamePattern
	 *            a table name pattern
	 * @param types
	 *            a list of table types to include; null returns all types
	 * @return ResultSet - each row is a table description
	 * @see #getSearchStringEscape
	 */
	public RJResultSetInterface getTables(String catalog, String schemaPattern,
			String tableNamePattern, String types[]) throws RemoteException,
			SQLException {
		return new RJResultSetServer(jdbcMetadata_.getTables(catalog,
				schemaPattern, tableNamePattern, types));
	}

	/**
	 * Get the schema names available in this database. The results are ordered
	 * by schema name.
	 * 
	 * <P>
	 * The schema column is:
	 * <OL>
	 * <LI><B>TABLE_SCHEM</B> String => schema name
	 * </OL>
	 * 
	 * @return ResultSet - each row has a single String column that is a schema
	 *         name
	 */
	public RJResultSetInterface getSchemas() throws RemoteException,
			SQLException {
		return new RJResultSetServer(jdbcMetadata_.getSchemas());
	}

	/**
	 * Get the catalog names available in this database. The results are ordered
	 * by catalog name.
	 * 
	 * <P>
	 * The catalog column is:
	 * <OL>
	 * <LI><B>TABLE_CAT</B> String => catalog name
	 * </OL>
	 * 
	 * @return ResultSet - each row has a single String column that is a catalog
	 *         name
	 */
	public RJResultSetInterface getCatalogs() throws RemoteException,
			SQLException {
		return new RJResultSetServer(jdbcMetadata_.getCatalogs());
	}

	/**
	 * Get the table types available in this database. The results are ordered
	 * by table type.
	 * 
	 * <P>
	 * The table type is:
	 * <OL>
	 * <LI><B>TABLE_TYPE</B> String => table type. Typical types are "TABLE",
	 * "VIEW", "SYSTEM TABLE", "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS",
	 * "SYNONYM".
	 * </OL>
	 * 
	 * @return ResultSet - each row has a single String column that is a table
	 *         type
	 */
	public RJResultSetInterface getTableTypes() throws RemoteException,
			SQLException {
		return new RJResultSetServer(jdbcMetadata_.getTableTypes());
	}

	/**
	 * Get a description of table columns available in a catalog.
	 * 
	 * <P>
	 * Only column descriptions matching the catalog, schema, table and column
	 * name criteria are returned. They are ordered by TABLE_SCHEM, TABLE_NAME
	 * and ORDINAL_POSITION.
	 * 
	 * <P>
	 * Each column description has the following columns:
	 * <OL>
	 * <LI><B>TABLE_CAT</B> String => table catalog (may be null)
	 * <LI><B>TABLE_SCHEM</B> String => table schema (may be null)
	 * <LI><B>TABLE_NAME</B> String => table name
	 * <LI><B>COLUMN_NAME</B> String => column name
	 * <LI><B>DATA_TYPE</B> short => SQL type from java.sql.Types
	 * <LI><B>TYPE_NAME</B> String => Data source dependent type name
	 * <LI><B>COLUMN_SIZE</B> int => column size. For char or date types this is
	 * the maximum number of characters, for numeric or decimal types this is
	 * precision.
	 * <LI><B>BUFFER_LENGTH</B> is not used.
	 * <LI><B>DECIMAL_DIGITS</B> int => the number of fractional digits
	 * <LI><B>NUM_PREC_RADIX</B> int => Radix (typically either 10 or 2)
	 * <LI><B>NULLABLE</B> int => is NULL allowed?
	 * <UL>
	 * <LI>columnNoNulls - might not allow NULL values
	 * <LI>columnNullable - definitely allows NULL values
	 * <LI>columnNullableUnknown - nullability unknown
	 * </UL>
	 * <LI><B>REMARKS</B> String => comment describing column (may be null)
	 * <LI><B>COLUMN_DEF</B> String => default value (may be null)
	 * <LI><B>SQL_DATA_TYPE</B> int => unused
	 * <LI><B>SQL_DATETIME_SUB</B> int => unused
	 * <LI><B>CHAR_OCTET_LENGTH</B> int => for char types the maximum number of
	 * bytes in the column
	 * <LI><B>ORDINAL_POSITION</B> int => index of column in table (starting at
	 * 1)
	 * <LI><B>IS_NULLABLE</B> String => "NO" means column definitely does not
	 * allow NULL values; "YES" means the column might allow NULL values. An
	 * empty string means nobody knows.
	 * </OL>
	 * 
	 * @param catalog
	 *            a catalog name; "" retrieves those without a catalog; null
	 *            means drop catalog name from the selection criteria
	 * @param schemaPattern
	 *            a schema name pattern; "" retrieves those without a schema
	 * @param tableNamePattern
	 *            a table name pattern
	 * @param columnNamePattern
	 *            a column name pattern
	 * @return ResultSet - each row is a column description
	 * @see #getSearchStringEscape
	 */
	public RJResultSetInterface getColumns(String catalog,
			String schemaPattern, String tableNamePattern,
			String columnNamePattern) throws RemoteException, SQLException {
		return new RJResultSetServer(jdbcMetadata_.getColumns(catalog,
				schemaPattern, tableNamePattern, columnNamePattern));
	}

	/**
	 * Get a description of the access rights for a table's columns.
	 * 
	 * <P>
	 * Only privileges matching the column name criteria are returned. They are
	 * ordered by COLUMN_NAME and PRIVILEGE.
	 * 
	 * <P>
	 * Each privilige description has the following columns:
	 * <OL>
	 * <LI><B>TABLE_CAT</B> String => table catalog (may be null)
	 * <LI><B>TABLE_SCHEM</B> String => table schema (may be null)
	 * <LI><B>TABLE_NAME</B> String => table name
	 * <LI><B>COLUMN_NAME</B> String => column name
	 * <LI><B>GRANTOR</B> => grantor of access (may be null)
	 * <LI><B>GRANTEE</B> String => grantee of access
	 * <LI><B>PRIVILEGE</B> String => name of access (SELECT, INSERT, UPDATE,
	 * REFRENCES, ...)
	 * <LI><B>IS_GRANTABLE</B> String => "YES" if grantee is permitted to grant
	 * to others; "NO" if not; null if unknown
	 * </OL>
	 * 
	 * @param catalog
	 *            a catalog name; "" retrieves those without a catalog; null
	 *            means drop catalog name from the selection criteria
	 * @param schema
	 *            a schema name; "" retrieves those without a schema
	 * @param table
	 *            a table name
	 * @param columnNamePattern
	 *            a column name pattern
	 * @return ResultSet - each row is a column privilege description
	 * @see #getSearchStringEscape
	 */
	public RJResultSetInterface getColumnPrivileges(String catalog,
			String schema, String table, String columnNamePattern)
			throws RemoteException, SQLException {
		return new RJResultSetServer(jdbcMetadata_.getColumnPrivileges(catalog,
				schema, table, columnNamePattern));
	}

	/**
	 * Get a description of the access rights for each table available in a
	 * catalog. Note that a table privilege applies to one or more columns in
	 * the table. It would be wrong to assume that this priviledge applies to
	 * all columns (this may be true for some systems but is not true for all.)
	 * 
	 * <P>
	 * Only privileges matching the schema and table name criteria are returned.
	 * They are ordered by TABLE_SCHEM, TABLE_NAME, and PRIVILEGE.
	 * 
	 * <P>
	 * Each privilige description has the following columns:
	 * <OL>
	 * <LI><B>TABLE_CAT</B> String => table catalog (may be null)
	 * <LI><B>TABLE_SCHEM</B> String => table schema (may be null)
	 * <LI><B>TABLE_NAME</B> String => table name
	 * <LI><B>GRANTOR</B> => grantor of access (may be null)
	 * <LI><B>GRANTEE</B> String => grantee of access
	 * <LI><B>PRIVILEGE</B> String => name of access (SELECT, INSERT, UPDATE,
	 * REFRENCES, ...)
	 * <LI><B>IS_GRANTABLE</B> String => "YES" if grantee is permitted to grant
	 * to others; "NO" if not; null if unknown
	 * </OL>
	 * 
	 * @param catalog
	 *            a catalog name; "" retrieves those without a catalog; null
	 *            means drop catalog name from the selection criteria
	 * @param schemaPattern
	 *            a schema name pattern; "" retrieves those without a schema
	 * @param tableNamePattern
	 *            a table name pattern
	 * @return ResultSet - each row is a table privilege description
	 * @see #getSearchStringEscape
	 */
	public RJResultSetInterface getTablePrivileges(String catalog,
			String schemaPattern, String tableNamePattern)
			throws RemoteException, SQLException {
		return new RJResultSetServer(jdbcMetadata_.getTablePrivileges(catalog,
				schemaPattern, tableNamePattern));
	}

	/**
	 * Get a description of a table's optimal set of columns that uniquely
	 * identifies a row. They are ordered by SCOPE.
	 * 
	 * <P>
	 * Each column description has the following columns:
	 * <OL>
	 * <LI><B>SCOPE</B> short => actual scope of result
	 * <UL>
	 * <LI>bestRowTemporary - very temporary, while using row
	 * <LI>bestRowTransaction - valid for remainder of current transaction
	 * <LI>bestRowSession - valid for remainder of current session
	 * </UL>
	 * <LI><B>COLUMN_NAME</B> String => column name
	 * <LI><B>DATA_TYPE</B> short => SQL data type from java.sql.Types
	 * <LI><B>TYPE_NAME</B> String => Data source dependent type name
	 * <LI><B>COLUMN_SIZE</B> int => precision
	 * <LI><B>BUFFER_LENGTH</B> int => not used
	 * <LI><B>DECIMAL_DIGITS</B> short => scale
	 * <LI><B>PSEUDO_COLUMN</B> short => is this a pseudo column like an Oracle
	 * ROWID
	 * <UL>
	 * <LI>bestRowUnknown - may or may not be pseudo column
	 * <LI>bestRowNotPseudo - is NOT a pseudo column
	 * <LI>bestRowPseudo - is a pseudo column
	 * </UL>
	 * </OL>
	 * 
	 * @param catalog
	 *            a catalog name; "" retrieves those without a catalog; null
	 *            means drop catalog name from the selection criteria
	 * @param schema
	 *            a schema name; "" retrieves those without a schema
	 * @param table
	 *            a table name
	 * @param scope
	 *            the scope of interest; use same values as SCOPE
	 * @param nullable
	 *            include columns that are nullable?
	 * @return ResultSet - each row is a column description
	 */
	public RJResultSetInterface getBestRowIdentifier(String catalog,
			String schema, String table, int scope, boolean nullable)
			throws RemoteException, SQLException {
		return new RJResultSetServer(jdbcMetadata_.getBestRowIdentifier(
				catalog, schema, table, scope, nullable));
	}

	/**
	 * Get a description of a table's columns that are automatically updated
	 * when any value in a row is updated. They are unordered.
	 * 
	 * <P>
	 * Each column description has the following columns:
	 * <OL>
	 * <LI><B>SCOPE</B> short => is not used
	 * <LI><B>COLUMN_NAME</B> String => column name
	 * <LI><B>DATA_TYPE</B> short => SQL data type from java.sql.Types
	 * <LI><B>TYPE_NAME</B> String => Data source dependent type name
	 * <LI><B>COLUMN_SIZE</B> int => precision
	 * <LI><B>BUFFER_LENGTH</B> int => length of column value in bytes
	 * <LI><B>DECIMAL_DIGITS</B> short => scale
	 * <LI><B>PSEUDO_COLUMN</B> short => is this a pseudo column like an Oracle
	 * ROWID
	 * <UL>
	 * <LI>versionColumnUnknown - may or may not be pseudo column
	 * <LI>versionColumnNotPseudo - is NOT a pseudo column
	 * <LI>versionColumnPseudo - is a pseudo column
	 * </UL>
	 * </OL>
	 * 
	 * @param catalog
	 *            a catalog name; "" retrieves those without a catalog; null
	 *            means drop catalog name from the selection criteria
	 * @param schema
	 *            a schema name; "" retrieves those without a schema
	 * @param table
	 *            a table name
	 * @return ResultSet - each row is a column description
	 */
	public RJResultSetInterface getVersionColumns(String catalog,
			String schema, String table) throws RemoteException, SQLException {
		return new RJResultSetServer(jdbcMetadata_.getVersionColumns(catalog,
				schema, table));
	}

	/**
	 * Get a description of a table's primary key columns. They are ordered by
	 * COLUMN_NAME.
	 * 
	 * <P>
	 * Each primary key column description has the following columns:
	 * <OL>
	 * <LI><B>TABLE_CAT</B> String => table catalog (may be null)
	 * <LI><B>TABLE_SCHEM</B> String => table schema (may be null)
	 * <LI><B>TABLE_NAME</B> String => table name
	 * <LI><B>COLUMN_NAME</B> String => column name
	 * <LI><B>KEY_SEQ</B> short => sequence number within primary key
	 * <LI><B>PK_NAME</B> String => primary key name (may be null)
	 * </OL>
	 * 
	 * @param catalog
	 *            a catalog name; "" retrieves those without a catalog; null
	 *            means drop catalog name from the selection criteria
	 * @param schema
	 *            a schema name pattern; "" retrieves those without a schema
	 * @param table
	 *            a table name
	 * @return ResultSet - each row is a primary key column description
	 */
	public RJResultSetInterface getPrimaryKeys(String catalog, String schema,
			String table) throws RemoteException, SQLException {
		return new RJResultSetServer(jdbcMetadata_.getPrimaryKeys(catalog,
				schema, table));
	}

	/**
	 * Get a description of the primary key columns that are referenced by a
	 * table's foreign key columns (the primary keys imported by a table). They
	 * are ordered by PKTABLE_CAT, PKTABLE_SCHEM, PKTABLE_NAME, and KEY_SEQ.
	 * 
	 * <P>
	 * Each primary key column description has the following columns:
	 * <OL>
	 * <LI><B>PKTABLE_CAT</B> String => primary key table catalog being imported
	 * (may be null)
	 * <LI><B>PKTABLE_SCHEM</B> String => primary key table schema being
	 * imported (may be null)
	 * <LI><B>PKTABLE_NAME</B> String => primary key table name being imported
	 * <LI><B>PKCOLUMN_NAME</B> String => primary key column name being imported
	 * <LI><B>FKTABLE_CAT</B> String => foreign key table catalog (may be null)
	 * <LI><B>FKTABLE_SCHEM</B> String => foreign key table schema (may be null)
	 * <LI><B>FKTABLE_NAME</B> String => foreign key table name
	 * <LI><B>FKCOLUMN_NAME</B> String => foreign key column name
	 * <LI><B>KEY_SEQ</B> short => sequence number within foreign key
	 * <LI><B>UPDATE_RULE</B> short => What happens to foreign key when primary
	 * is updated:
	 * <UL>
	 * <LI>importedNoAction - do not allow update of primary key if it has been
	 * imported
	 * <LI>importedKeyCascade - change imported key to agree with primary key
	 * update
	 * <LI>importedKeySetNull - change imported key to NULL if its primary key
	 * has been updated
	 * <LI>importedKeySetDefault - change imported key to default values if its
	 * primary key has been updated
	 * <LI>importedKeyRestrict - same as importedKeyNoAction (for ODBC 2.x
	 * compatibility)
	 * </UL>
	 * <LI><B>DELETE_RULE</B> short => What happens to the foreign key when
	 * primary is deleted.
	 * <UL>
	 * <LI>importedKeyNoAction - do not allow delete of primary key if it has
	 * been imported
	 * <LI>importedKeyCascade - delete rows that import a deleted key
	 * <LI>importedKeySetNull - change imported key to NULL if its primary key
	 * has been deleted
	 * <LI>importedKeyRestrict - same as importedKeyNoAction (for ODBC 2.x
	 * compatibility)
	 * <LI>importedKeySetDefault - change imported key to default if its primary
	 * key has been deleted
	 * </UL>
	 * <LI><B>FK_NAME</B> String => foreign key name (may be null)
	 * <LI><B>PK_NAME</B> String => primary key name (may be null)
	 * <LI><B>DEFERRABILITY</B> short => can the evaluation of foreign key
	 * constraints be deferred until commit
	 * <UL>
	 * <LI>importedKeyInitiallyDeferred - see SQL92 for definition
	 * <LI>importedKeyInitiallyImmediate - see SQL92 for definition
	 * <LI>importedKeyNotDeferrable - see SQL92 for definition
	 * </UL>
	 * </OL>
	 * 
	 * @param catalog
	 *            a catalog name; "" retrieves those without a catalog; null
	 *            means drop catalog name from the selection criteria
	 * @param schema
	 *            a schema name pattern; "" retrieves those without a schema
	 * @param table
	 *            a table name
	 * @return ResultSet - each row is a primary key column description
	 * @see #getExportedKeys
	 */
	public RJResultSetInterface getImportedKeys(String catalog, String schema,
			String table) throws RemoteException, SQLException {
		return new RJResultSetServer(jdbcMetadata_.getImportedKeys(catalog,
				schema, table));
	}

	/**
	 * Get a description of the foreign key columns that reference a table's
	 * primary key columns (the foreign keys exported by a table). They are
	 * ordered by FKTABLE_CAT, FKTABLE_SCHEM, FKTABLE_NAME, and KEY_SEQ.
	 * 
	 * <P>
	 * Each foreign key column description has the following columns:
	 * <OL>
	 * <LI><B>PKTABLE_CAT</B> String => primary key table catalog (may be null)
	 * <LI><B>PKTABLE_SCHEM</B> String => primary key table schema (may be null)
	 * <LI><B>PKTABLE_NAME</B> String => primary key table name
	 * <LI><B>PKCOLUMN_NAME</B> String => primary key column name
	 * <LI><B>FKTABLE_CAT</B> String => foreign key table catalog (may be null)
	 * being exported (may be null)
	 * <LI><B>FKTABLE_SCHEM</B> String => foreign key table schema (may be null)
	 * being exported (may be null)
	 * <LI><B>FKTABLE_NAME</B> String => foreign key table name being exported
	 * <LI><B>FKCOLUMN_NAME</B> String => foreign key column name being exported
	 * <LI><B>KEY_SEQ</B> short => sequence number within foreign key
	 * <LI><B>UPDATE_RULE</B> short => What happens to foreign key when primary
	 * is updated:
	 * <UL>
	 * <LI>importedNoAction - do not allow update of primary key if it has been
	 * imported
	 * <LI>importedKeyCascade - change imported key to agree with primary key
	 * update
	 * <LI>importedKeySetNull - change imported key to NULL if its primary key
	 * has been updated
	 * <LI>importedKeySetDefault - change imported key to default values if its
	 * primary key has been updated
	 * <LI>importedKeyRestrict - same as importedKeyNoAction (for ODBC 2.x
	 * compatibility)
	 * </UL>
	 * <LI><B>DELETE_RULE</B> short => What happens to the foreign key when
	 * primary is deleted.
	 * <UL>
	 * <LI>importedKeyNoAction - do not allow delete of primary key if it has
	 * been imported
	 * <LI>importedKeyCascade - delete rows that import a deleted key
	 * <LI>importedKeySetNull - change imported key to NULL if its primary key
	 * has been deleted
	 * <LI>importedKeyRestrict - same as importedKeyNoAction (for ODBC 2.x
	 * compatibility)
	 * <LI>importedKeySetDefault - change imported key to default if its primary
	 * key has been deleted
	 * </UL>
	 * <LI><B>FK_NAME</B> String => foreign key name (may be null)
	 * <LI><B>PK_NAME</B> String => primary key name (may be null)
	 * <LI><B>DEFERRABILITY</B> short => can the evaluation of foreign key
	 * constraints be deferred until commit
	 * <UL>
	 * <LI>importedKeyInitiallyDeferred - see SQL92 for definition
	 * <LI>importedKeyInitiallyImmediate - see SQL92 for definition
	 * <LI>importedKeyNotDeferrable - see SQL92 for definition
	 * </UL>
	 * </OL>
	 * 
	 * @param catalog
	 *            a catalog name; "" retrieves those without a catalog; null
	 *            means drop catalog name from the selection criteria
	 * @param schema
	 *            a schema name pattern; "" retrieves those without a schema
	 * @param table
	 *            a table name
	 * @return ResultSet - each row is a foreign key column description
	 * @see #getImportedKeys
	 */
	public RJResultSetInterface getExportedKeys(String catalog, String schema,
			String table) throws RemoteException, SQLException {
		return new RJResultSetServer(jdbcMetadata_.getExportedKeys(catalog,
				schema, table));
	}

	/**
	 * Get a description of the foreign key columns in the foreign key table
	 * that reference the primary key columns of the primary key table (describe
	 * how one table imports another's key.) This should normally return a
	 * single foreign key/primary key pair (most tables only import a foreign
	 * key from a table once.) They are ordered by FKTABLE_CAT, FKTABLE_SCHEM,
	 * FKTABLE_NAME, and KEY_SEQ.
	 * 
	 * <P>
	 * Each foreign key column description has the following columns:
	 * <OL>
	 * <LI><B>PKTABLE_CAT</B> String => primary key table catalog (may be null)
	 * <LI><B>PKTABLE_SCHEM</B> String => primary key table schema (may be null)
	 * <LI><B>PKTABLE_NAME</B> String => primary key table name
	 * <LI><B>PKCOLUMN_NAME</B> String => primary key column name
	 * <LI><B>FKTABLE_CAT</B> String => foreign key table catalog (may be null)
	 * being exported (may be null)
	 * <LI><B>FKTABLE_SCHEM</B> String => foreign key table schema (may be null)
	 * being exported (may be null)
	 * <LI><B>FKTABLE_NAME</B> String => foreign key table name being exported
	 * <LI><B>FKCOLUMN_NAME</B> String => foreign key column name being exported
	 * <LI><B>KEY_SEQ</B> short => sequence number within foreign key
	 * <LI><B>UPDATE_RULE</B> short => What happens to foreign key when primary
	 * is updated:
	 * <UL>
	 * <LI>importedNoAction - do not allow update of primary key if it has been
	 * imported
	 * <LI>importedKeyCascade - change imported key to agree with primary key
	 * update
	 * <LI>importedKeySetNull - change imported key to NULL if its primary key
	 * has been updated
	 * <LI>importedKeySetDefault - change imported key to default values if its
	 * primary key has been updated
	 * <LI>importedKeyRestrict - same as importedKeyNoAction (for ODBC 2.x
	 * compatibility)
	 * </UL>
	 * <LI><B>DELETE_RULE</B> short => What happens to the foreign key when
	 * primary is deleted.
	 * <UL>
	 * <LI>importedKeyNoAction - do not allow delete of primary key if it has
	 * been imported
	 * <LI>importedKeyCascade - delete rows that import a deleted key
	 * <LI>importedKeySetNull - change imported key to NULL if its primary key
	 * has been deleted
	 * <LI>importedKeyRestrict - same as importedKeyNoAction (for ODBC 2.x
	 * compatibility)
	 * <LI>importedKeySetDefault - change imported key to default if its primary
	 * key has been deleted
	 * </UL>
	 * <LI><B>FK_NAME</B> String => foreign key name (may be null)
	 * <LI><B>PK_NAME</B> String => primary key name (may be null)
	 * <LI><B>DEFERRABILITY</B> short => can the evaluation of foreign key
	 * constraints be deferred until commit
	 * <UL>
	 * <LI>importedKeyInitiallyDeferred - see SQL92 for definition
	 * <LI>importedKeyInitiallyImmediate - see SQL92 for definition
	 * <LI>importedKeyNotDeferrable - see SQL92 for definition
	 * </UL>
	 * </OL>
	 * 
	 * @param primaryCatalog
	 *            a catalog name; "" retrieves those without a catalog; null
	 *            means drop catalog name from the selection criteria
	 * @param primarySchema
	 *            a schema name pattern; "" retrieves those without a schema
	 * @param primaryTable
	 *            the table name that exports the key
	 * @param foreignCatalog
	 *            a catalog name; "" retrieves those without a catalog; null
	 *            means drop catalog name from the selection criteria
	 * @param foreignSchema
	 *            a schema name pattern; "" retrieves those without a schema
	 * @param foreignTable
	 *            the table name that imports the key
	 * @return ResultSet - each row is a foreign key column description
	 * @see #getImportedKeys
	 */
	public RJResultSetInterface getCrossReference(String primaryCatalog,
			String primarySchema, String primaryTable, String foreignCatalog,
			String foreignSchema, String foreignTable) throws RemoteException,
			SQLException {
		return new RJResultSetServer(jdbcMetadata_.getCrossReference(
				primaryCatalog, primarySchema, primaryTable, foreignCatalog,
				foreignSchema, foreignTable));
	}

	/**
	 * Get a description of all the standard SQL types supported by this
	 * database. They are ordered by DATA_TYPE and then by how closely the data
	 * type maps to the corresponding JDBC SQL type.
	 * 
	 * <P>
	 * Each type description has the following columns:
	 * <OL>
	 * <LI><B>TYPE_NAME</B> String => Type name
	 * <LI><B>DATA_TYPE</B> short => SQL data type from java.sql.Types
	 * <LI><B>PRECISION</B> int => maximum precision
	 * <LI><B>LITERAL_PREFIX</B> String => prefix used to quote a literal (may
	 * be null)
	 * <LI><B>LITERAL_SUFFIX</B> String => suffix used to quote a literal (may
	 * be null)
	 * <LI><B>CREATE_PARAMS</B> String => parameters used in creating the type
	 * (may be null)
	 * <LI><B>NULLABLE</B> short => can you use NULL for this type?
	 * <UL>
	 * <LI>typeNoNulls - does not allow NULL values
	 * <LI>typeNullable - allows NULL values
	 * <LI>typeNullableUnknown - nullability unknown
	 * </UL>
	 * <LI><B>CASE_SENSITIVE</B> boolean=> is it case sensitive?
	 * <LI><B>SEARCHABLE</B> short => can you use "WHERE" based on this type:
	 * <UL>
	 * <LI>typePredNone - No support
	 * <LI>typePredChar - Only supported with WHERE .. LIKE
	 * <LI>typePredBasic - Supported except for WHERE .. LIKE
	 * <LI>typeSearchable - Supported for all WHERE ..
	 * </UL>
	 * <LI><B>UNSIGNED_ATTRIBUTE</B> boolean => is it unsigned?
	 * <LI><B>FIXED_PREC_SCALE</B> boolean => can it be a money value?
	 * <LI><B>AUTO_INCREMENT</B> boolean => can it be used for an auto-increment
	 * value?
	 * <LI><B>LOCAL_TYPE_NAME</B> String => localized version of type name (may
	 * be null)
	 * <LI><B>MINIMUM_SCALE</B> short => minimum scale supported
	 * <LI><B>MAXIMUM_SCALE</B> short => maximum scale supported
	 * <LI><B>SQL_DATA_TYPE</B> int => unused
	 * <LI><B>SQL_DATETIME_SUB</B> int => unused
	 * <LI><B>NUM_PREC_RADIX</B> int => usually 2 or 10
	 * </OL>
	 * 
	 * @return ResultSet - each row is a SQL type description
	 */
	public RJResultSetInterface getTypeInfo() throws RemoteException,
			SQLException {
		return new RJResultSetServer(jdbcMetadata_.getTypeInfo());
	}

	/**
	 * Get a description of a table's indices and statistics. They are ordered
	 * by NON_UNIQUE, TYPE, INDEX_NAME, and ORDINAL_POSITION.
	 * 
	 * <P>
	 * Each index column description has the following columns:
	 * <OL>
	 * <LI><B>TABLE_CAT</B> String => table catalog (may be null)
	 * <LI><B>TABLE_SCHEM</B> String => table schema (may be null)
	 * <LI><B>TABLE_NAME</B> String => table name
	 * <LI><B>NON_UNIQUE</B> boolean => Can index values be non-unique? false
	 * when TYPE is tableIndexStatistic
	 * <LI><B>INDEX_QUALIFIER</B> String => index catalog (may be null); null
	 * when TYPE is tableIndexStatistic
	 * <LI><B>INDEX_NAME</B> String => index name; null when TYPE is
	 * tableIndexStatistic
	 * <LI><B>TYPE</B> short => index type:
	 * <UL>
	 * <LI>tableIndexStatistic - this identifies table statistics that are
	 * returned in conjuction with a table's index descriptions
	 * <LI>tableIndexClustered - this is a clustered index
	 * <LI>tableIndexHashed - this is a hashed index
	 * <LI>tableIndexOther - this is some other style of index
	 * </UL>
	 * <LI><B>ORDINAL_POSITION</B> short => column sequence number within index;
	 * zero when TYPE is tableIndexStatistic
	 * <LI><B>COLUMN_NAME</B> String => column name; null when TYPE is
	 * tableIndexStatistic
	 * <LI><B>ASC_OR_DESC</B> String => column sort sequence, "A" => ascending,
	 * "D" => descending, may be null if sort sequence is not supported; null
	 * when TYPE is tableIndexStatistic
	 * <LI><B>CARDINALITY</B> int => When TYPE is tableIndexStatistic, then this
	 * is the number of rows in the table; otherwise, it is the number of unique
	 * values in the index.
	 * <LI><B>PAGES</B> int => When TYPE is tableIndexStatisic then this is the
	 * number of pages used for the table, otherwise it is the number of pages
	 * used for the current index.
	 * <LI><B>FILTER_CONDITION</B> String => Filter condition, if any. (may be
	 * null)
	 * </OL>
	 * 
	 * @param catalog
	 *            a catalog name; "" retrieves those without a catalog; null
	 *            means drop catalog name from the selection criteria
	 * @param schema
	 *            a schema name pattern; "" retrieves those without a schema
	 * @param table
	 *            a table name
	 * @param unique
	 *            when true, return only indices for unique values; when false,
	 *            return indices regardless of whether unique or not
	 * @param approximate
	 *            when true, result is allowed to reflect approximate or out of
	 *            data values; when false, results are requested to be accurate
	 * @return ResultSet - each row is an index column description
	 */
	public RJResultSetInterface getIndexInfo(String catalog, String schema,
			String table, boolean unique, boolean approximate)
			throws RemoteException, SQLException {
		return new RJResultSetServer(jdbcMetadata_.getIndexInfo(catalog,
				schema, table, unique, approximate));
	}

	// JDBC 2. Added Aug 2000, Peter Hearty (peter.hearty@lutris.com).

	public boolean updatesAreDetected(int type) throws RemoteException,
			SQLException {
		return jdbcMetadata_.updatesAreDetected(type);
	}

	public boolean supportsResultSetType(int type) throws RemoteException,
			SQLException {
		return jdbcMetadata_.supportsResultSetType(type);
	}

	public boolean supportsResultSetConcurrency(int type, int concurrency)
			throws RemoteException, SQLException {
		return jdbcMetadata_.supportsResultSetConcurrency(type, concurrency);
	}

	public boolean ownUpdatesAreVisible(int type) throws RemoteException,
			SQLException {
		return jdbcMetadata_.ownUpdatesAreVisible(type);
	}

	public boolean ownInsertsAreVisible(int type) throws RemoteException,
			SQLException {
		return jdbcMetadata_.ownInsertsAreVisible(type);
	}

	public boolean ownDeletesAreVisible(int type) throws RemoteException,
			SQLException {
		return jdbcMetadata_.ownDeletesAreVisible(type);
	}

	public boolean othersUpdatesAreVisible(int type) throws RemoteException,
			SQLException {
		return jdbcMetadata_.othersUpdatesAreVisible(type);
	}

	public boolean othersInsertsAreVisible(int type) throws RemoteException,
			SQLException {
		return jdbcMetadata_.othersInsertsAreVisible(type);
	}

	public boolean othersDeletesAreVisible(int type) throws RemoteException,
			SQLException {
		return jdbcMetadata_.othersDeletesAreVisible(type);
	}

	public boolean insertsAreDetected(int type) throws RemoteException,
			SQLException {
		return jdbcMetadata_.insertsAreDetected(type);
	}

	public RJResultSetInterface getUDTs(String catalog, String schemaPattern,
			String typeNamePattern, int[] types) throws RemoteException,
			SQLException {
		return new RJResultSetServer(jdbcMetadata_.getUDTs(catalog,
				schemaPattern, typeNamePattern, types));
	}

	public boolean supportsBatchUpdates() throws RemoteException, SQLException {
		return jdbcMetadata_.supportsBatchUpdates();
	}

	public Connection getConnection() throws RemoteException, SQLException {
		return jdbcMetadata_.getConnection();
	}

	public boolean deletesAreDetected(int type) throws RemoteException,
			SQLException {
		return jdbcMetadata_.deletesAreDetected(type);
	}

	// ------------------- JDBC 3.0 -------------------------

	public boolean supportsSavepoints() throws RemoteException, SQLException {
		return jdbcMetadata_.supportsSavepoints();
	}

	public boolean supportsNamedParameters() throws RemoteException,
			SQLException {
		return jdbcMetadata_.supportsNamedParameters();
	}

	public boolean supportsMultipleOpenResults() throws RemoteException,
			SQLException {
		return jdbcMetadata_.supportsMultipleOpenResults();
	}

	public boolean supportsGetGeneratedKeys() throws RemoteException,
			SQLException {
		return jdbcMetadata_.supportsGetGeneratedKeys();
	}

	public RJResultSetInterface getSuperTypes(String catalog,
			String schemaPattern, String typeNamePattern)
			throws RemoteException, SQLException {
		return new RJResultSetServer(jdbcMetadata_.getSuperTypes(catalog,
				schemaPattern, typeNamePattern));
	}

	public RJResultSetInterface getSuperTables(String catalog,
			String schemaPattern, String tableNamePattern)
			throws RemoteException, SQLException {
		return new RJResultSetServer(jdbcMetadata_.getSuperTables(catalog,
				schemaPattern, tableNamePattern));
	}

	public RJResultSetInterface getAttributes(String catalog,
			String schemaPattern, String typeNamePattern,
			String attributeNamePattern) throws RemoteException, SQLException {
		return new RJResultSetServer(jdbcMetadata_.getAttributes(catalog,
				schemaPattern, typeNamePattern, attributeNamePattern));
	}

	public boolean supportsResultSetHoldability(int holdability)
			throws RemoteException, SQLException {
		return jdbcMetadata_.supportsResultSetHoldability(holdability);
	}

	public int getResultSetHoldability() throws RemoteException, SQLException {
		return jdbcMetadata_.getResultSetHoldability();
	}

	public int getDatabaseMajorVersion() throws RemoteException, SQLException {
		return jdbcMetadata_.getDatabaseMajorVersion();
	}

	public int getDatabaseMinorVersion() throws RemoteException, SQLException {
		return jdbcMetadata_.getDatabaseMinorVersion();
	}

	public int getJDBCMajorVersion() throws RemoteException, SQLException {
		return jdbcMetadata_.getJDBCMajorVersion();
	}

	public int getJDBCMinorVersion() throws RemoteException, SQLException {
		return jdbcMetadata_.getJDBCMinorVersion();
	}

	public int getSQLStateType() throws RemoteException, SQLException {
		return jdbcMetadata_.getSQLStateType();
	}

	public boolean locatorsUpdateCopy() throws RemoteException, SQLException {
		return jdbcMetadata_.locatorsUpdateCopy();
	}

	public boolean supportsStatementPooling() throws RemoteException,
			SQLException {
		return jdbcMetadata_.supportsStatementPooling();
	}

};
