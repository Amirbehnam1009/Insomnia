package httpclient.gui;

import httpclient.entity.Request;
import httpclient.entity.RequestBodyType;
import httpclient.entity.RequestGroup;
import httpclient.entity.Response;
import httpclient.gui.theme.ThemeType;
import httpclient.repository.OptionsRepository;
import jurl.Jutl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Http client GUI, a frame that will be shown as main window when program starts. It composed of three panels, named
 * saves requests panel and request panel and response panel. Also it has a menu bar that contains some actions.
 */
public class HttpClientGui extends JFrame {
    /**
     * determines full screen mode of frame
     */
    private boolean fullScreen = false;
    /**
     * holds frame size before full screen, it's been used when frame returns back from full screen mode.
     */
    private Dimension beforeFullScreenDimension;
    /**
     * holds location of split pane before toggling to retrieve it again
     */
    private int splitPaneLocation;

    /**
     * an split pane for content
     */
    private JSplitPane contentSplitPane;
    /**
     * instance of saved request panel (the left side panel)
     */
    private SavedRequestsPanel savedRequestsPanel;
    /**
     * instance of request panel (the middle panel)
     */
    private RequestPanel requestPanel;
    /**
     * instance of the response panel (the right side panel)
     */
    private ResponsePanel responsePanel;

    /**
     * options dialog that will be shown when Application|Options menu item selected or ctrl+o pressed
     */
    private OptionsDialog optionsDialog;
    /**
     * about dialog that will be shown when Help|About menu item selected or ctrl+a pressed
     */
    private AboutDialog aboutDialog;
    /**
     * help dialog that will be shown when Help|Help menu item selected or F1 pressed
     */
    private HelpDialog helpDialog;

    /**
     * instance of options repository for load and save options into file
     */
    private OptionsRepository optionsRepository;
    /**
     * and instance of options holder
     */
    private OptionsRepository.OptionsModel optionsModel;
    /**
     * command executor object
     */
    private Jutl jurl;
    /**
     * current loaded request
     */
    private Request loadedRequest;
    /**
     * current saved request name
     */
    private String savedRequestName;

    /**
     * Http client GUI constructor that initializes variables and makes frame UI.
     *
     * @param optionsRepository options repository for load and save options into file
     * @throws Exception If options file can't be loaded
     */
    public HttpClientGui(OptionsRepository optionsRepository) throws Exception {
        this.optionsRepository = optionsRepository;
        //load options model from repository
        this.optionsModel = optionsRepository.load();
        initUi();
    }

    /**
     * Initializes http client GUI.
     */
    private void initUi() {
        //initializing menus dialogs
        optionsDialog = new OptionsDialog(this, optionsModel);
        aboutDialog = new AboutDialog(this);
        helpDialog = new HelpDialog(this);

        //add a menu bar
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createApplicationMenu());
        menuBar.add(createViewMenu());
        menuBar.add(createHelpMenu());
        setJMenuBar(menuBar);

        //make three parts of frame using two split panes
        savedRequestsPanel = new SavedRequestsPanel(this);
        requestPanel = new RequestPanel(this);
        responsePanel = new ResponsePanel(this);
        JSplitPane requestSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, requestPanel, responsePanel);
        contentSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, savedRequestsPanel, requestSplitPane);
        add(contentSplitPane);

        //packing and setting size and title and icon and location
        pack();
        beforeFullScreenDimension = new Dimension(1100, 600);
        setSize(beforeFullScreenDimension);
        setTitle("Smart HTTP Client");
        setIconImage(new ImageIcon("icon.png").getImage());
        setLocationRelativeTo(null);

        //setting exit operation based on options
        if (optionsModel.isSystemTray()) {
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        } else {
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        }

        //adding listener for close operation, if options set to "go to system tray" changes the close operation
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                if (optionsModel.isSystemTray()) {
                    goToSystemTray();
                }
            }
        });
    }

    @Override
    public void setVisible(boolean b) {
        try {
            sendRequest(new String[]{"list"});
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        super.setVisible(b);
    }

    /**
     * Creates and returns Application menu that contains two menu items: Options and exit
     *
     * @return created application menu
     */
    private JMenu createApplicationMenu() {
        //creating menu
        JMenu applicationMenu = new JMenu("Application");
        applicationMenu.setMnemonic(KeyEvent.VK_A);

        //adding options menu item
        JMenuItem optionsMenuItem = new JMenuItem("Options", KeyEvent.VK_O);
        optionsMenuItem.setAccelerator(KeyStroke.getKeyStroke("control O"));
        applicationMenu.add(optionsMenuItem);

        //adding exit menu item
        JMenuItem exitMenuItem = new JMenuItem("Exit", KeyEvent.VK_X);
        exitMenuItem.setAccelerator(KeyStroke.getKeyStroke("alt X"));
        applicationMenu.add(exitMenuItem);

        //adding action listener for options menu item to show options dialog when selected
        optionsMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                optionsDialog.showOptions();
            }
        });
        //adding action listener for exit menu item to do exit operation of window
        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doExitMenuAction();
            }
        });

        return applicationMenu;
    }

    /**
     * Creates and returns View menu that contains two menu items: Toggle Full Screen and Toggle Sidebar
     *
     * @return created view menu
     */
    private JMenu createViewMenu() {
        //creating view menu
        JMenu viewMenu = new JMenu("View");
        viewMenu.setMnemonic(KeyEvent.VK_V);

        //adding Toggle Full Screen menu item
        JMenuItem toggleFullScreenMenuItem = new JMenuItem("Toggle Full Screen", KeyEvent.VK_F);
        toggleFullScreenMenuItem.setAccelerator(KeyStroke.getKeyStroke("F11"));
        viewMenu.add(toggleFullScreenMenuItem);

        //adding Toggle Sidebar menu item
        JMenuItem toggleSidebarMenuItem = new JMenuItem("Toggle Sidebar", KeyEvent.VK_S);
        toggleSidebarMenuItem.setAccelerator(KeyStroke.getKeyStroke("control S"));
        viewMenu.add(toggleSidebarMenuItem);

        //adding action listener for Toggle Sidebar menu item to toggle frame view between simple and full screen mode
        toggleFullScreenMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doToggleFullScreenAction();
            }
        });
        //adding action listener for Toggle Sidebar menu item to show and hide saved requests panel
        toggleSidebarMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doToggleSidebarMenuItemAction();
            }
        });

        return viewMenu;
    }

    /**
     * Creates and returns Help menu that contains two menu items: About and Help
     *
     * @return created view menu
     */
    private JMenu createHelpMenu() {
        //creating help menu
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);

        //adding About menu item
        JMenuItem aboutMenuItem = new JMenuItem("About", KeyEvent.VK_A);
        aboutMenuItem.setAccelerator(KeyStroke.getKeyStroke("control A"));
        helpMenu.add(aboutMenuItem);

        //adding Help menu item
        JMenuItem helpMenuItem = new JMenuItem("Help", KeyEvent.VK_H);
        helpMenuItem.setAccelerator(KeyStroke.getKeyStroke("F1"));
        helpMenu.add(helpMenuItem);

        //adding action listener for About menu item to show about dialog
        aboutMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                aboutDialog.setVisible(true);
            }
        });
        //adding action listener for Help menu item to show help dialog
        helpMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doHelpAction();
            }
        });

        return helpMenu;
    }

    /**
     * At the case of selecting of help menu, creates help command and asks for its execution.
     */
    private void doHelpAction() {
        try {
            sendRequest(new String[]{"-h"});
            helpDialog.setVisible(true);
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Calls for window close action.
     */
    private void doExitMenuAction() {
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    /**
     * Toggles window view between simple and full screen mode.
     */
    private void doToggleFullScreenAction() {
        dispose();
        fullScreen = !fullScreen;
        preview();
    }

    /**
     * Previews frame using defined mode. If mode is full screen, it uses {@link GraphicsDevice} to show in full screen
     * mode and otherwise simply shows the frame.
     */
    private void preview() {
        if (fullScreen) {
            beforeFullScreenDimension = getSize();
            setUndecorated(true);
            setResizable(false);
            setExtendedState(JFrame.MAXIMIZED_BOTH);
        } else {
            setUndecorated(false);
            pack();
            setSize(beforeFullScreenDimension);
            setResizable(true);
            setLocationRelativeTo(null);
        }
        setVisible(true);
    }

    /**
     * Toggles side bare frame that contains saved requests between show and hide.
     */
    private void doToggleSidebarMenuItemAction() {
        if (contentSplitPane.getLeftComponent().isVisible()) {
            splitPaneLocation = contentSplitPane.getDividerLocation();
            contentSplitPane.setDividerSize(0);
            contentSplitPane.getLeftComponent().setVisible(false);
        } else {
            contentSplitPane.getLeftComponent().setVisible(true);
            contentSplitPane.setDividerLocation(splitPaneLocation);
            contentSplitPane.setDividerSize((Integer) UIManager.get("SplitPane.dividerSize"));
        }
    }

    /**
     * puts the program into the system tray using a tray icon.
     */
    private void goToSystemTray() {
        //check for system tray support
        if (SystemTray.isSupported()) {
            //creating tray icon
            TrayIcon trayIcon = new TrayIcon(getIconImage());
            trayIcon.setImageAutoSize(true);
            SystemTray tray = SystemTray.getSystemTray();

            //adding a double click listener for tray icon to recover the program from system tray
            trayIcon.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        preview();
                        tray.remove(trayIcon);
                    }
                }
            });
            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                System.out.println("TrayIcon could not be added.");
            }
        }
    }

    /**
     * Saves the options that comes from options dialog and returns new options model.
     *
     * @param followRedirect follow redirect option
     * @param systemTray     system tray enabling option
     * @param themeType      theme type option
     * @return new options model
     * @throws Exception If the options repository can't save the new option to file
     */
    OptionsRepository.OptionsModel doSaveOptions(boolean followRedirect, boolean systemTray, ThemeType themeType) throws Exception {
        optionsModel = optionsRepository.save(followRedirect, systemTray, themeType.getCode());
        if (optionsModel.isSystemTray()) {
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        } else {
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        }
        return optionsModel;
    }

    /**
     * Creates a new request group and adds it to the saved requests tree.
     *
     * @param name new group name
     * @throws Exception If group name is invalid
     */
    void createGroup(String name) throws Exception {
        //checking [Create Group] name that is reserved keyword to create new group while saving a new request.
        if (name.equals("[Create Group]")) {
            throw new Exception("Invalid group name \"" + name + "\".");
        }
        sendRequest(new String[]{"create", name});
    }

    /**
     * Returns all request group names.
     *
     * @return all request group names
     */
    List<String> getAllGroupNames() {
        List<String> groupNames = new ArrayList<>();
        for (RequestGroup requestGroup : savedRequestsPanel.getAllGroups()) {
            groupNames.add(requestGroup.toString());
        }
        return groupNames;
    }

    /**
     * Saves a request in specified request group name, or in top level if group name is null. It also creates a new
     * request group if user selects the [Create Group] option.
     *
     * @param name      new saved request name
     * @param groupName request group name, or empty string for no group selected, or [Create Group] for creating
     *                  new request group and adding new request to the created group
     * @throws Exception If user selects [Create Group] option and new group name is invalid
     */
    void saveRequest(String name, String groupName) throws Exception {
        //if group name is an empty string, request should belong to no group
        if (groupName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Invalid group name!", "Error", JOptionPane.ERROR_MESSAGE);
        } else {

            //if [Create Group] option was selected, create a new group
            if (groupName.equals(SaveRequestDialog.CREATE_GROUP)) {
                //asking user for new group name using an input dialog
                groupName = JOptionPane.showInputDialog(this, "New group name:", SaveRequestDialog.CREATE_GROUP, JOptionPane.PLAIN_MESSAGE);
                //if user cancels the input dialog, everything is canceled
                if (groupName == null) {
                    return;
                }
                createGroup(groupName);
            }
            if (savedRequestsPanel.existsRequestInGroup(name, groupName)) {
                throw new Exception("Request name \"" + name + "\" already exists in group \"" + groupName + "\"");
            } else {
                savedRequestName = name;
                List<String> args = requestPanel.collectCommandArgs();
                args.add("-S");
                args.add(groupName);
                sendRequest(name, requestPanel.getQueryNameValues(), requestPanel.getSelectedBodyType(), args.toArray(new String[0]));
            }
        }
    }

    /**
     * Loads a saved request into the request panel.
     *
     * @param request request to load
     */
    void loadRequest(Request request) {
        int userConfirm = JOptionPane.showConfirmDialog(this, "Current request will be lose. Are you sure you want to continue?");
        if (userConfirm == JOptionPane.YES_OPTION) {
            loadedRequest = request;
            requestPanel.loadRequest(request);
        }
    }

    /**
     * sends a command for execution.
     *
     * @param command command to execute
     * @throws Exception if any problem occurs in request execution
     */
    private void sendRequest(String[] command) throws Exception {
        sendRequest("", null, null, command);
    }

    /**
     * sends a command for execution and keeps track of extra data for possible saving request and execution.
     *
     * @param command         command to execute
     * @param queryParams     query parameters of request
     * @param requestBodyType request body type
     */
    void sendRequest(String[] command, Map<String, String> queryParams, RequestBodyType requestBodyType) {
        try {
            if (loadedRequest != null) {
                sendRequest(loadedRequest.getName(), queryParams, requestBodyType, command);
            } else {
                sendRequest("", null, null, command);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * sends a command with specified request name for execution and keeps track of extra data for possible saving
     * request and execution.
     *
     * @param requestName     request name to execute
     * @param command         command to execute
     * @param queryParams     query parameters of request
     * @param requestBodyType request body type
     * @throws Exception if any problem occurs in request execution
     */
    private void sendRequest(String requestName, Map<String, String> queryParams, RequestBodyType requestBodyType, String[] command) throws Exception {
        requestPanel.disableActions();
        try {
            jurl.run(requestName, queryParams, requestBodyType, command);
        } catch (Exception e) {
            requestPanel.enableActions();
            throw e;
        }
    }

    /**
     * Sets command executor object.
     *
     * @param jurl command executor object
     */
    public void setJurl(Jutl jurl) {
        this.jurl = jurl;
    }

    /**
     * For response to help command execution, sets help result text.
     *
     * @param helpStr help result text
     */
    public void setHelpStr(String helpStr) {
        requestPanel.enableActions();
        helpDialog.setContentStr(helpStr);
    }

    /**
     * For response to execution of a command that leads to sending a request and receiving a response,
     * shows the response in response panel.
     *
     * @param request  sent request
     * @param response response to show
     */
    public void showResponse(Request request, Response response) {
        requestPanel.enableActions();
        if (request.getName().equals(savedRequestName)) {
            //finding the group and adding new request to found group
            for (RequestGroup requestGroup : savedRequestsPanel.getAllGroups()) {
                if (requestGroup.toString().equals(request.getGroupName())) {
                    savedRequestsPanel.addRequestItemToGroup(request, requestGroup);
                }
            }
        }
        responsePanel.fill(response);
    }

    /**
     * Adds a list of groups to saved requests group hierarchy and asks for execution of list of requests in each group.
     *
     * @param allGroupNames all groups to add
     */
    public void addAllGroups(List<String> allGroupNames) {
        requestPanel.enableActions();
        for (String groupName : allGroupNames) {
            try {
                addGroup(groupName);
                sendRequest(new String[]{"list", groupName});
            } catch (Exception e) {
                //not possible
            }
        }
    }

    /**
     * Adds a list of request to a group in saved requests hierarchy.
     *
     * @param groupName     group name
     * @param groupRequests all requests to add in group
     */
    public void addAllRequestsToGroup(String groupName, List<Request> groupRequests) {
        requestPanel.enableActions();
        for (RequestGroup requestGroup : savedRequestsPanel.getAllGroups()) {
            if (requestGroup.toString().equals(groupName)) {
                for (Request request : groupRequests) {
                    savedRequestsPanel.addRequestItemToGroup(request, requestGroup);
                }
            }
        }
    }

    /**
     * Adds a group to saved requests hierarchy.
     *
     * @param groupName group to add
     */
    public void addGroup(String groupName) {
        requestPanel.enableActions();
        RequestGroup requestGroup = new RequestGroup(groupName);
        savedRequestsPanel.addGroup(requestGroup);
    }
}
