package httpclient.gui;

import httpclient.entity.Request;
import httpclient.entity.Response;
import jurl.OutputHandler;

import java.util.List;

/**
 * Output handler to show request execution result in GUI form.
 */
public class GuiOutputHandler implements OutputHandler {
    /**
     * GUI object that data should be presented in
     */
    private HttpClientGui gui;

    /**
     * Constructor for GUI output handler
     *
     * @param gui GUI object
     */
    public GuiOutputHandler(HttpClientGui gui) {
        this.gui = gui;
    }

    /**
     * Handles presentation of request execution response in GUI.
     *
     * @param request  executed request
     * @param response request execution response
     */
    @Override
    public void handleOutput(Request request, Response response) {
        gui.showResponse(request, response);
    }

    /**
     * Handles presentation of help in GUI.
     *
     * @param helpStr help description to show
     */
    @Override
    public void handleHelp(String helpStr) {
        gui.setHelpStr(helpStr);
    }

    /**
     * Handles group list presentation in GUI.
     *
     * @param allGroupNames all group names to show
     */
    @Override
    public void outputGroupNames(List<String> allGroupNames) {
        gui.addAllGroups(allGroupNames);
    }

    /**
     * Handles presentation of list of saved request of a group in GUI.
     *
     * @param groupName     group name to show saved requests in it
     * @param groupRequests all saved requests of the group tho show
     */
    @Override
    public void outputSavedRequests(String groupName, List<Request> groupRequests) {
        gui.addAllRequestsToGroup(groupName, groupRequests);
    }

    /**
     * Handles presentation of result of creating a new group in GUI.
     *
     * @param groupName new group
     */
    @Override
    public void outputCreateGroup(String groupName) {
        gui.addGroup(groupName);
    }
}
