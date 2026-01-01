package waterfall.kernel.routing.route;

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
    private final Pattern regexPattern;
    private final Set<String> pathVariableKey;

    private Route(HttpMethod httpMethod, String uri, Method action, MediaType mediaType, Pattern regexPattern, Set<String> pathVariableKey) {
        this.httpMethod = httpMethod;
        this.uri = uri;
        this.mediaType = mediaType;
        this.regexPattern = regexPattern;
        this.action = action;
        this.pathVariableKey = pathVariableKey;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public Method getAction() {
        return action;
    }

    public boolean produces(MediaType mediaType) {
        return this.mediaType == mediaType;
    }

    public Pattern getRegexPattern() {
        return regexPattern;
    }

    public Map<String, String> getPathVariables(String uri) throws Exception {
        Matcher uriMatcher = regexPattern.matcher(uri);

        if (!uriMatcher.matches()) throw new Exception(uri + " doesn't match with " + this.uri);

        Map<String, String> pathVariables = new HashMap<>();
        for (String key : pathVariableKey) {
            String value = uriMatcher.group(key);
            pathVariables.put(key, value);
        }

        return pathVariables;
    }

    public static final class Builder {
        public static Route build(RouteBlueprint routeBlueprint) {
            Objects.requireNonNull(routeBlueprint.actionMethod(), "Cannot build a Route from an action method that is null");
            Objects.requireNonNull(routeBlueprint.uri(), "Cannot build a Route from a null URI");
            Objects.requireNonNull(routeBlueprint.httpMethod(), "Cannot build a Route from a null HTTP method");

            String regexUri = UriRegexConverter.convert(routeBlueprint.uri());

            Set<String> pathVariableKey = new HashSet<>();
            Matcher groupFinder = Constant.Regex.ROUTE_GRP_PATTERN.matcher(regexUri);

            while (groupFinder.find())
                pathVariableKey.add(groupFinder.group(1));

            MediaType mediaType = MediaType.mediaTypeOf(routeBlueprint.actionMethod());
            Pattern pattern = Pattern.compile(regexUri);

            return new Route(routeBlueprint.httpMethod(), routeBlueprint.uri(), routeBlueprint.actionMethod(), mediaType, pattern, pathVariableKey);
        }

        public record RouteBlueprint(Method actionMethod, String uri, HttpMethod httpMethod) {
            public static RouteBlueprint of(Method actionMethod, String uri, HttpMethod httpMethod) {
                return new RouteBlueprint(actionMethod, uri, httpMethod);
            }
        }
    }
}
