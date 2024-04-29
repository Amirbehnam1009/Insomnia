package httpclient.gui;

import httpclient.entity.Request;
import httpclient.entity.RequestBodyType;
import httpclient.entity.RequestMethod;
import jurl.RequestExecutor;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Implementation of a panel that placed at the middle of the frame and handles the user request.
 */
class RequestPanel extends JPanel {
    /**
     * request methods selection combo box
     */
    private JComboBox<RequestMethod> methodsCombo;
    /**
     * request utl textfield
     */
    private JTextField urlTextField;
    /**
     * owner frame of the panel
     */
    private HttpClientGui httpClientGui;
    /**
     * save request dialog that shown on save button action
     */
    private SaveRequestDialog saveRequestDialog;
    /**
     * a tabbed pane for request parameters
     */
    private JTabbedPane parametersPanel;
    /**
     * request body type selection combo box in tabbed pane
     */
    private JComboBox<RequestBodyType> requestBodyTypeCombo;
    /**
     * holds selected index of the request body type, when request body type changes
     */
    private int requestBodyTypeComboSelectedIndex = 0;

    /**
     * Constructor of the request panel
     *
     * @param httpClientGui owner frame of the request panel
     */
    RequestPanel(HttpClientGui httpClientGui) {
        this.httpClientGui = httpClientGui;
        initUi();
    }

    private JButton send;
    private JButton save;

    /**
     * Initializes request panel GUI.
     */
    private void initUi() {
        //instantiating save request deialog
        saveRequestDialog = new SaveRequestDialog(httpClientGui);

        setLayout(new BorderLayout());

        //creating request panel, the panel placed on top of the tabbed pane and contains request method, url, save and send
        JPanel requestPanel = new JPanel();
        requestPanel.setPreferredSize(new Dimension(-1, 40));
        methodsCombo = new JComboBox<>(RequestMethod.values());
        requestPanel.add(methodsCombo, BorderLayout.EAST);
        urlTextField = new JTextField(30);
        requestPanel.add(urlTextField);
        send = new JButton("Send");
        save = new JIconButton(new ImageIcon("save.png"), new ImageIcon("save1.png"));
        requestPanel.add(send);
        requestPanel.add(save);
        add(requestPanel, BorderLayout.NORTH);

        //adding action of save button that shows save request dialog
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    public void run() {
                        doSaveRequest();
                    }
                }).start();
            }
        });
        //adding action of send button that sends request and gets response
        send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    public void run() {
                        doSendRequest();
                    }
                }).start();
            }
        });

        //creating request parameters tabbed pane, containing request body, auth, query and header
        parametersPanel = new JTabbedPane(JTabbedPane.TOP);
        parametersPanel.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        requestBodyTypeCombo = new JComboBox<>(RequestBodyType.values());
        parametersPanel.addTab("", getRequestBodyPanel(getSelectedBodyType()));
        parametersPanel.setTabComponentAt(0, requestBodyTypeCombo);
        parametersPanel.addTab("Auth", getAuthPanel());
        parametersPanel.addTab("Query", makeQueryPanel());
        parametersPanel.addTab("Header", makeHeaderPanel());
        addProperHeader();
        add(parametersPanel);

        setMinimumSize(new Dimension(430, -1));

        //adding action for popup menu of request body combo box to select request body tab when selecting the combo box
        requestBodyTypeCombo.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                parametersPanel.setSelectedIndex(0);
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {

            }
        });
        //adding action of changing selected option of request body combo box to show selected panel
        requestBodyTypeCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        doRequestBodyTypeChangeAction();
                    }
                });
            }
        });
    }

    /**
     * Send request action that collects command arguments of request and asks for sending.
     */
    private void doSendRequest() {
        try {
            List<String> commandArgs = collectCommandArgs();
            httpClientGui.sendRequest(commandArgs.toArray(new String[0]), getQueryNameValues(), getSelectedBodyType());
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    void enableActions() {
        send.setEnabled(true);
        save.setEnabled(true);
    }

    void disableActions() {
        send.setEnabled(false);
        save.setEnabled(false);
    }

    /**
     * Collects all command arguments of the request.
     *
     * @return collected command arguments
     * @throws Exception if there is problems in data collection
     */
    List<String> collectCommandArgs() throws Exception {
        List<String> commandArgs = new ArrayList<>();
        Map<String, String> queryNameValues = getQueryNameValues();
        if (queryNameValues.size() > 0) {
            String queryString = getQueryString(queryNameValues);
            if (urlTextField.getText().contains("?")) {
                commandArgs.add(urlTextField.getText() + "&" + queryString);
            } else {
                commandArgs.add(urlTextField.getText() + "?" + queryString);
            }
        } else {
            commandArgs.add(urlTextField.getText());
        }

        commandArgs.add("-M");
        commandArgs.add(methodsCombo.getItemAt(methodsCombo.getSelectedIndex()).toString());

        NameValuePanel headerPanel = (NameValuePanel) parametersPanel.getComponentAt(3);
        Map<String, String> headerNameValues = headerPanel.getNameValues();
        if (headerNameValues.size() > 0) {
            commandArgs.add("-H");
            StringBuilder headerNameValueArgBuilder = new StringBuilder();
            Iterator<Map.Entry<String, String>> iterator = headerNameValues.entrySet().iterator();
            if (iterator.hasNext()) {
                Map.Entry<String, String> nameValueEntry = iterator.next();
                headerNameValueArgBuilder.append(nameValueEntry.getKey()).append(":").append(nameValueEntry.getValue());
                while (iterator.hasNext()) {
                    nameValueEntry = iterator.next();
                    headerNameValueArgBuilder.append(";").append(nameValueEntry.getKey()).append(":").append(nameValueEntry.getValue());
                }
            }
            commandArgs.add(headerNameValueArgBuilder.toString());
        }


        RequestBodyType selectedBodyType = getSelectedBodyType();
        switch (selectedBodyType) {
            case JSON:
                commandArgs.add("-j");
                JsonPanel jsonPanel = (JsonPanel) parametersPanel.getComponentAt(0);
                commandArgs.add(jsonPanel.getJsonContent());
                break;
            case UrlEncoded:
            case FormData:
                NameValuePanel formDataPanel = (NameValuePanel) parametersPanel.getComponentAt(0);
                Map<String, String> nameValues = formDataPanel.getNameValues();
                if (nameValues.size() > 0) {
                    commandArgs.add("-d");
                    StringBuilder nameValueArgBuilder = new StringBuilder();
                    Iterator<Map.Entry<String, String>> iterator = nameValues.entrySet().iterator();
                    if (iterator.hasNext()) {
                        Map.Entry<String, String> nameValueEntry = iterator.next();
                        nameValueArgBuilder.append(nameValueEntry.getKey()).append("=").append(nameValueEntry.getValue());
                        while (iterator.hasNext()) {
                            nameValueEntry = iterator.next();
                            nameValueArgBuilder.append("&").append(nameValueEntry.getKey()).append("=").append(nameValueEntry.getValue());
                        }
                    }
                    commandArgs.add(nameValueArgBuilder.toString());
                }
                break;
            case BinaryData:
                commandArgs.add("--upload");
                RequestBinaryDataPanel requestBinaryDataPanel = (RequestBinaryDataPanel) parametersPanel.getComponentAt(0);
                commandArgs.add(requestBinaryDataPanel.getSelectedFilePath());
                break;
        }

        commandArgs.add("-f");

        return commandArgs;
    }

    /**
     * Gets a map of key-values that contains all query parameters.
     *
     * @return query parameters
     */
    Map<String, String> getQueryNameValues() {
        NameValuePanel queryPanel = (NameValuePanel) parametersPanel.getComponentAt(2);
        return queryPanel.getNameValues();
    }

    /**
     * Gets selected body type.
     *
     * @return selected body type
     */
    RequestBodyType getSelectedBodyType() {
        return requestBodyTypeCombo.getItemAt(requestBodyTypeCombo.getSelectedIndex());
    }

    /**
     * Converts query parameters to query string of the destination url.
     *
     * @param queryNameValues query parameters to convert
     * @return query string of the destination url
     * @throws UnsupportedEncodingException this exception is not happening!
     */
    private String getQueryString(Map<String, String> queryNameValues) throws UnsupportedEncodingException {
        if (queryNameValues == null) {
            return "";
        }
        Iterator<Map.Entry<String, String>> iterator = queryNameValues.entrySet().iterator();
        StringBuilder queryArgBuilder = new StringBuilder();
        if (iterator.hasNext()) {
            Map.Entry<String, String> nameValueEntry = iterator.next();
            queryArgBuilder
                    .append(URLEncoder.encode(nameValueEntry.getKey(), StandardCharsets.UTF_8.toString()))
                    .append("=")
                    .append(URLEncoder.encode(nameValueEntry.getValue(), StandardCharsets.UTF_8.toString()));
            while (iterator.hasNext()) {
                nameValueEntry = iterator.next();
                queryArgBuilder
                        .append("&")
                        .append(URLEncoder.encode(nameValueEntry.getKey(), StandardCharsets.UTF_8.toString()))
                        .append("=")
                        .append(URLEncoder.encode(nameValueEntry.getValue(), StandardCharsets.UTF_8.toString()));
            }
        }
        return queryArgBuilder.toString();
    }

    /**
     * changes panel of request body tab, if request body type changes.
     */
    private void doRequestBodyTypeChangeAction() {
        //if request body type new selected option is another one, change the request body type panel
        if (requestBodyTypeCombo.getSelectedIndex() != requestBodyTypeComboSelectedIndex) {
            //gets user confirm that previous selected body type will be lose.
            int userConfirm = JOptionPane.showConfirmDialog(httpClientGui, "Current body will be lose. Are you sure you want to continue?");
            //if user confirsm, change the panel
            if (userConfirm == JOptionPane.YES_OPTION) {
                requestBodyTypeComboSelectedIndex = requestBodyTypeCombo.getSelectedIndex();
                int tabSelectedIndex = parametersPanel.getSelectedIndex();
                parametersPanel.removeTabAt(tabSelectedIndex);
                parametersPanel.insertTab("", null, getRequestBodyPanel(getSelectedBodyType()), null, tabSelectedIndex);
                parametersPanel.setTabComponentAt(tabSelectedIndex, requestBodyTypeCombo);
                parametersPanel.setSelectedIndex(tabSelectedIndex);
                addProperHeader();
            } else {
                requestBodyTypeCombo.setSelectedIndex(requestBodyTypeComboSelectedIndex);
            }
        }
    }

    /**
     * Adds proper header of content-type, based of the type of selected body type.
     */
    private void addProperHeader() {
        NameValuePanel headerPanel = (NameValuePanel) parametersPanel.getComponentAt(3);
        String contentTypeValue = "";
        switch (getSelectedBodyType()) {
            case FormData:
                contentTypeValue = RequestExecutor.MULTIPART_FORM_DATA;
                break;
            case UrlEncoded:
                contentTypeValue = RequestExecutor.URL_ENCODED;
                break;
            case JSON:
                contentTypeValue = RequestExecutor.JSON;
                break;
            case BinaryData:
                contentTypeValue = RequestExecutor.OCTET_STREAM;
                break;
        }
        headerPanel.addOrUpdateKeyValue(RequestExecutor.CONTENT_TYPE, contentTypeValue);
    }

    /**
     * Save request button action that shows the save request dialog.
     */
    private void doSaveRequest() {
        saveRequestDialog.preview();
    }

    /**
     * Returns request body panel based of selected request body type selected in combo box.
     *
     * @param requestBodyType selected request body type
     * @return request body panel
     */
    private JPanel getRequestBodyPanel(RequestBodyType requestBodyType) {
        switch (requestBodyType) {
            case FormData:
                return new NameValuePanel();
            case UrlEncoded:
                return new NameValuePanel();
            case JSON:
                return new JsonPanel(true);
            case BinaryData:
                return new RequestBinaryDataPanel(httpClientGui);
            default:
                return new JPanel();
        }
    }

    /**
     * Gets an instance of auth panel.
     *
     * @return an instance of auth panel
     */
    private JPanel getAuthPanel() {
        return new AuthPanel();
    }

    /**
     * Gets an instance of name value panel as query panel.
     *
     * @return an instance of query panel
     */
    private JPanel makeQueryPanel() {
        return new NameValuePanel();
    }

    /**
     * Gets an instance of name value panel as header panel.
     *
     * @return an instance of header panel
     */
    private JPanel makeHeaderPanel() {
        return new NameValuePanel();
    }

    /**
     * Loads specified saved request to request panel
     *
     * @param request saved request to load
     */
    void loadRequest(Request request) {
        try {
            urlTextField.setText(removeQueryParams(request.getDestinationAddress(), request.getQueryParams()));

            for (int i = 0; i < methodsCombo.getItemCount(); i++) {
                if (methodsCombo.getItemAt(i).equals(request.getRequestMethod())) {
                    methodsCombo.setSelectedIndex(i);
                }
            }

            NameValuePanel headerPanel = (NameValuePanel) parametersPanel.getComponentAt(3);
            headerPanel.removeAllNameValues();
            if (request.getHeader() != null) {
                for (Map.Entry<String, String> headerEntry : request.getHeader().entrySet()) {
                    headerPanel.addOrUpdateKeyValue(headerEntry.getKey(), headerEntry.getValue());
                }
            }

            NameValuePanel queryPanel = (NameValuePanel) parametersPanel.getComponentAt(2);
            queryPanel.removeAllNameValues();
            if (request.getQueryParams() != null) {
                for (Map.Entry<String, String> entry : request.getQueryParams().entrySet()) {
                    queryPanel.addOrUpdateKeyValue(entry.getKey(), entry.getValue());
                }
            }

            if (RequestBodyType.FormData.equals(request.getRequestBodyType())) {
                requestBodyTypeCombo.setSelectedIndex(0);
                NameValuePanel formDataPanel = (NameValuePanel) parametersPanel.getComponentAt(0);
                formDataPanel.removeAllNameValues();
                if (request.getData() != null) {
                    for (Map.Entry<String, String> entry : request.getData().entrySet()) {
                        formDataPanel.addOrUpdateKeyValue(entry.getKey(), entry.getValue());
                    }
                }
            } else if (RequestBodyType.UrlEncoded.equals(request.getRequestBodyType())) {
                requestBodyTypeCombo.setSelectedIndex(1);
                NameValuePanel formDataPanel = (NameValuePanel) parametersPanel.getComponentAt(0);
                formDataPanel.removeAllNameValues();
                if (request.getData() != null) {
                    for (Map.Entry<String, String> entry : request.getData().entrySet()) {
                        formDataPanel.addOrUpdateKeyValue(entry.getKey(), entry.getValue());
                    }
                }
            }

            if (request.isJson()) {
                JsonPanel jsonPanel = (JsonPanel) parametersPanel.getComponentAt(0);
                jsonPanel.clear();
                requestBodyTypeCombo.setSelectedIndex(2);
                jsonPanel.fill(request.getJsonData());
            }
            if (request.isUpload()) {
                RequestBinaryDataPanel requestBinaryDataPanel = (RequestBinaryDataPanel) parametersPanel.getComponentAt(0);
                requestBinaryDataPanel.clear();
                requestBodyTypeCombo.setSelectedIndex(3);
                requestBinaryDataPanel.setSelectedFilePath(request.getUploadPath());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * removes query parameters from url address for proper viewing.
     *
     * @param destinationAddress url address
     * @param queryParams        query parameters to remove
     * @return url address without query parameters
     * @throws Exception if any problem happens
     */
    private String removeQueryParams(String destinationAddress, Map<String, String> queryParams) throws Exception {
        String queryString = getQueryString(queryParams);
        String result = destinationAddress.replace(queryString, "");
        if (result.endsWith("?") || result.endsWith("&")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }
}
