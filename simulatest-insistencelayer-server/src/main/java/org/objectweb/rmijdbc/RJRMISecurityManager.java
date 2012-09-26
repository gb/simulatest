
/**
 * RmiJdbc client/server JDBC Driver
 * (C) GIE Dyade (Groupe BULL / INRIA Research Center) 1997
 *
 * @version     1.0
 * @author      Pierre-Yves Gibello (pierreyves.gibello@experlog.com)
 * @updated		Francois Orsini (Cloudscape Inc.)
 *				Removed/relaxed some of the RMI Security Manager restrictions.
 */

package org.objectweb.rmijdbc;

import java.io.FileDescriptor;
import java.rmi.*;

/**
 * <P>This is a specialized RMI Security Manager for RmiJdbc when _embedded_
 * applications/components are being accessed through RmiJdbc driver and
 * run in the same JVM than RmiJdbc.
 * This is required in order for the RMI threads to perform successfully
 * when accessing database/local files for instance and performing I/O
 * operations. The default RMI Security Manager is very restrictive and does
 * not allow RMI thread to perform some of the I/O operations for instance.
 *
 * <P><B>Note:</B> There might be a cleaner solution in a near future, but
 * right now this one allows embedded applications that does I/O operations
 * throughout the RMI thread(s) to run inside RmiJdbc server.
 *
 * @see RMISecurityManager
 */

public class RJRMISecurityManager
extends RMISecurityManager {

  /**
   * We do not restrict anything in particular here
   */
  public void checkRead(FileDescriptor fd)
  throws java.lang.SecurityException {
  	return;
  }

  /**
   * We do not restrict anything in particular here
   */
  public void checkRead(String file)
  throws java.lang.SecurityException {
  	return;
  }

  /**
   * We do not restrict anything in particular here
   */
  public void checkRead(String file, Object context)
  throws java.lang.SecurityException {
  	return;
  }

  /**
   * We do not restrict anything in particular here
   */
  public void checkWrite(FileDescriptor fd)
  throws java.lang.SecurityException {
  	return;
  }

  /**
   * We do not restrict anything in particular here
   */
  public void checkWrite(String file)
  throws java.lang.SecurityException {
  	return;
  }

  /**
   * We do not restrict anything in particular here
   */
  public void checkDelete(String file)
  throws java.lang.SecurityException {
  	return;
  }

  /**
   * We do not restrict anything in particular here
   */
  public synchronized void checkCreateClassLoader()
  throws java.lang.SecurityException {
  	return;
  }

  /**
   * We do not restrict anything in particular here
   */
  public void checkMemberAccess(Class<?> clazz, int which)
  {
  	return;
  }

  /**
   * We do not restrict anything in particular here
   */
  public synchronized void checkExec(String cmd)
  {
  	return;
  }

  /**
   * We do not restrict anything in particular here
   */
  public void checkPropertyAccess(String key)
  {
  	return;
  }

  /**
   * We do not restrict anything in particular here
   */
  public void checkPropertiesAccess()
  {
  	return;
  }
}
