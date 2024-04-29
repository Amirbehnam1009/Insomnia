package httpclient.entity;

import java.io.Serializable;
import java.util.Map;

/**
 * Holds a request data
 */
public class Request implements Serializable {
    /**
     * request name
     */
    private String name;
    /**
     * http request method
     */
    private RequestMethod requestMethod = RequestMethod.GET;
    /**
     * url address to send request
     */
    private String destinationAddress;
    /**
     * a key-value map for http header
     */
    private Map<String, String> header;
    /**
     * determines redirect state in http request
     */
    private boolean redirect;
    /**
     * a key-value map for form data
     */
    private Map<String, String> data;
    /**
     * json data content
     */
    private String jsonData;
    /**
     * determines json input state for http request
     */
    private boolean json;
    /**
     * determines upload file state
     */
    private boolean upload;
    /**
     * file path for upload
     */
    private String uploadPath;
    /**
     * determines output state
     */
    private boolean output;
    /**
     * determines if output file name provided or not
     */
    private boolean outputNameProvided;
    /**
     * output file name
     */
    private String outputName;
    /**
     * determines show headers in response state
     */
    private boolean showHeaderResponse;
    /**
     * group name of the request
     */
    private String groupName = "";
    /**
     * a key-value map for query parameters
     */
    private Map<String, String> queryParams;
    /**
     * determines request body type
     */
    private RequestBodyType requestBodyType;

    /**
     * Constructor of request.
     *
     * @param name               request name
     * @param destinationAddress destination url address of the request
     */
    public Request(String name, String destinationAddress) {
        this.name = name;
        this.destinationAddress = destinationAddress;
    }

    /**
     * Gets show headers response state.
     *
     * @return show headers response state
     */
    public boolean isShowHeaderResponse() {
        return showHeaderResponse;
    }

    /**
     * Sets show headers response state.
     *
     * @param showHeaderResponse show headers response state
     */
    public void setShowHeaderResponse(boolean showHeaderResponse) {
        this.showHeaderResponse = showHeaderResponse;
    }

    /**
     * Gets output state.
     *
     * @return output state
     */
    public boolean isOutput() {
        return output;
    }

    /**
     * Sets output state.
     *
     * @param output output state
     */
    public void setOutput(boolean output) {
        this.output = output;
    }

    /**
     * Gets output file name provided or not.
     *
     * @return output file name provided or not
     */
    public boolean isOutputNameProvided() {
        return outputNameProvided;
    }

    /**
     * Sets output file name provided or not.
     *
     * @param outputNameProvided output file name provided or not
     */
    public void setOutputNameProvided(boolean outputNameProvided) {
        this.outputNameProvided = outputNameProvided;
    }

    /**
     * Gets output file name.
     *
     * @return output file name
     */
    public String getOutputName() {
        return outputName;
    }

    /**
     * Sets output file name.
     *
     * @param outputName output file name
     */
    public void setOutputName(String outputName) {
        this.outputName = outputName;
    }

    /**
     * Gets request name.
     *
     * @return request name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets request name.
     *
     * @param name request name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets http request method.
     *
     * @return http request method
     */
    public RequestMethod getRequestMethod() {
        return requestMethod;
    }

    /**
     * Sets http request method.
     *
     * @param requestMethod http request method
     */
    public void setRequestMethod(RequestMethod requestMethod) {
        this.requestMethod = requestMethod;
    }

    /**
     * Gets url address to send request.
     *
     * @return url address to send request
     */
    public String getDestinationAddress() {
        return destinationAddress;
    }

    /**
     * Gets http header.
     *
     * @return http header
     */
    public Map<String, String> getHeader() {
        return header;
    }

    /**
     * Sets http header.
     *
     * @param header http header
     */
    public void setHeader(Map<String, String> header) {
        this.header = header;
    }

    /**
     * Gets redirect state in http request.
     *
     * @return redirect state in http request
     */
    public boolean isRedirect() {
        return redirect;
    }

    /**
     * Sets redirect state in http request.
     *
     * @param redirect redirect state in http request
     */
    public void setRedirect(boolean redirect) {
        this.redirect = redirect;
    }

    /**
     * Gets form data.
     *
     * @return form data
     */
    public Map<String, String> getData() {
        return data;
    }

    /**
     * Sets form data.
     *
     * @param data form data
     */
    public void setData(Map<String, String> data) {
        this.data = data;
    }

    /**
     * Gets json data content.
     *
     * @return json data content
     */
    public String getJsonData() {
        return jsonData;
    }

    /**
     * Sets json data content.
     *
     * @param jsonData json data content
     */
    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }

    /**
     * Gets json input state for http request.
     *
     * @return json input state for http request
     */
    public boolean isJson() {
        return json;
    }

    /**
     * Sets json input state for http request.
     *
     * @param json json input state for http request
     */
    public void setJson(boolean json) {
        this.json = json;
    }

    /**
     * Gets upload file state.
     *
     * @return upload file state
     */
    public boolean isUpload() {
        return upload;
    }

    /**
     * Sets upload file state.
     *
     * @param upload upload file state
     */
    public void setUpload(boolean upload) {
        this.upload = upload;
    }

    /**
     * Gets file path for upload.
     *
     * @return file path for upload
     */
    public String getUploadPath() {
        return uploadPath;
    }

    /**
     * Sets file path for upload.
     *
     * @param uploadPath file path for upload
     */
    public void setUploadPath(String uploadPath) {
        this.uploadPath = uploadPath;
    }

    /**
     * Gets group name of the request.
     *
     * @return group name of the request
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * Sets group name of the request.
     *
     * @param groupName group name of the request
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    /**
     * Returns a string representation of the request.
     *
     * @return a string representation of the request
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Returns print string of the request used in command line presentation.
     *
     * @return print string
     */
    public String getPrintString() {
        StringBuilder printStringBuilder = new StringBuilder();
        printStringBuilder.append("url: ").append(destinationAddress).append(" | ").append("method: ").append(requestMethod);
        if (header != null && !header.isEmpty()) {
            printStringBuilder.append(" | ").append("headers: ").append("\"");
            for (Map.Entry<String, String> headerEntry : header.entrySet()) {
                printStringBuilder.append(headerEntry.getKey()).append(":").append(headerEntry.getValue()).append(",");
            }
            printStringBuilder.append("\"");
        }
        if (data != null && !data.isEmpty()) {
            printStringBuilder.append(" | ").append("data: ").append("\"");
            for (Map.Entry<String, String> headerEntry : data.entrySet()) {
                printStringBuilder.append(headerEntry.getKey()).append(":").append(headerEntry.getValue()).append(",");
            }
            printStringBuilder.append("\"");
        }
        if (json) {
            printStringBuilder.append(" | ").append("json: \"").append(jsonData.replace("\n", "")).append("\"");
        }
        if (upload) {
            printStringBuilder.append(" | ").append("uploadData");
        }
        return printStringBuilder.toString();
    }

    /**
     * Gets query parameters.
     *
     * @return query parameters
     */
    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    /**
     * Sets query parameters.
     *
     * @param queryParams query parameters
     */
    public void setQueryParams(Map<String, String> queryParams) {
        this.queryParams = queryParams;
    }

    /**
     * Gets request body type.
     *
     * @return request body type
     */
    public RequestBodyType getRequestBodyType() {
        return requestBodyType;
    }

    /**
     * Sets request body type.
     *
     * @param requestBodyType request body type
     */
    public void setRequestBodyType(RequestBodyType requestBodyType) {
        this.requestBodyType = requestBodyType;
    }
}
