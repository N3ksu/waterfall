package waterfall.component.http;

public enum HttpMethod {
    GET,
    POST,
    UNKNOWN;

    public static HttpMethod typeOf(String method) {
        if ("GET".equals(method))
            return GET;

        if ("POST".equals(method))
            return POST;

        return UNKNOWN;
    }
}
