
/**
 * RmiJdbc client/server JDBC Driver
 * (C) ObjectWeb 1999-2003
 *
 * @version     3.0
 * @author      Pierre-Yves Gibello (pierreyves.gibello@experlog.com)
 */

package org.objectweb.rmijdbc;

import java.sql.SQLException;
import java.rmi.RemoteException;

public interface RJParameterMetaDataInterface extends java.rmi.Remote {

  String getParameterClassName(int param) throws RemoteException, SQLException;
  int getParameterCount() throws RemoteException, SQLException;
  int getParameterMode(int param) throws RemoteException, SQLException;
  int getParameterType(int param) throws RemoteException, SQLException;
  String getParameterTypeName(int param) throws RemoteException, SQLException;
  int getPrecision(int param) throws RemoteException, SQLException;
  int getScale(int param) throws RemoteException, SQLException;
  int isNullable(int param) throws RemoteException, SQLException;
  boolean isSigned(int param) throws RemoteException, SQLException;

};

