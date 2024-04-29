package httpclient.gui;

import httpclient.gui.theme.ThemeType;
import httpclient.repository.OptionsRepository;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A dialog for options menu item to configure some user options.
 */
class OptionsDialog extends JDialog {
    /**
     * follow redirect option check box
     */
    private JCheckBox followRedirectCheckBox;
    /**
     * system tray option check box
     */
    private JCheckBox systemTrayCheckBox;
    /**
     * theme selection combo box
     */
    private JComboBox<ThemeType> themeComboBox;

    /**
     * current options model object
     */
    private OptionsRepository.OptionsModel optionsModel;
    /**
     * owner of the options dialog
     */
    private HttpClientGui httpClientGui;

    /**
     * Constructor of the options dialog
     *
     * @param owner        owner of the dialog
     * @param optionsModel current options model
     */
    OptionsDialog(HttpClientGui owner, OptionsRepository.OptionsModel optionsModel) {
        super(owner, true);
        this.optionsModel = optionsModel;
        httpClientGui = owner;
        initUi();
    }

    /**
     * Initializes options dialog GUI
     */
    private void initUi() {
        //making options dialog using grid layout
        JPanel panel = new JPanel(new GridLayout(4, 1));
        add(panel);

        //adding follow redirect check box
        followRedirectCheckBox = new JCheckBox("Follow Redirect");
        panel.add(followRedirectCheckBox);

        //adding system tray check box
        systemTrayCheckBox = new JCheckBox("Go to System Tray On Exit");
        panel.add(systemTrayCheckBox);

        //adding theme selection combo box
        JLabel themeLabel = new JLabel("Theme:");
        themeComboBox = new JComboBox<>(ThemeType.values());
        JPanel themePanel = new JPanel();
        themePanel.add(themeLabel, BorderLayout.EAST);
        themePanel.add(themeComboBox);
        panel.add(themePanel);

        //adding save button
        JButton saveButton = new JButton("Save");
        JPanel savePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        savePanel.add(saveButton, BorderLayout.WEST);
        panel.add(savePanel);

        //adding action of saving to button
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doSave();
            }
        });

        setResizable(false);
        pack();
        setTitle("Options");
        setLocationRelativeTo(getOwner());
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    /**
     * saves user configured options.
     */
    private void doSave() {
        boolean followRedirect = followRedirectCheckBox.isSelected();
        boolean systemTray = systemTrayCheckBox.isSelected();
        ThemeType themeType = themeComboBox.getItemAt(themeComboBox.getSelectedIndex());

        try {
            optionsModel = httpClientGui.doSaveOptions(followRedirect, systemTray, themeType);
            dispose();
        } catch (Exception e) {
            //shows error message at the case of error in saving new options
            JOptionPane.showMessageDialog(this, "Save options failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Shows the options dialog with current options model.
     */
    void showOptions() {
        followRedirectCheckBox.setSelected(optionsModel.isFollowRedirect());
        systemTrayCheckBox.setSelected(optionsModel.isSystemTray());
        themeComboBox.setSelectedIndex(optionsModel.getThemeValue() - 1);
        setVisible(true);
    }
}
