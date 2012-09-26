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

/**
 * This class provides information about the database as a whole.
 * 
 * <P>
 * Many of the methods here return lists of information in ResultSets. You can
 * use the normal ResultSet methods such as getString and getInt to retrieve the
 * data from these ResultSets. If a given form of metadata is not available,
 * these methods should throw a SQLException.
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
 * A SQLException will be thrown if a driver does not support a meta data
 * method. In the case of methods that return a ResultSet, either a ResultSet
 * (which may be empty) is returned or a SQLException is thrown.
 */
public class RJDatabaseMetaData implements java.sql.DatabaseMetaData, java.io.Serializable {

	private static final long serialVersionUID = 7246732773207356442L;
	
	RJDatabaseMetaDataInterface rmiMetadata_;
	Connection connection_;

	public RJDatabaseMetaData(RJDatabaseMetaDataInterface d, Connection c) {
		rmiMetadata_ = d;
		connection_ = c;
	}

	// ----------------------------------------------------------------------
	// First, a variety of minor information about the target database.

	/**
	 * Can all the procedures returned by getProcedures be called by the current
	 * user?
	 * 
	 * @return true if so
	 */
	public boolean allProceduresAreCallable() throws SQLException {
		try {
			return rmiMetadata_.allProceduresAreCallable();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Can all the tables returned by getTable be SELECTed by the current user?
	 * 
	 * @return true if so
	 */
	public boolean allTablesAreSelectable() throws SQLException {
		try {
			return rmiMetadata_.allTablesAreSelectable();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * What's the url for this database?
	 * 
	 * @return the url or null if it can't be generated
	 */
	public String getURL() throws SQLException {
		try {
			return rmiMetadata_.getURL();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * What's our user name as known to the database?
	 * 
	 * @return our database user name
	 */
	public String getUserName() throws SQLException {
		try {
			return rmiMetadata_.getUserName();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Is the database in read-only mode?
	 * 
	 * @return true if so
	 */
	public boolean isReadOnly() throws SQLException {
		try {
			return rmiMetadata_.isReadOnly();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Are NULL values sorted high?
	 * 
	 * @return true if so
	 */
	public boolean nullsAreSortedHigh() throws SQLException {
		try {
			return rmiMetadata_.nullsAreSortedHigh();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Are NULL values sorted low?
	 * 
	 * @return true if so
	 */
	public boolean nullsAreSortedLow() throws SQLException {
		try {
			return rmiMetadata_.nullsAreSortedLow();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Are NULL values sorted at the start regardless of sort order?
	 * 
	 * @return true if so
	 */
	public boolean nullsAreSortedAtStart() throws SQLException {
		try {
			return rmiMetadata_.nullsAreSortedAtStart();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Are NULL values sorted at the end regardless of sort order?
	 * 
	 * @return true if so
	 */
	public boolean nullsAreSortedAtEnd() throws SQLException {
		try {
			return rmiMetadata_.nullsAreSortedAtEnd();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * What's the name of this database product?
	 * 
	 * @return database product name
	 */
	public String getDatabaseProductName() throws SQLException {
		try {
			return rmiMetadata_.getDatabaseProductName();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * What's the version of this database product?
	 * 
	 * @return database version
	 */
	public String getDatabaseProductVersion() throws SQLException {
		try {
			return rmiMetadata_.getDatabaseProductVersion();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * What's the name of this JDBC driver?
	 * 
	 * @return JDBC driver name
	 */
	public String getDriverName() throws SQLException {
		try {
			return rmiMetadata_.getDriverName();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * What's the version of this JDBC driver?
	 * 
	 * @return JDBC driver version
	 */
	public String getDriverVersion() throws SQLException {
		try {
			return rmiMetadata_.getDriverVersion();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * What's this JDBC driver's major version number?
	 * 
	 * @return JDBC driver major version
	 */
	public int getDriverMajorVersion() {
		try {
			return rmiMetadata_.getDriverMajorVersion();
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * What's this JDBC driver's minor version number?
	 * 
	 * @return JDBC driver minor version number
	 */
	public int getDriverMinorVersion() {
		try {
			return rmiMetadata_.getDriverMinorVersion();
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * Does the database store tables in a local file?
	 * 
	 * @return true if so
	 */
	public boolean usesLocalFiles() throws SQLException {
		try {
			return rmiMetadata_.usesLocalFiles();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Does the database use a file for each table?
	 * 
	 * @return true if the database uses a local file for each table
	 */
	public boolean usesLocalFilePerTable() throws SQLException {
		try {
			return rmiMetadata_.usesLocalFilePerTable();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Does the database treat mixed case unquoted SQL identifiers as case
	 * sensitive and as a result store them in mixed case?
	 * 
	 * A JDBC-Compliant driver will always return false.
	 * 
	 * @return true if so
	 */
	public boolean supportsMixedCaseIdentifiers() throws SQLException {
		try {
			return rmiMetadata_.supportsMixedCaseIdentifiers();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Does the database treat mixed case unquoted SQL identifiers as case
	 * insensitive and store them in upper case?
	 * 
	 * @return true if so
	 */
	public boolean storesUpperCaseIdentifiers() throws SQLException {
		try {
			return rmiMetadata_.storesUpperCaseIdentifiers();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Does the database treat mixed case unquoted SQL identifiers as case
	 * insensitive and store them in lower case?
	 * 
	 * @return true if so
	 */
	public boolean storesLowerCaseIdentifiers() throws SQLException {
		try {
			return rmiMetadata_.storesLowerCaseIdentifiers();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Does the database treat mixed case unquoted SQL identifiers as case
	 * insensitive and store them in mixed case?
	 * 
	 * @return true if so
	 */
	public boolean storesMixedCaseIdentifiers() throws SQLException {
		try {
			return rmiMetadata_.storesMixedCaseIdentifiers();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Does the database treat mixed case quoted SQL identifiers as case
	 * sensitive and as a result store them in mixed case?
	 * 
	 * A JDBC-Compliant driver will always return false.
	 * 
	 * @return true if so
	 */
	public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
		try {
			return rmiMetadata_.supportsMixedCaseQuotedIdentifiers();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Does the database treat mixed case quoted SQL identifiers as case
	 * insensitive and store them in upper case?
	 * 
	 * @return true if so
	 */
	public boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
		try {
			return rmiMetadata_.storesUpperCaseQuotedIdentifiers();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Does the database treat mixed case quoted SQL identifiers as case
	 * insensitive and store them in lower case?
	 * 
	 * @return true if so
	 */
	public boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
		try {
			return rmiMetadata_.storesLowerCaseQuotedIdentifiers();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Does the database treat mixed case quoted SQL identifiers as case
	 * insensitive and store them in mixed case?
	 * 
	 * @return true if so
	 */
	public boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
		try {
			return rmiMetadata_.storesMixedCaseQuotedIdentifiers();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * What's the string used to quote SQL identifiers? This returns a space " "
	 * if identifier quoting isn't supported.
	 * 
	 * A JDBC-Compliant driver always uses a double quote character.
	 * 
	 * @return the quoting string
	 */
	public String getIdentifierQuoteString() throws SQLException {
		try {
			return rmiMetadata_.getIdentifierQuoteString();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Get a comma separated list of all a database's SQL keywords that are NOT
	 * also SQL92 keywords.
	 * 
	 * @return the list
	 */
	public String getSQLKeywords() throws SQLException {
		try {
			return rmiMetadata_.getSQLKeywords();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Get a comma separated list of math functions.
	 * 
	 * @return the list
	 */
	public String getNumericFunctions() throws SQLException {
		try {
			return rmiMetadata_.getNumericFunctions();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Get a comma separated list of string functions.
	 * 
	 * @return the list
	 */
	public String getStringFunctions() throws SQLException {
		try {
			return rmiMetadata_.getStringFunctions();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Get a comma separated list of system functions.
	 * 
	 * @return the list
	 */
	public String getSystemFunctions() throws SQLException {
		try {
			return rmiMetadata_.getSystemFunctions();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Get a comma separated list of time and date functions.
	 * 
	 * @return the list
	 */
	public String getTimeDateFunctions() throws SQLException {
		try {
			return rmiMetadata_.getTimeDateFunctions();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
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
	public String getSearchStringEscape() throws SQLException {
		try {
			return rmiMetadata_.getSearchStringEscape();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Get all the "extra" characters that can be used in unquoted identifier
	 * names (those beyond a-z, A-Z, 0-9 and _).
	 * 
	 * @return the string containing the extra characters
	 */
	public String getExtraNameCharacters() throws SQLException {
		try {
			return rmiMetadata_.getExtraNameCharacters();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	// --------------------------------------------------------------------
	// Functions describing which features are supported.

	/**
	 * Is "ALTER TABLE" with add column supported?
	 * 
	 * @return true if so
	 */
	public boolean supportsAlterTableWithAddColumn() throws SQLException {
		try {
			return rmiMetadata_.supportsAlterTableWithAddColumn();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Is "ALTER TABLE" with drop column supported?
	 * 
	 * @return true if so
	 */
	public boolean supportsAlterTableWithDropColumn() throws SQLException {
		try {
			return rmiMetadata_.supportsAlterTableWithDropColumn();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
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
	public boolean supportsColumnAliasing() throws SQLException {
		try {
			return rmiMetadata_.supportsColumnAliasing();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Are concatenations between NULL and non-NULL values NULL?
	 * 
	 * A JDBC-Compliant driver always returns true.
	 * 
	 * @return true if so
	 */
	public boolean nullPlusNonNullIsNull() throws SQLException {
		try {
			return rmiMetadata_.nullPlusNonNullIsNull();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Is the CONVERT function between SQL types supported?
	 * 
	 * @return true if so
	 */
	public boolean supportsConvert() throws SQLException {
		try {
			return rmiMetadata_.supportsConvert();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
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
			throws SQLException {
		try {
			return rmiMetadata_.supportsConvert();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Are table correlation names supported?
	 * 
	 * A JDBC-Compliant driver always returns true.
	 * 
	 * @return true if so
	 */
	public boolean supportsTableCorrelationNames() throws SQLException {
		try {
			return rmiMetadata_.supportsTableCorrelationNames();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * If table correlation names are supported, are they restricted to be
	 * different from the names of the tables?
	 * 
	 * @return true if so
	 */
	public boolean supportsDifferentTableCorrelationNames() throws SQLException {
		try {
			return rmiMetadata_.supportsDifferentTableCorrelationNames();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Are expressions in "ORDER BY" lists supported?
	 * 
	 * @return true if so
	 */
	public boolean supportsExpressionsInOrderBy() throws SQLException {
		try {
			return rmiMetadata_.supportsExpressionsInOrderBy();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Can an "ORDER BY" clause use columns not in the SELECT?
	 * 
	 * @return true if so
	 */
	public boolean supportsOrderByUnrelated() throws SQLException {
		try {
			return rmiMetadata_.supportsOrderByUnrelated();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Is some form of "GROUP BY" clause supported?
	 * 
	 * @return true if so
	 */
	public boolean supportsGroupBy() throws SQLException {
		try {
			return rmiMetadata_.supportsGroupBy();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Can a "GROUP BY" clause use columns not in the SELECT?
	 * 
	 * @return true if so
	 */
	public boolean supportsGroupByUnrelated() throws SQLException {
		try {
			return rmiMetadata_.supportsGroupByUnrelated();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Can a "GROUP BY" clause add columns not in the SELECT provided it
	 * specifies all the columns in the SELECT?
	 * 
	 * @return true if so
	 */
	public boolean supportsGroupByBeyondSelect() throws SQLException {
		try {
			return rmiMetadata_.supportsGroupByBeyondSelect();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Is the escape character in "LIKE" clauses supported?
	 * 
	 * A JDBC-Compliant driver always returns true.
	 * 
	 * @return true if so
	 */
	public boolean supportsLikeEscapeClause() throws SQLException {
		try {
			return rmiMetadata_.supportsLikeEscapeClause();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Are multiple ResultSets from a single execute supported?
	 * 
	 * @return true if so
	 */
	public boolean supportsMultipleResultSets() throws SQLException {
		try {
			return rmiMetadata_.supportsMultipleResultSets();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Can we have multiple transactions open at once (on different
	 * connections)?
	 * 
	 * @return true if so
	 */
	public boolean supportsMultipleTransactions() throws SQLException {
		try {
			return rmiMetadata_.supportsMultipleTransactions();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Can columns be defined as non-nullable?
	 * 
	 * A JDBC-Compliant driver always returns true.
	 * 
	 * @return true if so
	 */
	public boolean supportsNonNullableColumns() throws SQLException {
		try {
			return rmiMetadata_.supportsNonNullableColumns();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Is the ODBC Minimum SQL grammar supported?
	 * 
	 * All JDBC-Compliant drivers must return true.
	 * 
	 * @return true if so
	 */
	public boolean supportsMinimumSQLGrammar() throws SQLException {
		try {
			return rmiMetadata_.supportsMinimumSQLGrammar();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Is the ODBC Core SQL grammar supported?
	 * 
	 * @return true if so
	 */
	public boolean supportsCoreSQLGrammar() throws SQLException {
		try {
			return rmiMetadata_.supportsCoreSQLGrammar();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Is the ODBC Extended SQL grammar supported?
	 * 
	 * @return true if so
	 */
	public boolean supportsExtendedSQLGrammar() throws SQLException {
		try {
			return rmiMetadata_.supportsExtendedSQLGrammar();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Is the ANSI92 entry level SQL grammar supported?
	 * 
	 * All JDBC-Compliant drivers must return true.
	 * 
	 * @return true if so
	 */
	public boolean supportsANSI92EntryLevelSQL() throws SQLException {
		try {
			return rmiMetadata_.supportsANSI92EntryLevelSQL();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Is the ANSI92 intermediate SQL grammar supported?
	 * 
	 * @return true if so
	 */
	public boolean supportsANSI92IntermediateSQL() throws SQLException {
		try {
			return rmiMetadata_.supportsANSI92IntermediateSQL();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Is the ANSI92 full SQL grammar supported?
	 * 
	 * @return true if so
	 */
	public boolean supportsANSI92FullSQL() throws SQLException {
		try {
			return rmiMetadata_.supportsANSI92FullSQL();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Is the SQL Integrity Enhancement Facility supported?
	 * 
	 * @return true if so
	 */
	public boolean supportsIntegrityEnhancementFacility() throws SQLException {
		try {
			return rmiMetadata_.supportsIntegrityEnhancementFacility();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Is some form of outer join supported?
	 * 
	 * @return true if so
	 */
	public boolean supportsOuterJoins() throws SQLException {
		try {
			return rmiMetadata_.supportsOuterJoins();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Are full nested outer joins supported?
	 * 
	 * @return true if so
	 */
	public boolean supportsFullOuterJoins() throws SQLException {
		try {
			return rmiMetadata_.supportsFullOuterJoins();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Is there limited support for outer joins? (This will be true if
	 * supportFullOuterJoins is true.)
	 * 
	 * @return true if so
	 */
	public boolean supportsLimitedOuterJoins() throws SQLException {
		try {
			return rmiMetadata_.supportsLimitedOuterJoins();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * What's the database vendor's preferred term for "schema"?
	 * 
	 * @return the vendor term
	 */
	public String getSchemaTerm() throws SQLException {
		try {
			return rmiMetadata_.getSchemaTerm();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * What's the database vendor's preferred term for "procedure"?
	 * 
	 * @return the vendor term
	 */
	public String getProcedureTerm() throws SQLException {
		try {
			return rmiMetadata_.getProcedureTerm();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * What's the database vendor's preferred term for "catalog"?
	 * 
	 * @return the vendor term
	 */
	public String getCatalogTerm() throws SQLException {
		try {
			return rmiMetadata_.getCatalogTerm();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Does a catalog appear at the start of a qualified table name? (Otherwise
	 * it appears at the end)
	 * 
	 * @return true if it appears at the start
	 */
	public boolean isCatalogAtStart() throws SQLException {
		try {
			return rmiMetadata_.isCatalogAtStart();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * What's the separator between catalog and table name?
	 * 
	 * @return the separator string
	 */
	public String getCatalogSeparator() throws SQLException {
		try {
			return rmiMetadata_.getCatalogSeparator();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Can a schema name be used in a data manipulation statement?
	 * 
	 * @return true if so
	 */
	public boolean supportsSchemasInDataManipulation() throws SQLException {
		try {
			return rmiMetadata_.supportsSchemasInDataManipulation();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Can a schema name be used in a procedure call statement?
	 * 
	 * @return true if so
	 */
	public boolean supportsSchemasInProcedureCalls() throws SQLException {
		try {
			return rmiMetadata_.supportsSchemasInProcedureCalls();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Can a schema name be used in a table definition statement?
	 * 
	 * @return true if so
	 */
	public boolean supportsSchemasInTableDefinitions() throws SQLException {
		try {
			return rmiMetadata_.supportsSchemasInTableDefinitions();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Can a schema name be used in an index definition statement?
	 * 
	 * @return true if so
	 */
	public boolean supportsSchemasInIndexDefinitions() throws SQLException {
		try {
			return rmiMetadata_.supportsSchemasInIndexDefinitions();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Can a schema name be used in a privilege definition statement?
	 * 
	 * @return true if so
	 */
	public boolean supportsSchemasInPrivilegeDefinitions() throws SQLException {
		try {
			return rmiMetadata_.supportsSchemasInPrivilegeDefinitions();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Can a catalog name be used in a data manipulation statement?
	 * 
	 * @return true if so
	 */
	public boolean supportsCatalogsInDataManipulation() throws SQLException {
		try {
			return rmiMetadata_.supportsCatalogsInDataManipulation();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Can a catalog name be used in a procedure call statement?
	 * 
	 * @return true if so
	 */
	public boolean supportsCatalogsInProcedureCalls() throws SQLException {
		try {
			return rmiMetadata_.supportsCatalogsInProcedureCalls();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Can a catalog name be used in a table definition statement?
	 * 
	 * @return true if so
	 */
	public boolean supportsCatalogsInTableDefinitions() throws SQLException {
		try {
			return rmiMetadata_.supportsCatalogsInTableDefinitions();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Can a catalog name be used in an index definition statement?
	 * 
	 * @return true if so
	 */
	public boolean supportsCatalogsInIndexDefinitions() throws SQLException {
		try {
			return rmiMetadata_.supportsCatalogsInIndexDefinitions();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Can a catalog name be used in a privilege definition statement?
	 * 
	 * @return true if so
	 */
	public boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException {
		try {
			return rmiMetadata_.supportsCatalogsInPrivilegeDefinitions();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Is positioned DELETE supported?
	 * 
	 * @return true if so
	 */
	public boolean supportsPositionedDelete() throws SQLException {
		try {
			return rmiMetadata_.supportsPositionedDelete();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Is positioned UPDATE supported?
	 * 
	 * @return true if so
	 */
	public boolean supportsPositionedUpdate() throws SQLException {
		try {
			return rmiMetadata_.supportsPositionedUpdate();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Is SELECT for UPDATE supported?
	 * 
	 * @return true if so
	 */
	public boolean supportsSelectForUpdate() throws SQLException {
		try {
			return rmiMetadata_.supportsSelectForUpdate();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Are stored procedure calls using the stored procedure escape syntax
	 * supported?
	 * 
	 * @return true if so
	 */
	public boolean supportsStoredProcedures() throws SQLException {
		try {
			return rmiMetadata_.supportsStoredProcedures();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Are subqueries in comparison expressions supported?
	 * 
	 * A JDBC-Compliant driver always returns true.
	 * 
	 * @return true if so
	 */
	public boolean supportsSubqueriesInComparisons() throws SQLException {
		try {
			return rmiMetadata_.supportsSubqueriesInComparisons();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Are subqueries in 'exists' expressions supported?
	 * 
	 * A JDBC-Compliant driver always returns true.
	 * 
	 * @return true if so
	 */
	public boolean supportsSubqueriesInExists() throws SQLException {
		try {
			return rmiMetadata_.supportsSubqueriesInExists();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Are subqueries in 'in' statements supported?
	 * 
	 * A JDBC-Compliant driver always returns true.
	 * 
	 * @return true if so
	 */
	public boolean supportsSubqueriesInIns() throws SQLException {
		try {
			return rmiMetadata_.supportsSubqueriesInIns();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Are subqueries in quantified expressions supported?
	 * 
	 * A JDBC-Compliant driver always returns true.
	 * 
	 * @return true if so
	 */
	public boolean supportsSubqueriesInQuantifieds() throws SQLException {
		try {
			return rmiMetadata_.supportsSubqueriesInQuantifieds();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Are correlated subqueries supported?
	 * 
	 * A JDBC-Compliant driver always returns true.
	 * 
	 * @return true if so
	 */
	public boolean supportsCorrelatedSubqueries() throws SQLException {
		try {
			return rmiMetadata_.supportsCorrelatedSubqueries();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Is SQL UNION supported?
	 * 
	 * @return true if so
	 */
	public boolean supportsUnion() throws SQLException {
		try {
			return rmiMetadata_.supportsUnion();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Is SQL UNION ALL supported?
	 * 
	 * @return true if so
	 */
	public boolean supportsUnionAll() throws SQLException {
		try {
			return rmiMetadata_.supportsUnionAll();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Can cursors remain open across commits?
	 * 
	 * @return true if cursors always remain open; false if they might not
	 *         remain open
	 */
	public boolean supportsOpenCursorsAcrossCommit() throws SQLException {
		try {
			return rmiMetadata_.supportsOpenCursorsAcrossCommit();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Can cursors remain open across rollbacks?
	 * 
	 * @return true if cursors always remain open; false if they might not
	 *         remain open
	 */
	public boolean supportsOpenCursorsAcrossRollback() throws SQLException {
		try {
			return rmiMetadata_.supportsOpenCursorsAcrossRollback();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Can statements remain open across commits?
	 * 
	 * @return true if statements always remain open; false if they might not
	 *         remain open
	 */
	public boolean supportsOpenStatementsAcrossCommit() throws SQLException {
		try {
			return rmiMetadata_.supportsOpenStatementsAcrossCommit();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Can statements remain open across rollbacks?
	 * 
	 * @return true if statements always remain open; false if they might not
	 *         remain open
	 */
	public boolean supportsOpenStatementsAcrossRollback() throws SQLException {
		try {
			return rmiMetadata_.supportsOpenStatementsAcrossRollback();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
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
	public int getMaxBinaryLiteralLength() throws SQLException {
		try {
			return rmiMetadata_.getMaxBinaryLiteralLength();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * What's the max length for a character literal?
	 * 
	 * @return max literal length
	 */
	public int getMaxCharLiteralLength() throws SQLException {
		try {
			return rmiMetadata_.getMaxCharLiteralLength();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * What's the limit on column name length?
	 * 
	 * @return max literal length
	 */
	public int getMaxColumnNameLength() throws SQLException {
		try {
			return rmiMetadata_.getMaxColumnNameLength();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * What's the maximum number of columns in a "GROUP BY" clause?
	 * 
	 * @return max number of columns
	 */
	public int getMaxColumnsInGroupBy() throws SQLException {
		try {
			return rmiMetadata_.getMaxColumnsInGroupBy();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * What's the maximum number of columns allowed in an index?
	 * 
	 * @return max columns
	 */
	public int getMaxColumnsInIndex() throws SQLException {
		try {
			return rmiMetadata_.getMaxColumnsInIndex();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * What's the maximum number of columns in an "ORDER BY" clause?
	 * 
	 * @return max columns
	 */
	public int getMaxColumnsInOrderBy() throws SQLException {
		try {
			return rmiMetadata_.getMaxColumnsInOrderBy();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * What's the maximum number of columns in a "SELECT" list?
	 * 
	 * @return max columns
	 */
	public int getMaxColumnsInSelect() throws SQLException {
		try {
			return rmiMetadata_.getMaxColumnsInSelect();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * What's the maximum number of columns in a table?
	 * 
	 * @return max columns
	 */
	public int getMaxColumnsInTable() throws SQLException {
		try {
			return rmiMetadata_.getMaxColumnsInTable();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * How many active connections can we have at a time to this database?
	 * 
	 * @return max connections
	 */
	public int getMaxConnections() throws SQLException {
		try {
			return rmiMetadata_.getMaxConnections();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * What's the maximum cursor name length?
	 * 
	 * @return max cursor name length in bytes
	 */
	public int getMaxCursorNameLength() throws SQLException {
		try {
			return rmiMetadata_.getMaxCursorNameLength();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * What's the maximum length of an index (in bytes)?
	 * 
	 * @return max index length in bytes
	 */
	public int getMaxIndexLength() throws SQLException {
		try {
			return rmiMetadata_.getMaxIndexLength();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * What's the maximum length allowed for a schema name?
	 * 
	 * @return max name length in bytes
	 */
	public int getMaxSchemaNameLength() throws SQLException {
		try {
			return rmiMetadata_.getMaxSchemaNameLength();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * What's the maximum length of a procedure name?
	 * 
	 * @return max name length in bytes
	 */
	public int getMaxProcedureNameLength() throws SQLException {
		try {
			return rmiMetadata_.getMaxProcedureNameLength();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * What's the maximum length of a catalog name?
	 * 
	 * @return max name length in bytes
	 */
	public int getMaxCatalogNameLength() throws SQLException {
		try {
			return rmiMetadata_.getMaxCatalogNameLength();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * What's the maximum length of a single row?
	 * 
	 * @return max row size in bytes
	 */
	public int getMaxRowSize() throws SQLException {
		try {
			return rmiMetadata_.getMaxRowSize();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Did getMaxRowSize() include LONGVARCHAR and LONGVARBINARY blobs?
	 * 
	 * @return true if so
	 */
	public boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
		try {
			return rmiMetadata_.doesMaxRowSizeIncludeBlobs();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * What's the maximum length of a SQL statement?
	 * 
	 * @return max length in bytes
	 */
	public int getMaxStatementLength() throws SQLException {
		try {
			return rmiMetadata_.getMaxStatementLength();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * How many active statements can we have open at one time to this database?
	 * 
	 * @return the maximum
	 */
	public int getMaxStatements() throws SQLException {
		try {
			return rmiMetadata_.getMaxStatements();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * What's the maximum length of a table name?
	 * 
	 * @return max name length in bytes
	 */
	public int getMaxTableNameLength() throws SQLException {
		try {
			return rmiMetadata_.getMaxTableNameLength();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * What's the maximum number of tables in a SELECT?
	 * 
	 * @return the maximum
	 */
	public int getMaxTablesInSelect() throws SQLException {
		try {
			return rmiMetadata_.getMaxTablesInSelect();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * What's the maximum length of a user name?
	 * 
	 * @return max name length in bytes
	 */
	public int getMaxUserNameLength() throws SQLException {
		try {
			return rmiMetadata_.getMaxUserNameLength();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	// ----------------------------------------------------------------------

	/**
	 * What's the database's default transaction isolation level? The values are
	 * defined in java.sql.Connection.
	 * 
	 * @return the default isolation level
	 * @see Connection
	 */
	public int getDefaultTransactionIsolation() throws SQLException {
		try {
			return rmiMetadata_.getDefaultTransactionIsolation();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Are transactions supported? If not, commit is a noop and the isolation
	 * level is TRANSACTION_NONE.
	 * 
	 * @return true if transactions are supported
	 */
	public boolean supportsTransactions() throws SQLException {
		try {
			return rmiMetadata_.supportsTransactions();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
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
			throws SQLException {
		try {
			return rmiMetadata_.supportsTransactionIsolationLevel(level);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Are both data definition and data manipulation statements within a
	 * transaction supported?
	 * 
	 * @return true if so
	 */
	public boolean supportsDataDefinitionAndDataManipulationTransactions()
			throws SQLException {
		try {
			return rmiMetadata_
					.supportsDataDefinitionAndDataManipulationTransactions();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Are only data manipulation statements within a transaction supported?
	 * 
	 * @return true if so
	 */
	public boolean supportsDataManipulationTransactionsOnly()
			throws SQLException {
		try {
			return rmiMetadata_.supportsDataManipulationTransactionsOnly();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Does a data definition statement within a transaction force the
	 * transaction to commit?
	 * 
	 * @return true if so
	 */
	public boolean dataDefinitionCausesTransactionCommit() throws SQLException {
		try {
			return rmiMetadata_.dataDefinitionCausesTransactionCommit();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	/**
	 * Is a data definition statement within a transaction ignored?
	 * 
	 * @return true if so
	 */
	public boolean dataDefinitionIgnoredInTransactions() throws SQLException {
		try {
			return rmiMetadata_.dataDefinitionIgnoredInTransactions();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
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
	public java.sql.ResultSet getProcedures(String catalog,
			String schemaPattern, String procedureNamePattern)
			throws SQLException {
		try {
			return new RJResultSet(rmiMetadata_.getProcedures(catalog,
					schemaPattern, procedureNamePattern), null);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
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
	public java.sql.ResultSet getProcedureColumns(String catalog,
			String schemaPattern, String procedureNamePattern,
			String columnNamePattern) throws SQLException {
		try {
			return new RJResultSet(rmiMetadata_.getProcedureColumns(catalog,
					schemaPattern, procedureNamePattern, columnNamePattern),
					null);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
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
	public java.sql.ResultSet getTables(String catalog, String schemaPattern,
			String tableNamePattern, String types[]) throws SQLException {
		try {
			return new RJResultSet(rmiMetadata_.getTables(catalog,
					schemaPattern, tableNamePattern, types), null);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
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
	public java.sql.ResultSet getSchemas() throws SQLException {
		try {
			return new RJResultSet(rmiMetadata_.getSchemas(), null);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
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
	public java.sql.ResultSet getCatalogs() throws SQLException {
		try {
			return new RJResultSet(rmiMetadata_.getCatalogs(), null);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
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
	public java.sql.ResultSet getTableTypes() throws SQLException {
		try {
			return new RJResultSet(rmiMetadata_.getTableTypes(), null);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
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
	public java.sql.ResultSet getColumns(String catalog, String schemaPattern,
			String tableNamePattern, String columnNamePattern)
			throws SQLException {
		try {
			return new RJResultSet(rmiMetadata_.getColumns(catalog,
					schemaPattern, tableNamePattern, columnNamePattern), null);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
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
	public java.sql.ResultSet getColumnPrivileges(String catalog,
			String schema, String table, String columnNamePattern)
			throws SQLException {
		try {
			return new RJResultSet(rmiMetadata_.getColumnPrivileges(catalog,
					schema, table, columnNamePattern), null);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
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
	public java.sql.ResultSet getTablePrivileges(String catalog,
			String schemaPattern, String tableNamePattern) throws SQLException {
		try {
			return new RJResultSet(rmiMetadata_.getTablePrivileges(catalog,
					schemaPattern, tableNamePattern), null);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
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
	public java.sql.ResultSet getBestRowIdentifier(String catalog,
			String schema, String table, int scope, boolean nullable)
			throws SQLException {
		try {
			return new RJResultSet(rmiMetadata_.getBestRowIdentifier(catalog,
					schema, table, scope, nullable), null);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
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
	public java.sql.ResultSet getVersionColumns(String catalog, String schema,
			String table) throws SQLException {
		try {
			return new RJResultSet(rmiMetadata_.getVersionColumns(catalog,
					schema, table), null);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
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
	public java.sql.ResultSet getPrimaryKeys(String catalog, String schema,
			String table) throws SQLException {
		try {
			return new RJResultSet(rmiMetadata_.getPrimaryKeys(catalog, schema,
					table), null);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
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
	public java.sql.ResultSet getImportedKeys(String catalog, String schema,
			String table) throws SQLException {
		try {
			return new RJResultSet(rmiMetadata_.getImportedKeys(catalog,
					schema, table), null);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
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
	public java.sql.ResultSet getExportedKeys(String catalog, String schema,
			String table) throws SQLException {
		try {
			return new RJResultSet(rmiMetadata_.getExportedKeys(catalog,
					schema, table), null);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
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
	public java.sql.ResultSet getCrossReference(String primaryCatalog,
			String primarySchema, String primaryTable, String foreignCatalog,
			String foreignSchema, String foreignTable) throws SQLException {
		try {
			return new RJResultSet(rmiMetadata_.getCrossReference(
					primaryCatalog, primarySchema, primaryTable,
					foreignCatalog, foreignSchema, foreignTable), null);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
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
	public java.sql.ResultSet getTypeInfo() throws SQLException {
		try {
			return new RJResultSet(rmiMetadata_.getTypeInfo(), null);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
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
	public java.sql.ResultSet getIndexInfo(String catalog, String schema,
			String table, boolean unique, boolean approximate)
			throws SQLException {
		try {
			return new RJResultSet(rmiMetadata_.getIndexInfo(catalog, schema,
					table, unique, approximate), null);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	// JDBC 2.0 methods

	public boolean updatesAreDetected(int type) throws SQLException {
		try {
			return rmiMetadata_.updatesAreDetected(type);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public boolean supportsResultSetType(int type) throws SQLException {
		try {
			return rmiMetadata_.supportsResultSetType(type);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public boolean supportsResultSetConcurrency(int type, int concurrency)
			throws SQLException {
		try {
			return rmiMetadata_.supportsResultSetConcurrency(type, concurrency);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public boolean ownUpdatesAreVisible(int type) throws SQLException {
		try {
			return rmiMetadata_.ownUpdatesAreVisible(type);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public boolean ownInsertsAreVisible(int type) throws SQLException {
		try {
			return rmiMetadata_.ownInsertsAreVisible(type);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public boolean ownDeletesAreVisible(int type) throws SQLException {
		try {
			return rmiMetadata_.ownDeletesAreVisible(type);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public boolean othersUpdatesAreVisible(int type) throws SQLException {
		try {
			return rmiMetadata_.othersUpdatesAreVisible(type);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public boolean othersInsertsAreVisible(int type) throws SQLException {
		try {
			return rmiMetadata_.othersInsertsAreVisible(type);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public boolean othersDeletesAreVisible(int type) throws SQLException {
		try {
			return rmiMetadata_.othersDeletesAreVisible(type);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public boolean insertsAreDetected(int type) throws SQLException {
		try {
			return rmiMetadata_.insertsAreDetected(type);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public ResultSet getUDTs(String catalog, String schemaPattern,
			String typeNamePattern, int[] types) throws SQLException {
		try {
			return new RJResultSet(rmiMetadata_.getUDTs(catalog, schemaPattern,
					typeNamePattern, types), null);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public boolean supportsBatchUpdates() throws SQLException {
		try {
			return rmiMetadata_.supportsBatchUpdates();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public Connection getConnection() throws SQLException {
		return connection_;
	}

	public boolean deletesAreDetected(int type) throws SQLException {
		try {
			return rmiMetadata_.deletesAreDetected(type);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	// ------------------- JDBC 3.0 -------------------------

	public boolean supportsSavepoints() throws SQLException {
		try {
			return rmiMetadata_.supportsSavepoints();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public boolean supportsNamedParameters() throws SQLException {
		try {
			return rmiMetadata_.supportsNamedParameters();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public boolean supportsMultipleOpenResults() throws SQLException {
		try {
			return rmiMetadata_.supportsMultipleOpenResults();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public boolean supportsGetGeneratedKeys() throws SQLException {
		try {
			return rmiMetadata_.supportsGetGeneratedKeys();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public ResultSet getSuperTypes(String catalog, String schemaPattern,
			String typeNamePattern) throws SQLException {
		try {
			return new RJResultSet(rmiMetadata_.getSuperTypes(catalog,
					schemaPattern, typeNamePattern), null);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public ResultSet getSuperTables(String catalog, String schemaPattern,
			String tableNamePattern) throws SQLException {
		try {
			return new RJResultSet(rmiMetadata_.getSuperTables(catalog,
					schemaPattern, tableNamePattern), null);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public ResultSet getAttributes(String catalog, String schemaPattern,
			String typeNamePattern, String attributeNamePattern)
			throws SQLException {
		try {
			return new RJResultSet(rmiMetadata_.getAttributes(catalog,
					schemaPattern, typeNamePattern, attributeNamePattern), null);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public boolean supportsResultSetHoldability(int holdability)
			throws SQLException {
		try {
			return rmiMetadata_.supportsResultSetHoldability(holdability);
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public int getResultSetHoldability() throws SQLException {
		try {
			return rmiMetadata_.getResultSetHoldability();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public int getDatabaseMajorVersion() throws SQLException {
		try {
			return rmiMetadata_.getDatabaseMajorVersion();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public int getDatabaseMinorVersion() throws SQLException {
		try {
			return rmiMetadata_.getDatabaseMinorVersion();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public int getJDBCMajorVersion() throws SQLException {
		try {
			return rmiMetadata_.getJDBCMajorVersion();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public int getJDBCMinorVersion() throws SQLException {
		try {
			return rmiMetadata_.getJDBCMinorVersion();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public int getSQLStateType() throws SQLException {
		try {
			return rmiMetadata_.getSQLStateType();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public boolean locatorsUpdateCopy() throws SQLException {
		try {
			return rmiMetadata_.locatorsUpdateCopy();
		} catch (RemoteException e) {
			throw new java.sql.SQLException(e.getMessage());
		}
	}

	public boolean supportsStatementPooling() throws SQLException {
		try {
			return rmiMetadata_.supportsStatementPooling();
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
	public RowIdLifetime getRowIdLifetime() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResultSet getSchemas(String catalog, String schemaPattern)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean autoCommitFailureClosesAllResultSets() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ResultSet getClientInfoProperties() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResultSet getFunctions(String catalog, String schemaPattern,
			String functionNamePattern) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResultSet getFunctionColumns(String catalog, String schemaPattern,
			String functionNamePattern, String columnNamePattern)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

};
