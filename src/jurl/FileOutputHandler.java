package jurl;

import httpclient.entity.Request;
import httpclient.entity.Response;
import httpclient.entity.ResponseContentType;

import java.io.*;

/**
 * Handles the output option that writes response text and binary content to a output file based of the response content
 * type. The file name either specified in command or will be generated if not specified.
 */
public class FileOutputHandler {
    /**
     * Handles the output option that writes response text and binary content to a output file based of the response
     * content type. The file name either specified in command or will be generated if not specified.
     *
     * @param request request
     * @param response response of request execution
     * @throws Exception if any problem exists in file creation and writing
     */
    public void handleFileOutput(Request request, Response response) throws Exception {
        File file;
        if (request.isOutput()) {
            if (request.isOutputNameProvided()) {
                file = new File(request.getOutputName());
            } else {
                file = new File("output" + "_" + System.currentTimeMillis());
            }
            //if response content was picture, uses output streams
            if (response.getContentType().equals(ResponseContentType.Picture)) {
                try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                    BufferedOutputStream br1 = new BufferedOutputStream(fileOutputStream);
                    br1.write(response.getContentBytes());
                    br1.flush();
                } catch (FileNotFoundException e) {
                    throw new Exception("File " + request.getOutputName() + " Not Found");
                } catch (IOException e) {
                    throw new Exception("Fail to save output");
                }
            } else { //use writer otherwise
                try (FileWriter fileWriter = new FileWriter(file)) {
                    BufferedWriter br1 = new BufferedWriter(fileWriter);
                    br1.write(response.getContentStr());
                    br1.flush();
                } catch (IOException e) {
                    throw new Exception("Fail to save output");
                }
            }
        }
    }
}

