package httpclient.gui;

import javax.swing.*;
import java.awt.*;

/**
 * A panel that should be placed in Auth tab of request panel to define bearer token of http request.
 */
class AuthPanel extends JPanel {
    /**
     * token text field
     */
    private JTextField tokenTextField;
    /**
     * prefix text field
     */
    private JTextField prefixTextField;
    /**
     * to enable token
     */
    private JCheckBox enableCheckBox;

    /**
     * Constructor of auth panel
     */
    AuthPanel() {
        initUi();
    }

    /**
     * Initializes auth panel GUI.
     */
    private void initUi() {
        //setting layout
        setLayout(new GridBagLayout());

        //creating three panel, each contain a label and an input in a row
        //creating token panel
        JPanel tokenPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tokenPanel.add(new JLabel("Token:"));
        tokenTextField = new JTextField(30);
        tokenPanel.add(tokenTextField);

        //creating prefix panel
        JPanel prefixPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        prefixPanel.add(new JLabel("Prefix:"));
        prefixTextField = new JTextField(30);
        prefixPanel.add(prefixTextField);

        //creating enable panel
        JPanel enablePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        enableCheckBox = new JCheckBox("Enable");
        enableCheckBox.setSelected(true);
        enablePanel.add(enableCheckBox);

        //adding each panel in a row and aligning to top in grid bag layout
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.weighty = 0;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.NORTH;
        add(tokenPanel, constraints);
        constraints = new GridBagConstraints();
        constraints.weighty = 0;
        constraints.gridx = 0;
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.gridy = 1;
        add(prefixPanel, constraints);
        constraints = new GridBagConstraints();
        constraints.weighty = 1;
        constraints.gridx = 0;
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.gridy = 2;
        add(enablePanel, constraints);
    }
}
