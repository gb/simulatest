package org.objectweb.rmijdbc;

import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;

public class RMIRepository {
	
	private RMIRepository() {
		
	}
	
	public static RMIClientSocketFactory rmiClientSocketFactory = new RJClientSocketFactory();
	public static RMIServerSocketFactory rmiServerSocketFactory = new RJServerSocketFactory();
	public static int rmiJdbcListenerPort = 0;

}
