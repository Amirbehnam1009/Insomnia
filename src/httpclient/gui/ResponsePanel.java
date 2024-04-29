package httpclient.gui;

import httpclient.entity.Response;
import httpclient.entity.ResponseBodyType;
import httpclient.entity.ResponseContentType;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Implementation of a panel that placed at the right side of the frame and displays the response.
 */
class ResponsePanel extends JPanel {
    /**
     * response header panel
     */
    private ResponseHeader responseHeader;
    /**
     * owner frame
     */
    private HttpClientGui httpClientGui;
    /**
     * response body type combo box
     */
    private JComboBox<ResponseBodyType> responseBodyTypeCombo;
    /**
     * holds last selected index of response body combo box
     */
    private int responseBodyTypeComboSelectedIndex = 0;
    /**
     * tabbed pane to show response content
     */
    private JTabbedPane responseContentPanel;
    /**
     * response json panel
     */
    private JsonPanel jsonPanel;
    /**
     * response raw content panel
     */
    private JPanel rawPanel;
    /**
     * response raw content text pane
     */
    private JTextPane rawContentPane;
    /**
     * response preview panel
     */
    private JPanel previewPanel;
    /**
     * status label
     */
    private JLabel status;
    /**
     * time label
     */
    private JLabel time;
    /**
     * volume label
     */
    private JLabel volume;

    /**
     * Constructor of response panel
     *
     * @param httpClientGui owner frame of the response panel
     */
    ResponsePanel(HttpClientGui httpClientGui) {
        this.httpClientGui = httpClientGui;
        initUi();
    }

    /**
     * Initializes response panel GUI.
     */
    private void initUi() {
        //instantiating internal panels
        jsonPanel = new JsonPanel(false);

        rawPanel = new JPanel(new BorderLayout());
        rawContentPane = new JTextPane();
        rawContentPane.setEditable(false);
        rawPanel.add(new JScrollPane(rawContentPane));

        previewPanel = new JPanel(new BorderLayout());

        responseHeader = new ResponseHeader(httpClientGui);

        setLayout(new BorderLayout());

        //adding labels
        JPanel responseStatusPanel = new JPanel();
        responseStatusPanel.setPreferredSize(new Dimension(-1, 40));
        status = new JLabel("");
        responseStatusPanel.add(status, BorderLayout.EAST);
        time = new JLabel("");
        responseStatusPanel.add(time, BorderLayout.EAST);
        volume = new JLabel("");
        responseStatusPanel.add(volume, BorderLayout.EAST);
        add(responseStatusPanel, BorderLayout.NORTH);

        //adding tabbed pane
        responseContentPanel = new JTabbedPane(JTabbedPane.TOP);
        responseContentPanel.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        responseBodyTypeCombo = new JComboBox<>(ResponseBodyType.values());
        responseContentPanel.addTab("", getMessageBodyPanel(responseBodyTypeCombo.getItemAt(responseBodyTypeCombo.getSelectedIndex())));
        responseContentPanel.setTabComponentAt(0, responseBodyTypeCombo);
        responseContentPanel.addTab("Header", getHeaderPanel());
        add(responseContentPanel);

        setMinimumSize(new Dimension(400, -1));

        //adding action for popup menu of response body combo box to select response body tab when selecting the combo box
        responseBodyTypeCombo.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                responseContentPanel.setSelectedIndex(0);
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {

            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {

            }
        });
        //adding action of changing selected option of response body combo box to show selected panel
        responseBodyTypeCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        doResponseBodyTypeChangeAction();
                    }
                });
            }
        });
    }

    /**
     * changes panel of response body tab, if response body type changes.
     */
    private void doResponseBodyTypeChangeAction() {
        if (responseBodyTypeCombo.getSelectedIndex() != responseBodyTypeComboSelectedIndex) {
            responseBodyTypeComboSelectedIndex = responseBodyTypeCombo.getSelectedIndex();
            int tabSelectedIndex = responseContentPanel.getSelectedIndex();
            responseContentPanel.removeTabAt(tabSelectedIndex);
            responseContentPanel.insertTab("", null, getMessageBodyPanel(responseBodyTypeCombo.getItemAt(responseBodyTypeCombo.getSelectedIndex())), null, tabSelectedIndex);
            responseContentPanel.setTabComponentAt(tabSelectedIndex, responseBodyTypeCombo);
            responseContentPanel.setSelectedIndex(tabSelectedIndex);
        }
    }

    /**
     * Returns response body panel based of selected response body type selected in combo box.
     *
     * @param responseBodyType selected response body type
     * @return response body panel
     */
    private JPanel getMessageBodyPanel(ResponseBodyType responseBodyType) {
        switch (responseBodyType) {
            case Raw:
                return rawPanel;
            case Preview:
                return previewPanel;
            case JSON:
                return jsonPanel;
            default:
                return new JPanel();
        }
    }

    /**
     * Gets an instance of header panel.
     *
     * @return an instance of header panel
     */
    private JPanel getHeaderPanel() {
        return responseHeader;
    }

    /**
     * Fills response panel with the specified response.
     *
     * @param response response to fill response panel
     */
    void fill(Response response) {
        //fill with real response data
        try {
            status.setText(response.getStatusCode() + " " + response.getStatusMessage());
            time.setText(response.getTime());
            volume.setText(response.getDataSize());
            if (response.getContentType().equals(ResponseContentType.Picture)) {
                rawContentPane.setText("Image content type");
                previewPanel.removeAll();
                ByteArrayInputStream inputStream = new ByteArrayInputStream(response.getContentBytes());
                BufferedImage testImage = ImageIO.read(inputStream);
                JPanel previewInternalPanel = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        g.drawImage(testImage, 0, 0, this);
                    }
                };
                previewInternalPanel.setPreferredSize(new Dimension(testImage.getWidth(), testImage.getHeight()));
                previewPanel.add(new JScrollPane(previewInternalPanel));
                responseBodyTypeCombo.setSelectedIndex(1);
            } else if (response.getContentType().equals(ResponseContentType.Json)) {
                rawContentPane.setText("Json content type");
                jsonPanel.fill(response.getContentStr());
                previewPanel.removeAll();
                responseBodyTypeCombo.setSelectedIndex(2);
            } else {
                rawContentPane.setText(response.getContentStr());
                jsonPanel.fill("");
                previewPanel.removeAll();
                responseBodyTypeCombo.setSelectedIndex(0);
            }
            responseHeader.fill(response.getHeader());

            previewPanel.revalidate();
            previewPanel.repaint();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
