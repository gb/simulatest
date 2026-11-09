package org.simulatest.insistencelayer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.sql.DataSource;

import org.simulatest.insistencelayer.infra.sql.InsistenceLayerDataSource;

/**
 * Holds Insistence Layer instances and their wrapped data sources.
 *
 * <p>Most users interact through the static convenience methods on
 * {@link InsistenceLayerFactory}, which delegate to a shared default
 * registry. When independent registries are needed (parallel test
 * execution, multi-datasource), create a separate instance.</p>
 *
 * <p><b>Thread-safety:</b> unconditionally thread-safe. All reads and
 * writes are serialized on the instance monitor. Insertion order is
 * preserved across concurrent modifications.</p>
 */
public final class InsistenceLayerRegistry {

	private static final String DEFAULT_KEY = InsistenceLayerFactory.DEFAULT;

	private final Map<String, InsistenceLayer> registry = new LinkedHashMap<>();
	private final Map<String, InsistenceLayerDataSource> dataSources = new LinkedHashMap<>();

	public synchronized void configure(DataSource dataSource) {
		Objects.requireNonNull(dataSource, "dataSource must not be null");
		InsistenceLayerDataSource wrapped = new InsistenceLayerDataSource(dataSource);
		dataSources.put(DEFAULT_KEY, wrapped);
		registry.put(DEFAULT_KEY, new LocalInsistenceLayer(wrapped.getConnectionWrapper()));
	}

	public synchronized Optional<InsistenceLayerDataSource> dataSource() {
		return dataSources.isEmpty()
				? Optional.empty()
				: Optional.of(dataSources.values().iterator().next());
	}

	public synchronized InsistenceLayerDataSource requireDataSource() {
		return dataSource().orElseThrow(() -> new IllegalStateException(
				"InsistenceLayer not configured - call InsistenceLayerFactory.configure(dataSource) first"));
	}

	public synchronized boolean isConfigured() {
		return !registry.isEmpty();
	}

	public synchronized void register(String name, InsistenceLayer layer) {
		Objects.requireNonNull(name, "name must not be null");
		Objects.requireNonNull(layer, "layer must not be null");
		registry.put(name, layer);
	}

	public synchronized void deregister(String name) {
		registry.remove(name);
	}

	public synchronized Optional<InsistenceLayer> resolve(String name) {
		return Optional.ofNullable(registry.get(name));
	}

	// Fallback chain: (1) return existing layer, (2) lazily build from configured
	// datasource, (3) empty if nothing is configured.
	public synchronized Optional<InsistenceLayer> resolve() {
		if (!registry.isEmpty()) {
			return Optional.of(registry.values().iterator().next());
		}

		if (!dataSources.isEmpty()) {
			InsistenceLayerDataSource ds = dataSources.values().iterator().next();
			InsistenceLayer layer = new LocalInsistenceLayer(ds.getConnectionWrapper());
			registry.put(DEFAULT_KEY, layer);
			return Optional.of(layer);
		}

		return Optional.empty();
	}

	/**
	 * Forgets every layer and data source and closes the connections the data
	 * sources hold, so a cleared registry leaves nothing open behind it. The
	 * registry is empty afterwards even when a connection fails to close; the
	 * first such failure is rethrown once every connection has been tried.
	 */
	public synchronized void clear() {
		List<InsistenceLayerDataSource> toClose = new ArrayList<>(dataSources.values());
		registry.clear();
		dataSources.clear();
		closeAll(toClose);
	}

	private static void closeAll(List<InsistenceLayerDataSource> dataSources) {
		RuntimeException first = null;
		for (InsistenceLayerDataSource dataSource : dataSources) {
			try {
				dataSource.getConnectionWrapper().close();
			} catch (RuntimeException e) {
				if (first == null) first = e;
				else first.addSuppressed(e);
			}
		}
		if (first != null) throw first;
	}

}
