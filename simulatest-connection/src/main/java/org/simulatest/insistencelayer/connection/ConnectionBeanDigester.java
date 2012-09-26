package org.simulatest.insistencelayer.connection;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.digester3.Digester;
import org.apache.commons.digester3.ObjectCreateRule;
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
		ObjectCreateRule rule = new ObjectCreateRule( ConnectionBean.class );
		rule.setConstructorArgumentTypes(String.class, String.class, String.class, String.class);
		
		Digester digester = new Digester();
		digester.addRule("datasource", rule);
		digester.addCallParam("datasource/driver", 0);
		digester.addCallParam("datasource/url", 1);
		digester.addCallParam("datasource/username", 2);
		digester.addCallParam("datasource/password", 3);
		
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