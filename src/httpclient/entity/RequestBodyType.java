package httpclient.entity;

/**
 * Request body types to show in request body tab.
 */
public enum RequestBodyType {
    /**
     * form data type
     */
    FormData("Form Data"),
    /**
     * form url encoded type
     */
    UrlEncoded("Form URL Encoded"),
    /**
     * json type
     */
    JSON("JSON"),
    /**
     * binary data type
     */
    BinaryData("Binary Data");

    /**
     * request body type title
     */
    private String title;

    /**
     * Constructor of request body type.
     *
     * @param title request body type title
     */
    RequestBodyType(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }
}
