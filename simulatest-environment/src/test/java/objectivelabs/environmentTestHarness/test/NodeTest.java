package objectivelabs.environmentTestHarness.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import objectiveLabs.environmentTestHarness.tree.Node;

import org.junit.Before;
import org.junit.Test;

public class NodeTest {
	
	private Node<String> jose;
	private Node<String> silvia;
	private Node<String> mario;
	private Node<String> raimundo;
	private Node<String> antonio;
	private Node<String> jozefa;
	
	@Before
	public void createNodes() {
		jose = new Node<String>("jose");
		silvia = new Node<String>("silvia");
		mario = new Node<String>("mario");
		raimundo = new Node<String>("raimundo");
		antonio = new Node<String>("antonio");
		jozefa = new Node<String>("jozefa");
		
		jose.addChild(silvia);
		jose.addChild(mario);
		jose.addChild(raimundo);
		
		mario.addChild(antonio);
		mario.addChild(jozefa);
	}

	@Test
	public void testGetValue() {
		assertEquals("jose", jose.getValue());
	}
	
	@Test
	public void testGetChildren() {
		List<Node<String>> filhosDeJose = jose.getChildren();
		
		assertNotNull(filhosDeJose);
		assertEquals(3, filhosDeJose.size());
		assertTrue(filhosDeJose.contains(silvia));
		assertTrue(filhosDeJose.contains(mario));
		assertTrue(filhosDeJose.contains(raimundo));
	}
	
	@Test
	public void testGetParent() {
		assertNull(jose.getParent());
		
		assertEquals(jose, silvia.getParent());
		assertEquals(jose, mario.getParent());
		assertEquals(jose, raimundo.getParent());
		
		assertEquals(mario, antonio.getParent());
		assertEquals(mario, jozefa.getParent());
	}
	
	@Test
	public void testGetFirstChild() throws InterruptedException {
		assertNull(silvia.getFirstChild());
				
		assertEquals(silvia, jose.getFirstChild());
		assertEquals(antonio, mario.getFirstChild());
	}
	
	@Test
	public void testGetLastChild() {
		assertNull(silvia.getLastChild());
		
		assertEquals(raimundo, jose.getLastChild());
		assertEquals(jozefa, mario.getLastChild());
	}
	
	@Test
	public void testHasParent() {
		assertTrue(silvia.hasParent());
		assertTrue(mario.hasParent());
		assertTrue(raimundo.hasParent());
		
		assertFalse(jose.hasParent());
	}
	
	@Test
	public void testIsLastChild() {
		assertFalse(jose.isLastChild());
		assertFalse(silvia.isLastChild());
		assertFalse(mario.isLastChild());
		assertFalse(antonio.isLastChild());
		
		assertTrue(raimundo.isLastChild());
		assertTrue(jozefa.isLastChild());
	}
	
	@Test
	public void testSize() {
		assertEquals(6, jose.totalQuantityOfNodes());
	}

}