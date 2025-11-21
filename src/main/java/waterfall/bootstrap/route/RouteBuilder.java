package waterfall.bootstrap.route;

import waterfall.bootstrap.uri.parser.regex.URIParser;
import waterfall.bootstrap.uri.type.URIType;
import waterfall.constant.WaterFallConstant;
import waterfall.core.route.Route;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
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

        Set<String> pathVarKeys = new HashSet<>();
        Matcher groupFinder = WaterFallConstant.URI_GRP_RGX_PATTERN.matcher(rgxPattern.pattern());
        while (groupFinder.find())
            pathVarKeys.add(groupFinder.group(1));

        return new Route(uri, method, rgxUri, rgxPattern, pathVarKeys);
    }
}
