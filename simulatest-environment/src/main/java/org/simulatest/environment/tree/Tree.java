package org.simulatest.environment.tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;


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
		List<T> values = new ArrayList<T>();
		for (Node<T> node : this) values.add(node.getValue());
		return values;
	}

	public int size() {
		return nodesByValue.size();
	}

	public T addChild(T parent, T child) {
		if (!contains(parent)) throw new IllegalArgumentException(String.format("The parent \"%s\" doesn't exists in Tree", parent));
		if (contains(child)) throw new IllegalArgumentException(String.format("The value \"%s\" already exists in Tree", child));

		Node<T> parentNode = getNode(parent);
		Node<T> childNode = new Node<T>(child);
		
		parentNode.addChild(childNode);
		nodesByValue.put(child, childNode);
		
		return child;
	}
	
	public List<T> getChildren(T parent) {
		if (!contains(parent)) throw new IllegalArgumentException(String.format("The parent \"%s\" doesn't exists in Tree", parent));
		
		List<T> children = new ArrayList<T>();
		for (Node<T> childNode : getNode(parent).getChildren()) children.add(childNode.getValue());		
		
		return children;
	}
	
	public boolean contains(T valor) {
		return nodesByValue.containsKey(valor);
	}
	
	private Node<T> getNode(T value) {
		return nodesByValue.get(value);
	}

	@Override
	public Iterator<Node<T>> iterator() {
		return new TreeDepthFirstIterator<T>(rootNode);
	}
	
	public String print() {
		StringBuilder builder = new StringBuilder();
		rootNode.print(builder);
		return builder.toString();
	}
	
}