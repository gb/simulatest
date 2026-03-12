package org.simulatest.environment.tree;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

public class TreeBreadthFirstIterator<T> implements Iterator<Node<T>> {

	private LinkedList<Node<T>> queue;

	public TreeBreadthFirstIterator(Node<T> rootNode) {
		queue = new LinkedList<Node<T>>();
		queue.add(rootNode);
	}

	@Override
	public boolean hasNext() {
		return !queue.isEmpty();
	}

	@Override
	public Node<T> next() {
		if (!hasNext()) throw new NoSuchElementException();
		Node<T> next = queue.pop();
		queue.addAll(next.getChildren());
		return next;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}