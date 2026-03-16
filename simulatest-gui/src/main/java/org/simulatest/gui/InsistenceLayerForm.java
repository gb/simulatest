package org.simulatest.gui;

import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import org.simulatest.insistencelayer.InsistenceLayer;

public class InsistenceLayerForm extends JFrame {

	private static final long serialVersionUID = 1L;

	private final InsistenceLayer insistenceLayer;
	private final JTextField tfCurrentLevel;

	public InsistenceLayerForm(InsistenceLayer insistenceLayer) {
		this.insistenceLayer = insistenceLayer;

		setTitle("Insistence Layer");
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setResizable(false);
		setLocationRelativeTo(null);

		JLabel label = new JLabel("Current Level");

		tfCurrentLevel = new JTextField(3);
		tfCurrentLevel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
		tfCurrentLevel.setEditable(false);
		tfCurrentLevel.setHorizontalAlignment(JTextField.CENTER);

		JButton btnIncrease = new JButton("+");
		btnIncrease.setName("+");
		btnIncrease.setToolTipText("Increase Level");
		btnIncrease.addActionListener(e -> {
			insistenceLayer.increaseLevel();
			updateDisplayLevel();
		});

		JButton btnDecrease = new JButton("-");
		btnDecrease.setName("-");
		btnDecrease.setToolTipText("Decrease Level");
		btnDecrease.addActionListener(e -> {
			insistenceLayer.decreaseLevel();
			updateDisplayLevel();
		});

		JButton btnReset = new JButton("reset");
		btnReset.setName("reset");
		btnReset.setToolTipText("Reset all levels");
		btnReset.addActionListener(e -> {
			insistenceLayer.decreaseAllLevels();
			updateDisplayLevel();
		});

		JButton btnClear = new JButton("clear");
		btnClear.setName("clear");
		btnClear.setToolTipText("Clear current level");
		btnClear.addActionListener(e -> {
			insistenceLayer.resetCurrentLevel();
			updateDisplayLevel();
		});

		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
		panel.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
		panel.add(label);
		panel.add(tfCurrentLevel);
		panel.add(btnIncrease);
		panel.add(btnDecrease);
		panel.add(btnReset);
		panel.add(btnClear);

		getContentPane().add(panel);
		updateDisplayLevel();
		pack();
		setLocationRelativeTo(null);
	}

	public void showMe() {
		updateDisplayLevel();
		setVisible(true);
	}

	private void updateDisplayLevel() {
		tfCurrentLevel.setText(String.valueOf(insistenceLayer.getCurrentLevel()));
	}

}
