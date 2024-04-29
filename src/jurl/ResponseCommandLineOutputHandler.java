package jurl;

import httpclient.entity.Request;
import httpclient.entity.Response;
import httpclient.entity.ResponseContentType;

import java.util.List;

/**
 * Output handler to show request execution result in command line interface.
 */
public class ResponseCommandLineOutputHandler implements OutputHandler {
    /**
     * Handles presentation of request execution response in command line interface.
     *
     * @param request  executed request
     * @param response request execution response
     */
    @Override
    public void handleOutput(Request request, Response response) {
        System.out.println("Response Code: " + response.getStatusCode());
        System.out.println("Response Message: " + response.getStatusMessage());
        System.out.println("Response Time: " + response.getTime());
        System.out.println("Response Size: " + response.getDataSize());
        if (request.isShowHeaderResponse()) {
            System.out.println(response.getHeader());
        }
        System.out.println(response.getContentType());
        if (!response.getContentType().equals(ResponseContentType.Picture)) {
            {
                System.out.println(response.getContentStr());
            }
        }
    }

    /**
     * Handles group list presentation in command line interface.
     *
     * @param allGroupNames all group names to show
     */
    @Override
    public void outputGroupNames(List<String> allGroupNames) {
        for (String groupName : allGroupNames) {
            if (groupName.equals("")) {
                System.out.println("\"" + groupName + "\" (Empty for no group requests)");
            } else {
                System.out.println("\"" + groupName + "\"");
            }
        }
    }

    /**
     * Handles presentation of list of saved request of a group in command line interface.
     *
     * @param groupName group name to show saved requests in it
     * @param requests  all saved requests of the group tho show
     */
    @Override
    public void outputSavedRequests(String groupName, List<Request> requests) {
        if (requests == null || requests.isEmpty()) {
            System.out.println("<No Request in Group>");
        } else {
            for (int i = 0; i < requests.size(); i++) {
                Request request = requests.get(i);
                System.out.println((i + 1) + ". " + request.getPrintString());
            }
        }
    }

    /**
     * Handles presentation of help in command line interface.
     *
     * @param helpStr help description to show
     */
    @Override
    public void handleHelp(String helpStr) {
        System.out.println(helpStr);
    }

    /**
     * Handles presentation of result of creating a new group in command line interface.
     *
     * @param groupName new group
     */
    @Override
    public void outputCreateGroup(String groupName) {
        System.out.println(groupName + " created!");
    }
}
