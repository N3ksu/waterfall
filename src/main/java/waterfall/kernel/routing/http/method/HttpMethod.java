package waterfall.kernel.routing.http.method;

public enum HttpMethod {
    GET,
    POST,
    UNKNOWN;

    public static HttpMethod httpMethodOf(String method) {
        if ("GET".equals(method))
            return GET;

        if ("POST".equals(method))
            return POST;

        return UNKNOWN;
    }
}
