package httpclient.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Determines a request group.
 */
public class RequestGroup {
    /**
     * request group name
     */
    private String name;
    /**
     * All requests inside the request group
     */
    private List<Request> requests = new ArrayList<>();

    /**
     * Constructor of the request group
     *
     * @param name request group name
     */
    public RequestGroup(String name) {
        this.name = name;
    }

    /**
     * Adds a request to the request group.
     *
     * @param request request to add
     */
    public void addRequestItem(Request request) {
        requests.add(request);
    }

    /**
     * Returns a string representation of the request group.
     *
     * @return a string representation of the request group
     */
    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestGroup that = (RequestGroup) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name);
    }
}
