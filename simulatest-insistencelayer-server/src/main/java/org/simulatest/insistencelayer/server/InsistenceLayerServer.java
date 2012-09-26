package org.simulatest.insistencelayer.server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.Connection;

import org.apache.log4j.Logger;
import org.objectweb.rmijdbc.RJDriverServer;
import org.objectweb.rmijdbc.RMIRepository;
import org.simulatest.insistencelayer.connection.ConnectionBean;
import org.simulatest.insistencelayer.server.infra.InsistenceLayerServerException;

public class InsistenceLayerServer {

	private static final int PORT = 1099;
	private static final Logger logger = Logger.getLogger(RMIRepository.class);

	public static void start() {
		logger.info("Starting InsistenceLayerServer!");
		initializeServer();
	}

	public static boolean isAvailable() {
		try {
			RMIConnectionFactory rmi = (RMIConnectionFactory) registry().lookup("InsistenceLayer");
			return rmi.isServerAvailable();
		} catch (Exception e) {
			return false;
		}
	}

	public static void shutdown() throws Exception {
		logger.info("Shutdowning InsistenceLayerServer!");
		
		try {
			registry().unbind("RmiJdbcServer");
			registry().unbind("InsistenceLayer");
		} catch (Throwable e) {
			throw new InsistenceLayerServerException("InsistenceLayerServer is already down", e);
		}
	}
	
	private static void initializeServer() {
		try {
			logger.info("Binding InsistenceLayer...");

			Registry registry = LocateRegistry.createRegistry(PORT);
			registry.bind("RmiJdbcServer", new RJDriverServer(null));
			registry.bind("InsistenceLayer", new RemoteConnectionFactory());

			logger.info("InsistenceLayer has been bound in RMI registry");
		} catch (Exception e) {
			throw new InsistenceLayerServerException("Error initializing InsistenceLayerServer", e);
		}
	}
	
	private static Registry registry() throws RemoteException {
		return LocateRegistry.getRegistry(PORT);
	}
	
	public static void registerConnectionBean(ConnectionBean connectionBean) throws RemoteException {
		rmiConnectionFactory().registerConnectionBean(connectionBean);
	}
	
	public static Connection getConnection() throws RemoteException {
		return rmiConnectionFactory().getConnection();
	}

	private static RMIConnectionFactory rmiConnectionFactory() {
		try {
			return (RMIConnectionFactory) LocateRegistry.getRegistry(1099).lookup("InsistenceLayer");
		} catch (Exception e) {
			throw new InsistenceLayerServerException(e);
		}
	}

}
