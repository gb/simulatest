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

		JLabel label = new JLabel("Current Level");

		tfCurrentLevel = new JTextField(3);
		tfCurrentLevel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
		tfCurrentLevel.setEditable(false);
		tfCurrentLevel.setHorizontalAlignment(JTextField.CENTER);

		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
		panel.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
		panel.add(label);
		panel.add(tfCurrentLevel);
		panel.add(levelButton("+", "Increase Level", insistenceLayer::increaseLevel));
		panel.add(levelButton("-", "Decrease Level", insistenceLayer::decreaseLevel));
		panel.add(levelButton("reset", "Reset all levels", insistenceLayer::decreaseAllLevels));
		panel.add(levelButton("clear", "Clear current level", insistenceLayer::resetCurrentLevel));

		getContentPane().add(panel);
		updateDisplayLevel();
		pack();
		setLocationRelativeTo(null);
	}

	/** Every level action refreshes the display, so that rule is stated once here. */
	private JButton levelButton(String label, String tooltip, Runnable action) {
		JButton button = new JButton(label);
		button.setName(label);
		button.setToolTipText(tooltip);
		button.addActionListener(e -> {
			action.run();
			updateDisplayLevel();
		});
		return button;
	}

	public void showMe() {
		updateDisplayLevel();
		setVisible(true);
	}

	private void updateDisplayLevel() {
		tfCurrentLevel.setText(String.valueOf(insistenceLayer.getCurrentLevel()));
	}

}
