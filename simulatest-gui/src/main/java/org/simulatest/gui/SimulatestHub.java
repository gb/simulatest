package org.simulatest.gui;

import javax.swing.JButton;
import javax.swing.JFrame;

import org.simulatest.insistencelayer.debug.gui.SQLWindow;

import net.miginfocom.swing.MigLayout;

public class SimulatestHub extends JFrame {

	private static final long serialVersionUID = 8209989456718369519L;

	private JButton sqlWindow;
	private JButton environmentRunnerButton;
	private JButton insistenceLayerButton;

	private final EnvironmentRunner environmentRunner;
	private final InsistenceLayerForm insistenceLayerForm;
	private final SQLWindow simulatestSQLWindow;

	public SimulatestHub(EnvironmentRunner environmentRunner, InsistenceLayerForm insistenceLayerForm,
			SQLWindow simulatestSQLWindow) {
		super("Simulatest Hub");

		this.environmentRunner = environmentRunner;
		this.insistenceLayerForm = insistenceLayerForm;
		this.simulatestSQLWindow = simulatestSQLWindow;

		addComponents();
		configureLayout();
		createEvents();
	}

	private void createEvents() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		insistenceLayerButton.addActionListener(e -> insistenceLayerForm.showMe());
		environmentRunnerButton.addActionListener(e -> environmentRunner.setVisible(true));
		sqlWindow.addActionListener(e -> simulatestSQLWindow.setVisible(true));
	}

	private void configureLayout() {
		this.setLayout(new MigLayout("center, wrap 2", "[110, fill]"));
		this.add(sqlWindow);
		this.add(environmentRunnerButton);
		this.add(insistenceLayerButton);
		this.setResizable(false);
		this.pack();
		this.setLocationRelativeTo(null);
	}

	private void addComponents() {
		sqlWindow = new JButton("SQL Window");
		environmentRunnerButton = new JButton("Environment Runner");
		insistenceLayerButton = new JButton("Insistence Layer");
	}

}
