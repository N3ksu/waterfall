package waterfall.core.route;

import waterfall.constant.WaterFallConstant;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Route {
    private final String uri;
    private final String rgxUri;
    private final Method method;
    private final Pattern rgxPattern;

    public Route(String uri, Method method, String rgxUri, Pattern rgxPattern) {
        this.uri = uri;
        this.rgxPattern = rgxPattern;
        this.method = method;
        this.rgxUri = rgxUri;
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

    // Only matching uri should be passed here
    public Map<String, String> getPathVariables(String uri) throws Exception {
        Map<String, String> pathVariable = new HashMap<>();
        Matcher uriMatcher = rgxPattern.matcher(uri);

        if (!uriMatcher.matches()) // this is mandatory in order to advance the matcher otherwise matcher.group() will throw an Exception
            throw new Exception(uri + " doesn't match with " + this.uri);

        Matcher groupFinder = WaterFallConstant.URI_GRP_RGX_PATTERN.matcher(rgxPattern.pattern());

        while (groupFinder.find()) {
            String key = groupFinder.group(1);
            String value = uriMatcher.group(key);
            pathVariable.put(key, value);
        }

        return pathVariable;
    }

    @Override
    public String toString() {
        return "{ uri: " + uri + " rgx: " + rgxUri + " method: " + method.getName() + "}";
    }
}
