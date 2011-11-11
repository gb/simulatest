package org.simulatest.environment.test.testdouble;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DatabaseMock {
	
	private static List<String> messages = new ArrayList<String>();
	
	public static void addMessage(String message) {
		messages.add(message);
	}

	public static List<String> getMessages() {
		return Collections.unmodifiableList(messages);
	}
	
	public void reset() {
		messages = new ArrayList<String>();
	}

}
