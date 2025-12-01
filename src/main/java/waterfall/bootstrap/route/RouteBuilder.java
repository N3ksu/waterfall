package waterfall.bootstrap.route;

import waterfall.bootstrap.uri.parser.regex.URIParser;
import waterfall.bootstrap.uri.type.URIType;
import waterfall.component.annotation.request.mapping.RequestMapping;
import waterfall.component.http.HttpMethod;
import waterfall.core.constant.WaterFallConstant;
import waterfall.core.route.Route;

import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RouteBuilder {
    public Route[] build(Method method, RequestMapping requestMapping) throws Exception {
        Objects.requireNonNull(method, "Cannot build a Route from a method null");
        Objects.requireNonNull(requestMapping, "Cannot build a Route from a requestMapping null");

        String uri = requestMapping.value();

        URIType uriType = URIType.typeOf(uri);
        URIParser parser = uriType.getParser();

        String rgxUri = parser.parse(uri);
        Pattern rgxPattern = Pattern.compile(rgxUri);

        Set<String> pathVarKeys = new HashSet<>();
        Matcher groupFinder = WaterFallConstant.URI_GRP_RGX_PATTERN.matcher(rgxPattern.pattern());
        while (groupFinder.find())
            pathVarKeys.add(groupFinder.group(1));

        HttpMethod[] httpMethods = requestMapping.method();
        Route[] routes = new Route[httpMethods.length];

        for (int i = 0; i < httpMethods.length; i++)
            routes[i] = new Route(httpMethods[i], uri, method, rgxUri, rgxPattern, pathVarKeys);

        return routes;
    }
}
