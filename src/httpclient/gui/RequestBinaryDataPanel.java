package httpclient.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Implementation of binary data panel in request panel.It contains a file chooser that selects a file as binary data
 * to send.
 */
class RequestBinaryDataPanel extends JPanel {
    /**
     * selected file path text field
     */
    private JTextField filePathTextField;
    /**
     * owner frame
     */
    private HttpClientGui httpClientGui;
    /**
     * selected file object
     */
    private File selectedFile;

    /**
     * Constructor of binary data panel
     *
     * @param httpClientGui owner of panel
     */
    RequestBinaryDataPanel(HttpClientGui httpClientGui) {
        this.httpClientGui = httpClientGui;
        initUi();
    }

    /**
     * Initializes binary data panel of request GUI.
     */
    private void initUi() {
        setLayout(new FlowLayout());

        //adding selected file textfield
        filePathTextField = new JTextField(30);
        filePathTextField.setEditable(false);
        add(filePathTextField);

        //adding a button to choose file
        JButton chooseFileButton = new JButton("Choose File");
        add(chooseFileButton);

        //adding action of button to show file chooser
        chooseFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doChooseFileAction();
            }
        });
    }

    /**
     * File selection action implementation that shows a file chooser and get chosen file from it.
     */
    private void doChooseFileAction() {
        //get an instance of file chooser
        JFileChooser fileChooser = new JFileChooser();
        //if a file is selected before, set directory of file chooser to parent directory of last selected file
        if (selectedFile != null) {
            fileChooser.setCurrentDirectory(selectedFile.getParentFile());
        }
        int result = fileChooser.showOpenDialog(httpClientGui);
        //if user chooses a file get selected file from file chooser and put its path to the text field
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            filePathTextField.setText(selectedFile.getAbsolutePath());
        }
    }

    /**
     * Gets path of the selected file.
     *
     * @return path of the selected file
     * @throws Exception if no file is selected
     */
    String getSelectedFilePath() throws Exception {
        if (selectedFile == null) {
            throw new Exception("Please select file.");
        }
        return selectedFile.getAbsolutePath();
    }

    /**
     * Sets the file path to upload.
     *
     * @param uploadPath file path to upload
     */
    public void setSelectedFilePath(String uploadPath) {
        selectedFile = new File(uploadPath);
        filePathTextField.setText(selectedFile.getAbsolutePath());
    }

    /**
     * Clears the file upload selection.
     */
    public void clear() {
        selectedFile = null;
        filePathTextField.setText("");
    }
}
