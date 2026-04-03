package org.simulatest.insistencelayer;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import javax.sql.DataSource;

import org.simulatest.insistencelayer.infra.sql.InsistenceLayerDataSource;

/**
 * Holds Insistence Layer instances and their wrapped data sources.
 *
 * <p>Most users interact through the static convenience methods on
 * {@link InsistenceLayerFactory}, which delegate to a shared default
 * registry. When independent registries are needed (parallel test
 * execution, multi-datasource), create a separate instance.</p>
 */
public final class InsistenceLayerRegistry {

	public static final String DEFAULT = "default";

	private final Map<String, InsistenceLayer> registry = new LinkedHashMap<>();
	private final Map<String, InsistenceLayerDataSource> dataSources = new LinkedHashMap<>();

	public void configure(DataSource dataSource) {
		Objects.requireNonNull(dataSource, "dataSource must not be null");
		var wrapped = new InsistenceLayerDataSource(dataSource);
		dataSources.put(DEFAULT, wrapped);
		registry.put(DEFAULT, new LocalInsistenceLayer(wrapped.getConnectionWrapper()));
	}

	public InsistenceLayerDataSource dataSource() {
		return dataSources.isEmpty() ? null : dataSources.values().iterator().next();
	}

	public InsistenceLayerDataSource requireDataSource() {
		InsistenceLayerDataSource ds = dataSource();
		if (ds == null) {
			throw new IllegalStateException("InsistenceLayer not configured - call InsistenceLayerFactory.configure(dataSource) first");
		}
		return ds;
	}

	public boolean isConfigured() {
		return !registry.isEmpty();
	}

	public void register(String name, InsistenceLayer layer) {
		Objects.requireNonNull(name, "name must not be null");
		Objects.requireNonNull(layer, "layer must not be null");
		registry.put(name, layer);
	}

	public void deregister(String name) {
		registry.remove(name);
	}

	public InsistenceLayer resolve(String name) {
		return registry.get(name);
	}

	// Fallback chain: (1) return existing layer, (2) lazily build from configured
	// datasource, (3) return null if nothing is configured.
	public InsistenceLayer resolve() {
		if (!registry.isEmpty()) {
			return registry.values().iterator().next();
		}

		if (!dataSources.isEmpty()) {
			InsistenceLayerDataSource ds = dataSources.values().iterator().next();
			InsistenceLayer layer = new LocalInsistenceLayer(ds.getConnectionWrapper());
			registry.put(DEFAULT, layer);
			return layer;
		}

		return null;
	}

	public void clear() {
		registry.clear();
		dataSources.clear();
	}

}
