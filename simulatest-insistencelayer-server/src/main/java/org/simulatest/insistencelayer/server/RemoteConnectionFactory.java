package org.simulatest.insistencelayer.server;

import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.DriverManager;

import org.simulatest.insistencelayer.connection.ConnectionBean;
import org.simulatest.insistencelayer.server.infra.InsistenceLayerServerException;

import com.google.common.base.Preconditions;

public class RemoteConnectionFactory extends UnicastRemoteObject implements RMIConnectionFactory {

	private static final long serialVersionUID = -5926268020903095808L;
	
	private ConnectionBean connectionBean;
	private Connection connection;
	
	protected RemoteConnectionFactory() throws RemoteException {
	}

	@Override
	public Connection getConnection() throws RemoteException {
		if (connection == null) initializeConnection();
		return connection;
	}

	private void initializeConnection() {
		Preconditions.checkNotNull(connectionBean, "ConnectionBean should be registered before getConnection");
		
		try {
			Class.forName("org.objectweb.rmijdbc.Driver").newInstance();
			
			String rmiHost = "jdbc:rmi://" + InetAddress.getLocalHost().getHostName();
			String url = connectionBean.getUrl();
			String user = connectionBean.getUsername();
			String password = connectionBean.getPassword();
			
			connection = DriverManager.getConnection(rmiHost + "/" + url, user, password);
		} catch (Exception e) {
			throw new InsistenceLayerServerException(e);
		}
	}

	@Override
	public void registerConnectionBean(ConnectionBean connectionBean) throws RemoteException {
		this.connectionBean = connectionBean;
	}

	@Override
	public boolean isServerAvailable() throws RemoteException {
		return true;
	}

}
