
/**
 * RmiJdbc client/server JDBC Driver
 * (C) ExperLog 1999-2000
 *
 * @version     1.0
 * @author      Pierre-Yves Gibello (pierreyves.gibello@experlog.com)
 */

package org.objectweb.rmijdbc;

import java.sql.*;
import java.rmi.RemoteException;

/**
 * The representation (mapping) in the JavaTM programming language of an
 * SQL BLOB. An SQL BLOB is
 * a built-in type that stores a Binary Large Object as a column value in
 * a row of a database table. The
 * driver implements Blob using an SQL locator(BLOB), which means that
 * a Blob object contains a logical pointer to the SQL BLOB data rather
 * than the data itself. A Blob object is valid for
 * the duration of the transaction in which is was created. 
 * 
 * Methods in the interfaces ResultSet, CallableStatement, and
 * PreparedStatement, such as getBlob and setBlob allow a programmer
 * to access the SQL BLOB. The Blob interface provides methods for getting
 * the length of an SQL BLOB (Binary Large Object) value, for
 * materializing a BLOB value on the client, and for determining the position
 * of a pattern of bytes within a BLOB value. 
 */

public interface RJBlobInterface extends java.rmi.Remote {

  long length() throws RemoteException, SQLException;

  byte[] getBytes(long pos, int length) throws RemoteException, SQLException;

// TBD There's a hack there (InputStream not serializable)
  byte[] getBinaryStream() throws RemoteException, SQLException;

  long position(byte[] pattern, long start)
  throws RemoteException, SQLException;

  long position(Blob pattern, long start)
  throws RemoteException, SQLException;

// -------------------------- JDBC 3.0 -----------------------------------
 
    int setBytes(long pos, byte[] bytes) throws RemoteException, SQLException;
 
    int setBytes(long pos, byte[] bytes, int offset, int len) throws RemoteException, SQLException;
 
    java.io.OutputStream setBinaryStream(long pos) throws RemoteException, SQLException;
 
    void truncate(long len) throws RemoteException, SQLException;

};

