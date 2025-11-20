package waterfall.bootstrap.net.route;

import waterfall.bootstrap.net.uri.parser.regex.URIParser;
import waterfall.bootstrap.net.uri.type.URIType;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

public final class RouteBuilder {
    private String uri;
    private Method method;

    public RouteBuilder setMethod(Method method) {
        this.method = method;
        return this;
    }

    public RouteBuilder setUri(String uri) {
        this.uri = uri;
        return this;
    }

    public Route build() {
        if (uri == null)
            throw new NullPointerException("Cannot build a Route from an uri null");

        if (method == null)
            throw new NullPointerException("Cannot build a Route from a method null");

        URIType uriType = URIType.typeOf(uri);
        URIParser parser = uriType.getParser();
        String rgxUri = parser.parse(uri);
        Pattern rgxPattern = Pattern.compile(rgxUri);

        return new Route(uri, method, rgxUri, rgxPattern);
    }
}
