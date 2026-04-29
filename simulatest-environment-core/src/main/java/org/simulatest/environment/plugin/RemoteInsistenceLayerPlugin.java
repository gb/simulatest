package org.simulatest.environment.plugin;

import java.util.Collection;
import org.simulatest.environment.infra.ExceptionAggregator;
import org.simulatest.insistencelayer.InsistenceLayer;
import org.simulatest.insistencelayer.InsistenceLayerFactory;
import org.simulatest.insistencelayer.remote.InsistenceLayerServer;
import org.simulatest.insistencelayer.remote.RemoteInsistenceLayer;

/**
 * Plugin that interposes a TCP layer between the Simulatest engine and the
 * local Insistence Layer. All savepoint commands travel through the wire,
 * proving the remote protocol works as a drop-in replacement.
 *
 * <p>Requires a DataSource to be configured via
 * {@link InsistenceLayerFactory#configure} first.
 * List this plugin AFTER the datasource-configuring plugin in the
 * ServiceLoader file.
 */
public final class RemoteInsistenceLayerPlugin implements SimulatestPlugin {

	private InsistenceLayerServer server;
	private RemoteInsistenceLayer remote;

	@Override
	public void initialize(Collection<Class<?>> testClasses) {
		InsistenceLayer local = InsistenceLayerFactory.build(
			InsistenceLayerFactory.requireDataSource().getConnectionWrapper()
		);

		server = new InsistenceLayerServer(local, 0);
		server.start();

		remote = new RemoteInsistenceLayer("localhost", server.getPort());
		InsistenceLayerFactory.register(InsistenceLayerFactory.DEFAULT, remote);
	}

	@Override
	public void destroy() {
		ExceptionAggregator failures = new ExceptionAggregator();
		if (remote != null) failures.capture(remote::close);
		if (server != null) failures.capture(server::stop);
		InsistenceLayerFactory.deregister(InsistenceLayerFactory.DEFAULT);
		failures.throwIfAny();
	}
}
