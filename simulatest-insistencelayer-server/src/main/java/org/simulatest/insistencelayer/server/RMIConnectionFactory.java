package org.simulatest.insistencelayer.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.Connection;

import org.simulatest.insistencelayer.connection.ConnectionBean;

public interface RMIConnectionFactory extends Remote {

	Connection getConnection() throws RemoteException;
	
	void registerConnectionBean(ConnectionBean connectionBean) throws RemoteException;
	
	boolean isServerAvailable() throws RemoteException;
	
}
