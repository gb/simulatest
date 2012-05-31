package org.simulatest.environment.tree;

import java.util.Iterator;
import java.util.LinkedList;

public class TreeDepthFirstIterator<T> implements Iterator<Node<T>> {

	private LinkedList<Node<T>> stack;

	public TreeDepthFirstIterator(Node<T> rootNode) {
		stack = new LinkedList<Node<T>>();
		stack.add(rootNode);
	}

	@Override
	public boolean hasNext() {
		return !stack.isEmpty();
	}

	@Override
	public Node<T> next() {
		Node<T> next = stack.pop();
		stack.addAll(0, next.getChildren());

		return next;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}