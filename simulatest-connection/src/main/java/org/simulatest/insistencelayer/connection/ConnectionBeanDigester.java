package org.simulatest.insistencelayer.connection;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ConnectionBeanDigester {

	public ConnectionBean digesterDefault() {
		return digesterByStream(getClass().getClassLoader().getResourceAsStream("insistenceLayer.cfg.xml"));
	}

	public ConnectionBean digesterByStream(InputStream stream) {
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.parse(stream);
			Element root = doc.getDocumentElement();

			String driver = getTextContent(root, "driver");
			String url = getTextContent(root, "url");
			String username = getTextContent(root, "username");
			String password = getTextContent(root, "password");

			return new ConnectionBean(driver, url, username, password);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public ConnectionBean digesterByFile(File file) {
		try {
			return digesterByStream(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	private String getTextContent(Element parent, String tagName) {
		return parent.getElementsByTagName(tagName).item(0).getTextContent().trim();
	}

}
