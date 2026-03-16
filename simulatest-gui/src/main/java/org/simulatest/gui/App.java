package org.simulatest.gui;

import java.sql.SQLException;

import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.Startable;
import org.picocontainer.behaviors.Caching;
import org.simulatest.insistencelayer.InsistenceLayer;
import org.simulatest.insistencelayer.InsistenceLayerFactory;

public class App implements Startable {

	private final SimulatestHub hub;

	public App(SimulatestHub hub) {
		this.hub = hub;
	}

	public static void main(String[] args) throws SQLException {
		InsistenceLayer insistenceLayer =
				InsistenceLayerFactory.resolve();
		
		DefaultPicoContainer container = new DefaultPicoContainer(new Caching());	
		
		container.addComponent(insistenceLayer);
		container.addComponent(EnvironmentRunner.class);
		container.addComponent(InsistenceLayerForm.class);
		container.addComponent(App.class);
		container.addComponent(SimulatestHub.class);	
		container.addComponent(SimulatestSQLWindow.class);
		container.start();
	}
	
	@Override
	public void start() {
		hub.setVisible(true);
	}

	@Override
	public void stop() { }
	
}
