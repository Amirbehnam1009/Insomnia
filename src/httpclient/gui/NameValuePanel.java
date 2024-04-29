package httpclient.gui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of a panel that have couple of name-value pairs. Last name-value pair is new one and if be selected by
 * user, another name-value pair will be added to the panel.
 */
class NameValuePanel extends JPanel {
    /**
     * all name-value pairs
     */
    private List<NameValue> nameValueList = new ArrayList<>();
    /**
     * new name-value pair
     */
    private NameValue emptyNameValue;

    /**
     * main panel of the name value panel
     */
    private JPanel mainPanel;

    /**
     * marks that it is after a name-value pair state
     */
    private boolean afterRemove = false;

    /**
     * Name value panel constructor.
     */
    NameValuePanel() {
        initUi();
    }

    /**
     * Initializes name value panel GUI
     */
    private void initUi() {
        //creating main panel and its grid bag layout
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setFocusable(true);

        //creating initial empty name value an aligning it to otp
        emptyNameValue = new NameValue(this);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.weighty = 1;
        mainPanel.add(emptyNameValue, constraints);

        //adding a scroll pane so that if number of name-values is more than size of name value panel, it scrolls
        setLayout(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Adds new name value pair to the name value panel.
     *
     * @param requester name value that issue the add request
     */
    void addNameValue(NameValue requester) {
        //remove action of last name value pair, gives focus to empty name value, the panel thinks user clicks on empty
        //text fields and it adds new name value row
        //therefore ignore add name value request, if its after remove
        if (afterRemove) {
            afterRemove = false;
        } else {
            //add new name value row, if requester is empty name value (last one)
            if (requester.equals(emptyNameValue)) {
                //remove empty name value
                mainPanel.remove(emptyNameValue);

                //make weighty of empty name value 0, so that it doesn't absorb extra vertical space of panel
                GridBagConstraints constraints = new GridBagConstraints();
                constraints.fill = GridBagConstraints.HORIZONTAL;
                constraints.gridx = 0;
                constraints.gridy = nameValueList.size();
                constraints.anchor = GridBagConstraints.NORTH;
                constraints.weighty = 0;
                mainPanel.add(emptyNameValue, constraints);

                //add empty name value to name value list and make its checkbox and remove button enable
                nameValueList.add(emptyNameValue);
                emptyNameValue.enableActions();

                //create new empty name value and give vertical extra space to it by setting weighty = 1
                emptyNameValue = new NameValue(this);
                constraints = new GridBagConstraints();
                constraints.fill = GridBagConstraints.HORIZONTAL;
                constraints.gridx = 0;
                constraints.gridy = nameValueList.size();
                constraints.anchor = GridBagConstraints.NORTH;
                constraints.weighty = 1;
                mainPanel.add(emptyNameValue, constraints);

                //revalidate and repaint the panel
                revalidate();
                repaint();
            }
        }
    }

    /**
     * Removes specified name value from the name value panel.
     *
     * @param nameValue name value to remove
     */
    void removeNameValue(NameValue nameValue) {
        mainPanel.remove(nameValue);
        nameValueList.remove(nameValue);
        afterRemove = true;
        mainPanel.requestFocus();
        revalidate();
        repaint();
    }

    /**
     * Gets a map containing all enabled name-values of the name-value panel.
     *
     * @return all enabled name-values
     */
    Map<String, String> getNameValues() {
        Map<String, String> result = new HashMap<>();
        for (NameValue nameValue : nameValueList) {
            if (nameValue.enabled()) {
                result.put(nameValue.getKey(), nameValue.getValue());
            }
        }
        return result;
    }

    /**
     * Adds a new name-value with specified key and value, and if the specified exists, updates the value of the key.
     *
     * @param key   the key to add or update
     * @param value the value
     */
    public void addOrUpdateKeyValue(String key, String value) {
        for (NameValue nameValue : nameValueList) {
            if (nameValue.getKey().equals(key)) {
                nameValue.setValue(value);
                return;
            }
        }
        emptyNameValue.setKey(key);
        emptyNameValue.setValue(value);
        addNameValue(emptyNameValue);
    }

    /**
     * Removes all name-values from the name-value panel.
     */
    void removeAllNameValues() {
        for (int i = nameValueList.size() - 1; i >= 0; i--) {
            NameValue nameValue = nameValueList.get(i);
            removeNameValue(nameValue);
        }
        afterRemove = false;
    }
}
