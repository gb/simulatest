package org.simulatest.jee5runner.environment.example.mock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.ejb.Stateless;

@Stateless
public class DatabaseMockImpl implements DatabaseMock {

	private List<String> messages = new ArrayList<String>();

	public void addMessage(String message) {
		messages.add(message);
	}

	public List<String> getMessages() {
		return Collections.unmodifiableList(messages);
	}

}