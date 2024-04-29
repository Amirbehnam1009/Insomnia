package httpclient.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A dialog for new group button to add new request group.
 */
class NewGroupDialog extends JDialog {
    /**
     * owner of dialog
     */
    private HttpClientGui httpClientGui;
    /**
     * new request group name text field
     */
    private JTextField nameTextField;

    /**
     * Constructor of new group dialog
     *
     * @param httpClientGui owner of the dialog
     */
    NewGroupDialog(HttpClientGui httpClientGui) {
        super(httpClientGui, true);
        this.httpClientGui = httpClientGui;

        initUi();
    }

    /**
     * Initializes new request group dialog GUI
     */
    private void initUi() {
        //adding textfield and button
        nameTextField = new JTextField(20);
        JButton createButton = new JButton("Create");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(createButton);
        add(nameTextField);
        add(buttonPanel, BorderLayout.SOUTH);

        //adding action of create button
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doCreateAction();
            }
        });

        setResizable(false);
        pack();
        setTitle("New Group");
        setLocationRelativeTo(getOwner());
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    /**
     * Create action of dialog that creates a new user group and adds it to saved requests panel.
     */
    private void doCreateAction() {
        String name = nameTextField.getText().trim();
        //if group name not specified, show error message
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please choose a name!", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            try {
                httpClientGui.createGroup(name);
            } catch (Exception e) {
                //show error message if creating request group leads to error
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        nameTextField.setText("");
        dispose();
    }
}
