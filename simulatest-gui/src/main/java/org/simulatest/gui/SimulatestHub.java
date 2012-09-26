package org.simulatest.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

import net.miginfocom.swing.MigLayout;

public class SimulatestHub extends JFrame {

	private static final long serialVersionUID = 8209989456718369519L;
	
	private JButton sqlWindow;
	private JButton environmentRunnerButton;
	private JButton insistenceLayerButton;
	private JButton insistenceLayerServerButton;
	
	private final EnvironmentRunner environmentRunner;
	private final InsistenceLayerForm insistenceLayerForm;
	private final SimulatestSQLWindow simulatestSQLWindow;
	private final InsistenceLayerServerForm insistenceLayerServerForm;

	public SimulatestHub(EnvironmentRunner environmentRunner, InsistenceLayerForm insistenceLayerForm,
			SimulatestSQLWindow simulatestSQLWindow, InsistenceLayerServerForm insistenceLayerServerForm) {		
		super("Simulatest Hub");
		
		this.environmentRunner = environmentRunner;
		this.insistenceLayerForm = insistenceLayerForm;
		this.simulatestSQLWindow = simulatestSQLWindow;
		this.insistenceLayerServerForm = insistenceLayerServerForm;
		
		addComponents();
		configureLayout();
		createEvents();
	}

	private void createEvents() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		insistenceLayerButton.addActionListener(new ActionListener() { @Override public void actionPerformed(ActionEvent e) {
			insistenceLayerForm.showMe();
		}});
		
		environmentRunnerButton.addActionListener(new ActionListener() { @Override public void actionPerformed(ActionEvent e) {
			environmentRunner.setVisible(true);
		}});
		
		sqlWindow.addActionListener(new ActionListener() { @Override public void actionPerformed(ActionEvent e) {
			simulatestSQLWindow.showMe();
		}});
		
		insistenceLayerServerButton.addActionListener(new ActionListener() { @Override public void actionPerformed(ActionEvent e) {
			insistenceLayerServerForm.showMe();
		}});
	}

	private void configureLayout() {
		this.setLayout(new MigLayout("center, wrap 2", "[110, fill]"));
		this.add(sqlWindow);
		this.add(environmentRunnerButton);
		this.add(insistenceLayerButton);
		this.add(insistenceLayerServerButton);
		this.setResizable(false);
		this.pack();
		this.setLocationRelativeTo(null);		
	}

	private void addComponents() {
		sqlWindow = new JButton("SQL Window");
		environmentRunnerButton = new JButton("Environment Runner");	
		insistenceLayerButton = new JButton("Insistence Layer");
		insistenceLayerServerButton = new JButton("Insistence Layer Server");
	}
	
}
