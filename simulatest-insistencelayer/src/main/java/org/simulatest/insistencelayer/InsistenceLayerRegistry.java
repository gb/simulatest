package org.simulatest.insistencelayer;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.simulatest.insistencelayer.infra.sql.ConnectionWrapper;
import org.simulatest.insistencelayer.infra.sql.InsistenceLayerDataSource;

/**
 * Holds Insistence Layer instances and their wrapped data sources.
 *
 * <p>Most users interact through the static convenience methods on
 * {@link InsistenceLayerFactory}, which delegate to a shared default
 * registry. When independent registries are needed (parallel test
 * execution, multi-datasource), create a separate instance.</p>
 */
public class InsistenceLayerRegistry {

	public static final String DEFAULT = "default";

	private final Map<String, InsistenceLayer> registry = new LinkedHashMap<>();
	private final Map<String, InsistenceLayerDataSource> dataSources = new LinkedHashMap<>();

	public void configure(DataSource dataSource) {
		var wrapped = new InsistenceLayerDataSource(dataSource);
		dataSources.put(DEFAULT, wrapped);
		registry.put(DEFAULT, InsistenceLayerFactory.build(wrapped.getConnectionWrapper()));
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
		registry.put(name, layer);
	}

	public void deregister(String name) {
		registry.remove(name);
	}

	public InsistenceLayer resolve(String name) {
		return registry.get(name);
	}

	public InsistenceLayer resolve() {
		if (!registry.isEmpty()) {
			return registry.values().iterator().next();
		}

		if (!dataSources.isEmpty()) {
			InsistenceLayerDataSource ds = dataSources.values().iterator().next();
			InsistenceLayer layer = InsistenceLayerFactory.build(ds.getConnectionWrapper());
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
