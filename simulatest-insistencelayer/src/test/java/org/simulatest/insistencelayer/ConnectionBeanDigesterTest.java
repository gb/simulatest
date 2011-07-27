package org.simulatest.insistencelayer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static junit.framework.Assert.*;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.simulatest.insistencelayer.infra.ConnectionBean;
import org.simulatest.insistencelayer.infra.ConnectionBeanDigester;

public class ConnectionBeanDigesterTest {
	
	@Rule
    public TemporaryFolder folder = new TemporaryFolder();
	
	private File insistenceLayerFileConfig;
	private ConnectionBeanDigester connectionDiggester;

    @Before
    public void createTestData() throws IOException {
    	insistenceLayerFileConfig = folder.newFile("insistenceLayer.cfg.xml.mock");
    	
        BufferedWriter out = new BufferedWriter(new FileWriter(insistenceLayerFileConfig));
        
        out.write("<?xml version=\"1.0\"?>\n");
        out.write("<datasource>\n");
        out.write("\t<driver>oracle.jdbc.driver.OracleDriver</driver>\n");
        out.write("\t<url>jdbc:oracle:thin:@localhost:1521:gb</url>\n");
        out.write("\t<username>gb</username>\n");
        out.write("\t<password>******</password>\n");
        out.write("</datasource>");
        
        out.close();
    }
	
	@Before
	public void setup() {
		connectionDiggester = new ConnectionBeanDigester();
	}
	
	@Test
	public void digesterDefaultTest() throws Exception {
		String expectedDriver = "oracle.jdbc.driver.OracleDriver";
		String expectedURL = "jdbc:oracle:thin:@localhost:1521:gb";
		String expectedUserName = "gb";
		String expectedPassword = "******";
		
		ConnectionBean connection = connectionDiggester.digesterByFile(insistenceLayerFileConfig);
		
		assertEquals(expectedDriver, connection.getDriver());
		assertEquals(expectedURL, connection.getUrl());
		assertEquals(expectedUserName, connection.getUsername());
		assertEquals(expectedPassword, connection.getPassword());
	}

}