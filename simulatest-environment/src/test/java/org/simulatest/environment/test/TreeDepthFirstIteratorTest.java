package org.simulatest.environment.test;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.simulatest.environment.tree.Node;
import org.simulatest.environment.tree.Tree;


public class TreeDepthFirstIteratorTest {
		
		Tree<Integer> tree = new Tree<Integer>(1);
		
		@Before
		public void createTree() {			
			tree.addChild(1, 2);
			
			tree.addChild(2, 3);
			tree.addChild(2, 4);
			
			tree.addChild(1, 5);
			
			tree.addChild(5, 6);
			tree.addChild(5, 7);
		}

		@Test
		public void iteratesThroughTheTree() {
			Integer expectedValue = 1;
			for (Node<Integer> node : tree) {
				assertEquals(expectedValue, node.getValue());
				expectedValue++;
			}
		}
}
