package httpclient.gui;

import httpclient.util.JsonValidator;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

/**
 * A panel that shows a json and user can edit a it. It also validates json content online and changes text color based on
 * its validity.
 */
class JsonPanel extends JPanel {
    /**
     * a text pane that user writes json in
     */
    private JTextPane jsonContentPane;
    /**
     * makes the json content editable or read only
     */
    private boolean editable;

    /**
     * Constructor of json panel.
     *
     * @param editable editable or readonly state of the panel
     */
    JsonPanel(boolean editable) {
        this.editable = editable;
        initUi();
    }

    /**
     * Initializes json panel GUI.
     */
    private void initUi() {
        setLayout(new BorderLayout());

        //creating text pane
        jsonContentPane = new JTextPane();
        jsonContentPane.setEditable(editable);
        add(new JScrollPane(jsonContentPane));

        //adding listener to detect any change of text pane to validate json
        jsonContentPane.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                doJsonChangeAction();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                doJsonChangeAction();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                doJsonChangeAction();
            }
        });
    }

    /**
     * Implementation of json change action. It validates json content and changes text color to red if json content
     * is invalid.
     */
    private void doJsonChangeAction() {
        if (JsonValidator.isValidJson(jsonContentPane.getText())) {
            jsonContentPane.setForeground(Color.BLACK);
        } else {
            jsonContentPane.setForeground(Color.RED);
        }
    }

    /**
     * Returns json content provided by user.
     *
     * @return json content
     */
    String getJsonContent() {
        return jsonContentPane.getText();
    }

    /**
     * Fills text pane with specified json content.
     *
     * @param jsonContent json content
     */
    void fill(String jsonContent) {
        jsonContentPane.setText(jsonContent);
        doJsonChangeAction();
    }

    /**
     * Clears json panel content.
     */
    public void clear() {
        jsonContentPane.setText("");
    }
}
