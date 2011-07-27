package org.simulatest.insistencelayer;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.Stack;


import org.apache.log4j.Logger;
import org.simulatest.insistencelayer.infra.ConnectionWrapper;
import org.simulatest.insistencelayer.infra.InsistenceLayerDataSource;
import org.simulatest.insistencelayer.infra.InsistenceLayerException;

import com.google.common.base.Preconditions;

public class InsistenceLayerManager {

	private static final String PREFIX_SAVEPOINT = "LAYER";
	private static final Logger logger = Logger.getLogger(InsistenceLayerManager.class);
	
	private static InsistenceLayerManager instance;
	private ConnectionWrapper connection;
	private Stack<Savepoint> savepoints;

	public InsistenceLayerManager(Connection connection) {
		Preconditions.checkNotNull(connection, "Connection is null");
		this.connection = new ConnectionWrapper(connection);
		this.savepoints = new Stack<Savepoint>();
	}
	
	public int getCurrentLevel() {
		return savepoints.size();
	}

	public void increaseLevel() {
		setup();
		createSavepoint();
		
		logger.info("[InsistenceLayer] Level increased to " + getCurrentLevel());
	}
	
	private void setup() {
		if (isDisabled()) connection.wrap();
	}

	private void createSavepoint() { 
		String savePointName = PREFIX_SAVEPOINT + (getCurrentLevel() + 1);
		
		try {
			savepoints.add(connection.setSavepoint(savePointName));
		} catch (Exception exception) {
			String message = "Error creating the savepoint: " + savePointName;
			throw new InsistenceLayerException(message, exception);
		}
	}
	
	public void resetCurrentLevel() {
		rollbackSavepoint(savepoints.peek());
	}
	
	public void decreaseLevel() {
		if (isDisabled()) return;
		
		rollbackSavepoint(savepoints.pop());
		tearDown();
		
		logger.info("[InsistenceLayer] Level decreased to " + getCurrentLevel());
	}
	
	private void tearDown() {
		if (isDisabled()) connection.unwrap();
	}
	
	public void decreaseAllLevels() {
		setLevelTo(0);
	}

	public void setLevelTo(int level) {
		Preconditions.checkArgument(level >= 0, "Level cannot be negative");
		logger.info("[InsistenceLayer] Setting level " + getCurrentLevel() + " to " + level);
		
		if (getCurrentLevel() > level) decreaseToLevel(level);
		else if (getCurrentLevel() < level ) increaseToLevel(level);
	}

	private void increaseToLevel(int level) {
		while (getCurrentLevel() < level) increaseLevel();
	}

	private void decreaseToLevel(int level) {
		while (getCurrentLevel() - 1 > level) dropCurrentLevel();
		if (getCurrentLevel() == level + 1) decreaseLevel();
	}
	
	private void dropCurrentLevel() {
		try {
			connection.releaseSavepoint(savepoints.pop());
		} catch (SQLException exception) {
			throw new InsistenceLayerException("Error dropping the current level", exception);
		}
	}
	
	private void rollbackSavepoint(Savepoint savepoint) {
		try {
			connection.rollback(savepoint);
		} catch (SQLException exception) {
			throw new InsistenceLayerException("Error rollbacking the savepoint", exception);
		}
	}
	
	private boolean isDisabled() {
		return getCurrentLevel() == 0;
	}
	
	public static InsistenceLayerManager getInstance() throws SQLException {
		if (instance == null) initializeInstance();
		return instance;
	}
	
	private static void initializeInstance() throws SQLException {
		instance = new InsistenceLayerManager(new InsistenceLayerDataSource().getConnection());
	}
	
}