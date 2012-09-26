
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
 * <P>Many of the methods here return lists of information in ResultSets.
 * You can use the normal ResultSet methods such as getString and getInt 
 * to retrieve the data from these ResultSets.  If a given form of
 * metadata is not available, these methods should throw a SQLException.
 *
 * <P>Some of these methods take arguments that are String patterns.  These
 * arguments all have names such as fooPattern.  Within a pattern String, "%"
 * means match any substring of 0 or more characters, and "_" means match
 * any one character. Only metadata entries matching the search pattern 
 * are returned. If a search pattern argument is set to a null ref, it means 
 * that argument's criteria should be dropped from the search.
 * 
 * <P>A SQLException will be thrown if a driver does not support a meta
 * data method.  In the case of methods that return a ResultSet,
 * either a ResultSet (which may be empty) is returned or a
 * SQLException is thrown.
 */
interface RJDatabaseMetaDataInterface extends java.rmi.Remote {

  //----------------------------------------------------------------------
  // First, a variety of minor information about the target database.

    /**
     * Can all the procedures returned by getProcedures be called by the
     * current user?
     *
     * @return true if so
     */
  boolean allProceduresAreCallable() throws RemoteException, SQLException;

    /**
     * Can all the tables returned by getTable be SELECTed by the
     * current user?
     *
     * @return true if so 
     */
  boolean allTablesAreSelectable() throws RemoteException, SQLException;

    /**
     * What's the url for this database?
     *
     * @return the url or null if it can't be generated
     */
  String getURL() throws RemoteException, SQLException;

    /**
     * What's our user name as known to the database?
     *
     * @return our database user name
     */
  String getUserName() throws RemoteException, SQLException;

    /**
     * Is the database in read-only mode?
     *
     * @return true if so
     */
  boolean isReadOnly() throws RemoteException, SQLException;

    /**
     * Are NULL values sorted high?
     *
     * @return true if so
     */
  boolean nullsAreSortedHigh() throws RemoteException, SQLException;

    /**
     * Are NULL values sorted low?
     *
     * @return true if so
     */
  boolean nullsAreSortedLow() throws RemoteException, SQLException;

    /**
     * Are NULL values sorted at the start regardless of sort order?
     *
     * @return true if so 
     */
  boolean nullsAreSortedAtStart() throws RemoteException, SQLException;

    /**
     * Are NULL values sorted at the end regardless of sort order?
     *
     * @return true if so
     */
  boolean nullsAreSortedAtEnd() throws RemoteException, SQLException;

    /**
     * What's the name of this database product?
     *
     * @return database product name
     */
  String getDatabaseProductName() throws RemoteException, SQLException;

    /**
     * What's the version of this database product?
     *
     * @return database version
     */
  String getDatabaseProductVersion() throws RemoteException, SQLException;

    /**
     * What's the name of this JDBC driver?
     *
     * @return JDBC driver name
     */
  String getDriverName() throws RemoteException, SQLException;

    /**
     * What's the version of this JDBC driver?
     *
     * @return JDBC driver version
     */
  String getDriverVersion() throws RemoteException, SQLException;

    /**
     * What's this JDBC driver's major version number?
     *
     * @return JDBC driver major version
     */
  int getDriverMajorVersion() throws RemoteException, SQLException;

    /**
     * What's this JDBC driver's minor version number?
     *
     * @return JDBC driver minor version number
     */
  int getDriverMinorVersion() throws RemoteException, SQLException;

    /**
     * Does the database store tables in a local file?
     *
     * @return true if so
     */
  boolean usesLocalFiles() throws RemoteException, SQLException;

    /**
     * Does the database use a file for each table?
     *
     * @return true if the database uses a local file for each table
     */
  boolean usesLocalFilePerTable() throws RemoteException, SQLException;

    /**
     * Does the database treat mixed case unquoted SQL identifiers as
     * case sensitive and as a result store them in mixed case?
     *
     * A JDBC-Compliant driver will always return false.
     *
     * @return true if so 
     */
  boolean supportsMixedCaseIdentifiers() throws RemoteException, SQLException;

    /**
     * Does the database treat mixed case unquoted SQL identifiers as
     * case insensitive and store them in upper case?
     *
     * @return true if so 
     */
  boolean storesUpperCaseIdentifiers() throws RemoteException, SQLException;

    /**
     * Does the database treat mixed case unquoted SQL identifiers as
     * case insensitive and store them in lower case?
     *
     * @return true if so 
     */
  boolean storesLowerCaseIdentifiers() throws RemoteException, SQLException;

    /**
     * Does the database treat mixed case unquoted SQL identifiers as
     * case insensitive and store them in mixed case?
     *
     * @return true if so 
     */
  boolean storesMixedCaseIdentifiers() throws RemoteException, SQLException;

    /**
     * Does the database treat mixed case quoted SQL identifiers as
     * case sensitive and as a result store them in mixed case?
     *
     * A JDBC-Compliant driver will always return false.
     *
     * @return true if so
     */
  boolean supportsMixedCaseQuotedIdentifiers()
  throws RemoteException, SQLException;

    /**
     * Does the database treat mixed case quoted SQL identifiers as
     * case insensitive and store them in upper case?
     *
     * @return true if so 
     */
  boolean storesUpperCaseQuotedIdentifiers()
  throws RemoteException, SQLException;

    /**
     * Does the database treat mixed case quoted SQL identifiers as
     * case insensitive and store them in lower case?
     *
     * @return true if so 
     */
  boolean storesLowerCaseQuotedIdentifiers()
  throws RemoteException, SQLException;

    /**
     * Does the database treat mixed case quoted SQL identifiers as
     * case insensitive and store them in mixed case?
     *
     * @return true if so 
     */
  boolean storesMixedCaseQuotedIdentifiers()
  throws RemoteException, SQLException;

    /**
     * What's the string used to quote SQL identifiers?
     * This returns a space " " if identifier quoting isn't supported.
     *
     * A JDBC-Compliant driver always uses a double quote character.
     *
     * @return the quoting string
     */
  String getIdentifierQuoteString() throws RemoteException, SQLException;

    /**
     * Get a comma separated list of all a database's SQL keywords
     * that are NOT also SQL92 keywords.
     *
     * @return the list 
     */
  String getSQLKeywords() throws RemoteException, SQLException;

    /**
     * Get a comma separated list of math functions.
     *
     * @return the list
     */
  String getNumericFunctions() throws RemoteException, SQLException;

    /**
     * Get a comma separated list of string functions.
     *
     * @return the list
     */
  String getStringFunctions() throws RemoteException, SQLException;

    /**
     * Get a comma separated list of system functions.
     *
     * @return the list
     */
  String getSystemFunctions() throws RemoteException, SQLException;

    /**
     * Get a comma separated list of time and date functions.
     *
     * @return the list
     */
  String getTimeDateFunctions() throws RemoteException, SQLException;

    /**
     * This is the string that can be used to escape '_' or '%' in
     * the string pattern style catalog search parameters.
     *
     * <P>The '_' character represents any single character.
     * <P>The '%' character represents any sequence of zero or 
     * more characters.
     * @return the string used to escape wildcard characters
     */
  String getSearchStringEscape() throws RemoteException, SQLException;

    /**
     * Get all the "extra" characters that can be used in unquoted
     * identifier names (those beyond a-z, A-Z, 0-9 and _).
     *
     * @return the string containing the extra characters 
     */
  String getExtraNameCharacters() throws RemoteException, SQLException;

    //--------------------------------------------------------------------
    // Functions describing which features are supported.

    /**
     * Is "ALTER TABLE" with add column supported?
     *
     * @return true if so
     */
  boolean supportsAlterTableWithAddColumn()
  throws RemoteException, SQLException;

    /**
     * Is "ALTER TABLE" with drop column supported?
     *
     * @return true if so
     */
  boolean supportsAlterTableWithDropColumn()
  throws RemoteException, SQLException;

    /**
     * Is column aliasing supported? 
     *
     * <P>If so, the SQL AS clause can be used to provide names for
     * computed columns or to provide alias names for columns as
     * required.
     *
     * A JDBC-Compliant driver always returns true.
     *
     * @return true if so 
     */
  boolean supportsColumnAliasing() throws RemoteException, SQLException;

    /**
     * Are concatenations between NULL and non-NULL values NULL?
     *
     * A JDBC-Compliant driver always returns true.
     *
     * @return true if so
     */
  boolean nullPlusNonNullIsNull() throws RemoteException, SQLException;

    /**
     * Is the CONVERT function between SQL types supported?
     *
     * @return true if so
     */
  boolean supportsConvert() throws RemoteException, SQLException;

    /**
     * Is CONVERT between the given SQL types supported?
     *
     * @param fromType the type to convert from
     * @param toType the type to convert to     
     * @return true if so
     * @see Types
     */
  boolean supportsConvert(int fromType, int toType)
  throws RemoteException, SQLException;

    /**
     * Are table correlation names supported?
     *
     * A JDBC-Compliant driver always returns true.
     *
     * @return true if so
     */
  boolean supportsTableCorrelationNames() throws RemoteException, SQLException;

    /**
     * If table correlation names are supported, are they restricted
     * to be different from the names of the tables?
     *
     * @return true if so 
     */
  boolean supportsDifferentTableCorrelationNames()
  throws RemoteException, SQLException;

    /**
     * Are expressions in "ORDER BY" lists supported?
     *
     * @return true if so
     */
  boolean supportsExpressionsInOrderBy() throws RemoteException, SQLException;

    /**
     * Can an "ORDER BY" clause use columns not in the SELECT?
     *
     * @return true if so
     */
  boolean supportsOrderByUnrelated() throws RemoteException, SQLException;

    /**
     * Is some form of "GROUP BY" clause supported?
     *
     * @return true if so
     */
  boolean supportsGroupBy() throws RemoteException, SQLException;

    /**
     * Can a "GROUP BY" clause use columns not in the SELECT?
     *
     * @return true if so
     */
  boolean supportsGroupByUnrelated() throws RemoteException, SQLException;

    /**
     * Can a "GROUP BY" clause add columns not in the SELECT
     * provided it specifies all the columns in the SELECT?
     *
     * @return true if so
     */
  boolean supportsGroupByBeyondSelect() throws RemoteException, SQLException;

    /**
     * Is the escape character in "LIKE" clauses supported?
     *
     * A JDBC-Compliant driver always returns true.
     *
     * @return true if so
     */
  boolean supportsLikeEscapeClause() throws RemoteException, SQLException;

    /**
     * Are multiple ResultSets from a single execute supported?
     *
     * @return true if so
     */
  boolean supportsMultipleResultSets() throws RemoteException, SQLException;

    /**
     * Can we have multiple transactions open at once (on different
     * connections)?
     *
     * @return true if so
     */
  boolean supportsMultipleTransactions() throws RemoteException, SQLException;

    /**
     * Can columns be defined as non-nullable?
     *
     * A JDBC-Compliant driver always returns true.
     *
     * @return true if so
     */
  boolean supportsNonNullableColumns() throws RemoteException, SQLException;

    /**
     * Is the ODBC Minimum SQL grammar supported?
     *
     * All JDBC-Compliant drivers must return true.
     *
     * @return true if so
     */
  boolean supportsMinimumSQLGrammar() throws RemoteException, SQLException;

    /**
     * Is the ODBC Core SQL grammar supported?
     *
     * @return true if so
     */
  boolean supportsCoreSQLGrammar() throws RemoteException, SQLException;

    /**
     * Is the ODBC Extended SQL grammar supported?
     *
     * @return true if so
     */
  boolean supportsExtendedSQLGrammar() throws RemoteException, SQLException;

    /**
     * Is the ANSI92 entry level SQL grammar supported?
     *
     * All JDBC-Compliant drivers must return true.
     *
     * @return true if so
     */
  boolean supportsANSI92EntryLevelSQL() throws RemoteException, SQLException;

    /**
     * Is the ANSI92 intermediate SQL grammar supported?
     *
     * @return true if so
     */
  boolean supportsANSI92IntermediateSQL() throws RemoteException, SQLException;

    /**
     * Is the ANSI92 full SQL grammar supported?
     *
     * @return true if so
     */
  boolean supportsANSI92FullSQL() throws RemoteException, SQLException;

    /**
     * Is the SQL Integrity Enhancement Facility supported?
     *
     * @return true if so
     */
  boolean supportsIntegrityEnhancementFacility()
  throws RemoteException, SQLException;

    /**
     * Is some form of outer join supported?
     *
     * @return true if so
     */
  boolean supportsOuterJoins() throws RemoteException, SQLException;

    /**
     * Are full nested outer joins supported?
     *
     * @return true if so
     */
  boolean supportsFullOuterJoins() throws RemoteException, SQLException;

    /**
     * Is there limited support for outer joins?  (This will be true
     * if supportFullOuterJoins is true.)
     *
     * @return true if so
     */
  boolean supportsLimitedOuterJoins() throws RemoteException, SQLException;

    /**
     * What's the database vendor's preferred term for "schema"?
     *
     * @return the vendor term
     */
  String getSchemaTerm() throws RemoteException, SQLException;

    /**
     * What's the database vendor's preferred term for "procedure"?
     *
     * @return the vendor term
     */
  String getProcedureTerm() throws RemoteException, SQLException;

    /**
     * What's the database vendor's preferred term for "catalog"?
     *
     * @return the vendor term
     */
  String getCatalogTerm() throws RemoteException, SQLException;

    /**
     * Does a catalog appear at the start of a qualified table name?
     * (Otherwise it appears at the end)
     *
     * @return true if it appears at the start 
     */
  boolean isCatalogAtStart() throws RemoteException, SQLException;

    /**
     * What's the separator between catalog and table name?
     *
     * @return the separator string
     */
  String getCatalogSeparator() throws RemoteException, SQLException;

    /**
     * Can a schema name be used in a data manipulation statement?
     *
     * @return true if so
     */
  boolean supportsSchemasInDataManipulation()
  throws RemoteException, SQLException;

    /**
     * Can a schema name be used in a procedure call statement?
     *
     * @return true if so
     */
  boolean supportsSchemasInProcedureCalls()
  throws RemoteException, SQLException;

    /**
     * Can a schema name be used in a table definition statement?
     *
     * @return true if so
     */
  boolean supportsSchemasInTableDefinitions()
  throws RemoteException, SQLException;

    /**
     * Can a schema name be used in an index definition statement?
     *
     * @return true if so
     */
  boolean supportsSchemasInIndexDefinitions()
  throws RemoteException, SQLException;

    /**
     * Can a schema name be used in a privilege definition statement?
     *
     * @return true if so
     */
  boolean supportsSchemasInPrivilegeDefinitions()
  throws RemoteException, SQLException;

    /**
     * Can a catalog name be used in a data manipulation statement?
     *
     * @return true if so
     */
  boolean supportsCatalogsInDataManipulation()
  throws RemoteException, SQLException;

    /**
     * Can a catalog name be used in a procedure call statement?
     *
     * @return true if so
     */
  boolean supportsCatalogsInProcedureCalls()
  throws RemoteException, SQLException;

    /**
     * Can a catalog name be used in a table definition statement?
     *
     * @return true if so
     */
  boolean supportsCatalogsInTableDefinitions()
  throws RemoteException, SQLException;

    /**
     * Can a catalog name be used in an index definition statement?
     *
     * @return true if so
     */
  boolean supportsCatalogsInIndexDefinitions()
  throws RemoteException, SQLException;

    /**
     * Can a catalog name be used in a privilege definition statement?
     *
     * @return true if so
     */
  boolean supportsCatalogsInPrivilegeDefinitions()
  throws RemoteException, SQLException;


    /**
     * Is positioned DELETE supported?
     *
     * @return true if so
     */
  boolean supportsPositionedDelete() throws RemoteException, SQLException;

    /**
     * Is positioned UPDATE supported?
     *
     * @return true if so
     */
  boolean supportsPositionedUpdate() throws RemoteException, SQLException;

    /**
     * Is SELECT for UPDATE supported?
     *
     * @return true if so
     */
  boolean supportsSelectForUpdate() throws RemoteException, SQLException;

    /**
     * Are stored procedure calls using the stored procedure escape
     * syntax supported?
     *
     * @return true if so 
     */
  boolean supportsStoredProcedures() throws RemoteException, SQLException;

    /**
     * Are subqueries in comparison expressions supported?
     *
     * A JDBC-Compliant driver always returns true.
     *
     * @return true if so
     */
  boolean supportsSubqueriesInComparisons()
  throws RemoteException, SQLException;

    /**
     * Are subqueries in 'exists' expressions supported?
     *
     * A JDBC-Compliant driver always returns true.
     *
     * @return true if so
     */
  boolean supportsSubqueriesInExists() throws RemoteException, SQLException;

    /**
     * Are subqueries in 'in' statements supported?
     *
     * A JDBC-Compliant driver always returns true.
     *
     * @return true if so
     */
  boolean supportsSubqueriesInIns() throws RemoteException, SQLException;

    /**
     * Are subqueries in quantified expressions supported?
     *
     * A JDBC-Compliant driver always returns true.
     *
     * @return true if so
     */
  boolean supportsSubqueriesInQuantifieds()
  throws RemoteException, SQLException;

    /**
     * Are correlated subqueries supported?
     *
     * A JDBC-Compliant driver always returns true.
     *
     * @return true if so
     */
  boolean supportsCorrelatedSubqueries() throws RemoteException, SQLException;

    /**
     * Is SQL UNION supported?
     *
     * @return true if so
     */
  boolean supportsUnion() throws RemoteException, SQLException;

    /**
     * Is SQL UNION ALL supported?
     *
     * @return true if so
     */
  boolean supportsUnionAll() throws RemoteException, SQLException;

    /**
     * Can cursors remain open across commits? 
     * 
     * @return true if cursors always remain open; false if they might not remain open
     */
  boolean supportsOpenCursorsAcrossCommit()
  throws RemoteException, SQLException;

    /**
     * Can cursors remain open across rollbacks?
     * 
     * @return true if cursors always remain open; false if they might not remain open
     */
  boolean supportsOpenCursorsAcrossRollback()
  throws RemoteException, SQLException;

    /**
     * Can statements remain open across commits?
     * 
     * @return true if statements always remain open; false if they might not remain open
     */
  boolean supportsOpenStatementsAcrossCommit()
  throws RemoteException, SQLException;

    /**
     * Can statements remain open across rollbacks?
     * 
     * @return true if statements always remain open; false if they might not remain open
     */
  boolean supportsOpenStatementsAcrossRollback()
  throws RemoteException, SQLException;

  

    //----------------------------------------------------------------------
    // The following group of methods exposes various limitations 
    // based on the target database with the current driver.
    // Unless otherwise specified, a result of zero means there is no
    // limit, or the limit is not known.
  
    /**
     * How many hex characters can you have in an inline binary literal?
     *
     * @return max literal length
     */
  int getMaxBinaryLiteralLength() throws RemoteException, SQLException;

    /**
     * What's the max length for a character literal?
     *
     * @return max literal length
     */
  int getMaxCharLiteralLength() throws RemoteException, SQLException;

    /**
     * What's the limit on column name length?
     *
     * @return max literal length
     */
  int getMaxColumnNameLength() throws RemoteException, SQLException;

    /**
     * What's the maximum number of columns in a "GROUP BY" clause?
     *
     * @return max number of columns
     */
  int getMaxColumnsInGroupBy() throws RemoteException, SQLException;

    /**
     * What's the maximum number of columns allowed in an index?
     *
     * @return max columns
     */
  int getMaxColumnsInIndex() throws RemoteException, SQLException;

    /**
     * What's the maximum number of columns in an "ORDER BY" clause?
     *
     * @return max columns
     */
  int getMaxColumnsInOrderBy() throws RemoteException, SQLException;

    /**
     * What's the maximum number of columns in a "SELECT" list?
     *
     * @return max columns
     */
  int getMaxColumnsInSelect() throws RemoteException, SQLException;

    /**
     * What's the maximum number of columns in a table?
     *
     * @return max columns
     */
  int getMaxColumnsInTable() throws RemoteException, SQLException;

    /**
     * How many active connections can we have at a time to this database?
     *
     * @return max connections
     */
  int getMaxConnections() throws RemoteException, SQLException;

    /**
     * What's the maximum cursor name length?
     *
     * @return max cursor name length in bytes
     */
  int getMaxCursorNameLength() throws RemoteException, SQLException;

    /**
     * What's the maximum length of an index (in bytes)?  
     *
     * @return max index length in bytes
     */
  int getMaxIndexLength() throws RemoteException, SQLException;

    /**
     * What's the maximum length allowed for a schema name?
     *
     * @return max name length in bytes
     */
  int getMaxSchemaNameLength() throws RemoteException, SQLException;

    /**
     * What's the maximum length of a procedure name?
     *
     * @return max name length in bytes
     */
  int getMaxProcedureNameLength() throws RemoteException, SQLException;

    /**
     * What's the maximum length of a catalog name?
     *
     * @return max name length in bytes
     */
  int getMaxCatalogNameLength() throws RemoteException, SQLException;

    /**
     * What's the maximum length of a single row?
     *
     * @return max row size in bytes
     */
  int getMaxRowSize() throws RemoteException, SQLException;

    /**
     * Did getMaxRowSize() include LONGVARCHAR and LONGVARBINARY
     * blobs?
     *
     * @return true if so 
     */
  boolean doesMaxRowSizeIncludeBlobs() throws RemoteException, SQLException;

    /**
     * What's the maximum length of a SQL statement?
     *
     * @return max length in bytes
     */
  int getMaxStatementLength() throws RemoteException, SQLException;

    /**
     * How many active statements can we have open at one time to this
     * database?
     *
     * @return the maximum 
     */
  int getMaxStatements() throws RemoteException, SQLException;

    /**
     * What's the maximum length of a table name?
     *
     * @return max name length in bytes
     */
  int getMaxTableNameLength() throws RemoteException, SQLException;

    /**
     * What's the maximum number of tables in a SELECT?
     *
     * @return the maximum
     */
  int getMaxTablesInSelect() throws RemoteException, SQLException;

    /**
     * What's the maximum length of a user name?
     *
     * @return max name length  in bytes
     */
  int getMaxUserNameLength() throws RemoteException, SQLException;

    //----------------------------------------------------------------------

    /**
     * What's the database's default transaction isolation level?  The
     * values are defined in java.sql.Connection.
     *
     * @return the default isolation level 
     * @see Connection
     */
  int getDefaultTransactionIsolation() throws RemoteException, SQLException;

    /**
     * Are transactions supported? If not, commit is a noop and the
     * isolation level is TRANSACTION_NONE.
     *
     * @return true if transactions are supported 
     */
  boolean supportsTransactions() throws RemoteException, SQLException;

    /**
     * Does the database support the given transaction isolation level?
     *
     * @param level the values are defined in java.sql.Connection
     * @return true if so 
     * @see Connection
     */
  boolean supportsTransactionIsolationLevel(int level)
  throws RemoteException, SQLException;

    /**
     * Are both data definition and data manipulation statements
     * within a transaction supported?
     *
     * @return true if so 
     */
  boolean supportsDataDefinitionAndDataManipulationTransactions()
  throws RemoteException, SQLException;

    /**
     * Are only data manipulation statements within a transaction
     * supported?
     *
     * @return true if so
     */
  boolean supportsDataManipulationTransactionsOnly()
  throws RemoteException, SQLException;

    /**
     * Does a data definition statement within a transaction force the
     * transaction to commit?
     *
     * @return true if so 
     */
  boolean dataDefinitionCausesTransactionCommit()
  throws RemoteException, SQLException;

    /**
     * Is a data definition statement within a transaction ignored?
     *
     * @return true if so 
     */
  boolean dataDefinitionIgnoredInTransactions()
  throws RemoteException, SQLException;


    /**
     * Get a description of stored procedures available in a
     * catalog.
     *
     * <P>Only procedure descriptions matching the schema and
     * procedure name criteria are returned.  They are ordered by
     * PROCEDURE_SCHEM, and PROCEDURE_NAME.
     *
     * <P>Each procedure description has the the following columns:
     *  <OL>
     *  <LI><B>PROCEDURE_CAT</B> String => procedure catalog (may be null)
     *  <LI><B>PROCEDURE_SCHEM</B> String => procedure schema (may be null)
     *  <LI><B>PROCEDURE_NAME</B> String => procedure name
     *  <LI> reserved for future use
     *  <LI> reserved for future use
     *  <LI> reserved for future use
     *  <LI><B>REMARKS</B> String => explanatory comment on the procedure
     *  <LI><B>PROCEDURE_TYPE</B> short => kind of procedure:
     *      <UL>
     *      <LI> procedureResultUnknown - May return a result
     *      <LI> procedureNoResult - Does not return a result
     *      <LI> procedureReturnsResult - Returns a result
     *      </UL>
     *  </OL>
     *
     * @param catalog a catalog name; "" retrieves those without a
     * catalog; null means drop catalog name from the selection criteria
     * @param schemaPattern a schema name pattern; "" retrieves those
     * without a schema
     * @param procedureNamePattern a procedure name pattern 
     * @return RJResultSetInterface - each row is a procedure description 
     * @see #getSearchStringEscape 
     */
  RJResultSetInterface getProcedures(String catalog, String schemaPattern,
   String procedureNamePattern) throws RemoteException, SQLException;

    /**
     * Get a description of a catalog's stored procedure parameters
     * and result columns.
     *
     * <P>Only descriptions matching the schema, procedure and
     * parameter name criteria are returned.  They are ordered by
     * PROCEDURE_SCHEM and PROCEDURE_NAME. Within this, the return value,
     * if any, is first. Next are the parameter descriptions in call
     * order. The column descriptions follow in column number order.
     *
     * <P>Each row in the RJResultSetInterface is a parameter description or
     * column description with the following fields:
     *  <OL>
     *  <LI><B>PROCEDURE_CAT</B> String => procedure catalog (may be null)
     *  <LI><B>PROCEDURE_SCHEM</B> String => procedure schema (may be null)
     *  <LI><B>PROCEDURE_NAME</B> String => procedure name
     *  <LI><B>COLUMN_NAME</B> String => column/parameter name 
     *  <LI><B>COLUMN_TYPE</B> Short => kind of column/parameter:
     *      <UL>
     *      <LI> procedureColumnUnknown - nobody knows
     *      <LI> procedureColumnIn - IN parameter
     *      <LI> procedureColumnInOut - INOUT parameter
     *      <LI> procedureColumnOut - OUT parameter
     *      <LI> procedureColumnReturn - procedure return value
     *      <LI> procedureColumnResult - result column in RJResultSetInterface
     *      </UL>
     *  <LI><B>DATA_TYPE</B> short => SQL type from java.sql.Types
     *  <LI><B>TYPE_NAME</B> String => SQL type name
     *  <LI><B>PRECISION</B> int => precision
     *  <LI><B>LENGTH</B> int => length in bytes of data
     *  <LI><B>SCALE</B> short => scale
     *  <LI><B>RADIX</B> short => radix
     *  <LI><B>NULLABLE</B> short => can it contain NULL?
     *      <UL>
     *      <LI> procedureNoNulls - does not allow NULL values
     *      <LI> procedureNullable - allows NULL values
     *      <LI> procedureNullableUnknown - nullability unknown
     *      </UL>
     *  <LI><B>REMARKS</B> String => comment describing parameter/column
     *  </OL>
     *
     * <P><B>Note:</B> Some databases may not return the column
     * descriptions for a procedure. Additional columns beyond
     * REMARKS can be defined by the database.
     *
     * @param catalog a catalog name; "" retrieves those without a
     * catalog; null means drop catalog name from the selection criteria
     * @param schemaPattern a schema name pattern; "" retrieves those
     * without a schema 
     * @param procedureNamePattern a procedure name pattern 
     * @param columnNamePattern a column name pattern 
     * @return RJResultSetInterface - each row is a stored procedure parameter or 
     *      column description 
     * @see #getSearchStringEscape 
     */
  RJResultSetInterface getProcedureColumns(String catalog,
   String schemaPattern,
   String procedureNamePattern, 
   String columnNamePattern) throws RemoteException, SQLException;

    /**
     * Get a description of tables available in a catalog.
     *
     * <P>Only table descriptions matching the catalog, schema, table
     * name and type criteria are returned.  They are ordered by
     * TABLE_TYPE, TABLE_SCHEM and TABLE_NAME.
     *
     * <P>Each table description has the following columns:
     *  <OL>
     *  <LI><B>TABLE_CAT</B> String => table catalog (may be null)
     *  <LI><B>TABLE_SCHEM</B> String => table schema (may be null)
     *  <LI><B>TABLE_NAME</B> String => table name
     *  <LI><B>TABLE_TYPE</B> String => table type.  Typical types are "TABLE",
     *      "VIEW",  "SYSTEM TABLE", "GLOBAL TEMPORARY", 
     *      "LOCAL TEMPORARY", "ALIAS", "SYNONYM".
     *  <LI><B>REMARKS</B> String => explanatory comment on the table
     *  </OL>
     *
     * <P><B>Note:</B> Some databases may not return information for
     * all tables.
     *
     * @param catalog a catalog name; "" retrieves those without a
     * catalog; null means drop catalog name from the selection criteria
     * @param schemaPattern a schema name pattern; "" retrieves those
     * without a schema
     * @param tableNamePattern a table name pattern 
     * @param types a list of table types to include; null returns all types 
     * @return RJResultSetInterface - each row is a table description
     * @see #getSearchStringEscape 
     */
  RJResultSetInterface getTables(String catalog, String schemaPattern,
  String tableNamePattern, String types[])
  throws RemoteException, SQLException;

    /**
     * Get the schema names available in this database.  The results
     * are ordered by schema name.
     *
     * <P>The schema column is:
     *  <OL>
     *  <LI><B>TABLE_SCHEM</B> String => schema name
     *  </OL>
     *
     * @return RJResultSetInterface - each row has a single String column that is a
     * schema name 
     */
  RJResultSetInterface getSchemas() throws RemoteException, SQLException;

    /**
     * Get the catalog names available in this database.  The results
     * are ordered by catalog name.
     *
     * <P>The catalog column is:
     *  <OL>
     *  <LI><B>TABLE_CAT</B> String => catalog name
     *  </OL>
     *
     * @return RJResultSetInterface - each row has a single String column that is a
     * catalog name 
     */
  RJResultSetInterface getCatalogs() throws RemoteException, SQLException;

    /**
     * Get the table types available in this database.  The results
     * are ordered by table type.
     *
     * <P>The table type is:
     *  <OL>
     *  <LI><B>TABLE_TYPE</B> String => table type.  Typical types are "TABLE",
     *      "VIEW",  "SYSTEM TABLE", "GLOBAL TEMPORARY", 
     *      "LOCAL TEMPORARY", "ALIAS", "SYNONYM".
     *  </OL>
     *
     * @return RJResultSetInterface - each row has a single String column that is a
     * table type 
     */
  RJResultSetInterface getTableTypes() throws RemoteException, SQLException;

    /**
     * Get a description of table columns available in a catalog.
     *
     * <P>Only column descriptions matching the catalog, schema, table
     * and column name criteria are returned.  They are ordered by
     * TABLE_SCHEM, TABLE_NAME and ORDINAL_POSITION.
     *
     * <P>Each column description has the following columns:
     *  <OL>
     *  <LI><B>TABLE_CAT</B> String => table catalog (may be null)
     *  <LI><B>TABLE_SCHEM</B> String => table schema (may be null)
     *  <LI><B>TABLE_NAME</B> String => table name
     *  <LI><B>COLUMN_NAME</B> String => column name
     *  <LI><B>DATA_TYPE</B> short => SQL type from java.sql.Types
     *  <LI><B>TYPE_NAME</B> String => Data source dependent type name
     *  <LI><B>COLUMN_SIZE</B> int => column size.  For char or date
     *      types this is the maximum number of characters, for numeric or
     *      decimal types this is precision.
     *  <LI><B>BUFFER_LENGTH</B> is not used.
     *  <LI><B>DECIMAL_DIGITS</B> int => the number of fractional digits
     *  <LI><B>NUM_PREC_RADIX</B> int => Radix (typically either 10 or 2)
     *  <LI><B>NULLABLE</B> int => is NULL allowed?
     *      <UL>
     *      <LI> columnNoNulls - might not allow NULL values
     *      <LI> columnNullable - definitely allows NULL values
     *      <LI> columnNullableUnknown - nullability unknown
     *      </UL>
     *  <LI><B>REMARKS</B> String => comment describing column (may be null)
     *   <LI><B>COLUMN_DEF</B> String => default value (may be null)
     *  <LI><B>SQL_DATA_TYPE</B> int => unused
     *  <LI><B>SQL_DATETIME_SUB</B> int => unused
     *  <LI><B>CHAR_OCTET_LENGTH</B> int => for char types the 
     *       maximum number of bytes in the column
     *  <LI><B>ORDINAL_POSITION</B> int  => index of column in table 
     *      (starting at 1)
     *  <LI><B>IS_NULLABLE</B> String => "NO" means column definitely 
     *      does not allow NULL values; "YES" means the column might 
     *      allow NULL values.  An empty string means nobody knows.
     *  </OL>
     *
     * @param catalog a catalog name; "" retrieves those without a
     * catalog; null means drop catalog name from the selection criteria
     * @param schemaPattern a schema name pattern; "" retrieves those
     * without a schema
     * @param tableNamePattern a table name pattern 
     * @param columnNamePattern a column name pattern 
     * @return RJResultSetInterface - each row is a column description
     * @see #getSearchStringEscape 
     */
  RJResultSetInterface getColumns(String catalog, String schemaPattern,
   String tableNamePattern, String columnNamePattern) throws RemoteException, SQLException;

    /**
     * Get a description of the access rights for a table's columns.
     *
     * <P>Only privileges matching the column name criteria are
     * returned.  They are ordered by COLUMN_NAME and PRIVILEGE.
     *
     * <P>Each privilige description has the following columns:
     *  <OL>
     *  <LI><B>TABLE_CAT</B> String => table catalog (may be null)
     *  <LI><B>TABLE_SCHEM</B> String => table schema (may be null)
     *  <LI><B>TABLE_NAME</B> String => table name
     *  <LI><B>COLUMN_NAME</B> String => column name
     *  <LI><B>GRANTOR</B> => grantor of access (may be null)
     *  <LI><B>GRANTEE</B> String => grantee of access
     *  <LI><B>PRIVILEGE</B> String => name of access (SELECT, 
     *      INSERT, UPDATE, REFRENCES, ...)
     *  <LI><B>IS_GRANTABLE</B> String => "YES" if grantee is permitted 
     *      to grant to others; "NO" if not; null if unknown 
     *  </OL>
     *
     * @param catalog a catalog name; "" retrieves those without a
     * catalog; null means drop catalog name from the selection criteria
     * @param schema a schema name; "" retrieves those without a schema
     * @param table a table name
     * @param columnNamePattern a column name pattern 
     * @return RJResultSetInterface - each row is a column privilege description
     * @see #getSearchStringEscape 
     */
  RJResultSetInterface getColumnPrivileges(String catalog, String schema,
   String table, String columnNamePattern) throws RemoteException, SQLException;

    /**
     * Get a description of the access rights for each table available
     * in a catalog. Note that a table privilege applies to one or
     * more columns in the table. It would be wrong to assume that
     * this priviledge applies to all columns (this may be true for
     * some systems but is not true for all.)
     *
     * <P>Only privileges matching the schema and table name
     * criteria are returned.  They are ordered by TABLE_SCHEM,
     * TABLE_NAME, and PRIVILEGE.
     *
     * <P>Each privilige description has the following columns:
     *  <OL>
     *  <LI><B>TABLE_CAT</B> String => table catalog (may be null)
     *  <LI><B>TABLE_SCHEM</B> String => table schema (may be null)
     *  <LI><B>TABLE_NAME</B> String => table name
     *  <LI><B>GRANTOR</B> => grantor of access (may be null)
     *  <LI><B>GRANTEE</B> String => grantee of access
     *  <LI><B>PRIVILEGE</B> String => name of access (SELECT, 
     *      INSERT, UPDATE, REFRENCES, ...)
     *  <LI><B>IS_GRANTABLE</B> String => "YES" if grantee is permitted 
     *      to grant to others; "NO" if not; null if unknown 
     *  </OL>
     *
     * @param catalog a catalog name; "" retrieves those without a
     * catalog; null means drop catalog name from the selection criteria
     * @param schemaPattern a schema name pattern; "" retrieves those
     * without a schema
     * @param tableNamePattern a table name pattern 
     * @return RJResultSetInterface - each row is a table privilege description
     * @see #getSearchStringEscape 
     */
  RJResultSetInterface getTablePrivileges(String catalog, String schemaPattern,
   String tableNamePattern) throws RemoteException, SQLException;

    /**
     * Get a description of a table's optimal set of columns that
     * uniquely identifies a row. They are ordered by SCOPE.
     *
     * <P>Each column description has the following columns:
     *  <OL>
     *  <LI><B>SCOPE</B> short => actual scope of result
     *      <UL>
     *      <LI> bestRowTemporary - very temporary, while using row
     *      <LI> bestRowTransaction - valid for remainder of current transaction
     *      <LI> bestRowSession - valid for remainder of current session
     *      </UL>
     *  <LI><B>COLUMN_NAME</B> String => column name
     *  <LI><B>DATA_TYPE</B> short => SQL data type from java.sql.Types
     *  <LI><B>TYPE_NAME</B> String => Data source dependent type name
     *  <LI><B>COLUMN_SIZE</B> int => precision
     *  <LI><B>BUFFER_LENGTH</B> int => not used
     *  <LI><B>DECIMAL_DIGITS</B> short   => scale
     *  <LI><B>PSEUDO_COLUMN</B> short => is this a pseudo column 
     *      like an Oracle ROWID
     *      <UL>
     *      <LI> bestRowUnknown - may or may not be pseudo column
     *      <LI> bestRowNotPseudo - is NOT a pseudo column
     *      <LI> bestRowPseudo - is a pseudo column
     *      </UL>
     *  </OL>
     *
     * @param catalog a catalog name; "" retrieves those without a
     * catalog; null means drop catalog name from the selection criteria
     * @param schema a schema name; "" retrieves those without a schema
     * @param table a table name
     * @param scope the scope of interest; use same values as SCOPE
     * @param nullable include columns that are nullable?
     * @return RJResultSetInterface - each row is a column description 
     */
  RJResultSetInterface getBestRowIdentifier(String catalog, String schema,
   String table, int scope, boolean nullable) throws RemoteException, SQLException;
  
    /**
     * Get a description of a table's columns that are automatically
     * updated when any value in a row is updated.  They are
     * unordered.
     *
     * <P>Each column description has the following columns:
     *  <OL>
     *  <LI><B>SCOPE</B> short => is not used
     *  <LI><B>COLUMN_NAME</B> String => column name
     *  <LI><B>DATA_TYPE</B> short => SQL data type from java.sql.Types
     *  <LI><B>TYPE_NAME</B> String => Data source dependent type name
     *  <LI><B>COLUMN_SIZE</B> int => precision
     *  <LI><B>BUFFER_LENGTH</B> int => length of column value in bytes
     *  <LI><B>DECIMAL_DIGITS</B> short   => scale
     *  <LI><B>PSEUDO_COLUMN</B> short => is this a pseudo column 
     *      like an Oracle ROWID
     *      <UL>
     *      <LI> versionColumnUnknown - may or may not be pseudo column
     *      <LI> versionColumnNotPseudo - is NOT a pseudo column
     *      <LI> versionColumnPseudo - is a pseudo column
     *      </UL>
     *  </OL>
     *
     * @param catalog a catalog name; "" retrieves those without a
     * catalog; null means drop catalog name from the selection criteria
     * @param schema a schema name; "" retrieves those without a schema
     * @param table a table name
     * @return RJResultSetInterface - each row is a column description 
     */
  RJResultSetInterface getVersionColumns(String catalog, String schema,
    String table) throws RemoteException, SQLException;
  
    /**
     * Get a description of a table's primary key columns.  They
     * are ordered by COLUMN_NAME.
     *
     * <P>Each primary key column description has the following columns:
     *  <OL>
     *  <LI><B>TABLE_CAT</B> String => table catalog (may be null)
     *  <LI><B>TABLE_SCHEM</B> String => table schema (may be null)
     *  <LI><B>TABLE_NAME</B> String => table name
     *  <LI><B>COLUMN_NAME</B> String => column name
     *  <LI><B>KEY_SEQ</B> short => sequence number within primary key
     *  <LI><B>PK_NAME</B> String => primary key name (may be null)
     *  </OL>
     *
     * @param catalog a catalog name; "" retrieves those without a
     * catalog; null means drop catalog name from the selection criteria
     * @param schema a schema name pattern; "" retrieves those
     * without a schema
     * @param table a table name
     * @return RJResultSetInterface - each row is a primary key column description 
     */
  RJResultSetInterface getPrimaryKeys(String catalog, String schema,
   String table) throws RemoteException, SQLException;

    /**
     * Get a description of the primary key columns that are
     * referenced by a table's foreign key columns (the primary keys
     * imported by a table).  They are ordered by PKTABLE_CAT,
     * PKTABLE_SCHEM, PKTABLE_NAME, and KEY_SEQ.
     *
     * <P>Each primary key column description has the following columns:
     *  <OL>
     *  <LI><B>PKTABLE_CAT</B> String => primary key table catalog 
     *      being imported (may be null)
     *  <LI><B>PKTABLE_SCHEM</B> String => primary key table schema
     *      being imported (may be null)
     *  <LI><B>PKTABLE_NAME</B> String => primary key table name
     *      being imported
     *  <LI><B>PKCOLUMN_NAME</B> String => primary key column name
     *      being imported
     *  <LI><B>FKTABLE_CAT</B> String => foreign key table catalog (may be null)
     *  <LI><B>FKTABLE_SCHEM</B> String => foreign key table schema (may be null)
     *  <LI><B>FKTABLE_NAME</B> String => foreign key table name
     *  <LI><B>FKCOLUMN_NAME</B> String => foreign key column name
     *  <LI><B>KEY_SEQ</B> short => sequence number within foreign key
     *  <LI><B>UPDATE_RULE</B> short => What happens to 
     *       foreign key when primary is updated:
     *      <UL>
     *      <LI> importedNoAction - do not allow update of primary 
     *               key if it has been imported
     *      <LI> importedKeyCascade - change imported key to agree 
     *               with primary key update
     *      <LI> importedKeySetNull - change imported key to NULL if 
     *               its primary key has been updated
     *      <LI> importedKeySetDefault - change imported key to default values 
     *               if its primary key has been updated
     *      <LI> importedKeyRestrict - same as importedKeyNoAction 
     *                                 (for ODBC 2.x compatibility)
     *      </UL>
     *  <LI><B>DELETE_RULE</B> short => What happens to 
     *      the foreign key when primary is deleted.
     *      <UL>
     *      <LI> importedKeyNoAction - do not allow delete of primary 
     *               key if it has been imported
     *      <LI> importedKeyCascade - delete rows that import a deleted key
     *      <LI> importedKeySetNull - change imported key to NULL if 
     *               its primary key has been deleted
     *      <LI> importedKeyRestrict - same as importedKeyNoAction 
     *                                 (for ODBC 2.x compatibility)
     *      <LI> importedKeySetDefault - change imported key to default if 
     *               its primary key has been deleted
     *      </UL>
     *  <LI><B>FK_NAME</B> String => foreign key name (may be null)
     *  <LI><B>PK_NAME</B> String => primary key name (may be null)
     *  <LI><B>DEFERRABILITY</B> short => can the evaluation of foreign key 
     *      constraints be deferred until commit
     *      <UL>
     *      <LI> importedKeyInitiallyDeferred - see SQL92 for definition
     *      <LI> importedKeyInitiallyImmediate - see SQL92 for definition 
     *      <LI> importedKeyNotDeferrable - see SQL92 for definition 
     *      </UL>
     *  </OL>
     *
     * @param catalog a catalog name; "" retrieves those without a
     * catalog; null means drop catalog name from the selection criteria
     * @param schema a schema name pattern; "" retrieves those
     * without a schema
     * @param table a table name
     * @return RJResultSetInterface - each row is a primary key column description 
     * @see #getExportedKeys 
     */
  RJResultSetInterface getImportedKeys(String catalog, String schema,
   String table) throws RemoteException, SQLException;

    /**
     * Get a description of the foreign key columns that reference a
     * table's primary key columns (the foreign keys exported by a
     * table).  They are ordered by FKTABLE_CAT, FKTABLE_SCHEM,
     * FKTABLE_NAME, and KEY_SEQ.
     *
     * <P>Each foreign key column description has the following columns:
     *  <OL>
     *  <LI><B>PKTABLE_CAT</B> String => primary key table catalog (may be null)
     *  <LI><B>PKTABLE_SCHEM</B> String => primary key table schema (may be null)
     *  <LI><B>PKTABLE_NAME</B> String => primary key table name
     *  <LI><B>PKCOLUMN_NAME</B> String => primary key column name
     *  <LI><B>FKTABLE_CAT</B> String => foreign key table catalog (may be null)
     *      being exported (may be null)
     *  <LI><B>FKTABLE_SCHEM</B> String => foreign key table schema (may be null)
     *      being exported (may be null)
     *  <LI><B>FKTABLE_NAME</B> String => foreign key table name
     *      being exported
     *  <LI><B>FKCOLUMN_NAME</B> String => foreign key column name
     *      being exported
     *  <LI><B>KEY_SEQ</B> short => sequence number within foreign key
     *  <LI><B>UPDATE_RULE</B> short => What happens to 
     *       foreign key when primary is updated:
     *      <UL>
     *      <LI> importedNoAction - do not allow update of primary 
     *               key if it has been imported
     *      <LI> importedKeyCascade - change imported key to agree 
     *               with primary key update
     *      <LI> importedKeySetNull - change imported key to NULL if 
     *               its primary key has been updated
     *      <LI> importedKeySetDefault - change imported key to default values 
     *               if its primary key has been updated
     *      <LI> importedKeyRestrict - same as importedKeyNoAction 
     *                                 (for ODBC 2.x compatibility)
     *      </UL>
     *  <LI><B>DELETE_RULE</B> short => What happens to 
     *      the foreign key when primary is deleted.
     *      <UL>
     *      <LI> importedKeyNoAction - do not allow delete of primary 
     *               key if it has been imported
     *      <LI> importedKeyCascade - delete rows that import a deleted key
     *      <LI> importedKeySetNull - change imported key to NULL if 
     *               its primary key has been deleted
     *      <LI> importedKeyRestrict - same as importedKeyNoAction 
     *                                 (for ODBC 2.x compatibility)
     *      <LI> importedKeySetDefault - change imported key to default if 
     *               its primary key has been deleted
     *      </UL>
     *  <LI><B>FK_NAME</B> String => foreign key name (may be null)
     *  <LI><B>PK_NAME</B> String => primary key name (may be null)
     *  <LI><B>DEFERRABILITY</B> short => can the evaluation of foreign key 
     *      constraints be deferred until commit
     *      <UL>
     *      <LI> importedKeyInitiallyDeferred - see SQL92 for definition
     *      <LI> importedKeyInitiallyImmediate - see SQL92 for definition 
     *      <LI> importedKeyNotDeferrable - see SQL92 for definition 
     *      </UL>
     *  </OL>
     *
     * @param catalog a catalog name; "" retrieves those without a
     * catalog; null means drop catalog name from the selection criteria
     * @param schema a schema name pattern; "" retrieves those
     * without a schema
     * @param table a table name
     * @return RJResultSetInterface - each row is a foreign key column description 
     * @see #getImportedKeys 
     */
  RJResultSetInterface getExportedKeys(String catalog, String schema,
   String table) throws RemoteException, SQLException;

    /**
     * Get a description of the foreign key columns in the foreign key
     * table that reference the primary key columns of the primary key
     * table (describe how one table imports another's key.) This
     * should normally return a single foreign key/primary key pair
     * (most tables only import a foreign key from a table once.)  They
     * are ordered by FKTABLE_CAT, FKTABLE_SCHEM, FKTABLE_NAME, and
     * KEY_SEQ.
     *
     * <P>Each foreign key column description has the following columns:
     *  <OL>
     *  <LI><B>PKTABLE_CAT</B> String => primary key table catalog (may be null)
     *  <LI><B>PKTABLE_SCHEM</B> String => primary key table schema (may be null)
     *  <LI><B>PKTABLE_NAME</B> String => primary key table name
     *  <LI><B>PKCOLUMN_NAME</B> String => primary key column name
     *  <LI><B>FKTABLE_CAT</B> String => foreign key table catalog (may be null)
     *      being exported (may be null)
     *  <LI><B>FKTABLE_SCHEM</B> String => foreign key table schema (may be null)
     *      being exported (may be null)
     *  <LI><B>FKTABLE_NAME</B> String => foreign key table name
     *      being exported
     *  <LI><B>FKCOLUMN_NAME</B> String => foreign key column name
     *      being exported
     *  <LI><B>KEY_SEQ</B> short => sequence number within foreign key
     *  <LI><B>UPDATE_RULE</B> short => What happens to 
     *       foreign key when primary is updated:
     *      <UL>
     *      <LI> importedNoAction - do not allow update of primary 
     *               key if it has been imported
     *      <LI> importedKeyCascade - change imported key to agree 
     *               with primary key update
     *      <LI> importedKeySetNull - change imported key to NULL if 
     *               its primary key has been updated
     *      <LI> importedKeySetDefault - change imported key to default values 
     *               if its primary key has been updated
     *      <LI> importedKeyRestrict - same as importedKeyNoAction 
     *                                 (for ODBC 2.x compatibility)
     *      </UL>
     *  <LI><B>DELETE_RULE</B> short => What happens to 
     *      the foreign key when primary is deleted.
     *      <UL>
     *      <LI> importedKeyNoAction - do not allow delete of primary 
     *               key if it has been imported
     *      <LI> importedKeyCascade - delete rows that import a deleted key
     *      <LI> importedKeySetNull - change imported key to NULL if 
     *               its primary key has been deleted
     *      <LI> importedKeyRestrict - same as importedKeyNoAction 
     *                                 (for ODBC 2.x compatibility)
     *      <LI> importedKeySetDefault - change imported key to default if 
     *               its primary key has been deleted
     *      </UL>
     *  <LI><B>FK_NAME</B> String => foreign key name (may be null)
     *  <LI><B>PK_NAME</B> String => primary key name (may be null)
     *  <LI><B>DEFERRABILITY</B> short => can the evaluation of foreign key 
     *      constraints be deferred until commit
     *      <UL>
     *      <LI> importedKeyInitiallyDeferred - see SQL92 for definition
     *      <LI> importedKeyInitiallyImmediate - see SQL92 for definition 
     *      <LI> importedKeyNotDeferrable - see SQL92 for definition 
     *      </UL>
     *  </OL>
     *
     * @param primaryCatalog a catalog name; "" retrieves those without a
     * catalog; null means drop catalog name from the selection criteria
     * @param primarySchema a schema name pattern; "" retrieves those
     * without a schema
     * @param primaryTable the table name that exports the key
     * @param foreignCatalog a catalog name; "" retrieves those without a
     * catalog; null means drop catalog name from the selection criteria
     * @param foreignSchema a schema name pattern; "" retrieves those
     * without a schema
     * @param foreignTable the table name that imports the key
     * @return RJResultSetInterface - each row is a foreign key column description 
     * @see #getImportedKeys 
     */
  RJResultSetInterface getCrossReference(
   String primaryCatalog, String primarySchema, String primaryTable,
   String foreignCatalog, String foreignSchema, String foreignTable)
   throws RemoteException, SQLException;

    /**
     * Get a description of all the standard SQL types supported by
     * this database. They are ordered by DATA_TYPE and then by how
     * closely the data type maps to the corresponding JDBC SQL type.
     *
     * <P>Each type description has the following columns:
     *  <OL>
     *  <LI><B>TYPE_NAME</B> String => Type name
     *  <LI><B>DATA_TYPE</B> short => SQL data type from java.sql.Types
     *  <LI><B>PRECISION</B> int => maximum precision
     *  <LI><B>LITERAL_PREFIX</B> String => prefix used to quote a literal 
     *      (may be null)
     *  <LI><B>LITERAL_SUFFIX</B> String => suffix used to quote a literal 
            (may be null)
     *  <LI><B>CREATE_PARAMS</B> String => parameters used in creating 
     *      the type (may be null)
     *  <LI><B>NULLABLE</B> short => can you use NULL for this type?
     *      <UL>
     *      <LI> typeNoNulls - does not allow NULL values
     *      <LI> typeNullable - allows NULL values
     *      <LI> typeNullableUnknown - nullability unknown
     *      </UL>
     *  <LI><B>CASE_SENSITIVE</B> boolean=> is it case sensitive?
     *  <LI><B>SEARCHABLE</B> short => can you use "WHERE" based on this type:
     *      <UL>
     *      <LI> typePredNone - No support
     *      <LI> typePredChar - Only supported with WHERE .. LIKE
     *      <LI> typePredBasic - Supported except for WHERE .. LIKE
     *      <LI> typeSearchable - Supported for all WHERE ..
     *      </UL>
     *  <LI><B>UNSIGNED_ATTRIBUTE</B> boolean => is it unsigned?
     *  <LI><B>FIXED_PREC_SCALE</B> boolean => can it be a money value?
     *  <LI><B>AUTO_INCREMENT</B> boolean => can it be used for an 
     *      auto-increment value?
     *  <LI><B>LOCAL_TYPE_NAME</B> String => localized version of type name 
     *      (may be null)
     *  <LI><B>MINIMUM_SCALE</B> short => minimum scale supported
     *  <LI><B>MAXIMUM_SCALE</B> short => maximum scale supported
     *  <LI><B>SQL_DATA_TYPE</B> int => unused
     *  <LI><B>SQL_DATETIME_SUB</B> int => unused
     *  <LI><B>NUM_PREC_RADIX</B> int => usually 2 or 10
     *  </OL>
     *
     * @return RJResultSetInterface - each row is a SQL type description 
     */
  RJResultSetInterface getTypeInfo() throws RemoteException, SQLException;
  
    /**
     * Get a description of a table's indices and statistics. They are
     * ordered by NON_UNIQUE, TYPE, INDEX_NAME, and ORDINAL_POSITION.
     *
     * <P>Each index column description has the following columns:
     *  <OL>
     *  <LI><B>TABLE_CAT</B> String => table catalog (may be null)
     *  <LI><B>TABLE_SCHEM</B> String => table schema (may be null)
     *  <LI><B>TABLE_NAME</B> String => table name
     *  <LI><B>NON_UNIQUE</B> boolean => Can index values be non-unique? 
     *      false when TYPE is tableIndexStatistic
     *  <LI><B>INDEX_QUALIFIER</B> String => index catalog (may be null); 
     *      null when TYPE is tableIndexStatistic
     *  <LI><B>INDEX_NAME</B> String => index name; null when TYPE is 
     *      tableIndexStatistic
     *  <LI><B>TYPE</B> short => index type:
     *      <UL>
     *      <LI> tableIndexStatistic - this identifies table statistics that are
     *           returned in conjuction with a table's index descriptions
     *      <LI> tableIndexClustered - this is a clustered index
     *      <LI> tableIndexHashed - this is a hashed index
     *      <LI> tableIndexOther - this is some other style of index
     *      </UL>
     *  <LI><B>ORDINAL_POSITION</B> short => column sequence number 
     *      within index; zero when TYPE is tableIndexStatistic
     *  <LI><B>COLUMN_NAME</B> String => column name; null when TYPE is 
     *      tableIndexStatistic
     *  <LI><B>ASC_OR_DESC</B> String => column sort sequence, "A" => ascending, 
     *      "D" => descending, may be null if sort sequence is not supported; 
     *      null when TYPE is tableIndexStatistic  
     *  <LI><B>CARDINALITY</B> int => When TYPE is tableIndexStatistic, then 
     *      this is the number of rows in the table; otherwise, it is the 
     *      number of unique values in the index.
     *  <LI><B>PAGES</B> int => When TYPE is  tableIndexStatisic then 
     *      this is the number of pages used for the table, otherwise it 
     *      is the number of pages used for the current index.
     *  <LI><B>FILTER_CONDITION</B> String => Filter condition, if any.  
     *      (may be null)
     *  </OL>
     *
     * @param catalog a catalog name; "" retrieves those without a
     * catalog; null means drop catalog name from the selection criteria
     * @param schema a schema name pattern; "" retrieves those without a schema
     * @param table a table name  
     * @param unique when true, return only indices for unique values; 
     *     when false, return indices regardless of whether unique or not 
     * @param approximate when true, result is allowed to reflect approximate 
     *     or out of data values; when false, results are requested to be 
     *     accurate
     * @return RJResultSetInterface - each row is an index column description 
     */
  RJResultSetInterface getIndexInfo(String catalog, String schema, String table,
   boolean unique, boolean approximate) throws RemoteException, SQLException;

    //--------------------------JDBC 2.0-----------------------------

    /**
     * JDBC 2.0
     *
     * Does the database support the given result set type?
     *
     * @param type defined in <code>java.sql.ResultSet</code>
     * @return <code>true</code> if so; <code>false</code> otherwise 
     * @exception SQLException if a database access error occurs
     * @see Connection
     */
    boolean supportsResultSetType(int type) throws RemoteException, SQLException;

    /**
     * JDBC 2.0
     *
     * Does the database support the concurrency type in combination
     * with the given result set type?
     *
     * @param type defined in <code>java.sql.ResultSet</code>
     * @param concurrency type defined in <code>java.sql.ResultSet</code>
     * @return <code>true</code> if so; <code>false</code> otherwise 
     * @exception SQLException if a database access error occurs
     * @see Connection
     */
    boolean supportsResultSetConcurrency(int type, int concurrency)
      throws RemoteException, SQLException;

    /**
     * JDBC 2.0
     *
     * Indicates whether a result set's own updates are visible.
     *
     * @param result set type, i.e. ResultSet.TYPE_XXX
     * @return <code>true</code> if updates are visible for the result set type;
	 *        <code>false</code> otherwise
     * @exception SQLException if a database access error occurs
     */
    boolean ownUpdatesAreVisible(int type) throws RemoteException, SQLException;

    /**
     * JDBC 2.0
     *
     * Indicates whether a result set's own deletes are visible.
     *
     * @param result set type, i.e. ResultSet.TYPE_XXX
     * @return <code>true</code> if deletes are visible for the result set type;
	 *        <code>false</code> otherwise
     * @exception SQLException if a database access error occurs
     */
    boolean ownDeletesAreVisible(int type) throws RemoteException, SQLException;
    /**
     * JDBC 2.0
     *
     * Indicates whether a result set's own inserts are visible.
     *
     * @param result set type, i.e. ResultSet.TYPE_XXX
     * @return <code>true</code> if inserts are visible for the result set type;
	 *        <code>false</code> otherwise
     * @exception SQLException if a database access error occurs
     */
    boolean ownInsertsAreVisible(int type) throws RemoteException, SQLException;

    /**
     * JDBC 2.0
     *
     * Indicates whether updates made by others are visible.
     *
     * @param result set type, i.e. ResultSet.TYPE_XXX
     * @return <code>true</code> if updates made by others
	 * are visible for the result set type;
	 *        <code>false</code> otherwise
     * @exception SQLException if a database access error occurs
     */
    boolean othersUpdatesAreVisible(int type) throws RemoteException, SQLException;

    /**
     * JDBC 2.0
     *
     * Indicates whether deletes made by others are visible.
     *
     * @param result set type, i.e. ResultSet.TYPE_XXX
     * @return <code>true</code> if deletes made by others
	 * are visible for the result set type;
	 *        <code>false</code> otherwise
     * @exception SQLException if a database access error occurs
     */
    boolean othersDeletesAreVisible(int type) throws RemoteException, SQLException;
    /**
     * JDBC 2.0
     *
     * Indicates whether inserts made by others are visible.
     *
     * @param result set type, i.e. ResultSet.TYPE_XXX
     * @return true if updates are visible for the result set type
     * @return <code>true</code> if inserts made by others
	 * are visible for the result set type;
	 *        <code>false</code> otherwise
     * @exception SQLException if a database access error occurs
     */
    boolean othersInsertsAreVisible(int type) throws RemoteException, SQLException;

    /**
     * JDBC 2.0
     *
     * Indicates whether or not a visible row update can be detected by 
     * calling the method <code>ResultSet.rowUpdated</code>.
     *
     * @param result set type, i.e. ResultSet.TYPE_XXX
     * @return <code>true</code> if changes are detected by the result set type;
	 *         <code>false</code> otherwise
     * @exception SQLException if a database access error occurs
     */
    boolean updatesAreDetected(int type) throws RemoteException, SQLException;

    /**
     * JDBC 2.0
     *
     * Indicates whether or not a visible row delete can be detected by 
     * calling ResultSet.rowDeleted().  If deletesAreDetected()
     * returns false, then deleted rows are removed from the result set.
     *
     * @param result set type, i.e. ResultSet.TYPE_XXX
     * @return true if changes are detected by the resultset type
     * @exception SQLException if a database access error occurs
     */
    boolean deletesAreDetected(int type) throws RemoteException, SQLException;

    /**
     * JDBC 2.0
     *
     * Indicates whether or not a visible row insert can be detected
     * by calling ResultSet.rowInserted().
     *
     * @param result set type, i.e. ResultSet.TYPE_XXX
     * @return true if changes are detected by the resultset type
     * @exception SQLException if a database access error occurs
     */
    boolean insertsAreDetected(int type) throws RemoteException, SQLException;

    /**
     * JDBC 2.0
     *
	 * Indicates whether the driver supports batch updates.
     * @return true if the driver supports batch updates; false otherwise
     */
    boolean supportsBatchUpdates() throws RemoteException, SQLException;

    /**
     * JDBC 2.0
     *
     * Gets a description of the user-defined types defined in a particular
     * schema.  Schema-specific UDTs may have type JAVA_OBJECT, STRUCT, 
     * or DISTINCT.
     *
     * <P>Only types matching the catalog, schema, type name and type  
     * criteria are returned.  They are ordered by DATA_TYPE, TYPE_SCHEM 
     * and TYPE_NAME.  The type name parameter may be a fully-qualified 
     * name.  In this case, the catalog and schemaPattern parameters are
     * ignored.
     *
     * <P>Each type description has the following columns:
     *  <OL>
     *	<LI><B>TYPE_CAT</B> String => the type's catalog (may be null)
     *	<LI><B>TYPE_SCHEM</B> String => type's schema (may be null)
     *	<LI><B>TYPE_NAME</B> String => type name
     *  <LI><B>CLASS_NAME</B> String => Java class name
     *	<LI><B>DATA_TYPE</B> String => type value defined in java.sql.Types.  
     *  One of JAVA_OBJECT, STRUCT, or DISTINCT
     *	<LI><B>REMARKS</B> String => explanatory comment on the type
     *  </OL>
     *
     * <P><B>Note:</B> If the driver does not support UDTs, an empty
     * result set is returned.
     *
     * @param catalog a catalog name; "" retrieves those without a
     * catalog; null means drop catalog name from the selection criteria
     * @param schemaPattern a schema name pattern; "" retrieves those
     * without a schema
     * @param typeNamePattern a type name pattern; may be a fully-qualified
     * name
     * @param types a list of user-named types to include (JAVA_OBJECT, 
     * STRUCT, or DISTINCT); null returns all types 
     * @return ResultSet - each row is a type description
     * @exception SQLException if a database access error occurs
     */
  RJResultSetInterface getUDTs(String catalog, String schemaPattern, 
   String typeNamePattern, int[] types) 
   throws RemoteException, SQLException;

    /**
     * JDBC 2.0
	 * Retrieves the connection that produced this metadata object.
     *
     * @return the connection that produced this metadata object
     */
    Connection getConnection() throws RemoteException, SQLException;


    // ------------------- JDBC 3.0 -------------------------

    /**
     * Retrieves whether this database supports savepoints.
     *
     * @return <code>true</code> if savepoints are supported; 
     *         <code>false</code> otherwise
     * @exception SQLException if a database access error occurs
     * @since 1.4
     */
    boolean supportsSavepoints() throws RemoteException, SQLException;

    /**
     * Retrieves whether this database supports named parameters to callable 
     * statements.
     *
     * @return <code>true</code> if named parameters are supported; 
     *         <code>false</code> otherwise
     * @exception SQLException if a database access error occurs
     * @since 1.4
     */
    boolean supportsNamedParameters() throws RemoteException, SQLException;

    /**
     * Retrieves whether it is possible to have multiple <code>ResultSet</code> objects
     * returned from a <code>CallableStatement</code> object
     * simultaneously.
     *
     * @return <code>true</code> if a <code>CallableStatement</code> object
     *         can return multiple <code>ResultSet</code> objects
     *         simultaneously; <code>false</code> otherwise
     * @exception SQLException if a datanase access error occurs
     * @since 1.4
     */
    boolean supportsMultipleOpenResults() throws RemoteException, SQLException;

    /**
     * Retrieves whether auto-generated keys can be retrieved after 
     * a statement has been executed.
     *
     * @return <code>true</code> if auto-generated keys can be retrieved
     *         after a statement has executed; <code>false</code> otherwise
     * @exception SQLException if a database access error occurs
     * @since 1.4
     */
    boolean supportsGetGeneratedKeys() throws RemoteException, SQLException;

    /**
     * Retrieves a description of the user-defined type (UDT) hierarchies defined in a 
     * particular schema in this database. Only the immediate super type/ 
     * sub type relationship is modeled.
     * <P>
     * Only supertype information for UDTs matching the catalog, 
     * schema, and type name is returned. The type name parameter
     * may be a fully-qualified name. When the UDT name supplied is a 
     * fully-qualified name, the catalog and schemaPattern parameters are 
     * ignored. 
     * <P>
     * If a UDT does not have a direct super type, it is not listed here.
     * A row of the <code>ResultSet</code> object returned by this method
     * describes the designated UDT and a direct supertype. A row has the following 
     * columns:
     *  <OL>
     *  <LI><B>TYPE_CAT</B> String => the UDT's catalog (may be <code>null</code>)
     *  <LI><B>TYPE_SCHEM</B> String => UDT's schema (may be <code>null</code>)
     *  <LI><B>TYPE_NAME</B> String => type name of the UDT
     *  <LI><B>SUPERTYPE_CAT</B> String => the direct super type's catalog 
     *                           (may be <code>null</code>)
     *  <LI><B>SUPERTYPE_SCHEM</B> String => the direct super type's schema 
     *                             (may be <code>null</code>)
     *  <LI><B>SUPERTYPE_NAME</B> String => the direct super type's name
     *  </OL>
     *
     * <P><B>Note:</B> If the driver does not support type hierarchies, an 
     * empty result set is returned.
     *
     * @param catalog a catalog name; "" retrieves those without a catalog;
     *        <code>null</code> means drop catalog name from the selection criteria
     * @param schemaPattern a schema name pattern; "" retrieves those 
     *        without a schema
     * @param typeNamePattern a UDT name pattern; may be a fully-qualified
     *        name
     * @return a <code>ResultSet</code> object in which a row gives information
     *         about the designated UDT
     * @throws SQLException if a database access error occurs
     * @since 1.4
     */
  RJResultSetInterface getSuperTypes(String catalog, String schemaPattern, 
   String typeNamePattern) throws RemoteException, SQLException;
    
    /**
     * Retrieves a description of the table hierarchies defined in a particular 
     * schema in this database.
     *
     * <P>Only supertable information for tables matching the catalog, schema
     * and table name are returned. The table name parameter may be a fully-
     * qualified name, in which case, the catalog and schemaPattern parameters
     * are ignored. If a table does not have a super table, it is not listed here.
     * Supertables have to be defined in the same catalog and schema as the 
     * sub tables. Therefore, the type description does not need to include
     * this information for the supertable.
     *
     * <P>Each type description has the following columns:
     *  <OL>
     *  <LI><B>TABLE_CAT</B> String => the type's catalog (may be <code>null</code>)
     *  <LI><B>TABLE_SCHEM</B> String => type's schema (may be <code>null</code>)
     *  <LI><B>TABLE_NAME</B> String => type name
     *  <LI><B>SUPERTABLE_NAME</B> String => the direct super type's name
     *  </OL>
     *
     * <P><B>Note:</B> If the driver does not support type hierarchies, an 
     * empty result set is returned.
     *
     * @param catalog a catalog name; "" retrieves those without a catalog;
     *        <code>null</code> means drop catalog name from the selection criteria
     * @param schemaPattern a schema name pattern; "" retrieves those 
     *        without a schema
     * @param tableNamePattern a table name pattern; may be a fully-qualified
     *        name
     * @return a <code>ResultSet</code> object in which each row is a type description
     * @throws SQLException if a database access error occurs
     * @since 1.4
     */
  RJResultSetInterface getSuperTables(String catalog, String schemaPattern,
   String tableNamePattern) throws RemoteException, SQLException;

    /**
     * Indicates that <code>NULL</code> values might not be allowed.
     * <P>
     * A possible value for the column
     * <code>NULLABLE</code> in the <code>ResultSet</code> object
     * returned by the method <code>getAttributes</code>.
     */
    short attributeNoNulls = 0;

    /**
     * Indicates that <code>NULL</code> values are definitely allowed.
     * <P>
     * A possible value for the column <code>NULLABLE</code>
     * in the <code>ResultSet</code> object
     * returned by the method <code>getAttributes</code>.
     */
    short attributeNullable = 1;

    /**
     * Indicates that whether <code>NULL</code> values are allowed is not
     * known. 
     * <P>
     * A possible value for the column <code>NULLABLE</code>
     * in the <code>ResultSet</code> object
     * returned by the method <code>getAttributes</code>.
     */
    short attributeNullableUnknown = 2;

    /**
     * Retrieves a description of the given attribute of the given type 
     * for a user-defined type (UDT) that is available in the given schema 
     * and catalog.
     * <P>
     * Descriptions are returned only for attributes of UDTs matching the 
     * catalog, schema, type, and attribute name criteria. They are ordered by
     * TYPE_SCHEM, TYPE_NAME and ORDINAL_POSITION. This description
     * does not contain inherited attributes.
     * <P>
     * The <code>ResultSet</code> object that is returned has the following 
     * columns:
     * <OL>
     *  <LI><B>TYPE_CAT</B> String => type catalog (may be <code>null</code>)
     *	<LI><B>TYPE_SCHEM</B> String => type schema (may be <code>null</code>)
     *	<LI><B>TYPE_NAME</B> String => type name
     *	<LI><B>ATTR_NAME</B> String => attribute name
     *	<LI><B>DATA_TYPE</B> short => attribute type SQL type from java.sql.Types
     *	<LI><B>ATTR_TYPE_NAME</B> String => Data source dependent type name.
     *  For a UDT, the type name is fully qualified. For a REF, the type name is 
     *  fully qualified and represents the target type of the reference type.
     *	<LI><B>ATTR_SIZE</B> int => column size.  For char or date
     *	    types this is the maximum number of characters; for numeric or
     *	    decimal types this is precision.
     *	<LI><B>DECIMAL_DIGITS</B> int => the number of fractional digits
     *	<LI><B>NUM_PREC_RADIX</B> int => Radix (typically either 10 or 2)
     *	<LI><B>NULLABLE</B> int => whether NULL is allowed
     *      <UL>
     *      <LI> attributeNoNulls - might not allow NULL values
     *      <LI> attributeNullable - definitely allows NULL values
     *      <LI> attributeNullableUnknown - nullability unknown
     *      </UL>
     *	<LI><B>REMARKS</B> String => comment describing column (may be <code>null</code>)
     * 	<LI><B>ATTR_DEF</B> String => default value (may be <code>null</code>)
     *	<LI><B>SQL_DATA_TYPE</B> int => unused
     *	<LI><B>SQL_DATETIME_SUB</B> int => unused
     *	<LI><B>CHAR_OCTET_LENGTH</B> int => for char types the 
     *       maximum number of bytes in the column
     *	<LI><B>ORDINAL_POSITION</B> int	=> index of column in table 
     *      (starting at 1)
     *	<LI><B>IS_NULLABLE</B> String => "NO" means column definitely 
     *      does not allow NULL values; "YES" means the column might 
     *      allow NULL values.  An empty string means unknown.
     *  <LI><B>SCOPE_CATALOG</B> String => catalog of table that is the
     *      scope of a reference attribute (<code>null</code> if DATA_TYPE isn't REF)
     *  <LI><B>SCOPE_SCHEMA</B> String => schema of table that is the 
     *      scope of a reference attribute (<code>null</code> if DATA_TYPE isn't REF)
     *  <LI><B>SCOPE_TABLE</B> String => table name that is the scope of a 
     *      reference attribute (<code>null</code> if the DATA_TYPE isn't REF)
     * <LI><B>SOURCE_DATA_TYPE</B> short => source type of a distinct type or user-generated
     *      Ref type,SQL type from java.sql.Types (<code>null</code> if DATA_TYPE 
     *      isn't DISTINCT or user-generated REF)
     *  </OL>
     * @param catalog a catalog name; must match the catalog name as it
     *        is stored in the database; "" retrieves those without a catalog;
     *        <code>null</code> means that the catalog name should not be used to narrow
     *        the search
     * @param schemaPattern a schema name pattern; must match the schema name
     *        as it is stored in the database; "" retrieves those without a schema;
     *        <code>null</code> means that the schema name should not be used to narrow
     *        the search
     * @param typeNamePattern a type name pattern; must match the
     *        type name as it is stored in the database 
     * @param attributeNamePattern an attribute name pattern; must match the attribute
     *        name as it is declared in the database
     * @return a <code>ResultSet</code> object in which each row is an 
     *         attribute description
     * @exception SQLException if a database access error occurs
     * @since 1.4
     */
  RJResultSetInterface getAttributes(String catalog, String schemaPattern,
   String typeNamePattern, String attributeNamePattern) 
   throws RemoteException, SQLException;

    /**
     * Retrieves whether this database supports the given result set holdability.
     *
     * @param holdability one of the following constants:
     *          <code>ResultSet.HOLD_CURSORS_OVER_COMMIT</code> or
     *          <code>ResultSet.CLOSE_CURSORS_AT_COMMIT<code>
     * @return <code>true</code> if so; <code>false</code> otherwise 
     * @exception SQLException if a database access error occurs
     * @see Connection
     * @since 1.4
     */
  boolean supportsResultSetHoldability(int holdability) throws RemoteException, SQLException;

    /**
     * Retrieves the default holdability of this <code>ResultSet</code>
     * object.
     *
     * @return the default holdability; either 
     *         <code>ResultSet.HOLD_CURSORS_OVER_COMMIT</code> or
     *         <code>ResultSet.CLOSE_CURSORS_AT_COMMIT</code>
     * @exception SQLException if a database access error occurs
     * @since 1.4
     */
  int getResultSetHoldability() throws RemoteException, SQLException;

    /**
     * Retrieves the major version number of the underlying database.
     *
     * @return the underlying database's major version
     * @exception SQLException if a database access error occurs
     * @since 1.4
     */
    int getDatabaseMajorVersion() throws RemoteException, SQLException;

    /**
     * Retrieves the minor version number of the underlying database.
     *
     * @return underlying database's minor version
     * @exception SQLException if a database access error occurs
     * @since 1.4
     */
    int getDatabaseMinorVersion() throws RemoteException, SQLException;

    /**
     * Retrieves the major JDBC version number for this
     * driver.
     * 
     * @return JDBC version major number
     * @exception SQLException if a database access error occurs
     * @since 1.4
     */
    int getJDBCMajorVersion() throws RemoteException, SQLException;

    /**
     * Retrieves the minor JDBC version number for this
     * driver.
     * 
     * @return JDBC version minor number
     * @exception SQLException if a database access error occurs
     * @since 1.4
     */
    int getJDBCMinorVersion() throws RemoteException, SQLException;

    /**
     * Indicates that the value is an
     * X/Open (now know as Open Group) SQL CLI SQLSTATE value.
     * <P>
     * A possible return value for the method
     * <code>SQLException.getSQLState</code>.
     * @since 1.4
     */
    int sqlStateXOpen = 1;

    /**
     * Indicates that the value is an SQL99 SQLSTATE value.
     * <P>
     * A possible return value for the method
     * <code>SQLException.getSQLState</code>.
     * @since 1.4
     */
    int sqlStateSQL99 = 2;

    /**
     * Indicates whether the SQLSTATEs returned by <code>SQLException.getSQLState</code>
     * is X/Open (now known as Open Group) SQL CLI or SQL99.
     * @return the type of SQLSTATEs, one of:
     *        sqlStateXOpen or
     *        sqlStateSQL99
     * @throws SQLException if a database access error occurs
     * @since 1.4
     */
    int getSQLStateType() throws RemoteException, SQLException;

    /**
     * Indicates whether updates made to a LOB are made on a copy or directly 
     * to the LOB.
     * @return <code>true</code> if updates are made to a copy of the LOB;
     *         <code>false</code> if updates are made directly to the LOB
     * @throws SQLException if a database access error occurs
     * @since 1.4
     */
    boolean locatorsUpdateCopy() throws RemoteException, SQLException;

    /**
     * Retrieves weather this database supports statement pooling.
     *
     * @return <code>true</code> is so;
	       <code>false</code> otherwise
     * @throws SQLExcpetion if a database access error occurs
     * @since 1.4
     */
    boolean supportsStatementPooling() throws RemoteException, SQLException;

};

