package httpclient.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A dialog for saving new request.
 */
class SaveRequestDialog extends JDialog {
    /**
     * create new group option in save a request
     */
    static final String CREATE_GROUP = "[Create Group]";
    /**
     * owner frame of the dialog
     */
    private HttpClientGui httpClientGui;
    /**
     * new request name text field
     */
    private JTextField nameTextField;
    /**
     * request group selection combo box
     */
    private JComboBox<String> groupComboBox;

    /**
     * Constructor of the save request dialog.
     *
     * @param httpClientGui owner frame of the save request dialog
     */
    SaveRequestDialog(HttpClientGui httpClientGui) {
        super(httpClientGui, true);
        this.httpClientGui = httpClientGui;

        initUi();
    }

    /**
     * Initializes save request dialog GUI.
     */
    private void initUi() {
        setLayout(new GridLayout(3, 1));
        //instantiating components
        nameTextField = new JTextField(20);
        groupComboBox = new JComboBox<>();
        groupComboBox.setPreferredSize(new Dimension(155, 20));
        JButton saveButton = new JButton("Save");

        //adding title
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new FlowLayout());
        titlePanel.add(new JLabel("Title: "));
        titlePanel.add(nameTextField);

        //adding group
        JPanel groupPanel = new JPanel();
        groupPanel.setLayout(new FlowLayout());
        groupPanel.add(new JLabel("Group: "));
        groupPanel.add(groupComboBox);

        //adding save button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(saveButton);

        add(titlePanel);
        add(groupPanel);
        add(buttonPanel);

        //adding action of saving request
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doSaveAction();
            }
        });

        setResizable(false);
        pack();
        setTitle("Save Request");
        setLocationRelativeTo(getOwner());
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    /**
     * Saves the specified new request.
     */
    private void doSaveAction() {
        String name = nameTextField.getText().trim();
        String groupName = groupComboBox.getItemAt(groupComboBox.getSelectedIndex());

        //show error message if the name is invalid
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please choose a name!", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            if (groupName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please choose a group name!", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                try {
                    httpClientGui.saveRequest(name, groupName);
                } catch (Exception e) {
                    //show error message when save request leads to error
                    JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
                dispose();
            }
        }
    }

    /**
     * Shows save request dialog.
     */
    void preview() {
        groupComboBox.removeAllItems();
        groupComboBox.addItem("");
        groupComboBox.addItem(CREATE_GROUP);
        for (String groupName : httpClientGui.getAllGroupNames()) {
            groupComboBox.addItem(groupName);
        }
        nameTextField.setText("");
        groupComboBox.setSelectedIndex(0);
        setVisible(true);
    }
}
