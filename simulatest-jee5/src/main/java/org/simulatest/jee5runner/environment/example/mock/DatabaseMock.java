package org.simulatest.jee5runner.environment.example.mock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DatabaseMock {
	
	private static List<String> messages;

	static {
		reseta();
	}

	public static void reseta() {
		messages = new ArrayList<String>();
	}
	
	public static void addMessage(String message) {
		messages.add(message);
	}
	
	public static List<String> getMessages() {
		return Collections.unmodifiableList(messages);
	}
	
}