package httpclient.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

/**
 * Implementation of header panel in response tab.
 */
class ResponseHeader extends JPanel {
    /**
     * main panel of the response header
     */
    private JPanel mainPanel;
    /**
     * owner frame of the panel
     */
    private HttpClientGui httpClientGui;

    /**
     * Constructor of the response header.
     *
     * @param httpClientGui owner frame of the response header
     */
    ResponseHeader(HttpClientGui httpClientGui) {
        this.httpClientGui = httpClientGui;
        initUi();
    }

    /**
     * Initializes response header panel GUI.
     */
    private void initUi() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());

        setLayout(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Fills response header panel with the specified name-values.
     *
     * @param nameValueMap name-values to put in response header
     */
    void fill(Map<String, String> nameValueMap) {
        mainPanel.removeAll();
        int i = 0;
        for (Map.Entry<String, String> entry : nameValueMap.entrySet()) {
            //create a panel and add name and value text fields
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JTextField name = new JTextField(15);
            name.setEditable(false);
            name.setText(entry.getKey());
            panel.add(name);
            JTextField value = new JTextField(15);
            value.setEditable(false);
            value.setText(entry.getValue());
            panel.add(value);

            //align name value to top
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.fill = GridBagConstraints.HORIZONTAL;
            constraints.gridx = 0;
            constraints.gridy = i;
            constraints.anchor = GridBagConstraints.NORTH;
            constraints.weighty = 0;
            mainPanel.add(panel, constraints);
            i++;
        }
        //adding copy to clipboard button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton copyToClipboardButton = new JButton("Copy to Clipboard");
        buttonPanel.add(copyToClipboardButton);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = i;
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.weighty = 1;
        mainPanel.add(buttonPanel, constraints);
        //adding action of copy to clipboard to button
        copyToClipboardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copyToClipboard(nameValueMap);
            }
        });

        revalidate();
        repaint();
    }

    /**
     * Copy to clipboard action to copy content of name-values to clipboard.
     *
     * @param nameValueMap name-values to be copied
     */
    private void copyToClipboard(Map<String, String> nameValueMap) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : nameValueMap.entrySet()) {
            stringBuilder.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        StringSelection stringSelection = new StringSelection(stringBuilder.toString());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);

        JOptionPane.showMessageDialog(httpClientGui, "Copied!");
    }
}
