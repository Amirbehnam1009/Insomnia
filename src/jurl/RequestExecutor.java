package jurl;


import httpclient.entity.Request;
import httpclient.entity.Response;
import httpclient.entity.ResponseContentType;

import java.io.*;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Executor class of the request. It takes a request object and sends it to destination and creates the response.
 */
public class RequestExecutor {
    /**
     * content type header key
     */
    public static final String CONTENT_TYPE = "Content-Type";
    /**
     * multipart form data header value for content type
     */
    public static final String MULTIPART_FORM_DATA = "multipart/form-data";
    /**
     * octet stream header value for content type
     */
    public static final String OCTET_STREAM = "application/octet-stream";
    /**
     * json header value for content type
     */
    public static final String JSON = "application/json";
    /**
     * url encoded header value for content type
     */
    public static final String URL_ENCODED = "application/x-www-form-urlencoded";

    /**
     * Handles data sending in the case of multipart form data selection.
     *
     * @param body                 data to send
     * @param boundary             data boundary determiner
     * @param bufferedOutputStream output stream to write data
     * @throws IOException if an IO problem occurs
     */
    private void bufferOutFormData(Map<String, String> body, String boundary, BufferedOutputStream bufferedOutputStream) throws IOException {
        for (String key : body.keySet()) {
            bufferedOutputStream.write(("--" + boundary + "\r\n").getBytes());
            if (key.contains("file")) {
                bufferedOutputStream.write(("Content-Disposition: form-data; filename=\"" + (new File(body.get(key))).getName() + "\"\r\nContent-Type: Auto\r\n\r\n").getBytes());
                BufferedInputStream tempBufferedInputStream = new BufferedInputStream(new FileInputStream(new File(body.get(key))));
                byte[] filesBytes = tempBufferedInputStream.readAllBytes();
                bufferedOutputStream.write(filesBytes);
                bufferedOutputStream.write("\r\n".getBytes());
            } else {
                bufferedOutputStream.write(("Content-Disposition: form-data; name=\"" + key + "\"\r\n\r\n").getBytes());
                bufferedOutputStream.write((body.get(key) + "\r\n").getBytes());
            }
        }
        bufferedOutputStream.write(("--" + boundary + "--\r\n").getBytes());
    }

    /**
     * Handles uploading binary data in the case of upload selection.
     *
     * @param fileName             file name to upload
     * @param bufferedOutputStream output stream to write
     * @throws IOException if an IO problem occurs
     */
    private void uploadBinary(String fileName, BufferedOutputStream bufferedOutputStream) throws IOException {
        File file = new File(fileName);
        BufferedInputStream fileInputStream = new BufferedInputStream(new FileInputStream(file));
        bufferedOutputStream.write(fileInputStream.readAllBytes());
        bufferedOutputStream.flush();
        bufferedOutputStream.close();
    }

    /**
     * Executes specified request and builds the execution response.
     *
     * @param request request to execute
     * @return response of execution
     * @throws Exception if any problem occurs
     */
    public Response executeRequest(Request request) throws Exception {
        URL url;
        Response resultResponse = new Response();

        try {
            url = new URL(request.getDestinationAddress());
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            setMethod(request, httpURLConnection);
            httpURLConnection.setInstanceFollowRedirects(request.isRedirect());
            setHeader(request, httpURLConnection);
            handleFormData(request, httpURLConnection);
            handleUpload(request, httpURLConnection);
            handleJson(request, httpURLConnection);

            long startTime = System.currentTimeMillis();
            resultResponse.setStatusCode(httpURLConnection.getResponseCode());
            long endTime = System.currentTimeMillis();
            resultResponse.setStatusMessage(httpURLConnection.getResponseMessage());
            resultResponse.setTime(calculateRequestExecutionTime(startTime, endTime));
            Map<String, String> responseHeader = new HashMap<>();
            for (String headerKey : httpURLConnection.getHeaderFields().keySet()) {
                if (headerKey != null) {
                    responseHeader.put(headerKey, httpURLConnection.getHeaderField(headerKey));
                }
            }
            resultResponse.setHeader(responseHeader);
            String contentType = responseHeader.get("Content-Type");
            if (contentType == null) {
                resultResponse.setContentType(ResponseContentType.General);
            } else {
                if (contentType.contains("image")) {
                    resultResponse.setContentType(ResponseContentType.Picture);
                } else if (contentType.contains("json")) {
                    resultResponse.setContentType(ResponseContentType.Json);
                } else {
                    resultResponse.setContentType(ResponseContentType.General);
                }
            }
            try {
                InputStream ip = httpURLConnection.getInputStream();
                //if output type is picture read binary stream, otherwise read text data
                if (resultResponse.getContentType().equals(ResponseContentType.Picture)) {
                    BufferedInputStream br1 = new BufferedInputStream(ip);
                    resultResponse.setContentBytes(br1.readAllBytes());
                    resultResponse.setDataSize(resultResponse.getContentBytes().length + " B");
                } else {
                    BufferedInputStream br = new BufferedInputStream(ip);
                    byte[] bytes = br.readAllBytes();
                    resultResponse.setContentStr(new String(bytes, StandardCharsets.UTF_8));
                    resultResponse.setDataSize(bytes.length + " B");
                }
            } catch (IOException e) {
                resultResponse.setContentStr("");
                resultResponse.setDataSize("0 B");
            }
        } catch (MalformedURLException e) {
            throw new Exception("Invalid destination Address");
        } catch (IOException e) {
            throw new Exception("destination Address can’t be reached");
        }
        return resultResponse;
    }

    /**
     * Handles json input data sendind.
     *
     * @param request           request to send
     * @param httpURLConnection connection to use
     * @throws IOException if any problem occurs
     */
    private void handleJson(Request request, HttpURLConnection httpURLConnection) throws IOException {
        if (request.isJson()) {
            httpURLConnection.setDoOutput(true);
            OutputStream out = httpURLConnection.getOutputStream();
            out.write(request.getJsonData().getBytes());
        }
    }

    /**
     * Handles all settings for uploading file.
     *
     * @param request           request to send
     * @param httpURLConnection connection to use
     * @throws IOException if any problem occurs
     */
    private void handleUpload(Request request, HttpURLConnection httpURLConnection) throws IOException {
        if (request.isUpload()) {
            httpURLConnection.setDoOutput(true);
            BufferedOutputStream requestBufferedOutputStream = new BufferedOutputStream(httpURLConnection.getOutputStream());
            uploadBinary(request.getUploadPath(), requestBufferedOutputStream);
        }
    }

    /**
     * Handles all settings for sending input data, either in multipart form or url encoded.
     *
     * @param request           request to send
     * @param httpURLConnection connection to use
     * @throws IOException if any problem occurs
     */
    private void handleFormData(Request request, HttpURLConnection httpURLConnection) throws IOException {
        Map<String, String> formData = request.getData();
        if (formData != null) {
            httpURLConnection.setDoOutput(true);
            String contentTypeProperty = httpURLConnection.getRequestProperty(CONTENT_TYPE);
            if (MULTIPART_FORM_DATA.equals(contentTypeProperty)) {
                String boundary = System.currentTimeMillis() + "";
                httpURLConnection.setRequestProperty(CONTENT_TYPE, contentTypeProperty + "; boundary=" + boundary);
                try (BufferedOutputStream requestBufferedOutputStream = new BufferedOutputStream(httpURLConnection.getOutputStream())) {
                    bufferOutFormData(formData, boundary, requestBufferedOutputStream);
                    requestBufferedOutputStream.flush();
                }
            } else {
                String dataString = getDataString(formData);
                byte[] postData = dataString.getBytes(StandardCharsets.UTF_8);
                int postDataLength = postData.length;
                httpURLConnection.setRequestProperty("Content-Length", Integer.toString(postDataLength));
                try (DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream())) {
                    wr.write(postData);
                }
            }
        }
    }

    /**
     * Converts input data into a key-value string
     *
     * @param params parameters to convert
     * @return string presenting key-value representation of input data
     */
    private String getDataString(Map<String, String> params) {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");
            result.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
        }
        return result.toString();
    }

    /**
     * Sets header key-values to connection to send.
     *
     * @param request           request to send
     * @param httpURLConnection connection to use
     */
    private void setHeader(Request request, HttpURLConnection httpURLConnection) {
        Map<String, String> headerMap = request.getHeader();
        if (headerMap != null) {
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                httpURLConnection.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * Sets the http method. It performs PATCH using force!
     *
     * @param request           request to send
     * @param httpURLConnection connection to use
     * @throws Exception if any problem occurs, specifically in forcing!
     */
    private void setMethod(Request request, HttpURLConnection httpURLConnection) throws Exception {
        String method = request.getRequestMethod().toString();
        if (!method.equals("PATCH")) {
            httpURLConnection.setRequestMethod(method);
        } else {
            Field field = HttpURLConnection.class.getDeclaredField("method");
            field.setAccessible(true);
            field.set(httpURLConnection, "PATCH");
        }
    }

    /**
     * Calculates the execution time and returns it in seconds.
     *
     * @param startTime execution start time
     * @param endTime   execution finish time
     * @return execution time in seconds
     */
    private String calculateRequestExecutionTime(long startTime, long endTime) {
        float diff = endTime - startTime;
        return diff / 1000 + "s";
    }
}
