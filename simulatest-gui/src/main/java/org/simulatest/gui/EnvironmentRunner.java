package org.simulatest.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.simulatest.environment.environment.Environment;
import org.simulatest.environment.environment.facade.EnvironmentRunnerFacade;
import org.simulatest.environment.infra.EnvironmentScanner;
import org.simulatest.insistencelayer.InsistenceLayerManager;

public class EnvironmentRunner extends JFrame implements ListSelectionListener {
	
	private static final long serialVersionUID = -568982423610625422L;
	
	private JList list;
    private DefaultListModel listModel;

    private JButton fireButton;
    private JTextField employeeName;
    
    private EnvironmentRunnerFacade facade;

    public EnvironmentRunner(InsistenceLayerManager insistenceLayerManager) {
        super("Environment Runner");
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        
        this.setSize(500, 375);
        this.setLocationRelativeTo(null);
        
        this.setLayout(new BorderLayout());
        
       // facade = new EnvironmentRunnerFacade(insistenceLayerManager);

        listModel = new DefaultListModel();
        
        for (Class<?> clazz : new EnvironmentScanner().getEnvironmentList())
        	listModel.addElement(clazz.getName());

        //Create the list and put it in a scroll pane.
        list = new JList(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setSelectedIndex(0);
        list.addListSelectionListener(this);
        list.setVisibleRowCount(5);
        JScrollPane listScrollPane = new JScrollPane(list);

        JButton hireButton = new JButton("Find");
        HireListener hireListener = new HireListener(hireButton);
        hireButton.setActionCommand("Find");
        hireButton.addActionListener(hireListener);
        hireButton.setEnabled(false);

        fireButton = new JButton("Run");
        fireButton.setActionCommand("Run");
        fireButton.addActionListener(new FireListener());

        employeeName = new JTextField(10);
        employeeName.addActionListener(hireListener);
        employeeName.getDocument().addDocumentListener(hireListener);
        String name = listModel.getElementAt(
                              list.getSelectedIndex()).toString();

        //Create a panel that uses BoxLayout.
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane,
                                           BoxLayout.LINE_AXIS));
        buttonPane.add(fireButton);
        buttonPane.add(Box.createHorizontalStrut(5));
        buttonPane.add(new JSeparator(SwingConstants.VERTICAL));
        buttonPane.add(Box.createHorizontalStrut(5));
        buttonPane.add(employeeName);
        buttonPane.add(hireButton);
        buttonPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        add(listScrollPane, BorderLayout.CENTER);
        add(buttonPane, BorderLayout.PAGE_END);
    }

    class FireListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int index = list.getSelectedIndex();
            Class<? extends Environment> clazz = null;
			try {
				clazz = (Class<? extends Environment>) Class.forName((String) listModel.get(index));
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
            facade.runEnvironment(clazz);

            int size = listModel.getSize();

            if (size == 0) { //Nobody's left, disable firing.
                fireButton.setEnabled(false);

            } else { //Select an index.
                if (index == listModel.getSize()) {
                    //removed item in last position
                    index--;
                }

                list.setSelectedIndex(index);
                list.ensureIndexIsVisible(index);
            }
        }
    }

    class HireListener implements ActionListener, DocumentListener {
        private boolean alreadyEnabled = false;
        private JButton button;

        public HireListener(JButton button) {
            this.button = button;
        }

        public void actionPerformed(ActionEvent e) {
        }

        protected boolean alreadyInList(String name) {
            return listModel.contains(name);
        }

        public void insertUpdate(DocumentEvent e) {
            enableButton();
        }

        public void removeUpdate(DocumentEvent e) {
            handleEmptyTextField(e);
        }

        public void changedUpdate(DocumentEvent e) {
            if (!handleEmptyTextField(e)) {
                enableButton();
            }
        }

        private void enableButton() {
            if (!alreadyEnabled) {
                button.setEnabled(true);
            }
        }

        private boolean handleEmptyTextField(DocumentEvent e) {
            if (e.getDocument().getLength() <= 0) {
                button.setEnabled(false);
                alreadyEnabled = false;
                return true;
            }
            return false;
        }
    }

    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting() == false) {

            if (list.getSelectedIndex() == -1) {
                fireButton.setEnabled(false);

            } else {
                fireButton.setEnabled(true);
            }
        }
    }


}