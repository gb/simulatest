/*
 * RMISSLClientSocketFactory.java
 *
 * Created on October 7, 2001, 6:18 PM
 * @Author Douglas Hammond(djhammond@sympatico.ca)
 */

package org.objectweb.rmijdbc;

import java.net.*;
import java.io.*;
import java.rmi.server.*;

/**
 * Default Socket Factory
 */
public class RJClientSocketFactory implements RMIClientSocketFactory, Serializable {

	private static final long serialVersionUID = -5287201315999845988L;

	public Socket createSocket(String host, int port) throws IOException {
		return new Socket(host, port);
	}
}
