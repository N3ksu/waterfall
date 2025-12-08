package waterfall.kernel.routing.route;

import waterfall.api.annotation.request.mapping.RequestMapping;
import waterfall.kernel.constant.Constant;
import waterfall.kernel.routing.http.method.HttpMethod;
import waterfall.kernel.routing.http.media.MediaType;
import waterfall.kernel.routing.uri.util.UriRegexConverter;

import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Route {
    private final HttpMethod httpMethod;
    private final String uri;
    private final Method action;
    private final MediaType mediaType;
    private final Pattern rgxPattern;
    private final Set<String> pathVarKeys;

    private Route(final HttpMethod httpMethod, final String uri, final Method action, final MediaType mediaType, final Pattern rgxPattern, final Set<String> pathVarKeys) {
        this.httpMethod = httpMethod;
        this.uri = uri;
        this.mediaType = mediaType;
        this.rgxPattern = rgxPattern;
        this.action = action;
        this.pathVarKeys = pathVarKeys;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public Method getAction() {
        return action;
    }

    public boolean produces(final MediaType mediaType) {
        return this.mediaType == mediaType;
    }

    public Pattern getRgxPattern() {
        return rgxPattern;
    }

    public Map<String, String> getPathVariables(final String uri) throws Exception {
        final Matcher uriMatcher = rgxPattern.matcher(uri);

        if (!uriMatcher.matches()) throw new Exception(uri + " doesn't match with " + this.uri);

        final Map<String, String> pathVariables = new HashMap<>();
        for (String key : pathVarKeys) {
            String value = uriMatcher.group(key);
            pathVariables.put(key, value);
        }

        return pathVariables;
    }

    public static final class Builder {
        public static Route[] build(final Method method, final RequestMapping requestMapping) throws Exception {
            Objects.requireNonNull(method, "Cannot build a Route from a method null");
            Objects.requireNonNull(requestMapping, "Cannot build a Route from a requestMapping null");

            final String uri = requestMapping.value();
            final String rgxUri = UriRegexConverter.convert(uri);

            final Set<String> pathVarKeys = new HashSet<>();
            final Matcher groupFinder = Constant.Regex.ROUTE_GRP_PATTERN.matcher(rgxUri);

            while (groupFinder.find())
                pathVarKeys.add(groupFinder.group(1));

            final HttpMethod[] httpMethods = requestMapping.method();
            final Route[] routes = new Route[httpMethods.length];

            for (int i = 0; i < httpMethods.length; i++)
                routes[i] = new Route(httpMethods[i], uri, method, MediaType.mediaTypeOf(method), Pattern.compile(rgxUri), pathVarKeys);

            return routes;
        }
    }
}
