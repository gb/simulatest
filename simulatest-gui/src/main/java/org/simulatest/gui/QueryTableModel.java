package org.simulatest.gui;

import java.io.Serial;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import org.simulatest.insistencelayer.InsistenceLayerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class QueryTableModel extends AbstractTableModel {

	@Serial
	private static final long serialVersionUID = 2437918897585160122L;
	private static final Logger logger = LoggerFactory.getLogger(QueryTableModel.class);

	private Vector<Object> cache;
	private int colCount;
	private String[] headers;
	private Connection db;
	private Statement statement;

	public QueryTableModel() {
		cache = new Vector<Object>();
		
		try {
			db = InsistenceLayerFactory.dataSource().getConnection();
			statement = db.createStatement();
		} catch (SQLException e) {
			logger.error("Failed to obtain connection", e);
		}
	}

	public String getColumnName(int index) {
		return headers[index];
	}

	public int getColumnCount() {
		return colCount;
	}

	public int getRowCount() {
		return cache.size();
	}

	public Object getValueAt(int row, int col) {
		return ((String[]) cache.elementAt(row))[col];
	}

	public void setQuery(String q) {
		cache = new Vector<Object>();
		try {
			ResultSet rs = statement.executeQuery(q);
			ResultSetMetaData meta = rs.getMetaData();
			colCount = meta.getColumnCount();

			headers = new String[colCount];
			for (int h = 1; h <= colCount; h++) {
				headers[h - 1] = meta.getColumnName(h);
			}

			while (rs.next()) {
				String[] record = new String[colCount];

				for (int i = 0; i < colCount; i++) 
					record[i] = rs.getString(i + 1);
				
				cache.addElement(record);
			}
			
			fireTableChanged(null);
		} catch (Exception e) {
			logger.error("Query execution failed", e);
		}
	}

	public void initDB(String url) {
		try {
			db = DriverManager.getConnection(url);
			statement = db.createStatement();
		} catch (Exception e) {
			logger.error("Could not initialize the database", e);
		}
	}

	public void closeDB() {
		try {
			if (statement != null) statement.close();
			if (db != null) db.close();
		} catch (Exception e) {
			logger.error("Could not close the current connection", e);
		}
	}
	
}
