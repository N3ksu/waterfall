package waterfall.kernel.routing.route;

import waterfall.constant.WFConstant;
import waterfall.kernel.routing.uri.URIType;
import waterfall.api.annotation.request.mapping.RequestMapping;
import waterfall.api.http.HttpMethod;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RouteBuilder {
    public Pattern groupPattern =  Pattern.compile("\\(\\?<(" + WFConstant.JAVA_VAR_NOMENCLATURE_RGX + ")>");

    // TODO Improve this class by adding more specific method
    public Route[] build(Method method, RequestMapping requestMapping) throws Exception {
        Objects.requireNonNull(method, "Cannot build a Route from a method null");
        Objects.requireNonNull(requestMapping, "Cannot build a Route from a requestMapping null");

        String uri = requestMapping.value();
        String rgxUri = URIType.typeOf(uri).getParser().parse(uri);
        Pattern rgxPattern = Pattern.compile(rgxUri);

        Set<String> pathVarKeys = new HashSet<>();
        Matcher groupFinder = groupPattern.matcher(rgxPattern.pattern());
        while (groupFinder.find())
            pathVarKeys.add(groupFinder.group(1));

        HttpMethod[] httpMethods = requestMapping.method();
        Route[] routes = new Route[httpMethods.length];

        for (int i = 0; i < httpMethods.length; i++)
            routes[i] = new Route(httpMethods[i], uri, method, rgxPattern, pathVarKeys);

        return routes;
    }
}
