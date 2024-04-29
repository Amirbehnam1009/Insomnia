package httpclient.entity;

import java.util.Map;

/**
 * Represents an http response.
 */
public class Response {
    /**
     * response header name values
     */
    private Map<String, String> header;
    /**
     * response status code
     */
    private int statusCode;
    /**
     * response status message
     */
    private String statusMessage;
    /**
     * request execution time
     */
    private String time;
    /**
     * response data size
     */
    private String dataSize;
    /**
     * response content type
     */
    private ResponseContentType contentType;
    /**
     * response text content
     */
    private String contentStr;
    /**
     * response byte stream content
     */
    private byte[] contentBytes;

    /**
     * Gets response header name values.
     *
     * @return response header name values
     */
    public Map<String, String> getHeader() {
        return header;
    }

    /**
     * Sets response header.
     *
     * @param header response header
     */
    public void setHeader(Map<String, String> header) {
        this.header = header;
    }

    /**
     * Gets response status code.
     *
     * @return response status code
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Sets response status code.
     *
     * @param statusCode response status code
     */
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * Gets response status message.
     *
     * @return response status message
     */
    public String getStatusMessage() {
        return statusMessage;
    }

    /**
     * Sets response status message.
     *
     * @param statusMessage response status message
     */
    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    /**
     * Gets request execution time.
     *
     * @return request execution time
     */
    public String getTime() {
        return time;
    }

    /**
     * Sets request execution time.
     *
     * @param time request execution time
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     * Gets response data size.
     *
     * @return response data size
     */
    public String getDataSize() {
        return dataSize;
    }

    /**
     * Sets response data size.
     *
     * @param dataSize response data size
     */
    public void setDataSize(String dataSize) {
        this.dataSize = dataSize;
    }

    /**
     * Gets response content type.
     *
     * @return response content type
     */
    public ResponseContentType getContentType() {
        return contentType;
    }

    /**
     * Sets response content type.
     *
     * @param contentType response content type
     */
    public void setContentType(ResponseContentType contentType) {
        this.contentType = contentType;
    }

    /**
     * Gets response text content.
     *
     * @return response text content
     */
    public String getContentStr() {
        return contentStr;
    }

    /**
     * Sets response text content.
     *
     * @param contentStr response text content
     */
    public void setContentStr(String contentStr) {
        this.contentStr = contentStr;
    }

    /**
     * Gets response byte stream content.
     *
     * @return response byte stream content
     */
    public byte[] getContentBytes() {
        return contentBytes;
    }

    /**
     * Sets response byte stream content.
     *
     * @param contentBytes response byte stream content
     */
    public void setContentBytes(byte[] contentBytes) {
        this.contentBytes = contentBytes;
    }
}
