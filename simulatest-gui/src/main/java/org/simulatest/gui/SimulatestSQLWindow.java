package org.simulatest.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

public class SimulatestSQLWindow extends JFrame {

	private static final long serialVersionUID = 7010069887814700821L;

	private JTextField queryField;
	private QueryTableModel qtm;

	public SimulatestSQLWindow() {
		super("Simulatest SQL Window");
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(350, 200);
		setLocationRelativeTo(null);

		qtm = new QueryTableModel();
		JTable table = new JTable(qtm);
		JScrollPane scrollpane = new JScrollPane(table);
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(3, 2));
		panel.add(queryField = new JTextField());
		
		JButton jb = new JButton("Search");
		
		jb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				qtm.setQuery(queryField.getText().trim());
			}
		});
		
		panel.add(jb);
		
		getContentPane().add(panel, BorderLayout.NORTH);
		getContentPane().add(scrollpane, BorderLayout.CENTER);
	}
	
	public void showMe() {
		this.setVisible(true);
	}

	public static void main(String args[]) {
		new SimulatestSQLWindow().showMe();
	}
	
}
