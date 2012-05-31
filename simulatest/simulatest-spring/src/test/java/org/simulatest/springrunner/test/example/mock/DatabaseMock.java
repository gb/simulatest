package org.simulatest.springrunner.test.example.mock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class DatabaseMock {
	
	private List<String> messages = new ArrayList<String>();

	public void addMessage(String message) {
		messages.add(message);
	}

	public List<String> getMessages() {
		return Collections.unmodifiableList(messages);
	}

}