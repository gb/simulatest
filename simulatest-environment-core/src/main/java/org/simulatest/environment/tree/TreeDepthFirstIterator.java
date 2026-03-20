package org.simulatest.environment.tree;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class TreeDepthFirstIterator<T> implements Iterator<Node<T>> {

	private final Deque<Node<T>> stack;

	public TreeDepthFirstIterator(Node<T> rootNode) {
		stack = new ArrayDeque<>();
		stack.push(rootNode);
	}

	@Override
	public boolean hasNext() {
		return !stack.isEmpty();
	}

	@Override
	public Node<T> next() {
		if (!hasNext()) throw new NoSuchElementException();
		Node<T> next = stack.pop();
		for (Node<T> child : next.getChildren().reversed()) {
			stack.push(child);
		}
		return next;
	}

}
