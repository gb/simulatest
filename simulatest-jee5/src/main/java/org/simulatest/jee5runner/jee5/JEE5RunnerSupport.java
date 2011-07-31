package org.simulatest.jee5runner.jee5;

import org.jboss.ejb3.embedded.EJB3StandaloneBootstrap;
import org.jboss.ejb3.embedded.EJB3StandaloneDeployer;

public class JEE5RunnerSupport {

	private static boolean containerRunning = false;
	private EJB3StandaloneDeployer deployer;

	public void startupEmbeddedContainer() throws Exception {
		if (containerRunning) return;

		EJB3StandaloneBootstrap.boot(null);
		deployer = EJB3StandaloneBootstrap.createDeployer();

		deployer.getArchivesByResource().add("META-INF/persistence.xml");
		EJB3StandaloneBootstrap.deployXmlResource("jboss-jms-beans.xml");
		EJB3StandaloneBootstrap.deployXmlResource("jboss-jms-queues.xml");

		deployer.create();
		deployer.start();

		containerRunning = true;
		System.out.println("startando container");
	}
	
	public void terminate() throws Exception {
		deployer.stop();
		deployer.destroy();

		EJB3StandaloneBootstrap.shutdown();

		containerRunning = false;
	}

}