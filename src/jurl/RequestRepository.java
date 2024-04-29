package jurl;

import httpclient.entity.Request;

import java.io.*;
import java.util.*;

/**
 * A repository for loading and saving requests and request groups into file. It serializes a map containing
 * request group names as key and for each request group, a list of requests as value and and saves the byte
 * stream into an specified file.
 */
public class RequestRepository {
    /**
     * saved requests file name
     */
    private static final String FILE_NAME = "saved-requests";
    /**
     * requests map
     */
    private Map<String, List<Request>> savedRequests = new HashMap<>();

    /**
     * Loads all requests and request groups from file and initializes the requests map.
     *
     * @throws Exception if any problem in loading requests file occurs
     */
    public void load() throws Exception {
        File savedRequestsFile = new File(FILE_NAME);
        //checking file existence
        if (savedRequestsFile.exists()) {
            Object readObject;
            try {
                //reading the file
                ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(savedRequestsFile));
                readObject = inputStream.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new Exception("Failed to load requests, " + e.getMessage(), e);
            }
            if (readObject instanceof Map) {
                savedRequests = (Map<String, List<Request>>) readObject;
            } else {
                throw new Exception("Invalid options file!");
            }
        }
    }

    /**
     * Saves a request into a request group. It simply gets request list of the group using group name and adds new
     * request to the list and finally asks for writing new map into file.
     *
     * @param groupName specified group name
     * @param request   request to save
     * @throws Exception if any problem occurs in saving map to file
     */
    public void save(String groupName, Request request) throws Exception {
        if (groupName == null) {
            groupName = "";
        }
        savedRequests.putIfAbsent(groupName, new ArrayList<>());
        savedRequests.get(groupName).add(request);
        flush();
    }

    /**
     * Saves the request in no request group.
     *
     * @param request request to save
     * @throws Exception if any problem occurs
     */
    public void save(Request request) throws Exception {
        save("", request);
    }

    /**
     * Writes the request map into requests file.
     *
     * @throws Exception if any problem occurs in file writing
     */
    private void flush() throws Exception {
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(FILE_NAME));
            outputStream.writeObject(savedRequests);
        } catch (IOException e) {
            throw new Exception("Failed to save requests, " + e.getMessage(), e);
        }
    }

    /**
     * Gets all group names.
     *
     * @return list of all group names
     */
    public List<String> getAllGroupNames() {
        List<String> result = new ArrayList<>();
        for (String groupName : savedRequests.keySet()) {
            if (!groupName.equals("")) {
                result.add(groupName);
            }
        }
        return result;
    }

    /**
     * Gets all requests in a group.
     *
     * @param groupName group name
     * @return list of  requests in the specified group
     */
    public List<Request> getGroupRequests(String groupName) {
        return savedRequests.getOrDefault(groupName, new ArrayList<>());
    }

    /**
     * Creates a new request group.
     *
     * @param groupName new request group
     * @throws Exception if any problem occurs in file writing
     */
    public void createGroup(String groupName) throws Exception {
        if (savedRequests.containsKey(groupName)) {
            throw new Exception("Group name \"" + groupName + "\" already exists!");
        } else {
            savedRequests.putIfAbsent(groupName, new ArrayList<>());
            flush();
        }
    }

    /**
     * Finds a list of request in specified group using given request indexes.
     *
     * @param groupName            group name
     * @param fireRequestIndexList request indexes to finf
     * @return list of found requests
     * @throws Exception if request indexes are invalid
     */
    public List<Request> getRequests(String groupName, Set<Integer> fireRequestIndexList) throws Exception {
        if (!savedRequests.containsKey(groupName)) {
            throw new Exception("Group with name \"" + groupName + "\" not found!");
        }
        List<Request> result = new ArrayList<>();
        List<Request> groupRequests = savedRequests.get(groupName);
        for (Integer idx : fireRequestIndexList) {
            if (groupRequests == null || idx < 0 || idx >= groupRequests.size()) {
                throw new Exception("Invalid request number " + (idx + 1));
            } else {
                result.add(groupRequests.get(idx));
            }
        }
        return result;
    }
}
