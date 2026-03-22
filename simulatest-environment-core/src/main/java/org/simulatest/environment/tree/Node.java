package org.simulatest.environment.tree;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Node<T> {
	
	private final T value;
	private Node<T> parent;
	private final LinkedList<Node<T>> children;
	
	public Node(T value) {
		this.value = value;
		this.children = new LinkedList<>();
	}

	void addChild(Node<T> child) {
		child.setParent(this);
		children.add(child);
	}

	public T getValue() {
		return value;
	}
	
	public Node<T> getParent() {
		return parent;
	}

	private void setParent(Node<T> parent) {
		this.parent = parent;
	}

	public boolean hasParent() {
		return parent != null;
	}

	public List<Node<T>> getChildren() {
		return Collections.unmodifiableList(children);
	}
	
	public Node<T> getFirstChild() {
		return children.isEmpty() ? null : children.getFirst();
	}
	
	public Node<T> getLastChild() {
		return children.isEmpty() ? null : children.getLast();
	}
	
	public boolean isLastChild() {
		return hasParent() && isTheLastChildOfParent();
	}
	
	private boolean isTheLastChildOfParent() {
		return this == getParent().getLastChild();
	}
	
	public int size() {
		int size = 1;
		for (Node<T> child : children) size += child.size();
		
		return size;
	}
	
	public void print(StringBuilder builder) {
		print(builder, "");
	}

	private void print(StringBuilder builder, String margin) {
		builder.append(margin).append("-").append(value.toString()).append("\n");
		for (Node<T> child : children) child.print(builder, margin + "   ");
	}
	
	public T getParentValue() {
		return (getParent() != null) ? getParent().getValue() : null; 
	}
	
}
