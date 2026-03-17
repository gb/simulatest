package org.simulatest.insistencelayer.gui;

import java.io.Serial;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

import org.simulatest.insistencelayer.InsistenceLayerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SQLTableModel extends AbstractTableModel {

	@Serial
	private static final long serialVersionUID = 2437918897585160122L;
	private static final Logger logger = LoggerFactory.getLogger(SQLTableModel.class);

	private List<String[]> rows = new ArrayList<>();
	private String[] headers = new String[0];
	private final Statement statement;

	SQLTableModel() {
		try {
			Connection connection = InsistenceLayerFactory.requireDataSource().getConnection();
			statement = connection.createStatement();
		} catch (SQLException e) {
			throw new IllegalStateException("Failed to obtain connection", e);
		}
	}

	String executeQuery(String sql) {
		rows = new ArrayList<>();
		headers = new String[0];

		try {
			if (statement.execute(sql)) {
				ResultSet rs = statement.getResultSet();
				ResultSetMetaData meta = rs.getMetaData();
				int colCount = meta.getColumnCount();

				headers = new String[colCount];
				for (int i = 1; i <= colCount; i++) {
					headers[i - 1] = meta.getColumnName(i);
				}

				while (rs.next()) {
					String[] record = new String[colCount];
					for (int i = 0; i < colCount; i++) {
						record[i] = rs.getString(i + 1);
					}
					rows.add(record);
				}

				fireTableStructureChanged();
				return rows.size() + " row" + (rows.size() != 1 ? "s" : "");
			} else {
				fireTableStructureChanged();
				return "Updated " + statement.getUpdateCount() + " row(s)";
			}
		} catch (SQLException e) {
			logger.error("Query execution failed", e);
			fireTableStructureChanged();
			return "ERROR: " + e.getMessage();
		}
	}

	@Override
	public String getColumnName(int index) {
		return headers[index];
	}

	@Override
	public int getColumnCount() {
		return headers.length;
	}

	@Override
	public int getRowCount() {
		return rows.size();
	}

	@Override
	public Object getValueAt(int row, int col) {
		return rows.get(row)[col];
	}

}
