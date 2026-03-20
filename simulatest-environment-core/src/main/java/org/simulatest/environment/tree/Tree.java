package org.simulatest.environment.tree;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.StreamSupport;


public class Tree<T> implements Iterable<Node<T>> {
	
	private final Map<T, Node<T>> nodesByValue;
	private final Node<T> rootNode;

	public Tree(T rootValue) {
		Objects.requireNonNull(rootValue, "The root can't be a null value");
		
		nodesByValue = new HashMap<>();
		rootNode = new Node<>(rootValue);
		nodesByValue.put(rootValue, rootNode);
	}
	
	public T getRootValue() {
		return rootNode.getValue();
	}

	public List<T> getValues() {
		return StreamSupport.stream(spliterator(), false)
				.map(Node::getValue)
				.toList();
	}

	public int size() {
		return nodesByValue.size();
	}

	public T addChild(T parent, T child) {
		if (!contains(parent)) throw new IllegalArgumentException(String.format("The parent \"%s\" doesn't exist in Tree", parent));
		if (contains(child)) throw new IllegalArgumentException(String.format("The value \"%s\" already exists in Tree", child));

		Node<T> parentNode = getNode(parent);
		Node<T> childNode = new Node<>(child);
		
		parentNode.addChild(childNode);
		nodesByValue.put(child, childNode);
		
		return child;
	}
	
	public List<T> getChildren(T parent) {
		if (!contains(parent)) throw new IllegalArgumentException(String.format("The parent \"%s\" doesn't exist in Tree", parent));

		return getNode(parent).getChildren().stream()
				.map(Node::getValue)
				.toList();
	}
	
	public boolean contains(T value) {
		return nodesByValue.containsKey(value);
	}
	
	private Node<T> getNode(T value) {
		return nodesByValue.get(value);
	}

	@Override
	public Iterator<Node<T>> iterator() {
		return new TreeDepthFirstIterator<>(rootNode);
	}
	
	public String print() {
		StringBuilder builder = new StringBuilder();
		rootNode.print(builder);
		return builder.toString();
	}
	
}