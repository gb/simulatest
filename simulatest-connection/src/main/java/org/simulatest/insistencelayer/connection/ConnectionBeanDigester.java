package org.simulatest.insistencelayer.connection;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;

public class ConnectionBeanDigester {
	
	public ConnectionBean digesterDefault() {
		return digesterByStream(getClass().getClassLoader().getResourceAsStream("insistenceLayer.cfg.xml"));
	}

	public ConnectionBean digesterByStream(InputStream stream) {
		return tryDigesterStream(getDigester(), stream);
	}
	
	public ConnectionBean digesterByFile(File file) {
		return tryDigesterFile(getDigester(), file);
	}
	
	private Digester getDigester() {
		Digester digester = new Digester();
		
		digester.addObjectCreate("datasource", ConnectionBean.class);
		digester.addCallMethod("datasource/driver", "setDriver", 0);
		digester.addCallMethod("datasource/url", "setUrl", 0);
		digester.addCallMethod("datasource/username", "setUsername", 0);
		digester.addCallMethod("datasource/password", "setPassword", 0);
		
		return digester;
	}
	
	private ConnectionBean tryDigesterFile(Digester digester, File file) {
		try {
			return tryDigesterStream(digester, new FileInputStream(file));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
	private ConnectionBean tryDigesterStream(Digester digester, InputStream stream) {
		try {
			return (ConnectionBean) digester.parse(stream);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (SAXException e) {
			throw new RuntimeException(e);
		} 
	}
	
}