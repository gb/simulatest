package org.simulatest.jee5runner.environment.example.mock;

import java.util.List;

import javax.ejb.Local;

@Local
public interface DatabaseMock {

	void addMessage(String message);

	List<String> getMessages();

}