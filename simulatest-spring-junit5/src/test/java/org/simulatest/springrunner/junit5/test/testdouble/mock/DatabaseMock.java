package org.simulatest.springrunner.junit5.test.testdouble.mock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class DatabaseMock {

	private final List<String> messages = new ArrayList<>();

	public void addMessage(String message) {
		messages.add(message);
	}

	public List<String> getMessages() {
		return Collections.unmodifiableList(messages);
	}

}
