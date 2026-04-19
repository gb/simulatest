package org.simulatest.environment.tree;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public final class TreeDepthFirstIterator<T> implements Iterator<Node<T>> {

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
		ListIterator<Node<T>> reverse = children.listIterator(children.size());
		while (reverse.hasPrevious()) {
			stack.push(reverse.previous());
		}
		return next;
	}

}
