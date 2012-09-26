
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

public interface RJSavepointInterface extends java.rmi.Remote {

  int getSavepointId() throws RemoteException, SQLException;

  String getSavepointName() throws RemoteException, SQLException;

};

