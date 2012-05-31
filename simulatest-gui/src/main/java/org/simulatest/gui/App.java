package org.simulatest.gui;

import java.sql.SQLException;

import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.Startable;
import org.picocontainer.behaviors.Caching;
import org.simulatest.insistencelayer.InsistenceLayerManager;
import org.simulatest.insistencelayer.InsistenceLayerManagerFactory;
import org.simulatest.insistencelayer.connection.ConnectionFactory;

public class App implements Startable {
	
	private final TestToolsForm testToolsForm;

	public App(TestToolsForm testToolsForm) {
		this.testToolsForm = testToolsForm;		
	}
	
	public static void main(String[] args) throws SQLException {
		InsistenceLayerManager insistenceLayer = 
				InsistenceLayerManagerFactory.build(ConnectionFactory.getConnection());
		
		
		DefaultPicoContainer container = new DefaultPicoContainer(new Caching());	
		
		container.addComponent(insistenceLayer);
		container.addComponent(EnvironmentRunner.class);
		container.addComponent(InsistenceLayerForm.class);
		container.addComponent(App.class);
		container.addComponent(TestToolsForm.class);		
		
		container.start();
	}

	
	@Override
	public void start() {
		testToolsForm.setVisible(true);
	}

	@Override
	public void stop() { }
	
}
