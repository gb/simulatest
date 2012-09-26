package org.simulatest.gui;

import java.awt.ComponentOrientation;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.simulatest.insistencelayer.server.InsistenceLayerServer;

public class InsistenceLayerServerForm extends JFrame {

	private static final long serialVersionUID = 2847655924513145467L;

	private JButton btnTurnOn;
	private JButton btnTurnOff;

	public InsistenceLayerServerForm() {
		configureLayout();
		addComponents();
		createEvents();
	}
	
	public void showMe() {
		refreshStatus();
		pack();
		setVisible(true);
	}

	private void addComponents() {
		btnTurnOn = new JButton("Start Server");
		btnTurnOff = new JButton("Shutdown Server");
		
		add(btnTurnOn);
		add(btnTurnOff);
	}

	private void configureLayout() {
		setLayout(new FlowLayout(FlowLayout.CENTER));
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setResizable(false);
		setTitle("Insistence Layer Server");
		setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		setLocationRelativeTo(null);
	}

	private void createEvents() {
		btnTurnOn.addActionListener(new ActionListener() { @Override public void actionPerformed(ActionEvent e) {
			InsistenceLayerServer.start();
			refreshStatus();
		}});
		
		btnTurnOff.addActionListener(new ActionListener() { @Override public void actionPerformed(ActionEvent e) {
			InsistenceLayerServer.shutdown();
			refreshStatus();
		}});
	}

	private void refreshStatus() {
		if (InsistenceLayerServer.isAvailable()) {
			btnTurnOn.setVisible(false);
			btnTurnOff.setVisible(true);
		} else {
			btnTurnOn.setVisible(true);
			btnTurnOff.setVisible(false);
		}
	}

}
