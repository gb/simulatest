package org.simulatest.insistencelayer.gui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import org.simulatest.insistencelayer.InsistenceLayerManager;
import org.simulatest.insistencelayer.connection.ConnectionFactory;

public class InsistenceLayerForm extends JFrame {
	
	private static final long serialVersionUID = 1L;

	private InsistenceLayerManager insistenceLayerManager;
	private JTextField tfCurrentLevel = new JTextField();
	private JButton btnIncreaseLevel = new JButton("+");
	private JButton btnDecreaseLevel = new JButton("-");
	private JButton btnReset = new JButton("reset");
	private JButton btnClear = new JButton("clear");
	private JLabel lbCurrentLevel = new JLabel("Current Level");

	public InsistenceLayerForm() throws SQLException {
		insistenceLayerManager = new InsistenceLayerManager(ConnectionFactory.getConnection());
		
		addComponents();
		configureLayout();
		createEvents();
		updateDisplayLevel();
	}

	public static void main(String[] args) throws SQLException {
		new InsistenceLayerForm().setVisible(true);
	}
	
	private void configureLayout() {
		setSize(395, 60);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setResizable(false);
		setTitle("Insistence Layer");
		setLayout(null);
		setLocationRelativeTo(null);
	}
	
	private void addComponents() {
		lbCurrentLevel.setBounds(10, 6, 85, 20);
		
		tfCurrentLevel.setFont(new Font("Tahoma", 1, 11));
		tfCurrentLevel.setEditable(false);
		tfCurrentLevel.setBounds(95, 5, 35, 25);
		tfCurrentLevel.setHorizontalAlignment(JTextField.CENTER);
		
		btnIncreaseLevel.setBounds(145, 5, 45, 25);
		btnDecreaseLevel.setBounds(195, 5, 45, 25);
		btnReset.setBounds(245, 5, 65, 25);
		btnClear.setBounds(315, 5, 65, 25);
		
		btnIncreaseLevel.setToolTipText("Increase Level");
		btnDecreaseLevel.setToolTipText("Decrease Level");
		btnReset.setToolTipText("Reset all levels");
		btnClear.setToolTipText("Clear current level");

		add(lbCurrentLevel);
		add(tfCurrentLevel);
		add(btnIncreaseLevel);
		add(btnDecreaseLevel);
		add(btnReset);
		add(btnClear);
	}
	
	private void updateDisplayLevel() {
		tfCurrentLevel.setText(Integer.toString(insistenceLayerManager.getCurrentLevel()));
	}
	
	private void createEvents() {
		btnIncreaseLevel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				insistenceLayerManager.increaseLevel();
				updateDisplayLevel();
			}
		});
		
		btnDecreaseLevel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				insistenceLayerManager.decreaseLevel();
				updateDisplayLevel();
			}
		});
		
		btnReset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				insistenceLayerManager.decreaseAllLevels();
				updateDisplayLevel();
			}
		});
		
		btnClear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				insistenceLayerManager.resetCurrentLevel();
				updateDisplayLevel();
			}
		});
	}

}