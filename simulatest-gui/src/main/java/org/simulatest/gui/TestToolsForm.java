package org.simulatest.gui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

public class TestToolsForm extends JFrame{

	private static final long serialVersionUID = 8209989456718369519L;
	private JButton insistenceButton;
	private JButton environmentRunnerButton;
	private final EnvironmentRunner environmentRunner;
	private final InsistenceLayerForm insistenceLayerForm;

	public TestToolsForm(final EnvironmentRunner environmentRunner, final InsistenceLayerForm insistenceLayerForm) {		
		super("Test Tools");
		this.environmentRunner = environmentRunner;
		this.insistenceLayerForm = insistenceLayerForm;
		
		addComponents();
		configureLayout();
		createEvents();
	}

	private void createEvents() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		insistenceButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				insistenceLayerForm.showMe();
			}
		});
		
		environmentRunnerButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				environmentRunner.setVisible(true);
			}
		});
	}

	private void configureLayout() {
		this.setLayout(new FlowLayout());
		this.add(insistenceButton);
		this.add(environmentRunnerButton);
		this.pack();
		this.setLocationRelativeTo(null);		
	}

	private void addComponents() {
		insistenceButton = new JButton("Insistence Layer");
		environmentRunnerButton = new JButton("Environment Runner");		
	}
}
