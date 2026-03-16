package org.simulatest.gui;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.SQLException;

import org.fest.swing.fixture.FrameFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.simulatest.insistencelayer.InsistenceLayer;

public class InsistenceLayerFormGUITest {

	private FrameFixture window;
	private InsistenceLayer insistenceLayer;

	@Before
	public void setUp() throws Exception {
		insistenceLayer = mock(InsistenceLayer.class);
		InsistenceLayerForm insistenceLayerForm = new InsistenceLayerForm(insistenceLayer);
		window = new FrameFixture(insistenceLayerForm);
		insistenceLayerForm.showMe();
	}

	@After
	public void tearDown() {
		window.cleanUp();
	}

	@Test
	public void testIncreaseButton() throws SQLException {
		window.button("+").click();
		verify(insistenceLayer, times(1)).increaseLevel();
	}
	
	@Test
	public void testIncreaseButtonAndUpdateCurrentLevel() throws SQLException {
		assertEquals("0", window.textBox().text());
		
		when(insistenceLayer.getCurrentLevel()).thenReturn(1);
		window.button("+").click();
		assertEquals("1", window.textBox().text());
	}

	@Test
	public void testDecreaseButton() throws SQLException {
		assertEquals("0", window.textBox().text());
		window.button("-").click();
		verify(insistenceLayer, times(1)).decreaseLevel();
	}
	
	@Test
	public void testDecreaseButtonAndNotUpdateCurrentLevel() throws SQLException {
		when(insistenceLayer.getCurrentLevel()).thenReturn(1);
		window.button("+").click();
		assertEquals("1", window.textBox().text());
		
		when(insistenceLayer.getCurrentLevel()).thenReturn(0);
		window.button("-").click();
		assertEquals("0", window.textBox().text());
	}
	
	@Test
	public void testDecreaseButtonAndNotUpdateCurrentLevelWhenCurrentAlreadyIsZero() throws SQLException {
		assertEquals("0", window.textBox().text());
		
		window.button("-").click();
		verify(insistenceLayer, times(1)).decreaseLevel();
		
		assertEquals("0", window.textBox().text());
	}
	
	@Test
	public void testClearCurrentLevel() throws SQLException {
		window.button("clear").click();
		verify(insistenceLayer, times(1)).resetCurrentLevel();
	}
	
	@Test
	public void testResetAllLevels() throws SQLException {
		window.button("reset").click();
		verify(insistenceLayer, times(1)).decreaseAllLevels();
	}

}