package httpclient.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * Implementation of a name-value pair that shows a name-value and can be edited by user, if sets to be editable. It
 * also can have an enabling check box and a remove button that controlled from its container.
 */
class NameValue extends JPanel {
    /**
     * Container of name-value
     */
    private NameValuePanel container;
    /**
     * name text field
     */
    private JTextField nameTextField;
    /**
     * value text field
     */
    private JTextField valueTextField;
    /**
     * enable checkbox
     */
    private JCheckBox enableCheckBox;
    /**
     * remove button
     */
    private JButton removeButton;

    /**
     * Constructor of name value panel.
     *
     * @param container container of the name value panel
     */
    NameValue(NameValuePanel container) {
        this.container = container;
        initUi();
    }

    /**
     * Initializes name value panel GUI.
     */
    private void initUi() {
        //aligning panel to left
        setLayout(new FlowLayout(FlowLayout.LEFT));

        //adding name-value components
        nameTextField = new JTextField(15);
        valueTextField = new JTextField(15);
        add(nameTextField);
        add(valueTextField);

        //creating enable checkbox and remove button to control their enabling from container
        enableCheckBox = new JCheckBox();
        enableCheckBox.setSelected(true);
        removeButton = new JIconButton(new ImageIcon("remove.png"), new ImageIcon("remove1.png"));

        setPreferredSize(new Dimension(350, 30));

        //adding listeners for focus to tell container add new row to its name-values if required.
        nameTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                doNameValueFocusAction();
                nameTextField.requestFocus();
            }
        });
        valueTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                doNameValueFocusAction();
                valueTextField.requestFocus();
            }
        });

        //adding listener for remove button to tell container to remove this name-value pair
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doRemoveAction();
            }
        });
    }

    /**
     * Remove button select action, it tells container to remove this name-value pair.
     */
    private void doRemoveAction() {
        container.removeNameValue(this);
    }

    /**
     * Focus action of name or value text fields, it tells container to add new name value to its panel if required.
     */
    private void doNameValueFocusAction() {
        container.addNameValue(this);
    }

    /**
     * Enables check box and remove button.
     */
    void enableActions() {
        add(enableCheckBox);
        add(removeButton);
    }

    /**
     * Gets enabled state of the name-value.
     *
     * @return enabled state
     */
    public boolean enabled() {
        return enableCheckBox.isSelected();
    }

    /**
     * Gets ket of the name-value;
     *
     * @return the key
     */
    public String getKey() {
        return nameTextField.getText().trim();
    }

    /**
     * Sets key of the name-vale.
     *
     * @param key the key to set
     */
    public void setKey(String key) {
        nameTextField.setText(key);
    }

    /**
     * Gets value of the name-value.
     *
     * @return the value
     */
    public String getValue() {
        return valueTextField.getText().trim();
    }

    /**
     * Sets value of the name-value.
     *
     * @param value the value to set
     */
    public void setValue(String value) {
        valueTextField.setText(value);
    }
}
