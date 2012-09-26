/*
 * RMISSLServerSocketFactory.java
 *
 * Created on October 7, 2001, 6:17 PM
 * @Author Douglas Hammond(djhammond@sympatico.ca)
 */

package org.objectweb.rmijdbc;

import java.net.*;
import java.io.*;
import javax.net.ssl.*;
import java.rmi.server.*;

/**
 * ServerSocketFactory with SSL support
 */
public class RJSSLServerSocketFactory implements RMIServerSocketFactory, Serializable {

	private static final long serialVersionUID = 8422315949751316945L;

	public ServerSocket createServerSocket(int serverPort) throws IOException {
		try {
			SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
			SSLServerSocket sslSocket = (SSLServerSocket) factory.createServerSocket(serverPort);
			return sslSocket;
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		return null;
	}
}
