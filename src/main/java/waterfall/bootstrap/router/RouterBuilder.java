package waterfall.bootstrap.router;

import waterfall.component.annotation.request.mapping.RequestMapping;
import waterfall.component.http.HttpMethod;
import waterfall.core.route.Route;
import waterfall.bootstrap.route.RouteBuilder;
import waterfall.core.router.Router;
import waterfall.util.tuple.Pair;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class RouterBuilder {
    public static Router build(Set<Pair<Method, RequestMapping>> pairs) throws Exception {
        Map<HttpMethod, Set<Route>> routes = new HashMap<>();

        for (HttpMethod httpMethod : HttpMethod.values())
            routes.put(httpMethod, new HashSet<>());

        for (Pair<Method, RequestMapping> pair : pairs)
            for (Route route: RouteBuilder.build(pair.getLeft(), pair.getRight()))
                routes.get(route.getHttpMethod()).add(route);

        return new Router(routes);
    }
}
