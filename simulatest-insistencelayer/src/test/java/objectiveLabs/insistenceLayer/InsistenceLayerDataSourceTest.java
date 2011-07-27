package objectiveLabs.insistenceLayer;

import static org.junit.Assert.*;

import java.sql.SQLException;

import objectiveLabs.insistenceLayer.infra.InsistenceLayerDataSource;

import org.junit.Test;

public class InsistenceLayerDataSourceTest {
	
	private InsistenceLayerDataSource insistenceLayerDataSource;
	
	@Test
	public void shouldNotBePossibleCreateAnInstanceWithANullConnection() throws SQLException {
		try {
			insistenceLayerDataSource = new InsistenceLayerDataSource(null);
			fail("was possible create an instance of InsistenceLayerDataSource with a null Connection!");
		} catch (RuntimeException e) {
			assertEquals("ConnectionBean is null", e.getMessage());
		}
	}
	
	@Test
	public void shouldGetANonNullableConnection() throws SQLException {
		insistenceLayerDataSource = new InsistenceLayerDataSource();
		
		assertNotNull(insistenceLayerDataSource.getConnection());
	}

}