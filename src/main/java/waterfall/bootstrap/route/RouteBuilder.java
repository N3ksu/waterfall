package waterfall.bootstrap.route;

import waterfall.bootstrap.uri.parser.regex.URIParser;
import waterfall.bootstrap.uri.type.URIType;
import waterfall.component.annotation.request.mapping.RequestMapping;
import waterfall.component.http.HttpMethod;
import waterfall.core.constant.WaterFallConstant;
import waterfall.core.route.Route;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RouteBuilder {
    public static List<Route> build(Method method, RequestMapping requestMapping) throws Exception {
        if (method == null)
            throw new NullPointerException("Cannot build a Route from a method null");

        if (requestMapping == null)
            throw new NullPointerException("Cannot build a Route from a requestMapping null");

        String uri = requestMapping.value();

        URIType uriType = URIType.typeOf(uri);
        URIParser parser = uriType.getParser();

        String rgxUri = parser.parse(uri);
        Pattern rgxPattern = Pattern.compile(rgxUri);

        Set<String> pathVarKeys = new HashSet<>();
        Matcher groupFinder = WaterFallConstant.URI_GRP_RGX_PATTERN.matcher(rgxPattern.pattern());
        while (groupFinder.find())
            pathVarKeys.add(groupFinder.group(1));

        List<Route> routes = new ArrayList<>();

        for (HttpMethod httpMethod : requestMapping.method())
            routes.add(new Route(httpMethod, uri, method, rgxUri, rgxPattern, pathVarKeys));

        return routes;
    }
}
