package jurl;

import httpclient.entity.Request;
import httpclient.entity.RequestBodyType;
import httpclient.entity.Response;

import java.util.List;
import java.util.Map;

/**
 * Jurl class is the main logic of the application. It takes the command, either from command line args or from GUI,
 * validates the command, runs the command and if it leads to sending a http request, performs the action and gets the
 * command result (http response or other results) and sends the result to output handler for presentation.
 */
public class Jutl {
    /**
     * output handler of the jurl
     */
    private OutputHandler outputHandler;
    /**
     * saved requests repository instance
     */
    RequestRepository requestRepository = new RequestRepository();
    /**
     * command processor instance
     */
    CommandProcessor commandProcessor = new CommandProcessor();
    /**
     * request executor instance
     */
    RequestExecutor requestExecutor = new RequestExecutor();
    /**
     * file output handler instance
     */
    FileOutputHandler fileOutputHandler = new FileOutputHandler();

    /**
     * Constructor of the jurl that initializes the output handler.
     *
     * @param outputHandler output handler
     */
    public Jutl(OutputHandler outputHandler) {
        this.outputHandler = outputHandler;
    }

    /**
     * Main method of the jurl to execute in command line format.
     *
     * @param args command line arguments for command execution
     */
    public static void main(String[] args) {
        Jutl jutl = new Jutl(new ResponseCommandLineOutputHandler());
        try {
            jutl.run(args);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Runs the specified command with no name or query options. This method used by command line interface to execute
     * commands.
     *
     * @param args command arguments
     * @throws Exception if command was not correct or any problem occurs in executing and saving and outputting
     *                   the command.
     */
    public void run(String[] args) throws Exception {
        run("", null, null, args);
    }

    /**
     * Runs the specified command and keeps track of extra information about how to present the result. This method used
     * by GUI to execute commands.
     *
     * @param requestName     request name to execute
     * @param queryParams     query parameters of request
     * @param requestBodyType request body type
     * @param args            command arguments
     * @throws Exception if command was not correct or any problem occurs in executing and saving and outputting
     *                   the command.
     */
    public void run(String requestName, Map<String, String> queryParams, RequestBodyType requestBodyType, String[] args) throws Exception {
        //loading saved requests repository latest state
        requestRepository.load();

        //creating command object from specified command arguments
        Command command = commandProcessor.createCommand(requestName, queryParams, requestBodyType, args);

        //determining command types and handling each command type
        if (command.isGroupList()) {
            //in the case of listing groups, simply load all groups from repository and send them to show
            outputHandler.outputGroupNames(requestRepository.getAllGroupNames());
        } else if (command.isList()) {
            //in the case of listing requests of a group, load requests of group from repository and send them to show
            outputHandler.outputSavedRequests(command.getGroupName(), requestRepository.getGroupRequests(command.getGroupName()));
        } else if (command.isCreateGroup()) {
            //in the case of creating a group, create it in repository and send it to show
            requestRepository.createGroup(command.getGroupName());
            outputHandler.outputCreateGroup(command.getGroupName());
        } else if (command.isFire()) {
            //in the case of firing list of requests of a group, find them in repository for each request
            //execute it using request executor and handle its file output and finally send them to show
            List<Request> requests = requestRepository.getRequests(command.getGroupName(), command.getFireRequestIndexList());
            for (Request request : requests) {
                Response response = requestExecutor.executeRequest(request);
                fileOutputHandler.handleFileOutput(request, response);
                outputHandler.handleOutput(request, response);
            }
        } else if (command.isHelp()) {
            //in the case of help command, make the help result text and send it to show
            outputHandler.handleHelp(makeHelpStr());
        } else if (command.isSave()) {
            //in the case of a command that has save option, first save it using repository and then
            //execute it using request executor and handle its file output and finally send them to show
            requestRepository.save(command.getRequest().getGroupName(), command.getRequest());
            Response response = requestExecutor.executeRequest(command.getRequest());
            fileOutputHandler.handleFileOutput(command.getRequest(), response);
            outputHandler.handleOutput(command.getRequest(), response);
        } else {
            //otherwise, execute the request using request executor and handle its file output and finally send them to show
            Response response = requestExecutor.executeRequest(command.getRequest());
            fileOutputHandler.handleFileOutput(command.getRequest(), response);
            outputHandler.handleOutput(command.getRequest(), response);
        }
    }

    /**
     * Makes help result text based on supporting commands.
     *
     * @return help result text
     */
    private String makeHelpStr() {
        return "Usage: jurl <utl> [options...]\n" +
                "-M, --method                    Request methods from list \"GET, PUT, DELETE, POST, PATCH\", Default method is GET\n" +
                "-H, --header <header>           Pass custom header(s) to server\n" +
                "-i, --include                   Include protocol response headers in the output\n" +
                "-h, --help                      This help text\n" +
                "-f                              Forward redirect\n" +
                "-O, --output [output_file_name] Outputs response body to a file named [output_file_name] or output_[CurrentDate] for not specified file name\n" +
                "-S, --save <group_name>         Saves the request to request repository to [group_name]\n" +
                "-d, --data <data>               HTTP POST data\n" +
                "-j, --json                      Send message body as a json object\n" +
                "--upload <file>                 Upload file\n" +
                "Usage: jurl create <group_name>\n" +
                "\tCreate a saved request group named <group_name>\n" +
                "Usage: jurl list\n" +
                "\tList all saved request groups\n" +
                "Usage: jurl list <group_name>\n" +
                "\tList all saved requests of <group_name>\n" +
                "Usage: jurl fire <group_name> <request_num_1> [request_num_2...]\n" +
                "\tExecutes saved request in <group_name> specified by numbers <request_num_1> [request_num_2...] one by one\n";
    }
}
