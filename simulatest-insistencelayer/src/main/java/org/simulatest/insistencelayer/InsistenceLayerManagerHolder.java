package org.simulatest.insistencelayer;

public class InsistenceLayerManagerHolder {

	private static InsistenceLayerManager instance;

	public static void set(InsistenceLayerManager manager) {
		instance = manager;
	}

	public static InsistenceLayerManager get() {
		return instance;
	}

	public static void clear() {
		instance = null;
	}

}
