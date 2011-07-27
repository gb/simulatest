package objectiveLabs.environmentTestHarness.tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.base.Preconditions;

public class Tree<T> implements Iterable<Node<T>> {
	
	private Map<T, Node<T>> nodesByValue;
	private Node<T> rootNode;

	public Tree(T rootValue) { 
		Preconditions.checkNotNull(rootValue, "The root can't be a null value");
		
		nodesByValue = new HashMap<T, Node<T>>();
		rootNode = new Node<T>(rootValue);
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
		Preconditions.checkArgument(contains(parent), "The parent \"%s\" doesn't exists in Tree", parent);
		Preconditions.checkArgument(!contains(child), "The value \"%s\" already exists in Tree", child);

		Node<T> parentNode = getNode(parent);
		Node<T> childNode = new Node<T>(child);
		
		parentNode.addChild(childNode);
		nodesByValue.put(child, childNode);
		
		return child;
	}
	
	public List<T> getChildren(T parent) {
		Preconditions.checkArgument(contains(parent), "The parent \"%s\" doesn't exists in Tree", parent);
		
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