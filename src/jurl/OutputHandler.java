package jurl;

import httpclient.entity.Request;
import httpclient.entity.Response;

import java.util.List;

/**
 * Output handler interface to connect request execution system to any kind of user interface.
 */
public interface OutputHandler {
    /**
     * Handles presentation of request execution response.
     *
     * @param request  executed request
     * @param response request execution response
     */
    void handleOutput(Request request, Response response);

    /**
     * Handles presentation of help.
     *
     * @param helpStr help description to show
     */
    void handleHelp(String helpStr);

    /**
     * Handles group list presentation.
     *
     * @param allGroupNames all group names to show
     */
    void outputGroupNames(List<String> allGroupNames);

    /**
     * Handles presentation of list of saved request of a group.
     *
     * @param groupName     group name to show saved requests in it
     * @param groupRequests all saved requests of the group tho show
     */
    void outputSavedRequests(String groupName, List<Request> groupRequests);

    /**
     * Handles presentation of result of creating a new group.
     *
     * @param groupName new group
     */
    void outputCreateGroup(String groupName);
}
