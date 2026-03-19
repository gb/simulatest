package org.simulatest.insistencelayer.debug.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.CountDownLatch;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class SQLWindow extends JFrame {

	private static final long serialVersionUID = 7010069887814700821L;

	private final JTextField queryField;
	private final JLabel statusLabel;
	private final SQLTableModel tableModel;

	public SQLWindow() {
		super("Simulatest SQL Window");

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(700, 450);
		setLocationRelativeTo(null);

		tableModel = new SQLTableModel();

		JTable table = new JTable(tableModel);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		table.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		JScrollPane scrollPane = new JScrollPane(table);

		queryField = new JTextField();
		queryField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
		queryField.addActionListener(e -> executeQuery());

		JButton executeButton = new JButton("Execute");
		executeButton.addActionListener(e -> executeQuery());

		JPanel inputPanel = new JPanel(new BorderLayout(6, 0));
		inputPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 4, 8));
		inputPanel.add(new JLabel("SQL:"), BorderLayout.WEST);
		inputPanel.add(queryField, BorderLayout.CENTER);
		inputPanel.add(executeButton, BorderLayout.EAST);

		statusLabel = new JLabel(" ");
		statusLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));

		JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		statusPanel.setBorder(BorderFactory.createEmptyBorder(2, 8, 4, 8));
		statusPanel.add(statusLabel);

		getContentPane().add(inputPanel, BorderLayout.NORTH);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		getContentPane().add(statusPanel, BorderLayout.SOUTH);

		queryField.requestFocusInWindow();
	}

	private void executeQuery() {
		String sql = queryField.getText().trim();
		if (sql.isEmpty()) return;

		String result = tableModel.executeQuery(sql);
		statusLabel.setText(result);
	}

	public static void debug() {
		CountDownLatch latch = new CountDownLatch(1);

		SwingUtilities.invokeLater(() -> {
			SQLWindow window = new SQLWindow();
			window.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosed(WindowEvent e) {
					latch.countDown();
				}
			});
			window.setVisible(true);
		});

		try {
			latch.await();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	public void showMe() {
		this.setVisible(true);
	}

}
