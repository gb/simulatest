package org.simulatest.insistencelayer.connection;

import java.io.Serializable;

public class ConnectionBean implements Serializable {

	private static final long serialVersionUID = -6376606668444102131L;

	private String driver;
	private String url;
	private String username;
	private String password;
	
	public ConnectionBean() {
	}

	public ConnectionBean(String driver, String url, String username, String password) {
		this.driver = driver;
		this.url = url;
		this.username = username;
		this.password = password;
	}
	
	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getDriver() {
		return driver;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

}
