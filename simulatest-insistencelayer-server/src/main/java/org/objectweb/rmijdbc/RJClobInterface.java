
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

public interface RJClobInterface extends java.rmi.Remote {

// TBD There's a hack there (InputStream not serializable)
  byte[] getAsciiStream() throws RemoteException, SQLException;

// TBD There's a hack there (Reader not serializable)
  char[] getCharacterStream() throws RemoteException, SQLException;

  long length() throws RemoteException, SQLException;

  String getSubString(long pos, int length)
  throws RemoteException, SQLException;

  long position(String searchstr, long start)
  throws RemoteException, SQLException;

  long position(Clob searchstr, long start)
  throws RemoteException, SQLException;

//---------------------------- jdbc 3.0 -----------------------------------
  int setString(long pos, String str)
  throws RemoteException, SQLException;
 
  int setString(long pos, String str, int offset, int len)
  throws RemoteException, SQLException;
 
  java.io.OutputStream setAsciiStream(long pos)
  throws RemoteException, SQLException;
 
  java.io.Writer setCharacterStream(long pos)
  throws RemoteException, SQLException;
 
  void truncate(long len)
  throws RemoteException, SQLException;
};

