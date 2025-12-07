package waterfall.kernel.routing.route;

import waterfall.kernel.routing.http.HttpMethod;
import waterfall.kernel.routing.media.MediaType;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Route {
    private final HttpMethod httpMethod;
    private final String uri;
    private final Method method;
    private final MediaType mediaType;
    private final Pattern rgxPattern;
    private final Set<String> pathVarKeys;

    public Route(HttpMethod httpMethod, String uri, Method method, MediaType mediaType, Pattern rgxPattern, Set<String> pathVarKeys) {
        this.httpMethod = httpMethod;
        this.uri = uri;
        this.mediaType = mediaType;
        this.rgxPattern = rgxPattern;
        this.method = method;
        this.pathVarKeys = pathVarKeys;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public Method getMethod() {
        return method;
    }

    public boolean produces(MediaType mediaType) {
        return this.mediaType == mediaType;
    }

    public Pattern getRgxPattern() {
        return rgxPattern;
    }

    public Map<String, String> getPathVariables(String uri) throws Exception {
        Matcher uriMatcher = rgxPattern.matcher(uri);

        if (!uriMatcher.matches())
            throw new Exception(uri + " doesn't match with " + this.uri);

        Map<String, String> pathVariables = new HashMap<>();
        for (String key : pathVarKeys) {
            String value = uriMatcher.group(key);
            pathVariables.put(key, value);
        }

        return pathVariables;
    }
}
