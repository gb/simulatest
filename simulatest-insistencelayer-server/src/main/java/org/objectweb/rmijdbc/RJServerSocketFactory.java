/*
 * RMISSLServerSocketFactory.java
 *
 * Created on October 7, 2001, 6:17 PM
 * @Author Douglas Hammond(djhammond@sympatico.ca)
 */

package org.objectweb.rmijdbc;

import java.net.*;
import java.io.*;
import java.rmi.server.*;

/**
 * Default ServerSocket Factory
 */
public class RJServerSocketFactory implements RMIServerSocketFactory, Serializable {
	
	private static final long serialVersionUID = -388343097141857840L;

	public ServerSocket createServerSocket(int serverPort) throws IOException {
		return new ServerSocket(serverPort);
	}
	
}
