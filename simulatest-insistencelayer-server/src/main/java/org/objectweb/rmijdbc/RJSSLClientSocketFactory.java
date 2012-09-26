/*
 * RMISSLClientSocketFactory.java
 *
 * Created on October 7, 2001, 6:18 PM
 * @Author Douglas Hammond(djhammond@sympatico.ca)
 */

package org.objectweb.rmijdbc;

import java.net.*;
import java.io.*;
import javax.net.ssl.*;
//import com.sun.net.ssl.*;
import java.rmi.server.*;

/**
 * SocketFactory with SSL support
 */
public class RJSSLClientSocketFactory implements RMIClientSocketFactory, Serializable {
	
	private static final long serialVersionUID = -2873209942187563674L;

	public Socket createSocket(String host, int port) throws IOException {
		SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		SSLSocket sslSocket = null;
		
		try {
			sslSocket = (SSLSocket) factory.createSocket(host, port);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(2);
		}
		
		return sslSocket;
	}
}
