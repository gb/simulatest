package org.simulatest.insistencelayer.remote;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collection;
import org.simulatest.environment.plugin.SimulatestPlugin;
import org.simulatest.insistencelayer.InsistenceLayer;
import org.simulatest.insistencelayer.InsistenceLayerFactory;

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
public class RemoteInsistenceLayerPlugin implements SimulatestPlugin {

	private InsistenceLayerServer server;
	private RemoteInsistenceLayer remote;

	@Override
	public void initialize(Collection<Class<?>> testClasses) {
		InsistenceLayer local = InsistenceLayerFactory.build(
			InsistenceLayerFactory.requireDataSource().getConnectionWrapper()
		);

		try {
			server = new InsistenceLayerServer(local, 0);
			server.start();
		} catch (IOException e) {
			throw new UncheckedIOException("Failed to start Insistence Layer server", e);
		}

		remote = new RemoteInsistenceLayer("localhost", server.getPort());
		InsistenceLayerFactory.register(InsistenceLayerFactory.DEFAULT, remote);
	}

	@Override
	public void destroy() {
		if (remote != null) remote.close();
		try {
			if (server != null) server.stop();
		} catch (IOException e) {
			throw new UncheckedIOException("Failed to stop Insistence Layer server", e);
		}
		InsistenceLayerFactory.deregister(InsistenceLayerFactory.DEFAULT);
	}
}
