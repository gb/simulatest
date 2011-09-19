package org.simulatest.insistencelayer.gui;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;

import org.fest.swing.fixture.FrameFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.simulatest.insistencelayer.InsistenceLayerManager;

public class InsistenceLayerFormGUITest {

	private FrameFixture window;
	private InsistenceLayerManager insistenceLayerManager;

	@Before
	public void setUp() throws Exception {
		insistenceLayerManager = Mockito.mock(InsistenceLayerManager.class);
		window = new FrameFixture(new InsistenceLayerForm(insistenceLayerManager));
	}

	@After
	public void tearDown() {
		window.cleanUp();
	}

	@Test
	public void testIncreaseButton() throws SQLException {
		window.button("+").click();
		Mockito.verify(insistenceLayerManager, Mockito.times(1)).increaseLevel();
	}
	
	@Test
	public void testIncreaseButtonAndUpdateCurrentLevel() throws SQLException {
		assertEquals("0", window.textBox().text());
		
		Mockito.when(insistenceLayerManager.getCurrentLevel()).thenReturn(1);
		window.button("+").click();
		assertEquals("1", window.textBox().text());
	}

	@Test
	public void testDecreaseButton() throws SQLException {
		assertEquals("0", window.textBox().text());
		window.button("-").click();
		Mockito.verify(insistenceLayerManager, Mockito.times(1)).decreaseLevel();
	}
	
	@Test
	public void testDecreaseButtonAndNotUpdateCurrentLevel() throws SQLException {
		Mockito.when(insistenceLayerManager.getCurrentLevel()).thenReturn(1);
		window.button("+").click();
		assertEquals("1", window.textBox().text());
		
		Mockito.when(insistenceLayerManager.getCurrentLevel()).thenReturn(0);
		window.button("-").click();
		assertEquals("0", window.textBox().text());
	}
	
	@Test
	public void testDecreaseButtonAndNotUpdateCurrentLevelWhenCurrentAlreadyIsZero() throws SQLException {
		assertEquals("0", window.textBox().text());
		
		window.button("-").click();
		Mockito.verify(insistenceLayerManager, Mockito.times(1)).decreaseLevel();
		
		assertEquals("0", window.textBox().text());
	}
	
	@Test
	public void testClearCurrentLevel() throws SQLException {
		window.button("clear").click();
		Mockito.verify(insistenceLayerManager, Mockito.times(1)).resetCurrentLevel();
	}
	
	@Test
	public void testResetAllLevels() throws SQLException {
		window.button("reset").click();
		Mockito.verify(insistenceLayerManager, Mockito.times(1)).decreaseAllLevels();
	}

}