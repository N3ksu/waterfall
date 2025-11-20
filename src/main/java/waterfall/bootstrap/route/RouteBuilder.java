package waterfall.bootstrap.route;

import waterfall.bootstrap.uri.parser.regex.URIParser;
import waterfall.bootstrap.uri.type.URIType;
import waterfall.core.route.Route;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

public final class RouteBuilder {
    public static Route build(String uri, Method method) {
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
