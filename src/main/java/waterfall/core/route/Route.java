package waterfall.core.route;

import waterfall.constant.WaterFallConstant;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Route {
    private final String uri;
    private final String rgxUri;
    private final Method method;
    private final Pattern rgxPattern;
    private final Set<String> pathVarKeys;

    public Route(String uri, Method method, String rgxUri, Pattern rgxPattern, Set<String> pathVarKeys) {
        this.uri = uri;
        this.rgxPattern = rgxPattern;
        this.method = method;
        this.rgxUri = rgxUri;
        this.pathVarKeys = pathVarKeys;
    }

    public String getRgxUri() {
        return rgxUri;
    }

    public String getUri() {
        return uri;
    }

    public Method getMethod() {
        return method;
    }

    public Pattern getRgxPattern() {
        return rgxPattern;
    }

    public Map<String, String> getPathVariables(String uri) throws Exception {
        Matcher uriMatcher = rgxPattern.matcher(uri);

        // this is mandatory in order to advance the matcher otherwise matcher.group() will throw an Exception
        if (!uriMatcher.matches())
            throw new Exception(uri + " doesn't match with " + this.uri);

        Map<String, String> pathVariables = new HashMap<>();
        for (String key : pathVarKeys) {
            String value = uriMatcher.group(key);
            pathVariables.put(key, value);
        }

        return pathVariables;
    }

    @Override
    public String toString() {
        return "{ uri: " + uri + " rgx: " + rgxUri + " method: " + method.getName() + "}";
    }
}
