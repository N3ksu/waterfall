package waterfall.core.route;

import java.lang.reflect.Method;
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

    @Override
    public String toString() {
        return "{ uri: " + uri + " rgx: " + rgxUri + " method: " + method.getName() + "}";
    }
}
