package org.simulatest.guice.test.testdouble.mock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DatabaseMock {

	private final List<String> messages = new ArrayList<>();

	public void addMessage(String message) {
		messages.add(message);
	}

	public List<String> getMessages() {
		return Collections.unmodifiableList(messages);
	}

}
