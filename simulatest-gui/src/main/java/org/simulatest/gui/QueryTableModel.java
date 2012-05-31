package org.simulatest.gui;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import org.simulatest.insistencelayer.connection.ConnectionFactory;

class QueryTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 2437918897585160122L;

	private Vector<Object> cache;
	private int colCount;
	private String[] headers;
	private Connection db;
	private Statement statement;

	public QueryTableModel() {
		cache = new Vector<Object>();
		
		try {
			db = ConnectionFactory.getConnection();
			statement = db.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
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
			cache = new Vector<Object>(); // blank it out and keep going.
			e.printStackTrace();
		}
	}

	public void initDB(String url) {
		try {
			db = DriverManager.getConnection(url);
			statement = db.createStatement();
		} catch (Exception e) {
			System.out.println("Could not initialize the database.");
			e.printStackTrace();
		}
	}

	public void closeDB() {
		try {
			if (statement != null) statement.close();
			if (db != null) db.close();
		} catch (Exception e) {
			System.out.println("Could not close the current connection.");
			e.printStackTrace();
		}
	}
	
}
