package org.simulatest.environment.tree;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
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
		List<Node<T>> children = next.getChildren();
		ListIterator<Node<T>> it = children.listIterator(children.size());
		while (it.hasPrevious()) {
			stack.push(it.previous());
		}
		return next;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
