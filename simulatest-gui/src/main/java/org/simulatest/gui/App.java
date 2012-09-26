package org.simulatest.gui;

import java.sql.SQLException;

import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.Startable;
import org.picocontainer.behaviors.Caching;
import org.simulatest.insistencelayer.InsistenceLayerManager;
import org.simulatest.insistencelayer.InsistenceLayerManagerFactory;
import org.simulatest.insistencelayer.connection.ConnectionFactory;

public class App implements Startable {
	
	private final SimulatestHub hub;

	public App(SimulatestHub hub) {
		this.hub = hub;		
	}
	
	public static void main(String[] args) throws SQLException {
		InsistenceLayerManager insistenceLayer = 
				InsistenceLayerManagerFactory.build(ConnectionFactory.getConnection());
		
		DefaultPicoContainer container = new DefaultPicoContainer(new Caching());	
		
		container.addComponent(insistenceLayer);
		container.addComponent(EnvironmentRunner.class);
		container.addComponent(InsistenceLayerForm.class);
		container.addComponent(App.class);
		container.addComponent(SimulatestHub.class);	
		container.addComponent(SimulatestSQLWindow.class);
		container.addComponent(InsistenceLayerServerForm.class);
		
		container.start();
	}
	
	@Override
	public void start() {
		hub.setVisible(true);
	}

	@Override
	public void stop() { }
	
}
