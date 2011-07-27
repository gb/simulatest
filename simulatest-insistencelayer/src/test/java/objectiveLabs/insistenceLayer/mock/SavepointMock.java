package objectiveLabs.insistenceLayer.mock;

import java.sql.SQLException;
import java.sql.Savepoint;

public class SavepointMock implements Savepoint {

	private int id;
	private String name;

	public SavepointMock(int id, String name) {
		this.id = id;
		this.name = name;
	}

	@Override
	public int getSavepointId() throws SQLException {
		return id;
	}

	@Override
	public String getSavepointName() throws SQLException {
		return name;
	}
	
}