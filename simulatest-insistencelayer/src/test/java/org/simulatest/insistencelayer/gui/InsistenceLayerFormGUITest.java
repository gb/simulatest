package org.simulatest.insistencelayer.gui;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;
import org.uispec4j.UISpecTestCase;
import org.uispec4j.Window;
import org.uispec4j.interception.MainClassAdapter;

public class InsistenceLayerFormGUITest extends UISpecTestCase {

	@Before
	protected void setUp() throws Exception {
		setAdapter(new MainClassAdapter(InsistenceLayerForm.class, new String[0]));
	}

	@Test
	public void testIncreaseButton() throws SQLException {
		Window window = getMainWindow();
		
		assertEquals("0", window.getInputTextBox().getText());
		window.getButton("+").click();
		assertEquals("1", window.getInputTextBox().getText());
		window.getButton("+").click();
		assertEquals("2", window.getInputTextBox().getText());
	}
	
	@Test
	public void testDecreaseButton() throws SQLException {
		Window window = getMainWindow();
		
		window.getButton("+").click();
		window.getButton("+").click();
		window.getButton("+").click();
		assertEquals("3", window.getInputTextBox().getText());
		
		window.getButton("-").click();
		assertEquals("2", window.getInputTextBox().getText());
		window.getButton("-").click();
		assertEquals("1", window.getInputTextBox().getText());
		window.getButton("-").click();
		assertEquals("0", window.getInputTextBox().getText());
		
		window.getButton("-").click();
		window.getButton("-").click();
		window.getButton("-").click();
		assertEquals("0", window.getInputTextBox().getText());
	}

}