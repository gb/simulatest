package org.simulatest.insistencelayer.remote;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collection;

import org.simulatest.environment.environment.SimulatestPlugin;
import org.simulatest.insistencelayer.InsistenceLayerManager;
import org.simulatest.insistencelayer.InsistenceLayerManagerFactory;
import org.simulatest.insistencelayer.InsistenceLayerManagerHolder;
import org.simulatest.insistencelayer.datasource.InsistenceLayerDataSource;

/**
 * Plugin that interposes a TCP layer between the Simulatest engine and the
 * local Insistence Layer. All savepoint commands travel through the wire,
 * proving the remote protocol works as a drop-in replacement.
 *
 * <p>Requires {@link InsistenceLayerDataSource} to be configured first.
 * List this plugin AFTER the datasource-configuring plugin in the
 * ServiceLoader file.
 */
public class RemoteInsistenceLayerPlugin implements SimulatestPlugin {

	private InsistenceLayerServer server;
	private RemoteInsistenceLayerManager remote;

	@Override
	public void initialize(Collection<Class<?>> testClasses) {
		InsistenceLayerManager local = InsistenceLayerManagerFactory.build(
			InsistenceLayerDataSource.getDefault().getConnectionWrapper()
		);

		try {
			server = new InsistenceLayerServer(local, 0);
			server.start();
		} catch (IOException e) {
			throw new UncheckedIOException("Failed to start Insistence Layer server", e);
		}

		remote = new RemoteInsistenceLayerManager("localhost", server.getPort());
		InsistenceLayerManagerHolder.set(remote);
	}

	@Override
	public void destroy() {
		if (remote != null) remote.close();
		try {
			if (server != null) server.stop();
		} catch (IOException e) {
			throw new UncheckedIOException("Failed to stop Insistence Layer server", e);
		}
		InsistenceLayerManagerHolder.clear();
	}
}
