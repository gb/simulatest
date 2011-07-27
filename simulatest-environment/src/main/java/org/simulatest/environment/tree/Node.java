package org.simulatest.environment.tree;

import java.util.LinkedList;

public class Node<T> {
	
	private T value;
	private Node<T> parent;
	private LinkedList<Node<T>> children;
	
	public Node(T value) {
		this.value = value;
		this.children = new LinkedList<Node<T>>();
	}

	public void addChild(Node<T> child) {
		if (children.contains(child)) return;
		
		child.setParent(this);
		children.add(child);
	}

	public T getValue() {
		return value;
	}
	
	public Node<T> getParent() {
		return parent;
	}

	protected void setParent(Node<T> parent) {
		this.parent = parent;
	}

	public boolean hasParent() {
		return parent != null;
	}

	public LinkedList<Node<T>> getChildren() {
		return children;
	}
	
	public Node<T> getFirstChild() {
		return children.isEmpty() ? null : children.getFirst();
	}
	
	public Node<T> getLastChild() {
		return children.isEmpty() ? null : children.getLast();
	}
	
	public boolean isLastChild() {
		return hasParent() ? isTheLastChildOfParent() : false;
	}
	
	private boolean isTheLastChildOfParent() {
		return this.equals(getParent().getLastChild());
	}
	
	public int totalQuantityOfNodes() {
		int size = 1;
		for (Node<T> child : children) size += child.totalQuantityOfNodes();
		
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
