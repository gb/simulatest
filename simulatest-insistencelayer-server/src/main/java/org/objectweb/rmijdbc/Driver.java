/**
 * RmiJdbc client/server JDBC Driver
 * (C) GIE Dyade (Groupe BULL / INRIA Research Center) 1997
 *
 * @version     1.0
 * @author      Pierre-Yves Gibello (pierreyves.gibello@experlog.com)
 */

package org.objectweb.rmijdbc;

import java.io.Serializable;
import java.net.InetAddress;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * <P>
 * The Java SQL framework allows for multiple database drivers.
 * 
 * <P>
 * Each driver should supply a class that implements the Driver interface.
 * 
 * <P>
 * The DriverManager will try to load as many drivers as it can find and then
 * for any given connection request, it will ask each driver in turn to try to
 * connect to the target URL.
 * 
 * <P>
 * It is strongly recommended that each Driver class should be small and
 * standalone so that the Driver class can be loaded and queried without
 * bringing in vast quantities of supporting code.
 * 
 * <P>
 * When a Driver class is loaded, it should create an instance of itself and
 * register it with the DriverManager. This means that a user can load and
 * register a driver by doing Class.forName("foo.bah.Driver").
 * 
 * @see DriverManager
 * @see Connection
 */
public class Driver implements java.sql.Driver, Serializable {
	
	private static final long serialVersionUID = -1506225066772653534L;

	static {
		try {
			DriverManager.registerDriver(new org.objectweb.rmijdbc.Driver());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static int RMI_REGISTRY = 0;
	private static int RMI_ADDRESS = 1;
	private static int JDBC_URL = 2;

	private String[] splitURL(String url) {

		/*
		 * The url has the format:
		 * "jdbc:rmi://<serverHostName[:port]>/<serverJdbcDriverName>" First,
		 * get a reference to the RJDriverServer running on computer
		 * <serverHostName>. Then, connect using the server's registered jdbc
		 * driver
		 */

		String serverHostName, jdbcUrl;
		if (url.substring(9, 11).equals("//")) {
			try {
				serverHostName = url.substring(9, url.indexOf("/", 11));
			} catch (Exception e) {
				serverHostName = url.substring(9);
			}
			try {
				jdbcUrl = url.substring(url.indexOf("/", 11) + 1);
			} catch (Exception e) {
				jdbcUrl = null;
			}
		} else {
			try {
				serverHostName = "//"
						+ InetAddress.getLocalHost().getHostName();
			} catch (Exception e) {
				System.err
						.println("WARNING: can\'t retrieve local host name, //localhost will be used !"
								+ " Exception follows:");
				e.printStackTrace();
				serverHostName = "//localhost";
			}

			jdbcUrl = url.substring(9);

			// Check if there was something wierd in the RMI URL part:
			// If so, skip it!
			if (!jdbcUrl.toLowerCase().startsWith("jdbc")) {
				System.err
						.println("WARNING: Wierd RMI server URL: localhost will be used.");
				jdbcUrl = url.substring(url.indexOf("/", 9) + 1);
			}
		}
		String rmiAddr = serverHostName + "/RmiJdbcServer";

		String split[] = new String[3];
		split[RMI_REGISTRY] = serverHostName.substring(2);
		split[RMI_ADDRESS] = rmiAddr;
		split[JDBC_URL] = jdbcUrl;
		return split;
	}

	private RJDriverInterface lookupDriver(String url) throws Exception {
		String split[] = splitURL(url);

		// Reg is of the form: host[:port]
		StringTokenizer st = new StringTokenizer(split[RMI_REGISTRY], ":");
		if (!st.hasMoreTokens()) {
			throw new SQLException("No RMI server host specified in JDBC URL");
		}
		String host = st.nextToken();
		int port = 1099;
		if (st.hasMoreTokens()) {
			try {
				port = Integer.parseInt(st.nextToken());
			} catch (NumberFormatException e) {
				port = 1099;
			}
		}

		Registry registry = LocateRegistry.getRegistry(host, port);

		// rmiAddr is an RMI object name (ex. //host/objName)
		// Note that:
		// An internal registry registers //host/objName as "//host/objName"
		// An external registry registers //host/objName as "objName"
		RJDriverInterface theDriver;
		try {
			theDriver = (RJDriverInterface) registry.lookup(split[RMI_ADDRESS]);
		} catch (NotBoundException e) {
			String obj;
			int pos = split[RMI_ADDRESS].lastIndexOf("/");
			if (pos < 0)
				obj = split[RMI_ADDRESS];
			else
				obj = split[RMI_ADDRESS].substring(pos + 1);
			theDriver = (RJDriverInterface) registry.lookup(obj);
		}

		return theDriver;
	}

	/**
	 * Administrative methods
	 */
	public void shutdown(String url, String pwd) throws RemoteException {

		try {

			RJDriverInterface drv = lookupDriver(url);
			if (drv != null) {
				try {
					drv.shutdown(pwd);
				} catch (java.rmi.UnmarshalException e) {
					// ignore
				}
			}

		} catch (RemoteException e) {
			throw e;
		} catch (Exception e) {
			throw new RemoteException(e.getMessage());
		}
	}

	/**
	 * Try to make a database connection to the given URL. The driver should
	 * return "null" if it realizes it is the wrong kind of driver to connect to
	 * the given URL. This will be common, as when the JDBC driver manager is
	 * asked to connect to a given URL it passes the URL to each loaded driver
	 * in turn.
	 * 
	 * <P>
	 * The driver should raise a SQLException if it is the right driver to
	 * connect to the given URL, but has trouble connecting to the database.
	 * 
	 * <P>
	 * The java.util.Properties argument can be used to passed arbitrary string
	 * tag/value pairs as connection arguments. Normally at least "user" and
	 * "password" properties should be included in the Properties.
	 * 
	 * @param url
	 *            The URL of the database to connect to
	 * 
	 * @param info
	 *            a list of arbitrary string tag/value pairs as connection
	 *            arguments; normally at least a "user" and "password" property
	 *            should be included
	 * 
	 * @return a Connection to the URL
	 */
	public java.sql.Connection connect(String url, Properties info)
			throws SQLException {

		if (!acceptsURL(url))
			return null; // Will cause a "No suitable driver" upon DriverManager
							// call

		String split[] = splitURL(url);
		try {
			// url and info are JDBC data
			return new RJConnection(lookupDriver(url), split[JDBC_URL], info);

		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new SQLException(e.getMessage());
		}
	}

	/**
	 * Returns true if the driver thinks that it can open a connection to the
	 * given URL. Typically drivers will return true if they understand the
	 * subprotocol specified in the URL and false if they don't.
	 * 
	 * @param url
	 *            The URL of the database.
	 * @return True if this driver can connect to the given URL.
	 */
	public boolean acceptsURL(String url) throws SQLException {
		return url.startsWith("jdbc:rmi:");
	}

	/**
	 * <p>
	 * The getPropertyInfo method is intended to allow a generic GUI tool to
	 * discover what properties it should prompt a human for in order to get
	 * enough information to connect to a database. Note that depending on the
	 * values the human has supplied so far, additional values may become
	 * necessary, so it may be necessary to iterate though several calls to
	 * getPropertyInfo.
	 * 
	 * @param url
	 *            The URL of the database to connect to.
	 * @param info
	 *            A proposed list of tag/value pairs that will be sent on
	 *            connect open.
	 * @return An array of DriverPropertyInfo objects describing possible
	 *         properties. This array may be an empty array if no properties are
	 *         required.
	 */
	public java.sql.DriverPropertyInfo[] getPropertyInfo(String url,
			java.util.Properties info) throws SQLException {
		try {

			RJDriverInterface drv = lookupDriver(url);
			String split[] = splitURL(url);

			// DriverPropertyInfo is not serializable:
			// the reason for RJDriverPropertyInfo !
			RJDriverPropertyInfo infos[] = drv.getPropertyInfo(split[JDBC_URL],
					info);
			if (infos == null)
				return null;
			DriverPropertyInfo dpis[] = new DriverPropertyInfo[infos.length];
			for (int i = 0; i < infos.length; i++) {
				if (infos[i] == null)
					dpis[i] = null;
				dpis[i] = infos[i].getPropertyInfo();
			}
			return dpis;

		} catch (RemoteException e) {
			throw new SQLException("[RemoteException] " + e.getMessage());
		} catch (java.rmi.NotBoundException e) {
			throw new SQLException("[java.rmi.NotBoundException] "
					+ e.getMessage());
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		}
	}

	/**
	 * Get the driver's major version number. Initially this should be 1.
	 */
	public int getMajorVersion() {
		return 1;
	}

	/**
	 * Get the driver's minor version number. Initially this should be 0.
	 */
	public int getMinorVersion() {
		return 0;
	}

	/**
	 * Report whether the Driver is a genuine JDBC COMPLIANT (tm) driver. A
	 * driver may only report "true" here if it passes the JDBC compliance
	 * tests, otherwise it is required to return false.
	 * 
	 * JDBC compliance requires full support for the JDBC API and full support
	 * for SQL 92 Entry Level. It is expected that JDBC compliant drivers will
	 * be available for all the major commercial databases.
	 * 
	 * This method is not intended to encourage the development of non-JDBC
	 * compliant drivers, but is a recognition of the fact that some vendors are
	 * interested in using the JDBC API and framework for lightweight databases
	 * that do not support full database functionality, or for special databases
	 * such as document information retrieval where a SQL implementation may not
	 * be feasible.
	 */
	public boolean jdbcCompliant() {
		return true;
	}

};
