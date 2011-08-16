package org.simulatest.environment.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;


import org.junit.Before;
import org.junit.Test;
import org.simulatest.environment.tree.Tree;

public class TreeTest {
	
	private Tree<String> tree;
	
	@Before
	public void setup() {
		tree = new Tree<String>("chuck");
		tree.addChild("chuck", "jose");
		tree.addChild("chuck", "silvia");
		tree.addChild("chuck", "maria");
		tree.addChild("chuck", "raimundo");
		tree.addChild("silvia", "antonio");
		tree.addChild("silvia", "josefa");
		tree.addChild("maria", "jorge");
	}
	
	@Test
	public void testGetRootValue() {
		assertEquals("chuck", tree.getRootValue());
	}

	@Test
	public void testSizeOfTree() {
		assertEquals(8, tree.size());
	}
	
	@Test
	public void testExceptionWhenAddWithUnavailableParent() {
		try {
			tree.addChild("leonidas", "goro");
			fail();
		} catch (Exception e) {
			assertEquals("The parent \"leonidas\" doesn't exists in Tree", e.getMessage());
		}
	}
	
	@Test
	public void testExceptionWhenAddAlreadyExistentValue() {
		try {
			tree.addChild("maria", "jose");
			fail();
		} catch (IllegalArgumentException e) {
			assertEquals("The value \"jose\" already exists in Tree", e.getMessage());
		}
	}
	
	@Test
	public void testContains() {
		assertTrue(tree.contains("chuck"));
		assertTrue(tree.contains("jose"));
		assertTrue(tree.contains("silvia"));
		assertTrue(tree.contains("maria"));
		assertTrue(tree.contains("raimundo"));
		assertTrue(tree.contains("antonio"));
		assertTrue(tree.contains("josefa"));
		assertTrue(tree.contains("jorge"));
		assertFalse(tree.contains("leonidas"));
		assertFalse(tree.contains("goro"));
		assertFalse(tree.contains("mobi"));
	}
	
	@Test
	public void testGetValuesOfFather() {
		List<String> filhosDeChuck = tree.getChildren("chuck");
		
		assertNotNull(filhosDeChuck);
		assertEquals(4, filhosDeChuck.size());
		assertTrue(filhosDeChuck.contains("jose"));
		assertTrue(filhosDeChuck.contains("silvia"));
		assertTrue(filhosDeChuck.contains("maria"));
		assertTrue(filhosDeChuck.contains("raimundo"));
		
		List<String> filhosDeSilvia = tree.getChildren("silvia");
		
		assertNotNull(filhosDeSilvia);
		assertEquals(2, filhosDeSilvia.size());
		assertTrue(filhosDeSilvia.contains("antonio"));
		assertTrue(filhosDeSilvia.contains("josefa"));
		
		List<String> filhosDeMaria = tree.getChildren("maria");
		
		assertNotNull(filhosDeMaria);
		assertEquals(1, filhosDeMaria.size());
		assertTrue(filhosDeMaria.contains("jorge"));
	}
	
	@Test
	public void testGetChildreWithAnUnexistParent() {
		try {
			tree.getChildren("leonidas");
		} catch (IllegalArgumentException e) {
			assertEquals("The parent \"leonidas\" doesn't exists in Tree", e.getMessage());
		}
	}

	@Test
	public void testPrint() {
		String printEsperado =
			"-chuck\n" +
			"   -jose\n" +
			"   -silvia\n" +
			"      -antonio\n" +
			"      -josefa\n" +
			"   -maria\n" +
			"      -jorge\n" +
			"   -raimundo\n";
		
		assertEquals(printEsperado, tree.print());
	}
	
	@Test
	public void testGetValues() {
		List<String> expectedItens = new ArrayList<String>();
		expectedItens.add("chuck");
		expectedItens.add("jose");
		expectedItens.add("silvia");
		expectedItens.add("antonio");
		expectedItens.add("josefa");
		expectedItens.add("maria");
		expectedItens.add("jorge");
		expectedItens.add("raimundo");
		
		List<String> itens = tree.getValues();
		
		assertNotNull(itens);
		assertEquals(expectedItens,itens);
	}
	
}