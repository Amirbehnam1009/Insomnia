package jurl;

import httpclient.entity.Request;
import httpclient.entity.RequestBodyType;
import httpclient.entity.RequestMethod;
import httpclient.util.JsonValidator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Processes the command and creates command object. The process contains validating input arguments, saving extra info
 * and creating command and request instances.
 */
public class CommandProcessor {
    /**
     * Processes the command and creates command object. The process contains validating input arguments,
     * saving extra info and creating command and request instances.
     *
     * @param requestName     request display name, if was a saved request
     * @param queryParams     request query parameters map
     * @param requestBodyType request body type
     * @param commandArray    command arguments
     * @return command object
     * @throws Exception if command arguments is not valid or other problem happens
     */
    public Command createCommand(String requestName, Map<String, String> queryParams, RequestBodyType requestBodyType, String[] commandArray) throws Exception {
        //if command args was empty, its not valid
        if (commandArray.length <= 0) {
            throw new Exception("Invalid command ");
        }
        Command resultCommand = new Command();
        Request request;

        //finds command type
        switch (commandArray[0]) {
            case "create":
                //create command should have group name
                resultCommand.setCreateGroup(true);
                if (commandArray.length <= 1) {
                    throw new Exception("Group name n create command not found");
                } else if (commandArray.length > 2) {
                    throw new Exception("Invalid command format");
                } else {
                    resultCommand.setGroupName(commandArray[1]);
                    return resultCommand;
                }
            case "list":
                //if list command has no arguments, its group list. But if an argument exists, its requests of a group list
                if (commandArray.length == 1) {
                    resultCommand.setGroupList(true);
                    return resultCommand;
                } else if (commandArray.length == 2) {
                    resultCommand.setList(true);
                    resultCommand.setGroupName(commandArray[1]);
                    return resultCommand;
                } else {
                    throw new Exception("Invalid command format");
                }
            case "fire":
                //fire command should have a group name and at least one request number to execute
                if (commandArray.length <= 1) {
                    throw new Exception("Group name n create command not found");
                } else {
                    resultCommand.setGroupName(commandArray[1]);
                    if (commandArray.length <= 2) {
                        throw new Exception("Saved requests to fire not found");
                    } else {
                        //validating command numbers
                        resultCommand.setFire(true);
                        Set<Integer> fireRequestIndexList = new HashSet<>();
                        for (int i = 2; i < commandArray.length; i++) {
                            try {
                                int savedRequestNumber = Integer.parseInt(commandArray[i]);
                                if (savedRequestNumber < 1) {
                                    throw new Exception("Invalid saved request number " + commandArray[i]);
                                } else {
                                    fireRequestIndexList.add(savedRequestNumber - 1);
                                }
                            } catch (NumberFormatException e) {
                                throw new Exception("Invalid saved request number " + commandArray[i]);
                            }
                        }
                        resultCommand.setFireRequestIndexList(fireRequestIndexList);
                        return resultCommand;
                    }
                }
            case "-h":
            case "--help":
                resultCommand.setHelp(true);
                return resultCommand;
            default:
                //if none of the above commands, it should be a request.
                if (!commandArray[0].startsWith("http://")) {
                    commandArray[0] = "http://" + commandArray[0];
                }
                request = new Request(requestName, commandArray[0]);
                request.setQueryParams(queryParams);
                request.setRequestBodyType(requestBodyType);
                resultCommand.setRequest(request);
        }

        boolean method = false;
        boolean header = false;
        boolean showHeaderResponse = false;
        boolean redirect = false;
        boolean output = false;
        boolean save = false;
        boolean data = false;
        boolean json = false;
        boolean upload = false;

        //finding other options of request
        if (commandArray.length > 1) {
            for (int i = 1; i < commandArray.length; i++) {
                if ((commandArray[i].equals("-M") || commandArray[i].equals("--method"))) {
                    //http method option
                    if (method) {
                        throw new Exception("must use method parameter at most once.");
                    }
                    if (i + 1 < commandArray.length) {
                        if (commandArray[i + 1].equals("GET") || commandArray[i + 1].equals("POST") || commandArray[i + 1].equals("HEAD")
                                || commandArray[i + 1].equals("PUT") || commandArray[i + 1].equals("DELETE") || commandArray[i + 1].equals("CONNECT")
                                || commandArray[i + 1].equals("OPTIONS") || commandArray[i + 1].equals("TRACE") || commandArray[i + 1].equals("PATCH")) {
                            method = true;
                            request.setRequestMethod(RequestMethod.valueOf(commandArray[i + 1]));
                            i++;
                        } else {
                            throw new Exception(" Invalid method parameter value.");

                        }
                    } else {
                        throw new Exception(" method parameter value not found.");
                    }
                } else if ((commandArray[i].equals("-H") || commandArray[i].equals("--headers"))) {
                    //header option
                    if (header) {
                        throw new Exception("must use header parameter at most once.");
                    }
                    if (i + 1 < commandArray.length) {
                        header = true;
                        //splitting name-values of header
                        Map<String, String> headerMap = new HashMap<>();
                        String[] headerNameValues = commandArray[i + 1].split(";");
                        for (String headerNameValue : headerNameValues) {
                            String[] keyValuePair = headerNameValue.split(":");
                            headerMap.put(keyValuePair[0].trim(), keyValuePair[1].trim());
                        }
                        request.setHeader(headerMap);
                        i++;
                    } else {
                        throw new Exception(" header parameter value not found.");
                    }
                } else if ((commandArray[i].equals("-i")) || (commandArray[i].equals("--include"))) {
                    //include response header option
                    if (showHeaderResponse) {
                        throw new Exception("must use show Header Response (i) parameter at most once.");
                    } else {
                        showHeaderResponse = true;
                        request.setShowHeaderResponse(true);
                    }
                } else if ((commandArray[i].equals("-f"))) {
                    //follow redirect option
                    if (redirect) {
                        throw new Exception("must use redirect parameter at most once.");
                    } else {
                        redirect = true;
                        request.setRedirect(true);
                    }
                } else if ((commandArray[i].equals("-O") || (commandArray[i].equals("--output")))) {
                    //output to file option
                    if (output) {
                        throw new Exception("must use output parameter at most once.");
                    } else {
                        output = true;
                        request.setOutput(true);
                        if (i + 1 < commandArray.length) {
                            if (!commandArray[i + 1].startsWith("-")) {
                                request.setOutputName(commandArray[i + 1]);
                                request.setOutputNameProvided(true);
                                i++;
                            }
                        }
                    }
                } else if ((commandArray[i].equals("-S") || (commandArray[i].equals("--save")))) {
                    //save option
                    if (save) {
                        throw new Exception("must use save parameter at most once.");
                    } else {
                        save = true;
                        resultCommand.setSave(true);
                        if (i + 1 < commandArray.length) {
                            request.setGroupName(commandArray[i + 1]);
                            i++;
                        } else {
                            throw new Exception("Group name not found");
                        }
                    }
                } else if ((commandArray[i].equals("-d") || (commandArray[i].equals("--data")))) {
                    //data option
                    if (data || upload || json) {
                        throw new Exception("must use one of data, upload, json  parameter at most once.");
                    } else {
                        if (i + 1 < commandArray.length) {
                            data = true;
                            Map<String, String> dataMap = new HashMap<>();
                            String[] dataNameValues = commandArray[i + 1].split("&");
                            for (String dataNameValue : dataNameValues) {
                                String[] keyValuePair = dataNameValue.split("=");
                                dataMap.put(keyValuePair[0].trim(), keyValuePair[1].trim());
                            }
                            request.setData(dataMap);
                            i++;
                        }
                    }
                } else if ((commandArray[i].equals("-j") || (commandArray[i].equals("--json")))) {
                    //json input option
                    if (data || upload || json) {
                        throw new Exception("must use one of data, upload, json  parameter at most once.");
                    } else {
                        if (i + 1 < commandArray.length) {
                            if (JsonValidator.isValidJson(commandArray[i + 1])) {
                                json = true;
                                request.setJson(true);
                                request.setJsonData(commandArray[i + 1]);
                                i++;
                            } else {
                                throw new Exception("json invalid format");
                            }
                        }
                    }
                } else if ((commandArray[i].equals("--upload"))) {
                    //upload file option
                    if (data || upload || json) {
                        throw new Exception("must use one of data, upload, json  parameter at most once.");
                    } else {
                        if (i + 1 < commandArray.length) {
                            upload = true;
                            request.setUpload(true);
                            request.setUploadPath(commandArray[i + 1]);
                            i++;
                        } else {
                            throw new Exception(" direction not found");
                        }
                    }
                } else {
                    throw new Exception(" invalid parameter: " + commandArray[i]);
                }
            }

        }
        return resultCommand;
    }
}
