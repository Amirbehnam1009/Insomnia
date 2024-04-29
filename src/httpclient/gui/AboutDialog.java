package httpclient.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A dialog for about menu item to show product and developer info.
 */
class AboutDialog extends JDialog {
    /**
     * Constructor of about dialog
     *
     * @param owner owner frame of the dialog
     */
    AboutDialog(HttpClientGui owner) {
        super(owner, true);
        initUi();
    }

    /**
     * Initializes about dialog GUI
     */
    private void initUi() {
        //creating text pane to display info
        JTextPane textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setFont(new Font("Font", Font.ITALIC, 20));
        String aboutText = "Http Client, Developed by" + "\n\t" + "Amir Behnam" + "\n\t" + "9831133" + "\n\t" + "amirbehnam1009@gmail.com";
        textPane.setText(aboutText);
        add(textPane);

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
        setSize(new Dimension(350, 200));
        setResizable(false);
        setTitle("About");
        setLocationRelativeTo(getOwner());
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }
}
