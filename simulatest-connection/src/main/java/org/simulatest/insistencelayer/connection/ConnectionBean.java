package org.simulatest.insistencelayer.connection;

import java.io.Serializable;

public class ConnectionBean implements Serializable {

	private static final long serialVersionUID = -6376606668444102131L;

	private final String driver;
	private final String url;
	private final String username;
	private final String password;

	public ConnectionBean(String driver, String url, String username, String password) {
		this.driver = driver;
		this.url = url;
		this.username = username;
		this.password = password;
	}

	public String getDriver() {
		return driver;
	}

	public String getUrl() {
		return url;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

}