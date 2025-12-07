package waterfall.kernel.routing.router;

import waterfall.api.http.HttpMethod;
import waterfall.kernel.routing.route.Route;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Router {
    private final Map<HttpMethod, Set<Route>> routes;

    public Router() {
        routes = new HashMap<>();

        for (HttpMethod httpMethod : HttpMethod.values())
            routes.put(httpMethod, new HashSet<>());
    }

    public void add(Route route) {
        routes.get(route.getHttpMethod()).add(route);
    }

    public Route findRoute(HttpMethod httpMethod, String uri) {
        for (Route route: routes.get(httpMethod))
            if (route.getRgxPattern().matcher(uri).matches())
                return route;

        return null;
    }
}
