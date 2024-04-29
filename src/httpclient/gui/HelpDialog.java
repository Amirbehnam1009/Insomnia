package httpclient.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A dialog for help menu item to show product help.
 */
class HelpDialog extends JDialog {
    private JTextPane helpTextPane;

    /**
     * Constructor of help dialog
     *
     * @param owner owner frame of the dialog
     */
    HelpDialog(HttpClientGui owner) {
        super(owner, true);
        initUi();
    }

    /**
     * Initializes help dialog GUI
     */
    private void initUi() {
        //creating text pane to display help
        helpTextPane = new JTextPane();
        helpTextPane.setEditable(false);
        add(helpTextPane);

        //creating ok button to exit dialog
        JPanel okPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okButton = new JButton("OK");
        okPanel.add(okButton, BorderLayout.WEST);
        add(okPanel, BorderLayout.SOUTH);
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        //packing and setting size and title and location
        pack();
        setSize(new Dimension(750, 400));
        setResizable(false);
        setTitle("Help");
        setLocationRelativeTo(getOwner());
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    /**
     * Sets the help content came from help command execution to help dialog.
     *
     * @param helpStr help content text
     */
    public void setContentStr(String helpStr) {
        helpTextPane.setText(helpStr);
    }
}
